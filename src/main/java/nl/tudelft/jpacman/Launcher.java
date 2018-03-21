package nl.tudelft.jpacman;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.ui.Action;
import nl.tudelft.jpacman.ui.PacManUI;
import nl.tudelft.jpacman.ui.PacManUiBuilder;
import org.apache.commons.cli.*;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
/**
 * Creates and launches the JPacMan UI.
 * 
 * @author Jeroen Roosen
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Launcher {

    private static final PacManSprites SPRITE_STORE = new PacManSprites();

    public static final String DEFAULT_MAP = "/board.txt";
    private String levelMap = DEFAULT_MAP;

    @MonotonicNonNull private PacManUI pacManUI;
    @MonotonicNonNull private Game game;

    /**
     * @return The game object this launcher will start when {@link #launch()}
     *         is called.
     */
    @RequiresNonNull("game")
    public Game getGame() {
        return game;
    }

    /**
     * The map file used to populate the level.
     *
     * @return The name of the map file.
     */
    protected String getLevelMap() {
        return levelMap;
    }

    /**
     * Set the name of the file containing this level's map.
     *
     * @param fileName
     *            Map to be used.
     * @return Level corresponding to the given map.
     */
    public Launcher withMapFile(String fileName) {
        levelMap = fileName;
        return this;
    }

    /**
     * Creates a new game using the level from {@link #makeLevel()}.
     *
     * @param playerId
     *              The player's name
     * @return a new Game.
     */
    @EnsuresNonNull("game")
    public Game makeGame(String playerId) {
        GameFactory gf = getGameFactory();
        Level level = makeLevel();
        game = gf.createSinglePlayerGame(level, playerId);
        return game;
    }

    /**
     * Creates a new level. By default this method will use the map parser to
     * parse the default board stored in the <code>board.txt</code> resource.
     *
     * @return A new level.
     */
    public Level makeLevel() {
        try {
            return getMapParser().parseMap(getLevelMap());
        } catch (IOException e) {
            throw new PacmanConfigurationException(
                    "Unable to create level, name = " + getLevelMap(), e);
        }
    }

    /**
     * @return A new map parser object using the factories from
     *         {@link #getLevelFactory()} and {@link #getBoardFactory()}.
     */
    protected MapParser getMapParser() {
        return new MapParser(getLevelFactory(), getBoardFactory());
    }

    /**
     * @return A new board factory using the sprite store from
     *         {@link #getSpriteStore()}.
     */
    protected BoardFactory getBoardFactory() {
        return new BoardFactory(getSpriteStore());
    }

    /**
     * @return The default {@link PacManSprites}.
     */
    protected PacManSprites getSpriteStore() {
        return SPRITE_STORE;
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}
     *         and the ghosts from {@link #getGhostFactory()}.
     */
    protected LevelFactory getLevelFactory() {
        return new LevelFactory(getSpriteStore(), getGhostFactory());
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}.
     */
    protected GhostFactory getGhostFactory() {
        return new GhostFactory(getSpriteStore());
    }

    /**
     * @return A new factory using the players from {@link #getPlayerFactory()}.
     */
    protected GameFactory getGameFactory() {
        return new GameFactory(getPlayerFactory());
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}.
     */
    protected PlayerFactory getPlayerFactory() {
        return new PlayerFactory(getSpriteStore());
    }

    /**
     * Adds key events UP, DOWN, LEFT and RIGHT to a game.
     *
     * @param builder
     *            The {@link PacManUiBuilder} that will provide the UI.
     */
    protected void addSinglePlayerKeys(final PacManUiBuilder builder) {
        builder.addKey(KeyEvent.VK_UP, moveTowardsDirection(Direction.NORTH))
                .addKey(KeyEvent.VK_DOWN, moveTowardsDirection(Direction.SOUTH))
                .addKey(KeyEvent.VK_LEFT, moveTowardsDirection(Direction.WEST))
                .addKey(KeyEvent.VK_RIGHT, moveTowardsDirection(Direction.EAST));
    }

    private Action moveTowardsDirection(Direction direction) {
        return () -> {
            assert game != null;
            getGame().move(getSinglePlayer(getGame()), direction);
        };
    }

    private Player getSinglePlayer(final Game game) {
        List<Player> players = game.getPlayers();
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Game has 0 players.");
        }
        return players.get(0);
    }

    /**
     * Creates and starts a JPac-Man game.
     *
     * @param playerId
     *                  The player's name
     *
     */
    @EnsuresNonNull("game")
    public void launch(String playerId) {
        makeGame(playerId);

        PacManUiBuilder builder = new PacManUiBuilder().withDefaultButtons();
        addSinglePlayerKeys(builder);
        pacManUI = builder.build(getGame());
        pacManUI.start();
    }

    /**
     * Disposes of the UI. For more information see
     * {@link javax.swing.JFrame#dispose()}.
     *
     * Precondition: The game was launched first.
     */
    public void dispose() {
        assert pacManUI != null;
        pacManUI.dispose();
    }

    /**
     * Parse command line arguments and store in HashMap
     * @param args the command line arguments
     * @return the parsed command line arguments
     */
    public static Map<String, String> parseCommandLine(String[] args){
        Options options = new Options();

        Option applicationId = new Option("a", "applicationid", true, "application id");
        applicationId.setRequired(false);
        options.addOption(applicationId);

        Option deviceId = new Option("d", "deviceid", true, "device id");
        deviceId.setRequired(false);
        options.addOption(deviceId);

        Option beaconURL = new Option("b", "beaconurl", true, "beacon url");
        beaconURL.setRequired(false);
        options.addOption(beaconURL);

        Option playerName = new Option("p", "player", true, "player name");
        playerName.setRequired(false);
        options.addOption(playerName);

        Option disableNPCs = new Option("dn", "disable-npc", false, "disable npcs");
        disableNPCs.setRequired(false);
        options.addOption(disableNPCs);

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return new HashMap<>();
        }

        Map<String, String> commandLineArguments = new HashMap<>();

        String value =  cmd.getOptionValue("applicationid");
        if(value!=null && !value.isEmpty()) {
            commandLineArguments.put("applicationID", value);
        }

        value = cmd.getOptionValue("deviceid");
        if(value!=null && !value.isEmpty()) {
            commandLineArguments.put("deviceID", value);
        }

        value = cmd.getOptionValue("beaconurl");
        if(value!=null && !value.isEmpty()) {
            commandLineArguments.put("beaconURL", value);
        }

        value = cmd.getOptionValue("player");
        if(value!=null && !value.isEmpty()) {
            commandLineArguments.put("player", value);
        }

        if(cmd.hasOption("disable-npc")) {
            commandLineArguments.put("disable-npc", "");
        }

        return commandLineArguments;
    }

    /**
     * Read configuration from properties file
     * @return A HashMap with the configuration values successfully read from the properties file
     */
    public static Map<String, String> readFromPropertiesFile(){

        String endpointURL = "";
        String applicationID = "";
        String deviceID = "";

        try {
            CodeSource source = Launcher.class.getProtectionDomain().getCodeSource();
            if(source!= null) {
                URL file = new URL(source.getLocation(), "dynatrace.properties");


                Properties properties = new Properties();
                InputStream in = file.openStream();
                properties.load(in);
                in.close();

                if(properties.containsKey("beacon_url")) {
                    String prop = properties.getProperty("beacon_url");
                    if (prop != null && !prop.isEmpty()) {
                        endpointURL = prop;
                    }
                }

                if(properties.containsKey("application_id")) {
                    String prop = properties.getProperty("application_id");
                    if (prop != null && !prop.isEmpty()) {
                        applicationID = prop;
                    }
                }

                if(properties.containsKey("device_id")) {
                    String prop = properties.getProperty("device_id");
                    if (prop != null && !prop.isEmpty()) {
                        deviceID = prop;
                    }
                }
            }

        }
        catch(Exception e) {
            System.err.println("dynatrace.properties file not found in classpath");
        }

        Map<String, String> propertiesFile = new HashMap<>();

        propertiesFile.put("applicationID", applicationID);
        propertiesFile.put("deviceID", deviceID);
        propertiesFile.put("beaconURL", endpointURL);

        return propertiesFile;
    }

    /**
     * Obtain a property from either command line or properties file
     * NOTE: If both command line and properties file are available command line
     *       is preferred over properties file
     * NOTE: In case a value is not found in either properties file or command
     *       line arguments a reasonable default is returned.
     * @param property Name of the property
     * @param properties Map created from property file
     * @param commandLine Map created from command line
     * @return String containing the requested property
     */
    public static String returnValueFromConfigOrDefault(String property, Map<String, String> properties, Map<String,String> commandLine) {
        String configValue;

        if(commandLine.containsKey(property)) {
            configValue = commandLine.get(property);
            if(configValue!=null && configValue.length() > 0) {
                return configValue;
            }
        }

        if(properties.containsKey(property)) {
            configValue = properties.get(property);
            if(configValue!=null && configValue.length() > 0) {
                return configValue;
            }
        }

        return "";
    }


    /**
     * Main execution method for the Launcher.
     *
     * @param args
     *            The command line arguments - which are ignored.
     * @throws IOException
     *             When a resource could not be read.
     */
    public static void main(String[] args) {

        Map<String,String> commandLine = parseCommandLine(args);
        Map<String,String> propertiesFile = readFromPropertiesFile();

        String endpointURL = returnValueFromConfigOrDefault("beaconURL",
            propertiesFile,
            commandLine);

        String applicationID = returnValueFromConfigOrDefault("applicationID",
            propertiesFile,
            commandLine);

        String device = returnValueFromConfigOrDefault("deviceID",
            propertiesFile,
            commandLine);
        long deviceID = Long.parseLong(device);

        String player = "";
        if(commandLine.containsKey("player")) {
            String configValue = commandLine.get("player");
            if(configValue!=null && configValue.length() > 0) {
                player = configValue;
            }
        }

        if(player.length() == 0 ) {
            player = "default";
        }

        System.out.println("got endpoint URL " + endpointURL);
        System.out.println("got application ID " + applicationID);
        System.out.println("got device ID " + deviceID);

        OpenKitConfiguration openKitConfig = new OpenKitConfiguration(endpointURL, applicationID, deviceID);
        OpenKitSingleton.getInstance().initialize(openKitConfig, player);
        GameModeSingleton.getInstance().setDisableNPCs(commandLine.containsKey("disable-npc"));
        new Launcher().launch(player);
    }
}

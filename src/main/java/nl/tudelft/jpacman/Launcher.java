package nl.tudelft.jpacman;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.dynatrace.openkit.DynatraceOpenKitBuilder;
import com.dynatrace.openkit.api.OpenKit;
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
     * @param openKit
     *              Open Kit instance to monitor the application
     * @param playerId
     *              The player's name
     * @return a new Game.
     */
    @EnsuresNonNull("game")
    public Game makeGame(Optional<OpenKit> openKit, String playerId) {
        GameFactory gf = getGameFactory();
        Level level = makeLevel(openKit);
        game = gf.createSinglePlayerGame(level, playerId);
        return game;
    }

    /**
     * Creates a new level. By default this method will use the map parser to
     * parse the default board stored in the <code>board.txt</code> resource.
     *
     * @return A new level.
     */
    public Level makeLevel(Optional<OpenKit> openKit) {
        try {
            return getMapParser(openKit).parseMap(getLevelMap());
        } catch (IOException e) {
            throw new PacmanConfigurationException(
                    "Unable to create level, name = " + getLevelMap(), e);
        }
    }

    /**
     * @return A new map parser object using the factories from
     *         {@link #getLevelFactory()} and {@link #getBoardFactory()}.
     */
    protected MapParser getMapParser(Optional<OpenKit> openKit) {
        return new MapParser(getLevelFactory(), getBoardFactory(), openKit);
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
     * Create a new OpenKit instance from the given configuration parameters
     * @param configuration OpenKit instance
     */
    protected OpenKit getOpenKit(OpenKitConfiguration configuration)
    {
        OpenKit openKit = new DynatraceOpenKitBuilder(
            configuration.getBeaconURL(),
            configuration.getApplicationID(),
            configuration.getDeviceID())
            .withApplicationName("Pacman JAVA")
            .withApplicationVersion("7.0.0")
            .build();

        return openKit;
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
     * @param configuration
     *                  OpenKit configuration to use
     * @param playerId
     *                  The player's name
     *
     */
    @EnsuresNonNull("game")
    public void launch(OpenKitConfiguration configuration, String playerId) {
        Optional<OpenKit> openKit = Optional.empty();
        if(configuration.isValid()) {
            openKit = Optional.of(getOpenKit(configuration));
        }

        makeGame(openKit, playerId);

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
     * Main execution method for the Launcher.
     *
     * @param args
     *            The command line arguments - which are ignored.
     * @throws IOException
     *             When a resource could not be read.
     */
    public static void main(String[] args) {

        String endpointURL = "";
        String applicationID = "";
        long deviceID = 0;
        try {
            CodeSource source = Launcher.class.getProtectionDomain().getCodeSource();
            if(source!= null) {
                URL propertiesFile = new URL(source.getLocation(), "dynatrace.properties");


                Properties properties = new Properties();
                InputStream in = propertiesFile.openStream();
                properties.load(in);
                in.close();

                String prop = properties.getProperty("beacon_url");
                if (prop != null && !prop.isEmpty()) {
                    endpointURL = prop;
                }

                prop = properties.getProperty("application_id");
                if (prop != null && !prop.isEmpty()) {
                    applicationID = prop;
                }

                prop = properties.getProperty("device_id");
                if (prop != null && !prop.isEmpty()) {
                    deviceID = Long.parseLong(prop);
                }
            }

        }
        catch(Exception e) {
            System.err.println("dynatrace.properties file not found in classpath");
        }

        System.out.println("got endpoint URL " + endpointURL);
        System.out.println("got application ID " + applicationID);
        System.out.println("got device ID " + deviceID);

        OpenKitConfiguration openKitConfig = new OpenKitConfiguration(endpointURL, applicationID, deviceID);

        new Launcher().launch(openKitConfig, "player");
    }
}

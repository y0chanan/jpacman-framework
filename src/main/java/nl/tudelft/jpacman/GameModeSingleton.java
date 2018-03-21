package nl.tudelft.jpacman;

/**
 * A singleton for the OpenKit objects
 */
public class GameModeSingleton {

    /**
     * The static singleton instance
     */
    private static GameModeSingleton instance = new GameModeSingleton();

    /**
     * flag if NPCs are disabled
     */
    private boolean disableNonPlayerCharacters;

    /**
     * sync object to ensure access to methods from one context at a time
     */
    private Object syncObject;

    /**
     * Default constructor
     */
    private GameModeSingleton(){
        syncObject = new Object();
        disableNonPlayerCharacters = false;
    }

    /**
     * Return the singleton instance
     * @return the singleton
     */
    public static GameModeSingleton getInstance(){
        return instance;
    }

    /**
     * Return a flag if NPCs are disabled
     * @return @code true if NPCs are disabled, @code false if not
     */
    public boolean getDisableNPCs() {
        synchronized (syncObject) {
            return disableNonPlayerCharacters;
        }
    }

    /**
     * Set if NPCs are disabled
     * @param disableNPCs flag if NPCs are disabled
     */
    public void setDisableNPCs(boolean disableNPCs){
        synchronized (syncObject) {
            disableNonPlayerCharacters = disableNPCs;
        }
    }

}

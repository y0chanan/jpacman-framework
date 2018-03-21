package nl.tudelft.jpacman;

import com.dynatrace.openkit.DynatraceOpenKitBuilder;
import com.dynatrace.openkit.api.OpenKit;
import com.dynatrace.openkit.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A singleton for the OpenKit objects
 */
public class OpenKitSingleton {

    /**
     * true if initialisation was successful.
     */
    private boolean initialized;

    /**
     * The OpenKit instance
     */
    private Optional<OpenKit> openKit;

    /**
     * list of sessions
     */
    private Map<String, Session> sessions;

    /**
     * The static singleton instance
     */
    private static OpenKitSingleton instance = new OpenKitSingleton();

    /**
     * player name
     */
    private String playerID;

    /**
     * sync object to ensure access to methods from one context at a time
     */
    private Object syncObject;

    /**
     * Default constructor
     */
    private OpenKitSingleton(){
        initialized = false;
        playerID="";
        syncObject = new Object();
        sessions = new HashMap();
        openKit = Optional.empty();
    }

    /**
     * Return the singleton instance
     * @return the singleton
     */
    public static OpenKitSingleton getInstance(){
        return instance;
    }

    /**
     * Initialise the singleton's OpenKit instance using the configuration and player id
     * @param c the OpenKitConfiguration object
     * @param playerID player name
     */
    public void initialize(OpenKitConfiguration c, String playerID){
        synchronized (syncObject) {
            if (!initialized) {
                if(c.isValid()) {

                    openKit = Optional.of(new DynatraceOpenKitBuilder(
                        c.getBeaconURL(),
                        c.getApplicationID(),
                        c.getDeviceID())
                        .withApplicationName("Pacman JAVA")
                        .withApplicationVersion("7.0.0")
                        .build());

                    this.playerID = playerID;
                    initialized = true;
                }
            }
        }
    }

    /**
     * Return the session used for player actions/events
     * @return Session for player actions/events
     */
    public Session getPlayerSession(){
        synchronized (syncObject) {
            if (sessions.containsKey(playerID)) {
                return sessions.get(playerID);
            }
            Session newPlayerSession = openKit.get().createSession("");
            sessions.put(playerID, newPlayerSession);
            newPlayerSession.identifyUser(playerID);

            return newPlayerSession;
        }
    }

    /**
     * Return the session used for npc actions/events
     * @return Session for npc actions/events
     */
    public Session getNonPlayerCharacterSession(String npcID){
        synchronized (syncObject) {
            if (sessions.containsKey(npcID)) {
                return sessions.get(npcID);
            }
            Session newNPCSession = openKit.get().createSession("");
            sessions.put(npcID, newNPCSession);
            newNPCSession.identifyUser(npcID);

            return newNPCSession;
        }
    }

    /**
     * Return a flag if this singleton was initialized successfully
     * @return true if the OpenKit object is valid and can be used
     */
    public boolean isValid(){
        return initialized;
    }

    /**
     * Cleanup: close sessions and empty session list
     */
    public void clearSessions(){
        for(final Session session : sessions.values())
        {
            session.end();
        }
        sessions.clear();
    }


}

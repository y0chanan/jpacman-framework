package nl.tudelft.jpacman;

/**
 * Encapsulates all required parameters to create instances of OpenKit
 */
public class OpenKitConfiguration {

    /**
     * Creates a new OpenKitConfiguration with default/empty values.
     *
     * @return a new OpenKit Configuration.
     */
    public OpenKitConfiguration() {
        beaconURL = "";
        applicationID = "";
        deviceID = 0;
    }

    /**
     * Creates a new OpenKitConfiguration with default/empty values.
     * @param beaconEndpointURL
     *                  The endpoint URL of the applcation
     * @param application
     *                  The application id registered at the server
     * @param device
     *                  Device id to identify different devices
     * @return a new OpenKit Configuration.
     */
    public OpenKitConfiguration(String beaconEndpointURL, String application, long device) {
        beaconURL = beaconEndpointURL;
        applicationID = application;
        deviceID = device;
    }

    /**
     * Return the beacon endpoint URL of this configuration.
     * @return the endpoint URL
     */
    public String getBeaconURL() {
        return beaconURL;
    }

    /**
     * Return the application ID of this configuration.
     * @return the application ID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * Return the device ID of this configuration.
     * @return the device id
     */
    public long getDeviceID() {
        return deviceID;
    }

    /**
     * Check if a configuration with the given parameters is valid
     * @return true if the configuration contains valid parameters, false in other cases
     */
    public boolean isValid(){
        return !beaconURL.isEmpty() && ! applicationID.isEmpty() && deviceID > 0;
    }

    /**
     * String for the beacon endpoint URL
     */
    private String beaconURL;

    /**
     * String for the application ID
     */
    private String applicationID;

    /**
     * Device ID
     */
    private long deviceID;
}

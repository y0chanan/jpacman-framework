package nl.tudelft.jpacman.net;

/**
 * Encapsulates response code and transferred bytes of HTTP requests
 */
public class HttpResponse {

    /**
     * Response code received from the request
     */
    private int responseCode;

    /**
     * Bytes sent during the request
     */
    private int bytesSent;

    /**
     * Bytes received during the request
     */
    private int bytesReceived;

    /**
     * Construct a HttpResponse
     * @param responseCode response code from the URLConnection
     * @param bytesSent amount of sent bytes
     * @param bytesReceived amount of received bytes
     */
    public HttpResponse(int responseCode, int bytesSent, int bytesReceived)
    {
        this.responseCode = responseCode;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
    }

    /**
     * Returns the response code
     * @return response code
     */
    public int getResponseCode(){
        return responseCode;
    }

    /**
     * Returns the amount of bytes sent
     * @return amount of sent bytes
     */
    public int getBytesSent(){
        return bytesSent;
    }

    /**
     * Returns the amount of bytes received
     * @return amout of received bytes
     */
    public int getBytesReceived(){
        return bytesReceived;
    }
}

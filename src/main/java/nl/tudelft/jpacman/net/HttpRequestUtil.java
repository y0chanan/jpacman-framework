package nl.tudelft.jpacman.net;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utility class to make http post/get requests
 */
public class HttpRequestUtil {

    /**
     * target url of the HTTP request
     */
    private String url;

    /**
     * Construct a HttpRequestUtil using a string
     * @param requestURL target url of the web request
     */
    public HttpRequestUtil(String requestURL)
    {
        this.url = requestURL;
    }

    /**
     * Make a post requst
     * @param jsonData payload of the webrequest
     * @return HttpResponse object containg response code and transmitted bytes
     */
    public HttpResponse makePostRequest(JSONObject jsonData) {
        int bytesSent = jsonData.length();
        int responseCode = -1;
        int bytesReceived = -1;
        try {
            // Define the server endpoint to send the HTTP request to
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Indicate that we want to write to the HTTP request body
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            // Writing the post data to the HTTP request body
            BufferedWriter httpRequestBodyWriter =
                new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            httpRequestBodyWriter.write(jsonData.toString());

            httpRequestBodyWriter.close();

            // Reading from the HTTP response body
            responseCode = urlConnection.getResponseCode();
            bytesReceived = urlConnection.getResponseMessage().length();

            Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
            while (httpResponseScanner.hasNextLine())

            {
                System.out.println(httpResponseScanner.nextLine());
            }
            httpResponseScanner.close();
        }
        catch(MalformedURLException me)
        {
            responseCode = 404;
            bytesReceived = 0;
        }
        catch(ProtocolException pe)
        {
            responseCode = 405;
            bytesReceived = 0;
        }
        catch(IOException ie)
        {
            responseCode = -1;
            bytesReceived = 0;
        }

        return new HttpResponse(responseCode, bytesSent, bytesReceived);
    }
}

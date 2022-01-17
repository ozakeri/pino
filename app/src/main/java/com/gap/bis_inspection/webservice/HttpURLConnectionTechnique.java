package com.gap.bis_inspection.webservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;

public class HttpURLConnectionTechnique {

    public static Boolean connectionIsHttps (String urlString){
        //Checks the beginning of a String for https. Returns true for Strings
        //that begin with https. Returns false otherwise.
        if (urlString.regionMatches(0, "https", 0, 5)){
            return true;
        }
        else {
            return false;
        }
    }

    public static String getHostNameFromUrl (String urlString){
        //Uses connectionIsHttps to determine whether the String starts with
        //https. If the string starts with https, it returns a substring
        //strating at index 8 and ending at the next occurrence of forward
        //slash. If not, it starts at index 7. This is to accomodate https://
        //versus http://
        //The host name is assumed to be located between :// and the next
        //occurrence of /
        if (connectionIsHttps(urlString)){
            return urlString.substring(8,urlString.indexOf("/", 8));
        }
        else{
            return urlString.substring(7,urlString.indexOf("/", 7));
        }
    }

    public static String createHttpURLConnectionAndMakeRequest
            (String soapMessage, String urlString)
            throws MalformedURLException, FileNotFoundException, IOException{

        String lvSoapMessage = soapMessage;
        String responseString = "";
        //Create connection
        URL URLForSOAP = new URL(urlString);
        URLConnection URLConnectionForSOAP = URLForSOAP.openConnection();
        if (connectionIsHttps(urlString)) {
            HttpsURLConnection Connection =
                    (HttpsURLConnection) URLConnectionForSOAP;
            //Adjust connection
            Connection.setDoOutput(true);
            Connection.setDoInput(true);
            Connection.setRequestMethod("POST");
            //Use the method to get the host name from the URL string and set
            //the request property for the connection.
            Connection.setRequestProperty
                    ("Host", getHostNameFromUrl(urlString));
            Connection.setRequestProperty
                    ("Content-Type","application/soap+xml; charset=utf-8");
            //Send the request
            OutputStreamWriter soapRequestWriter =
                    new OutputStreamWriter(Connection.getOutputStream());
            soapRequestWriter.write(lvSoapMessage);
            System.out.println(lvSoapMessage);
            soapRequestWriter.flush();
            //Read the reply
            BufferedReader soapRequestReader =
                    new BufferedReader
                            (new InputStreamReader
                                    (Connection.getInputStream()));
            String line;
            while ((line = soapRequestReader.readLine()) != null) {
                responseString = responseString.concat(line);
            }
            //Clean up
            soapRequestWriter.close();
            soapRequestReader.close();
            Connection.disconnect();
        }
        else{
            //See comments for https case above.
            HttpURLConnection Connection =
                    (HttpURLConnection) URLConnectionForSOAP;
            Connection.setDoOutput(true);
            Connection.setDoInput(true);
            Connection.setRequestMethod("POST");
            Connection.setRequestProperty
                    ("Host", getHostNameFromUrl(urlString));
            Connection.setRequestProperty
                    ("Content-Type","application/soap+xml; charset=utf-8");
            OutputStreamWriter soapRequestWriter =
                    new OutputStreamWriter(Connection.getOutputStream());
            soapRequestWriter.write(lvSoapMessage);
            soapRequestWriter.flush();
            BufferedReader soapRequestReader =
                    new BufferedReader
                            (new InputStreamReader
                                    (Connection.getInputStream()));
            String line;
            while ((line = soapRequestReader.readLine()) != null) {
                responseString = responseString.concat(line);
            }
            soapRequestWriter.close();
            soapRequestReader.close();
            Connection.disconnect();
        }

        return responseString;
    }

}

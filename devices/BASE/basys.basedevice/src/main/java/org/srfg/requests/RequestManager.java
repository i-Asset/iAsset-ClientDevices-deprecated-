package org.srfg.requests;

import org.srfg.properties.MyProperties;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

/*********************************************************************************************************
 * RequestManager Class
 ********************************************************************************************************/
public class RequestManager {

    private MyProperties props = new MyProperties();
    private HttpURLConnection conType = null;
    private HttpURLConnection conInstance = null;
    private boolean isInit = false;

    public enum RegistryType {eAssetType, eAssetInstance}

    /*********************************************************************************************************
     * CTOR (can only do POST requests atm)
     ********************************************************************************************************/
    public RequestManager()
    {
        String strURL_BASE = props.getServerAddress() + "/registry/" + props.getServerRegistryID();

        try {
            conType = (HttpURLConnection)(new URL(strURL_BASE + "/type")).openConnection();
            conInstance = (HttpURLConnection)(new URL(strURL_BASE + "/instance")).openConnection();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        isInit = true;
    }

    /*********************************************************************************************************
     * SendRegisterRequest
     * method = GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
     ********************************************************************************************************/
    public boolean SendRegisterRequest(RegistryType type, String method, String parameter)
    {
        if(!isInit) return false;

        HttpURLConnection localConnection = (type == RegistryType.eAssetType) ? conType : conInstance;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(props.getServerCredentials().getBytes()));

        try {
            localConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        localConnection.setRequestProperty ("Authorization", basicAuth);
        localConnection.setRequestProperty("Content-Type", "application/json; utf-8");  // application/json; utf-8 / application/x-www-form-urlencoded
        localConnection.setRequestProperty("Accept", "application/json"); // set response read type
        localConnection.setRequestProperty("Content-Length", "" + parameter.getBytes().length);
        localConnection.setRequestProperty("Content-Language", "en-US");
        localConnection.setUseCaches(false);
        localConnection.setDoInput(true);
        localConnection.setDoOutput(true);

        try {
            localConnection.setRequestMethod(method);
        }
        catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        }

        // write POST
        try{
			OutputStream os = localConnection.getOutputStream();
            byte[] input = parameter.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        catch (IOException e) { e.printStackTrace(); }

        // wait for response
        try{
			BufferedReader br = new BufferedReader(new InputStreamReader(localConnection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}



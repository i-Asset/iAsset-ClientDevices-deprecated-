package org.srfg.panda.requests;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/*********************************************************************************************************
 * RequestManager Class
 ********************************************************************************************************/
public class RequestManager {

    private String strURL_BASE = "https://iasset.salzburgresearch.at/registry-service";
    private String strURL_DIR = "/directory/aas";
    private String strURL_AAS = "/aas";
    private HttpURLConnection conDIR = null;
    private HttpURLConnection conAAS = null;

    private boolean isInit = false;

    public enum RegistryType {eDirectory, eFullAAS}

    /*********************************************************************************************************
     * CTOR (can only do POST requests atm)
     ********************************************************************************************************/
    public RequestManager()
    {
        try {
            conDIR = (HttpURLConnection)(new URL(strURL_BASE + strURL_DIR)).openConnection();
            conAAS = (HttpURLConnection)(new URL(strURL_BASE + strURL_AAS)).openConnection();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            conDIR.setRequestMethod("POST");
            conDIR.setRequestProperty("Content-Type", "application/json; utf-8"); // set request header
            conDIR.setRequestProperty("Accept", "application/json"); // set response read type
            conDIR.setDoOutput(true); // to enable writing content to output stream

            conAAS.setRequestMethod("POST");
            conAAS.setRequestProperty("Content-Type", "application/json; utf-8"); // set request header
            conAAS.setRequestProperty("Accept", "application/json"); // set response read type
            conAAS.setDoOutput(true); // to enable writing content to output stream
        }
        catch (ProtocolException e) {
            e.printStackTrace();
            return;
        }

        isInit = true;
    }

    /*********************************************************************************************************
     * SendRegisterRequest
     ********************************************************************************************************/
    public boolean SendRegisterRequest(RegistryType type, String parameter)
    {
        if(!isInit) return false;

        HttpURLConnection localConnection = (type == RegistryType.eDirectory) ? conDIR : conAAS;

        // write POST
        try(OutputStream os = localConnection.getOutputStream())
        {
            byte[] input = parameter.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        catch (IOException e) { e.printStackTrace(); }

        // wait for response
        try(BufferedReader br = new BufferedReader(new InputStreamReader(localConnection.getInputStream(), "utf-8")))
        {
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



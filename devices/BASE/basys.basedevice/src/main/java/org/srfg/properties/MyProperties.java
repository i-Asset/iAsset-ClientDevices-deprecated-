package org.srfg.properties;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

/*********************************************************************************************************
 * MyProperties
 ********************************************************************************************************/
public class MyProperties {

    private Properties myProperties;

    /*********************************************************************************************************
     * CTOR
     ********************************************************************************************************/
    public MyProperties() {

        try {
            myProperties = new Properties();
            myProperties.load(getClass().getResourceAsStream("/myProps.properties"));
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /*********************************************************************************************************
     * getProperties
     ********************************************************************************************************/
    public String getDeviceAddress() { return myProperties.getProperty("device.address"); }
    public String getDevicePort() { return myProperties.getProperty("device.port"); }
    public String getServerAddress() { return myProperties.getProperty("server.address"); }
    public String getServerRegistryID() { return myProperties.getProperty("server.registry.id"); }
    public String getServerCredentials() { return myProperties.getProperty("server.credentials"); }
    public String getAssetInstanceName() { return myProperties.getProperty("asset.instance.name"); }

    public String getImageStringBase64()
    {
        byte[] fileContent = new byte[0];
        try
        {
            InputStream is = getClass().getResourceAsStream("/myAssetImage.png");
            fileContent = IOUtils.toByteArray(is);
        }
        catch (IOException e) {e.printStackTrace();}
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        return encodedString;
    }
}

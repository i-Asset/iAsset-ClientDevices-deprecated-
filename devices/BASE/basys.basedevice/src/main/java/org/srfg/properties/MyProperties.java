package org.srfg.properties;

import java.io.IOException;
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
    public String getServerRegistryType() { return myProperties.getProperty("server.registry.type"); }
    public String getServerRegistryInstance() { return myProperties.getProperty("server.registry.instance"); }
    public String getServerCredentials() { return myProperties.getProperty("server.credentials"); }
}

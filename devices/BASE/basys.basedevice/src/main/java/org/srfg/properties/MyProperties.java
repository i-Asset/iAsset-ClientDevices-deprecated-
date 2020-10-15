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
    public String getPropertyAddress() { return myProperties.getProperty("prop.address"); }
    public String getPropertyPort() { return myProperties.getProperty("prop.port"); }
}

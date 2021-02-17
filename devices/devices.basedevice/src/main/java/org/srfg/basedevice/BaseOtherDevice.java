package org.srfg.basedevice;

import at.srfg.iot.common.datamodel.asset.aas.basic.Identifier;
import at.srfg.iot.common.datamodel.asset.provider.IAssetProvider;
import at.srfg.iot.common.registryconnector.IAssetRegistry;
import org.srfg.properties.MyProperties;

public abstract class BaseOtherDevice extends javax.swing.JFrame {

    private MyProperties properties = new MyProperties();

    // needed for model thread
    protected Thread runner;
    protected IAssetProvider model;
    protected IAssetRegistry registry;
    protected IAssetProvider connectedDevice;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JToggleButton jToggleButton;
    protected javax.swing.JComboBox<String> jComboBox;
    protected javax.swing.JCheckBox jCheckBox;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JLabel jLabel2;
    protected javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    /*********************************************************************************************************
     * BaseOtherDevice
     ********************************************************************************************************/
    public BaseOtherDevice()
    {
        jToggleButton = new javax.swing.JToggleButton();
        jComboBox = new javax.swing.JComboBox<>();
        jCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    /*********************************************************************************************************
     * abstract functions
     ********************************************************************************************************/
    protected abstract void initComponents();
    protected abstract Thread doIt(final IAssetProvider model);

    /*********************************************************************************************************
     * abstract getters name/dir
     ********************************************************************************************************/
    public abstract String getName();
    public abstract String getDirectory();

    /*********************************************************************************************************
     * connectToDevice
     ********************************************************************************************************/
    protected IAssetProvider connectToDevice()
    {
        String fullAddress = properties.getDeviceAddress() + ":" + properties.getDevicePort() + "/" + this.getName();
        registry = IAssetRegistry.componentWithRegistry(properties.getServerAddress())
                                 .componentAtPort(5000); // device address will be assigned automatically

        //IAssetProvider instance = registry.fromType(
        //        new Identifier(this.getName()),
        //        new Identifier(this.getName() +"#aas"));

        connectedDevice = registry.connect(new Identifier("properties"));
        return connectedDevice;
    }
}

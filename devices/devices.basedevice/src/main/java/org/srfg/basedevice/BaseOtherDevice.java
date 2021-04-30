package org.srfg.basedevice;

import org.srfg.properties.MyProperties;

import at.srfg.iot.common.aas.IAssetModel;
import at.srfg.iot.common.datamodel.asset.aas.basic.Identifier;
import at.srfg.iot.common.registryconnector.IAssetRegistry;

public abstract class BaseOtherDevice extends javax.swing.JFrame {

    private MyProperties properties = new MyProperties();

    // needed for model thread
    protected Thread runner;
    protected IAssetModel model;
    protected IAssetRegistry registry;
    protected IAssetModel connectedDevice;

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
    protected abstract Thread doIt(final IAssetModel model);

    /*********************************************************************************************************
     * abstract getters name/dir
     ********************************************************************************************************/
    public abstract String getName();
    public abstract String getDirectory();

    /*********************************************************************************************************
     * connectToDevice
     ********************************************************************************************************/
    protected IAssetModel connectToDevice()
    {
        registry = IAssetRegistry.componentWithRegistry(properties.getServerAddress());
        Identifier id = new Identifier("http://iasset.labor/" + this.getName());
        //IAssetProvider instance = registry.fromType(
        //        new Identifier(this.getName()),
        //        new Identifier(this.getName() +"#aas"));

        connectedDevice = registry.connect(id);
        return connectedDevice;
    }
}

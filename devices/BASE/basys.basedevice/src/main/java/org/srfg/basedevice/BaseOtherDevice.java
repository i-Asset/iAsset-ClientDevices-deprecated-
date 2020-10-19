package org.srfg.basedevice;

import org.eclipse.basyx.vab.directory.proxy.VABDirectoryProxy;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;
import org.srfg.properties.MyProperties;

public abstract class BaseOtherDevice extends javax.swing.JFrame {

    private MyProperties properties = new MyProperties();

    // needed for model thread
    protected Thread runner;
    protected IModelProvider model;

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
    protected abstract Thread doIt(final IModelProvider model);

    /*********************************************************************************************************
     * abstract getters name/dir
     ********************************************************************************************************/
    public abstract String getName();
    public abstract String getDirectory();

    /*********************************************************************************************************
     * connectToDevice
     ********************************************************************************************************/
    protected IModelProvider connectToDevice()
    {
        // At the connected site, no direct access to the model is possible. Every access is done through the network infrastructure
        // The Virtual Automation Bus hides network details to the connected site. Only the endpoint of the directory has to be known:
        String fullAddress = "http://" + properties.getDeviceAddress() + ":" + properties.getDevicePort() + "/iasset/directory/";
        VABDirectoryProxy directoryProxy = new VABDirectoryProxy(fullAddress);

        // The connection manager is responsible for resolving every connection attempt. It needs:
        // - The directory at which all models are registered
        // - A provider for different types of network protocols (in this example, only HTTP-REST)
        VABConnectionManager connectionManager = new VABConnectionManager(directoryProxy, new HTTPConnectorProvider());

        // It is now one line of code to retrieve a model provider for any registered model in the network
        IModelProvider connectedBelt = connectionManager.connectToVABElement(getName() + "01");
        return connectedBelt;
    }
}

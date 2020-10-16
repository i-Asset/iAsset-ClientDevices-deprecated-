package org.srfg;

import org.eclipse.basyx.vab.directory.proxy.VABDirectoryProxy;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;
import org.srfg.basedevice.BaseOtherDevice;

import java.util.logging.Level;
import java.util.logging.Logger;

/********************************************************************************************************
 * This class contains the main function implementation and
 * serves as the basyx Peak2Pi client starting point
 *
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class MainOtherDevice extends BaseOtherDevice {

    /*********************************************************************************************************
     * Creates new form CustomApp
     ********************************************************************************************************/
    public MainOtherDevice() {
        initComponents();
    }


    /*********************************************************************************************************
     * initComponents
     *
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     ********************************************************************************************************/
    @SuppressWarnings("unchecked")
    @Override
    protected void initComponents() {

        jToggleButton = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToggleButton.setText("Verbinde zu Device");
        jToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleButton1ItemStateChanged(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(jToggleButton)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jToggleButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    /*********************************************************************************************************
     * Start Peak2Pi device
     ********************************************************************************************************/
    private void jToggleButton1ItemStateChanged(java.awt.event.ItemEvent evt) {
        switch (evt.getStateChange()) {
            case 1:
                jToggleButton.setText("Trenne von Device");

                // start basyx communication
                model = connectToDevice();
                runner = doIt(model);
                runner.start();
                break;
            case 2:
                jToggleButton.setText("Verbinde mit Device");
                if (runner != null && runner.isAlive()) {
                    model = null;
                    runner.interrupt();
                }
                break;
        }
        // TODO add your handling code here
    }

    /*********************************************************************************************************
     * connectToDevice
     ********************************************************************************************************/
    @Override
    protected IModelProvider connectToDevice() {

        // At the connected site, no direct access to the model is possible. Every access is done through the network infrastructure
        // The Virtual Automation Bus hides network details to the connected site. Only the endpoint of the directory has to be known:
        VABDirectoryProxy directoryProxy = new VABDirectoryProxy("http://localhost:5000/iasset/directory/");

        // The connection manager is responsible for resolving every connection attempt. It needs:
        // - The directory at which all models are registered
        // - A provider for different types of network protocols (in this example, only HTTP-REST)
        VABConnectionManager connectionManager = new VABConnectionManager(directoryProxy, new HTTPConnectorProvider());

        // It is now one line of code to retrieve a model provider for any registered model in the network
        IModelProvider connectedPeak2PiDevice = connectionManager.connectToVABElement("peak2pi01");
        return connectedPeak2PiDevice;
    }

    /*********************************************************************************************************
     * doIt
     ********************************************************************************************************/
    @Override
    protected Thread doIt(final IModelProvider model) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(500);


                        byte robotmode = (byte)((int) model.getModelPropertyValue("/properties/robotmode"));
                        double posX = (double) model.getModelPropertyValue("/properties/posX");
                        double posY = (double) model.getModelPropertyValue("/properties/posY");
                        double posZ = (double) model.getModelPropertyValue("/properties/posZ");
                        double forceZ = (double) model.getModelPropertyValue("/properties/forceZ");
                        double gripperDistance = (double) model.getModelPropertyValue("/properties/gripperDistance");

                        jTextArea1.setText( "Robot Mode: " + robotmode + "\n" +
                                            "Position X: " + posX + "\n" +
                                            "Position Y: " + posY + "\n" +
                                            "Position Z: " + posZ + "\n" +
                                            "Force Z: " + forceZ + "\n" +
                                            "Gripper Distance: " + gripperDistance );
                    }
                } catch (InterruptedException ex) {
                    // stop the thread now
                    Logger.getLogger(MainOtherDevice.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        return new Thread(runnable);
    }

    /*********************************************************************************************************
     * main program entrance
     ********************************************************************************************************/
    public static void main(String[] args)
    {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainOtherDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainOtherDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainOtherDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainOtherDevice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainOtherDevice().setVisible(true);
            }
        });
    }
}

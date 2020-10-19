package org.srfg.basedevice;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

public abstract class BaseOtherDevice extends javax.swing.JFrame {

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
    protected abstract IModelProvider connectToDevice();
    protected abstract Thread doIt(final IModelProvider model);
}

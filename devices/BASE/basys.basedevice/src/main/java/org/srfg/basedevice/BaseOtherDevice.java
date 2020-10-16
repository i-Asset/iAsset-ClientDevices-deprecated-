package org.srfg.basedevice;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

public abstract class BaseOtherDevice extends javax.swing.JFrame {

    // needed for model thread
    protected Thread runner;
    protected IModelProvider model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JComboBox<String> jComboBox;
    protected javax.swing.JCheckBox jCheckBox;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JLabel jLabel2;
    protected javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JTextArea jTextArea1;
    protected javax.swing.JToggleButton jToggleButton;
    // End of variables declaration//GEN-END:variables

    /*********************************************************************************************************
     * abstract functions
     ********************************************************************************************************/
    protected abstract void initComponents();
    protected abstract IModelProvider connectToDevice();
    protected abstract Thread doIt(final IModelProvider model);
}

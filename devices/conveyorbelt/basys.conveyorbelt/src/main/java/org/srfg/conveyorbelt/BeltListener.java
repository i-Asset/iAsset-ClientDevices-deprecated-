package org.srfg.conveyorbelt;

/*********************************************************************************************************
 * BeltListener
 ********************************************************************************************************/
public interface BeltListener {

    // writable variables
    public void MoveBeltChanged();
    public void SwitchBusyLightChanged();

    // readable variables
    public void ConBeltStateChanged();
    public void ConBeltDistChanged();
    public void ConBeltMovingChanged();
}

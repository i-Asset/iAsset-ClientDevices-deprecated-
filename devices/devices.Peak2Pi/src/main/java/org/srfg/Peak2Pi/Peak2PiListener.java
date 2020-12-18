package org.srfg.Peak2Pi;

/*********************************************************************************************************
 * Peak2PiListener
 ********************************************************************************************************/
public interface Peak2PiListener {

    public void robotModeChanged();
    public void posXChanged();
    public void posYChanged();
    public void posZChanged();
    public void forceZChanged();
    public void gripperDistanceChanged();
}

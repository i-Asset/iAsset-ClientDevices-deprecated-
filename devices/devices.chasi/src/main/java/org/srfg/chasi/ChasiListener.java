package org.srfg.chasi;

/*********************************************************************************************************
 * ChasiListener
 ********************************************************************************************************/
public interface ChasiListener {

    public void robotModeChanged();
    public void posXChanged();
    public void posYChanged();
    public void posZChanged();
    public void forceZChanged();
    public void gripperDistanceChanged();
}

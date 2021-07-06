package at.srfg.panda;

/*********************************************************************************************************
 * PandaListener
 ********************************************************************************************************/
public interface PandaListener {

    public void robotModeChanged();
    public void posXChanged();
    public void posYChanged();
    public void posZChanged();
    public void forceZChanged();
    public void gripperDistanceChanged();
}

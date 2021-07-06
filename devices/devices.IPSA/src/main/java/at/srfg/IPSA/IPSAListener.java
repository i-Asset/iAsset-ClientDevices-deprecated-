package at.srfg.IPSA;

/*********************************************************************************************************
 * IPSAListener
 ********************************************************************************************************/
public interface IPSAListener {

    public void robotModeChanged();
    public void posXChanged();
    public void posYChanged();
    public void posZChanged();
    public void forceZChanged();
    public void gripperDistanceChanged();
}

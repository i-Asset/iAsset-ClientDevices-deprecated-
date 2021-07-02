package org.srfg.panda.nodes;

import org.srfg.panda.PandaDevice;

public class ROSMain {
    public static void main(String[] args) {
        final PandaDevice myPanda = new PandaDevice();
        ROSNodeManager rnm = new ROSNodeManager(myPanda);
        rnm.startROSNodes();

    }

}

package org.srfg.basedevice;

import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.server.AASHTTPServer;

import java.util.Map;

public abstract class BaseDevice {

    /*********************************************************************************************************
     * abstract start/stop
     ********************************************************************************************************/
    public abstract void start();
    public abstract void stop();

    protected abstract IModelProvider createAAS();
    protected abstract SubModel createIdentification();
    protected abstract SubModel createProperties();
    protected abstract Map<String, Object> createModel();

    /*********************************************************************************************************
     * hostComponent
     ********************************************************************************************************/
    //public void hostComponent(AASHTTPServer server)
    //{

    //}

    /*********************************************************************************************************
     * register
     ********************************************************************************************************/
    //public void register()
    //{

    //}
}

package org.srfg.basedevice;

import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.vab.directory.api.IVABDirectoryService;
import org.eclipse.basyx.vab.directory.memory.InMemoryDirectory;
import org.eclipse.basyx.vab.directory.restapi.DirectoryModelProvider;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.server.AASHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.srfg.properties.MyProperties;
import org.srfg.requests.RequestManager;

import javax.servlet.http.HttpServlet;
import java.util.Map;

public abstract class BaseDevice {

    private MyProperties properties = new MyProperties();

    /*********************************************************************************************************
     * abstract getters name/dir
     ********************************************************************************************************/
    public abstract String getName();
    public abstract String getDirectory();

    /*********************************************************************************************************
     * abstract start/stop
     ********************************************************************************************************/
    public abstract void start();
    public abstract void stop();

    /*********************************************************************************************************
     * abstract AAS methods
     ********************************************************************************************************/
    protected abstract IModelProvider createAAS();
    protected abstract SubModel createIdentification();
    protected abstract SubModel createProperties();
    protected abstract Map<String, Object> createModel();

    /*********************************************************************************************************
     * hostComponent
     ********************************************************************************************************/
    public void hostComponent(AASHTTPServer server)
    {
        Map<String, Object> beltMap = this.createModel();
        IModelProvider beltAAS = this.createAAS();
        IModelProvider modelProvider = new VABLambdaProvider(beltMap);
        HttpServlet aasServlet = new VABHTTPInterface<IModelProvider>(beltAAS);
        HttpServlet modelServlet = new VABHTTPInterface<IModelProvider>(modelProvider);
        IVABDirectoryService directory = new InMemoryDirectory();

        // Register the VAB model at the directory (locally in this case)
        String fullAddress = "http://" + properties.getDeviceAddress() + ":" + properties.getDevicePort() + "/iasset" + getDirectory();
        directory.addMapping(getName() + "01", fullAddress);

        IModelProvider directoryProvider = new DirectoryModelProvider(directory);
        HttpServlet directoryServlet = new VABHTTPInterface<IModelProvider>(directoryProvider);

        // asset exposes its functionality with localhost & port 5000
        BaSyxContext context = new BaSyxContext("/iasset", "",
                properties.getDeviceAddress(),
                Integer.parseInt(properties.getDevicePort()));
        context.addServletMapping("/directory/*", directoryServlet);
        context.addServletMapping(getDirectory() + "/*", modelServlet);
        context.addServletMapping("/" + getName() + "/*", aasServlet);

        // Now, define a context to which multiple servlets can be added
        server = new AASHTTPServer(context);
        server.start();
    }

    /*********************************************************************************************************
     * register
     ********************************************************************************************************/
    public void register() // TODO: finish as soon as new Backend is done
    {
        RequestManager manager = new RequestManager();

        // register AAS descriptor for lookup of others
        //manager.SendRegisterRequest(RequestManager.RegistryType.eDirectory, "POST", "/" + getName());

        // register full AAS
        //manager.SendRegisterRequest(RequestManager.RegistryType.eFullAAS, "POST", "{\"name\": \"sample\", \"job\": \"robot\"}");
    }
}

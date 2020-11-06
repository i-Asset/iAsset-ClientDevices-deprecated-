package org.srfg.basedevice;

import org.apache.commons.io.IOUtils;
import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.vab.directory.api.IVABDirectoryService;
import org.eclipse.basyx.vab.directory.memory.InMemoryDirectory;
import org.eclipse.basyx.vab.directory.restapi.DirectoryModelProvider;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.server.AASHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.json.JSONObject;
import org.srfg.properties.MyProperties;
import org.srfg.requests.RequestManager;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    public void register() // TODO: refactor as soon as new Backend is done
    {
        String jsonParameterType = "";
        String strAssetTypeName = "";
        try {
            InputStream isType = getClass().getResourceAsStream("/myAssetType.json");
            jsonParameterType = IOUtils.toString(isType, StandardCharsets.UTF_8.name());

            JSONObject jsonObject = new JSONObject(jsonParameterType);
            strAssetTypeName = jsonObject.getString("name");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // make request manager
        RequestManager manager = new RequestManager();

        // register AAS descriptor for lookup of others
        manager.SendRegisterRequest(RequestManager.RegistryType.eAssetType, "POST", jsonParameterType);

        // register full AAS
        manager.SendRegisterRequest(RequestManager.RegistryType.eAssetInstance, "POST",
                "{\n" +
                        "  \"assetImages\": [\n" +
                        "    {\n" +
                        "      \"fileName\": \"SRFGLabImage\",\n" +
                        "      \"id\": 0,\n" +
                        "      \"languageID\": \"\",\n" +
                        "      \"mimeCode\": \"image/png\",\n" +
                        "      \"objectMetadata\": \"\",\n" +
                        "      \"uri\": \"\",\n" +
                        "      \"value\": \"" + properties.getImageStringBase64() + "\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"assetType\": \"" + strAssetTypeName + "\",\n" +
                        "  \"currentLocation\": \"" + properties.getAssetInstanceCurrentLocation() + "\",\n" +
                        "  \"id\": 0,\n" +
                        "  \"listAvailableProperties\": \"" + properties.getAssetInstanceAvailableProperties() + "\",\n" +
                        "  \"listMaintenance\": [],\n" +
                        "  \"name\": \"" + properties.getAssetInstanceName() + "\",\n" +
                        "  \"originalLocation\": \"" + properties.getAssetInstanceOriginalLocation() + "\",\n" +
                        "  \"ownerProperty\": \"" + properties.getAssetInstanceOwner() + "\",\n" +
                        "  \"serialNumber\": \"" + properties.getAssetInstanceSerialNumber() + "\"\n" +
                        "}");
    }
}

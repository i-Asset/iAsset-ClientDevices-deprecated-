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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

//import at.srfg.iot.aas.basic.AssetAdministrationShell;
//import at.srfg.iot.aas.basic.Identifier;
//import at.srfg.iot.aas.basic.Submodel;
//import at.srfg.iot.aas.common.Referable;
//import at.srfg.iot.aas.common.referencing.Key;
//import at.srfg.iot.aas.common.referencing.KeyElementsEnum;
//import at.srfg.iot.aas.common.referencing.Kind;
//import at.srfg.iot.aas.common.referencing.Reference;
//import at.srfg.iot.aas.common.types.DataTypeEnum;
//import at.srfg.iot.aas.common.types.DirectionEnum;
//import at.srfg.iot.aas.modeling.submodelelement.Operation;
//import at.srfg.iot.aas.modeling.submodelelement.OperationVariable;
//import at.srfg.iot.aas.modeling.submodelelement.Property;
//import at.srfg.iot.aas.modeling.submodelelement.SubmodelElementCollection;
//import at.srfg.iot.provider.IAssetProvider;
//import at.srfg.iot.provider.impl.AssetModel;


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
    public void register() {
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

        registerStyleFrontend(jsonParameterType, strAssetTypeName); // request for frontend compatible registration in Backend of Mathias
        registerStyleBasyx(jsonParameterType, strAssetTypeName); //  request with new iAsset-SDK for registration in Backend of Dietmar
    }

    /*********************************************************************************************************
     * registerStyleFrontend
     ********************************************************************************************************/
    public void registerStyleFrontend(String jsonParameterType, String strAssetTypeName) {

        RequestManager manager = new RequestManager();
        manager.SendRegisterRequest(RequestManager.RegistryType.eAssetType, "POST", jsonParameterType);
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
                        "  \"dataEndpoint\": \"" + properties.getAssetInstanceFullEndpoint() + "\",\n" +
                        "  \"listMaintenance\": [],\n" +
                        "  \"name\": \"" + properties.getAssetInstanceName() + "\",\n" +
                        "  \"originalLocation\": \"" + properties.getAssetInstanceOriginalLocation() + "\",\n" +
                        "  \"ownerProperty\": \"" + properties.getAssetInstanceOwner() + "\",\n" +
                        "  \"serialNumber\": \"" + properties.getAssetInstanceSerialNumber() + "\"\n" +
                        "}");
    }

    /*********************************************************************************************************
     * registerStyleBasyx
     ********************************************************************************************************/
    public void registerStyleBasyx(String jsonParameterType, String strAssetTypeName) {

   //    // component.registerWith(registry);
   //    IAssetRegistry registry = IAssetRegistry.connectWithRegistry("http://localhost:8085");
   //    // clone the AssetAdministrationShell from a given type
   //    IAssetProvider beltProvider = registry.fromType(
   //            new Identifier("http://iasset.labor/belt"),
   //            new Identifier("http://iasset.salzburgresearch.at/labor/belt#aas"));
   //    // use the idShort - possibly for an alias
   //    beltProvider.getRoot().setIdShort("belt");
   //    // directly access an operation element and set the function which is to execute on INVOKE
   //    beltProvider.setFunction("operations/setSpeed", new Function<Map<String, Object>, Object>() {

   //        @Override
   //        public Object apply(Map<String, Object> t) {
   //            if (!t.containsKey("speed")) {
   //                throw new IllegalStateException("Missing parameter [message]");
   //            }
   //            return "Set speed to the new value: " + t.get("speed");
   //        }
   //    });
   //    // add a consumer function to the property
   //    beltProvider.setValueConsumer("properties/beltData/distance",
   //            // setter function, simply show the new value at System.out
   //            (String t) -> System.out.println("Distance value change: " + t));
   //    // add a supplier function to the property
   //    beltProvider.setValueSupplier("properties/beltData/distance",
   //            // the getter function must return a string
   //            () -> "Distance value read: " + LocalDateTime.now().toString());
   //    // tell the registry to serve the model with the context path "alias"
   //    registry.serve(beltProvider, "belt");
   //    // serve the AAS
   //    registry.serve(shell(), "xyz");
   //    // serve the Submodel only
   //    registry.serve(submodel(), "other");
   //    // now start the service, e.g. provide the endpoint
   //    registry.start();
   //    // pseudo-code
   //    // register the shell with the system
   //    registry.register(beltProvider);

   //    registry.delete(beltProvider.getRoot().getIdentification());
   //    // iasset.register(aShellProvider);
   //    // iasset.create()
   //    // iasset.delete(aShellProvider.getRoot().getIdentification());
   //    registry.stop();
    }
}

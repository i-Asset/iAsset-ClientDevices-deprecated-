package at.srfg.basedevice;

import java.util.Map;

import at.srfg.properties.MyProperties;
import org.json.JSONObject;

import at.srfg.iot.common.aas.IAssetModel;
import at.srfg.iot.common.datamodel.asset.aas.basic.AssetAdministrationShell;
import at.srfg.iot.common.datamodel.asset.aas.basic.Identifier;
import at.srfg.iot.common.datamodel.asset.aas.basic.Submodel;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.Kind;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.Reference;
import at.srfg.iot.common.registryconnector.AssetComponent;
import at.srfg.iot.common.registryconnector.IAssetRegistry;

public abstract class BaseDevice {

    private MyProperties properties;
    private IAssetRegistry registry;
    private IAssetModel instance;
    private IAssetModel type;
    private AssetComponent edgeServer;

    public BaseDevice()
    {
        // create class members
        properties = new MyProperties();
        registry = IAssetRegistry.componentWithRegistry(properties.getServerAddress());

        // Auto-register with local type if no type exists
        registerAssetTypeAutomaticallyIfNotExists();
    }


    /*********************************************************************************************************
     * abstract getters name/dir
     ********************************************************************************************************/
    public abstract String getName();
    public abstract String getDirectory();
    public abstract String getAssetTypeFromResources();

    /*********************************************************************************************************
     * abstract start/stop
     ********************************************************************************************************/
    public abstract void start();
    public abstract void stop();

    /*********************************************************************************************************
     * abstract AAS methods
     ********************************************************************************************************/
    protected abstract Submodel createIdentification();
    protected abstract Submodel createProperties();
    protected abstract Map<String, Object> createModel();

    /*********************************************************************************************************
     * alter an existing base instance, received from server
     ********************************************************************************************************/
    protected IAssetModel alterExistingInstance(IAssetModel existingInstance) {

        existingInstance.getRoot().setIdentification(new Identifier(this.getName() + "01"));
        existingInstance.getRoot().setIdShort(this.getName() + "01");

        return existingInstance;
    }

    /*********************************************************************************************************
     * create a basic Asset Administration Shell
     ********************************************************************************************************/
    protected IAssetModel createNewInstance() {

        AssetAdministrationShell aas = new AssetAdministrationShell();
        aas.setIdentification(new Identifier(this.getName() + "01"));
        aas.setIdShort(this.getName() + "01");

        Submodel id = createIdentification();
        id.setKind(Kind.Instance);
        aas.addSubmodel(id);

        Submodel prop = createProperties();
        prop.setKind(Kind.Instance);
        aas.addSubmodel(prop);

        return registry.create(getName(), aas);
    }

    /*********************************************************************************************************
     * create a basic Asset Administration Shell
     ********************************************************************************************************/
    protected IAssetModel createNewType() {

        String str = this.getAssetTypeFromResources();
        AssetAdministrationShell aas = new AssetAdministrationShell(new JSONObject(str)); // init with JSONFile
        return registry.create(getName(), aas);
    }
    protected IAssetModel getModel() {
    	if ( instance == null) {
    		registerAssetTypeAutomaticallyIfNotExists();
    	}
    	return instance;
    }
    /*********************************************************************************************************
     * registerTypeAutomaticallyIfNeeded
     ********************************************************************************************************/
    protected void registerAssetTypeAutomaticallyIfNotExists() {

        // use ".fromType"-function to check for existing type on server repository -> will create instance based on type!
        instance = registry.create(this.getName(),
                new Identifier("http://iasset.labor/" + this.getName()),
                new Identifier("http://iasset.salzburgresearch.at/labor/" + this.getName() +"#aas"));

        if(instance == null) // if type does not exist
        {
            // register local asset type stored in JSON file (kind.type);
            type = createNewType();
            registerType();

            // then register instance based on that type (kind.instance);
            instance = this.createNewInstance();

            // refer instance to created type
            JSONObject jsonObject = new JSONObject(this.getAssetTypeFromResources());
            instance.setElement(new Reference((String)jsonObject.get("idShort")), null);
        }
        else // if type DOES exist -> register instance with reference to existing online type ((1)kind.instance)
        {
            // set properties of root aas
            // alterExistingInstance(instance);
        }
    }

    /*********************************************************************************************************
     * startHostComponent / stopHostComponent
     *
     * Tell the Industry 4.0 Component to serve the INSTANCE with a given context and start the component's endpoint.
     * The AAS can be accessed from outside then.
     ********************************************************************************************************/
    public void startHostComponent()
    {
    	edgeServer = registry.getComponent(5000);
    	edgeServer.serve(instance, this.getName());
//        registry.serve(instance, this.getName());
        edgeServer.start();
    }
    public void stopHostComponent()
    {
    	if ( edgeServer != null ) {
    		edgeServer.stop();
    	}
    }

    /*********************************************************************************************************
     * registerInstance
     *
     * Register the INSTANCE with the registry. Creates a copy of the instance in the repository
     ********************************************************************************************************/
    public void registerType()
    {
//        registry.serve(type, this.getName());
//        registry.start();
//        registry.register(type);
    }

    /*********************************************************************************************************
     * registerInstance
     *
     * Register the INSTANCE with the registry. Creates a copy of the instance in the repository
     ********************************************************************************************************/
    public void registerInstance()
    {
//        registry.serve(instance, this.getName());
//        registry.start();
        registry.save(instance);
    }
}

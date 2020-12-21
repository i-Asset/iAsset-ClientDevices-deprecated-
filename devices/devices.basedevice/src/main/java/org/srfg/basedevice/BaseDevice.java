package org.srfg.basedevice;

import at.srfg.iot.common.datamodel.asset.aas.basic.AssetAdministrationShell;
import at.srfg.iot.common.datamodel.asset.aas.basic.Submodel;
import at.srfg.iot.common.datamodel.asset.provider.impl.AssetModel;
import at.srfg.iot.common.datamodel.asset.provider.IAssetProvider;
import at.srfg.iot.common.registryconnector.IAssetRegistry;
import at.srfg.iot.common.datamodel.asset.aas.basic.Identifier;

import org.srfg.properties.MyProperties;
import java.util.Map;

public abstract class BaseDevice {

    private MyProperties properties;
    private IAssetRegistry registry;
    private IAssetProvider instance;

    public BaseDevice()
    {
        // create class members
        properties = new MyProperties();
        registry = IAssetRegistry.componentWithRegistry(properties.getServerAddress())
                                 .componentAtPort(5000); // device address will be assigned automatically

        // create AAS (the instance root) and instance
        //instance = registry.fromType(
        //        new Identifier("http://iasset.labor/" + this.getName()),
        //        new Identifier("http://iasset.salzburgresearch.at/labor/" + this.getName() +"#aas"));

        this.createInstance();
    }


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
    protected abstract Submodel createIdentification();
    protected abstract Submodel createProperties();
    protected abstract Map<String, Object> createModel();

    /*********************************************************************************************************
     * create a basic Asset Administration Shell
     ********************************************************************************************************/
    protected void createInstance() {

        AssetAdministrationShell aas = new AssetAdministrationShell();
        aas.setIdentification(new Identifier(this.getName() + "01"));
        aas.setIdShort(this.getName() + "01");

        Submodel id = createIdentification();
        aas.addSubmodel(id);

        Submodel prop = createProperties();
        aas.addSubmodel(prop);

        instance = new AssetModel(aas);
    }

    /*********************************************************************************************************
     * startHostComponent / stopHostComponent
     *
     * Tell the Industry 4.0 Component to serve the INSTANCE with a given context and start the component's endpoint.
     * The AAS can be accessed from outside then.
     ********************************************************************************************************/
    public void startHostComponent()
    {
        registry.serve(instance, this.getName());
        registry.start();
    }
    public void stopHostComponent()
    {
        registry.stop();
    }

    /*********************************************************************************************************
     * register
     *
     * Register the INSTANCE with the registry. Creates a copy of the instance in the repository
     ********************************************************************************************************/
    public void register()
    {
        registry.register(instance);
    }
}

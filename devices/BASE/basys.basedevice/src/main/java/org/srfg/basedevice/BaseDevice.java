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
        registry = IAssetRegistry.componentWithRegistry(properties.getServerAddress());
        instance = registry.fromType(
                new Identifier("http://iasset.labor/" + this.getName()),
                new Identifier("http://iasset.salzburgresearch.at/labor/" + this.getName() +"#aas"));
        instance.getRoot().setIdShort(this.getName()); // use the idShort - possibly for an alias


        // TODO register the AAS is still missing
        IAssetProvider AAS = this.createAAS(); // ????? Whats next???
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
    protected IAssetProvider createAAS() {

        AssetAdministrationShell aas = new AssetAdministrationShell();
        aas.setIdentification(new Identifier(this.getName() + "01"));
        aas.setIdShort(this.getName() + "01");

        Submodel id = createIdentification();
        aas.addSubmodel(id);

        Submodel prop = createProperties();
        aas.addSubmodel(prop);

        IAssetProvider shellProvider = new AssetModel(aas);
        return shellProvider;
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

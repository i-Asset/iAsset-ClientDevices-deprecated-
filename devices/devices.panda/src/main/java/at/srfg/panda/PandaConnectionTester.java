package at.srfg.panda;

import at.srfg.iot.common.aas.IAssetModel;
import at.srfg.iot.common.datamodel.asset.aas.basic.Identifier;
import at.srfg.iot.common.datamodel.asset.aas.modeling.submodelelement.Property;
import at.srfg.iot.common.registryconnector.AssetComponent;
import at.srfg.iot.common.registryconnector.IAssetMessaging;
import at.srfg.iot.common.registryconnector.IAssetRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PandaConnectionTester {

    private static final Logger logger = LoggerFactory.getLogger(PandaConnectionTester.class);

    public static void main(String[] args) {

        PandaDevice myPanda = new PandaDevice();
        myPanda.start();

        IAssetRegistry registry = IAssetRegistry.componentWithRegistry("https://iasset.salzburgresearch.at/registry-service/");

        /*
        registry.addModelListener(new IAssetModelListener() {

            @Override
            public void onEventElementCreate(String path, EventElement element) {
                System.out.println(String.format("Event %s - element created", path));

            }

            @Override
            public void onEventElementRemove(String path, EventElement element) {
                System.out.println(String.format("Event %s - element removed", path));

            }

            @Override
            public void onOperationCreate(String path, Operation element) {
                System.out.println(String.format("Operation %s - element created", path));

            }

            @Override
            public void onOperationRemove(String path, Operation element) {
                System.out.println(String.format("Operation %s - element removed", path));

            }

            @Override
            public void onPropertyCreate(String path, Property property) {
                System.out.println(String.format("Property %s - element created", path));
                property.setGetter(() -> "Value of Property " + path + " " + LocalDateTime.now()); // pick up value from device!

            }

            @Override
            public void onPropertyRemove(String path, Property property) {
                System.out.println(String.format("Property %s - element removed", path));

            }

            @Override
            public void onValueChange(DataElement<?> element, Object oldValue, Object newValue) {
                // TODO Auto-generated method stub
                System.out.println(String.format("Element %s - value changed: Old %s, New: %s", element.asReference().getPath(), oldValue, newValue));

            }


        });*/

        /**
         * connect an existing AAS or Submodel (loaded from file)
         *
         *
         */
        IAssetModel pandaInstance = registry.create("panda",
                // Identifier of new Shell (instance)
                new Identifier("http://iasset.labor/panda"), // instance-id
                // Identifier of Shell-Template (type)
                new Identifier("http://iasset.salzburgresearch.at/labor/panda#aas")); // type-d

        pandaInstance.setValueSupplier("properties/position/posX",
                // the getter function must return a string
                () -> "" + myPanda.getPositionX()); // alternative: pick up value from physical device
        pandaInstance.setValueSupplier("properties/position/posY",
                // the getter function must return a string
                () -> "" + myPanda.getPositionY());
        pandaInstance.setValueSupplier("properties/position/posZ",
                // the getter function must return a string
                () -> "" + myPanda.getPositionZ());
        pandaInstance.setValueSupplier("properties/gripper/zForce",
                // the getter function must return a string
                () -> "" + myPanda.getForceZ());
        pandaInstance.setValueSupplier("properties/gripper/distance",
                // the getter function must return a string
                () -> "" + myPanda.getGripperDistance());

        /**
         * Obtain the edge component
         */
        AssetComponent edgeServer = registry.getComponent(5000);
        // add the distinct models to serve
        edgeServer.serve(pandaInstance, "panda");
//		AssetComponent edgeServer2 = registry.getComponent(5005);
//		edgeServer2.serve(beltInstance2, "belt2");
        // start the edge component, e.g. activate the endpoint
        edgeServer.start();
        logger.info("service started {}", edgeServer);

//		edgeServer2.start();


        IAssetMessaging messenger = registry.getMessaging();
        messenger.startup();
        logger.info("messenger started {}", messenger);

        /**
         * Updates all elements, save only has effect when the
         * AAS model has been registered, this is done during startup
         * of the AssetComponent
         */
        registry.save(pandaInstance);
        /**
         *
         */
        IAssetModel connected = registry.connect(pandaInstance.getRoot().getIdentification());
        // connected must do at this point:
        // - resolve the reference for "properties"
        // -
        Optional<Property> gripperDistProperty = connected.getElement("properties/gripper/distance", Property.class);
        if (gripperDistProperty.isPresent()) {
            logger.info("Gripper Distance Value: {}", gripperDistProperty.get().getValue());
        }

        try {
            logger.info("waiting for requests...");
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {

            /**
             * Deactivate the service endpoint(s)
             */
            logger.info("stopping system");
            edgeServer.stop();
            messenger.shutdown();
        }
    }

}

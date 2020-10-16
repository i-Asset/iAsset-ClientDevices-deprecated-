package org.srfg.panda;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.VABMultiSubmodelProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.SubModelProvider;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.vab.directory.api.IVABDirectoryService;
import org.eclipse.basyx.vab.directory.memory.InMemoryDirectory;
import org.eclipse.basyx.vab.directory.restapi.DirectoryModelProvider;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;
import org.eclipse.basyx.vab.protocol.http.server.AASHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.srfg.panda.nodes.ROSNodeManager;
import org.srfg.properties.MyProperties;
import org.srfg.requests.RequestManager;

import javax.servlet.http.HttpServlet;


/********************************************************************************************************
 * This class implements the smart manufacturing robot franka panda
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class PandaDevice {

	private final String registryDir = "/lab/panda/panda01";
	private MyProperties properties = new MyProperties();

	// needed for ROS communication
	private ROSNodeManager nodeManager;

	private PandaListener listener;
	private final String id;
	private boolean active;

	private double speed;
	private double distanceRun;

	private byte robotMode;
	private double positonX;
	private double positonY;
	private double positonZ;
	private double forceZ;
	private double gripperDistance;


	/*********************************************************************************************************
	 * CTOR
	 ********************************************************************************************************/
	public PandaDevice(String id) {
		this.id = id;
		this.nodeManager = new ROSNodeManager(this);
	}

	public String getId() {
		return this.id;
	}

	/*********************************************************************************************************
	 * PandaListener
	 ********************************************************************************************************/
	private class PandaRunner implements Runnable {

		@Override
		public void run() {
			while (active) {
                // TODO: add cyclic stuff here
			}
		}
	}

	/*********************************************************************************************************
	 * PandaListener
	 ********************************************************************************************************/
	public void start() {
		if (!isActive()) {

			nodeManager.startROSNodes();
			System.out.println(String.format("Starting panda %s with speed (%s)", id, Double.toString(speed)));
			active = true;

			new Thread(new PandaRunner()).start();
		}
	}

	/*********************************************************************************************************
	 * PandaListener
	 ********************************************************************************************************/
	public void stop() {
		if (isActive()) {

			nodeManager.shutdownROSNodes();
			System.out.println(String.format("Stopping panda %s, current speed setting (%s)", id, Double.toString(speed)));
			active = false;
		}
	}

	/*********************************************************************************************************
	 * Active
	 ********************************************************************************************************/
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		if (active != this.active) {
			if (active) {
				start();
			} else {
				stop();
			}
		}
		// no effect
		this.active = active;
	}

	/*********************************************************************************************************
	 * Listener
	 ********************************************************************************************************/
	public PandaListener getListener() {
		return listener;
	}
	public void setListener(PandaListener listener) {
		this.listener = listener;
	}

	/*********************************************************************************************************
	 * PandaListener - RobotMode
	 ********************************************************************************************************/
	public byte getRobotMode() {
		return robotMode;
	}
	public void setRobotMode(byte robotMode) {
		this.robotMode = robotMode;
		if (listener != null) {
			listener.robotModeChanged();
		}
	}

	/*********************************************************************************************************
	 * PandaListener - PositionX
	 ********************************************************************************************************/
	public double getPositionX() {
		return positonX;
	}
	public void setPositionX(double positionX) {
		this.positonX = positionX;
		if (listener != null) {
			listener.posXChanged();
		}
	}

	/*********************************************************************************************************
	 * PandaListener - PositionY
	 ********************************************************************************************************/
	public double getPositionY() {
		return positonY;
	}
	public void setPositionY(double positionY) {
		this.positonY = positionY;
		if (listener != null) {
			listener.posYChanged();
		}
	}

	/*********************************************************************************************************
	 * PandaListener - PositionZ
	 ********************************************************************************************************/
	public double getPositionZ() {
		return positonZ;
	}
	public void setPositionZ(double positionZ) {
		this.positonZ = positionZ;
		if (listener != null) {
			listener.posZChanged();
		}
	}

	/*********************************************************************************************************
	 * PandaListener - forceZ
	 ********************************************************************************************************/
	public double getForceZ() {
		return forceZ;
	}
	public void setForceZ(double forceZ) {
		this.forceZ = forceZ;
		if (listener != null) {
			listener.forceZChanged();
		}
	}

	/*********************************************************************************************************
	 * PandaListener - gripperDistance
	 ********************************************************************************************************/
	public double getGripperDistance() {
		return gripperDistance;
	}
	public void setGripperDistance(double gripperDistance) {
		this.gripperDistance = gripperDistance;
		if (listener != null) {
			listener.gripperDistanceChanged();
		}
	}

	/*********************************************************************************************************
	 * hostComponent
	 ********************************************************************************************************/
	public void hostComponent(AASHTTPServer server)
	{
		Map<String, Object> beltMap = PandaDevice.createModel(this);
		IModelProvider beltAAS = PandaDevice.createAAS(this);
		IModelProvider modelProvider = new VABLambdaProvider(beltMap);
		HttpServlet aasServlet = new VABHTTPInterface<IModelProvider>(beltAAS);

		// Now, the model provider is given to a HTTP servlet that gives access to the model in the next steps
		// => The model will be published using an HTTP-REST interface
		HttpServlet modelServlet = new VABHTTPInterface<IModelProvider>(modelProvider);
		IVABDirectoryService directory = new InMemoryDirectory();

		// Register the VAB model at the directory (locally in this case)
		String fullAddress = "http://" + properties.getDeviceAddress() + ":" + properties.getDevicePort() + "/iasset" + registryDir;
		directory.addMapping("panda01", fullAddress);
		// logger.info("ConveyorBelt model registered!");

		IModelProvider directoryProvider = new DirectoryModelProvider(directory);
		HttpServlet directoryServlet = new VABHTTPInterface<IModelProvider>(directoryProvider);


		// asset exposes its functionality with localhost & port 5000
		BaSyxContext context = new BaSyxContext("/iasset", "",
				properties.getDeviceAddress(),
				Integer.parseInt(properties.getDevicePort()));
		context.addServletMapping("/directory/*", directoryServlet);
		context.addServletMapping(registryDir + "/*", modelServlet);
		context.addServletMapping("/panda/*", aasServlet);

		// Now, define a context to which multiple servlets can be added
		// The model will be available at http://localhost:4001/handson/oven/
		// The directory will be available at http://localhost:4001/handson/directory/
		server = new AASHTTPServer(context);
		server.start();
	}

	/*********************************************************************************************************
	 * register
	 ********************************************************************************************************/
	public void register()
	{
		// TEST
		RequestManager manager = new RequestManager();

		// register AAS descriptor for lookup of others
		manager.SendRegisterRequest(RequestManager.RegistryType.eDirectory, "POST", "/panda");

		// register full AAS (TEST)
		manager.SendRegisterRequest(RequestManager.RegistryType.eFullAAS, "POST", "{\"name\": \"Panda\", \"job\": \"robot\"}");
	}



	/*********************************************************************************************************
	 * PandaListener
	 ********************************************************************************************************/
	public static IModelProvider createAAS(PandaDevice panda) {

		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.setIdentification(IdentifierType.CUSTOM, panda.getId());
		aas.setIdShort(panda.getId());

		SubModel id = createIdentification(panda);
		SubmodelDescriptor idDesc = new SubmodelDescriptor(id);
		aas.addSubModel(idDesc);

		SubModel prop = createProperties(panda);
		SubmodelDescriptor propDesc = new SubmodelDescriptor(prop);
		aas.addSubModel(propDesc);

		AASModelProvider shellProvider = new AASModelProvider(aas);
		VABMultiSubmodelProvider aasFull = new VABMultiSubmodelProvider(shellProvider);
		aasFull.addSubmodel("identification", new SubModelProvider(id));
		aasFull.addSubmodel("properties", new SubModelProvider(prop));

		return aasFull;
	}

	/*********************************************************************************************************
	 * PandaListener
	 ********************************************************************************************************/
	private static SubModel createIdentification(PandaDevice panda) {

		// create the sub model
		SubModel info = new SubModel();
		info.setIdShort("identification");
		info.setSemanticId((IReference) new Reference().put("27380107", null)); // e-class-ID "Roboterarm"
		info.setDescription(new LangStrings("de", "Herstellerinformationen"));
		info.setModelingKind(ModelingKind.INSTANCE);
		//info.setSemanticId(new Reference(new Key(KeyElements.SUBMODEL, false, "0173-1#02-AAV232#002", KeyType.IRDI)));

		Property manufacturer = new Property();
		manufacturer.setIdShort("manufacturer");
		manufacturer.set("FRANKA EMIKA Gmbh");
		manufacturer.setSemanticID(new Reference(new Key(KeyElements.PROPERTY, false, "0173-1#02-AAO677#002", KeyType.IRDI)));
		info.addSubModelElement(manufacturer);

		Property gln = new Property();
		gln.setIdShort("gln");
		gln.set("GLN-Number Franka Panda");
		gln.setSemanticID(new Reference(new Key(KeyElements.PROPERTY, false, "0173-1#02-AAY812#001", KeyType.IRDI)));
		info.addSubModelElement(gln);

		Property productFamily = new Property();
		productFamily.setIdShort("productFamily");
		productFamily.set("research robot arm");
		productFamily.setSemanticID(new Reference(new Key(KeyElements.PROPERTY, false, "0173-1#02-AAY812#001", KeyType.IRDI)));

		info.addSubModelElement(productFamily);
		return info;
	}

	/*********************************************************************************************************
	 * PandaListener
	 *
	 * INFO: For lambda properties, the type has to be explicitly specified as it
	 * can not be retrieved from supplier automatically
	 ********************************************************************************************************/
	private static SubModel createProperties(PandaDevice panda) {

		SubModel info = new SubModel();
		info.setIdShort("properties");
		info.setDescription(new LangStrings("de", "Statusinformationen"));
		info.setModelingKind(ModelingKind.INSTANCE);

		// Panda Properties (ReadAccess)
		Collection<IReference> listSpecificationsPositions = new LinkedList<IReference>();
		listSpecificationsPositions.add((IReference) new Reference().put("Centimeters", null));
		listSpecificationsPositions.add((IReference) new Reference().put("Inches", null));

		Collection<IReference> listSpecificationsForces = new LinkedList<IReference>();
		listSpecificationsForces.add((IReference) new Reference().put("Newton", null));

		// add property for robot mode
		Property modeProp = new Property(0);
		modeProp.setIdShort("robotMode");
		modeProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAK543#004", null)); // e-class-ID "anwenderrelevante Ausführung"
		modeProp.setDescription(new LangStrings("en", "robot mode represents current state of franka panda robot"));
		info.addSubModelElement(modeProp);

		// add properties for positions in 3D working env
		Property positionXProp = new Property(0);
		positionXProp.setIdShort("posX");
		positionXProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		positionXProp.setDescription(new LangStrings("en", "franka panda robot end effector position X"));
		positionXProp.setDataSpecificationReferences(listSpecificationsPositions);
		info.addSubModelElement(positionXProp);

		Property positionYProp = new Property(0);
		positionYProp.setIdShort("posY");
		positionYProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		positionYProp.setDescription(new LangStrings("en", "franka panda robot end effector position Y"));
		positionYProp.setDataSpecificationReferences(listSpecificationsPositions);
		info.addSubModelElement(positionYProp);

		Property positionZProp = new Property(0);
		positionZProp.setIdShort("posZ");
		positionZProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		positionZProp.setDescription(new LangStrings("en", "franka panda robot end effector position Z"));
		positionZProp.setDataSpecificationReferences(listSpecificationsPositions);
		info.addSubModelElement(positionZProp);

		// add property for panda force
		Property forceProp = new Property(0);
		forceProp.setIdShort("z-force");
		forceProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAI621#002", null)); // e-class-ID "Hebekraft"
		forceProp.setDescription(new LangStrings("en", "franka panda robot force for z-axis"));
		forceProp.setDataSpecificationReferences(listSpecificationsForces);
		info.addSubModelElement(forceProp);

		// add property for gripper states
		Property gripperProp = new Property(0);
		gripperProp.setIdShort("gripper distance");
		gripperProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		gripperProp.setDescription(new LangStrings("en", "distance of gripper parts to each other"));
		gripperProp.setDataSpecificationReferences(listSpecificationsPositions);
		info.addSubModelElement(gripperProp);

		return info;
	}

	/*********************************************************************************************************
	 * PandaListener
	 ********************************************************************************************************/
	public static Map<String, Object> createModel(PandaDevice panda) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("id", panda.getId()); // Add the id of the Panda to the model
		properties.put("desc", "Model connected with the edge device");

		// add robotmode property
		Supplier<Object> lambdaFunction3 = () -> panda.getRobotMode();
		Map<String, Object> lambdaProperty3 = VABLambdaProviderHelper.createSimple(lambdaFunction3, null);
		properties.put("robotmode", lambdaProperty3);

		// add posX property
		Supplier<Object> lambdaFunction4 = () -> panda.getPositionX();
		Map<String, Object> lambdaProperty4 = VABLambdaProviderHelper.createSimple(lambdaFunction4, null);
		properties.put("posX", lambdaProperty4);

		// add posY property
		Supplier<Object> lambdaFunction5 = () -> panda.getPositionY();
		Map<String, Object> lambdaProperty5 = VABLambdaProviderHelper.createSimple(lambdaFunction5, null);
		properties.put("posY", lambdaProperty5);

		// add posZ property
		Supplier<Object> lambdaFunction6 = () -> panda.getPositionZ();
		Map<String, Object> lambdaProperty6 = VABLambdaProviderHelper.createSimple(lambdaFunction6, null);
		properties.put("posZ", lambdaProperty6);

		// add forceZ property
		Supplier<Object> lambdaFunction7 = () -> panda.getForceZ();
		Map<String, Object> lambdaProperty7 = VABLambdaProviderHelper.createSimple(lambdaFunction7, null);
		properties.put("forceZ", lambdaProperty7);

		// add gripperDistance property
		Supplier<Object> lambdaFunction8 = () -> panda.getGripperDistance();
		Map<String, Object> lambdaProperty8 = VABLambdaProviderHelper.createSimple(lambdaFunction8, null);
		properties.put("gripperDistance", lambdaProperty8);


		// Create an empty container for custom operations
		Map<String, Object> operations = new HashMap<>();
		Function<Object, Object> activateFunction = (args) -> {
			panda.setActive(true);
			return null;
		};
		operations.put("start", activateFunction);

		// Add a function that deactivates the oven and implements a functional interface
		Function<Object, Object> deactivateFunction = (args) -> {
			panda.setActive(false);
			return null;
		};
		operations.put("stop", deactivateFunction);

		Map<String, Object> myModel = new HashMap<>();
		myModel.put("operations", operations);
		myModel.put("properties", properties);
		return myModel;
	}
}

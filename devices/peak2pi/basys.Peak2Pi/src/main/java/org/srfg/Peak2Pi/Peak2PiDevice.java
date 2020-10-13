package org.srfg.Peak2Pi;

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
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;


/********************************************************************************************************
 * This class implements the Peak2Pi controller for iAsset
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class Peak2PiDevice {

	//public Map<String, ModelUrn> getAllModelURNShortcuts() {return objectIDs;}

	private Peak2PiListener listener;

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


	public Peak2PiDevice(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;

	}

	/*********************************************************************************************************
	 * Peak2PiRunner
	 ********************************************************************************************************/
	private class Peak2PiRunner implements Runnable {

		@Override
		public void run() {
			while (active) {
                // TODO: add cyclic stuff here
			}
		}
	}

	/*********************************************************************************************************
	 * start
	 ********************************************************************************************************/
	public void start() {
		if (!isActive()) {

			// TODO: start ISPRO_NG and other Peak2Pi communication

			//System.out.println(String.format("Starting Peak2Pi %s with speed (%s)", id, Double.toString(speed)));
			active = true;

			new Thread(new Peak2PiRunner()).start();
		}
	}

	/*********************************************************************************************************
	 * stop
	 ********************************************************************************************************/
	public void stop() {
		if (isActive()) {

			// TODO: end ISPRO_NG and other Peak2Pi communication

			//System.out.println(String.format("Stopping Peak2Pi %s, current speed setting (%s)", id, Double.toString(speed)));
			active = false;
		}
	}

	/*********************************************************************************************************
	 * isActive
	 ********************************************************************************************************/
	public boolean isActive() {
		return active;
	}

	/*********************************************************************************************************
	 * setActive
	 ********************************************************************************************************/
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
	 * RobotMode
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
	 * PositionX
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
	 * PositionY
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
	 * PositionZ
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
	 * forceZ
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
	 * gripperDistance
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
	 * createAAS
	 ********************************************************************************************************/
	public static IModelProvider createAAS(Peak2PiDevice peak2pi) {

		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.setIdentification(IdentifierType.CUSTOM, peak2pi.getId());
		aas.setIdShort(peak2pi.getId());

		SubModel id = createIdentification(peak2pi);
		SubmodelDescriptor idDesc = new SubmodelDescriptor(id);
		aas.addSubModel(idDesc);

		SubModel prop = createProperties(peak2pi);
		SubmodelDescriptor propDesc = new SubmodelDescriptor(prop);
		aas.addSubModel(propDesc);

		AASModelProvider shellProvider = new AASModelProvider(aas);
		VABMultiSubmodelProvider aasFull = new VABMultiSubmodelProvider(shellProvider);
		aasFull.addSubmodel("identification", new SubModelProvider(id));
		aasFull.addSubmodel("properties", new SubModelProvider(prop));

		return aasFull;
	}

	/*********************************************************************************************************
	 * createIdentification
	 ********************************************************************************************************/
	private static SubModel createIdentification(Peak2PiDevice peak2pi) {

	    // TODO: change this to Peak2Pi description

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
	 * createProperties
	 *
	 * INFO: For lambda properties, the type has to be explicitly specified as it
	 * can not be retrieved from supplier automatically
	 ********************************************************************************************************/
	private static SubModel createProperties(Peak2PiDevice peak2pi) {

        // TODO: change this to Peak2Pi description

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
	 * createModel
	 ********************************************************************************************************/
	public static Map<String, Object> createModel(Peak2PiDevice peak2pi) {

        // TODO: change this to Peak2Pi description

		Map<String, Object> properties = new HashMap<>();
		properties.put("id", peak2pi.getId()); // Add the id of the Panda to the model
		properties.put("desc", "Model connected with the edge device");

		// add robotmode property
		Supplier<Object> lambdaFunction3 = () -> peak2pi.getRobotMode();
		Map<String, Object> lambdaProperty3 = VABLambdaProviderHelper.createSimple(lambdaFunction3, null);
		properties.put("robotmode", lambdaProperty3);

		// add posX property
		Supplier<Object> lambdaFunction4 = () -> peak2pi.getPositionX();
		Map<String, Object> lambdaProperty4 = VABLambdaProviderHelper.createSimple(lambdaFunction4, null);
		properties.put("posX", lambdaProperty4);

		// add posY property
		Supplier<Object> lambdaFunction5 = () -> peak2pi.getPositionY();
		Map<String, Object> lambdaProperty5 = VABLambdaProviderHelper.createSimple(lambdaFunction5, null);
		properties.put("posY", lambdaProperty5);

		// add posZ property
		Supplier<Object> lambdaFunction6 = () -> peak2pi.getPositionZ();
		Map<String, Object> lambdaProperty6 = VABLambdaProviderHelper.createSimple(lambdaFunction6, null);
		properties.put("posZ", lambdaProperty6);

		// add forceZ property
		Supplier<Object> lambdaFunction7 = () -> peak2pi.getForceZ();
		Map<String, Object> lambdaProperty7 = VABLambdaProviderHelper.createSimple(lambdaFunction7, null);
		properties.put("forceZ", lambdaProperty7);

		// add gripperDistance property
		Supplier<Object> lambdaFunction8 = () -> peak2pi.getGripperDistance();
		Map<String, Object> lambdaProperty8 = VABLambdaProviderHelper.createSimple(lambdaFunction8, null);
		properties.put("gripperDistance", lambdaProperty8);


		// Create an empty container for custom operations
		Map<String, Object> operations = new HashMap<>();
		Function<Object, Object> activateFunction = (args) -> {
			peak2pi.setActive(true);
			return null;
		};
		operations.put("start", activateFunction);

		// Add a function that deactivates the oven and implements a functional interface
		Function<Object, Object> deactivateFunction = (args) -> {
			peak2pi.setActive(false);
			return null;
		};
		operations.put("stop", deactivateFunction);

		Map<String, Object> myModel = new HashMap<>();
		myModel.put("operations", operations);
		myModel.put("properties", properties);
		return myModel;
	}

	/*********************************************************************************************************
	 * getListener
	 ********************************************************************************************************/
	public Peak2PiListener getListener() {
		return listener;
	}

	/*********************************************************************************************************
	 * setListener
	 ********************************************************************************************************/
	public void setListener(Peak2PiListener listener) {
		this.listener = listener;
	}

}

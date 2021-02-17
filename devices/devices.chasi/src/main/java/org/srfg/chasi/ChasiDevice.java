package org.srfg.chasi;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import at.srfg.iot.common.datamodel.asset.aas.basic.Submodel;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.KeyElementsEnum;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.Kind;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.ReferableElement;
import at.srfg.iot.common.datamodel.asset.aas.modeling.submodelelement.Property;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.Reference;

import org.srfg.basedevice.BaseDevice;
import org.srfg.chasi.nodes.ROSNodeManager;


/********************************************************************************************************
 * This class implements the smart manufacturing robot ARTI Chasi
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ChasiDevice extends BaseDevice {

	// needed for ROS communication
	private ROSNodeManager nodeManager;
	private ChasiListener listener;
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
	public ChasiDevice() { this.nodeManager = new ROSNodeManager(this); }

	@Override
	public String getName() {return "chasi";}

	@Override
	public String getDirectory() {return "/lab/chasi/chasi01";}

	/*********************************************************************************************************
	 * ChasiListener
	 ********************************************************************************************************/
	private class ChasiRunner implements Runnable {

		@Override
		public void run() {
			while (active) {
                // TODO: add cyclic stuff here
			}
		}
	}

	/*********************************************************************************************************
	 * ChasiListener
	 ********************************************************************************************************/
	@Override
	public void start() {
		if (!isActive()) {

			nodeManager.startROSNodes();
			System.out.println(String.format("Starting chasi %s with speed (%s)", this.getName() + "01", Double.toString(speed)));
			active = true;

			new Thread(new ChasiRunner()).start();
		}
	}

	/*********************************************************************************************************
	 * ChasiListener
	 ********************************************************************************************************/
	@Override
	public void stop() {
		if (isActive()) {

			nodeManager.shutdownROSNodes();
			System.out.println(String.format("Stopping chasi %s, current speed setting (%s)", this.getName() + "01", Double.toString(speed)));
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
	public ChasiListener getListener() {
		return listener;
	}
	public void setListener(ChasiListener listener) {
		this.listener = listener;
	}

	/*********************************************************************************************************
	 * ChasiListener - RobotMode
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
	 * ChasiListener - PositionX
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
	 * ChasiListener - PositionY
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
	 * ChasiListener - PositionZ
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
	 * ChasiListener - forceZ
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
	 * ChasiListener - gripperDistance
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
	 * ChasiListener
	 ********************************************************************************************************/
	@Override
	protected Submodel createIdentification() {

		// create the sub model
		Submodel info = new Submodel();
		info.setIdShort("identification");
		info.setSemanticId(new Reference("27380107", KeyElementsEnum.Submodel)); // e-class-ID "Roboterarm"
		info.setDescription("de", "Herstellerinformationen");
		info.setKind(Kind.Instance);
		//info.setSemanticId(new Reference(new Key(KeyElements.SUBMODEL, false, "0173-1#02-AAV232#002", KeyType.IRDI)));

		Property manufacturer = new Property();
		manufacturer.setIdShort("manufacturer");
		manufacturer.setValue("FRANKA EMIKA Gmbh");
		manufacturer.setSemanticId(new Reference("0173-1#02-AAO677#002", KeyElementsEnum.Property));
		info.addSubmodelElement(manufacturer);

		Property gln = new Property();
		gln.setIdShort("gln");
		gln.setValue("GLN-Number ARTI Chasi");
		gln.setSemanticId(new Reference("0173-1#02-AAY812#001", KeyElementsEnum.Property));
		info.addSubmodelElement(gln);

		Property productFamily = new Property();
		productFamily.setIdShort("productFamily");
		productFamily.setValue("research robot arm");
		productFamily.setSemanticId(new Reference("0173-1#02-AAY812#001", KeyElementsEnum.Property));

		info.addSubmodelElement(productFamily);
		return info;
	}

	/*********************************************************************************************************
	 * ChasiListener
	 *
	 * INFO: For lambda properties, the type has to be explicitly specified as it
	 * can not be retrieved from supplier automatically
	 ********************************************************************************************************/
	@Override
	protected Submodel createProperties() {

		Submodel info = new Submodel();
		info.setIdShort("properties");
		info.setDescription("de", "Statusinformationen");
		info.setKind(Kind.Instance);

		// Chasi Properties (ReadAccess)
		LinkedList<ReferableElement> listSpecificationsPositions = new LinkedList<ReferableElement>();
		listSpecificationsPositions.add(new Reference("Centimeters", KeyElementsEnum.Property));
		listSpecificationsPositions.add(new Reference("Inches", KeyElementsEnum.Property));

		LinkedList<ReferableElement> listSpecificationsForces = new LinkedList<ReferableElement>();
		listSpecificationsForces.add(new Reference("Newton", KeyElementsEnum.Property));

		// add property for robot mode
		Property modeProp = new Property();
		modeProp.setIdShort("robotMode");
		modeProp.setSemanticId(new Reference("0173-1#02-AAK543#004", KeyElementsEnum.Property)); // e-class-ID "anwenderrelevante Ausführung"
		modeProp.setDescription("en", "robot mode represents current state of ARTI Chasi robot");
		info.addSubmodelElement(modeProp);

		// add properties for positions in 3D working env
		Property positionXProp = new Property();
		positionXProp.setIdShort("posX");
		positionXProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		positionXProp.setDescription("en", "ARTI Chasi robot end effector position X");
		positionXProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(positionXProp);

		Property positionYProp = new Property();
		positionYProp.setIdShort("posY");
		positionYProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		positionYProp.setDescription("en", "ARTI Chasi robot end effector position Y");
		positionYProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(positionYProp);

		Property positionZProp = new Property();
		positionZProp.setIdShort("posZ");
		positionZProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		positionZProp.setDescription("en", "ARTI Chasi robot end effector position Z");
		positionZProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(positionZProp);

		// add property for chasi force
		Property forceProp = new Property();
		forceProp.setIdShort("z-force");
		forceProp.setSemanticId(new Reference("0173-1#02-AAI621#002", KeyElementsEnum.Property)); // e-class-ID "Hebekraft"
		forceProp.setDescription("en", "ARTI Chasi robot force for z-axis");
		forceProp.setDataSpecification(listSpecificationsForces);
		info.addSubmodelElement(forceProp);

		// add property for gripper states
		Property gripperProp = new Property();
		gripperProp.setIdShort("gripper distance");
		gripperProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		gripperProp.setDescription("en", "distance of gripper parts to each other");
		gripperProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(gripperProp);

		return info;
	}

	/*********************************************************************************************************
	 * ChasiListener
	 ********************************************************************************************************/
	@Override
	protected Map<String, Object> createModel() {

		Map<String, Object> properties = new HashMap<>();
		properties.put("id", this.getName() + "01");
		properties.put("desc", "Model connected with the edge device");

		// add robotmode property
		Supplier<Object> lambdaFunction3 = () -> this.getRobotMode();
		properties.put("robotmode", lambdaFunction3);

		// add posX property
		Supplier<Object> lambdaFunction4 = () -> this.getPositionX();
		properties.put("posX", lambdaFunction4);

		// add posY property
		Supplier<Object> lambdaFunction5 = () -> this.getPositionY();
		properties.put("posY", lambdaFunction5);

		// add posZ property
		Supplier<Object> lambdaFunction6 = () -> this.getPositionZ();
		properties.put("posZ", lambdaFunction6);

		// add forceZ property
		Supplier<Object> lambdaFunction7 = () -> this.getForceZ();
		properties.put("forceZ", lambdaFunction7);

		// add gripperDistance property
		Supplier<Object> lambdaFunction8 = () -> this.getGripperDistance();
		properties.put("gripperDistance", lambdaFunction8);


		// Create an empty container for custom operations
		Map<String, Object> operations = new HashMap<>();
		Function<Object, Object> activateFunction = (args) -> {
			this.setActive(true);
			return null;
		};
		operations.put("start", activateFunction);

		// Add a function that deactivates the oven and implements a functional interface
		Function<Object, Object> deactivateFunction = (args) -> {
			this.setActive(false);
			return null;
		};
		operations.put("stop", deactivateFunction);

		Map<String, Object> myModel = new HashMap<>();
		myModel.put("operations", operations);
		myModel.put("properties", properties);
		return myModel;
	}
}

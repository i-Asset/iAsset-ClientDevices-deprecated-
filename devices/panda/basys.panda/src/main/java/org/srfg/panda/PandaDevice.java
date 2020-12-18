package org.srfg.panda;

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
import org.srfg.panda.nodes.ROSNodeManager;


/********************************************************************************************************
 * This class implements the smart manufacturing robot franka panda
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class PandaDevice extends BaseDevice {

	// needed for ROS communication
	private ROSNodeManager nodeManager;
	private PandaListener listener;
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
	public PandaDevice() { this.nodeManager = new ROSNodeManager(this); }

	@Override
	public String getName() {return "panda";}

	@Override
	public String getDirectory() {return "/lab/panda/panda01";}

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
	@Override
	public void start() {
		if (!isActive()) {

			nodeManager.startROSNodes();
			System.out.println(String.format("Starting panda %s with speed (%s)", this.getName() + "01", Double.toString(speed)));
			active = true;

			new Thread(new PandaRunner()).start();
		}
	}

	/*********************************************************************************************************
	 * PandaListener
	 ********************************************************************************************************/
	@Override
	public void stop() {
		if (isActive()) {

			nodeManager.shutdownROSNodes();
			System.out.println(String.format("Stopping panda %s, current speed setting (%s)", this.getName() + "01", Double.toString(speed)));
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
	 * PandaListener
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
		gln.setValue("GLN-Number Franka Panda");
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
	 * PandaListener
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

		// Panda Properties (ReadAccess)
		LinkedList<ReferableElement> listSpecificationsPositions = new LinkedList<ReferableElement>();
		listSpecificationsPositions.add(new Reference("Centimeters", KeyElementsEnum.Property));
		listSpecificationsPositions.add(new Reference("Inches", KeyElementsEnum.Property));

		LinkedList<ReferableElement> listSpecificationsForces = new LinkedList<ReferableElement>();
		listSpecificationsForces.add(new Reference("Newton", KeyElementsEnum.Property));

		// add property for robot mode
		Property modeProp = new Property();
		modeProp.setIdShort("robotMode");
		modeProp.setSemanticId(new Reference("0173-1#02-AAK543#004", KeyElementsEnum.Property)); // e-class-ID "anwenderrelevante Ausführung"
		modeProp.setDescription("en", "robot mode represents current state of franka panda robot");
		info.addSubmodelElement(modeProp);

		// add properties for positions in 3D working env
		Property positionXProp = new Property();
		positionXProp.setIdShort("posX");
		positionXProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		positionXProp.setDescription("en", "franka panda robot end effector position X");
		positionXProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(positionXProp);

		Property positionYProp = new Property();
		positionYProp.setIdShort("posY");
		positionYProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		positionYProp.setDescription("en", "franka panda robot end effector position Y");
		positionYProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(positionYProp);

		Property positionZProp = new Property();
		positionZProp.setIdShort("posZ");
		positionZProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		positionZProp.setDescription("en", "franka panda robot end effector position Z");
		positionZProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(positionZProp);

		// add property for panda force
		Property forceProp = new Property();
		forceProp.setIdShort("z-force");
		forceProp.setSemanticId(new Reference("0173-1#02-AAI621#002", KeyElementsEnum.Property)); // e-class-ID "Hebekraft"
		forceProp.setDescription("en", "franka panda robot force for z-axis");
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
	 * PandaListener
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

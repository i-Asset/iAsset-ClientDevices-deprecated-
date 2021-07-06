package at.srfg.chasi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import at.srfg.basedevice.BaseDevice;
import at.srfg.chasi.nodes.ROSNodeManager;


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

	private double linear_posX;
	private double linear_posY;
	private double linear_posZ;
	private double angular_posX;
	private double angular_posY;
	private double angular_posZ;


	/*********************************************************************************************************
	 * CTOR
	 ********************************************************************************************************/
	public ChasiDevice() { this.nodeManager = new ROSNodeManager(this); }

	@Override
	public String getName() {return "chasi";}

	@Override
	public String getDirectory() {return "/lab/chasi/chasi01";}

	@Override
	public String getAssetTypeFromResources()
	{
		byte[] bytes = new byte[0];
		try {
			URL resource = ChasiDevice.class.getClassLoader().getResource("MyAssetType.json");
			bytes = Files.readAllBytes(Paths.get(resource.toURI()));
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (URISyntaxException e) {e.printStackTrace();}

		return new String(bytes);
	}

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
			System.out.println(String.format("Starting chasi %s", this.getName() + "01"));
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
			System.out.println(String.format("Stopping chasi %s", this.getName() + "01"));
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
	 * ChasiListener - LinearPositionX
	 ********************************************************************************************************/
	public double getLinearPositionX() {
		return linear_posX;
	}
	public void setLinearPositionX(double positionX) {
		this.linear_posX = positionX;
		if (listener != null) {
			listener.linear_posX_Changed();
		}
	}

	/*********************************************************************************************************
	 * ChasiListener - LinearPositionY
	 ********************************************************************************************************/
	public double getLinearPositionY() {
		return linear_posY;
	}
	public void setLinearPositionY(double positionY) {
		this.linear_posY = positionY;
		if (listener != null) {
			listener.linear_posY_Changed();
		}
	}

	/*********************************************************************************************************
	 * ChasiListener - LinearPositionZ
	 ********************************************************************************************************/
	public double getLinearPositionZ() {
		return linear_posZ;
	}
	public void setLinearPositionZ(double positionZ) {
		this.linear_posZ = positionZ;
		if (listener != null) {
			listener.linear_posZ_Changed();
		}
	}

	/*********************************************************************************************************
	 * ChasiListener - AngularPositionX
	 ********************************************************************************************************/
	public double getAngularPositionX() {
		return angular_posX;
	}
	public void setAngularPositionX(double positionX) {
		this.angular_posX = positionX;
		if (listener != null) {
			listener.angular_posX_Changed();
		}
	}

	/*********************************************************************************************************
	 * ChasiListener - AngularPositionY
	 ********************************************************************************************************/
	public double getAngularPositionY() {
		return angular_posY;
	}
	public void setAngularPositionY(double positionY) {
		this.angular_posY = positionY;
		if (listener != null) {
			listener.angular_posY_Changed();
		}
	}

	/*********************************************************************************************************
	 * ChasiListener - AngularPositionZ
	 ********************************************************************************************************/
	public double getAngularPositionZ() {
		return angular_posZ;
	}
	public void setAngularPositionZ(double positionZ) {
		this.angular_posZ = positionZ;
		if (listener != null) {
			listener.angular_posZ_Changed();
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
		info.setSemanticId(new Reference("27380100", KeyElementsEnum.Submodel)); // e-class-ID "Roboter"
		info.setDescription("de", "Herstellerinformationen");
		info.setKind(Kind.Instance);
		//info.setSemanticId(new Reference(new Key(KeyElements.SUBMODEL, false, "0173-1#02-AAV232#002", KeyType.IRDI)));

		Property manufacturer = new Property();
		manufacturer.setIdShort("manufacturer");
		manufacturer.setValue("ARTI - Autonomous Robot Technology GmbH");
		manufacturer.setSemanticId(new Reference("0173-1#02-AAO677#002", KeyElementsEnum.Property));
		info.addSubmodelElement(manufacturer);

		Property gln = new Property();
		gln.setIdShort("gln");
		gln.setValue("GLN-Number ARTI Chasi");
		gln.setSemanticId(new Reference("0173-1#02-AAY812#001", KeyElementsEnum.Property));
		info.addSubmodelElement(gln);

		Property productFamily = new Property();
		productFamily.setIdShort("productFamily");
		productFamily.setValue("research mobile robot");
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

		// add properties for positions in 3D working env
		Property linear_posXProp = new Property();
		linear_posXProp.setIdShort("linear_posX");
		linear_posXProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		linear_posXProp.setDescription("en", "ARTI Chasi robot linear motion X");
		linear_posXProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(linear_posXProp);

		Property linear_posYProp = new Property();
		linear_posYProp.setIdShort("linear_posY");
		linear_posYProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		linear_posYProp.setDescription("en", "ARTI Chasi robot linear motion Y");
		linear_posYProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(linear_posYProp);

		Property linear_posZProp = new Property();
		linear_posZProp.setIdShort("linear_posZ");
		linear_posZProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		linear_posZProp.setDescription("en", "ARTI Chasi robot linear motion Z");
		linear_posZProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(linear_posZProp);

		// add properties for positions in 3D working env
		Property angular_posXProp = new Property();
		angular_posXProp.setIdShort("angular_posX");
		linear_posXProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		angular_posXProp.setDescription("en", "ARTI Chasi robot angular motion X");
		angular_posXProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(angular_posXProp);

		Property angular_posYProp = new Property();
		angular_posYProp.setIdShort("angular_posY");
		angular_posYProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		angular_posYProp.setDescription("en", "ARTI Chasi robot angular motion Y");
		angular_posYProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(angular_posYProp);

		Property angular_posZProp = new Property();
		angular_posZProp.setIdShort("angular_posZ");
		angular_posZProp.setSemanticId(new Reference("0173-1#02-AAZ424#001", KeyElementsEnum.Property)); // e-class-ID "Positionserkennung"
		angular_posZProp.setDescription("en", "ARTI Chasi robot angular motion Z");
		angular_posZProp.setDataSpecification(listSpecificationsPositions);
		info.addSubmodelElement(angular_posZProp);

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

		// add linear position X property
		Supplier<Object> lambdaFunction3 = () -> this.getLinearPositionX();
		properties.put("linear_posX", lambdaFunction3);

		// add linear position Y property
		Supplier<Object> lambdaFunction4 = () -> this.getLinearPositionY();
		properties.put("linear_posY", lambdaFunction4);

		// add linear position Z property
		Supplier<Object> lambdaFunction5 = () -> this.getLinearPositionZ();
		properties.put("linear_posZ", lambdaFunction5);

		// add angular position X property
		Supplier<Object> lambdaFunction6 = () -> this.getAngularPositionX();
		properties.put("angular_posX", lambdaFunction6);

		// add angular position Y property
		Supplier<Object> lambdaFunction7 = () -> this.getAngularPositionY();
		properties.put("angular_posY", lambdaFunction7);

		// add angular position Z property
		Supplier<Object> lambdaFunction8 = () -> this.getAngularPositionZ();
		properties.put("angular_posZ", lambdaFunction8);


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

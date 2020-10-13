package org.srfg.conveyorbelt;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import com.digitalpetri.opcua.stack.core.types.builtin.Variant;
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
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetypedef.PropertyValueTypeDef;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;
import org.srfg.conveyorbelt.opcua.OPCUAManager;


/********************************************************************************************************
 * This class implements the smart manufacturing class for our lab conveyor belt
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ConveyorBelt {

	//public Map<String, ModelUrn> getAllModelURNShortcuts() {return objectIDs;}

	private OPCUAManager opcuaManager;

	private BeltListener listener;
	private final String id;
	private boolean active;

	// writable
	private float movebelt;
	private boolean switchbusylight;

	// readable
	private String beltstate = "";
	private String beltdist = "";
	private boolean beltmoving;


	public ConveyorBelt(String id) {
		this.id = id;
		this.opcuaManager = new OPCUAManager(this);
	}

	/*********************************************************************************************************
	 * movebelt
	 ********************************************************************************************************/
	public float getMoveBelt() {
		return movebelt;
	}
	public void setMoveBelt(float newSpeed) {

		if (this.movebelt != newSpeed)
			opcuaManager.WriteValue(OPCUAManager.WriteLocation.MoveBelt, new Variant(newSpeed));

		this.movebelt = newSpeed;
		if (listener != null) {
			listener.MoveBeltChanged();
		}
	}

	/*********************************************************************************************************
	 * switchbusylight
	 ********************************************************************************************************/
	public boolean getSwitchBusyLight() {
		return switchbusylight;
	}
	public void setSwitchBusyLight(boolean newLightValue) {

		if(this.switchbusylight != newLightValue)
			opcuaManager.WriteValue(OPCUAManager.WriteLocation.Switchlight, new Variant(newLightValue));

		this.switchbusylight = newLightValue;
		if (listener != null) {
			listener.SwitchBusyLightChanged();
		}
	}

	/*********************************************************************************************************
	 * beltstate
	 ********************************************************************************************************/
	public String getBeltState() {
		return beltstate;
	}
	public void setBeltState(String state) {
		this.beltstate = state;
		if (listener != null) {
			listener.ConBeltStateChanged();
		}
	}

	/*********************************************************************************************************
	 * beltdist
	 ********************************************************************************************************/
	public String getBeltDist() {
		return beltdist;
	}
	public void setBeltDist(String dist) {
		this.beltdist = dist;
		if (listener != null) {
			listener.ConBeltDistChanged();
		}
	}

	/*********************************************************************************************************
	 * beltmoving
	 ********************************************************************************************************/
	public boolean getBeltMoving() {
		return beltmoving;
	}
	public void setBeltMoving(boolean moving) {
		this.beltmoving = moving;
		if (listener != null) {
			listener.ConBeltMovingChanged();
		}
	}


	/*********************************************************************************************************
	 * BeltRunner
	 ********************************************************************************************************/
	private class BeltRunner implements Runnable {

		@Override
		public void run() {
			while (active) {
				try {
					Thread.sleep(1000);
					// the speed is the distance per second

					//distanceRun += speed;
					if (listener != null) {
						//listener.distanceChanged();
					}
				} catch (InterruptedException e) {
					//
				}
			}
		}
	}

	/*********************************************************************************************************
	 * start
	 ********************************************************************************************************/
	public void start() {
		if (!isActive()) {

			this.opcuaManager.StartReadThread();
			active = true;

			// init read properties
			this.setBeltState("0");
			this.setBeltDist("0");
			this.setBeltMoving(false);

			new Thread(new BeltRunner()).start();
		}
	}

	/*********************************************************************************************************
	 * stop
	 ********************************************************************************************************/
	public void stop() {
		if (isActive()) {
			this.opcuaManager.StopReadThread();
            active = false;
		}
	}

	/*********************************************************************************************************
	 * active and ID
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
	public String getId() {
		return this.id;

	}

	/*********************************************************************************************************
	 * createAAS
	 ********************************************************************************************************/
	public static IModelProvider createAAS(ConveyorBelt belt) {

		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.setIdentification(IdentifierType.CUSTOM, belt.getId());
		aas.setIdShort(belt.getId());

		SubModel id = createIdentification(belt);
		SubmodelDescriptor idDesc = new SubmodelDescriptor(id);
		aas.addSubModel(idDesc);

		SubModel prop = createProperties(belt);
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
	private static SubModel createIdentification(ConveyorBelt belt) {

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
		gln.set("GLN-Number Coneyor Belt");
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
	private static SubModel createProperties(ConveyorBelt belt) {

		SubModel info = new SubModel();
		info.setIdShort("properties");
		info.setDescription(new LangStrings("de", "Statusinformationen"));
		info.setModelingKind(ModelingKind.INSTANCE);

		// (ReadWriteAccess)
		Property movebelt = new Property();
		movebelt.setIdShort("movebelt");
		movebelt.setDescription(new LangStrings("de", "Förderband bewegen"));
		movebelt.set(VABLambdaProviderHelper.createSimple(	// create the read function for this object
				// Supplier Function (Getter)
				() -> {
					// ROS abfrage
					return belt.getMoveBelt();
				},
				// Consumer Function (Setter)
				(args) -> {
					Object[] params = (Object[]) args;
					if (params.length == 1) {
						belt.setMoveBelt(((Double) params[0]).floatValue());
					}
				})
		);
		movebelt.setValueType(PropertyValueTypeDef.Double);
		info.addSubModelElement(movebelt);


		Property switchbusylight = new Property();
		switchbusylight.setIdShort("switchbusylight");
		switchbusylight.setDescription(new LangStrings("de", "Warnlampenstatus ändern"));
		switchbusylight.set(VABLambdaProviderHelper.createSimple(	// create the read function for this object
				// Supplier Function (Getter)
				() -> {
					// ROS abfrage
					return belt.getSwitchBusyLight();
				},
				// Consumer Function (Setter)
				(args) -> {
					Object[] params = (Object[]) args;
					if (params.length == 1) {
						belt.setSwitchBusyLight(((boolean) params[0]));
					}
				})
		);
		switchbusylight.setValueType(PropertyValueTypeDef.Double);
		info.addSubModelElement(switchbusylight);


		// (ReadOnlyAccess)
		Property beltState = new Property();
		beltState.setIdShort("beltstate");
		beltState.setDescription(new LangStrings("de", "BeltState"));
		beltState.set(VABLambdaProviderHelper.createSimple(belt::getBeltState,null));
		beltState.setValueType(PropertyValueTypeDef.Float);
		info.addSubModelElement(beltState);

		Property beltDist = new Property();
		beltDist.setIdShort("beltdist");
		beltDist.setDescription(new LangStrings("de", "BeltDist"));
		beltDist.set(VABLambdaProviderHelper.createSimple(belt::getBeltDist,null));
		beltDist.setValueType(PropertyValueTypeDef.Float);
		info.addSubModelElement(beltDist);

		Property beltMoving = new Property();
		beltMoving.setIdShort("beltmoving");
		beltMoving.setDescription(new LangStrings("de", "BeltMoving"));
		beltMoving.set(VABLambdaProviderHelper.createSimple(belt::getBeltDist,null));
		beltMoving.setValueType(PropertyValueTypeDef.Boolean);
		info.addSubModelElement(beltMoving);

		return info;
	}

	/*********************************************************************************************************
	 * createModel
	 ********************************************************************************************************/
	public static Map<String, Object> createModel(ConveyorBelt belt) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("id", belt.getId()); // Add the id of the Belt to the model
		properties.put("desc", "Model connected with the edge device");

		// add movebelt property
		Supplier<Object> lambdaFunction = () -> belt.getMoveBelt();
		Map<String, Object> lambdaProperty = VABLambdaProviderHelper.createSimple(lambdaFunction, null);
		properties.put("movebelt", lambdaProperty);

		// add switchbusylight property
		Supplier<Object> lambdaFunction1 = () -> belt.getSwitchBusyLight();
		Map<String, Object> lambdaProperty1 = VABLambdaProviderHelper.createSimple(lambdaFunction1, null);
		properties.put("switchbusylight", lambdaProperty1);

		// add beltstate property
		Supplier<Object> lambdaFunction3 = () -> belt.getBeltState();
		Map<String, Object> lambdaProperty3 = VABLambdaProviderHelper.createSimple(lambdaFunction3, null);
		properties.put("beltstate", lambdaProperty3);

		// add beltdist property
		Supplier<Object> lambdaFunction4 = () -> belt.getBeltDist();
		Map<String, Object> lambdaProperty4 = VABLambdaProviderHelper.createSimple(lambdaFunction4, null);
		properties.put("beltdist", lambdaProperty4);

		// add beltmoving property
		Supplier<Object> lambdaFunction5 = () -> belt.getBeltMoving();
		Map<String, Object> lambdaProperty5 = VABLambdaProviderHelper.createSimple(lambdaFunction5, null);
		properties.put("beltmoving", lambdaProperty5);


		// Create an empty container for custom operations
		Map<String, Object> operations = new HashMap<>();
		Function<Object, Object> activateFunction = (args) -> {
			belt.setActive(true);
			return null;
		};
		operations.put("start", activateFunction);

		// Add a function that deactivates the oven and implements a functional interface
		Function<Object, Object> deactivateFunction = (args) -> {
			belt.setActive(false);
			return null;
		};
		operations.put("stop", deactivateFunction);

		// Add a function
		Function<Object, Object> setSpeedFunction = (args) -> {
			Object[] params = (Object[]) args;
			if (params.length == 1) {
				belt.setMoveBelt(((Double) params[0]).floatValue());
			}
			return null;
		};
		operations.put("setSpeed", setSpeedFunction);

		Function<Object, Object> setLightFunction = (args) -> {
			Object[] params = (Object[]) args;
			if (params.length == 1) {
				belt.setSwitchBusyLight(((boolean) params[0]));
			}
			return null;
		};
		operations.put("setLight", setLightFunction);

		Map<String, Object> myModel = new HashMap<>();
		myModel.put("operations", operations);
		myModel.put("properties", properties);
		return myModel;
	}

	/*********************************************************************************************************
	 * Listener
	 ********************************************************************************************************/
	public BeltListener getListener() {
		return listener;
	}
	public void setListener(BeltListener listener) {
		this.listener = listener;
	}

}

package at.srfg.conveyorbelt;

import at.srfg.iot.common.datamodel.asset.aas.basic.Submodel;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.KeyElementsEnum;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.Kind;
import at.srfg.iot.common.datamodel.asset.aas.common.referencing.Reference;
import at.srfg.iot.common.datamodel.asset.aas.common.types.DataTypeEnum;
import at.srfg.iot.common.datamodel.asset.aas.modeling.submodelelement.Property;
import com.digitalpetri.opcua.stack.core.types.builtin.Variant;
import at.srfg.basedevice.BaseDevice;
import at.srfg.conveyorbelt.opcua.OPCUAManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;



/********************************************************************************************************
 * This class implements the smart manufacturing class for our lab conveyor belt
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ConveyorBelt extends BaseDevice {

	private OPCUAManager opcuaManager;
	private BeltListener listener;
	private boolean active;

	// writable
	private float movebelt;
	private boolean switchbusylight;

	// readable
	private String beltstate = "";
	private String beltdist = "";
	private boolean beltmoving;

	/*********************************************************************************************************
	 * CTOR
	 ********************************************************************************************************/
	public ConveyorBelt() { this.opcuaManager = new OPCUAManager(this); }

	@Override
	public String getName() {return "belt";}

	@Override
	public String getDirectory() {return "/lab/belt/belt01";}

	@Override
	public String getAssetTypeFromResources()
	{
		byte[] bytes = new byte[0];
		try {
			URL resource = ConveyorBelt.class.getClassLoader().getResource("MyAssetType.json");
			bytes = Files.readAllBytes(Paths.get(resource.toURI()));
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (URISyntaxException e) {e.printStackTrace();}

		return new String(bytes);
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

	/*********************************************************************************************************
	 * movebelt
	 ********************************************************************************************************/
	public float getMoveBelt() {
		return movebelt;
	}
	public void setMoveBelt(String direction, float distance) {

		if (this.movebelt != distance)
			opcuaManager.callMethod(OPCUAManager.WriteLocation.MoveBelt, new Variant[]{new Variant(direction), new Variant(distance)});

		this.movebelt = distance;
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
			opcuaManager.callMethod(OPCUAManager.WriteLocation.Switchlight, new Variant[]{new Variant(newLightValue)});

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
	@Override
	public void start() {
		if (!isActive()) {

			this.opcuaManager.startReadThread();
			active = true;

			// init read properties
			this.setBeltState("0");
			this.setBeltDist("0");
			this.setBeltMoving(false);

			new Thread(new BeltRunner()).start();
			// combine the model with the AAS
			getModel().setValueSupplier("properties/beltData/distance",
					// the getter function must return a string
					() -> beltdist) ;
			getModel().setValueSupplier("properties/beltData/serverTime",
					// the getter function must return a string
					() -> "" + System.currentTimeMillis()) ;
			getModel().setValueSupplier("properties/beltData/state",
					// the getter function must return a string
					() -> beltstate) ;
			getModel().setValueSupplier("properties/beltData/moving",
					// the getter function must return a string
					() -> beltmoving ? "true": "false")  ;
			getModel().setFunction("operations/switchBusyLight", new Function<Map<String,Object>, Object>() {
				@Override
				public Object apply(Map<String, Object> t) {
					
					if (!t.containsKey("state")) {
						throw new IllegalStateException("Missing parameter [state]");
					}
					// type check der input parameter
					Object state = t.get("state");
					if ( Boolean.class.isInstance(state)) {
						// 
						switchBusyLight((Boolean) state);
						return String.format("SWITCH light Command passed to ConveyorBelt: state %s", state) ;
					}
					return String.format("SWITCH light Command failed") ;

				}
			});
			getModel().setFunction("operations/moveBelt", new Function<Map<String,Object>, Object>() {
				
				@Override
				public Object apply(Map<String, Object> t) {
					if (!t.containsKey("direction")) {
						throw new IllegalStateException("Missing parameter [direction]");
					}
					if (!t.containsKey("distance")) {
						throw new IllegalStateException("Missing parameter [distance]");
					}
					// type check der input parameter
					// is direction gültig?? 
					
					// 
					Object distance = t.get("distance");
					Object direction = t.get("direction");
					Double d = 0.0d;
					if ( Number.class.isInstance(distance)) {
						// 
						d = Double.valueOf(distance.toString());
						// AN dieser stelle folgt die METHODE welche die OPC-UA ROUTINE aufruft
						moveBelt((String) direction, d.floatValue());
						
						return String.format("MoveBelt Command passed to ConveyorBelt: direction %s distance %s", direction, distance) ;
					}
					return String.format("Command not successful") ;
				}
			});
		}
	}

	/*********************************************************************************************************
	 * stop
	 ********************************************************************************************************/
	@Override
	public void stop() {
		if (isActive()) {
			this.opcuaManager.stopReadThread();
            active = false;
			getModel().setValueSupplier("properties/beltData/distance", null);
			getModel().setValueSupplier("properties/beltData/serverTime", null);
			getModel().setValueSupplier("properties/beltData/state", null);
			getModel().setValueSupplier("properties/beltData/moving", null);
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
	 * createIdentification
	 ********************************************************************************************************/
	@Override
	@Deprecated
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
		gln.setValue("GLN-Number Coneyor Belt");
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
	 * createProperties
	 *
	 * INFO: For lambda properties, the type has to be explicitly specified as it
	 * can not be retrieved from supplier automatically
	 ********************************************************************************************************/
	@Override
	@Deprecated
	protected Submodel createProperties() {

		Submodel info = new Submodel();
		info.setIdShort("properties");
		info.setDescription("de", "Statusinformationen");
		info.setKind(Kind.Instance);

		// (ReadWriteAccess)
		Property movebelt = new Property();
		movebelt.setIdShort("movebelt");
		movebelt.setDescription("de", "Förderband bewegen");
		movebelt.setGetter(() -> { // Supplier Function (Getter)
			return String.valueOf(this.getMoveBelt());
		});
		movebelt.setSetter((args) -> { // Consumer Function (Setter)
			// disabled, because of deprecation of function
			//this.setMoveBelt(Float.parseFloat(args));
		});
		movebelt.setValueQualifier(DataTypeEnum.DECIMAL); // DOUBLE
		info.addSubmodelElement(movebelt);


		Property switchbusylight = new Property();
		switchbusylight.setIdShort("switchbusylight");
		switchbusylight.setDescription("de", "Warnlampenstatus ändern");
		switchbusylight.setGetter(() -> { // Supplier Function (Getter)
			return String.valueOf(this.getSwitchBusyLight());
		});
		switchbusylight.setSetter((args) -> { // Consumer Function (Setter)
			this.setSwitchBusyLight(Boolean.parseBoolean(args));
		});
		switchbusylight.setValueQualifier(DataTypeEnum.DECIMAL); // DOUBLE
		info.addSubmodelElement(switchbusylight);


		// (ReadOnlyAccess)
		Property beltState = new Property();
		beltState.setIdShort("beltstate");
		beltState.setDescription("de", "BeltState");
		beltState.setGetter(this::getBeltState);
		beltState.setValueQualifier(DataTypeEnum.DECIMAL); // FLOAT
		info.addSubmodelElement(beltState);

		Property beltDist = new Property();
		beltDist.setIdShort("beltdist");
		beltDist.setDescription("de", "BeltDist");
		beltDist.setGetter(this::getBeltDist);
		beltDist.setValueQualifier(DataTypeEnum.DECIMAL); // FLOAT
		info.addSubmodelElement(beltDist);

		Property beltMoving = new Property();
		beltMoving.setIdShort("beltmoving");
		beltMoving.setDescription("de", "BeltMoving");
		beltMoving.setGetter(this::getBeltDist);
		beltMoving.setValueQualifier(DataTypeEnum.BOOLEAN);
		info.addSubmodelElement(beltMoving);

		return info;
	}

	/*********************************************************************************************************
	 * createModel
	 ********************************************************************************************************/
	@Override
	@Deprecated
	protected Map<String, Object> createModel() {

		Map<String, Object> properties = new HashMap<>();
		properties.put("id", this.getName() + "01");
		properties.put("desc", "Model connected with the edge device");

		// add movebelt property
		Supplier<Object> lambdaFunction = () -> this.getMoveBelt();
		properties.put("movebelt", lambdaFunction);

		// add switchbusylight property
		Supplier<Object> lambdaFunction1 = () -> this.getSwitchBusyLight();
		properties.put("switchbusylight", lambdaFunction1);

		// add beltstate property
		Supplier<Object> lambdaFunction3 = () -> this.getBeltState();
		properties.put("beltstate", lambdaFunction3);

		// add beltdist property
		Supplier<Object> lambdaFunction4 = () -> this.getBeltDist();
		properties.put("beltdist", lambdaFunction4);

		// add beltmoving property
		Supplier<Object> lambdaFunction5 = () -> this.getBeltMoving();
		properties.put("beltmoving", lambdaFunction5);


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

		// Add a function
		Function<Object, Object> setSpeedFunction = (args) -> {
			Object[] params = (Object[]) args;
			if (params.length == 1) {
				// disabled, because of deprecation of function
				//this.setMoveBelt(((Double) params[0]).floatValue());
			}
			return null;
		};
		operations.put("setSpeed", setSpeedFunction);

		Function<Object, Object> setLightFunction = (args) -> {
			Object[] params = (Object[]) args;
			if (params.length == 1) {
				this.setSwitchBusyLight(((boolean) params[0]));
			}
			return null;
		};
		operations.put("setLight", setLightFunction);

		Map<String, Object> myModel = new HashMap<>();
		myModel.put("operations", operations);
		myModel.put("properties", properties);
		return myModel;
	}
	private void switchBusyLight(boolean newLightValue) {
		if(this.switchbusylight != newLightValue)
			opcuaManager.callMethod(OPCUAManager.WriteLocation.Switchlight, new Variant[]{new Variant(newLightValue)});

		this.switchbusylight = newLightValue;
		if (listener != null) {
			listener.SwitchBusyLightChanged();
		}

	}
	private void moveBelt(String direction, float distance) {

		// FIXME: Direction is always "left"
		opcuaManager.callMethod(OPCUAManager.WriteLocation.MoveBelt, new Variant[]{new Variant(direction), new Variant(distance)});
		if (listener != null) {
			listener.MoveBeltChanged();
		}

	}
}

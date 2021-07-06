package at.srfg.conveyorbelt;

public class OPCUATester {

    public static void main(String[] args) {
        ConveyorBelt belt = new ConveyorBelt();
        belt.setSwitchBusyLight(false);
        belt.setSwitchBusyLight(true);
        belt.setMoveBelt("left", 0.1f);
        belt.setMoveBelt("right", 0.1f);
        belt.setSwitchBusyLight(false);

    }
}

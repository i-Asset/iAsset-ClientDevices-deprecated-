package at.srfg;

public class MyConveyorBelt {
	
	private long serverTime;
	private double distance;
	private boolean moving;
	private String state;
	
	public void switchBusyLight(boolean busy) {
		
	}
	public void moveBelt(String direction, float distance) {
		
	}
	
	public long getServerTime() {
		return serverTime;
	}
	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public boolean isMoving() {
		return moving;
	}
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}

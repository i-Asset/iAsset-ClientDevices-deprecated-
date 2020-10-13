package org.srfg.panda.franka;

/********************************************************************************************************
 * This class represents robot joint state message object as defined by ROS
 * 
 * see docu page:
 * https://docs.ros.org/api/sensor_msgs/html/msg/JointState.html
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class JointState  {
	
	public String[] name;
	public double[] position;
	public double[] velocity;
	public double[] effort;
}
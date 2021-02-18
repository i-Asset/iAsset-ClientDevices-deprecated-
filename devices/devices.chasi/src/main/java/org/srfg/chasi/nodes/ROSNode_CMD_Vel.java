
package org.srfg.chasi.nodes;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;
import org.srfg.chasi.ChasiDevice;


/********************************************************************************************************
 * This class represents a listener node subscribing to required chasi topics
 * 
 * tutorials:
 * http://rosjava.github.io/rosjava_core/latest/getting_started.html
 * https://answers.ros.org/question/313257/rosjava-how-to-start-to-use-rosjava_core-with-maven/
 * http://docs.ros.org/kinetic/api/franka_msgs/html/msg/FrankaState.html
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ROSNode_CMD_Vel extends AbstractNodeMain {

	private static String ROS_TOPIC_CHASI_CMD_VEL = "/cmd_vel"; // msg format = "geometry_msgs.Twist"
	private ChasiDevice m_device;

	public ROSNode_CMD_Vel(ChasiDevice device)
	{
		m_device = device;
	}

	/********************************************************************************************************
	 * getDefaultNodeName
	 ********************************************************************************************************/
	@Override
	public GraphName getDefaultNodeName() {
		 return GraphName.of(ROS_TOPIC_CHASI_CMD_VEL);
	}

	/********************************************************************************************************
	 * onStart
	 ********************************************************************************************************/
	@Override
	public void onStart(final ConnectedNode connectedNode) {
	  
		final Log log = connectedNode.getLog();
	  
		 Subscriber<geometry_msgs.Twist> subscriberFrankaStates =
		 		connectedNode.newSubscriber(connectedNode.getName().toString(), geometry_msgs.Twist._TYPE);

		 subscriberFrankaStates.addMessageListener(new MessageListener<geometry_msgs.Twist>()
		 {
		 	@Override
		 	public void onNewMessage(geometry_msgs.Twist message) {

				double linear_pos_X = message.getLinear().getX();
				double linear_pos_Y = message.getLinear().getY();
				double linear_pos_Z = message.getLinear().getZ();
		 		double angular_pos_X = message.getAngular().getX();
		 		double angular_pos_Y = message.getAngular().getY();
				double angular_pos_Z = message.getAngular().getZ();

				m_device.setLinearPositionX(linear_pos_X);
				m_device.setLinearPositionY(linear_pos_Y);
				m_device.setLinearPositionZ(linear_pos_Z);
		 		m_device.setAngularPositionX(angular_pos_X);
		 		m_device.setAngularPositionY(angular_pos_Y);
		 		m_device.setAngularPositionZ(angular_pos_Z);

		 		//log.info("robot mode is: " + robot_mode);
		 		//log.info("effector position for xyz is: " + effector_pos_X + "," + effector_pos_Y + "," + effector_pos_Z);
		 		//log.info("force on z-axis is: " + z_force);
		 	}
		 });
	}

	
	/********************************************************************************************************
	 * TODO
	 ********************************************************************************************************/
	@Override
	public void onError(Node arg0, Throwable arg1) {

		if(arg0 != null) {
			arg0.getLog().info("Error happened!");
		}
	}

	@Override
	public void onShutdown(Node arg0) {

		if(arg0 != null) {
			arg0.getLog().info("Shutdown happened!");
		}
	}

	@Override
	public void onShutdownComplete(Node arg0) {

		if(arg0 != null) {
			arg0.getLog().info("Shutdown Complete happened!");
		}
	}
}
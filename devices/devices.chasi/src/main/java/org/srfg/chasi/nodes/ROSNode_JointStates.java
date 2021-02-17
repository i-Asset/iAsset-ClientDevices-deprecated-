
package org.srfg.chasi.nodes;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;
import org.srfg.chasi.ChasiDevice;


/********************************************************************************************************
 * This class represents a listener node subscribing to required chasi topics
 * 
 * tutorials:
 * http://rosjava.github.io/rosjava_core/latest/getting_started.html
 * https://answers.ros.org/question/313257/rosjava-how-to-start-to-use-rosjava_core-with-maven/
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ROSNode_JointStates extends AbstractNodeMain {

	private static String ROS_TOPIC_JOINT_STATES = "/joint_states";
	private ChasiDevice m_device;

	public ROSNode_JointStates(ChasiDevice device)
	{
		m_device = device;
	}

	/********************************************************************************************************
	 * getDefaultNodeName
	 ********************************************************************************************************/
	@Override
	public GraphName getDefaultNodeName() {
		 return GraphName.of(ROS_TOPIC_JOINT_STATES);
	}

	/********************************************************************************************************
	 * onStart
	 ********************************************************************************************************/
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		final Log log = connectedNode.getLog();

		//Subscriber<sensor_msgs.JointState> subscriberJointStates =
		//		connectedNode.newSubscriber(connectedNode.getName().toString(), sensor_msgs.JointState._TYPE);
		//subscriberJointStates.addMessageListener(new MessageListener<sensor_msgs.JointState>()
		//{
		//	@Override
		//	public void onNewMessage(sensor_msgs.JointState message) {
 		//
		//		log.info(message.getHeader().getSeq()); // log message package seq number
		//
		//		double gripper_distance = message.getPosition()[7] + message.getPosition()[8];	// jointState.position[8] + jointState.position[9]
		//		m_device.setGripperDistance(gripper_distance);
		//
		//		log.info("gripper distance is: " + gripper_distance);
		//	}
		//});
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
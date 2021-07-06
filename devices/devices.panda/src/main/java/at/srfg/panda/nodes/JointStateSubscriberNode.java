
package at.srfg.panda.nodes;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import at.srfg.panda.PandaDevice;


/********************************************************************************************************
 * This class represents a listener node subscribing to required panda topics
 *
 * tutorials:
 * http://rosjava.github.io/rosjava_core/latest/getting_started.html
 * https://answers.ros.org/question/313257/rosjava-how-to-start-to-use-rosjava_core-with-maven/
 *
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class JointStateSubscriberNode extends AbstractNodeMain {

    private static final String ROS_TOPIC_JOINT_STATES = "/joint_states";
    private final PandaDevice m_device;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public JointStateSubscriberNode(PandaDevice device) {
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

        Subscriber<sensor_msgs.JointState> subscriberJointStates =
                connectedNode.newSubscriber(connectedNode.getName().toString(), sensor_msgs.JointState._TYPE);
        subscriberJointStates.addMessageListener(new MessageListener<sensor_msgs.JointState>() {
            @Override
            public void onNewMessage(sensor_msgs.JointState message) {

                logger.debug("Retrieved Message (Seq: {})", message.getHeader().getSeq()); // log message package seq number

                // message.position[]:
                //  [panda_joint1,
                //  panda_joint2, panda_joint3, panda_joint4, panda_joint5, panda_joint6,
                // panda_joint7, panda_finger_joint1, panda_finger_joint2]
                double gripper_distance = message.getPosition()[7] + message.getPosition()[8];    // jointState.position[8] + jointState.position[9]
                m_device.setGripperDistance(gripper_distance);
                logger.info("New gripper distance value: {} ", gripper_distance);
            }
        });
    }

    /********************************************************************************************************
     * onError
     ********************************************************************************************************/
    @Override
    public void onError(Node arg0, Throwable arg1) {
        if (arg0 != null) {
            logger.error("Error in node {}", arg0);
            logger.error("Error", arg1);
        }
    }

    /********************************************************************************************************
     * onShutdown
     ********************************************************************************************************/
    @Override
    public void onShutdown(Node arg0) {
        if (arg0 != null) {
            logger.info("Shutdown node {}", arg0.getName());
        }
    }

    /********************************************************************************************************
     * onShutdownComplete
     ********************************************************************************************************/
    @Override
    public void onShutdownComplete(Node arg0) {
        if (arg0 != null) {
            logger.info("Complete Shutdown in node {}", arg0.getName());
        }
    }
}
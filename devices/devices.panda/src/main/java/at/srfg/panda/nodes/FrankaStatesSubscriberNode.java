
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
 * http://docs.ros.org/kinetic/api/franka_msgs/html/msg/FrankaState.html
 *
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class FrankaStatesSubscriberNode extends AbstractNodeMain {

    private static final String ROS_TOPIC_FRANKA_STATES = "/franka_state_controller/franka_states";
    private final PandaDevice m_device;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public FrankaStatesSubscriberNode(PandaDevice device) {
        m_device = device;
    }

    /********************************************************************************************************
     * getDefaultNodeName
     ********************************************************************************************************/
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(ROS_TOPIC_FRANKA_STATES);
    }

    /********************************************************************************************************
     * onStart
     ********************************************************************************************************/
    @Override
    public void onStart(final ConnectedNode connectedNode) {

        Subscriber<franka_msgs.FrankaState> subscriberFrankaStates =
                connectedNode.newSubscriber(connectedNode.getName().toString(), franka_msgs.FrankaState._TYPE);

        subscriberFrankaStates.addMessageListener(new MessageListener<franka_msgs.FrankaState>() {
            @Override
            public void onNewMessage(franka_msgs.FrankaState message) {

                logger.debug("Retrieved Message (Seq: {})", message.getHeader().getSeq()); // log message package seq number

                byte robot_mode = message.getRobotMode();        // frankaState.robot_mode
                double effector_pos_X = message.getOTEE()[12];    // frankaState.O_T_EE[12]
                double effector_pos_Y = message.getOTEE()[13];    // frankaState.O_T_EE[13]
                double effector_pos_Z = message.getOTEE()[14];    // frankaState.O_T_EE[14]
                double z_force = message.getOFExtHatK()[3];        // frankaState.O_F_ext_hat_K[3]

                m_device.setRobotMode(robot_mode);
                m_device.setPositionX(effector_pos_X);
                m_device.setPositionY(effector_pos_Y);
                m_device.setPositionZ(effector_pos_Z);
                m_device.setForceZ(z_force);

                logger.info("robot mode is: " + robot_mode);
                logger.info("effector position for xyz is: " + effector_pos_X + "," + effector_pos_Y + "," + effector_pos_Z);
                logger.info("force on z-axis is: " + z_force);
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
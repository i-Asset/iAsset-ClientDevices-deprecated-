package at.srfg.panda.nodes;

import franka_msgs.FrankaState;
import org.junit.Test;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.topic.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sensor_msgs.JointState;

import java.net.URI;

public class ROSTest {
    private static final Logger logger = LoggerFactory.getLogger(ROSTest.class);

    @Test
    public void rosCoreConnectionTest() {
        URI masteruri = URI.create("http://il041:11311");
        NodeConfiguration subNodeConfiguration = NodeConfiguration.newPrivate(masteruri);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        MyTestNode testnode = new MyTestNode();
        nodeMainExecutor.execute(testnode, subNodeConfiguration);
        try {
            logger.info("listening for messages (5 seconds...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.info("stopping system");
        }

    }

    public static class MyTestNode extends AbstractNodeMain {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public GraphName getDefaultNodeName() {
            return GraphName.of("rosjava/testnode");
        }

        public void onStart(ConnectedNode connectedNode) {
            Subscriber<FrankaState> frankaSub = connectedNode.newSubscriber("/franka_state_controller/franka_states", FrankaState._TYPE);
            frankaSub.addMessageListener(message -> logger.info("/franka_state_controller/franka_states: {}", message.getOTEE()));

            Subscriber<JointState> jointSub = connectedNode.newSubscriber("/joint_states", JointState._TYPE);
            jointSub.addMessageListener(message -> logger.info("/joint_states: {}", message.getName()));
        }

    }
}

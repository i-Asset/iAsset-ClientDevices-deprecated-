package at.srfg.panda.nodes;

import franka_msgs.FrankaState;
import org.junit.Test;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.topic.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class ROSTest {
    private static final Logger logger = LoggerFactory.getLogger(ROSTest.class);

    @Test
    public void rosCoreConnectionTest() {
        URI masteruri = URI.create("http://192.168.48.41:11311");
        NodeConfiguration subNodeConfiguration = NodeConfiguration.newPrivate(masteruri);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        MyTestNode testnode = new MyTestNode();
        nodeMainExecutor.execute(testnode, subNodeConfiguration);
        try {
            logger.info("waiting 10 seconds for requests...");
            Thread.sleep(10);
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
            final URI uri = connectedNode.lookupServiceUri("/franka_state_controller/franka_states");
            logger.info("{}", uri);
            logger.info("{}", connectedNode.getParameterTree());
            Subscriber<FrankaState> subscriber = connectedNode.newSubscriber("/franka_state_controller/franka_states", FrankaState._TYPE);
            subscriber.addMessageListener(new MessageListener<FrankaState>() {
                @Override
                public void onNewMessage(FrankaState message) {
                    logger.info("I heard: {}", message);
                }
            });
        }

    }
}

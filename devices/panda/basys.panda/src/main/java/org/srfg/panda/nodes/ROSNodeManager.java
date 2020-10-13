package org.srfg.panda.nodes;

import com.google.common.base.Preconditions;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.srfg.panda.PandaDevice;

import java.net.URI;

// This class will run a publisher and subscriber, and relay data between them.
/********************************************************************************************************
 * This class implements manager of all used ROS nodes
 *
 * The manager will init all ROS subscriber and publisher nodes
 * rosjava messages used; converted franka messages used
 *
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ROSNodeManager {

    // ROS Nodes listening to messages from their topics
    private ROSNode_FrankaStates nodeFrankaStates;
    private ROSNode_JointStates nodeJointStates;
    private NodeMainExecutor nodeMainExecutor;

    public ROSNodeManager(PandaDevice device)
    {
        nodeFrankaStates = new ROSNode_FrankaStates(device);
        nodeJointStates = new ROSNode_JointStates(device);
    }

    public void startROSNodes()
    {
        // export ROS_MASTER_URI=http://192.168.48.41:11311
        URI masteruri = URI.create("http://192.168.48.41:11311");

        //NodeConfiguration pubNodeConfiguration = NodeConfiguration.newPublic(host, masteruri); // Load the publisher(talker)
        //Preconditions.checkState(pubNodeMain != null); //Check if Talker class correctly instantiated
        //nodeMainExecutor.execute(nodeFrankaStates, pubNodeConfiguration); //execute the nodelet talker (this will run the method onStart of Talker.java)

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        //NodeConfiguration subNodeConfiguration = NodeConfiguration.newPublic("192.168.48.41", masteruri); // Load the subscriber(listener)
        NodeConfiguration subNodeConfiguration = NodeConfiguration.newPrivate(masteruri);

        Preconditions.checkState(nodeFrankaStates != null);
        nodeMainExecutor.execute(nodeFrankaStates, subNodeConfiguration);

        Preconditions.checkState(nodeJointStates != null);
        nodeMainExecutor.execute(nodeJointStates, subNodeConfiguration);
    }

    public void shutdownROSNodes()
    {
        nodeMainExecutor.shutdown();
    }
}
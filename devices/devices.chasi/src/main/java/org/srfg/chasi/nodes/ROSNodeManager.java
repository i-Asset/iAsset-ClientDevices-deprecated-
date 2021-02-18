package org.srfg.chasi.nodes;

import com.google.common.base.Preconditions;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.srfg.chasi.ChasiDevice;

import java.net.URI;

// This class will run a publisher and subscriber, and relay data between them.
/********************************************************************************************************
 * This class implements manager of all used ROS nodes
 * The manager will init all ROS subscriber and publisher nodes. rosjava messages used;
 *
 * Connect with ARTI Chasi:
 * Step 1) make sure corresponding ROS environment variables are set
 *         -> export ROS_IP=x.x.x.x (your automatic IP)
 *         -> export ROS_MASTER_URI=http://192.168.5.3:11311/
 * Step 2) Connect with ARTI Chasi WLAN Router
 * Step 3) Create SSH tunnel to raspberry pi
 * Step 4) in cmd line: "roslaunch arti_chasi_mark3 arti_chasi_mark3_upstart_with_teleop.launch"
 *
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ROSNodeManager {

    // ROS Nodes listening to messages from their topics
    private ROSNode_CMD_Vel nodeChasiMotion;
    private NodeMainExecutor nodeMainExecutor;

    public ROSNodeManager(ChasiDevice device)
    {
        nodeChasiMotion = new ROSNode_CMD_Vel(device);
    }

    public void startROSNodes()
    {
        URI masteruri = URI.create("http://192.168.5.3:11311"); // Laptop-IP: 192.168.5.5

        //NodeConfiguration pubNodeConfiguration = NodeConfiguration.newPublic(host, masteruri); // Load the publisher(talker)
        //Preconditions.checkState(pubNodeMain != null); //Check if Talker class correctly instantiated
        //nodeMainExecutor.execute(nodeFrankaStates, pubNodeConfiguration); //execute the nodelet talker (this will run the method onStart of Talker.java)

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        //NodeConfiguration subNodeConfiguration = NodeConfiguration.newPublic("192.168.5.3", masteruri); // Load the subscriber(listener)
        NodeConfiguration subNodeConfiguration = NodeConfiguration.newPrivate(masteruri);

        Preconditions.checkState(nodeChasiMotion != null);
        nodeMainExecutor.execute(nodeChasiMotion, subNodeConfiguration);
    }

    public void shutdownROSNodes()
    {
        nodeMainExecutor.shutdown();
    }
}
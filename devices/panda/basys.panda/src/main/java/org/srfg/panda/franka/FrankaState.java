
package org.srfg.panda.franka;

/********************************************************************************************************
 * This class represents robot franka panda state object as defined by franka lib
 * 
 * see wiki page:
 * https://secure.salzburgresearch.at/wiki/pages/viewpage.action?pageId=31000016
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class FrankaState  {
	
	/*********************************************************************************************************
	 * all static franca panda lib members
	 * std_msgs/Header header
	 ********************************************************************************************************/
	
	// float64 values and arrays
	// representing all properties
	public double[] cartesian_collision = new double[6];
	public double[] cartesian_contact = new double[6];
	public double[] q = new double[7];
	public double[] q_d = new double[7];
	public double[] dq = new double[7];
	public double[] dq_d = new double[7];
	public double[] theta = new double[7];
	public double[] dtheta = new double[7];
	public double[] tau_J = new double[7];
	public double[] dtau_J = new double[7];
	public double[] tau_J_d = new double[7];
	public double[] K_F_ext_hat_K = new double[6];
	public double[] elbow = new double[2];
	public double[] elbow_d = new double[2];
	public double[] joint_collision = new double[7];
	public double[] joint_contact = new double[7];
	public double[] O_F_ext_hat_K = new double[6];
	public double[] tau_ext_hat_filtered = new double[7];
	public double m_ee;
	public double[] F_x_Cee = new double[3];
	public double[] I_ee = new double[9];
	public double m_load;
	public double[] F_x_Cload = new double[3];
	public double[] I_load = new double[9];
	public double m_total;
	public double[] F_x_Ctotal = new double[3];
	public double[] I_total = new double[9];
	public double[] O_T_EE = new double[16];
	public double[] O_T_EE_d = new double[16];
	public double[] F_T_EE = new double[16];
	public double[] EE_T_K = new double[16];
	public double time;
	public FrankaErrors current_errors;
	public FrankaErrors last_motion_errors;
	
	//constants for robot modes
	// actually are uint8 values -> java byte -> java int
	public final int ROBOT_MODE_OTHER=0;
	public final int ROBOT_MODE_IDLE=1;
	public final int ROBOT_MODE_MOVE=2;
	public final int ROBOT_MODE_GUIDING=3;
	public final int ROBOT_MODE_REFLEX=4;
	public final int ROBOT_MODE_USER_STOPPED=5;
	public final int ROBOT_MODE_AUTOMATIC_ERROR_RECOVERY=6;
	public int robot_mode = 1; // set robot mode to idle state
}
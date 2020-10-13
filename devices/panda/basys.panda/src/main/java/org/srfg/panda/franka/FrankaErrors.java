
package org.srfg.panda.franka;

/********************************************************************************************************
 * This class represents a franka panda error object as defined in franca lib
 * 
 * see wiki page: 
 * https://secure.salzburgresearch.at/wiki/pages/viewpage.action?pageId=31000016
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class FrankaErrors  {
	
	public boolean joint_position_limits_violation;
	public boolean cartesian_position_limits_violation;
	public boolean self_collision_avoidance_violation;
	public boolean joint_velocity_violation;
	public boolean cartesian_velocity_violation;
	public boolean force_control_safety_violation;
	public boolean joint_reflex;
	public boolean cartesian_reflex;
	public boolean max_goal_pose_deviation_violation;
	public boolean max_path_pose_deviation_violation;
	public boolean cartesian_velocity_profile_safety_violation;
	public boolean joint_position_motion_generator_start_pose_invalid;
	public boolean joint_motion_generator_position_limits_violation;
	public boolean joint_motion_generator_velocity_limits_violation;
	public boolean joint_motion_generator_velocity_discontinuity;
	public boolean joint_motion_generator_acceleration_discontinuity;
	public boolean cartesian_position_motion_generator_start_pose_invalid;
	public boolean cartesian_motion_generator_elbow_limit_violation;
	public boolean cartesian_motion_generator_velocity_limits_violation;
	public boolean cartesian_motion_generator_velocity_discontinuity;
	public boolean cartesian_motion_generator_acceleration_discontinuity;
	public boolean cartesian_motion_generator_elbow_sign_inconsistent;
	public boolean cartesian_motion_generator_start_elbow_invalid;
	public boolean cartesian_motion_generator_joint_position_limits_violation;
	public boolean cartesian_motion_generator_joint_velocity_limits_violation;
	public boolean cartesian_motion_generator_joint_velocity_discontinuity;
	public boolean cartesian_motion_generator_joint_acceleration_discontinuity;
	public boolean cartesian_position_motion_generator_invalid_frame;
	public boolean force_controller_desired_force_tolerance_violation;
	public boolean controller_torque_discontinuity;
	public boolean start_elbow_sign_inconsistent;
	public boolean communication_constraints_violation;
	public boolean power_limit_violation;
	public boolean joint_p2p_insufficient_torque_for_planning;
	public boolean tau_j_range_violation;
	public boolean instability_detected;
}
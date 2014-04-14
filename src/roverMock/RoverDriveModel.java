package roverMock;

import objects.DecimalPoint;
import objects.ThreadTimer;
import simulatorWrapper.WrapperEvents;

public class RoverDriveModel {

	private int time_step = 100;
	
	private int[] MotorPowers = new int[] { 150, 150, 150, 150 };
	private int[] MotorStates = new int[] { 0, 0, 0, 0 };
	public static final int FORWARD = 1, BACKWARD = -1, RELEASE = 0;
	public static final int FL = 0, BL = 1, BR = 2, FR = 3;
	
	private double axel_width = 40;
	private double wheel_radius = 6;
	private double rover_width = 40;
	private double rover_length = 40;
	private double motor_arm = Math.sqrt(Math.pow(rover_width, 2) + Math.pow(rover_length, 2)) / 2.0;
	private double mass = 1.2;
	private double rover_inertia = .7;
	private double wheel_inertia = .3;
	
	private double motor_energy_transform = 5;
	private double motor_resistance = 5000;
	private double motor_inductance = .00001;
	private double friction_gr = 0.3;
	private double friction_s = 0.9;
	private double gamma = Math.atan(1/rover_width);
	private double grav_accel = 9.81; //mars = 3.72

	private double battery_voltage = 12;
	private double battery_capacitance = .0005;
	private double battery_charge = 90;
	
	private double x, y;
	private double angle;
	private double velocity = 0;
	private double angular_velocity = 0;
	private double acceleration = 0;
	private double angular_acceleration = 0;
	
	private double wheel_speed_FL = 0;
	private double wheel_speed_FR = 0;
	private double wheel_speed_BL = 0;
	private double wheel_speed_BR = 0;
	
	public RoverDriveModel(){
		x = WrapperEvents.getRoverLocation().getX();
		y = WrapperEvents.getRoverLocation().getY();
		angle = WrapperEvents.getRoverDirection();
		new ThreadTimer(0, new Runnable() {
			public void run() {
				drive();
				try {
					Thread.sleep(time_step);
				} catch (Exception e) {
					Thread.currentThread().interrupt();
				}
			}
		}, ThreadTimer.FOREVER);
	}
	
	private void drive(){
		// translational friction, approximately the same for all wheels
		double fric_all = friction_gr * motor_arm * angular_velocity;
		// Slip forces on wheels, based on speed differences
		double slip_FL = friction_s * ((wheel_speed_FL*wheel_radius) - velocity);
		double slip_FR = friction_s * ((wheel_speed_FR*wheel_radius) - velocity);
		double slip_BL = friction_s * ((wheel_speed_BL*wheel_radius) - velocity);
		double slip_BR = friction_s * ((wheel_speed_BR*wheel_radius) - velocity);		
		// Motor Currents, based on voltage		
		double current_FL = (MotorPowers[FL] / 255 * battery_voltage) / motor_resistance;
		double current_FR = (MotorPowers[FR] / 255 * battery_voltage) / motor_resistance;
		double current_BL = (MotorPowers[BL] / 255 * battery_voltage) / motor_resistance;
		double current_BR = (MotorPowers[BR] / 255 * battery_voltage) / motor_resistance;
		// angular motor speeds, based on torques
		wheel_speed_FL += 1/wheel_inertia * ( motor_energy_transform*current_FL - wheel_radius*slip_FL + wheel_radius*fric_all*Math.cos(gamma));
		wheel_speed_FR += 1/wheel_inertia * ( motor_energy_transform*current_FR - wheel_radius*slip_FR + wheel_radius*fric_all*Math.cos(gamma));
		wheel_speed_BL += 1/wheel_inertia * ( motor_energy_transform*current_BL - wheel_radius*slip_BL + wheel_radius*fric_all*Math.cos(gamma));
		wheel_speed_BR += 1/wheel_inertia * ( motor_energy_transform*current_BR - wheel_radius*slip_BR + wheel_radius*fric_all*Math.cos(gamma));
		// Acceleration changes based on forces
		acceleration = 1/mass*(slip_FL + slip_BL + slip_FR + slip_BR) - grav_accel*Math.sin(WrapperEvents.getIncline());
						//TODO check publishing error
		angular_acceleration = 1/rover_inertia * (motor_arm*(slip_FL + slip_BL - slip_FR - slip_BR)*Math.cos(gamma) - motor_arm*(fric_all - 3*fric_all));
		// Speed changes based on Acceleration
		angular_velocity += angular_acceleration * time_step;
		velocity += acceleration * time_step;
		// Position changes based on velocities
		angle += angular_velocity * time_step;
		x += velocity * Math.cos(angle) * time_step;
		y += velocity * Math.sin(angle) * time_step;
		// report new location to map
		WrapperEvents.setRoverConditions(new DecimalPoint(x, y), angle);
		
		//TODO charge motor charge based on draw
		
		// simple code
		/*double dist_left = 0;
		double dist_right = 0;
		if (Math.abs(adjustForIncline(
				getMotorSpeed(MotorPowers[0] * MotorStates[0], motorVoltage), 0)) > Math
				.abs(adjustForIncline(
						getMotorSpeed(MotorPowers[1] * MotorStates[1],
								motorVoltage), 0))) {
			dist_left = adjustForIncline(
					getMotorSpeed(MotorPowers[0] * MotorStates[0], motorVoltage),
					0)
					* wheel_radius * time_step / 1000.0;
		} else {
			dist_left = adjustForIncline(
					getMotorSpeed(MotorPowers[1] * MotorStates[1], motorVoltage),
					0)
					* wheel_radius * time_step / 1000.0;
		}
		if (Math.abs(adjustForIncline(
				getMotorSpeed(MotorPowers[2] * MotorStates[2], motorVoltage), 0)) > Math
				.abs(adjustForIncline(
						getMotorSpeed(MotorPowers[3] * MotorStates[3],
								motorVoltage), 0))) {
			dist_right = adjustForIncline(
					getMotorSpeed(MotorPowers[2] * MotorStates[2], motorVoltage),
					0)
					* wheel_radius * time_step / 1000.0;
		} else {
			dist_right = adjustForIncline(
					getMotorSpeed(MotorPowers[3] * MotorStates[3], motorVoltage),
					0)
					* wheel_radius * time_step / 1000.0;
		}
		double distance = (dist_left + dist_right) / 2.0;
		double angle = Math.atan((dist_right - dist_left) / axel_width);
		WrapperEvents.moveRover(distance, angle);*/
	}
	
	public void setMotorPower(int which, int power){
		if (power >= 0 && power <= 255){
			MotorPowers[which] = power;
		}
	}
	
	public void setMotorState(int which, int state){
		if (Math.abs(state) <= 1){
			MotorStates[which] = state;
		}
	}
}
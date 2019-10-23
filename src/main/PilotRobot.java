package main;
import lejos.hardware.Battery;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;

import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;

/**
 * Robot controller class
 * Initialises sensors and the pilot.
 */
public class PilotRobot {
	private Brick ev3;
	private EV3TouchSensor leftBumper, rightBumper;
	private EV3UltrasonicSensor ultrasonicSensor;
	// private NXTRegulatedMotor ultrasonicSensorMotor;
	private EV3GyroSensor gyroSensor;
	private SampleProvider leftBumperSampleProvider, rightBumperSampleProvider, ultrasonicSampleProvider, gyroSampleProvider;
	private float[] leftBumperSample, rightBumperSample, ultrasonicSample, gyroSample;
	private MovePilot pilot;
	private OdometryPoseProvider opp;
	private boolean scanRequired = true;

	// SmartRobot constructor
	public PilotRobot() {
		ev3 = BrickFinder.getDefault();
//		setupGyroSensor();
//		setupTouchSensor();
//		setupUltrasonicSensor();
		setupPilot();
		setupOdometryPoseProvider();
	}
	
	/**
	 * Set up the gyro sensor.
	 */
	private void setupGyroSensor() {
		gyroSensor = new EV3GyroSensor(ev3.getPort("S3"));
		gyroSampleProvider = gyroSensor.getAngleMode();
		gyroSample = new float[gyroSampleProvider.sampleSize()];
	}

	/**
	 * Set up the touch sensors.
	 */
	private void setupTouchSensor() {
		leftBumper = new EV3TouchSensor(ev3.getPort("S1"));
		rightBumper = new EV3TouchSensor(ev3.getPort("S4"));
		leftBumperSampleProvider = leftBumper.getTouchMode();
		rightBumperSampleProvider = rightBumper.getTouchMode();
		leftBumperSample = new float[leftBumperSampleProvider.sampleSize()];
		rightBumperSample = new float[rightBumperSampleProvider.sampleSize()];
	}

	/**
	 * Set up the ultrasonic sensor.
	 */
	private void setupUltrasonicSensor() {
		ultrasonicSensor = new EV3UltrasonicSensor(ev3.getPort("S2"));
		ultrasonicSampleProvider = ultrasonicSensor.getDistanceMode();
		ultrasonicSample = new float[ultrasonicSampleProvider.sampleSize()];
		// ultrasonicSensorMotor = new NXTRegulatedMotor(ev3.getPort("C"));
	}
	
	public EV3UltrasonicSensor getUltrasonicSensor() {
	  return ultrasonicSensor;
	}

	/**
	 * Set up the pilot.
	 */
	private void setupPilot() {
		Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, 4.05).offset(-4.9);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.D, 4.05).offset(4.9);
		Chassis myChassis = new WheeledChassis(new Wheel[] { leftWheel, rightWheel }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new MovePilot(myChassis);
		pilot.setLinearAcceleration(5);
		pilot.setLinearSpeed(25);
		pilot.setAngularAcceleration(45);
		pilot.setAngularSpeed(90);
	}
	
	/**
	 * Set up the odometry pose provider.
	 */
	private void setupOdometryPoseProvider() {
		opp = new OdometryPoseProvider(pilot);
		opp.setPose(new Pose(0, 0, 0));
	}

	// whether the left bumper is pressed
	public final boolean isLeftBumperPressed() {
		leftBumperSampleProvider.fetchSample(leftBumperSample, 0);
		return (leftBumperSample[0] == 1.0);
	}

	// whether the right bumper is pressed
	public final boolean isRightBumperPressed() {
		rightBumperSampleProvider.fetchSample(rightBumperSample, 0);
		return (rightBumperSample[0] == 1.0);
	}

	// get the distance from the ultrasonic sensor
	public final float getDistance() {
		ultrasonicSampleProvider.fetchSample(ultrasonicSample, 0);
		return ultrasonicSample[0] * 100;
	}

	// get the pilot object from the robot
	public final MovePilot getPilot() {
		return this.pilot;
	}
	
	// get the odometry pose provider
	public final OdometryPoseProvider getOdometryPoseProvider() {
		return opp;
	}

	// get the robots current angle
	public final float getAngle() {
		gyroSampleProvider.fetchSample(gyroSample, 0);
		return gyroSample[0];
	}

	// get the robots current battery voltage
	public final float getBatteryVoltage() {
		return Battery.getVoltage();
	}

	// close the bumpers and ultrasonic sensor
	public final void closeRobot() {
		leftBumper.close();
		rightBumper.close();
		ultrasonicSensor.close();
	}
	
	public final boolean isScanRequired() {
		return scanRequired;
	}
	
	public final void setScanRequired(boolean scanRequired) {
		this.scanRequired = scanRequired;
	}
	
	public final void rotateSensorLeft() {
		Motor.C.rotateTo(90);
	}
	
	public final void rotateSensorRight() {
		Motor.C.rotateTo(-90);
	}
	
	public final void rotateSensorCentre() {
		Motor.C.rotateTo(0);
	}
}

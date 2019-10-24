package monitors;
import main.Cell;
import main.Grid;
import main.PilotRobot;

import java.io.*;
import java.net.*;

// Used to send data about the robot to a PC client interface.
public class PCMonitor extends Thread {

	//Server socket between robot and client
	private Socket client;

	//checks if thread is running.
	private volatile boolean running = true;

	//Data output stream
	private PrintWriter out;

	//The actual robot.
	private PilotRobot robot;

	private Grid grid;

	public PCMonitor(Socket client, PilotRobot robot, Grid grid) {
		this.client = client;
		this.robot = robot;
		this.grid = grid;
		
		try {
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
			running = false;
		}
	}

	//run the thread
	public void run() {
		while (running) {


			/*output data for
			 * Battery,
			 * ultrasound sensor,
			 * gyroscope,
			 * and motor status
			 */
			out.println("Battery: " + robot.getBatteryVoltage());
			out.println("Sonar distance: " + robot.getDistance());
			out.println("Gyro angle: " + robot.getAngle());
			if (robot.getPilot().isMoving()) {
				out.println("Motor status: " + "Moving");
			} else {
				out.println("Motor status: " + "Stationary");
			}
			out.println("  type: " + robot.getPilot().getMovement().getMoveType());
			
			// output probability data and current cell
			String probabilityData = "";
			for (Cell cell : grid.getGrid()) {
				probabilityData += cell.getOccupancyProbability() + ",";
			}
			out.println(probabilityData);
			out.println(grid.getCurrentCell().getX() + "," + grid.getCurrentCell().getY());
			out.flush();
			
			try {
				sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}
	
	public final void terminate() {
		running = false;
	}
}

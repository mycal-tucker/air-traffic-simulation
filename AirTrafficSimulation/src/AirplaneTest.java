//import static org.junit.Assert.*;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//
//public class AirplaneTest {
//	public void testConstructor() {
//		double [] pose = {1, 2, 0};
//		double dx = 5, dy = 0, dt = 0;
//		double s = 5;
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, s, dt, sim, fuel);
//		double [] newPose = plane.getPosition();
//		assertEquals(pose[0], newPose[0], 1e-6);
//		assertEquals(pose[1], newPose[1], 1e-6);
//		assertEquals(pose[2], newPose[2], 1e-6);
//
//		double [] newVel = plane.getVelocity();
//		assertEquals(dx, newVel[0], 1e-6);
//		assertEquals(dy, newVel[1], 1e-6);
//		assertEquals(dt, newVel[2], 1e-6);
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testTooManyArgumentsInConstructor() {
//		// Too many arguments in pose constructor 
//		double [] pose = {0, 0, 0, 0};
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		new Airplane(pose, 0, 0, sim, fuel);
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testTooFewArgumentsInConstructor() {
//		// Too few arguments in pose constructor 
//		double [] pose = {0};
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		new Airplane(pose, 0, 0, sim, fuel);
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testTooManyArgumentsSetPosition() {
//		// Too many arguments in setPosition 
//		double [] pose = {0, 0, 0};
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, 0, 0, sim, fuel);
//		double [] newPose = {0, 0, 0, 0};
//		plane.setPosition(newPose);
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testTooFewArgumentsSetPosition() {
//		// Too few arguments in setPosition 
//		double [] pose = {0, 0, 0};
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, 0, 0, sim, fuel);
//		double [] newPose = {0};
//		plane.setPosition(newPose);
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testTooManyArgumentsSetVelocity() {
//		// Too many arguments in setVelocity 
//		double [] pose = {0, 0, 0};
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, 0, 0, sim, fuel);
//		double [] newVel = {0, 0, 0, 0};
//		plane.setVelocity(newVel);
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testTooFewArgumentsSetVelocity() {
//		// Too few arguments in setVelocity
//		double [] pose = {0, 0, 0};
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, 0, 0, sim, fuel);
//		double [] newVel = {0};
//		plane.setVelocity(newVel);
//	}
//
//	// Test get/set Position/Velocity at all legal position bounds
//
//	@SuppressWarnings("deprecation")
//	@Test
//	public void testGetSetPositionValid() {
//		double [] pose = {1, 2, 0};
//		double dx = 5, dy = 0, dt = 0;
//		double s = 5;
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, s, dt, sim, fuel);
//		double [] newPose = plane.getPosition();
//		assertEquals(pose[0], newPose[0], 1e-6);
//		assertEquals(pose[1], newPose[1], 1e-6);
//		assertEquals(pose[2], newPose[2], 1e-6);
//
//		double [] newVel = plane.getVelocity();
//		assertEquals(dx, newVel[0], 1e-6);
//		assertEquals(dy, newVel[1], 1e-6);
//		assertEquals(dt, newVel[2], 1e-6);
//
//		// First test getPosition and setPosition at legal bounds
//
//		pose[0] = 0; 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(pose[0], newPose[0], 1e-6);
//
//		pose[0] = 99; 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(pose[0], newPose[0], 1e-6);
//
//		pose[1] = 0; 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(pose[1], newPose[1], 1e-6);
//
//		pose[1] = 99; 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(pose[1], newPose[1], 1e-6);
//
//		pose[2] = -Math.PI; 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(pose[2], newPose[2], 1e-6);
//
//		pose[2] = Math.toRadians(179); 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(pose[2], newPose[2], 1e-6);
//
//		// Test getVelocity and setVelocity at all legal position bounds
//
//		double [] vel = plane.getVelocity();
//
//		vel[0] = 5; 
//		vel[1] = 0; 
//		plane.setVelocity(vel);
//		newVel = plane.getVelocity();
//		assertEquals(vel[0], newVel[0], 1e-6);
//
//		vel[0] = 10; 
//		vel[1] = 0; 
//		plane.setVelocity(vel);
//		newVel = plane.getVelocity();
//		assertEquals(vel[0], newVel[0], 1e-6);
//
//		vel[0] = 0; 
//		vel[1] = 5; 
//		plane.setVelocity(vel);
//		newVel = plane.getVelocity();
//		assertEquals(vel[1], newVel[1], 1e-6);
//
//		vel[0] = 0; 
//		vel[1] = 10; 
//		plane.setVelocity(vel);
//		newVel = plane.getVelocity();
//		assertEquals(vel[1], newVel[1], 1e-6);
//
//		vel[2] = -Math.PI/4.0; 
//		plane.setVelocity(vel);
//		newVel = plane.getVelocity();
//		assertEquals(vel[2], newVel[2], 1e-6);
//
//		vel[2] = -Math.PI/4.0;
//		plane.setVelocity(vel);
//		newVel = plane.getVelocity();
//		assertEquals(vel[2], newVel[2], 1e-6);    
//	}
//
//	// Test get/set Position and Velocity at illegal position bounds
//
//	@SuppressWarnings({ "unused", "deprecation" })
//	@Test
//	public void testGetSetPositionInvalid(){
//		double [] pose = {1, 2, 0};
//		double dx = 5, dy = 0, dt = 0;
//		double s = 5;
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, s, dt, sim, fuel);
//		double [] newPose = plane.getPosition();
//
//		// Test getPosition and setPosition at illegal bounds. Since all bounds
//		// violations get clamped to legal limits, we can test all three
//		// dimensions of position at once. 
//
//		pose[0] = -1; 
//		pose[1] = -1; 
//		pose[2] = -2*Math.PI; 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(0, newPose[0], 1e-6);
//		assertEquals(0, newPose[1], 1e-6);
//		assertEquals(0, newPose[2], 1e-6); //Note the angle of 0
//		//this is because angles should be wrapped instead of clamped.
//
//		pose[0] = 101; 
//		pose[1] = 101; 
//		pose[2] = Math.PI; 
//		plane.setPosition(pose);
//		newPose = plane.getPosition();
//		assertEquals(100, newPose[0], 1e-6);
//		assertEquals(100, newPose[1], 1e-6);
//		assertEquals(-Math.PI, newPose[2], 1e-6);
//
//		// Test getVelocity and setVelocity at illegal bounds. Since all bounds
//		// violations get clamped to legal limits, we can test all three
//		// dimensions of velocity at once.
//
//		double [] vel = plane.getVelocity();
//		vel[0] = 0; 
//		vel[1] = 1; 
//		vel[2] = -Math.PI; 
//		plane.setVelocity(vel);
//		double [] newVel = plane.getVelocity();
//		assertEquals(0, newVel[0], 1e-6);
//		assertEquals(5, newVel[1], 1e-6);
//		assertEquals(-Math.PI/4, newVel[2], 1e-6);
//
//		vel[0] = 0; 
//		vel[1] = 20; 
//		vel[2] = Math.PI; 
//		plane.setVelocity(vel);
//		newVel = plane.getVelocity();
//		assertEquals(0, newVel[0], 1e-6);
//		assertEquals(10, newVel[1], 1e-6);
//		assertEquals(Math.PI/4, newVel[2], 1e-6);
//	}
//
//	// controlVehicle and updateState are tricky to test. You have to use your
//	// judgment as to how to test these. Typically what happens is that as you
//	// develop, you discover edge cases that need to be added. 
//
//	@SuppressWarnings({ "deprecation", "unused" })
//	@Test
//	public void testControlVehicle() {
//		double [] pose = {0, 0, 0};
//		double dx = 5, dy = 0, dt = 0;
//		double s = 0;
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, s, dt, sim, fuel);
//
//		// Acceleration in x
//
//		Control c = new Control(10, 0);
//		plane.controlVehicle(c);
//
//		double [] newVel = plane.getVelocity();
//
//		assertEquals(10, newVel[0], 1e-6);
//		assertEquals(0, newVel[1], 1e-6);
//		assertEquals(0, newVel[2], 1e-6);
//
//		// Acceleration in y
//
//		pose[0] = 0;
//		pose[1] = 0;
//		pose[2] = Math.PI/2;
//		plane.setPosition(pose);
//		double [] vel = {10, 0, 0};
//		plane.setVelocity(vel);
//
//		c = new Control(10, 0);
//		plane.controlVehicle(c);
//
//		newVel = plane.getVelocity();    
//		assertEquals(0, newVel[0], 1e-6);
//		assertEquals(10, newVel[1], 1e-6);
//		assertEquals(0, newVel[2], 1e-6);
//
//		// Acceleration at PI/4 from 5m/s to 10 m/s.
//
//		vel[0] = Math.sqrt(12.5);
//		vel[1] = Math.sqrt(12.5);
//		vel[2] = Math.PI/4;
//		plane.setVelocity(vel);
//		c = new Control(10, 0);
//		plane.controlVehicle(c);
//
//		newVel = plane.getVelocity();    
//		assertEquals(10, Math.sqrt(newVel[0]*newVel[0]+newVel[1]*newVel[1]), 1e-6);
//
//		// Rotational accleration in x
//
//		vel[2] = 0;
//		plane.setVelocity(vel);
//		c = new Control(5, Math.PI/8);
//		plane.controlVehicle(c);
//
//		newVel = plane.getVelocity();
//		assertEquals(Math.PI/8, newVel[2], 1e-6);
//	}
//
//	/**
//	 * Test advance() for a ground vehicle going straight
//	 */
//	@Test
//	public void testAdvanceStraight(){
//		double x = 1.4; 
//		double y = 5.1;
//		double theta = 0;
//		double[] pose = {x, y, theta};
//		double speed = 7;
//
//		double xDot = speed*Math.cos(theta);
//		double yDot = speed*Math.sin(theta);
//
//		double omega = 0;
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, speed, omega, sim, fuel);
//
//		int time = 1000;
//		plane.setFlying(true);
//		plane.advance(time);
//
//		double newX = 1.4 + 7;
//		double newY = 5.1;
//
//		assertTrue(plane.getPosition()[0] == newX && plane.getPosition()[1] == newY &&
//				plane.getPosition()[2] == theta && Math.abs(plane.getVelocity()[0] - xDot) < .0001 &&
//				Math.abs(plane.getVelocity()[1] - yDot) < .0001 && plane.getVelocity()[2] == omega);
//	}
//
//	/**
//	 * Test advance() for a ground vehicle curving
//	 */
//	@Test
//	public void testAdvanceArc(){
//		double x = 21.4; 
//		double y = 19.2;
//		double theta = Math.PI/3;
//		double[] pose = {x, y, theta};
//		double speed = 7;
//
//		double omega = .1;
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, speed, omega, sim, fuel);
//
//		int sec = 12;
//		int msec = 18;
//		
//		int time = 1000*sec + msec;
//		plane.setFlying(true);
//		plane.advance(time);
//
//		//Manually calculated values.
//		double newX = 15.2874;
//		double newY = 98.1175;
//		double newTheta = 2.2490;
//		double newXDot = -4.3918;
//		double newYDot = 5.4509;
//
//		assertTrue(Math.abs(plane.getPosition()[0] - newX)<.001 && Math.abs(plane.getPosition()[1] -newY) < .001 &&
//				Math.abs(plane.getPosition()[2]- newTheta)<.001 && Math.abs(plane.getVelocity()[0] - newXDot) < .0001 &&
//				Math.abs(plane.getVelocity()[1] - newYDot) < .0001 && plane.getVelocity()[2] == omega);
//	}
//
//	/**
//	 * Test advance() for negative time arguments
//	 */
//	@Test(expected = RuntimeException.class)
//	public void testAdvanceNegativeTime(){
//		double x = 21.4; 
//		double y = 19.2;
//		double theta = Math.PI/3;
//		double[] pose = {x, y, theta};
//		double speed = 7;
//
//		double omega = .1;
//		DisplayClient dc = new DisplayClient("127.0.0.1");
//		Simulator sim = new Simulator(dc);
//		int fuel = 50;
//		Airplane plane = new Airplane(pose, speed, omega, sim, fuel);
//
//		int sec = -12;
//		int msec = 18;
//		int time = 1000*sec + msec;
//		plane.setFlying(true);
//		plane.advance(time);
//	}
//
////	public static void main(String[] args){
////		JUnitCore.main(TestAirplane.class.getName());
////	}
//}

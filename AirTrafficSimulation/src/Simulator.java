import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Mycal Tucker
 */

public class Simulator extends Thread{
	private int time; //current time of simulation in milliseconds
	private ArrayList<Airplane> airplaneList; //the Airplanes that will be simulated
	private ArrayList<Airport> airportList;
	private HashMap<Airplane, AirplaneController> controllerMap;
	
	private boolean running; //whether or not the simulation has started
	private DisplayClient dc;
	private int numNonUpdatedPlanes; 
	
	public final int simDuration = 100000;

	public Simulator(DisplayClient dc){
		this.dc = dc;
		this.running = false;
		this.airplaneList = new ArrayList<Airplane>();
		this.airportList = new ArrayList<Airport>();
		this.controllerMap = new HashMap<Airplane, AirplaneController>();
	}

	/**
	 * @return how many seconds have elapsed in the simulation. Returns 0 if
	 * the simulation hasn't started yet.
	 */
	public synchronized int getCurrentSec(){
		if (this.running){
			return this.time/1000;
		}
		return 0;
	}

	/**
	 * @return how many milliseconds have elapsed since the last full second
	 * in the simulation. Returns 0 if the simulation hasn't started yet.
	 */
	public synchronized int getCurrentMSec(){
		if (this.running){
			return this.time%1000;
		}
		return 0;
	}

	/**
	 * Return the ground vehicle at index i in simulator.
	 * Used primarily for testing purposes.
	 * If the index is illegal, throw an illegal argument exception
	 */
	public synchronized Airplane getAirplaneAtInd(int i){
		if (i < 0 || i >= this.airplaneList.size()){
			throw new IllegalArgumentException("index out of bounds");
		}
		return this.airplaneList.get(i);
	}

	/**
	 * @param gv: ground vehicle to add the list of ground vehicles
	 * Note: assumes that the ground vehicle arrives non-updated
	 */
	public synchronized void addAirplane(Airplane a, AirplaneController ac){
		this.airplaneList.add(a);
		a.start();
		this.controllerMap.put(a, ac);
		this.numNonUpdatedPlanes ++;
	}


	private void addAirport(Airport a){
		this.airportList.add(a);
	}

	public synchronized ArrayList<Airplane> getAirplaneList(){
		return this.airplaneList;
	}

	public synchronized int getNumNonUpdated(){
		return this.numNonUpdatedPlanes;
	}

	public synchronized void setNumNonUpdated(int newNum){
		this.numNonUpdatedPlanes = newNum;
	}
	
	public synchronized AirplaneController getController(Airplane a){
		return this.controllerMap.get(a);
	}

	/**
	 * Just updates the time. Airplanes must pull the time
	 * on their own.
	 */
	public void run(){
		dc.clear();
		dc.traceOff();

		this.running = true;
		this.time = 0;

		while (this.time < this.simDuration){ //100 seconds == 100,000 milliseconds
			/*
			 * Must lock on this (the simulator) to guarantee that all vehicles
			 * get updated exactly once at each time step.
			 */
			
			synchronized(this){	
				///////////////////////////////
				/*
				 * Trying a periodic thing
				 */
				//spawn airplanes periodically
//				if (this.time%40000 == 5000){
//					System.out.println("launching a new plane");
//					double[] startPose = {25, 25, 0};
//					Airplane tempAirplane = new Airplane(startPose, 5, 0, this, 75);
//					tempAirplane.setPlaneName("plane" + this.time);
//					//tempAirplane.setPlaneName("plane5");
//					AirplaneController cont1 = new AirplaneController(this, tempAirplane, this.airportList.get(0), this.airportList.get(1), this.time + 100);
//					cont1.start();
//					this.airportList.get(0).spawnAirplane(tempAirplane); //get an airport
//					this.addAirplane(tempAirplane, cont1);
//					this.airportList.get(0).takeoff(tempAirplane);
//				}
				
				
				
				if (this.time%15000 == 10000){
					this.flyRoundTrip();
				}
				///////////////////////////////
				
				
				
				dc.sendAirportMessage(this.airportList);
				dc.sendFuelMessage(this.airplaneList);
				dc.sendAirportCapacity(this.airportList);
				
				double[] x = new double[this.airplaneList.size()];
				double[] y = new double[this.airplaneList.size()];
				double[] theta = new double[this.airplaneList.size()];
				for (int i = 0; i < this.airplaneList.size(); i ++){
					Airplane temp = this.airplaneList.get(i);
					x[i] = temp.getPosition()[0];
					y[i] = temp.getPosition()[1];
					theta[i] = temp.getPosition()[2];
				}
				dc.update(this.airplaneList.size(), x, y, theta);
				dc.traceOn();

				this.time += 1;

				notifyAll();
				//wait for all planes to update

				while (this.numNonUpdatedPlanes > 0){
					try{
						this.wait();
					}
					catch(InterruptedException ie){
						System.err.println("There was an ie error");
						System.err.println(ie);
					}
				}

				this.numNonUpdatedPlanes = this.airplaneList.size();
			}
			
		}

		dc.traceOff();
		dc.clear();
	}
	
	//for any plane that is landed at its destination, make it fly back
	private void flyRoundTrip(){
		for (Airplane a: this.airplaneList){
			AirplaneController ac = this.controllerMap.get(a);
			if (ac.reachedDestination()){
				Airport start = ac.getStartAirport();
				Airport end = ac.getEndAirport();
				ac.setEndAirport(start);
				ac.setStartAirport(end);
				ac.setDepartureTime(this.time + 5);
				ac.setDestinationReached(false);
				end.takeoff(a);
				a.setFuelLevel(a.getFuelLevel() + 10);
			}
		}
	}

	public void printInfo(){
		for (Airplane gv: this.airplaneList){
			String output = new String();
			output = output + String.format("%.2f", this.time/1000.0) + "\t"; //2 decimal places
			output = output + String.format("%.2f", gv.getPosition()[0]) + "\t" + 
					String.format("%.2f", gv.getPosition()[1]); //2 decimal places
			output = output + "\t" + String.format("%.1f", 180.0/Math.PI*gv.getPosition()[2]); //1 decimal place
			System.out.println(output);
			System.out.println();
		}
	}

	/**
	 * Constructs a Simulator and then runs it.
	 * @param argv
	 */
	public static void main(String[] argv) {		
		String host = null;
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DisplayClient tempDC = new DisplayClient(host);
		Simulator s = new Simulator(tempDC);
		tempDC.addSimulator(s);

		Airport a1 = new Airport(15, 15, 3, s);
		Airport a2 = new Airport(50, 50, 1, s);
		Airport a3 = new Airport(25, 75, 3, s);
		Airport a4 = new Airport(75, 25, 3, s);
		
		a1.setName("15, 15");
		a2.setName("50, 50");
		a3.setName("25, 75");
		a4.setName("75, 25");

		s.addAirport(a1);
		s.addAirport(a2);
		s.addAirport(a3);
		s.addAirport(a4);
		
		
		double[] p1startPose = {25, 25, 0};
		Airplane plane1 = new Airplane(p1startPose, 5, 0, s, 50);
		plane1.setPlaneName("plane1");

		double[] p2startPose = {5, 5, 0};
		Airplane plane2 = new Airplane(p2startPose, 5, 0, s, 100);
		plane2.setPlaneName("plane2");

		double[] p3startPose = {5, 5, 0};
		Airplane plane3= new Airplane(p3startPose, 5, 0, s, 10);
		plane3.setPlaneName("plane3");

		AirplaneController cont1 = new AirplaneController(s, plane1, a1, a3, 100);
		AirplaneController cont2 = new AirplaneController(s, plane2, a3, a2, 100);
		AirplaneController cont3 = new AirplaneController(s, plane3, a4, a2, 100);

		s.addAirplane(plane1, cont1);
		s.addAirplane(plane2, cont2);
		s.addAirplane(plane3, cont3);
		
		
		cont1.start();
		cont2.start();
		cont3.start();

		s.start();
	}
}
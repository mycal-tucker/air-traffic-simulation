import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class DisplayClient  {
  PrintWriter output;
  BufferedReader input;
  protected NumberFormat format = new DecimalFormat("#####.##");
  private boolean gotUserMessage = false;
  private Simulator sim;
  private ArrayList<Airport> airports;

  public DisplayClient(String host) {
    InetAddress address;
    try {
      address = InetAddress.getByName(host);
      Socket server = new Socket(address, 5065);
      output = new PrintWriter(server.getOutputStream());
      input = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }
    catch (UnknownHostException e) {
      System.err.println("I can't find a host called "+host+". Are you sure you got the name right?");
      System.err.println(e);
      System.exit(-1);
    }
    catch (IOException e) {
      System.err.println("I can't connect to the DisplayServer running on "+host+".\n");
      System.err.println("Did you remember to start the DisplayServer?");
      System.err.println(e);
      System.exit(-1);
    }
  }
  
  /**
   * Added by Mycal
   * @param s
   */
  public void addSimulator(Simulator s){
	  this.sim = s;
  }

  public void clear() {
    output.println("clear");
  }

  public void traceOn() {
    output.println("traceon");
  }

  public void traceOff() {
    output.println("traceoff");
  }

  public void update(int numVehicles, double gvX[], double gvY[], double gvTheta[])
  {
    StringBuffer message = new StringBuffer();
    message.append(numVehicles);
    message.append(" ");
    for (int i = 0; i < numVehicles; i++) {
      message.append(format.format(gvX[i])+" "+format.format(gvY[i])+" "+
		     format.format(gvTheta[i])+" ");
    }
    //System.out.println("Sent "+message);
    output.println(message);
    output.flush();

    	this.getServerMessage();
  }
  
  /**
   * The message goes like this
   * airports size x1 y1 x2 y2 x3 y3
   * 
   * e.g.:
   * airports 2 25 25 75 75
   * 
   * @param airportList: list of airports
   */
  public void sendAirportMessage(ArrayList<Airport> airportList){
	  this.airports = airportList;
	  StringBuffer message = new StringBuffer();
	  message.append("airports");
	  message.append(" ");
	  message.append(airportList.size());
	  message.append(" ");
	  for (int i = 0; i < airportList.size(); i ++){
		  Airport a = airportList.get(i);
		  message.append(a.getX() + " ");
		  message.append(a.getY() + " ");
	  }
	  output.println(message);
	  output.flush();
  }
  
  /**
   * The message goes like this
   * fuel num f1 f2 f3 f4 f5
   * 
   * e.g.:
   * fuel 6 25 25 25 75 75
   * 
   * @param airplaneList: list of airplanes
   */
  
  public void sendFuelMessage(ArrayList<Airplane> airplaneList){
	  StringBuffer message = new StringBuffer();
	  message.append("fuel");
	  message.append(" ");
	  for (int i = 0; i < airplaneList.size(); i ++){
		  Airplane a = airplaneList.get(i);
		  message.append(a.getFuelLevel() + " ");
	  }
	  output.println(message);
	  output.flush();
  }
  
  public void sendAirportCapacity(ArrayList<Airport> airportList){
	  this.airports = airportList;
	  StringBuffer message = new StringBuffer();
	  message.append("capacity");
	  message.append(" ");
	  for (int i = 0; i < airportList.size(); i ++){
		  Airport a = airportList.get(i);
		  message.append(a.getCapacity() + " ");
	  }
	  output.println(message);
	  output.flush();
  }
  
  private void getServerMessage(){
	    try {
	    	output.println("getMessage");
	    	output.flush();
			String serverMessage = input.readLine();
			if (this.sim == null){
				System.err.println("I can't do anything with this because I don't know sim");
			}
			else if (serverMessage.equals("0 0")){
				//default message, ignore it
			}
			else{
				String[] parsed = serverMessage.split(" ");
				int startAirportIndex = Integer.parseInt(parsed[0]);
				int endAirportIndex = Integer.parseInt(parsed[1]);
				
				double[] startPose = {0, 0, 0}; //will be overwritten
				Airplane plane = new Airplane(startPose, 5, 0, this.sim, 50);
				plane.setPlaneName("user plane" + this.sim.getCurrentSec()*1000 + this.sim.getCurrentMSec());
				
				AirplaneController cont = new AirplaneController(this.sim, plane, this.airports.get(startAirportIndex), this.airports.get(endAirportIndex), this.sim.getCurrentSec()*1000 + this.sim.getCurrentMSec() + 50);
				cont.start();
				this.sim.addAirplane(plane, cont);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
  }
}
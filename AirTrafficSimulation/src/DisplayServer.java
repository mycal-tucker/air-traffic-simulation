import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class DisplayServer extends JPanel implements KeyListener {
	private static int historySkip = 5;
	private static final long serialVersionUID = 1l;

	protected double gvX [], gvY[], gvTheta[];
	protected boolean isTryAgain = true;
	protected int numVehicles = 0;
	protected int gain = 5;
	protected int droneX[], droneY[];
	protected double apX [], apY[], fuel[];
	protected int airportX[], airportY[];
	protected int numAirports;
	protected JFrame frame;
	protected NumberFormat format = new DecimalFormat("#####.##");
	protected String myHostname; 
	protected Color[] my_colors = new Color[] {Color.black,Color.blue,Color.cyan,
			Color.green, Color.magenta, 
			Color.orange, Color.pink,
			Color.red, Color.yellow};
	
	public class History {
		History() {
			myX = new double[100000];
			myY = new double[100000];
			myNumPoints = 0;
			loopHistory = 0;
			trueHistoryLength = 0;
		}
		public double [] myX;
		public double [] myY;
		int myNumPoints;
		int trueHistoryLength;
		int loopHistory;
	}
	History [] histories;
	boolean trace = false;

	public synchronized void clear() {
		if (histories !=null){
			for (int i = 0; i < histories.length; i++) {
				histories[i].myNumPoints = 0;
				histories[i].loopHistory = 0;
				histories[i].trueHistoryLength = 0;
			}
		}
	}

	public synchronized void resetHistories(int numVehicles) {
		histories = new History[numVehicles];
		for (int i = 0; i < numVehicles; i++)
			histories[i] = new History();
	}


	public class MessageListener extends Thread {
		public BufferedReader my_client; 
		public DisplayServer my_display;
		public MessageListener(Socket client, DisplayServer display) {
			my_display = display; 
			try {
				//System.out.println("Default size: " + client.getReceiveBufferSize());
				my_client = new BufferedReader
						(new InputStreamReader(client.getInputStream()));
			}
			catch (IOException e) {
				System.err.println("Very weird IOException in creating the BufferedReader");
				System.err.println(e);
				System.exit(-1);
			}
		}
		public void run() {
			try {
				while (true) {
					String message = my_client.readLine();
					if (message == null){
						System.out.println("EOF reached!");
						return; //EOF reached	
					}

					StringTokenizer st = new StringTokenizer(message);
					//System.out.println("Received: " + message);
					String tok = st.nextToken();	  
					if (tok.equals("clear")) {
						my_display.clear();
					}
					else if (tok.equals("traceon")) {
						synchronized (my_display) {
							my_display.trace = true;
						}
					} else if (tok.equals("traceoff")) {
						synchronized (my_display) {
							my_display.trace = false;
						}
					} 

					/*Our thing below:
					 * 
					 */
					else if (tok.equals("airports")){
						synchronized (my_display){
							tok = st.nextToken();
							numAirports = Integer.parseInt(tok);
							my_display.apX = new double[numAirports];
							my_display.apY = new double[numAirports];
							for (int i = 0; i < numAirports; i ++){
								tok = st.nextToken();
								double x = Double.parseDouble(tok);
								tok = st.nextToken();
								double y = Double.parseDouble(tok);
								apX[i] = x;
								apY[i] = y;
								//TODO: draw an airport at x, y
							}

							//printAPS();
						}
					}

					/*
					 * End of our thing
					 */
					else if (tok.equals("fuel")){
						synchronized (my_display){
							my_display.fuel = new double[numVehicles];		
							for (int i = 0; i < numVehicles; i ++){
								tok = st.nextToken();
								double x = Double.parseDouble(tok);
								fuel[i]=x;
							}
						}
					}

					else {
						synchronized (my_display) {
							if (my_display.numVehicles != Integer.parseInt(tok)) {
								my_display.numVehicles = Integer.parseInt(tok);
								my_display.gvX = new double[my_display.numVehicles];
								my_display.gvY = new double[my_display.numVehicles];
								my_display.gvTheta = new double[my_display.numVehicles];
								my_display.resetHistories(numVehicles);
							}
							for (int i = 0; i < my_display.numVehicles; i++) {
								tok = st.nextToken();
								my_display.gvX[i] = Double.parseDouble(tok);
								tok = st.nextToken();
								my_display.gvY[i] = Double.parseDouble(tok);
								tok = st.nextToken();
								my_display.gvTheta[i] = Double.parseDouble(tok);
								if (trace) {
									if (histories[i].trueHistoryLength % historySkip == 0){
										int n;
										if (histories[i].myNumPoints == histories[i].myX.length) {
											n = 0;                                                                    
											histories[i].myNumPoints = 0;
											histories[i].loopHistory = 1;
										} else {
											n = histories[i].myNumPoints;
											histories[i].myNumPoints++;
										}
										histories[i].myX[n] = my_display.gvX[i];
										histories[i].myY[n] = my_display.gvY[i];
									}
									histories[i].trueHistoryLength++;
								} // end if (trace) 
							} // end for (int i = 0; i < my_display.numVehicles; i++) 
						} // End synchronized (my_display) 
					}
					my_display.repaint();
				}
			}
			catch (IOException e) {
			}
			return; 
		}
	}

	/**
	 * was used to help debug airport info
	 */
	@SuppressWarnings("unused")
	private void printAPS(){
		for (int i = 0; i < numAirports; i ++){
			System.out.println("x: " + apX[i] + " y: " + apY[i]);
		}
	}

	public DisplayServer (String hostname) {
		myHostname = hostname;
		droneX = new int[9];
		droneY = new int[9];

		// This is just the UAV shape centered at the origin.
		// If you wanted to draw a more realistic UAV, you would modify this
		// polygon. 

		droneX[0] = 10;  droneY[0] = 0;
		droneX[1] = 0;   droneY[1] = -5;
		droneX[2] = 0;   droneY[2] = -2;
		droneX[3] = -8;  droneY[3] = -2;
		droneX[4] = -10; droneY[4] = -4;
		droneX[5] = -10; droneY[5] = 4;
		droneX[6] = -8;  droneY[6] = 2;
		droneX[7] = 0;   droneY[7] = 2;
		droneX[8] = 0;   droneY[8] = 5;

		//Airports are just squares:
		airportX = new int[4];
		airportY = new int[4];

		airportX[0] = -10; airportY[0] = -10;
		airportX[1] = 10; airportY[1] = -10;
		airportX[2] = 10; airportY[2] = 10;
		airportX[3] = -10; airportY[3] = 10;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				startGraphics();
			}
		});
	}

	public void startGraphics()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);

		frame = new JFrame("16.35 Display");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container container = frame.getContentPane();
		//container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		container.setLayout(new BorderLayout());
		
		Object[] options = {"Quit",
				"Add New Airplane",
		"Continue"};
		int n = JOptionPane.showOptionDialog(frame,
				"What would you like to do?",
						"name",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]);
		System.out.println(n);
		
		if (n==0)
			System.exit(-1);
		else if(n==1){
			while(isTryAgain == true){
			Object[] departOptions = {"Airport 4", "Airport 3", "Airport 2","Airport 1"};
			
			Object[] arriveOptions = {"Airport 4", "Airport 3", "Airport 2","Airport 1"};
					
			int depart = JOptionPane.showOptionDialog(frame,
					"From what airport would you like to depart?",
							"name", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							departOptions,
							departOptions[3]);
			
			int arrive = JOptionPane.showOptionDialog(frame,
					"To what airport would you like to arrive?",
							"name", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							arriveOptions,
							arriveOptions[3]);
			
			if(arrive==depart){
				JOptionPane.showMessageDialog(frame,
					    "You cannot depart and arrive at the same airport. Please try again.");
			}
			else
				isTryAgain = false;
			}
		}

		setOpaque(true);   
		setFocusable(true);
		setMinimumSize(new Dimension(100*gain,100*gain));
		setPreferredSize(new Dimension(100*gain,100*gain));
		addKeyListener(this);
		container.add(this,BorderLayout.WEST);
		setVisible(true);

		frame.pack();
		frame.setVisible(true);    
	}

	public void keyPressed(KeyEvent e) { }

	public void keyReleased(KeyEvent e) { }

	public void keyTyped(KeyEvent e)
	{
		switch (e.getKeyChar()) {
		case 'q':
		case 'Q':
			System.exit(0);
		}
	}

	protected synchronized void drawVehicles(Graphics g) {
		g.setColor(Color.black);

		// This chunk of code just translate and rotates the shape.

		for (int j = 0; j < numVehicles; j++) {
			if (j < my_colors.length){
				g.setColor(my_colors[j]);
			}else{
				g.setColor(my_colors[my_colors.length-1]);
			}
			int drawX[] = new int[9];
			int drawY[] = new int[9];

			for (int i = 0; i < 9; i++) {
				// We scale the x and y by gain, since the bounds on X and Y are 100(gain)x100(gain)

				double x = gvX[j]*gain;
				double y = gvY[j]*gain;
				double th = gvTheta[j];
				drawX[i] = (int)(x+Math.cos(th)*droneX[i]+Math.sin(th)*droneY[i]);
				drawY[i] = (int)(y+Math.sin( th)*droneX[i]-Math.cos(th)*droneY[i]);
				drawY[i] = 100*gain- drawY[i];
			}
			g.drawPolygon(drawX, drawY, 9);
			
//			if (fuel[j]>100)
//				g.setColor(Color.green);
//			else if(fuel[j]<=100 && fuel[j]>50)
//				g.setColor(Color.yellow);
//			else if(fuel[j]<=50 && fuel[j]>0)
//				g.setColor(Color.orange);
//			else
//				g.setColor(Color.red);
//			
//			System.out.println(fuel[j]);
			
			try{
			
			if(fuel[j]>100)
				fuel[j]=100;
				
			if(fuel[j]<=100 && fuel[j]>=50)
				g.setColor(new Color((int)((100-fuel[j])/50*255), 255, 0));
			else if(fuel[j]<50 && fuel[j]>0)
				g.setColor(new Color(255, (int)((fuel[j])/50*255), 0));
			else
				g.setColor(Color.black);
			
			g.fillPolygon(drawX, drawY, 9);
			
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.out.println(e);
			}
		
		}

	}

	protected synchronized void drawAirports(Graphics g){
		g.setColor(Color.black);
		//TODO

		for (int j = 0; j < numAirports; j ++){
			int drawX[] = new int[4];
			int drawY[] = new int[4];

			for (int i = 0; i < 4; i ++){
				double x = apX[j]*gain;
				double y = apY[j]*gain;
				drawX[i] = (int)(x + airportX[i]);
				drawY[i] = (int)(y - airportY[i]);
				drawY[i] = 100*gain - drawY[i];
			}
			g.drawString("Airport "+(j+1), drawX[0],drawY[0] );
			g.drawPolygon(drawX, drawY, 4);
		}
	}

	//	protected synchronized void drawFuel(Graphics g){
	//		g.setColor(Color.black);
	//		//TODO
	//		
	//		for (int j = 0; j < numVehicles; j ++){
	//			int drawX[] = new int[4];
	//			int drawY[] = new int[4];
	//			
	//			for (int i = 0; i < 4; i ++){
	//				double x = apX[j]*gain;
	//				double y = apY[j]*gain;
	//				drawX[i] = (int)(x + airportX[i]);
	//				drawY[i] = (int)(y - airportY[i]);
	//				drawY[i] = 100*gain - drawY[i];
	//			}
	//			Graphics2D g2d = (Graphics2D)g;
	//			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, 1*gain, 1*gain);
	//			   g2d.fill(circle);
	//		}
	//	}

	protected synchronized void drawHistories(Graphics g) {
		g.setColor(Color.black);

		// This chunk of code just translate and rotates the shape.

		for (int j = 0; j < numVehicles; j++) {
			if (j < my_colors.length){
				g.setColor(my_colors[j]);
			}else{
				g.setColor(my_colors[my_colors.length-1]);
			}
			int drawX[]; int drawY[];
			if (histories[j].loopHistory == 0){
				drawX = new int[histories[j].myNumPoints];
				drawY = new int[histories[j].myNumPoints];
			}
			else{

				drawX = new int[histories[j].myX.length];
				drawY = new int[histories[j].myY.length];
			}
			for (int i = 0; i < drawX.length;i++){
				// We scale the x and y by gain, since the bounds on X and Y are 100(gain)x100(gain)

				double x = histories[j].myX[i]*gain;
				double y = histories[j].myY[i]*gain;
				drawX[i] = (int)(x);
				drawY[i] = 100*gain- (int)y;

			}
			g.drawPolyline(drawX, drawY, drawX.length);
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g); //paints the background and image

		Rectangle bounds = this.getBounds();
		g.setColor(Color.white);
		g.fillRect(0, 0, bounds.width, bounds.height);

		g.setColor(Color.black);
		g.drawString("Display running on "+myHostname, 10,10);
		if (trace) 
			drawHistories(g);
		drawVehicles(g);
		drawAirports(g);
	}

	protected void addClient(Socket client) {
		MessageListener l = new MessageListener(client, this);
		l.start();
	}

	public static void main(String [] argv) {
		try {
			ServerSocket s = new ServerSocket(5065);
			s.setReuseAddress(true);      
			if (!s.isBound())
				System.exit(-1);
			String address = GeneralInetAddress.getLocalHost().getHostAddress();
			DisplayServer d = new DisplayServer(address);
			do {
				Socket client = s.accept();
				d.addClient(client);
			} while (true);
		} 
		catch (IOException e) {
			System.err.println("I couldn't create a new socket.\n"+
					"You probably are already running DisplayServer.\n");
			System.err.println(e);
			System.exit(-1);
		}
	}

}
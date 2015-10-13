package coreAtom;

/********************
 * TODO: OPENGL, SAVING TO FILE, REVAMP PHYSICS (USE KHANACADEMY FOR THAT)
 */
import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
public class run extends JFrame implements Runnable, MouseListener, KeyListener, MouseMotionListener
{
	ArrayList<Atom> atomList = new ArrayList<Atom>();
	ArrayList<Bond> bondList = new ArrayList<Bond>();
	
	Container con = getContentPane();
	Thread t = new Thread(this);
	ArrayList<Point2D.Double> pList = new ArrayList<Point2D.Double>();
	ArrayList<Integer> aList = new ArrayList<Integer>();
	QuadTree root = new QuadTree(-100, -100, 800, 800);
	Collider c = new Collider();
	Rectangle worldBorder = new Rectangle();
	
	int minQuadSize = 100;
	
	boolean doRectangle = false;
	public boolean stick = true;
	
	double targetTimeStep = 0.02;
	public double elapsedTime = 0;
	public static double timeStep = 0.04;
	//time
	public double time = System.nanoTime();
	public double lastTime = time;
	double delay = 0;
	int tick = 0;
	double tm = 0;
	double paintTime = 0;
	double repaintTime = 17;
	static double scale = 1.5;
	
	double maxLengthChange = 0;
	double maxSpeed = 0;
	
	//this stuff is for setting new rectangles
	Boolean place = false;
	Point startPoint = new Point();
	Point endPoint = new Point();
	Point rPoint = new Point();
	int length = 0;
	int height = 0;
	
	double dX = 0;
	double dY = 0;
	int mouseX = 0;
	int mouseY = 0;
	boolean pressed = false;
	
	Material goop = new Material();
	Material liquid = new Material();
	Material concrete = new Material();
	Material steel = new Material();
	Material car = new Material();
	
	Material selectedMaterial = goop;
	
	public static boolean pause = true;
	public boolean clear = false;
	
	
	public run ()
	{
		con.setLayout(new FlowLayout());
		t.start();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addMouseListener(this);
		addKeyListener(this);
		worldBorder.setBounds(0, 0, 600, 500);
		
		
		//Initializes materials
		goop.setMass(1);
		goop.setMinDist(16);
		goop.setMaxDist(40);
		goop.setDampener(10);
		goop.setTensileStrength(0.3);
		goop.setCompressiveStrength(1);
		goop.setEquilibrium(30);
		goop.setSpacing(30);
		goop.setCost(10);
		goop.setColor(new Color(30, 255, 50));
		
		concrete.setMass(1.3);
		concrete.setMinDist(13);
		concrete.setMaxDist(40);
		concrete.setDampener(13);
		concrete.setTensileStrength(0.1);
		concrete.setCompressiveStrength(1);
		concrete.setEquilibrium(30);
		concrete.setSpacing(31);
		concrete.setCost(20);
		concrete.setColor(new Color(150, 150, 150));
		
		steel.setMass(1);
		steel.setMinDist(13);
		steel.setMaxDist(40);
		steel.setDampener(13);
		steel.setTensileStrength(0.5);
		steel.setCompressiveStrength(0.5);
		steel.setEquilibrium(30);
		steel.setSpacing(35);
		steel.setCost(50);
		steel.setColor(new Color(50, 50, 50));
		
		liquid.setMass(1);
		liquid.setMinDist(13);
		liquid.setMaxDist(40);
		liquid.setDampener(13);
		liquid.setTensileStrength(0);
		liquid.setCompressiveStrength(0);
		liquid.setEquilibrium(30);
		liquid.setSpacing(25);
		liquid.setCost(2);
		liquid.setColor(new Color(50, 50, 255));
		
		car.setMass(3);
		car.setMinDist(13);
		car.setMaxDist(50);
		car.setDampener(13);
		car.setTensileStrength(1);
		car.setCompressiveStrength(1);
		car.setEquilibrium(30);
		car.setCost(0);
		car.setColor(new Color(255, 0, 0));
	}
	
	public void run()
	{
		try{
			
			 
			root.setIndex(-1);
			
			
			time = System.nanoTime()/1000000;
			time /= 1000;
			
			while(true)
			{
				
				
				//updates time
				lastTime = time;
				time = System.nanoTime()/1000/1000/1000;
				elapsedTime = (time-lastTime);
				//timeStep = elapsedTime;
				
				
				if (elapsedTime > targetTimeStep)
				{
					delay = 0;
				}
				else 
				{
					delay = targetTimeStep;
				}
				
				//System.out.println(delay);
				
				t.sleep((int)(1000*Math.abs(delay)));
				paintTime += 1000*timeStep;
				
				c.bondsChecked = 0;
				c.bondsMade = 0;

				//places atoms with mouse
				if (place && pause)
				{
					dX = startPoint.x-endPoint.x;
					dY = startPoint.y-endPoint.y;
					
					System.out.println("Start: "+startPoint.x+" "+startPoint.y);
					System.out.println("End: "+endPoint.x+" "+endPoint.y);
					
					if (dX < 0)
					{
						rPoint.setLocation(startPoint.x, rPoint.y);
					}
					else
					{
						rPoint.setLocation(endPoint.x, rPoint.y);
					}
					
					if (dY < 0)
					{
						rPoint.setLocation(rPoint.x, startPoint.y);
					}
					else
					{
						rPoint.setLocation(rPoint.x, endPoint.y);
					}
					
					
					if (doRectangle && dX != 0 && dY != 0)
					{
					
						System.out.println(rPoint.getX()+" "+ rPoint.getY());
						//System.out.println(endPoint.getX());
						
						length = (int)(Math.abs(dX)/30);
						length *= 30;
						
						height = (int)((Math.abs(dY)/(15*Math.sqrt(3))));
						height *= (15*Math.sqrt(3));
						
						int num = 0;
						//int num = (int)((length/30)*(height/(15*Math.sqrt(3))));
						
						int row = 0;
						int col = 0;
						
						//tests how many can fit in the rectangle
						while (row < height/(15*Math.sqrt(3)))
						{
							if (col > length/30)
							{
								col = 0;
								row ++;
							}
							col += 1;
							num++;
						}
						//removes the extra one
						num -=1;
						
						int start = atomList.size();
						Setter.init(atomList, num, selectedMaterial);
						
						double size = selectedMaterial.getSpacing();
						
						for (int i = 0; i < start+num; i++)
						{
							Setter.setRect(atomList, start, start+num, rPoint.getX(), rPoint.getY(), length/size, size);
						}
					
					}
					else if (dX != 0 && dY != 0)
					{
						
						double dist = Math.sqrt(Math.pow(startPoint.x-endPoint.x, 2)+Math.pow(startPoint.y-endPoint.y, 2));
						
						
						
						double size = selectedMaterial.getSpacing();
						int num = (int)Math.floor(dist/size);
						int start = atomList.size();
						System.out.println(start + " total: " + (start+num) + " startx: " + startPoint.x + " starty: " + startPoint.y + 
								" endx: " +endPoint.x+" endy: "+endPoint.y+" dist: "+dist+" size: "+size);
						Setter.init(atomList, num, selectedMaterial);
						
						Setter.setLine(atomList, start, start+num, startPoint.x, startPoint.y, endPoint.x, endPoint.y, dist, size);
					}
					
				    //removes ones that are too close together
					c.removeOverlappers(atomList, 15);
					
					
					place = false;
					
					
					
					/*********
					 * UPDATES QUADTREES AND MAKES NEW BONDS
					 */
					stick = true;
					
					root.addAtoms(atomList, aList, 10, minQuadSize);
					root.collideAtoms(root, atomList, bondList);
					root.addAtoms(atomList, aList, 10, minQuadSize);
					root.collideAtoms(root, atomList, bondList);
					
					stick = false;
					//System.out.println(atomList.size());
				}
				
				/********
				 * UPDATES LENGTHS
				 */
				for (Bond b: bondList)
					Force.updateLength(b);
				
				/*********
				 * UPDATES QUADTREES
				 */
				root.addAtoms(atomList, aList, 10, minQuadSize);
				

				
				/*********
				 * MAKES NEW BONDS AND COLLIDES WITH GROUND
				 */
				if (tick %10 == 0 && !pause)
				{
					root.collideAtoms(root, atomList, bondList);
				}
				
				
				/******
				 * UPDATES MAX SPEED
				 */
				if (!pause)
				{
					root.updateMaxSpeed(atomList);
				}
				

				
				/*********
				 * MOVES ATOMS AND UPDATES CONNECTIONS
				 */
				if (!pause)
				{
					for (int l = 0; l < atomList.size(); l++)
					{	
						
						atomList.get(l).move(timeStep);
						
						//almost forgot this...
						atomList.get(l).updateConnections(bondList, l);
					}
					//c.ground(500, atomList);
					c.doBorder(worldBorder, atomList);
				}
				
				if (pause)
				{
					for (int i = 0; i < atomList.size(); i++)
					{
						//c.stickToHardPoints(atomList.get(i), new Rectangle2D.Double());
					}
				
				}

				
				/*********
				 * DELETES EXTRA BONDS, FINDS WHAT BONDS HAVE BOTH ATOMS FULLY INSIDE A QUADTREE
				 */
				if (tick %1 == 0)
				{
					c.deleteFarBonds(bondList);
					if (!pause)
					{
						root.addBonds(atomList, bondList, root.bList);
						root.DoForce(atomList, bondList);
					}
				}
				
				
				/*********
				 * CLEARS ATOMS AND BONDS AND STUFF
				 */
				if (clear)
				{
					//clear = false;
				}
				
				//System.out.println("MaxNumberOfComparisons: "+0.5*(atomList.size()+1)*(atomList.size()));
				//System.out.println("bonds checked: "+c.bondsChecked);
				//System.out.println("bonds made: "+c.bondsMade);
				
				//updates mouse positions
				updateMouseLoc();

				//repaints
				if (paintTime >= repaintTime)
				{
					repaint();
					paintTime = 0;
				}
				tick ++;
			}
				
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void paint(Graphics gr)
	{
		Image i=createImage(getSize().width, getSize().height);
		Graphics2D g2 = (Graphics2D)i.getGraphics();
		
		//scales
		g2.scale(scale, scale);
		
		//sets antialiasing
	    g2.setRenderingHints(new RenderingHints(
	             RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
	    g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		g2.setColor(new Color(0, 0, 0));
		
		
		//draws quadtrees
		g2.setColor(new Color(0, 255, 255));
		root.draw(g2, new Color(0, 255, 255));
		
		root.highlightAtom(g2, mouseX, mouseY, pressed);
		
		
		g2.setColor(new Color(0, 0, 0));
		for (int p = 0; p < atomList.size(); p++)
		{
			atomList.get(p).draw(g2);
		}
		//*/
		
		
		
		for (int u = 0; u < bondList.size(); u++)
		{
			if (bondList.get(u).length <= bondList.get(u).getMaxDist())
			{
				bondList.get(u).draw(g2, atomList);

				//double force = bondList.get(u).getForce();
				
				//g2.drawString(Double.toString(Force.getRotation(atomList.get(host), atomList.get(target))[0]*force), (int)atomList.get(host).getPosition()[0], 
				//		10+(int)atomList.get(host).getPosition()[1]);
				/*
				g2.drawString(Double.toString(bondList.get(u).getForce()), (int)atomList.get(bondList.get(u).getTargets()[0]).getPosition()[0], 
				(int)atomList.get(bondList.get(u).getTargets()[0]).getPosition()[1]);		
				//*/
			}
		}
		
		/*
		g2.drawString(Double.toString(maxLengthChange), 10, 40);
		g2.drawString(Double.toString(maxSpeed), 10, 60);
		g2.drawString(Double.toString(timeStep), 10, 80);
		*/
		
		
		g2.dispose();
		gr.drawImage(i, 0, 0, this);
	}
	
	public double randDouble(double lowest, double highest)
	{
		Random r = new Random();
		double rnd = r.nextDouble();
		double ans = lowest + (highest-lowest)*rnd;
		return ans;
	}
	
	public static void main(String[] args)
	{
		run frame = new run ();
		frame.setSize((int)(600*scale), (int)(500*scale));
		frame.setVisible(true);
	}
	public void update(Graphics g)
	{
		paint(g);
	}

	public void mouseClicked(MouseEvent e) {
		pressed = true;
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		startPoint.setLocation(e.getX()/scale, e.getY()/scale);
	}

	public void mouseReleased(MouseEvent e) {
		endPoint.setLocation(e.getX()/scale, e.getY()/scale);
		place = true;
	}
	
	public void updateMouseLoc()
	{
		Point tmpPoint = MouseInfo.getPointerInfo().getLocation();
		mouseX = (int)(tmpPoint.x/scale);
		mouseY = (int)(tmpPoint.y/scale);
	}

	public void keyPressed(KeyEvent k) {
		
	}

	public void keyReleased(KeyEvent k) {
		//clears stuff
		if (k.getKeyCode() == 8 || k.getKeyCode() == 67)
		{
			clear = true;
		}
		
		//p
		if (k.getKeyCode() == 80 || k.getKeyCode() == 32)
		{
			pause = !pause;
			//updates current cost
		}
		
		//1
		if (k.getKeyCode() == 49)
		{
			selectedMaterial = goop;
		}
		
		//2
		if (k.getKeyCode() == 50)
		{
			selectedMaterial = concrete;
		}
		
		//3
		if (k.getKeyCode() == 51)
		{
			selectedMaterial = steel;
		}
		
		//4
		if (k.getKeyCode() == 52)
		{
			selectedMaterial = liquid;
		}
	}

	public void keyTyped(KeyEvent k) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}

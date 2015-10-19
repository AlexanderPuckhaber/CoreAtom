package coreAtom;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Atom {
	ArrayList<Integer> connectedTo = new ArrayList<Integer>();
	
	
	public Material m;
    

    ////////////////////START TRANSITION TO POINTS FROM X AND Y VALUES
    private Point2D.Double p;
    private Point2D.Double lP;
    
    private double xVel;
    private double yVel;
    
    private double elapsedTime;
    public boolean firstStep;
    private boolean isActive;
    
    
    private Color c = new Color(0, 0, 0);
    
    public Atom()
    {
    	p = new Point2D.Double(0, 0);
    	lP = new Point2D.Double(0, 0);
    	firstStep = true;
    }
    
    public void move(double timeStep)
    {

    	
    	lP.x = p.x;
    	lP.y = p.y;
    	

    	p.x += xVel*timeStep;
    	yVel -= 100*timeStep;
    	p.y -= yVel*timeStep;

    	firstStep = false;
    }
    
    public void updateConnections(ArrayList<Bond> bondList, int target)
    {
    	connectedTo.clear();
    	for (int u = 0; u < bondList.size(); u++)
    	{
    		if (bondList.get(u).getTargets()[0] == target)
    		{
    			connectedTo.add(bondList.get(u).getTargets()[1]);
    		}
    		if (bondList.get(u).getTargets()[1] == target)
    		{
    			connectedTo.add(bondList.get(u).getTargets()[0]);
    		}
    	}
    }
    
    public void draw(Graphics2D g)
    {
    	g.setColor(c);
    	
    	
    	g.fillOval((int)(p.x-m.getMinDist()), (int)(p.y-m.getMinDist()), (int)(2*m.getMinDist()), (int)(2*m.getMinDist()));
    	g.drawOval((int)(p.x-m.getMaxDist()), (int)(p.y-m.getMaxDist()), (int)(2*m.getMaxDist()), (int)(2*m.getMaxDist()));
    		
    	//draw line
    	if (!firstStep)
    	{
    		g.setColor(Color.BLACK);
    		g.drawLine((int)p.x, (int)p.y, (int)lP.x, (int)lP.y);
    	}
    	
    }
    
    //set methods
    
    public void setPosition(double newX, double newY)
    {
    	p.x = newX;
    	p.y = newY;
    }
    
    public void setVelocity(double newXVel, double newYVel)
    {
    	xVel = newXVel;
    	yVel = newYVel;
    }
    
    
    //also randomizes color
    public void setColor(Color newColor) 
    {
    	c = newColor;
	}
    
    public void setMaterial(Material newMaterial)
    {
    	m = newMaterial;
    }
    
    public void setActive(Boolean newActive)
    {
    	isActive = newActive;
    }
    
    
    //get methods
    
    //public double[] getPosition()
    //{
   // 	return new double[]{x, y};
   // }
    
    public double[] getVelocity()
    {
    	return new double[]{xVel, yVel};
    }
    
   
	public Material getMaterial()
	{
		return m;
	}
	
	public Point2D.Double getPoint()
	{
		return p;
	}
	
	public boolean isActive()
    {
    	return isActive;
    }
    
}

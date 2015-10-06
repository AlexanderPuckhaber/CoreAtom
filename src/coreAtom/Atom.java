package coreAtom;


import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

public class Atom {
	ArrayList<Integer> connectedTo = new ArrayList<Integer>();
	
	
	public Material m;
    
    private double x;
    private double y;
    
    private double xVel;
    private double yVel;
    
    private boolean inHardPoint = false;
    private boolean isActive = false;
    
    public boolean isRoad = false;
    
    
    private Color c = new Color(0, 0, 0);
    
    public Atom()
    {
    	x = 0;
    	y = 0;
    }
    
    public void move(double timeStep)
    {
    	//x += xVel*Math.pow(timeStep, 2);
    	//y -= yVel*Math.pow(timeStep, 2);
    	
    	
    	//if it's not in a hardpoint
    	if (!inHardPoint)
    	{
    		x += xVel*timeStep;
    		yVel -= 100*timeStep;
    		y -= yVel*timeStep;
    	}
    	
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
    	
    	//g.fillRect((int)(x-size/2), (int)(y-size/2), (int)size, (int)size);
    	//g.drawOval((int)(x-maxDist), (int)(y-maxDist), (int)(2*maxDist), (int)(2*maxDist));
    	if (isActive)
    	{
    		g.setColor(new Color(255, 0, 0));
    		g.fillOval((int)(x-m.getMaxDist()), (int)(y-m.getMaxDist()), (int)(2*m.getMaxDist()), (int)(2*m.getMaxDist()));
    		//g.fillOval((int)x, (int)y, 50, 50);
    		System.out.println(m.getMaxDist());
    	}
    	else
    	{
    		g.fillOval((int)(x-m.getMinDist()), (int)(y-m.getMinDist()), (int)(2*m.getMinDist()), (int)(2*m.getMinDist()));
    	}
    	
    	if (isRoad)
    	{
    		g.setColor(Color.black);
    		g.fillOval((int)(x-m.getMinDist())+5, (int)(y-m.getMinDist())+5, (int)(2*m.getMinDist()-10), (int)(2*m.getMinDist())-10);
    		g.setColor(c);
    	}
    	
    }
    
    //set methods
    
    public void setPosition(double newX, double newY)
    {
    	x = newX;
    	y = newY;
    }
    
    public void setVelocity(double newXVel, double newYVel)
    {
    	xVel = newXVel;
    	yVel = newYVel;
    	
    }
    
    public void setMaterial(Material newM)
    {
    	m = newM;
    }
    
    public void setInHardPoint()
    {
    	inHardPoint = true;
    }
    
    public void setRoad()
    {
    	isRoad = true;
    }
    
    public void setActive()
    {
    	isActive = true;
    }
    
    //also randomizes color
    public void setColor(Color newColor) 
    {
    	int r = newColor.getRed();
    	int g = newColor.getGreen();
    	int b = newColor.getBlue();
    	
    	Random rnd = new Random();
    	r+= rnd.nextInt(10)-5;
    	g+= rnd.nextInt(10)-5;
    	b+= rnd.nextInt(10)-5;
    	
    	if (r < 0)
    		r = 0;
    	if (g < 0)
    		g = 0;
    	if (b < 0)
    		b = 0;
    	
    	if (r > 255)
    		r = 255;
    	if (g > 255)
    		g = 255;
    	if (b > 255)
    		b = 255;
    	
    	c = new Color(r, g, b);
	}
    
    
    //get methods
    
    public double[] getPosition()
    {
    	return new double[]{x, y};
    }
    
    public double[] getVelocity()
    {
    	return new double[]{xVel, yVel};
    }
    
    public boolean getInHardPoint()
    {
    	return inHardPoint;
    }
    
    public boolean isRoad()
    {
    	return isRoad;
    }
    
    public boolean isActive()
    {
    	return isActive;
    }

	public Material getMaterial()
	{
		return m;
	}
    
}

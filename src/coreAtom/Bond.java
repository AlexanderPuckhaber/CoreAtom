package coreAtom;

import java.util.ArrayList;

public class Bond {
	private int a = 0, b = 0;
	public double length;
	public double lastLength;
	private double lastTimeStep;
	
	private double minDist;
	private double maxDist;
	
    private double tensileStrength;
    private double compressiveStrength;
    private double dampener;
    private double equilibrium;
    
	private boolean alive = true;
	private double force = 0;
	public boolean stick = true;
	
	//set methods
	
	public void setLength(double newLength)
	{
		lastLength = length;
		length = newLength;
	}
	
	public void setLastLength(double newLastLength)
	{
		lastLength = newLastLength;
	}
	
	public void setTargets(int newA, int newB)
	{
		a = newA;
		b = newB;
	}
	
	public void setForce(double newForce)
	{
		force = newForce;
	}
	
	public void setStick(boolean newStick)
	{
		stick = newStick;
	}
	
	
	public void getBondAvg(Atom c, Atom d)
	{
			
		minDist = c.m.getMinDist()+d.m.getMinDist();
		
		if (c.m.getMaxDist() > d.m.getMaxDist())
		{
			maxDist = c.m.getMaxDist();
		}
		else
		{
			maxDist = d.m.getMaxDist();
		}
		
		tensileStrength = (c.m.getTensileStrength()+d.m.getTensileStrength())/2;
		compressiveStrength = (c.m.getCompressiveStrength()+d.m.getCompressiveStrength())/2;
		dampener = (c.m.getDampener()+d.m.getDampener())/2;
		
		//sets equilibrium at halfway point
		equilibrium = (c.m.getEquilibrium()+d.m.getEquilibrium())/2;
		
	}
	
	//return methods
	
	public boolean getStatus()
	{
		return alive;
	}
	
	public int[] getTargets()
	{
		return new int[]{a, b};
	}
	
	public double getLength()
	{
		return length;
	}
	
	public double getLastLength()
	{
		return lastLength;
	}
	
	public double getLastTimeStep()
	{
		return lastTimeStep;
	}
	
	public double getForce()
	{
		return force;
	}
	
	public boolean getStick()
	{
		return stick;
	}
	
	//properties
	public double getMinDist()
    {
    	return minDist;
    }
    
    public double getMaxDist()
    {
    	return maxDist;
    }
    
    public double getTensileStrength()
    {
    	return tensileStrength;
    }

    public double getCompressiveStrength()
    {
    	return compressiveStrength;
    }
    
    public double getDampener()
    {
    	return dampener;
    }
    
    public double getEquilibrium()
    {
    	return equilibrium;
    }
}

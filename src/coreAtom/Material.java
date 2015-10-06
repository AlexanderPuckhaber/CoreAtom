package coreAtom;


import java.awt.Color;

public class Material {
	
private double mass;
	
	private double minDist;
	private double maxDist;
	
    private double tensileStrength;
    private double compressiveStrength;
    private double dampener;
    private double equilibrium;
    private double spacing;
    
    private double cost;
    
    private Color c;
	
	public Material ()
	{
		
	}
	
	//set methods
	public void setColor(Color newC)
    {
    	c = newC;
    }
    
    public void setMinDist(double newMinDist)
    {
    	minDist = newMinDist;
    }
    
    public void setMaxDist(double newMaxDist)
    {
    	maxDist = newMaxDist;
    }
    
    public void setTensileStrength(double newTensileStrength)
    {
    	tensileStrength = newTensileStrength;
    }

    public void setCompressiveStrength(double newCompressiveStrength)
    {
    	compressiveStrength = newCompressiveStrength;
    }
    
    public void setDampener(double newDampener)
    {
    	dampener = newDampener;
    }
    
    public void setMass(double newMass)
    {
    	mass = newMass;
    }
    
    public void setEquilibrium(double newEquilibrium)
    {
    	equilibrium = newEquilibrium;
    }
    
    public void setSpacing(double newSpacing)
    {
    	spacing = newSpacing;
    }
    
    public void setCost(double newCost)
    {
    	cost = newCost;
    }
    
    
    //get methods
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
    
    public double getMass()
    {
    	return mass;
    }
    
    public Color getColor()
    {
    	return c;
    }
    
    public double getEquilibrium()
    {
    	return equilibrium;
    }
    
    public double getSpacing()
    {
    	return spacing;
    }
    
    public double getCost()
    {
    	return cost;
    }

    
}


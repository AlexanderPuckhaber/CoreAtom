package coreAtom;


import java.util.ArrayList;

public class Force {

	public static void applyForce(ArrayList<Atom> a, Bond b, double timeStep)
	{
		Atom a1 = a.get(b.getTargets()[0]);
		Atom a2 = a.get(b.getTargets()[1]);
		
		double wF = 500;
		double sF = 50000;
		double moveForce = 4;
		
		b.setLength(Collider.getDist(a1, a2));
		
		b.getBondAvg(a1, a2);
		
		updateLength(b);
		double distance = b.getLength();
		double force = 0;
		
		double totalMass = a1.getMaterial().getMass()+a2.getMaterial().getMass();
		double a1massFraction = a1.getMaterial().getMass()/totalMass;
		double a2massFraction = a2.getMaterial().getMass()/totalMass;
		
		
		if (b.getStick())
		{
	        if (distance <= b.getMaxDist() && distance > b.getMinDist()){
	        	
	        	//dampener
	            force = (b.getLastLength()-b.getLength())*Math.pow(timeStep, -1);
	            
	            if (distance > b.getEquilibrium()){
	                force -= b.getTensileStrength()*wF*(Math.pow(distance-b.getEquilibrium(), 2));
	            }
	            else
	            {
	                force += b.getCompressiveStrength()*wF*(Math.pow(b.getEquilibrium()-distance, 2));
	            }
	          
	        }
	        else if (distance <=b.getMinDist()){
	        	force = sF*(Math.pow((b.getMinDist()-distance)/distance, 2));
	        }
		}
        else if (distance <=b.getMinDist())
        {
        	force = sF*(Math.pow((b.getMinDist()-distance)/distance, 2));
        }
        
        
		
		//force = 1;
		force *= timeStep;
		b.setForce(force);
		
		
		
		//"car" atom drives on road atoms
		if (a1.isActive() && a2.isRoad() && distance < b.getMaxDist())
		{
			//if the other is to the left and below
			if (a2.getPosition()[0] < a1.getPosition()[0] && a2.getPosition()[1] > a1.getPosition()[1])
			{
				//repel
				force += moveForce;
				
			}

		}
		else if (a2.isActive() && a1.isRoad() && distance < b.getMaxDist())
		{
			//if the other is to the left and below
			if (a1.getPosition()[0] < a2.getPosition()[0] && a1.getPosition()[1] > a2.getPosition()[1])
			{
				//repel
				force += moveForce;
				
			}
		}
		
		
			
		double dA1X = (getRotation(a1, a2)[0]*force*a2massFraction);
		double dA1Y = (-getRotation(a1, a2)[1]*force*a2massFraction);
		
		double dA2X = (-getRotation(a1, a2)[0]*force*a1massFraction);
		double dA2Y = (getRotation(a1, a2)[1]*force*a1massFraction);
		
		//if one is active and the other is road
		if ((a1.isActive() && a2.isRoad() || a2.isActive() && a1.isRoad()))
		{
			a2.setVelocity(a2.getVelocity()[0]+dA2X,  a2.getVelocity()[1]+dA2Y);
	    	a1.setVelocity(a1.getVelocity()[0]+dA1X,  a1.getVelocity()[1]+dA1Y);
		}
		//if one is active and the other isn't a road, do nothing
		else if (a1.isActive() || a2.isActive())
		{
			
		}
		//if neither are active, calculate the forces normally
		else
		{
			a2.setVelocity(a2.getVelocity()[0]+dA2X,  a2.getVelocity()[1]+dA2Y);
			a1.setVelocity(a1.getVelocity()[0]+dA1X,  a1.getVelocity()[1]+dA1Y);
		}
        
		
	}
	
	public static double[] getRotation (Atom a, Atom b){
	    double hypotenuse = Collider.getDist(a, b);
	    double xAngle = ((a.getPosition()[0]-b.getPosition()[0])/hypotenuse);
	    
	    double yAngle = ((a.getPosition()[1]-b.getPosition()[1])/hypotenuse);

	    return new double[]{xAngle, yAngle};
	}
	
	public static void updateLength(Bond b)
	{
		double distance = b.getLength();
		if (b.getLastLength() == 0)
		{
			b.setLastLength(distance);
		}
	}
	
	
}


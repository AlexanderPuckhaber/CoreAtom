package coreAtom;


import java.util.ArrayList;

public class Force {

	public static void applyForce(ArrayList<Atom> a, Bond b, double timeStep)
	{
		Atom a1 = a.get(b.getTargets()[0]);
		Atom a2 = a.get(b.getTargets()[1]);
		
		double wF = 50; 	//keeps bonds near equilibrium
		double sF = 100;	//pushes atoms apart if below min dist
		double moveForce = 4;
		double timeMultiplier = 0.8;
		
		b.setLength(Collider.getDist(a1, a2));
		
		b.getBondAvg(a1, a2);
		
		updateLength(b);
		double distance = b.getLength();
		double force = 0;
		
		//does mass
		double totalMass = a1.getMaterial().getMass()+a2.getMaterial().getMass();
		double a1massFraction = a1.getMaterial().getMass()/totalMass;
		double a2massFraction = a2.getMaterial().getMass()/totalMass;
		
		
		if (b.getStick())
		{
			//if the distance is between the max and min distances
	        if (distance <= b.getMaxDist() && distance > b.getMinDist())
	        {
	        	
	        	//dampener
	            force = (b.getLastLength()-b.getLength())*Math.pow(timeStep, -1);
	            
	            //if stretching, pull
	            if (distance > b.getEquilibrium())
	            {
	                force -= b.getTensileStrength()*wF*(Math.pow((distance-b.getEquilibrium())/(distance+1), 2));
	                
	                
	            }
	            else //if contracting, push
	            {
	                force += b.getCompressiveStrength()*wF*(Math.pow((b.getEquilibrium()-distance)/(distance+1), 2));
	              
	               
	            }
	          
	        }
	        //if the distance is close to minimum, push hard
	        else if (distance <=b.getMinDist())
	        {
	        	force = sF*(Math.pow((b.getMinDist()-distance)/(distance+1), 2));
	        	
	        	//if situation didn't improve last time
                if (distance < b.getLastLength())
                {
                	b.setHazardTime(b.getHazardTime()+timeStep*timeMultiplier);
                }
                else
                {
                	b.setHazardTime(0);
                }
	        }
		}
        else if (distance <=b.getMinDist())
        {
        	force = sF*(Math.pow((b.getMinDist()-distance)/(distance+1), 2));
        	
        	//if situation didn't improve last time
            if (distance < b.getLastLength())
            {
            	b.setHazardTime(b.getHazardTime()+timeStep*timeMultiplier);
            }
            else
            {
            	b.setHazardTime(0);
            }
        }
        
        
		
		//force = 1;
		force *= timeStep;
		force *= 1+b.getHazardTime();
		
		b.setForce(force);
		
		if (force > 0)
		System.out.println(force);
		
		
		//"car" atom drives on road atoms
		if (a1.isActive() && a2.isRoad() && distance < b.getMaxDist())
		{
			//if the other is to the left and below
			if (a2.getPoint().x < a1.getPoint().x && a2.getPoint().y > a1.getPoint().y)
			{
				//repel
				force += moveForce;
				
			}

		}
		else if (a2.isActive() && a1.isRoad() && distance < b.getMaxDist())
		{
			//if the other is to the left and below
			if (a1.getPoint().x < a2.getPoint().x && a1.getPoint().y > a2.getPoint().y)
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
	    double xAngle = ((a.getPoint().x-b.getPoint().x)/hypotenuse);
	    
	    double yAngle = ((a.getPoint().y-b.getPoint().y)/hypotenuse);

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


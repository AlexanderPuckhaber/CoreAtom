package coreAtom;


import java.util.ArrayList;

public class Force {

	public static void calculateForce(ArrayList<Atom> a, Bond b, double timeStep)
	{
		Atom a1 = a.get(b.getTargets()[0]);
		Atom a2 = a.get(b.getTargets()[1]);
		
		double wF = 100000; 	//keeps bonds near equilibrium
		double sF = 50000;	//pushes atoms apart if below min dist
		
		double timeMultiplier = 20.0;
		double dampenerMultiplier = .1;
		
		b.setLength(Collider.getDist(a1, a2));
		
		b.getBondAvg(a1, a2);
		
		updateLength(b);
		double distance = b.getLength();
		double force = 0;
		
		
		//dampener
        //force = dampenerMultiplier*(b.getLastLength()-b.getLength())*Math.pow(timeStep, -1);
		
		if (b.getStick())
		{
			//if the distance is between the max and min distances
	        if (distance <= b.getMaxDist() && distance > b.getMinDist())
	        {
	        	
	        	
	            
	            //if stretching, pull
	            if (distance > b.getEquilibrium())
	            {
	                force -= b.getTensileStrength()*wF*(Math.pow((distance-b.getEquilibrium())/(b.getEquilibrium()), 2));
	                
	                
	            }
	            else //if contracting, push
	            {
	                force += b.getCompressiveStrength()*wF*(Math.pow((b.getEquilibrium()-distance)/(b.getEquilibrium()), 2));
	              
	               
	            }
	          
	        }
	        //if the distance is close to minimum, push hard
	        else if (distance <=b.getMinDist())
	        {
	        	//force = sF*(Math.pow((b.getMinDist()-distance)/(distance+1), 2));
	        	force = sF*((b.getEquilibrium()-distance)/(b.getEquilibrium()));
	        	
	        	//if situation didn't improve last time
                if (distance < b.getLastLength())
                {
                	b.setHazardTime(b.getHazardTime()+timeStep*timeMultiplier);
                }
                else
                {
                	b.setHazardTime(0);
                }
                /*
                double estimatedDistChange = force*timeStep*timeStep*(1+b.getHazardTime());
                if (distance + estimatedDistChange > b.getEquilibrium())
                {
                	force *= 0.5;
                }
                */
	        }
		}
        else if (distance <=b.getMinDist())
        {
        	//force = sF*(Math.pow((b.getMinDist()-distance)/(distance+1), 2));
        	force = sF*((b.getEquilibrium()-distance)/(b.getEquilibrium()));
        	
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
		
		if (force > 0)
		{
		//dampener
			force += dampenerMultiplier*((b.getLastLength()-b.getLength())/b.getLength())*Math.pow(timeStep, -1);
			
		}
        
        
		
		//force = 1;
		force *= timeStep;
		force *= 1+b.getHazardTime();
		
		b.setForce(force);
		
		if (force > 0)
		System.out.println(force);
	}
	
	public static void applyForce(Bond b, ArrayList<Atom> a)
	{
		double force = b.getForce();
		double moveForce = 4;
		double distance = b.getLength();
		
		Atom a1 = a.get(b.getTargets()[0]);
		Atom a2 = a.get(b.getTargets()[1]);
		
		//does mass
				double totalMass = a1.getMaterial().getMass()+a2.getMaterial().getMass();
				double a1massFraction = a1.getMaterial().getMass()/totalMass;
				double a2massFraction = a2.getMaterial().getMass()/totalMass;
				
		
					
				double dA1X = (getRotation(a1, a2)[0]*force*a2massFraction);
				double dA1Y = (-getRotation(a1, a2)[1]*force*a2massFraction);
				
				double dA2X = (-getRotation(a1, a2)[0]*force*a1massFraction);
				double dA2Y = (getRotation(a1, a2)[1]*force*a1massFraction);
				
				//if both are active, do nothing
				if (a1.isActive() || a2.isActive())
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
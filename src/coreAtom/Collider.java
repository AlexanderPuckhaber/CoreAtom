package coreAtom;


import java.util.*;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.Math.*;

public class Collider {
	
	static boolean stick;
	static double maxBondLength;
	public static int bondsChecked, bondsMade;
	
	public Collider()
	{
		stick = true;
		maxBondLength = 100;
		bondsChecked = 0;
		bondsMade = 0;
	}
	
	
	public static void makeBonds(ArrayList<Atom> atomList, ArrayList<Bond> bondList, int aLoc, int bLoc)
	{
		Atom a = atomList.get(aLoc);
		Atom b = atomList.get(bLoc);
		
		if (aLoc != bLoc)
		{
			//rough distance
			if (getRoughDist(a, b) < maxBondLength)
			{
				Double dist = getDist(a, b);
				if (dist < maxBondLength)
				{
					boolean skip = false;
					//System.out.println(atomList.get(target).connectedTo.size());
					for (int i = 0; i < b.connectedTo.size(); i++)
					{
						if (b.connectedTo.get(i) == aLoc)
						{
							skip = true;
						}
					}
					if (!skip)
					{
						Bond newBond = new Bond();
						newBond.setTargets(aLoc, bLoc);
						
						newBond.getBondAvg(a, b);
						
						if (stick && dist <= newBond.getMaxDist())
						{
							newBond.setStick(true);
						}
						else
						{
							newBond.setStick(false);
						}
						
						bondList.add(newBond);
						bondsMade++;
						b.connectedTo.add(aLoc);
						a.connectedTo.add(bLoc);
					}
				}
	
			}
		}
		bondsChecked ++;
		
	}
	
	//not used
	public static void deleteExtraBonds(ArrayList<Bond> bondList, ArrayList<Integer> done)
	{
		
		for (int i = 0; i < bondList.size(); i++)
		{
			
			done.clear();
			
			for (int l = 0; l < bondList.size(); l++)
			{
				//if the first target is the same
				if (bondList.get(l).getTargets()[0] == i)
				{
					boolean skip = false;
					for (int y = 0; y < done.size(); y++)
					{
						//if the second target was already done, kill it
						if (bondList.get(l).getTargets()[1] == done.get(y))
						{
							bondList.remove(l);
							skip = true;
						}
					}
					//if the second target was not already done, add it to the done list
					if (!skip)
					{
						done.add(bondList.get(l).getTargets()[1]);
					}
				}
			}
		}	
	}
	
	public static void deleteFarBonds(ArrayList<Bond> bondList)
	{
		for (int k = 0; k < bondList.size(); k++)
		{
			if (bondList.get(k).getLength() >= maxBondLength)
			{
				bondList.remove(k);
			}	
		}
	}
	
	public static void ground(double y, ArrayList<Atom> atomList)
	{
		for (int i = 0; i < atomList.size(); i++)
		{
			if (atomList.get(i).getPoint().y > y-atomList.get(i).m.getMinDist())
			{
				atomList.get(i).setPosition(atomList.get(i).getPoint().x, y-atomList.get(i).m.getMinDist());
				atomList.get(i).setVelocity(atomList.get(i).getVelocity()[0]*0.5, atomList.get(i).getVelocity()[1]*0.5);
			}
		}
	}
	
	public static void doBorder(Rectangle r, ArrayList<Atom> atomList)
	{
		
		int top = r.y;
		int bottom = top+r.height;
		int left = r.x;
		int right = left + r.width;
		double amt = 0.4;
		
		for (int i = 0; i < atomList.size(); i++)
		{
			if (atomList.get(i).getPoint().y > bottom-atomList.get(i).m.getMinDist())
			{
				atomList.get(i).setPosition(atomList.get(i).getPoint().x, bottom-atomList.get(i).m.getMinDist());
				atomList.get(i).setVelocity(atomList.get(i).getVelocity()[0]*amt, atomList.get(i).getVelocity()[1]*-amt);
			}
			else if (atomList.get(i).getPoint().y < top+atomList.get(i).m.getMinDist())
			{
				atomList.get(i).setPosition(atomList.get(i).getPoint().x, top+atomList.get(i).m.getMinDist());
				atomList.get(i).setVelocity(atomList.get(i).getVelocity()[0]*amt, atomList.get(i).getVelocity()[1]*-amt);
			}
			
			if (atomList.get(i).getPoint().x < left+atomList.get(i).m.getMinDist())
			{
				
				atomList.get(i).setPosition(left+atomList.get(i).m.getMinDist(), atomList.get(i).getPoint().y);
				atomList.get(i).setVelocity(atomList.get(i).getVelocity()[0]*-amt, atomList.get(i).getVelocity()[1]*amt);
			}
			else if (atomList.get(i).getPoint().x > right-atomList.get(i).m.getMinDist())
			{
				atomList.get(i).setPosition(right-atomList.get(i).m.getMinDist(), atomList.get(i).getPoint().y);
				atomList.get(i).setVelocity(atomList.get(i).getVelocity()[0]*-amt, atomList.get(i).getVelocity()[1]*amt);
			}
		}
	}
	
	public static void stickToRectangles(Atom a, ArrayList<Rectangle2D.Double> rList)
	{
		Point2D.Double p = a.getPoint();
		
		for (int i = 0; i < rList.size(); i++)
		{
			if (rList.get(i).contains(p) && !a.isActive())
			{
				a.setInHardPoint();
				a.setVelocity(0, 0);
			}
		}
	}
	
	public static void removeOverlappers(ArrayList<Atom> atomList, double minSpace)
	{
		int size = atomList.size();
		
		for (int i = 0; i < size; i++)
		{
			for (int p = 0; p < size; p++)
			{

				double dist = 1000;
				if (i!= p && i < atomList.size() && p < atomList.size())
				{
					dist = Collider.getDist(atomList.get(i), atomList.get(p));
				}
				if (dist < minSpace)
				{
					atomList.remove(i);
					p = size+1;
				}
		   
			}
		}
	}
	
	public static void removeAtomsOfMaterial(ArrayList<Atom> atomList, Material m)
	{
		for (int i = 0; i < atomList.size(); i++)
		{
			if (atomList.get(i).getMaterial() == m)
			{
				atomList.remove(i);
			}
		}
	}
	
	
	public static double getDist(Atom a, Atom b)
	{
		double xDist = a.getPoint().x-b.getPoint().x;
		double yDist = a.getPoint().y-b.getPoint().y;
		
		double dist = Math.sqrt((xDist * xDist) + (yDist * yDist));
		return dist;
	}
	
	public static double getRoughDist(Atom a, Atom b)
	{
		double xDist = a.getPoint().x-b.getPoint().x;
		double yDist = a.getPoint().y-b.getPoint().y;
		
		return xDist+yDist;
	}
	
	
}

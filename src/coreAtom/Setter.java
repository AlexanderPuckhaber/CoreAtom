package coreAtom;


import java.lang.Math.*;
import java.util.ArrayList;

public class Setter {
	
	
	public static void setRect(ArrayList<Atom> atomList, int start, int end, double x, double y, double len, double space)
	{
		int total = end-start;
		int row = 0;
		int col = 0;
		
		for (int i = start; i < total+start; i++)
		{
			if (col > len)
			{
				col = 0;
				row ++;
			}
			atomList.get(i).setPosition(x+col*space+space*0.5*(row%2), y+row*(space/2)*Math.sqrt(3));
			col += 1;
		}
	}
	
	public static void setLine(ArrayList<Atom> atomList, int start, int end, double x, double y, double endX, double endY, double dist, double space)
	{
		
		int total = end-start;
		
		double tmpX = x;
		double tmpY = y;
		
		double xDist = x-endX;
		double yDist = y-endY;
		
		for (int i = start; i < end; i++)
		{
			atomList.get(i).setPosition(tmpX, tmpY);
			System.out.println("put atom in "+tmpX+" "+tmpY);
			
			tmpX -= xDist/total;
			tmpY -= yDist/total;
		}
	}
	
	public static void init(ArrayList<Atom> atomList, int amount, Material m)
	{
		int start = atomList.size();
		for (int i = start; i < start+amount; i++)
		{
			Atom newAtom = new Atom();
			newAtom.setMaterial(m);
			newAtom.setColor(m.getColor());
			
			atomList.add(newAtom);
		}
	}
	
	public static void setVel(ArrayList<Atom> atomList, int start, int end, double newXVel, double newYVel)
	{
		for (int i = start; i < end; i++)
		{
			atomList.get(i).setVelocity(newXVel, newYVel);
		}
	}
	
}

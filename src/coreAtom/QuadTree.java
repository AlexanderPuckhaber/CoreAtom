package coreAtom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class QuadTree 
{
	private double x, y, length, height;
	public Rectangle2D.Double r, bufferRect;
	Boolean hasChildren;
	QuadTree nw, ne, sw, se;
	ArrayList<Integer> aList;
	ArrayList<Integer> edgeList;
	ArrayList<Point2D.Double> pList;
	ArrayList<Integer> bList;
	public int index, buffer;
	double maxSpeed;
	double time, oldTime;
	double recalcTime;
	
	//quadtree constructor
	public QuadTree(double newX, double newY, double newLength, double newHeight)
	{
		x = newX;
		y = newY;
		length = newLength;
		height = newHeight;
		r = new Rectangle2D.Double();
		r.setRect(x, y, length, height);
		hasChildren = false;
		aList = new ArrayList<Integer>();
		pList = new ArrayList<Point2D.Double>();
		edgeList = new ArrayList<Integer>();
		bList = new ArrayList<Integer>();

		index = 1;
		buffer = 100;
		bufferRect = new Rectangle2D.Double(x-buffer, y-buffer, length+buffer*2, height+buffer*2);
		maxSpeed = 0;
		time = System.nanoTime()/1000/1000/1000;
		oldTime = time;
		recalcTime = 0.01;
	}
	
	public void setIndex(int newIndex)
	{
		index = newIndex;
	}
	
	//splits the quadtree by creating more objects
	public void split()
	{
		double subL = length/2;
		double subH = height/2;
		nw = new QuadTree(x, y, subL, subH);
		ne = new QuadTree(x+subL, y, subL, subH);
		sw = new QuadTree(x, y+subH, subL, subH);
		se = new QuadTree(x+subL, y+subH, subL, subH);
		hasChildren = true;
	}
	
	//NOT CURRENTLY WORKING :(
	//goes through all the quads that intersect its buffer rectangle (larger by int buffer on all sides, returns a list of the atoms
	public ArrayList<Integer> getWithinBounds(ArrayList<Integer> tmpList, ArrayList<Atom> atomList, Rectangle2D.Double bR)
	{
		ArrayList<Integer> nullList = new ArrayList<Integer>();
		//tmpList.clear(); don't
		if (r.intersects(bR))
		{
			if (hasChildren)
			{
				nullList.addAll(nw.getWithinBounds(nullList, atomList, bR));
				nullList.addAll(ne.getWithinBounds(nullList, atomList, bR));
				nullList.addAll(sw.getWithinBounds(nullList, atomList, bR));
				nullList.addAll(se.getWithinBounds(nullList, atomList, bR));
			}
			else
			{
				//tmpList.addAll(aList);
				for (int i = 0; i < edgeList.size(); i++)
				{
					Point2D.Double p = new Point2D.Double();
					p.setLocation(atomList.get(edgeList.get(i)).getPoint().x, atomList.get(edgeList.get(i)).getPoint().y);
					
					if (bR.contains(p))
					{
						nullList.add(edgeList.get(i));
					}
				}
				//System.out.println("Adding "+aList.size()+" now "+tmpList.size());
			}
		}
		return nullList;
	}
	

	
	//puts atoms in quads.  If the quads have too many atoms, they split
	public void addAtoms(ArrayList<Atom> atomList, ArrayList<Integer> newAList, int maxAmount, int minSize)
	{
		//clears lists
		pList.clear();
		aList.clear();
		edgeList.clear();
		
		Point2D.Double p = new Point2D.Double();
		
		//if it is root
		if (index < 0)
		{
			for (int i = 0; i < atomList.size(); i++)
			{
				p.setLocation(atomList.get(i).getPoint().x, atomList.get(i).getPoint().y);
				
				if (r.contains(p))
				{
					pList.add(p);
					aList.add(i);
					//this checks if the atoms will be childrens' in the child's rectangles
					if (hasChildren)
					{
						if (isOutlier(atomList, i, 50, nw.r)
								|| isOutlier(atomList, i, 50, ne.r)
								|| isOutlier(atomList, i, 50, sw.r)
								|| isOutlier(atomList, i, 50, se.r))
						{
							edgeList.add(i);
						}
					}
					if (isOutlier(atomList, i, 50, r))
					{
						edgeList.add(i);
					}
				}
			}
		}
		else {
			for (int g = 0; g < newAList.size(); g++)
			{
				p.setLocation(atomList.get(newAList.get(g)).getPoint().x, atomList.get(newAList.get(g)).getPoint().y);
				
				if (r.contains(p))
				{
					pList.add(p);
					aList.add(newAList.get(g));
					//this checks if the atoms will be outliers in the childrens' rectangles
					if (hasChildren)
					{
						if (isOutlier(atomList, newAList.get(g), 50, nw.r)
								|| isOutlier(atomList, newAList.get(g), 50, ne.r)
								|| isOutlier(atomList, newAList.get(g), 50, sw.r)
								|| isOutlier(atomList, newAList.get(g), 50, se.r))
						{
							edgeList.add(newAList.get(g));
						}
					}
					if (isOutlier(atomList, newAList.get(g), 50, r))
					{
						edgeList.add(newAList.get(g));

					}
				}
			}	
		}
		
		//if there are too many
		if (aList.size() >= maxAmount)
		{
			//if it can be split
			if (length >= minSize && height >= minSize)
			{
				if (hasChildren)
				{
					nw.addAtoms(atomList, aList, maxAmount, minSize);
					ne.addAtoms(atomList, aList, maxAmount, minSize);
					sw.addAtoms(atomList, aList, maxAmount, minSize);
					se.addAtoms(atomList, aList, maxAmount, minSize);
				}
				else
				{
					split();
					System.out.println("splitting");
					
					//THIS THING RIGHT HERE IS EVIL.
					//Explanation: if this isn't there, then the quadtree only subdivides once per tick
					//See above if; if it has children, it has them check for subdivisions.  If it doesn't it makes children
					//So, the children had to wait until the next time the while loop ran before their parent's hasChildren was true
					//This makes the bonds break.  Don't believe me?  Take this code out, then!
					//I can't believe I found that this was the thing that broke the bonds...
					nw.addAtoms(atomList, aList, maxAmount, minSize);
					ne.addAtoms(atomList, aList, maxAmount, minSize);
					sw.addAtoms(atomList, aList, maxAmount, minSize);
					se.addAtoms(atomList, aList, maxAmount, minSize);
					//END OF EVIL CODE
				}
			}
		}
		else
		{
			if (hasChildren)
			{
				hasChildren = false;
			}
		}
		//System.out.println(inList.size());
	}
	
	//IDEA FOUND ON THE INTERNETS: only put the atom in the child node if it can fully fit.  Otherwise, put it in the parent node.  I'm thinking radius=50
	//but this thing just deletes the atoms close to the edge, so they don't get passed on to the children.
	public boolean isOutlier(ArrayList<Atom> atomList, int y, double dist, Rectangle2D.Double cR)
	{
		//this uses a rectangle for simplicity
		Point2D.Double p;
		double aX, aY;
		
		//makes rectangle smaller by the distance
		cR = new Rectangle2D.Double(cR.x+dist, cR.y+dist, cR.width-dist, cR.height-dist);
		
		aX = atomList.get(y).getPoint().x;
		aY = atomList.get(y).getPoint().y;
		p = new Point2D.Double(aX, aY);
		
		//if the point is completely inside, do nothing
		if (cR.contains(p))
		{
			//atomList.get(y).setColor(new Color(0, 255, 0));
			return false;
		}
		//if it is outside, return false
		else
		{
			//atomList.get(y).setColor(new Color(255, 0, 0));
			return true;
		}
		
	}
	
	
	public void collideAtoms (QuadTree tree, ArrayList<Atom> atomList, ArrayList<Bond> bondList)
	{
		
		
		//if it has children and it's children don't have children
		if (hasChildren)
		{
			
			if (!nw.hasChildren || !ne.hasChildren || !sw.hasChildren || !se.hasChildren)
			{
				for (int x = 0; x < edgeList.size(); x++)
				{
					for (int y = 0; y < edgeList.size(); y++)
					{
						Collider.makeBonds(atomList, bondList, edgeList.get(x), edgeList.get(y));
					}
				}
			}
			

			nw.collideAtoms(tree, atomList, bondList);
			ne.collideAtoms(tree, atomList, bondList);
			sw.collideAtoms(tree, atomList, bondList);
			se.collideAtoms(tree, atomList, bondList);
		}
		
		//if there are no children, do the in list
		else
		{
			//System.out.println(inList.size());
			
			for (int x = 0; x < aList.size(); x++)
			{
				for (int y = x; y < aList.size(); y++)
				{
					if (aList.get(x) != aList.get(y))
					{
						Collider.makeBonds(atomList, bondList, aList.get(x), aList.get(y));
					}
				}
			}
		}
	}
	
	public void addBonds(ArrayList<Atom> atomList, ArrayList<Bond> bondList, ArrayList<Integer> newBList)
	{
		Atom a;
		Atom b;
		
		Point2D.Double p1, p2;
		
		ArrayList<Integer> tmpList = new ArrayList<Integer>();
		bList.clear();
		
		//if it is root
		if (index == -1)
		{
			//sets atom a and b
			for (int i = 0; i < bondList.size(); i++)
			{
				a = atomList.get(bondList.get(i).getTargets()[0]);
				b = atomList.get(bondList.get(i).getTargets()[1]);
				
				p1 = new Point2D.Double(a.getPoint().x, a.getPoint().y);
				p2 = new Point2D.Double(b.getPoint().x, b.getPoint().y);
				
				if (r.contains(p1) && r.contains(p2))
				{
					//check if it can be contained by the children
					if (hasChildren)
					{
						if ((nw.r.contains(p1) && nw.r.contains(p2)) || (ne.r.contains(p1) && ne.r.contains(p2))
								|| (sw.r.contains(p1) && sw.r.contains(p2)) || (se.r.contains(p1) && se.r.contains(p2)))
						{
							tmpList.add(i);
						}
						else
						{
							bList.add(i);
						}
					}
					else if (i < newBList.size())
					{
						bList.add(newBList.get(i));
					}
				}
			}
		}
		//if it is NOT root
		else
		{
			for (int i = 0; i < newBList.size(); i++)
			{
				a = atomList.get(bondList.get(newBList.get(i)).getTargets()[0]);
				b = atomList.get(bondList.get(newBList.get(i)).getTargets()[1]);
				
				p1 = new Point2D.Double(a.getPoint().x, a.getPoint().y);
				p2 = new Point2D.Double(b.getPoint().x, b.getPoint().y);
				
				if (r.contains(p1) && r.contains(p2))
				{
					//check if it can be contained by the children
					if (hasChildren)
					{
						if ((nw.r.contains(p1) && nw.r.contains(p2)) || (ne.r.contains(p1) && ne.r.contains(p2))
								|| (sw.r.contains(p1) && sw.r.contains(p2)) || (se.r.contains(p1) && se.r.contains(p2)))
						{
							tmpList.add(newBList.get(i));
						}
						else
						{
							bList.add(newBList.get(i));
						}
					}
					else
					{
						bList.add(newBList.get(i));
					}
				}
			}
		}
		
		if (hasChildren)
		{
			tmpList.addAll(bList);
			nw.addBonds(atomList, bondList, tmpList);
			ne.addBonds(atomList, bondList, tmpList);
			sw.addBonds(atomList, bondList, tmpList);
			se.addBonds(atomList, bondList, tmpList);
		}
			
	}
	
	public void DoForce(ArrayList<Atom> atomList, ArrayList<Bond> bondList)
	{
		//System.out.println(bList.size());
		//System.out.println("bonds: "+bondList.size());
		
		time = System.nanoTime();
		time = time*0.001;
		time = time*0.001;
		time = time*0.001;

		//System.out.println("time "+time);
		//System.out.println(time-oldTime);
		if (time-oldTime > recalcTime)
		{
			
		
			for (int i = 0; i < bList.size(); i++)
			{
				Bond b = bondList.get(bList.get(i));
				Force.applyForce(atomList, b, run.timeStep);
			}
			
			
			if (hasChildren)
			{
				nw.DoForce(atomList, bondList);
				ne.DoForce(atomList, bondList);
				sw.DoForce(atomList, bondList);
				se.DoForce(atomList, bondList);
			}
			
			oldTime = time;
		}
	}
	
	public void updateMaxSpeed(ArrayList<Atom> atomList)
	{
		//gets max speed
		maxSpeed = 0;
		for (int z = 0; z < aList.size(); z++)
		{
			double newMaxSpeed = Math.sqrt(Math.pow(atomList.get(aList.get(z)).getVelocity()[0], 2)+Math.pow(atomList.get(aList.get(z)).getVelocity()[1], 2));
			if (newMaxSpeed > maxSpeed)
			{
				maxSpeed = newMaxSpeed;
			}
		}
		
		//also updates recalc time
		//System.out.println("sped: "+maxSpeed);
		recalcTime = 0.5/maxSpeed;
		
		if (hasChildren)
		{
			nw.updateMaxSpeed(atomList);
			ne.updateMaxSpeed(atomList);
			sw.updateMaxSpeed(atomList);
			se.updateMaxSpeed(atomList);
		}
	}
	
	public ArrayList<Integer> addEdgeToBigList(ArrayList<Integer> tmpList)
	{
		ArrayList<Integer> nullList = new ArrayList<Integer>();
		
		
		if (hasChildren)
		{
			tmpList.addAll(nw.addEdgeToBigList(nullList));
			tmpList.addAll(ne.addEdgeToBigList(nullList));
			tmpList.addAll(sw.addEdgeToBigList(nullList));
			tmpList.addAll(se.addEdgeToBigList(nullList));
		}
		else
		{
			nullList.addAll(edgeList);
			System.out.println("add "+tmpList.size());
		}
		return nullList;
	}
	
	public void drawQuadWithPoint(Graphics2D g, Point2D.Double p)
	{
		if (r.contains(p))
		{
			if (hasChildren)
			{
				ne.drawQuadWithPoint(g, p);
				nw.drawQuadWithPoint(g, p);
				se.drawQuadWithPoint(g, p);
				sw.drawQuadWithPoint(g, p);
			}
			else
			{
				this.draw(g, Color.orange);
			}
		}
	}
	
	public void findQuadWithPoint(QuadTree q, Point2D.Double p)
	{
		if (r.contains(p))
		{
			if (hasChildren)
			{
				ne.findQuadWithPoint(q, p);
				nw.findQuadWithPoint(q, p);
				se.findQuadWithPoint(q, p);
				sw.findQuadWithPoint(q, p);
			}
			else
			{
				q = this;
			}
		}

	}
	
	public void highlightAtom(Graphics2D g, int mX, int mY, Boolean clicked)
	{
		
		Point2D.Double p = new Point2D.Double(mX, mY);
		drawQuadWithPoint(g, p);
		
		QuadTree qua = new QuadTree(-1, -1, -1, -1);
		findQuadWithPoint(qua, p);
		
		ArrayList<Integer> tmpList = new ArrayList<Integer>();
		tmpList = qua.aList;
		
		for (int i = 0; i < tmpList.size(); i++)
		{
			//Atom a = 
		}
	}
	
	//draws it (mostly for debug)
	public void draw(Graphics2D g, Color c)
	{
		g.setColor(c);
		
		if (hasChildren)
		{
			
			//g.drawString(Integer.toString((int)maxSpeed), (int)(x+length/3), (int)(y+height/3-40));
			
			if (!nw.hasChildren && !ne.hasChildren && !sw.hasChildren && !se.hasChildren)
			{
				
				//g.setColor(new Color(0, 0, 0));	//black
				//g.drawString(Integer.toString(edgeList.size()), (int)(x+length/3), (int)(y+height/3-20));
				//g.setColor(new Color(255, 255, 0, 200));
			}
			
		}
		g.drawRect((int)x, (int)y, (int)length, (int)height);
		
		
		
		
		/*
		g.setColor(new Color(255, 0, 0));	//red
		if (hasChildren)
		{
			if (!nw.hasChildren)
			{
				g.drawString(Integer.toString(edgeList.size()), (int)(x+length/3), (int)(y+height/3));
			}
		}
		g.setColor(new Color(255, 150, 0));	//orange
		//g.drawString(Integer.toString(edgeListCompareSize), (int)(x+length/3), (int)(y+height/3+20));
		*/
		
		
		if (hasChildren)
		{
			nw.draw(g, c);
			ne.draw(g, c);
			sw.draw(g, c);
			se.draw(g, c);
		}
	}
	
	public void debugDraw(Graphics2D g, ArrayList<Atom> atomList)
	{
		
		
		for (int p = 0; p < edgeList.size(); p++)
		{
			//atomList.get(edgeList.get(p)).setColor(new Color(0, 0, 255));
			atomList.get(edgeList.get(p)).draw(g);
		}
		
		if (hasChildren)
		{
			nw.debugDraw(g, atomList);
			ne.debugDraw(g, atomList);
			sw.debugDraw(g, atomList);
			se.debugDraw(g, atomList);
		}
	}
	
}

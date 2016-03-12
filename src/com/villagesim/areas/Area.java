package com.villagesim.areas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.villagesim.Const;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Updateable;
import com.villagesim.resources.Resource;

public abstract class Area implements Drawable, Updateable {
	
	private int width;
	private int height;
	private Point2D coordinate;
	private Color color;
	private Set<Resource> resourceSet = new HashSet<Resource>();
	
	public Area(Color color, int width, int height)
	{
		this.color = color;
		this.width = width;
		this.height = height;
		this.coordinate = generateCoordinate();
		
		populateResourceSet();
	}
	
	@Override
	public void draw(Graphics bbg)
	{
		bbg.setColor(color);
		bbg.fillRect((int)(coordinate.getX()+0.5), (int)(coordinate.getY()+0.5), width, height);
		// TODO change appearance depending on content
	}
	
	@Override
	public void update(int seconds) 
	{
		for(Iterator<Resource> i = resourceSet.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			item.update(seconds);
		}
	}
	
	protected abstract void populateResourceSet();

	public Set<Resource> getResourceSet() {
		return resourceSet;
	}

	public void setResourceSet(Set<Resource> resourceSet) {
		this.resourceSet = resourceSet;
	}
	
	public boolean containsResource(Class<? extends Resource> resourceClass)
	{
		for(Iterator<Resource> i = resourceSet.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			
			if(item.getClass().equals(resourceClass.getClass()))
			{
				return true;
			}
		}
		return false;
	}
	
	public double getSize()
	{
		return width*height;
	}
	
	public Point2D getCoordinate()
	{
		return coordinate;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	private Point2D generateCoordinate()
	{
		Point2D coord = new Point2D.Double();
		Random rand = new Random();
		
		int x = rand.nextInt(Const.WINDOW_WIDTH-width);
		int y = rand.nextInt(Const.WINDOW_HEIGHT-height);
		coord.setLocation(x, y);
		return coord;
	}
	
}

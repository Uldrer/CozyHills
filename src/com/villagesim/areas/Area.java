package com.villagesim.areas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.villagesim.Const;
import com.villagesim.interfaces.DepletedListener;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Updateable;
import com.villagesim.resources.Resource;

public abstract class Area implements Drawable, Updateable, DepletedListener {
	
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
	
	public void reset()
	{
		for(Iterator<Resource> i = resourceSet.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			item.reset();
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
			
			if(resourceClass.isAssignableFrom(item.getClass()))
			{
				return true;
			}
		}
		return false;
	}
	
	public double getResourceAquaValue(Class<? extends Resource> resourceClass)
	{
		return getResourceValue(resourceClass, false, false);
	}
	
	private double getResourceValue(Class<? extends Resource> resourceClass, boolean nutrition, boolean useAmount)
	{
		double value = 0;
		for(Iterator<Resource> i = resourceSet.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			
			if(resourceClass.isAssignableFrom(item.getClass()))
			{
				if(useAmount)
				{
					value += item.getAmount();
				}
				else
				{
					if(nutrition)
					{
						value += item.getAmount()*item.getNutritionPerAmount();
					}
					else
					{
						value += item.getAmount()*item.getAquaPerAmount();
					}
				}
				
			}
		}
		return value;
	}
	
	public void fillResourceAquaValue(Class<? extends Resource> resourceClass, double value)
	{
		fillResourceValue(resourceClass, value, false, false);
	}
	
	public void consumeResourceAquaValue(Class<? extends Resource> resourceClass, double value)
	{
		consumeResourceValue(resourceClass, value, false, false);
	}
	
	public double getResourceNutritionValue(Class<? extends Resource> resourceClass)
	{
		return getResourceValue(resourceClass, true, false);
	}
	
	public double getResourceAmountValue(Class<? extends Resource> resourceClass)
	{
		return getResourceValue(resourceClass, true, true); // the same as false, true
	}
	
	public void consumeResourceNutritionValue(Class<? extends Resource> resourceClass, double value)
	{
		consumeResourceValue(resourceClass, value, true, false);
	}
	
	public void consumeResourceAmountValue(Class<? extends Resource> resourceClass, double value)
	{
		consumeResourceValue(resourceClass, value, true, true); // the same as false, true
	}
	
	private void consumeResourceValue(Class<? extends Resource> resourceClass, double value, boolean nutrition, boolean useAmount)
	{
		double valueLeftToConsume = value;
		for(Iterator<Resource> i = resourceSet.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			
			if(resourceClass.isAssignableFrom(item.getClass()))
			{
				// Consume until done
				double amount;
				if(useAmount)
				{
					amount = valueLeftToConsume;
				}
				else
				{
					if(nutrition)
					{
						amount = valueLeftToConsume/item.getNutritionPerAmount();
					}
					else
					{
						amount = valueLeftToConsume/item.getAquaPerAmount();
					}
				}
				
				// If successful return
				if(item.consume(amount)) return;
				
				// If not successful we need to remove partly
				if(amount > item.getAmount())
				{
					boolean res = item.consume(item.getAmount());
					if(res)
					{
						if(useAmount)
						{
							valueLeftToConsume -= item.getAmount();
						}
						else
						{
							if(nutrition)
							{
								valueLeftToConsume -= item.getAmount()*item.getNutritionPerAmount();
							}
							else
							{
								valueLeftToConsume -= item.getAmount()*item.getAquaPerAmount();
							}
						}
					}
					else 
					{
						if(useAmount)
						{
							throw new AssertionError("AMOUNT: Should be possible to remove what there is.");
						}
						else
						{
							if(nutrition)
								throw new AssertionError("NUTRITION: Should be possible to remove what there is.");
							else
								throw new AssertionError("AQUA: Should be possible to remove what there is.");
						}
					}
				}
				else
				{
					if(useAmount)
					{
						throw new AssertionError("AMOUNT: Should have been greater than available. Something is wrong.");
					}
					else
					{
						if(nutrition)
							throw new AssertionError("NUTRITION: Should have been greater than available. Something is wrong.");
						else
							throw new AssertionError("AQUA: Should have been greater than available. Something is wrong.");
					}
				}
			}
		}
	}
	
	public void fillResourceNutritionValue(Class<? extends Resource> resourceClass, double value)
	{
		fillResourceValue(resourceClass, value, true, false);
	}
	
	public void fillResourceAmountValue(Class<? extends Resource> resourceClass, double value)
	{
		fillResourceValue(resourceClass, value, true, true); // the same as false, true
	}
	
	private void fillResourceValue(Class<? extends Resource> resourceClass, double value, boolean nutrition, boolean useAmount)
	{
		double valueToFill = value;
		for(Iterator<Resource> i = resourceSet.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			
			if(resourceClass.isAssignableFrom(item.getClass()))
			{
				double amount;
				if(useAmount)
				{
					amount= valueToFill;
				}
				else
				{
					if(nutrition)
					{
						amount= valueToFill/item.getNutritionPerAmount();
					}
					else
					{
						amount= valueToFill/item.getAquaPerAmount();
					}
				}
				
				item.fill(amount);
				return;
			}
		}
	}
	
	public void setColor(Color color)
	{
		this.color = color;
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

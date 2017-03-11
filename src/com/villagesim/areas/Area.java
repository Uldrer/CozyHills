package com.villagesim.areas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	private List<Resource> resourceList = new ArrayList<Resource>();
	private boolean restrictionActive = false;
	private double restrictionWeightLimit = 0;
	private boolean resourceLimitReached = false;
	private int id; // unique area id
	private Map<Class<? extends Resource>, Boolean> containsMap = new HashMap<Class<? extends Resource>, Boolean>();
	
	private static int id_counter = 0;
	
	public Area(Color color, int width, int height)
	{
		this.color = color;
		this.width = width;
		this.height = height;
		this.coordinate = generateCoordinate();
		id = ++id_counter;
	}
	
	public void reGenerateCoordinate()
	{
		this.coordinate = generateCoordinate();
	}
	
	public boolean overlaps(Area other)
	{
		double x_other_min = other.getCoordinate().getX() - other.width;
		double x_other_max = other.getCoordinate().getX() + other.width;
		double x_this_min = this.coordinate.getX() - this.width;
		double x_this_max = this.coordinate.getX() + this.width;
		
		// No overlap in x so no overlap
		if(x_other_max < x_this_min) return false;
		if(x_other_min > x_this_max) return false;
		
		double y_other_min = other.getCoordinate().getY() - other.height;
		double y_other_max = other.getCoordinate().getY() + other.height;
		double y_this_min = this.coordinate.getY() - this.height;
		double y_this_max = this.coordinate.getY() + this.height;
		
		// No overlap in y so no overlap
		if(y_other_max < y_this_min) return false;
		if(y_other_min > y_this_max) return false;
		
		// Else we have overlap
		return true;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setRestrictionActive(boolean active)
	{
		restrictionActive = active;
	}
	
	public void setRestrictionWeightLimit(double weightLimit)
	{
		restrictionWeightLimit = weightLimit;
	}
	
	@Override
	public void draw(Graphics bbg)
	{
		bbg.setColor(color);
		double x_mid = coordinate.getX() - width/2;
		double y_mid = coordinate.getY() - height/2;
		bbg.fillRect((int)(x_mid+0.5), (int)(y_mid+0.5), width, height);
		// TODO change appearance depending on content
	}
	
	@Override
	public void update(int seconds) 
	{
		double weight = 0;
		for(Iterator<Resource> i = resourceList.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			item.update(seconds);
			weight += item.getAmount()*item.getWeightPerAmount();
		}
		
		if(restrictionActive && weight > restrictionWeightLimit)
		{
			resourceLimitReached = true;
		}
		else 
		{
			resourceLimitReached = false;
		}
	}
	
	public void reset()
	{
		for(Iterator<Resource> i = resourceList.iterator(); i.hasNext(); ) 
		{
			Resource item = i.next();
			item.reset();
		}
		resourceLimitReached = false;
	}
	
	protected abstract void populateResourceList();

	public List<Resource> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<Resource> resourceList) {
		this.resourceList = resourceList;
	}
	
	public boolean containsResource(Class<? extends Resource> resourceClass)
	{
		if(!containsMap.containsKey(resourceClass))
		{
			boolean containsResource = containsResourceSlow(resourceClass);
			containsMap.put(resourceClass, containsResource);
			return containsResource;
		}
		return containsMap.get(resourceClass);
	}
	
	private boolean containsResourceSlow(Class<? extends Resource> resourceClass)
	{
		for(Resource item : resourceList)
		{
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
		for(Iterator<Resource> i = resourceList.iterator(); i.hasNext(); ) 
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
		for(Iterator<Resource> i = resourceList.iterator(); i.hasNext(); ) 
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
		// Don't fill if limit is reached
		if(resourceLimitReached) return;
		
		double valueToFill = value;
		for(Iterator<Resource> i = resourceList.iterator(); i.hasNext(); ) 
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

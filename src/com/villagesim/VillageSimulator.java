package com.villagesim;

import java.awt.Graphics;
import java.util.*;

import com.villagesim.areas.Lake;
import com.villagesim.areas.Wood;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Updateable;
import com.villagesim.people.Person;

public class VillageSimulator 
{
	
	private Set<Object> objectSet = new HashSet<Object>();
	private final int WATER_AREAS = 40;
	private final int FOOD_AREAS = 40;
	private final int POPULATION = 100;
	private final int TIME_STEP = 60;
	
	public VillageSimulator()
	{
		// Setup state
		setupState();
		
	}

	public void update() 
	{
		for(Iterator<Object> i = objectSet.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Updateable)
		    {
		    	((Updateable) item).update(TIME_STEP);
		    }
		}
    }
	
	public void drawAllObjects(Graphics bbg)
	{
		for(Iterator<Object> i = objectSet.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Drawable)
		    {
		    	((Drawable) item).draw(bbg);
		    }
		}
	}
	
	
	
	private void setupState()
	{
		// Create resources
		createWater();
		createFood();
		
		// Create people
		createPeople();
	}
	
	private void createWater()
	{
		for (int i = 0; i < WATER_AREAS; i++) {
			objectSet.add(new Lake(20, 20));
        }
	}
	
	private void createFood()
	{
		for (int i = 0; i < FOOD_AREAS; i++) {
			objectSet.add(new Wood(20, 20));
        }
	}
	
	private void createPeople()
	{
		for (int i = 0; i < POPULATION; i++) {
			objectSet.add(new Person());
        }
	}

}

package com.villagesim;

import java.awt.Graphics;
import java.util.*;

import com.villagesim.actions.ActionMediator;
import com.villagesim.areas.Lake;
import com.villagesim.areas.Wood;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Updateable;
import com.villagesim.people.Person;
import com.villagesim.sensors.SensorUpdater;

public class VillageSimulator 
{
	
	private Set<Object> objectSet = new HashSet<Object>();
	private final int WATER_AREAS = 40;
	private final int FOOD_AREAS = 40;
	private final int POPULATION = 1;
	private final int TIME_STEP = 60;
	private SensorUpdater sensorUpdater;
	
	public VillageSimulator()
	{
		// Setup state
		setupState();
		
		sensorUpdater = new SensorUpdater(objectSet);
		
	}

	public void update() 
	{
		// Update sensors
		sensorUpdater.updateSensorReadingsAll();
		
		// Update objects
		for(Iterator<Object> i = objectSet.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Updateable)
		    {
		    	((Updateable) item).update(TIME_STEP);
		    }
		}
		
		// Perform actions
		ActionMediator.executeActions(TIME_STEP);
		
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
	
	public boolean isAlive()
	{
		return sensorUpdater.getAliveState();
	}
	
	public double getLifeTimeDays(double[][][] weights)
	{
		for(Object obj : objectSet)
		{
			if(obj instanceof Person)
			{
				Person person = (Person)obj;
				if(person.isWeightsEqual(weights))
				{
					return person.getLifetime();
				}
			}
		}
		return -1;
	}
	
	private void setupState()
	{
		// Create resources
		createWater();
		createFood();
		
		// Create people
		createPeople();
	}
	
	public void addPeople()
	{
		clearPeople();
		// TODO add population from weights
		// Problem, how do we get correct one back?
	}
	
	// Using this we have no interaction with others, works for now
	public void addPerson(double[][][] weights)
	{
		clearPeople();
		objectSet.add(new Person(weights));
		sensorUpdater.resetAliveState();
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
	
	private void clearPeople()
	{
		Iterator<Object> it = objectSet.iterator(); 
		while(it.hasNext()){
		    Object obj = it.next();
		    if(obj instanceof Person){
		        it.remove();
		    }
		}
	}

}

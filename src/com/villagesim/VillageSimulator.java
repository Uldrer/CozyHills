package com.villagesim;

import java.awt.Graphics;
import java.util.*;

import com.villagesim.actions.ActionMediator;
import com.villagesim.areas.Area;
import com.villagesim.areas.Lake;
import com.villagesim.areas.Wood;
import com.villagesim.helpers.FileHandler;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Updateable;
import com.villagesim.people.Person;
import com.villagesim.sensors.SensorUpdater;

public class VillageSimulator 
{
	
	private List<Person> personList = new ArrayList<Person>();
	private Map<Integer, Area> areaMap = new HashMap<Integer, Area>();
	private final int WATER_AREAS = 40;
	private final int FOOD_AREAS = 40;
	private final int POPULATION = 1;
	private final int TIME_STEP = 60;
	private SensorUpdater sensorUpdater;
	
	public VillageSimulator()
	{
		// Setup state
		setupState();
		
		sensorUpdater = new SensorUpdater(personList, areaMap);
		
	}

	public void update() 
	{
		// Update sensors
		sensorUpdater.updateSensorReadingsAll();
		
		// Update persons
		for(Iterator<Person> i = personList.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Updateable)
		    {
		    	((Updateable) item).update(TIME_STEP);
		    }
		}
		
		// Update areas
		for(Area area : areaMap.values()) 
		{
		    if(area instanceof Updateable)
		    {
		    	((Updateable) area).update(TIME_STEP);
		    }
		}
		
		// Perform actions
		ActionMediator.executeActions(TIME_STEP);
    }
	
	public void logResources()
	{
		FileHandler.logResourcesToFile(areaMap);
	}
	
	public void drawAllObjects(Graphics bbg)
	{
		for(Iterator<Person> i = personList.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Drawable)
		    {
		    	((Drawable) item).draw(bbg);
		    }
		}
		
		for(Area area : areaMap.values()) 
		{
		    if(area instanceof Drawable)
		    {
		    	((Drawable) area).draw(bbg);
		    }
		}
	}
	
	public boolean isAlive()
	{
		return sensorUpdater.getAliveState();
	}
	
	public double getLifeTimeDays(double[][][] basicWeights, double[][][] gatherWeights, double[][][] moveWeights, double[][][] workWeights)
	{
		for(Person person : personList)
		{
			if(person.isWeightsEqual(basicWeights, gatherWeights, moveWeights, workWeights))
			{
				return person.getLifetime();
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
	public void addPerson(double[][][] basicWeights, double[][][] gatherWeights, double[][][] moveWeights, double[][][] workWeights)
	{
		clearPeople();
		personList.add(new Person(basicWeights, gatherWeights, moveWeights, workWeights));
		sensorUpdater.resetAliveState();
	}
	
	private void createWater()
	{
		for (int i = 0; i < WATER_AREAS; i++) {
			Area newArea = new Lake(20, 20);
			areaMap.put(newArea.getId(), newArea);
        }
	}
	
	private void createFood()
	{
		for (int i = 0; i < FOOD_AREAS; i++) {
			Area newArea = new Wood(20, 20);
			areaMap.put(newArea.getId(), newArea);
        }
	}
	
	private void createPeople()
	{
		for (int i = 0; i < POPULATION; i++) {
			personList.add(new Person());
        }
	}
	
	private void clearPeople()
	{
		personList.clear();
	}
	
	public void resetState()
	{
		for(Area area : areaMap.values())
		{
			area.reset();
		}
	}

}

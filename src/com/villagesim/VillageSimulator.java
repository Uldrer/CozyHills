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
	private final int WATER_AREAS = 5;
	private final int FOOD_AREAS = 20;
	private final int POPULATION = 1;
	private final int TIME_STEP = 60;
	private final int FISH_AMOUNT = 100;
	private SensorUpdater sensorUpdater;
	
	public VillageSimulator()
	{
		this(true);		
	}
	
	public VillageSimulator(boolean enableLogging)
	{
		// Setup state
		setupState(enableLogging);
		
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
		for(Area area : areaMap.values()) 
		{
		    if(area instanceof Drawable)
		    {
		    	((Drawable) area).draw(bbg);
		    }
		}
		
		for(Iterator<Person> i = personList.iterator(); i.hasNext(); ) 
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
	
	public double getLifeTimeDays(double[][][] basicWeights)
	{
		for(Person person : personList)
		{
			if(person.isWeightsEqual(basicWeights))
			{
				return person.getLifetime();
			}
		}
		return -1;
	}
	
	private void setupState(boolean enableLogging)
	{
		// Create resources
		createWater(enableLogging);
		createFood(enableLogging);
		
		// Create people
		createPeople(enableLogging);
	}
	
	public void addPeople()
	{
		clearPeople();
		// TODO add population from weights
		// Problem, how do we get correct one back?
	}
	
	// Using this we have no interaction with others, works for now
	public void addPerson(double[][][] basicWeights)
	{
		clearPeople();
		personList.add(new Person(basicWeights));
		sensorUpdater.resetAliveState();
	}
	
	private void createWater(boolean enableLogging)
	{
		for (int i = 0; i < WATER_AREAS; i++) {
			Area newArea = new Lake(20, 20, FISH_AMOUNT);
			newArea.enableLogging(enableLogging);
			while(isAreaOverlappingExisting(areaMap, newArea))
			{
				newArea.reGenerateCoordinate();
			}
			areaMap.put(newArea.getId(), newArea);
        }
	}
	
	private void createFood(boolean enableLogging)
	{
		for (int i = 0; i < FOOD_AREAS; i++) {
			Area newArea = new Wood(20, 20);
			newArea.enableLogging(enableLogging);
			while(isAreaOverlappingExisting(areaMap, newArea))
			{
				newArea.reGenerateCoordinate();
			}
			areaMap.put(newArea.getId(), newArea);
        }
	}
	
	private void createPeople(boolean enableLogging)
	{
		for (int i = 0; i < POPULATION; i++) {
			Person newPerson = new Person();
			newPerson.enableLogging(enableLogging);
			personList.add(newPerson);
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
	
	private boolean isAreaOverlappingExisting(Map<Integer, Area> areas, Area newArea)
	{
		for(Area existingArea : areas.values())
		{
			if(newArea.overlaps(existingArea)) return true;
		}
		return false;
	}

}

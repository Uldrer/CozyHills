package com.villagesim.sensors;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.villagesim.areas.Area;
import com.villagesim.areas.Lake;
import com.villagesim.areas.Storage;
import com.villagesim.areas.Wood;
import com.villagesim.people.Person;
import com.villagesim.resources.Berries;
import com.villagesim.resources.Fish;
import com.villagesim.resources.Food;
import com.villagesim.resources.Game;
import com.villagesim.resources.Nuts;
import com.villagesim.resources.Resource;
import com.villagesim.resources.Water;

public class SensorUpdater {
	
	private Set<Object> stateObjects;
	private boolean aliveState = true;
	
	public SensorUpdater(Set<Object> objects)
	{
		this.stateObjects = objects;
	}
	
	public void updateSensorReadingsAll()
	{
		aliveState = false;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Person)
		    {
		    	Person person = ((Person) item);
		    	
		    	if(person.isAlive())
		    	{
		    		updateSensorReadings(person);
		    		aliveState = true;
		    	}
		    }
		}
	}
	
	public boolean getAliveState()
	{
		return aliveState;
	}
	
	public void resetAliveState()
	{
		aliveState = true;
	}
	
	private void updateSensorReadings(Person person)
	{
		// TODO here we can probably optimize quite a lot if needed
		
		// Create list of sensor inputs
		List<Double> sensorInputs = new ArrayList<Double>();
		
		// Create list of closest areas
		List<Area> closestAreas = new ArrayList<Area>();

		// The order of these inputs should not be altered lightly when weights have been produced to the ANN
		// If more are added change in SensoreHelper.SENSOR_INPUTS
		
		// #1 Distance to nearest drinking water
		SensorArea drinkingArea = getNearestDrinkingWater(person);
		sensorInputs.add(drinkingArea.getDistance());
		closestAreas.add(drinkingArea.getArea());
		
		// #2 Distance to nearest food storage
		SensorArea storageArea = getNearestStorage(person);
		sensorInputs.add(storageArea.getDistance());
		closestAreas.add(storageArea.getArea());
		
		// #3 Distance to nearest wild food gathering ground
		SensorArea wildFoodArea = getNearestWildFood(person);
		sensorInputs.add(wildFoodArea.getDistance());
		closestAreas.add(wildFoodArea.getArea());
		
		// #4 Distance to nearest wild game herd
		SensorArea wildGameArea = getNearestGameHerd(person);
		sensorInputs.add(wildGameArea.getDistance());
		closestAreas.add(wildGameArea.getArea());
		
		// #5 Distance to nearest fishing ground
		SensorArea fishingArea = getNearestFishingGround(person);
		sensorInputs.add(fishingArea.getDistance());
		closestAreas.add(fishingArea.getArea());
		
		// #6 Current person thirst value
		sensorInputs.add(getPersonThirst(person));
		closestAreas.add(null);
		
		// #7 Current person hunger value
		sensorInputs.add(getPersonHunger(person));
		closestAreas.add(null);
		
		// #8 Amount of aqua in nearest drinking water
		sensorInputs.add(getAmountOfAquaInArea(drinkingArea.getArea(), Water.class));
		closestAreas.add(null);
		
		// #9 Amount of nutrition in nearest storage
		sensorInputs.add(getAmountOfNutritionInArea(storageArea.getArea(), Food.class));
		closestAreas.add(null);
		
		// #10 Amount of nutrition in nearest wild food gathering ground
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(Nuts.class);
		resources.add(Berries.class);
		sensorInputs.add(getAmountOfNutritionInAreas(wildFoodArea.getArea(), resources));
		closestAreas.add(null);
		
		// #11 Amount of nutrition in nearest wild game herd
		sensorInputs.add(getAmountOfNutritionInArea(wildGameArea.getArea(), Game.class));
		closestAreas.add(null);
		
		// #12 Amount of nutrition in nearest fishing ground
		sensorInputs.add(getAmountOfNutritionInArea(fishingArea.getArea(), Fish.class));
		closestAreas.add(null);
		
		// #13 Amount of nutrition in personal storage
		sensorInputs.add(getAmountOfNutritionInArea(person.getPersonalStorage(), Food.class));
		closestAreas.add(null);
		
		// #14 Amount of aqua in personal storage
		sensorInputs.add(getAmountOfAquaInArea(person.getPersonalStorage(), Water.class));
		closestAreas.add(null);
		
		// #15 Amount of aqua in nearest storage
		sensorInputs.add(getAmountOfAquaInArea(storageArea.getArea(), Water.class));
		closestAreas.add(null);
		
		// Set new sensor inputs
		person.updateSensorReadings(sensorInputs, closestAreas);
		
	}
	
	private SensorArea getNearestDrinkingWater(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = SensorHelper.getNormalizedMaxDistance();
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Lake)
		    {
		    	Lake lake = ((Lake) item);
		    	
		    	if(!lake.containsResource(Water.class)) continue;
		    	
		    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, lake);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    		closestArea = lake;
		    	}
		    }
		}
		return new SensorArea(closestArea, closestDist);
	}
	
	private SensorArea getNearestStorage(Person person)
	{
		// TODO maybe redo to building
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = SensorHelper.getNormalizedMaxDistance();
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Storage)
		    {
		    	Storage storage = ((Storage) item);
		    	
		    	if(!storage.containsResource(Food.class) && !storage.containsResource(Water.class)) continue;
		    	
		    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, storage);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    		closestArea = storage;
		    	}
		    }
		}
		return new SensorArea(closestArea, closestDist);
	}
	
	private SensorArea getNearestWildFood(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = SensorHelper.getNormalizedMaxDistance();
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Wood)
		    {
		    	Wood wood = ((Wood) item);
		    	
		    	if(!wood.containsResource(Nuts.class) && !wood.containsResource(Berries.class)) continue;
		    	
		    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, wood);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    		closestArea = wood;
		    	}
		    }
		}
		return new SensorArea(closestArea, closestDist);
	}
	
	private SensorArea getNearestGameHerd(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = SensorHelper.getNormalizedMaxDistance();
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Wood)
		    {
		    	Wood wood = ((Wood) item);
		    	
		    	if(!wood.containsResource(Game.class)) continue;
		    	
		    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, wood);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    		closestArea = wood;
		    	}
		    }
		}
		return new SensorArea(closestArea, closestDist);
	}
	
	private SensorArea getNearestFishingGround(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = SensorHelper.getNormalizedMaxDistance();
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Lake)
		    {
		    	Lake lake = ((Lake) item);
		    	
		    	if(!lake.containsResource(Fish.class)) continue;
		    	
		    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, lake);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    		closestArea = lake;
		    	}
		    }
		}
		return new SensorArea(closestArea, closestDist);
	}
	
	private double getPersonThirst(Person person)
	{
		return person.getThirstValue();
	}
	
	private double getPersonHunger(Person person)
	{
		return person.getHungerValue();
	}
	
	private double getAmountOfAquaInArea(Area area, Class<? extends Resource> resourceClass)
	{
		double aqua = 0;
		if(area != null)
		{
			aqua = SensorHelper.normalizeAqua(area.getResourceAquaValue(resourceClass));
		}
		return aqua;
	}
	
	private double getAmountOfNutritionInArea(Area area, Class<? extends Resource> resourceClass)
	{
		double nutrition = 0;
		if(area != null)
		{
			nutrition = SensorHelper.normalizeNutrition(area.getResourceNutritionValue(resourceClass));
		}
		return nutrition;
	}
	
	private double getAmountOfNutritionInAreas(Area area, List<Class<? extends Resource>> resourceClasses)
	{
		double nutrition = 0;
		if(area != null)
		{
			for(Class<? extends Resource> resourceClass : resourceClasses)
			{
				nutrition += area.getResourceNutritionValue(resourceClass);
			}
		}
		return SensorHelper.normalizeNutrition(nutrition);
	}
}

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
import com.villagesim.resources.Water;

public class SensorUpdater {
	
	private Set<Object> stateObjects;
	
	public SensorUpdater(Set<Object> objects)
	{
		this.stateObjects = objects;
	}
	
	public void updateSensorReadingsAll()
	{
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Person)
		    {
		    	Person person = ((Person) item);
		    	updateSensorReadings(person);
		    }
		}
	}
	
	private void updateSensorReadings(Person person)
	{
		// TODO here we can probably optimize quite a lot if needed
		
		// Create list of sensor inputs
		List<Double> sensorInputs = new ArrayList<Double>();
		
		// The order of these inputs should not be altered lightly when weights have been produced to the ANN
		
		// #1 Distance to nearest drinking water
		sensorInputs.add(getDistanceToNearestDrinkingWater(person));
		
		// #2 Distance to nearest food storage
		sensorInputs.add(getDistanceToNearestFoodStorage(person));
		
		// #3 Distance to nearest wild food gathering ground
		sensorInputs.add(getDistanceToNearestWildFood(person));
		
		// #4 Distance to nearest wild game herd
		sensorInputs.add(getDistanceToNearestGameHerd(person));
		
		// #5 Distance to nearest fishing ground
		sensorInputs.add(getDistanceToNearestFishingGround(person));
		
		// #6 Current person thirst value
		sensorInputs.add(getPersonThirst(person));
		
		// #7 Current person hunger value
		sensorInputs.add(getPersonHunger(person));
		
		// #8 Amount of aqua in nearest drinking water
		sensorInputs.add(getAmountOfAquaInNearestDrinkingWater(person));
		
		// #9 Amount of nutrition in nearest food storage
		sensorInputs.add(getAmountOfNutritionInNearestFoodStorage(person));
		
		// #10 Amount of nutrition in nearest wild food gathering ground
		sensorInputs.add(getAmountOfNutritionInNearestWildFood(person));
		
		// #11 Amount of nutrition in nearest wild game herd
		sensorInputs.add(getAmountOfNutritionInNearestGameHerd(person));
		
		// #12 Amount of nutrition in nearest fishing ground
		sensorInputs.add(getAmountOfNutritionInNearestFishingGround(person));
		
	}
	
	private double getDistanceToNearestDrinkingWater(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Lake)
		    {
		    	Lake lake = ((Lake) item);
		    	
		    	if(!lake.containsResource(Water.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, lake);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    	}
		    }
		}
		return closestDist;
	}
	
	private double getDistanceToNearestFoodStorage(Person person)
	{
		// TODO maybe redo to building
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Storage)
		    {
		    	Storage storage = ((Storage) item);
		    	
		    	if(!storage.containsResource(Food.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, storage);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    	}
		    }
		}
		return closestDist;
	}
	
	private double getDistanceToNearestWildFood(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Wood)
		    {
		    	Wood wood = ((Wood) item);
		    	
		    	if(!wood.containsResource(Nuts.class) && !wood.containsResource(Berries.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, wood);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    	}
		    }
		}
		return closestDist;
	}
	
	private double getDistanceToNearestGameHerd(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Wood)
		    {
		    	Wood wood = ((Wood) item);
		    	
		    	if(!wood.containsResource(Game.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, wood);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    	}
		    }
		}
		return closestDist;
	}
	
	private double getDistanceToNearestFishingGround(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Lake)
		    {
		    	Lake lake = ((Lake) item);
		    	
		    	if(!lake.containsResource(Fish.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, lake);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestDist = dist;
		    	}
		    }
		}
		return closestDist;
	}
	
	private double getPersonThirst(Person person)
	{
		return person.getThirstValue();
	}
	
	private double getPersonHunger(Person person)
	{
		return person.getHungerValue();
	}
	
	private double getAmountOfAquaInNearestDrinkingWater(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Lake)
		    {
		    	Lake lake = ((Lake) item);
		    	
		    	if(!lake.containsResource(Water.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, lake);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestArea = lake;
		    		closestDist = dist;
		    	}
		    }
		}
		
		if(closestArea != null)
		{
			return closestArea.getResourceAquaValue(Water.class);
		}
		return 0;
	}
	
	private double getAmountOfNutritionInNearestFoodStorage(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Storage)
		    {
		    	Storage storage = ((Storage) item);
		    	
		    	if(!storage.containsResource(Food.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, storage);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestArea = storage;
		    		closestDist = dist;
		    	}
		    }
		}
		
		if(closestArea != null)
		{
			return closestArea.getResourceNutritionValue(Food.class);
		}
		return 0;
	}
	
	private double getAmountOfNutritionInNearestWildFood(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Wood)
		    {
		    	Wood wood = ((Wood) item);
		    	
		    	if(!wood.containsResource(Berries.class) && !wood.containsResource(Nuts.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, wood);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestArea = wood;
		    		closestDist = dist;
		    	}
		    }
		}
		
		if(closestArea != null)
		{
			double value = closestArea.getResourceNutritionValue(Berries.class);
			value += closestArea.getResourceNutritionValue(Nuts.class);
			return value;
		}
		return 0;
	}
	
	private double getAmountOfNutritionInNearestGameHerd(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Wood)
		    {
		    	Wood wood = ((Wood) item);
		    	
		    	if(!wood.containsResource(Game.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, wood);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestArea = wood;
		    		closestDist = dist;
		    	}
		    }
		}
		
		if(closestArea != null)
		{
			return closestArea.getResourceNutritionValue(Game.class);
		}
		return 0;
	}
	
	private double getAmountOfNutritionInNearestFishingGround(Person person)
	{
		Point2D personCoordinate = person.getCoordinate();
		double closestDist = Double.MAX_VALUE;
		Area closestArea = null;
		for(Iterator<Object> i = stateObjects.iterator(); i.hasNext(); ) 
		{
		    Object item = i.next();
		    if(item instanceof Lake)
		    {
		    	Lake lake = ((Lake) item);
		    	
		    	if(!lake.containsResource(Fish.class)) continue;
		    	
		    	double dist = SensorHelper.computeDistanceToArea(personCoordinate, lake);
		    	
		    	if(dist < closestDist)
		    	{
		    		closestArea = lake;
		    		closestDist = dist;
		    	}
		    }
		}
		
		if(closestArea != null)
		{
			return closestArea.getResourceNutritionValue(Fish.class);
		}
		return 0;
	}
	
	
}

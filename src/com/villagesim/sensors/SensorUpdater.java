package com.villagesim.sensors;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	private List<Person> personList;
	private Map<Integer, Area> areaMap;
	private boolean aliveState = true;
	
	public SensorUpdater(List<Person> personList, Map<Integer, Area> areaMap)
	{
		this.personList = personList;
		this.areaMap = areaMap;
	}
	
	public void updateSensorReadingsAll()
	{
		aliveState = false;
		for(Person person : personList) 
		{
	    	if(person.isAlive())
	    	{
	    		updateSensorReadings(person);
	    		aliveState = true;
	    	}
		}
	}
	
	public Area getAreaFromId(int id)
	{
		return areaMap.get(id);
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

		// If more are added change in SensoreHelper.SENSOR_INPUTS
		
		// #1 Distance to nearest drinking water
		SensorArea drinkingArea = getNearestDrinkingWater(person);
		sensorInputs.add(drinkingArea.getDistance());
		closestAreas.add(getAreaFromId(drinkingArea.getAreaId()));
		
		// #2 Distance to nearest food storage
		SensorArea storageArea = getNearestStorage(person);
		sensorInputs.add(storageArea.getDistance());
		closestAreas.add(getAreaFromId(storageArea.getAreaId()));
		
		// #3 Distance to nearest wild food gathering ground
		SensorArea wildFoodArea = getNearestWildFood(person);
		sensorInputs.add(wildFoodArea.getDistance());
		closestAreas.add(getAreaFromId(wildFoodArea.getAreaId()));
		
		// #4 Distance to nearest wild game herd
		SensorArea wildGameArea = getNearestGameHerd(person);
		sensorInputs.add(wildGameArea.getDistance());
		closestAreas.add(getAreaFromId(wildGameArea.getAreaId()));
		
		// #5 Distance to nearest fishing ground
		SensorArea fishingArea = getNearestFishingGround(person);
		sensorInputs.add(fishingArea.getDistance());
		closestAreas.add(getAreaFromId(fishingArea.getAreaId()));
		
		// #6 Current person thirst value
		sensorInputs.add(getPersonThirst(person));
		closestAreas.add(null);
		
		// #7 Current person hunger value
		sensorInputs.add(getPersonHunger(person));
		closestAreas.add(null);
		
		// #8 Amount of aqua in nearest drinking water
		sensorInputs.add(getAmountOfAquaInArea(getAreaFromId(drinkingArea.getAreaId()), Water.class));
		closestAreas.add(null);
		
		// #9 Amount of nutrition in nearest storage
		sensorInputs.add(getAmountOfNutritionInArea(getAreaFromId(storageArea.getAreaId()), Food.class));
		closestAreas.add(null);
		
		// #10 Amount of nutrition in nearest wild food gathering ground
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(Nuts.class);
		resources.add(Berries.class);
		sensorInputs.add(getAmountOfNutritionInAreas(getAreaFromId(wildFoodArea.getAreaId()), resources));
		closestAreas.add(null);
		
		// #11 Amount of nutrition in nearest wild game herd
		sensorInputs.add(getAmountOfNutritionInArea(getAreaFromId(wildGameArea.getAreaId()), Game.class));
		closestAreas.add(null);
		
		// #12 Amount of nutrition in nearest fishing ground
		sensorInputs.add(getAmountOfNutritionInArea(getAreaFromId(fishingArea.getAreaId()), Fish.class));
		closestAreas.add(null);
		
		// #13 Amount of nutrition in personal storage
		sensorInputs.add(getAmountOfNutritionInArea(person.getPersonalStorage(), Food.class));
		closestAreas.add(null);
		
		// #14 Amount of aqua in personal storage
		sensorInputs.add(getAmountOfAquaInArea(person.getPersonalStorage(), Water.class));
		closestAreas.add(null);
		
		// #15 Amount of aqua in nearest storage
		sensorInputs.add(getAmountOfAquaInArea(getAreaFromId(storageArea.getAreaId()), Water.class));
		closestAreas.add(null);
		
		// #16 Direction in radians to nearest water
		sensorInputs.add(getDirectionInRadians(person, getAreaFromId(drinkingArea.getAreaId())));
		closestAreas.add(null);
				
		// #17 Direction in radians to nearest wood
		sensorInputs.add(getDirectionInRadians(person, getAreaFromId(wildFoodArea.getAreaId())));
		closestAreas.add(null);
		
		// Set new sensor inputs
		person.updateSensorReadings(sensorInputs, closestAreas);
		
	}
	
	private SensorArea getNearestDrinkingWater(Person person)
	{
		boolean hasNewCoordinate = person.hasNewCoordinate(Water.class);
		if(hasNewCoordinate)
		{
			Point2D personCoordinate = person.getCoordinate(Water.class);
			double closestDist = SensorHelper.getNormalizedMaxDistance();
			Area closestArea = null;
			for(Area area : areaMap.values()) 
			{
			    if(area instanceof Lake)
			    {
			    	Lake lake = ((Lake) area);
			    	
			    	if(!lake.containsResource(Water.class)) continue;
			    	
			    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, lake);
			    	
			    	if(dist < closestDist)
			    	{
			    		closestDist = dist;
			    		closestArea = lake;
			    	}
			    }
			}
			SensorArea newDrinkingWaterArea = new SensorArea(closestArea != null ? closestArea.getId() : -1, closestDist);
			person.setLastSensorArea(Water.class,newDrinkingWaterArea);
			return newDrinkingWaterArea;
		}
		else
		{
			return person.getLastSensorArea(Water.class);
		}
	}
	
	private SensorArea getNearestStorage(Person person)
	{
		boolean hasNewCoordinate = person.hasNewCoordinate(Food.class);
		if(hasNewCoordinate)
		{
			// TODO maybe redo to building
			Point2D personCoordinate = person.getCoordinate(Food.class);
			double closestDist = SensorHelper.getNormalizedMaxDistance();
			Area closestArea = null;
			for(Area area : areaMap.values())
			{
			    if(area instanceof Storage)
			    {
			    	Storage storage = ((Storage) area);
			    	
			    	if(!storage.containsResource(Food.class) && !storage.containsResource(Water.class)) continue;
			    	
			    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, storage);
			    	
			    	if(dist < closestDist)
			    	{
			    		closestDist = dist;
			    		closestArea = storage;
			    	}
			    }
			}
			SensorArea newStorageArea = new SensorArea(closestArea != null ? closestArea.getId() : -1, closestDist);
			person.setLastSensorArea(Food.class,newStorageArea);
			return newStorageArea;
		}
		else
		{
			return person.getLastSensorArea(Food.class);
		}
	}
	
	private SensorArea getNearestWildFood(Person person)
	{
		boolean hasNewCoordinate = person.hasNewCoordinate(Nuts.class);
		if(hasNewCoordinate)
		{
			Point2D personCoordinate = person.getCoordinate(Nuts.class);
			double closestDist = SensorHelper.getNormalizedMaxDistance();
			Area closestArea = null;
			for(Area area : areaMap.values())
			{
			    if(area instanceof Wood)
			    {
			    	Wood wood = ((Wood) area);
			    	
			    	if(!wood.containsResource(Nuts.class) && !wood.containsResource(Berries.class)) continue;
			    	
			    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, wood);
			    	
			    	if(dist < closestDist)
			    	{
			    		closestDist = dist;
			    		closestArea = wood;
			    	}
			    }
			}
			SensorArea newWildFoodArea = new SensorArea(closestArea != null ? closestArea.getId() : -1, closestDist);
			person.setLastSensorArea(Nuts.class,newWildFoodArea);
			person.setLastSensorArea(Berries.class,newWildFoodArea);
			return newWildFoodArea;
		}
		else
		{
			return person.getLastSensorArea(Nuts.class);
		}
	}
	
	private SensorArea getNearestGameHerd(Person person)
	{
		boolean hasNewCoordinate = person.hasNewCoordinate(Game.class);
		if(hasNewCoordinate)
		{
			Point2D personCoordinate = person.getCoordinate(Game.class);
			double closestDist = SensorHelper.getNormalizedMaxDistance();
			Area closestArea = null;
			for(Area area : areaMap.values())
			{
			    if(area instanceof Wood)
			    {
			    	Wood wood = ((Wood) area);
			    	
			    	if(!wood.containsResource(Game.class)) continue;
			    	
			    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, wood);
			    	
			    	if(dist < closestDist)
			    	{
			    		closestDist = dist;
			    		closestArea = wood;
			    	}
			    }
			}
			SensorArea newGameArea = new SensorArea(closestArea != null ? closestArea.getId() : -1, closestDist);
			person.setLastSensorArea(Game.class,newGameArea);
			return newGameArea;
		}
		else
		{
			return person.getLastSensorArea(Game.class);
		}
	}
	
	private SensorArea getNearestFishingGround(Person person)
	{
		boolean hasNewCoordinate = person.hasNewCoordinate(Fish.class);
		if(hasNewCoordinate)
		{
			Point2D personCoordinate = person.getCoordinate(Fish.class);
			double closestDist = SensorHelper.getNormalizedMaxDistance();
			Area closestArea = null;
			for(Area area : areaMap.values()) 
			{
			    if(area instanceof Lake)
			    {
			    	Lake lake = ((Lake) area);
			    	
			    	if(!lake.containsResource(Fish.class)) continue;
			    	
			    	double dist = SensorHelper.computeNormalizedDistanceToArea(personCoordinate, lake);
			    	
			    	if(dist < closestDist)
			    	{
			    		closestDist = dist;
			    		closestArea = lake;
			    	}
			    }
			}
			SensorArea newFishArea = new SensorArea(closestArea != null ? closestArea.getId() : -1, closestDist);
			person.setLastSensorArea(Fish.class,newFishArea);
			return newFishArea;
		}
		else
		{
			return person.getLastSensorArea(Fish.class);
		}
	}
	
	private double getPersonThirst(Person person)
	{
		return person.getThirstValue();
	}
	
	private double getPersonHunger(Person person)
	{
		return person.getHungerValue();
	}
	
	private double getDirectionInRadians(Person person, Area area)
	{
		double direction_radians = 0;
		if(area != null)
		{
			direction_radians = SensorHelper.computeDirectionToArea(person.getCoordinate(), area);
		}
		return direction_radians;
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

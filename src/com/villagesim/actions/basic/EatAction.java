package com.villagesim.actions.basic;

import java.util.ArrayList;
import java.util.List;

import com.villagesim.areas.Area;
import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;
import com.villagesim.resources.Berries;
import com.villagesim.resources.Fish;
import com.villagesim.resources.Food;
import com.villagesim.resources.Game;
import com.villagesim.resources.Nuts;
import com.villagesim.resources.Resource;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class EatAction implements Action {

	private Person person;
	private List<Sensor> distSensors;
	private List<List<Class<? extends Resource>>> resourceLists;
	
	public EatAction(Person person)
	{
		this.person = person;
		
		// TODO eat from storage
		// For now eat whatever is available
		this.distSensors = new ArrayList<Sensor>();
		this.resourceLists = new ArrayList<List<Class<? extends Resource>>>();
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		distSensors.add(Sensor.DIST_TO_FOOD_STORAGE);
		resources.add(Food.class);
		resourceLists.add(resources);
		distSensors.add(Sensor.DIST_TO_FISH);
		resources = new ArrayList<Class<? extends Resource>>();
		resources.add(Fish.class);
		resourceLists.add(resources);
		distSensors.add(Sensor.DIST_TO_GAME);
		resources = new ArrayList<Class<? extends Resource>>();
		resources.add(Game.class);
		resourceLists.add(resources);
		distSensors.add(Sensor.DIST_TO_WILD_FOOD);
		resources = new ArrayList<Class<? extends Resource>>();
		resources.add(Nuts.class);
		resources.add(Berries.class);
		resourceLists.add(resources);
	}
	
	@Override
	public void execute(int seconds) {

		// Check how much will be consumed
		double potentialNutrition = person.getPotentialNutrition(seconds);
		
		// TODO eat from storage
		for(int i = 0; i < distSensors.size(); i++)
		{
			Sensor distSensor = distSensors.get(i);
			
			// Check if that is possible
			Area area = person.getClosestArea(distSensor.getIndex());
			
			if(area == null) continue;
			
			// Check if region is close enough
			double distanceToResource = person.getSensorReading(distSensor.getIndex());
			boolean success = SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);
			
			if(!success) continue;
			
			List<Class<? extends Resource>> resources = resourceLists.get(i);
			
			for(Class<? extends Resource> resource : resources)
			{
				double availableValue = area.getResourceNutritionValue(resource);
				double value = potentialNutrition <= availableValue ? potentialNutrition : availableValue;
	
				// Eat what's available
				person.eat(value);
				potentialNutrition -= value;
				
				// Remove that value from resource
				area.consumeResourceNutritionValue(resource, value);
			}
		}
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is eating.");
		}
	}

	@Override
	public boolean isValid() {
		
		for(Sensor distSensor : distSensors)
		{
			double distanceToResource = person.getSensorReading(distSensor.getIndex());
		
			boolean success = SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);
			if(success) return true;
		}
		return false;
	}
	

}

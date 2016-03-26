package com.villagesim.actions.basic;

import java.util.ArrayList;
import java.util.List;

import com.villagesim.areas.Area;
import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;
import com.villagesim.resources.Food;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class EatAction implements Action {

	private Person person;
	private List<Sensor> distSensors;
	
	public EatAction(Person person)
	{
		this.person = person;
		
		// TODO eat from storage
		// For now eat whatever is available
		this.distSensors = new ArrayList<Sensor>();
		
		distSensors.add(Sensor.DIST_TO_FOOD_STORAGE);
		distSensors.add(Sensor.DIST_TO_FISH);
		distSensors.add(Sensor.DIST_TO_GAME);
		distSensors.add(Sensor.DIST_TO_WILD_FOOD);
	}
	
	@Override
	public void execute(int seconds) {

		// Check how much will be consumed
		double potentialNutrition = person.getPotentialNutrition(seconds);
		
		// TODO eat from storage
		for(Sensor distSensor : distSensors)
		{
			// Check if that is possible
			Area area = person.getClosestArea(distSensor.getIndex());
			double availableValue = area.getResourceNutritionValue(Food.class);
			double value = potentialNutrition <= availableValue ? potentialNutrition : potentialNutrition - availableValue;

			// Eat what's available
			person.eat(value);
			potentialNutrition -= value;
			
			// Remove that value from resource
			area.consumeResourceNutritionValue(Food.class, value);
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

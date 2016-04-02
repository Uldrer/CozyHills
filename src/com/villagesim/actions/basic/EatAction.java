package com.villagesim.actions.basic;


import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.resources.Food;
import com.villagesim.resources.Resource;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class EatAction implements Action, Printable  {

	private Person person;
	private Sensor distSensor;
	private Class<? extends Resource> resource;
	
	public EatAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_STORAGE;
		this.resource = Food.class;
	}
	
	@Override
	public void execute(int seconds) {

		// If within range of storage use that food, else use personal storage
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
			
		if(area == null) 
		{
			eatFromPersonalStorage(seconds);
			return;
		}
			
		// Check if region is close enough
		double distanceToResource = person.getSensorReading(distSensor.getIndex());
		boolean success = SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);
			
		if(!success) 
		{
			eatFromPersonalStorage(seconds);
			return;
		}
		
		if(area instanceof Storage)
		{
			Storage storage = (Storage) area;
			eatFromStorage(storage, seconds);
		}
		else
		{
			eatFromPersonalStorage(seconds);
			return;
		}
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is eating from storage.");
		}
	}

	private void eatFromPersonalStorage(int seconds)
	{
		eatFromStorage(person.getPersonalStorage(), seconds);
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is eating from personal storage.");
		}
	}
	
	private void eatFromStorage(Storage storage, int seconds)
	{
		// Check how much will be consumed
		double potentialNutrition = person.getPotentialNutrition(seconds);
		double availableValue = storage.getResourceNutritionValue(resource);
		double value = potentialNutrition <= availableValue ? potentialNutrition : availableValue;

		// Eat what's available
		person.eat(value);
		potentialNutrition -= value;
		
		// Remove that value from resource
		storage.consumeResourceNutritionValue(resource, value);
	}

	@Override
	public boolean isValid() 
	{
		// Always valid to eat from personal storage?
		// For now don't allow it if personalStorage is empty
		double availableResource = person.getSensorReading(Sensor.NUTRITION_IN_PERSONAL_STORAGE.getIndex());
		return availableResource > 0;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "E"; 
		return str;
	}
	

}

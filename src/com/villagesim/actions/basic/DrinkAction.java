package com.villagesim.actions.basic;

import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.resources.Resource;
import com.villagesim.resources.Water;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class DrinkAction implements Action, Printable  {

	private Person person;
	private Sensor distSensor;
	private Class<? extends Resource> resource;
	
	public DrinkAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_STORAGE;
		this.resource = Water.class;
	}
	
	@Override
	public void execute(int seconds) {
		
		// If within range of storage use that water, else use personal storage
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
			
		if(area == null) 
		{
			drinkFromPersonalStorage(seconds);
			return;
		}
			
		// Check if region is close enough
		double distanceToResource = person.getSensorReading(distSensor.getIndex());
		boolean success = SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);
			
		if(!success) 
		{
			drinkFromPersonalStorage(seconds);
			return;
		}
		
		if(area instanceof Storage)
		{
			Storage storage = (Storage) area;
			drinkFromStorage(storage, seconds);
		}
		else
		{
			drinkFromPersonalStorage(seconds);
			return;
		}
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is drinking from storage.");
		}
	}
	
	private void drinkFromPersonalStorage(int seconds)
	{
		drinkFromStorage(person.getPersonalStorage(), seconds);
		if(person.printDebug())
		{
			//System.out.println("Person id: " + person.getId() + " is drinking from personal storage.");
			System.out.print("+");
		}
	}
	
	private void drinkFromStorage(Storage storage, int seconds)
	{
		// Check how much will be consumed
		double potentialAqua = person.getPotentialAqua(seconds);
		double availableValue = storage.getResourceAquaValue(resource);
		double value = potentialAqua <= availableValue ? potentialAqua : availableValue;

		// Eat what's available
		person.drink(value);
		potentialAqua -= value;
		
		// Remove that value from resource
		storage.consumeResourceAquaValue(resource, value);
	}

	@Override
	public boolean isValid() {
		// Always valid to drink from personal storage?
		// For now don't allow it if personalStorage is empty
		double availableResource = person.getSensorReading(Sensor.AQUA_IN_PERSONAL_STORAGE.getIndex());
		return availableResource > 0;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "D"; 
		return str;
	}

}

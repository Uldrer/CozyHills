package com.villagesim.actions.basic;

import com.villagesim.areas.Area;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.resources.Resource;
import com.villagesim.resources.Water;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class DrinkActionLake implements Action, Printable {

	private Person person;
	private Sensor distSensor;
	private Class<? extends Resource> resource;
	
	public DrinkActionLake(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_WATER;
		this.resource = Water.class;
	}
	
	@Override
	public void execute(int seconds) {

		// Check how much will be consumed
		double potentialAqua = person.getPotentialAqua(seconds);
		
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
		double availableValue = area.getResourceAquaValue(resource);
		double value = potentialAqua <= availableValue ? potentialAqua : availableValue;

		// Drink what's available
		person.drink(value);
		
		// Remove that value from resource
		area.consumeResourceAquaValue(resource, value);
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is drinking.");
		}
	}

	@Override
	public boolean isValid() {
		
		double distanceToResource = person.getSensorReading(distSensor.getIndex());
		
		boolean valid = SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);
		
		if(!valid && person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " can't drink. Distance to water: " + distanceToResource);
		}
		return valid;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "D_L"; 
		return str;
	}

}

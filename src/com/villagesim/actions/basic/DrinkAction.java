package com.villagesim.actions.basic;

import com.villagesim.areas.Area;
import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;
import com.villagesim.resources.Water;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class DrinkAction implements Action {

	private Person person;
	private Sensor distSensor;
	
	public DrinkAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_WATER;
	}
	
	@Override
	public void execute(int seconds) {

		// Check how much will be consumed
		double potentialAqua = person.getPotentialAqua(seconds);
		
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
		double availableValue = area.getResourceAquaValue(Water.class);
		double value = potentialAqua <= availableValue ? potentialAqua : potentialAqua - availableValue;

		// Drink what's available
		person.drink(value);
		
		// Remove that value from resource
		area.consumeResourceAquaValue(Water.class, value);
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is drinking.");
		}
	}

	@Override
	public boolean isValid() {
		
		double distanceToResource = person.getSensorReading(distSensor.getIndex());
		
		return SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);
	}

}

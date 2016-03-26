package com.villagesim.actions.basic;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;
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

		// TODO Add only as much aqua as is available
		// TODO remove aqua from resource
		
		person.drink(seconds);
		
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

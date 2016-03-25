package com.villagesim.actions.basic;

import java.util.ArrayList;
import java.util.List;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;
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
		
		// TODO Add only as much nutrition as is available
		// TODO remove nutrition from resource

		person.eat(seconds);
		
		//System.out.println("Person id: " + person.getId() + " is eating.");
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

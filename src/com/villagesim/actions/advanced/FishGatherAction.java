package com.villagesim.actions.advanced;

import com.villagesim.Const;
import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.resources.Fish;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class FishGatherAction implements Action, Printable  {

	private Person person;
	private Sensor distSensor;
	private double gather_rate; // amount/s
	
	public FishGatherAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_FISH;
		this.gather_rate = 1/Const.SECONDS_PER_HOUR; // Assumption, you can catch one fish per hour on average
	}
	
	@Override
	public void execute(int seconds) 
	{
		// TODO use gathering rate to randomize the catch, so that parts of fish are not caught
		// Consume resource according to gathering rate
		double potentialAmount = gather_rate*seconds;
		
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
		double availableValue = area.getResourceAmountValue(Fish.class);
		double value = potentialAmount <= availableValue ? potentialAmount : availableValue;
		
		// Remove that value from resource
		area.consumeResourceAmountValue(Fish.class, value);
		
		// Fill personal storage with equal amount
		Storage personalStorage = person.getPersonalStorage();
		personalStorage.fillResourceAmountValue(Fish.class, value);
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is fishing.");
		}
	}

	@Override
	public boolean isValid() {
		double distanceToResource = person.getSensorReading(distSensor.getIndex());
		
		boolean valid = SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);

		return valid;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "G_F"; 
		return str;
	}

}

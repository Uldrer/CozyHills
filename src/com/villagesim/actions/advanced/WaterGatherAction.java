package com.villagesim.actions.advanced;

import com.villagesim.Const;
import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.resources.Water;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class WaterGatherAction implements Action, Printable  {
	
	private Person person;
	private Sensor distSensor;
	private double gather_rate; // amount/s
	
	public WaterGatherAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_WATER;
		this.gather_rate = 120/Const.SECONDS_PER_HOUR; // Assumption, you can gather 2 liters of water in a minute on average
	}
	
	@Override
	public void execute(int seconds) {
		// Consume resource according to gathering rate
		double potentialAmount = gather_rate*seconds;
		
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
		double availableValue = area.getResourceAmountValue(Water.class);
		double value = potentialAmount <= availableValue ? potentialAmount : availableValue;
		
		// Remove that value from resource
		area.consumeResourceAmountValue(Water.class, value);
		
		// Fill personal storage with equal amount
		Storage personalStorage = person.getPersonalStorage();
		personalStorage.fillResourceAmountValue(Water.class, value);
				
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is gathering water.");
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
		String str = "G_W"; 
		return str;
	}

}

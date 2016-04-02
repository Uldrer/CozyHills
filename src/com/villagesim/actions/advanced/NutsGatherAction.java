package com.villagesim.actions.advanced;

import com.villagesim.Const;
import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.resources.Nuts;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class NutsGatherAction implements Action, Printable  {

	private Person person;
	private Sensor distSensor;
	private double gather_rate; // amount/s
	
	public NutsGatherAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_WILD_FOOD;
		this.gather_rate = 2/Const.SECONDS_PER_HOUR; // Assumption, you can gather 2 liters of nuts per hour on average
	}
	
	@Override
	public void execute(int seconds) {
		// Consume resource according to gathering rate
		double potentialAmount = gather_rate*seconds;
		
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
		double availableValue = area.getResourceAmountValue(Nuts.class);
		double value = potentialAmount <= availableValue ? potentialAmount : availableValue;
		
		// Remove that value from resource
		area.consumeResourceAmountValue(Nuts.class, value);
		
		// Fill personal storage with equal amount
		Storage personalStorage = person.getPersonalStorage();
		personalStorage.fillResourceAmountValue(Nuts.class, value);
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is gathering nuts.");
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
		String str = "G_N"; 
		return str;
	}

}

package com.villagesim.actions.advanced;

import com.villagesim.Const;
import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;
import com.villagesim.resources.Berries;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class BerriesGatherAction implements Action {

	private Person person;
	private Sensor distSensor;
	private double gather_rate; // amount/s
	
	public BerriesGatherAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_WILD_FOOD;
		this.gather_rate = 3/Const.SECONDS_PER_HOUR; // Assumption, you can gather 3 liters of berries per hour on average
	}
	
	@Override
	public void execute(int seconds) 
	{
		// Consume resource according to gathering rate
		double potentialAmount = gather_rate*seconds;
		
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
		double availableValue = area.getResourceAmountValue(Berries.class);
		double value = potentialAmount <= availableValue ? potentialAmount : availableValue;
		
		// Remove that value from resource
		area.consumeResourceAmountValue(Berries.class, value);
		
		// Fill personal storage with equal amount
		Storage personalStorage = person.getPersonalStorage();
		personalStorage.fillResourceAmountValue(Berries.class, value);
				
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is gathering berries.");
		}
	}

	@Override
	public boolean isValid() {
		double distanceToResource = person.getSensorReading(distSensor.getIndex());
		
		boolean valid = SensorHelper.isNormalizedDistanceCloseEnoughForAction(distanceToResource);

		return valid;
	}


}

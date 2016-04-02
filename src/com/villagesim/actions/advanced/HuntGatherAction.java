package com.villagesim.actions.advanced;

import com.villagesim.Const;
import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.resources.Game;
import com.villagesim.sensors.Sensor;
import com.villagesim.sensors.SensorHelper;

public class HuntGatherAction implements Action, Printable  {
	
	private Person person;
	private Sensor distSensor;
	private double gather_rate; // amount/s
	
	public HuntGatherAction(Person person)
	{
		this.person = person;
		this.distSensor = Sensor.DIST_TO_GAME;
		this.gather_rate = 0.25/Const.SECONDS_PER_HOUR; // Assumption, you can hunt 1 animal in 4 hours on average
	}
	
	@Override
	public void execute(int seconds) {
		// TODO use gathering rate to randomize the catch, so that parts of animals are not hunted
		// Consume resource according to gathering rate
		double potentialAmount = gather_rate*seconds;
		
		// Check if that is possible
		Area area = person.getClosestArea(distSensor.getIndex());
		double availableValue = area.getResourceAmountValue(Game.class);
		double value = potentialAmount <= availableValue ? potentialAmount : availableValue;
		
		// Remove that value from resource
		area.consumeResourceAmountValue(Game.class, value);
		
		// Fill personal storage with equal amount
		Storage personalStorage = person.getPersonalStorage();
		personalStorage.fillResourceAmountValue(Game.class, value);
				
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is hunting.");
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
		String str = "G_G"; 
		return str;
	}

}

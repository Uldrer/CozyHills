package com.villagesim.actions.advanced;

import com.villagesim.Const;
import com.villagesim.actions.BasicAction;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.sensors.Measurement;

public class WalkFishMoveAction implements Action, Printable  {

	private Person person;
	private Measurement directionMeasurement;
	private boolean depletedFish = false;
	
	public WalkFishMoveAction(Person person)
	{
		this.person = person;
		this.directionMeasurement = Measurement.DIRECTION_TO_NEAREST_FISH;
	}

	
	@Override
	public void execute(int seconds) {
		
		// Move in direction towards nearest water
		double angle_radians = person.getMeasurementReading(directionMeasurement.getIndex());
		
		if(angle_radians == -100) 
		{
			depletedFish =true;
			return;
		}

		double vx = Math.cos(angle_radians);
		double vy = Math.sin(angle_radians);
		
		double dx = Const.WALKING_SPEED/Const.METER_PER_PIXEL * seconds * vx;
		double dy = Const.WALKING_SPEED/Const.METER_PER_PIXEL * seconds * vy;
		
		person.move(dx, dy, BasicAction.WALK_DIRECTION_FISH);
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is walking.");
		}
	}


	@Override
	public boolean isValid() {
		// Valid as long as there is some fish
		return !depletedFish;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "W_WF"; 
		return str;
	}

}

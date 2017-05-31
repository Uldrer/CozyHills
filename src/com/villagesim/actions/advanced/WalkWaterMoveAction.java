package com.villagesim.actions.advanced;

import com.villagesim.Const;
import com.villagesim.actions.BasicAction;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.sensors.Measurement;

public class WalkWaterMoveAction implements Action, Printable  {

	private Person person;
	private Measurement directionMeasurement;
	
	public WalkWaterMoveAction(Person person)
	{
		this.person = person;
		this.directionMeasurement = Measurement.DIRECTION_TO_NEAREST_WATER;
	}

	
	@Override
	public void execute(int seconds) {
		
		// Move in direction towards nearest water
		double angle_radians = person.getSensorReading(directionMeasurement.getIndex());
		double vx = Math.cos(angle_radians);
		double vy = Math.sin(angle_radians);
		
		double dx = Const.WALKING_SPEED/Const.METER_PER_PIXEL * seconds * vx;
		double dy = Const.WALKING_SPEED/Const.METER_PER_PIXEL * seconds * vy;
		
		person.move(dx, dy, BasicAction.WALK_DIRECTION_WATER);
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is walking towards water.");
		}
	}


	@Override
	public boolean isValid() {
		// For now always valid to walk
		return true;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "W_WWa"; 
		return str;
	}

}

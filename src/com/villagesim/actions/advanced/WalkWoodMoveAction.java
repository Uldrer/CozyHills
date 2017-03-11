package com.villagesim.actions.advanced;


import com.villagesim.Const;
import com.villagesim.actions.AdvancedAction;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;
import com.villagesim.sensors.Sensor;

public class WalkWoodMoveAction implements Action, Printable  {

	private Person person;
	private Sensor directionSensor;
	
	public WalkWoodMoveAction(Person person)
	{
		this.person = person;
		this.directionSensor = Sensor.DIRECTION_TO_NEAREST_WOOD;
	}

	
	@Override
	public void execute(int seconds) {
		
		// Move in direction towards nearest water
		double angle_radians = person.getSensorReading(directionSensor.getIndex());
		double vx = Math.cos(angle_radians);
		double vy = Math.sin(angle_radians);
		
		double dx = Const.WALKING_SPEED/Const.METER_PER_PIXEL * seconds * vx;
		double dy = Const.WALKING_SPEED/Const.METER_PER_PIXEL * seconds * vy;
		
		person.move(dx, dy, AdvancedAction.WALK_DIRECTION_WOOD);
		
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is walking.");
		}
	}


	@Override
	public boolean isValid() {
		// For now always valid to walk
		return true;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "W_WWo"; 
		return str;
	}

}

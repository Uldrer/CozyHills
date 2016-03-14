package com.villagesim.actions.advanced;

import java.util.Random;

import com.villagesim.Const;
import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class RunMoveAction implements Action {

	private Person person;
	private Random rand = new Random();
	private final double RUNNING_SPEED = 3.3; // m/s
	
	public RunMoveAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public boolean execute(int seconds) {
		// TODO move person in some given direction, not in random direction
		
		// Randomize direction
		double angle_radians = rand.nextDouble()*2*Math.PI;
		double vx = Math.cos(angle_radians);
		double vy = Math.sin(angle_radians);
		
		double dx = RUNNING_SPEED/Const.METER_PER_PIXEL * seconds * vx;
		double dy = RUNNING_SPEED/Const.METER_PER_PIXEL * seconds * vy;
		
		person.move(dx, dy);		
				
		System.out.println("Person id: " + person.getId() + " is running.");
		return true;
	}

}

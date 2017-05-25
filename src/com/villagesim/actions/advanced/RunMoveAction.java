package com.villagesim.actions.advanced;

import java.util.Random;

import com.villagesim.Const;
import com.villagesim.actions.BasicAction;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;

public class RunMoveAction implements Action, Printable  {

	private Person person;
	private Random rand = new Random();
	
	public RunMoveAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute(int seconds) {
		// TODO move person in some given direction, not in random direction
		
		// Randomize direction
		double angle_radians = rand.nextDouble()*2*Math.PI;
		double vx = Math.cos(angle_radians);
		double vy = Math.sin(angle_radians);
		
		double dx = Const.RUNNING_SPEED/Const.METER_PER_PIXEL * seconds * vx;
		double dy = Const.RUNNING_SPEED/Const.METER_PER_PIXEL * seconds * vy;
		
		person.move(dx, dy, BasicAction.RUN);		
				
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is running.");
		}
	}

	@Override
	public boolean isValid() {
		// For now always valid to run
		return true;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "W_R"; 
		return str;
	}

}

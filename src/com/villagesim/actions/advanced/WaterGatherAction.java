package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class WaterGatherAction implements Action {
	
	private Person person;
	
	public WaterGatherAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		System.out.println("Person id: " + person.getId() + " is gathering water.");
	}

}

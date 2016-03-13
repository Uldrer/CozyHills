package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class RunMoveAction implements Action {

	private Person person;
	
	public RunMoveAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute() {
		// TODO move person in some given direction faster
		System.out.println("Person id: " + person.getId() + " is running.");
	}

}

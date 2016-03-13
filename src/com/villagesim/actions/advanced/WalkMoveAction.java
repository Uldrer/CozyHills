package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class WalkMoveAction implements Action {

	private Person person;
	
	public WalkMoveAction(Person person)
	{
		this.person = person;
	}

	
	@Override
	public void execute() {
		
		// TODO move person in some given direction
		System.out.println("Person id: " + person.getId() + " is wakling.");

	}

}

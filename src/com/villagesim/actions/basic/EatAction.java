package com.villagesim.actions.basic;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class EatAction implements Action {

	private Person person;
	
	public EatAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute() {
		
		// TODO add nutrition to person if it is available
		System.out.println("Person id: " + person.getId() + " is eating.");
		
	}

}

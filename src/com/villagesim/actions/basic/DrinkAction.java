package com.villagesim.actions.basic;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class DrinkAction implements Action {

	private Person person;
	
	public DrinkAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public boolean execute(int seconds) {

		// TODO add aqua to person if it is available
		
		person.drink(seconds);
		
		System.out.println("Person id: " + person.getId() + " is drinking.");
		
		return true;

	}

}

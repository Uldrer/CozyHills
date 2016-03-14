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
	public void execute(int seconds) {

		// TODO add aqua to person if it is available
		
		person.drink(seconds);
		
		System.out.println("Person id: " + person.getId() + " is drinking.");
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}

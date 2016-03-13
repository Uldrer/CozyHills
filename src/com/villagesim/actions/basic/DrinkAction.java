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
	public void execute() {

		// TODO add aqua to person if it is available

	}

}

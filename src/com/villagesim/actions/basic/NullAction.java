package com.villagesim.actions.basic;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class NullAction implements Action {
	
	private Person person;
	
	public NullAction(Person person)
	{
		this.person = person;
	}

	@Override
	public void execute(int seconds) {
		System.out.println("Person id: " + person.getId() + " doing no action.");
	}

	@Override
	public boolean isValid() {
		// It is always valid to chill
		return true;
	}

}

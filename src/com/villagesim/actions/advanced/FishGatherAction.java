package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class FishGatherAction implements Action {

	private Person person;
	
	public FishGatherAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public boolean execute(int seconds) {
		// TODO Auto-generated method stub
		System.out.println("Person id: " + person.getId() + " is fishing.");
		return true;
	}

}

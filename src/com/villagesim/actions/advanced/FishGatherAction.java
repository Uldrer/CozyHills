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
	public void execute() {
		// TODO Auto-generated method stub

	}

}
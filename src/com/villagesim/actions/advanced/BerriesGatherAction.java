package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class BerriesGatherAction implements Action {

	private Person person;
	
	public BerriesGatherAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}

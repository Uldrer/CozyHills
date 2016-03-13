package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class NutsGatherAction implements Action {

	private Person person;
	
	public NutsGatherAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}

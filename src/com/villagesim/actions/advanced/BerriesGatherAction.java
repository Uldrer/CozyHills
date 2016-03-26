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
	public void execute(int seconds) {
		// TODO Auto-generated method stub
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is gathering berries.");
		}
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}


}

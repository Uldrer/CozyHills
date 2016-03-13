package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class HuntGatherAction implements Action {
	
	private Person person;
	
	public HuntGatherAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		System.out.println("Person id: " + person.getId() + " is hunting.");
	}

}

package com.villagesim.actions.advanced;

import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Printable;
import com.villagesim.people.Person;

public class WoodGatherAction implements Action, Printable  {

	private Person person;
	
	public WoodGatherAction(Person person)
	{
		this.person = person;
	}
	
	@Override
	public void execute(int seconds) {
		// TODO Auto-generated method stub
		if(person.printDebug())
		{
			System.out.println("Person id: " + person.getId() + " is gathering wood.");
		}
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String getDebugPrint() {
		String str = "G_Wo"; 
		return str;
	}

}

package com.villagesim.actions;

import java.util.HashSet;
import java.util.Set;

import com.villagesim.interfaces.Action;

public class ActionMediator {
	
	private static Set<Action> actionSet = new HashSet<Action>(); 
	
	public static void addAction(Action action)
	{
		actionSet.add(action);
	}
	
	
	public static void executeActions(int seconds)
	{
		for (Action action : actionSet)
		{
			action.execute(seconds);
	    }
		actionSet.clear();
	}
	

}

package com.villagesim.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.villagesim.interfaces.Action;

public class ActionMediator {
	
	private static Set<List<Action>> actionListSet = new HashSet<List<Action>>(); 
	
	public static void addActionList(List<Action> action)
	{
		actionListSet.add(action);
	}
	
	
	public static void executeActions(int seconds)
	{
		for (List<Action> actionList : actionListSet)
		{
			// Loop over prioritized list, if success move on
			for(Action action : actionList)
			{
				boolean success = action.execute(seconds);
				if(success) break;
			}
	    }
		actionListSet.clear();
	}
	

}

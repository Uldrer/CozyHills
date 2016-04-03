package com.villagesim.actions;

import java.util.ArrayList;
import java.util.List;

import com.villagesim.interfaces.Action;

public class ActionMediator {
	
	private static List<List<Action>> actionListSet = new ArrayList<List<Action>>(); 
	
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
				if(action.isValid()) 
				{
					action.execute(seconds);
					break;
				}
			}
	    }
		actionListSet.clear();
	}
	

}

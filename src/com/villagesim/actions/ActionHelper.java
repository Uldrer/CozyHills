package com.villagesim.actions;

public class ActionHelper {
	
	public static int getAdvancedActionSize(String actionType)
	{
		// Determine advanced actions size
		int size = 0;
		for(AdvancedAction action : AdvancedAction.values())
		{
			// Use only gather for now
			if(action.getActionType().equals(actionType))
			{
				size++;
			}
		}
		return size;
	}
}

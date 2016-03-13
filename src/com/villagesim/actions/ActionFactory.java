package com.villagesim.actions;

import java.util.ArrayList;
import java.util.List;

import com.villagesim.actions.advanced.BerriesGatherAction;
import com.villagesim.actions.advanced.FishGatherAction;
import com.villagesim.actions.advanced.HuntGatherAction;
import com.villagesim.actions.advanced.NutsGatherAction;
import com.villagesim.actions.advanced.RunMoveAction;
import com.villagesim.actions.advanced.WalkMoveAction;
import com.villagesim.actions.advanced.WaterGatherAction;
import com.villagesim.actions.basic.DrinkAction;
import com.villagesim.actions.basic.EatAction;
import com.villagesim.actions.basic.NullAction;
import com.villagesim.helpers.AdvancedActionComparator;
import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class ActionFactory {
	private Person person;
	
	public ActionFactory(Person person)
	{
		this.person = person;
	}
	
	public Action getAction(int index)
	{
		List<AdvancedAction> actionList = new ArrayList<AdvancedAction>();
		for (AdvancedAction action : AdvancedAction.values()) 
		{
			if(index == action.getIndex())
			{
				actionList.add(action);
			}
		}
		
		if(actionList.isEmpty())
		{
			for (BasicAction action : BasicAction.values()) 
			{
				if(index == action.getIndex())
				{
					return createBasicAction(action);
				}
			}
			
		}
		else
		{
			// TODO do some fancy decision making here instead of using rank
			actionList.sort(new AdvancedActionComparator());
			
			// Return first applicable action
			createAdvancedAction(actionList.get(0));
		}
		
		return new NullAction(person);
	}
	
	private Action createAdvancedAction(AdvancedAction actionType)
	{
		Action newAction;
		switch(actionType)
		{
		case BERRIES:
			newAction = new BerriesGatherAction(person);
			break;
		case BUILD:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		case CLIMB:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		case CUT_TREES:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		case FISH:
			newAction = new FishGatherAction(person);
			break;
		case HUNT:
			newAction = new HuntGatherAction(person);
			break;
		case NUTS:
			newAction = new NutsGatherAction(person);
			break;
		case RUN:
			newAction = new RunMoveAction(person);
			break;
		case SWIM:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		case TRANSPORT_GOODS:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		case WALK:
			newAction = new WalkMoveAction(person);
			break;
		case WATER:
			newAction = new WaterGatherAction(person);
			break;
		case WOOD:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		default:
			newAction = new NullAction(person);
			break;
			
		}
		return newAction;
	}
	
	private Action createBasicAction(BasicAction actionType)
	{
		Action newAction;
		switch(actionType)
		{
		case EAT:
			newAction = new EatAction(person);
			break;
		case DRINK:
			newAction = new DrinkAction(person);
			break;
		case GATHER:
			// Should never happen as it has advanced actions
			System.out.println("Trying to create basic GATHER action. Should not happen.");
			newAction = new NullAction(person);
			break;
		case MOVE:
			// Should never happen as it has advanced actions
			System.out.println("Trying to create basic MOVE action. Should not happen.");
			newAction = new NullAction(person);
			break;
		case WORK:
			// Should never happen as it has advanced actions
			System.out.println("Trying to create basic WORK action. Should not happen.");
			newAction = new NullAction(person);
			break;
		case SLEEP:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		case SOCIALIZE:
			// TODO Implement
			newAction = new NullAction(person);
			break;
		default:
			newAction = new NullAction(person);
			break;
		}
		return newAction;
	}
	 

}

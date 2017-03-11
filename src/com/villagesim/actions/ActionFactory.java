package com.villagesim.actions;

import java.util.ArrayList;
import java.util.List;

import com.villagesim.actions.advanced.BerriesGatherAction;
import com.villagesim.actions.advanced.FishGatherAction;
import com.villagesim.actions.advanced.HuntGatherAction;
import com.villagesim.actions.advanced.NutsGatherAction;
import com.villagesim.actions.advanced.RunMoveAction;
import com.villagesim.actions.advanced.WalkRandomMoveAction;
import com.villagesim.actions.advanced.WalkWaterMoveAction;
import com.villagesim.actions.advanced.WalkWoodMoveAction;
import com.villagesim.actions.advanced.WaterGatherAction;
import com.villagesim.actions.advanced.WoodGatherAction;
import com.villagesim.actions.basic.DrinkAction;
import com.villagesim.actions.basic.EatAction;
import com.villagesim.actions.basic.NullAction;
import com.villagesim.interfaces.Action;
import com.villagesim.people.Person;

public class ActionFactory {
	private Person person;
	
	public ActionFactory(Person person)
	{
		this.person = person;
	}
	
	public List<Action> getActions(int index)
	{
		// TODO make this nicer
		List<Action> returnList = new ArrayList<Action>();
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
					returnList.add(createBasicAction(action));
					return returnList;
				}
			}
			
		}
		else
		{
			for (BasicAction action : BasicAction.values()) 
			{
				if(index == action.getIndex())
				{
					List<Action> advancedActions = person.makeAdvancedActionDecision(action);
					return advancedActions;
				}
			}
		}
		returnList.add(new NullAction(person));
		return returnList;
	}
	
	public Action getAdvancedAction(int index, BasicAction basicAction)
	{
		for (AdvancedAction action : AdvancedAction.values()) 
		{
			if(index == action.getRank() && basicAction.getActionType().equals(action.getActionType()))
			{
				return createAdvancedAction(action);
			}
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
		case WALK_RANDOM:
			newAction = new WalkRandomMoveAction(person);
			break;
		case WALK_DIRECTION_WATER:
			newAction = new WalkWaterMoveAction(person);
			break;
		case WALK_DIRECTION_WOOD:
			newAction = new WalkWoodMoveAction(person);
			break;
		case WATER:
			newAction = new WaterGatherAction(person);
			break;
		case WOOD:
			newAction = new WoodGatherAction(person);
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

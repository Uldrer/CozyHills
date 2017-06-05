package com.villageorganismsim;

import java.util.ArrayList;

public class ActionFactory {
	
	private ArrayList<Action> actions;
	
	public ActionFactory()
	{
		actions = new ArrayList<Action>();
		initActions();
	}
	
	public ArrayList<Action> getActions()
	{
		return actions;
	}
	
	private void initActions()
	{
		// TODO create all actions
		
		// Resources
		Resource trees = new Resource("Trees", ResourceType.RawMaterial);
		Resource wood = new Resource("Wood", ResourceType.BuildingMaterial);
		Resource city_land = new Resource("City_land", ResourceType.LandSpace);
		Resource city_built_land = new Resource("City_built_land", ResourceType.CitySpace);
		Resource living_space = new Resource("Living_space", ResourceType.LivingSpace);
		Resource population = new Resource("Population", ResourceType.Population);
		
		// Cut Wood
		{
			Action action = new Action("Cut_Wood");
			// Inputs
			Input treesInput = new Input(trees, 1);
			action.addInput(treesInput);
			// Outputs
			Output woodOutput = new Output(wood, 1);
			action.addOutput(woodOutput);
			
			actions.add(action);
		}
		
		// Build House
		{
			Action action = new Action("Build_House");
			// Inputs
			Input woodInput = new Input(wood, 0.01);
			action.addInput(woodInput);
			Input landInput = new Input(city_land, 0.1);
			action.addInput(landInput);
			// Outputs
			Output landOutput = new Output(city_built_land, 0.1);
			action.addOutput(landOutput);
			Output livingOutput = new Output(living_space, 0.06);
			action.addOutput(livingOutput);
			
			actions.add(action);
		}
		
		// Reproduce
		{
			Action action = new Action("Reproduce");
			// Inputs
			// No inputs so far, something with children?
			// Outputs
			Output populationOutput = new Output(population,1.0/113880.0); // new population at age 13
			action.addOutput(populationOutput);
			
			actions.add(action);
		}
		
	}

}

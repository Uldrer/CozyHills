package com.villageorganismsim;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.villagesim.interfaces.Drawable;

public class City implements Drawable {
	
	private ActionFactory actionFactory;
	private ArrayList<Action> actions;
	private double[] actionWeights;
	private HashMap<ResourceType, Double> storageMap;
	private final int INITIAL_POPULATION_SIZE = 10;
	private ParticleSwarmOptimization pso;
	private int trainingIter = 200;
	private FitnessHelper currentFitnessHelper;
	
	public City()
	{
		// Init actions
		actionFactory = new ActionFactory();
		actions = actionFactory.getActions();
		actionWeights = new double[actions.size()];
		
		// Init storage
		storageMap = new HashMap<ResourceType, Double>();
		initStartupValues();
		
		setPolicy();
		
		// Init descision making
		double[] psoParams = {1.5 , 0.1, 1.5, 2, 1.4, 0.4, 50 , trainingIter, actions.size()};
		pso = new ParticleSwarmOptimization(psoParams);
	}
	
	private void initStartupValues()
	{
		// Add all resource types to map
		for(ResourceType type : ResourceType.values())
		{
			storageMap.put(type, 0.0);
		}
		
		// Init values
		storageMap.put(ResourceType.Population, (double)INITIAL_POPULATION_SIZE);
		storageMap.put(ResourceType.LivingSpace, (double)INITIAL_POPULATION_SIZE);
		storageMap.put(ResourceType.LandSpace, 1000.0);
		storageMap.put(ResourceType.RawMaterial, 100000.0);
		storageMap.put(ResourceType.CitySpace, 10.0);
	}
	

	
	private void setPolicy()
	{
		// TODO set fitness function according to policy
		// Hard-coded for now, Growth
		FitnessHelper fitnessHelper = new FitnessHelper();
		fitnessHelper.addMultiplier(ResourceType.Population, 2.0);
		fitnessHelper.addBounded(ResourceType.LivingSpace, ResourceType.Population, 10);
		fitnessHelper.addThreshold(ResourceType.Food,0.0, 10);
		fitnessHelper.addThreshold(ResourceType.DrinkingWater,0.0, 10);
		fitnessHelper.addThreshold(ResourceType.BuildingMaterial,0.0, 5);
		
		currentFitnessHelper = fitnessHelper;
	}
	
	public void optimizePopulation()
	{
		pso.setActions(actions);
		pso.setFitnessHelper(currentFitnessHelper);
		
		// TODO use optimizer to optimize placement of population
		for(int i = 0; i < trainingIter; i++)
		{
			pso.trainNetwork(i);
		}
		// Get best weights
		actionWeights = pso.getBestWeights();
	}
	
	public void update()
	{
		double populationSize = storageMap.get(ResourceType.Population);
		HashMap<ResourceType, Double> tempStorageMap = new HashMap<ResourceType, Double>(storageMap);
		// Update storage depending on actual focus
		for(int i = 0; i < actions.size(); i++)
		{
			Action action = actions.get(i);
			double weight = actionWeights[i];
			
			// Inputs
			for(Input input : action.getInputs())
			{
				ResourceType type = input.getResourceType();
				double amount = populationSize*weight*input.getConsumeRate();
				double oldVal = tempStorageMap.get(type);
				double newVal = oldVal - amount;
				tempStorageMap.put(type, newVal);
			}
			
			// Inputs
			for(Output output : action.getOutputs())
			{
				ResourceType type = output.getResourceType();
				double amount = populationSize*weight*output.getProductionRate();
				double oldVal = tempStorageMap.get(type);
				double newVal = oldVal + amount;
				tempStorageMap.put(type, newVal);
			}
		}
		storageMap = tempStorageMap;
		
		// Print result
		//printStorage(storageMap);
	}
	
	private void printStorage(HashMap<ResourceType, Double> mapToPrint)
	{
		System.out.println("Printing map:");
		Iterator<Entry<ResourceType, Double>> it = mapToPrint.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<ResourceType, Double> pair = it.next();
			System.out.println("Type:" + pair.getKey() + ":" + pair.getValue());
		}
		
	}

	@Override
	public void draw(Graphics bbg) 
	{
		// TODO Draw total land
		
		printStorage(storageMap);
		// Draw living areas
		/* TODO
		bbg.setColor(Color.BLACK);
		double x_mid = coordinate.getX() - width/2;
		double y_mid = coordinate.getY() - height/2;
		bbg.fillRect((int)(x_mid+0.5), (int)(y_mid+0.5), width, height);
		*/
	}
}

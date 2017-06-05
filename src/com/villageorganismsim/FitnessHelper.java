package com.villageorganismsim;

import java.util.ArrayList;
import java.util.HashMap;

public class FitnessHelper {
	
	private class ThresholdFitness
	{
		ResourceType type;
		double threshold;
		double negativeScore;
		
		public ThresholdFitness(ResourceType type, double threshold, double negativeScore)
		{
			this.type = type;
			this.threshold = threshold;
			this.negativeScore = negativeScore;
		}
		public ResourceType getType() { return type; }
		public double getThreshold() { return threshold; }
		public double getNegativeScore() { return negativeScore; }
	}
	
	private class MultiplierFitness
	{
		ResourceType type;
		double multiplier;
		
		public MultiplierFitness(ResourceType type, double multiplier)
		{
			this.type = type;
			this.multiplier = multiplier;
		}
		public ResourceType getType() { return type; }
		public double getMultiplier() { return multiplier; }
	}
	
	private class BoundedFitness
	{
		ResourceType srcType;
		ResourceType boundedType;
		double negativeScore;
		
		public BoundedFitness(ResourceType srcType, ResourceType boundedType, double negativeScore)
		{
			this.srcType = srcType;
			this.boundedType = boundedType;
			this.negativeScore = negativeScore;
		}
		public ResourceType getSrcType() { return srcType; }
		public ResourceType getBoundedType() { return boundedType; }
		public double getNegativeScore() { return negativeScore; }
	}
	
	private ArrayList<ThresholdFitness> thresholdFitness;
	private ArrayList<MultiplierFitness> multiplierFitness;
	private ArrayList<BoundedFitness> boundedFitness;
	
	public FitnessHelper()
	{
		thresholdFitness = new ArrayList<ThresholdFitness>();
		multiplierFitness = new ArrayList<MultiplierFitness>();
		boundedFitness = new ArrayList<BoundedFitness>();
	}
	
	// Add fitness threshold that if it is not ok gives negative evaluation score
	public void addThreshold(ResourceType type, double threshold, double negativeScore)
	{
		thresholdFitness.add(new ThresholdFitness(type, threshold, negativeScore));
	}
	
	// Add fitness multiplier that scores multiplied with value
	public void addMultiplier(ResourceType type, double multiplier)
	{
		multiplierFitness.add(new MultiplierFitness(type, multiplier));
	}
	
	// Add fitness bounded that if it is not ok gives negative evaluation score
	public void addBounded(ResourceType srcType, ResourceType boundedType, double negativeScore)
	{
		boundedFitness.add(new BoundedFitness(srcType, boundedType, negativeScore));
	}
	
	public double computeFitness(HashMap<ResourceType, Double> mapToEvaluate)
	{
		double fitness = 0;
		
		// Threshold fitness
		for(ThresholdFitness thFitness : thresholdFitness)
		{
			double valueOfType = mapToEvaluate.get(thFitness.getType());
			if(valueOfType < thFitness.getThreshold())
			{
				fitness -= thFitness.getNegativeScore();
			}
		}
		
		// Multiplier fitness
		for(MultiplierFitness thFitness : multiplierFitness)
		{
			double valueOfType = mapToEvaluate.get(thFitness.getType());
			
			fitness += valueOfType*thFitness.getMultiplier();
		}
		
		// Bounded fitness
		for(BoundedFitness thFitness : boundedFitness)
		{
			double valueOfSrcType = mapToEvaluate.get(thFitness.getSrcType());
			double valueOfBoundedType = mapToEvaluate.get(thFitness.getBoundedType());
			if(valueOfSrcType < valueOfBoundedType)
			{
				fitness -= thFitness.getNegativeScore();
			}
		}

		return fitness;
	}
	
	public HashMap<ResourceType, Double> computeChangeRates(double[] weights, ArrayList<Action> actions)
	{
		HashMap<ResourceType, Double> tempChangeMap = new HashMap<ResourceType, Double>();
		
		// Init so that we have all values
		for(ResourceType type : ResourceType.values())
		{
			tempChangeMap.put(type, 0.0);
		}
		
		// Update storage depending on actual focus
		for(int i = 0; i < actions.size(); i++)
		{
			Action action = actions.get(i);
			double weight = weights[i];
			
			// Inputs
			for(Input input : action.getInputs())
			{
				ResourceType type = input.getResourceType();
				double old_val = tempChangeMap.get(type);
				double new_val = old_val-weight*input.getConsumeRate();
				tempChangeMap.put(type, new_val);
			}
			
			// Outputs
			for(Output output : action.getOutputs())
			{
				ResourceType type = output.getResourceType();
				double old_val = tempChangeMap.get(type);
				double new_val = old_val+weight*output.getProductionRate();
				tempChangeMap.put(type, new_val);
			}
		}
		
		return tempChangeMap;
	}

}

package com.villagesim.ui;

import com.villagesim.helpers.FileHandler;
import com.villagesim.helpers.FileHeader.WeightType;
import com.villagesim.optimizer.GeneticAlgorithm;

public class GATrainer {
	
	int trainingIter;
	// mutation-rate , crossover probability, population size, tournament select param, number of best to insert, mutate width
	double[] gaParam = {0.4 , 0.7, 100, 0.75 , 5, 0.2};
	
	public GATrainer()
	{
		 trainingIter = 1000;
	}
	
	public void run()
	{
		
		 GeneticAlgorithm GA = new GeneticAlgorithm(gaParam, true);
		 double bestTotalLifeTime = 3.1; // better than dehydration
         //GA
         for (int i = 0; i < trainingIter; i++)
         {
             GA.trainNetwork();

             double[][][][] weights = GA.getBestWeights();
             double bestLifeTime = GA.getBestScore();

             System.out.println("Iteration GA: " + i + " best lifetime: " + bestLifeTime);
             
             if(bestLifeTime > bestTotalLifeTime)
             {
            	 bestTotalLifeTime = bestLifeTime;
            	 FileHandler.writeWeightsToFile(weights[0], "weights.txt", WeightType.MAIN);
            	 FileHandler.writeWeightsToFile(weights[1], "gatherWeights.txt", WeightType.GATHER);
            	 FileHandler.writeWeightsToFile(weights[2], "moveWeights.txt", WeightType.MOVE);
            	 FileHandler.writeWeightsToFile(weights[3], "workWeights.txt", WeightType.WORK);
             }
         }
	}

}

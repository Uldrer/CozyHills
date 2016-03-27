package com.villagesim.ui;

import com.villagesim.helpers.FileHandler;
import com.villagesim.optimizer.GeneticAlgorithm;

public class GATrainer {
	
	int trainingIter;
	// mutation-rate , crossover probability, population size, tournament select param, number of best to insert, mutate width
	double[] gaParam = {0.4 , 0.4, 100, 0.75 , 2, 0.5};
	
	public GATrainer()
	{
		 trainingIter = 1000;
	}
	
	public void run()
	{
		
		 GeneticAlgorithm GA = new GeneticAlgorithm(gaParam);
		 double bestToalLifeTime = 3.1; // better than dehydration
         //GA
         for (int i = 0; i < trainingIter; i++)
         {
             GA.trainNetwork();

             double[][][] weights = GA.getBestWeights();
             double bestLifeTime = GA.getBestScore();

             System.out.println("Iteration GA: " + i + " best lifetime: " + bestLifeTime);
             
             if(bestLifeTime > bestToalLifeTime)
             {
            	 bestToalLifeTime = bestLifeTime;
            	 FileHandler.writeWeightsToFile(weights);
             }
         }
	}

}

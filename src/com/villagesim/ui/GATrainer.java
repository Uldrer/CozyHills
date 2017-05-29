package com.villagesim.ui;

import com.villagesim.actions.BasicAction;
import com.villagesim.helpers.FileHandler;
import com.villagesim.helpers.FileHeader.WeightType;
import com.villagesim.optimizer.GeneticAlgorithm;
import com.villagesim.sensors.Sensor;

public class GATrainer {
	
	int trainingIter;
	// mutation-rate (should be 1/genes), crossover probability (0.6-0.8), population size (100-10000), tournament select param, number of best to insert, mutate width, average for scoring
	double[] gaParam = {1.0/(BasicAction.size*Sensor.size) , 0.7, 100, 0.75 , 5, 0.2, 5};
	
	public GATrainer()
	{
		 trainingIter = 100;
	}
	
	public void run()
	{
		
		 GeneticAlgorithm GA = new GeneticAlgorithm(gaParam, true);
		 double bestTotalLifeTime = 3.1; // better than dehydration
         //GA
         for (int i = 0; i < trainingIter; i++)
         {
             GA.trainNetwork(i);

             double[][][][] weights = GA.getBestWeights();
             double bestLifeTime = GA.getBestScore();

             System.out.println("Iteration GA: " + i + " best lifetime: " + bestLifeTime);
             
             if(bestLifeTime > bestTotalLifeTime)
             {
            	 bestTotalLifeTime = bestLifeTime;
            	 FileHandler.writeWeightsToFile(weights[0], "weights.txt", WeightType.MAIN);
             }
         }
	}

}

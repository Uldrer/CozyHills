package com.villagesim.ui;

import com.villagesim.helpers.FileHandler;
import com.villagesim.helpers.FileHeader.WeightType;
import com.villagesim.optimizer.ParticleSwarmOptimization;

public class PSOTrainer {
	
	int trainingIter;
	
	// c1 , c2, v_max, w, swarm size, score iterations
	double[] psoParam = {1.5 , 1.5, 2, 1, 50 , 5};
	
	public PSOTrainer()
	{
		 trainingIter = 100;
	}
	
	public void run()
	{
		
		 ParticleSwarmOptimization PSO = new ParticleSwarmOptimization(psoParam);
		 double bestTotalLifeTime = 3.1; // better than dehydration
         //PSO
         for (int i = 0; i < trainingIter; i++)
         {
        	 PSO.trainNetwork(i);

             double[][][] weights = PSO.getBestWeights();
             double bestLifeTime = PSO.getBestScore();

             System.out.println("Iteration PSO: " + i + " best lifetime: " + bestLifeTime);
             
             if(bestLifeTime > bestTotalLifeTime)
             {
            	 bestTotalLifeTime = bestLifeTime;
            	 FileHandler.writeWeightsToFile(weights, "weights.txt", WeightType.MAIN);
             }
         }
	}
}
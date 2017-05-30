package com.villagesim.ui;

import com.villagesim.helpers.FileHandler;
import com.villagesim.helpers.FileHeader.WeightType;
import com.villagesim.optimizer.ParticleSwarmOptimization;

public class PSOTrainer {
	
	int trainingIter = 100;
	
	// c1 , c2_start, c2_end, v_max, w_start, w_end, swarm size, score iterations, total training iterations
	double[] psoParam = {1.5 , 0.1, 1.5, 2, 1.4, 0.4, 50 , 5, trainingIter};
	
	public PSOTrainer()
	{
	}
	
	public void run()
	{
		 boolean addBestToSwarm = true;
		 ParticleSwarmOptimization PSO = new ParticleSwarmOptimization(psoParam, addBestToSwarm);
		 double bestTotalLifeTime = 3.1; // better than dehydration
         //PSO
         for (int i = 0; i < trainingIter; i++)
         {
        	 PSO.trainNetwork(i);

             double[][][] weights = PSO.getBestWeights();
             double bestLifeTime = PSO.getBestScore();
             
             if(bestLifeTime > bestTotalLifeTime)
             {
            	 bestTotalLifeTime = bestLifeTime;
            	 FileHandler.writeWeightsToFile(weights, "weights.txt", WeightType.MAIN);
             }
         }
	}
}
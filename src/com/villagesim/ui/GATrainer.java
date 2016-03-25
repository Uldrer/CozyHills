package com.villagesim.ui;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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

         //GA
         for (int i = 0; i < trainingIter; i++)
         {
             GA.trainNetwork();

             double[][][] weights = GA.getBestWeights();
             double bestLifeTime = GA.getBestScore();

             System.out.println("Iteration GA: " + i + " best lifetime: " + bestLifeTime);
             
             if(bestLifeTime > 3.1)
             {
            	 writeWeightsToFile(weights);
             }
         }
	}
	
	private void writeWeightsToFile(double[][][] weights)
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter("weights.txt", "UTF-8");
			
			for (int i = 0; i < weights.length; i++)
            {
                for (int j = 0; j < weights[i].length; j++)
                {
                    for (int k = 0; k < weights[i][j].length; k++)
                    {
                    	writer.print(String.valueOf(weights[i][j][k]));
                        if (k != weights[i][j].length - 1)
                        {
                        	writer.print(" ");
                        }
                    }
                    writer.println();
                }
            }
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

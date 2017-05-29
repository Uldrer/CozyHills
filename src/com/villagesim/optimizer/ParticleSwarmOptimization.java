package com.villagesim.optimizer;

import java.util.Random;

import com.villagesim.VillageSimulator;
import com.villagesim.actions.BasicAction;
import com.villagesim.sensors.Sensor;

public class ParticleSwarmOptimization {
	
	private static final double X_MAX = 1;
	
	private static final double X_MIN = -1;

	/// A random number generator.
    private static Random rand = new Random();
    
    /// The current best fitness score for global best. 
    private double bestGlobalScore = 0;
    
  /// The current best fitness score for local best of swarm. 
    private double[] bestLocalScores;
    
    /// The swarm positions (weights)
    private double[][][][] swarmPositions;
    
    // The swarm velocities (weight velocities)
    private double[][][][] swarmVelocities;
    
    /// The local best swarm positions (weights)
    private double[][][][] swarmLocalBestPositions;
    
    /// The stored current best particle position
    private double[][][] swarmGlobalBestPosition;
    
    /// c1, the cognative constant
    private double c1;
    
    /// c2, the social constant
    private double c2;
    
    /// v_max, the maximum allowed absolute velocity
    private double v_max;
    
    /// w, intertia weight
    private double w;
    
    /// the swarm size
    private int swarmSize;
    
    /// the number of runs to average score over
    private int averageRuns;
    
    /// The simulator to use for evaluation
    private VillageSimulator villageSimulator;
    
    /// The basic artificial neural network for which the basic weights are trained.
    private ArtificialNeuralNetwork basicNetwork;
    
    // Parameters: c1 , c2, v_max, w, swarm size, score iterations
    public ParticleSwarmOptimization(double[] psoParameters)
    {
    	// init
    	this.c1 = psoParameters[0];
    	this.c2 = psoParameters[1];
    	this.v_max = psoParameters[2];
    	this.w = psoParameters[3];
    	this.swarmSize = (int)psoParameters[4];
    	this.averageRuns = (int)psoParameters[5];
    	
    	basicNetwork = new ArtificialNeuralNetwork(Sensor.size, new int[]{}, BasicAction.size);
    	
    	initiateRandomSwarm();
        
        villageSimulator = new VillageSimulator();
    }
    
    public void trainNetwork(int iteration)
    {
    	// Evaluate all swarm particles
    	for (int i = 0; i < swarmSize; i++)
        {
    		// Evaluate particle
            double[][][] weights = swarmPositions[i];
            double score = evaluateIndividual(weights);

            // Update best local
            if (score > bestLocalScores[i])
            { 
            	bestLocalScores[i] = score;
            	swarmLocalBestPositions[i] = OptimizationHelper.copy(weights);
            }
            
            // Update best global
            if (score > bestGlobalScore)
            { 
            	bestGlobalScore = score;
            	swarmGlobalBestPosition = OptimizationHelper.copy(weights);
            }
        }
    	
    	
    	for (int i = 0; i < swarmSize; i++)
    	{
    		// Update values
    		for (int j = 0; j < swarmVelocities[i].length; j++)
            {
                for (int k = 0; k < swarmVelocities[i][j].length; k++)
                {
                    for (int l = 0; l < swarmVelocities[i][j][k].length; l++)
                    {
                    	double q = rand.nextDouble();
                    	double r = rand.nextDouble();
                    	
                    	// Update velocities
                    	double oldVel = swarmVelocities[i][j][k][l];
                    	double x = swarmPositions[i][j][k][l];
                    	double x_pb = swarmLocalBestPositions[i][j][k][l];
                    	double x_sb = swarmGlobalBestPosition[j][k][l];
                		double newVel = w*oldVel + c1*q*(x_pb-x) + c2*r*(x_sb-x);
                		swarmVelocities[i][j][k][l] = newVel;
                		
                		// Restrict velocities
                		if(Math.abs(newVel) > v_max)
                		{
                			if(newVel < 0)
                			{
                				newVel = -v_max;
                			}
                			else
                			{
                				newVel = v_max;
                			}
                		}
                    	
                    	// Update particle positions
                		double newPos = x + newVel;
                		
                		//Make sure it is within -1, 1
                		swarmPositions[i][j][k][l] = Math.tanh(newPos);                   
                	}

                }
            }
    		
    	}
    }
    
    /// Initiates a swarm of random position and random velocities.
    private void initiateRandomSwarm()
    {
    	swarmPositions = new double[swarmSize][][][];
    	swarmVelocities = new double[swarmSize][][][];
    	swarmLocalBestPositions = new double[swarmSize][][][];
    	bestLocalScores = new double[swarmSize];

    	// Initiate random positions
        for (int i = 0; i < swarmSize; i++)
        {
        	double[][][] weightsArray = basicNetwork.initiateRandomWeights();
        	swarmPositions[i] = OptimizationHelper.copy(weightsArray);
        }
    	
    	// Initiate random velocities
        for (int i = 0; i < swarmSize; i++)
    	{
        	double[][][] temp1 = new double[swarmPositions[i].length][][];
    		// Update values
    		for (int j = 0; j < swarmPositions[i].length; j++)
            {
    			double[][] temp2 = new double[swarmPositions[i][j].length][];
                for (int k = 0; k < swarmPositions[i][j].length; k++)
                {
                	double[] temp3 = new double[swarmPositions[i][j][k].length];
                    for (int l = 0; l < swarmPositions[i][j][k].length; l++)
                    {
                    	double r = rand.nextDouble();
                    	double velocity = X_MIN + r*(X_MAX-X_MIN);
                    	temp3[l] = velocity;
                    	
                    }
                    temp2[k] = temp3;
                }
                temp1[j] = temp2;
            }
    		swarmVelocities[i] = temp1;
    	}
    }
    
    /// Evaluation function for an individual of weights. 
    /// Computes the energy of five random patterns in the network. 
    /// The fitness is the inverse of this energy with respect to number of data points.
    /// <param name="weights">The weights to evaluate.</param>
    /// <returns>Returns the fitness of these weights.</returns>
    private double evaluateIndividual(double[][][] basicWeights)
    {
    	double score = 0;
    	for(int i = 0; i < averageRuns; i++)
    	{
	        // Add a person with this weight to simulator
	        villageSimulator.addPerson(basicWeights);
	        
	        // Let his life play out
	        while(villageSimulator.isAlive())
	        {
	        	villageSimulator.update();
	        }
	        
	        // For now, give fitness score according to long life
	        score += villageSimulator.getLifeTimeDays(basicWeights);
	        
	        // Reset state for next individual
	        villageSimulator.resetState();
    	}

        return score/averageRuns;
    }
    
    public double getBestScore()
    {
    	return bestGlobalScore;
    }

    /// Get method to get the current best weights.
    public double[][][] getBestWeights()
    {
        return swarmGlobalBestPosition;
    }
}

package com.villagesim.optimizer;

import java.util.Random;

import com.villagesim.VillageSimulator;
import com.villagesim.actions.BasicAction;
import com.villagesim.helpers.FileHandler;
import com.villagesim.sensors.Sensor;

public class ParticleSwarmOptimization {
	
	private static final double X_MAX = 1;
	
	private static final double X_MIN = -1;

	/// A random number generator.
    private static Random rand = new Random();
    
    /// The current best fitness score for global best. 
    private double bestGlobalScore = 0;
    
    // The id of the best particle
    private int bestParticle;
    
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
    
    /// c2 start, the start value of c2
    private double c2_start;
    
    /// c2 end, the end value of c2
    private double c2_end;
    
    /// Computed from c2_start and c2_end, how much it should change per iteration
    private double c2_change_rate;
    
    /// v_max, the maximum allowed absolute velocity
    private double v_max;
    
    /// w, intertia weight
    private double w;
    
    /// w start, the start value of w
    private double w_start;
    
    /// w end, the end value of w
    private double w_end;
    
    /// Computed from w_start and w_end, how much it should change per iteration
    private double w_change_rate;
    
    /// the swarm size
    private int swarmSize;
    
    /// the number of runs to average score over
    private int averageRuns;
    
    /// The total number of iterations to apply
    private int totalIterations;
    
    /// The simulator to use for evaluation
    private VillageSimulator villageSimulator;
    
    /// The basic artificial neural network for which the basic weights are trained.
    private ArtificialNeuralNetwork basicNetwork;
    
    // c1 , c2_start, c2_end, v_max, w_start, w_end, swarm size, score iterations, total training iterations
    public ParticleSwarmOptimization(double[] psoParameters, boolean addBestToSwarm)
    {
    	// init
    	this.c1 = psoParameters[0];
    	this.c2 = psoParameters[1];
    	this.c2_start = psoParameters[1];
    	this.c2_end = psoParameters[2];
    	this.v_max = psoParameters[3];
    	this.w = psoParameters[4];
    	this.w_start = psoParameters[4];
    	this.w_end = psoParameters[5];
    	this.swarmSize = (int)psoParameters[6];
    	this.averageRuns = (int)psoParameters[7];
    	this.totalIterations = (int)psoParameters[8];
    	
    	basicNetwork = new ArtificialNeuralNetwork(Sensor.size, new int[]{}, BasicAction.size);
    	
    	initiateRandomSwarm();
    	
    	// Compute increase/decrease rates of c2 and w
    	c2_change_rate = computeChangeRate(c2_start, c2_end, totalIterations);
    	w_change_rate = computeChangeRate(w_start, w_end, totalIterations);
    	
    	if(addBestToSwarm)
    	{
    		addBestWeights();
    	}
        
        villageSimulator = new VillageSimulator(false);
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
            	System.out.println("New best local score found. Score: " + score + " for particle: " + i);
            	bestLocalScores[i] = score;
            	swarmLocalBestPositions[i] = OptimizationHelper.copy(weights);
            }
            
            // Update best global
            if (score > bestGlobalScore)
            { 
            	System.out.println("New best global score found. Score: " + score + " for particle: " + i);
            	bestGlobalScore = score;
            	swarmGlobalBestPosition = OptimizationHelper.copy(weights);
            	bestParticle = i;
            }
        }
    	
    	FileHandler.logScoreToFile(bestLocalScores, iteration);
    	
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
    	
    	// Update c2 and w
    	c2 += c2_change_rate;
    	w += w_change_rate;
    	
    	// Print result
    	System.out.println("Iteration PSO: " + iteration + " best lifetime: " + bestGlobalScore + " for particle: " + bestParticle);
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
    
    private void addBestWeights()
    {
    	double[][][][] bestWeights = FileHandler.retrieveWeights("weights.txt", basicNetwork);
    	
    	// Add old best weights to initial population
    	swarmPositions[0] = OptimizationHelper.copy(bestWeights[0]);
    }
    
    private double computeChangeRate(double c2_start, double c2_end, int totalIterations)
    {
    	double change_rate = (c2_end-c2_start)/totalIterations;
    	return change_rate;
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

package com.villageorganismsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class ParticleSwarmOptimization {
	
	private static final double X_MAX = 1;
	
	private static final double X_MIN = 0;

	/// A random number generator.
    private static Random rand = new Random();
    
    /// The current best fitness score for global best. 
    private double bestGlobalScore = -100000;
    
    // The id of the best particle
    private int bestParticle;
    
    /// The current best fitness score for local best of swarm. 
    private double[] bestLocalScores;
    
    /// The swarm positions (weights)
    private double[][] swarmPositions;
    
    // The swarm velocities (weight velocities)
    private double[][] swarmVelocities;
    
    /// The local best swarm positions (weights)
    private double[][] swarmLocalBestPositions;
    
    /// The stored current best particle position
    private double[] swarmGlobalBestPosition;
    
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
    
    /// The total number of iterations to apply
    private int totalIterations;
    
    /// The size of the weight array to optimize
    private int weightSize;
    
    /// Help class for evaluating fitness
    private FitnessHelper fitnessHelper;
    
    /// Actions possible
    private ArrayList<Action> actions;
    
    // c1 , c2_start, c2_end, v_max, w_start, w_end, swarm size, total training iterations, weight size
    public ParticleSwarmOptimization(double[] psoParameters)
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
    	this.totalIterations = (int)psoParameters[7];
    	this.weightSize = (int)psoParameters[8];
    	
    	initiateRandomSwarm();
    	
    	// Compute increase/decrease rates of c2 and w
    	c2_change_rate = computeChangeRate(c2_start, c2_end, totalIterations);
    	w_change_rate = computeChangeRate(w_start, w_end, totalIterations);
    	
    }
    
    public void trainNetwork(int iteration)
    {
    	// Evaluate all swarm particles
    	for (int i = 0; i < swarmSize; i++)
        {
    		// Evaluate particle
            double[] weights = swarmPositions[i];
            double score = evaluateIndividual(weights);

            // Update best local
            if (score > bestLocalScores[i])
            { 
            	System.out.println("New best local score found. Score: " + score + " for particle: " + i);
            	bestLocalScores[i] = score;
            	swarmLocalBestPositions[i] = copy(weights);
            }
            
            // Update best global
            if (score > bestGlobalScore)
            { 
            	System.out.println("New best global score found. Score: " + score + " for particle: " + i);
            	bestGlobalScore = score;
            	swarmGlobalBestPosition = copy(weights);
            	bestParticle = i;
            }
            System.out.println("Done with particle: " + i + " current score: " + score);
        }
    	
    	for (int i = 0; i < swarmSize; i++)
    	{
    		double sum = 0;
    		double[] weightsArray = new double[weightSize];
    		// Update values
    		for (int j = 0; j < swarmVelocities[i].length; j++)
	        {
            	double q = rand.nextDouble();
            	double r = rand.nextDouble();
            	
            	// Update velocities
            	double oldVel = swarmVelocities[i][j];
            	double x = swarmPositions[i][j];
            	double x_pb = swarmLocalBestPositions[i][j];
            	double x_sb = swarmGlobalBestPosition[j];
        		double newVel = w*oldVel + c1*q*(x_pb-x) + c2*r*(x_sb-x);
        		swarmVelocities[i][j] = newVel;
        		
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
        		
        		// Limit to [0,1]
        		if(newPos < 0.0) newPos = 0.0;
        		if(newPos > 1.0) newPos = 1.0;
        		
        		weightsArray[j] = newPos;   
        		sum += newPos;
            }	
    		
    		//Make sure it is normalized
    		double [] normalizedWeightsArray = normalizeArray(weightsArray, sum);
        	swarmPositions[i] = copy(normalizedWeightsArray);
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
    	swarmPositions = new double[swarmSize][];
    	swarmVelocities = new double[swarmSize][];
    	swarmLocalBestPositions = new double[swarmSize][];
    	bestLocalScores = new double[swarmSize];
    	Arrays.fill(bestLocalScores,bestGlobalScore);

    	// Initiate random positions
        for (int i = 0; i < swarmSize; i++)
        {
        	double[] weightsArray = new double[weightSize];
        	double sum = 0;
        	for(int j = 0; j < weightSize; j++)
        	{
        		double r = rand.nextDouble();
        		double val = X_MIN + r*(X_MAX- X_MIN);
        		weightsArray[j] = val;
        		sum += val;
        	}
        	// Normalize positions
        	double [] normalizedWeightsArray = normalizeArray(weightsArray, sum);
        	swarmPositions[i] = copy(normalizedWeightsArray);
        }
    	
    	// Initiate random velocities
        for (int i = 0; i < swarmSize; i++)
    	{
        	double[] velocitiesArray = new double[weightSize];
    		// Update values
    		for (int j = 0; j < weightSize; j++)
            {
            	double r = rand.nextDouble();
            	double velocity = -(X_MAX -X_MIN)/2 + r*(X_MAX-X_MIN);
            	velocitiesArray[j] = velocity;
            }
    		swarmVelocities[i] = copy(velocitiesArray);
    	}
    }
    
    private double[] normalizeArray(double[] src, double sum)
    {
    	if(sum == 0)
    	{
    		return src;
    	}
    	double[] dst = new double[src.length];
    	for(int i = 0; i < src.length; i++)
    	{
    		dst[i] = src[i]/sum;
    	}
    	return dst;
    }
    
    private double computeChangeRate(double c2_start, double c2_end, int totalIterations)
    {
    	double change_rate = (c2_end-c2_start)/totalIterations;
    	return change_rate;
    }
    
    private double[] copy(double[] src)
    {
    	double[] dst = src.clone();
    	return dst;
    }
    
    /// Evaluation function for an individual of weights. 
    /// Computes the energy of five random patterns in the network. 
    /// The fitness is the inverse of this energy with respect to number of data points.
    /// <param name="weights">The weights to evaluate.</param>
    /// <returns>Returns the fitness of these weights.</returns>
    private double evaluateIndividual(double[] weights)
    {
    	HashMap<ResourceType, Double> changeRates = fitnessHelper.computeChangeRates(weights, actions);
    	double score = fitnessHelper.computeFitness(changeRates);
    	
        return score;
    }
    
    public void setFitnessHelper(FitnessHelper fitnessHelper)
    {
    	this.fitnessHelper = fitnessHelper;
    }
    
    
    public void setActions(ArrayList<Action> actions)
    {
    	this.actions = actions;
    }
    
    public double getBestScore()
    {
    	return bestGlobalScore;
    }

    /// Get method to get the current best weights.
    public double[] getBestWeights()
    {
        return copy(swarmGlobalBestPosition);
    }
}

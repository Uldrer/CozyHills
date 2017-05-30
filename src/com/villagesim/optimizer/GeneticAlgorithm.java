package com.villagesim.optimizer;
import java.util.Random;

import com.villagesim.VillageSimulator;
import com.villagesim.actions.BasicAction;
import com.villagesim.helpers.FileHandler;
import com.villagesim.sensors.SensorHelper;

public class GeneticAlgorithm {
	
    /// A random number generator.
    private static Random rand = new Random();
    
    /// The population of weights.
    private double[][][][][] population;
    
    /// The stored current best individual of weights.
    private double[][][][] bestWeights;
    
  /// The stored the last best individual of weights.
    private double[][][][] lastBestWeights;
    
    /// The mutation probability for a weight value of an individual to mutate.
    private double mutationRate;
    
    /// The crossover probability of two weight values changing hosts during crossover.
    private double crossoverProb;
    
    /// The parameter that determines how likely we are to choose the better, according to fitness,
    /// of two chosen individuals during <see cref="tournamentSelect"/>.
    private double tournamentSelectParam;
    
    /// The ratio of how much a weight can be modified during mutation. Lies within +- this value.
    private double mutateWidth;
    
    /// The size of the population of weights to train.
    private int populationSize;
    
    /// The number of <see cref="bestWeights"/> to insert into the population at the end of each generation
    /// to preserve some of the best individuals in the population. This is called elitism.
    private int numberOfBestToInsert;
    
    /// The average number of runs to compute score over
    private int averageRuns;
    
    /// The current score vector that holds the fitness values of the current population.
    private double[] score;
    
    /// The basic artificial neural network for which the basic weights are trained.
    private ArtificialNeuralNetwork basicNetwork;
    
    // Change if adding or removing networks
    private final int NUMBER_OF_NETWORKS = 1;
    
    /// The current best fitness score. The score of <see cref="bestWeights"/>.
    private double bestScore;
    
  /// The current best fitness score. The score of <see cref="bestWeights"/>.
    private double lastBestScore;
    
    /// The simulator to use for evaluation
    private VillageSimulator villageSimulator;
    
    private boolean evaluateAllIndividuals = false;
    private boolean bestEvaluated = false;
    private boolean bestEvalChecked = false;
    private boolean newBestInPopulation = false;
    
    public GeneticAlgorithm(double[] gAParameters)
    {
    	this(gAParameters, false);
    }

    /// Class constructor. Initializes a random population of weights according to 
    /// the size of <see cref="network"/>. Zero-thresholds are used.
    /// <param name="inData">The entire input and output data set of patterns.</param>
    /// <param name="theNetwork">The artificial neural network.</param>
    /// <param name="gAParameters">Genetic algorithm parameters. Has the form 
    /// [mutation rate,crossover probability, poplation size, tournament select param, elitism, mutate width]</param>
    public GeneticAlgorithm(double[] gAParameters, boolean addBestWeights)
    {
    	int networkSize = BasicAction.values().length; 
        basicNetwork = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[]{}, networkSize);

        mutationRate = gAParameters[0];
        crossoverProb = gAParameters[1];
        populationSize = (int) gAParameters[2];
        tournamentSelectParam = gAParameters[3];
        numberOfBestToInsert = (int) gAParameters[4];
        mutateWidth = gAParameters[5];
        averageRuns = (int) gAParameters[6];

        initiateRandomPopulation();
        score = new double[populationSize];
        
        if(addBestWeights)
        {
        	addBestWeights();
        }

        bestScore = 0;
        
        villageSimulator = new VillageSimulator(false);
    }
    
    /// Method for training the network. Trains one generation each time using mutation, crossover and
    /// elitism. The best individual in the population is always compared to the best so far and saved 
    /// if it has a better fitness score. Crossover is done among individuals chosen by <see cref="tournamentSelect"/>
    public void trainNetwork(int iteration)
    {
        if(evaluateAllIndividuals)
        {
        	for (int i = 0; i < populationSize; i++)
            {
                double[][][][] weights = population[i];
                
                score[i] = evaluateIndividual(weights[0]);

                if (score[i] > bestScore)
                { 
                    bestScore = score[i];
                    bestWeights = OptimizationHelper.copy(weights);
                }
            }
        }
        else
        {
        	for (int i = 0; i < populationSize; i++)
            {
                double[][][][] weights = population[i];

                if(i < numberOfBestToInsert && bestEvaluated)
                {
                	if(bestEvalChecked)
                	{
    	            	// Don't re-evaluate for best weights, as that takes the longest,
    	            	// we are interested in new combinations that come from these.
    	            	score[i] = bestScore;
                	}
                	else
                	{
                		// Check best again and make sure they are really better the the last over several runs
                		score[i] = evaluateIndividual(weights[0]);
                		System.out.println("Bestscore reevaluated: " + score[i]);
                	}
                }
                else 
                {
                	score[i] = evaluateIndividual(weights[0]);
                	if(bestEvaluated && !bestEvalChecked)
                	{
                		System.out.println("In not bestEvalChecked");
                		// Check if average of new best is better than last best
                		double average = 0;
                		for(int j = 0; j < numberOfBestToInsert; j++)
                		{
                			average += score[j]/numberOfBestToInsert;
                		}
                		
                		System.out.println("In not bestEvalChecked i: "+ i + " average: " + average);
                		
                		if(average > lastBestScore)
                		{
                			bestScore = average;
                		}
                		else
                		{
                			// Go back to last weights, the new ones were a lucky hit
                			bestWeights = OptimizationHelper.copy(lastBestWeights);
                			bestScore = lastBestScore;
                		}
                		bestEvalChecked = true;
                	}
                }
                

                if (score[i] > bestScore)
                { 
                    if(!newBestInPopulation)
                    {
                    	lastBestScore = bestScore;
                    	lastBestWeights = OptimizationHelper.copy(bestWeights);
                    }
                    bestScore = score[i];
                    bestWeights = OptimizationHelper.copy(weights);
                    newBestInPopulation = true;
                }
            }
        }
        
        FileHandler.logScoreToFile(score, iteration);

        double[][][][][] tempPopulation = OptimizationHelper.copy(population);

        if (populationSize == 1)
        {
            tempPopulation[0] = OptimizationHelper.copy(bestWeights);
        }

        //Crossover among the population.
        if (populationSize > 1)
        {
            for (int i = 0; i < populationSize; i = i + 2)
            {
                int index1 = tournamentSelect();
                int index2 = tournamentSelect();
                double r = rand.nextDouble();

                if (r < crossoverProb)
                {
                    double[][][][][] newPair = cross(index1, index2);
                    tempPopulation[i] = OptimizationHelper.copy(newPair[0]);
                    tempPopulation[i + 1] = OptimizationHelper.copy(newPair[1]);
                }
                else
                {
                    tempPopulation[i] = OptimizationHelper.copy(population[index1]);
                    tempPopulation[i + 1] = OptimizationHelper.copy(population[index2]);
                }
            }
        }

        //Mutation of the population
        for (int i = 0; i < populationSize; i++)
        {
            tempPopulation[i] = mutate(population[i]);
        }

        if (populationSize > 2)
        {
            //Insert a number of the best weights in the population
            for (int i = 0; i < numberOfBestToInsert; i++)
            {
            	// TODO FIX: could crash on populationSize <= numberOfBestToInsert
                tempPopulation[i] = OptimizationHelper.copy(bestWeights);
            }
        }
        population = tempPopulation;
        
        // Set after first run
        bestEvaluated = true;
        
        if(newBestInPopulation)
        {
        	bestEvalChecked = false;
        	newBestInPopulation = false;
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
    
    /// Crossover method that returns a new pair of individuals out of two original.
    /// Crossover here works by switching weight values among these individuals 
    /// with a certain probability, <see cref="crossoverProb"/>.
    /// <param name="individ1">The index of the first individual.</param>
    /// <param name="individ2">The index of the second individual.</param>
    /// <returns>Returns a pair of new individuals.</returns>
    private double[][][][][] cross(int individ1, int individ2)
    {
        double[][][][][] newPair = new double[2][][][][];

        newPair[0] = OptimizationHelper.copy(population[individ1]);
        newPair[1] = OptimizationHelper.copy(population[individ2]);

        for (int i = 0; i < newPair[0].length; i++)
        {
            for (int j = 0; j < newPair[0][i].length; j++)
            {
                for (int k = 0; k < newPair[0][i][j].length; k++)
                {
                	for (int l = 0; l < newPair[0][i][j][k].length; l++)
                    {
	                    double randomNr = rand.nextDouble();
	
	                    if (randomNr < crossoverProb)
	                    {
	                        double value1 = population[individ1][i][j][k][l];
	                        double value2 = population[individ2][i][j][k][l];
	                        newPair[0][i][j][k][l] = value2;
	                        newPair[1][i][j][k][l] = value1;
	                    }
                    }
                }
            }
        }

        return newPair;

    }
    
    /// Mutation mehod that mutates accordning to the mutation probability. 
    /// Generates a random number r in [0,1] if r is less than <see cref="mutationRate"/> then
    /// the weight value is modifed by a new r in [-1,1], times <see cref="mutateWidth"/>.
    /// <param name="individual">The individual to mutate.</param>
    /// <returns>Returns the mutated individual.</returns>
    private double[][][][] mutate(double[][][][] individual)
    {
        double[][][][] individ = new double[individual.length][][][];
        for (int i = 0; i < individual.length; i++)
        {
            double[][][] individP1 = new double[individual[i].length][][];
            for (int j = 0; j < individual[i].length; j++)
            {
                double[][] individP2 = new double[individual[i][j].length][];
                for (int k = 0; k < individual[i][j].length; k++)
                {
                	double[] individP3 = new double[individual[i][j][k].length];
                	for (int l = 0; l < individual[i][j][k].length; l++)
                    {
	                    double randomNr = rand.nextDouble();
	
	                    if (randomNr < mutationRate)
	                    {
	                        randomNr = getRandomNumber(-1, 1);
	
	                        individP3[l] = individual[i][j][k][l] + randomNr * mutateWidth;
	                    }
	                    else
	                    {
	                    	individP3[l] = individual[i][j][k][l];
	                    }
                    }
                	individP2[k] = individP3;
                }
                individP1[j] = individP2;
            }
            individ[i] = individP1;
        }
        return individ;
    }
    
    /// A tournament selection method. This method chooses two random individuals, 
    /// the one with the best fitness "wins" with probability equal to the 
    /// <see cref="tournamentSelectParam"/>.
    /// <returns>Returns the index of the winning individual.</returns>
    private int tournamentSelect()
    {
        int[] index = new int[2];
        for (int i = 0; i < index.length; i++)
        {
            index[i] = rand.nextInt(populationSize);
        }

        double randomNr = rand.nextDouble();

        if (randomNr < tournamentSelectParam)
        {
            if (score[index[0]] > score[index[1]])
            {
                return index[0];
            }
            else
            {
                return index[1];
            }
        }
        else
        {
            if (score[index[0]] > score[index[1]])
            {
                return index[1];
            }
            else
            {
                return index[0];
            }
        }
    }
    
    

    /// Generates random double between to double values. 
    /// <param name="min">The minimum value.</param>
    /// <param name="max">The maximum value.</param>
    /// <returns>Returns a double between min and max.</returns>
    private double getRandomNumber(double min, double max)
    {
        return rand.nextDouble() * (max - min) + min;
    }

    /// Initiates a population of random weigths.
    private void initiateRandomPopulation()
    {
        population = new double[populationSize][][][][];

        double[][][][] weightsArray = new double[NUMBER_OF_NETWORKS][][][];
        for (int i = 0; i < populationSize; i++)
        {
        	weightsArray[0] = basicNetwork.initiateRandomWeights();
        	population[i] = OptimizationHelper.copy(weightsArray);
        }
    }
    
    private void addBestWeights()
    {
    	double[][][][] bestWeights = FileHandler.retrieveWeights("weights.txt", basicNetwork);
    	
    	// Add old best weights to initial population
    	for(int i = 0; i < numberOfBestToInsert; i++)
    	{
    		population[i] = OptimizationHelper.copy(bestWeights);
    	}
    	this.bestWeights = OptimizationHelper.copy(bestWeights);;
    }
    
    public double getBestScore()
    {
    	return bestScore;
    }

    /// Get method to get the current best weights.
    public double[][][][] getBestWeights()
    {
        return bestWeights;
    }
    
}

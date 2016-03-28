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
    private double[][][][] population;
    
    /// The stored current best individual of weights.
    private double[][][] bestWeights;
    
    /// The stored values for the network of the current <see cref="bestWeights"/>. To avoid 
    /// re-computation during validation.
    private double[][][] bestNetwork;
    
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
    
    /// The current score vector that holds the fitness values of the current population.
    private double[] score;
    
    /// The artificial neural network for which the weights are trained.
    private ArtificialNeuralNetwork network;
    
    /// The current best fitness score. The score of <see cref="bestWeights"/>.
    private double bestScore;
    
    /// The simulator to use for evaluation
    private VillageSimulator villageSimulator;
    
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
        network = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[]{}, BasicAction.values().length);

        mutationRate = gAParameters[0];
        crossoverProb = gAParameters[1];
        populationSize = (int) gAParameters[2];
        tournamentSelectParam = gAParameters[3];
        numberOfBestToInsert = (int) gAParameters[4];
        mutateWidth = gAParameters[5];

        initiateRandomPopulation();
        score = new double[populationSize];
        
        if(addBestWeights)
        {
        	addBestWeights();
        }

        bestScore = 0;
        
        villageSimulator = new VillageSimulator();
    }
    
    /// Method for training the network. Trains one generation each time using mutation, crossover and
    /// elitism. The best individual in the population is always compared to the best so far and saved 
    /// if it has a better fitness score. Crossover is done among individuals chosen by <see cref="tournamentSelect"/>
    public void trainNetwork()
    {
        
        for (int i = 0; i < populationSize; i++)
        {
            double[][][] weights = population[i];

            score[i] = evaluateIndividual(weights);

            if (score[i] > bestScore)
            { 
                bestScore = score[i];
                bestWeights = copy(weights);
                bestNetwork = network.getNetworks();
            }
        }

        double[][][][] tempPopulation = copyPopulation(population);

        if (populationSize == 1)
        {
            tempPopulation[0] = bestWeights;
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
                    double[][][][] newPair = cross(index1, index2);
                    tempPopulation[i] = newPair[0];
                    tempPopulation[i + 1] = newPair[1];
                }
                else
                {
                    tempPopulation[i] = population[index1];
                    tempPopulation[i + 1] = population[index2];
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
                tempPopulation[i] = copy(bestWeights);
            }
        }
        population = tempPopulation;

    }

    /// Evaluation function for an individual of weights. 
    /// Computes the energy of five random patterns in the network. 
    /// The fitness is the inverse of this energy with respect to number of data points.
    /// <param name="weights">The weights to evaluate.</param>
    /// <returns>Returns the fitness of these weights.</returns>
    private double evaluateIndividual(double[][][] weights)
    {
        // Add a person with this weight to simulator
        villageSimulator.addPerson(weights);
        
        // Let his life play out
        while(villageSimulator.isAlive())
        {
        	villageSimulator.update();
        }
        
        // For now, give fitness score according to long life
        double score = villageSimulator.getLifeTimeDays(weights);
        
        // Reset state for next individual
        villageSimulator.resetState();

        return score;
    }
    
    /// Crossover method that returns a new pair of individuals out of two original.
    /// Crossover here works by switching weight values among these individuals 
    /// with a certain probability, <see cref="crossoverProb"/>.
    /// <param name="individ1">The index of the first individual.</param>
    /// <param name="individ2">The index of the second individual.</param>
    /// <returns>Returns a pair of new individuals.</returns>
    private double[][][][] cross(int individ1, int individ2)
    {
        double[][][][] newPair = new double[2][][][];

        newPair[0] = population[individ1];
        newPair[1] = population[individ2];

        for (int i = 0; i < newPair[0].length; i++)
        {
            for (int j = 0; j < newPair[0][i].length; j++)
            {
                for (int k = 0; k < newPair[0][i][j].length; k++)
                {
                    double randomNr = rand.nextDouble();

                    if (randomNr < crossoverProb)
                    {
                        double value1 = population[individ1][i][j][k];
                        double value2 = population[individ2][i][j][k];
                        newPair[0][i][j][k] = value2;
                        newPair[1][i][j][k] = value1;
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
    private double[][][] mutate(double[][][] individual)
    {
        double[][][] individ = new double[individual.length][][];
        for (int i = 0; i < individual.length; i++)
        {
            double[][] individP1 = new double[individual[i].length][];
            for (int j = 0; j < individual[i].length; j++)
            {
                double[] individP2 = new double[individual[i][j].length];
                for (int k = 0; k < individual[i][j].length; k++)
                {
                    double randomNr = rand.nextDouble();

                    if (randomNr < mutationRate)
                    {
                        randomNr = getRandomNumber(-1, 1);

                        individP2[k] = individP2[k] + randomNr * mutateWidth;
                    }
                    else
                    {
                        individP2[k] = individual[i][j][k];
                    }
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
    
    /// Method to copy the population before applying crossover and mutation.
    /// <param name="thePopulation">The population to copy.</param>
    /// <returns>The copied population.</returns>
    private double[][][][] copyPopulation(double[][][][] thePopulation)
    {
        double[][][][] copy = new double[thePopulation.length][][][];
        for (int i = 0; i < thePopulation.length; i++)
        {
            double[][][] copy1 = new double[thePopulation[i].length][][];
            for (int j = 0; j < thePopulation[i].length; j++)
            {
                double[][] copy2 = new double[thePopulation[i][j].length][];
                for (int k = 0; k < thePopulation[i][j].length; k++)
                {
                    double[] copy3 = new double[thePopulation[i][j][k].length];
                    for (int l = 0; l < thePopulation[i][j][k].length; l++)
                    {
                        copy3[l] = thePopulation[i][j][k][l];
                    }
                    copy2[k] = copy3;
                }
                copy1[j] = copy2;
            }
            copy[i] = copy1;
        }
        return copy;
    }
    
    /// Copy function for weights.
    /// <param name="weights">The weights to copy.</param>
    /// <returns>The copied weights.</returns>
    private double[][][] copy(double[][][] weights)
    {
        double[][][] temp1 = new double[weights.length][][];
        for (int i = 0; i < weights.length; i++)
        {
            double[][] temp2 = new double[weights[i].length][];
            for (int j = 0; j < weights[i].length; j++)
            {
                double[] temp3 = new double[weights[i][j].length];
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    temp3[k] = weights[i][j][k];
                }
                temp2[j] = temp3;
            }
            temp1[i] = temp2;
        }
        return temp1;
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
        population = new double[populationSize][][][];

        for (int i = 0; i < populationSize; i++)
        {
            population[i] = network.initiateRandomWeights();
        }

    }
    
    private void addBestWeights()
    {
    	double[][][] bestWeights = FileHandler.retrieveWeights("weights.txt", network);
    	
    	// Add old best weights to initial population
    	for(int i = 0; i < numberOfBestToInsert; i++)
    	{
    		population[i] = bestWeights;
    	}
    }
    
    public double getBestScore()
    {
    	return bestScore;
    }

    /// Get method to get the current best weights.
    public double[][][] getBestWeights()
    {
        return bestWeights;
    }

    /// Get current best network values.
    public double[][][] getBestNetwork()
    {
        return bestNetwork;
    }
    
}

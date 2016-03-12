package com.villagesim.optimizer;
import java.util.Random;

public class BackPropagation {
	
    /// A random number generator.
    private static Random rand = new Random();

    /// The set of networks for all patterns available in <see cref="data"/>. Here the network represent
    /// the values of all the nodes in the network for a specific pattern.
    private double[][][] theNetworks;

    /// The set of unmodified networks for <see cref="data"/>. The node values haven't been rescaled with tanh(x).
    /// These values are used by <see cref="gPrimFunction"/> as the unmodified values are needed to determine 
    /// how much a weight needs to be changed.
    private double[][][] theUnModNetworks;

    /// The weights of the network. Contains a weight for all edges connecting nodes in the network.
    private double[][][] weights;

    /// The thresholds determining if the input to a specific node is high enough to send the signal forward.
    private double[][] thresholds;

    /// An array of the size of the current network. [inputNodes,hiddenLayer1,....,outputNodes].
    private int[] nodes;
 
    /// The step lenght used for changing the weights.
    private double stepLength;

    /// The weight decay parameter. Makes unused weights disappear.
    private double epsilon;

    /// The momentum paramter, includes a part of the previous change in the weights as a part of the new changes. 
    private double alpha;
    
    /// Is 1 if non-zero thresholds should be used. Otherwise 0.
    private double useThresholds;
    
    /// The data set of patterns to train on. The size is defined by the <see cref="network"/>.
    private double[][] data;
    
    /// The representation of the artificial neural network.
    private ArtificialNeuralNetwork network;

    /// Class constructor. Initializes thresholds and random weights according to the <see cref="network"/>.
    /// <param name="inData">The entire input and output data set.</param>
    /// <param name="theNetwork">The artificial neural network.</param>
    /// <param name="parameters">The parameters used by the algorithm.
    /// Has the form [step length,weight decay  ,momentum, use thresholds].</param>
    public BackPropagation(double[][] inData, ArtificialNeuralNetwork theNetwork, double[] parameters)
    {
        data = inData;
        network = theNetwork;
        nodes = network.getNodes();

        weights = theNetwork.initiateRandomWeights();
        if (parameters[3] == 0)
        {
            thresholds = theNetwork.inititateNullThresholds();
        }
        else
        {
            thresholds = theNetwork.inititateRandomThresholds();
        }
        theNetwork.createData(data,weights,thresholds);

        stepLength = parameters[0];
        epsilon = parameters[1];
        alpha = parameters[2];
        useThresholds = parameters[3];
        
    } 

    /// Method that trains the weights in sequential mode.
    public void trainNetwork()
    {
        int randomPattern = rand.nextInt(data.length);

        theNetworks = network.getNetworks();
        theUnModNetworks = network.getUnModNetworks();

        //Creates temporary weights
        double[][][] tempWeights = new double[weights.length][][];
        for (int i = 0; i < weights.length; i++)
        {
            double[][] tempWeR = new double[weights[i].length][];
            for (int j = 0; j < weights[i].length; j++)
            {
                double[] tempWeC = new double[weights[i][j].length];
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    tempWeC[k] = weights[i][j][k];
                }
                tempWeR[j] = tempWeC;

            }
            tempWeights[i] = tempWeR;
        }

        //Creates temporary thresholds
        double[][] tempThresholds = new double[thresholds.length][];

        for (int i = 0; i < thresholds.length; i++)
        {
            tempThresholds[i] = new double[thresholds[i].length];

            for (int j = 0; j < thresholds[i].length; j++)
            {
                tempThresholds[i][j] = thresholds[i][j];
            }
        }

        double lastDeltaW = 0;

        //This part updates the weights and thresholds of the network
        for (int i = (weights.length - 1); i > -1; i--)
        {
            for (int j = 0; j < weights[i].length; j++)
            {
                double thresholdValue = 0;
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    double weightValue = 0;

                    weightValue = (1 - epsilon) * weightValue + deltaFunction(i, j, randomPattern) * theNetworks[randomPattern][i][k];
                    thresholdValue = thresholdValue + deltaFunction(i, j, randomPattern);

                    weightValue = weightValue + alpha * lastDeltaW;

                    tempWeights[i][j][k] = weights[i][j][k] + stepLength * weightValue;

                    lastDeltaW = weightValue;
                }
                tempThresholds[i][j] = thresholds[i][j] - stepLength * thresholdValue*useThresholds;
            }
        }
        //Updates the weights
        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < weights[i].length; j++)
            {
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    weights[i][j][k] = tempWeights[i][j][k];
                }
            }
        }

        //Updates the thresholds
        for (int i = 0; i < thresholds.length; i++)
        {
            for (int j = 0; j < thresholds[i].length; j++)
            {
                thresholds[i][j] = tempThresholds[i][j];
            }
        }
    }
    
    /// The recursive delta function used in the algorithm. 
    /// <param name="theLayer">The layer for which you want to compute the delta.</param>
    /// <param name="theNumberOfUnits">The number of the unit considered in the layer.</param>
    /// <param name="thePattern">The current pattern the network is training for.</param>
    /// <returns>Returns the function value of delta.</returns>
    private double deltaFunction(int theLayer, int theNumberOfUnits, int thePattern)
    {
        double delta = 0;

        if (theLayer == (weights.length - 1))
        {
            delta = (data[thePattern][data[thePattern].length - nodes[nodes.length-1]+theNumberOfUnits] - theNetworks[thePattern][theLayer + 1][theNumberOfUnits]) * gPrimFunction(theUnModNetworks[thePattern][theLayer + 1][theNumberOfUnits]);
        }
        else
        {
            for (int i = 0; i < theUnModNetworks[thePattern][theLayer + 2].length; i++)
            {
                delta = delta + deltaFunction(theLayer + 1, i, thePattern) * weights[theLayer + 1][i][theNumberOfUnits] * gPrimFunction(theUnModNetworks[thePattern][theLayer + 1][theNumberOfUnits]);
            }
        }


        return delta;
    }

    /// The g' function for the network, g = tanh(x).
    /// <param name="value"> The function input.</param>
    /// <returns>Returns the function value.</returns>
    private double gPrimFunction(double value)
    {
        return (1 - Math.pow(Math.tanh(value), 2));
    }

    /// Get the entire network computed with current weights.
    /// <returns>Returns the entire network.</returns>
    public double[][][] getNetwork()
    {
        return theNetworks;
    }

    /// Get the current weights.
    /// <returns>Returns the weights.</returns>
    public double[][][] getWeights()
    {
        return weights;
    }
    
    /// Get the current thresholds.
    /// <returns>Returns the thresholds.</returns>
    public double[][] getThresholds()
    {
        return thresholds;
    }

    /// Set method for changing the step lenght of the algorithm.
    /// <param name="theStepLenght">The new step lenght.</param>
    public void setStepLenght(double theStepLenght)
    {
        stepLength = theStepLenght;
    }
}

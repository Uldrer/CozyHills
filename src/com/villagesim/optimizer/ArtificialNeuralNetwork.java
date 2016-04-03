package com.villagesim.optimizer;
import java.util.Random;

public class ArtificialNeuralNetwork {
	
    /// An array representing all the nodes in the network.
    /// Has the form [inputNodes,hiddenLayer1,hiddenLayer2,...,outputNodes].
    private int[] nodes;
    
    /// A random number generator.
    private static Random rand;
    
    /// A network with values outside [-1,1].
    private double[][] unModNetwork;
    
    /// The whole network set for specified data outside the range [-1,1].
    private double[][][] unModNetworks;
    
    /// A single representation of a network with all its values.
    private double[][] network;
    
    /// The whole set of networks obtained for a specified data set.
    private double[][][] networks;
    
    /// The weights of the network. These weights contains one weight for 
    /// each of the edges in the network. When computing the value of a new node
    /// one takes the value of the nodes connected with it times the weight for the
    /// edge connecting them. 
    private double[][][] weights;
    
    /// The thresholds of the network. There is one threshold for each node in the network.
    /// The threshold is removed from the input to a node before the value is propagated 
    /// forward in the network.
    private double[][] thresholds;
	
	// Constructor
	public ArtificialNeuralNetwork(int theInputNodes, int[] theHiddenLayers, int theOutputNodes)
    {
        nodes = new int[theHiddenLayers.length+2];
        nodes[0] = theInputNodes;
        nodes[nodes.length-1] = theOutputNodes;

        for (int i=0;i<theHiddenLayers.length;i++)
        {
            nodes[i+1] = theHiddenLayers[i];
        }

        rand = new Random();
    }
	
	public ArtificialNeuralNetwork(int[] theNodes)
    {
        nodes = theNodes;
        rand = new Random();
    }
	
    /// Method for initiating an empty network. Is used before <see cref="computeNetwork"/>.
    private void initiateNetwork()
    {
        network = new double[nodes.length][];
        unModNetwork = new double[nodes.length][];
        for (int i=0;i<nodes.length;i++)
        {
            double[] tempLayer1 = new double[nodes[i]];
            double[] tempLayer2 = new double[nodes[i]];
            for (int j=0;j<nodes[i];j++)
            {
                tempLayer1[j] = 0;
                tempLayer2[j] = 0;
            }
            network[i] = tempLayer1;
            unModNetwork[i] = tempLayer2;
        }
    }
    
    /// A method to compute the values of a network according to current weights and thresholds.
    /// The network consist of the values of the nodes for a specified pattern.
    /// <param name="patternData">The input pattern for the network.</param>
    /// <param name="weights">The network weights.</param>
    /// <param name="thresholds">The network thresholds.</param>
    private void computeNetwork(double[] patternData, double[][][] weights, double[][] thresholds)
    {
        double[] input = new double[nodes[0]];
        for (int i = 0; i < input.length; i++)
        {
            input[i] = patternData[i];
        }
        initiateNetwork();

        network[0] = input;
        unModNetwork[0] = input;
        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < weights[i].length; j++)
            {
                double theValue = 0;
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    theValue = theValue + network[i][k] * weights[i][j][k];
                }
                //The threshold
                theValue = theValue - thresholds[i][j];

                network[i + 1][j] = Math.tanh(theValue);
                unModNetwork[i + 1][j] = theValue;

            }
        }
    }
    
    /// A method to compute the values of a network according to current weights and thresholds
    /// and then return that network. 
    /// <remarks>The value of a node not in the input layer depends on 
    /// the sum of the weight values of the edges connected to it and the values 
    /// of those nodes that it is connected to. The threshold is removed from the node value
    /// before it is sent on in the network.</remarks>
    /// <param name="patternData">The input pattern for the network.</param>
    /// <param name="weights">The network weights.</param>
    /// <param name="thresholds">The network thresholds.</param>
    /// <returns>The computed network.</returns>
    public double[][] computePatternNetwork(double[] patternData, double[][][] weights, double[][] thresholds)
    {
        double[] input = new double[nodes[0]];
        for (int i = 0; i < input.length; i++)
        {
            input[i] = patternData[i];
        }
        initiateNetwork();

        network[0] = input;
        unModNetwork[0] = input;
        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < weights[i].length; j++)
            {
                double theValue = 0;
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    theValue = theValue + network[i][k] * weights[i][j][k];
                }
                //The threshold
                theValue = theValue - thresholds[i][j];

                network[i + 1][j] = Math.tanh(theValue);
                unModNetwork[i + 1][j] = theValue;

            }
        }
        return network;
    }
    
    public double[][] computePatternNetwork(double[] patternData, double[][][] weights)
    {
    	return computePatternNetwork(patternData, weights, thresholds);
    }
    
    public double[][] computePatternNetwork(double[] patternData)
    {
    	return computePatternNetwork(patternData, weights, thresholds);
    }
    
    public double[] computePatternNetwork_fast(double[] patternData)
    {
    	// TODO set at initiation time which should be used
    	if(nodes[nodes.length-1] == 3)
    	{
    		return computePatternNetwork_fast_3out(patternData);
    	}
    	else if(nodes[nodes.length-1] == 4)
    	{
    		return computePatternNetwork_fast_4out(patternData);
    	}
    	else if(nodes[nodes.length-1] == 5)
    	{
    		return computePatternNetwork_fast_5out(patternData);
    	}
    	else if(nodes[nodes.length-1] == 6)
    	{
    		return computePatternNetwork_fast_6out(patternData);
    	}
    	else if(nodes[nodes.length-1] == 7)
    	{
    		return computePatternNetwork_fast_7out(patternData);
    	}
    	else
    	{
    		double[][] outNetwork = computePatternNetwork(patternData);
    		int outputLayer = outNetwork.length - 1;
    		
            double[] output = outNetwork[outputLayer];
    		return output;
    	}
    }
    
    private double[] computePatternNetwork_fast_3out(double[] patternData)
    {
        double[] output = new double[nodes[nodes.length-1]];
        
        for(int i = 0; i < nodes[0]; i++)
        {
        	output[0] += weights[0][0][i] * patternData[i];
        	output[1] += weights[0][1][i] * patternData[i];
        	output[2] += weights[0][2][i] * patternData[i];
        }
        
        // Thresholds
        output[0] -= thresholds[0][0];
        output[1] -= thresholds[0][1];
        output[2] -= thresholds[0][2];
        
        /*
         * Not using Tanh in the fast functions, we hope that it converges fast anyway during training
        // Tanh (-1:1)	
        output[0] = Math.tanh(output[0]);
        output[1] = Math.tanh(output[1]);
        output[2] = Math.tanh(output[2]);
        */
        
        return output;
    }
    
    private double[] computePatternNetwork_fast_4out(double[] patternData)
    {
        double[] output = new double[nodes[nodes.length-1]];
        
        for(int i = 0; i < nodes[0]; i++)
        {
        	output[0] += weights[0][0][i] * patternData[i];
        	output[1] += weights[0][1][i] * patternData[i];
        	output[2] += weights[0][2][i] * patternData[i];
        	output[3] += weights[0][3][i] * patternData[i];
        }
        
        // Thresholds
        output[0] -= thresholds[0][0];
        output[1] -= thresholds[0][1];
        output[2] -= thresholds[0][2];
        output[3] -= thresholds[0][3];
        
        /*
         * Not using Tanh in the fast functions, we hope that it converges fast anyway during training
        // Tanh (-1:1)	
        output[0] = Math.tanh(output[0]);
        output[1] = Math.tanh(output[1]);
        output[2] = Math.tanh(output[2]);
        output[3] = Math.tanh(output[3]);
        */
        
        return output;
    }
    
    private double[] computePatternNetwork_fast_5out(double[] patternData)
    {
        double[] output = new double[nodes[nodes.length-1]];
        
        for(int i = 0; i < nodes[0]; i++)
        {
        	output[0] += weights[0][0][i] * patternData[i];
        	output[1] += weights[0][1][i] * patternData[i];
        	output[2] += weights[0][2][i] * patternData[i];
        	output[3] += weights[0][3][i] * patternData[i];
        	output[4] += weights[0][4][i] * patternData[i];
        }
        
        // Thresholds
        output[0] -= thresholds[0][0];
        output[1] -= thresholds[0][1];
        output[2] -= thresholds[0][2];
        output[3] -= thresholds[0][3];
        output[4] -= thresholds[0][4];
        
        /*
         * Not using Tanh in the fast functions, we hope that it converges fast anyway during training
        // Tanh (-1:1)	
        output[0] = Math.tanh(output[0]);
        output[1] = Math.tanh(output[1]);
        output[2] = Math.tanh(output[2]);
        output[3] = Math.tanh(output[3]);
        output[4] = Math.tanh(output[4]);
        */
        
        return output;
    }
    
    private double[] computePatternNetwork_fast_6out(double[] patternData)
    {
        double[] output = new double[nodes[nodes.length-1]];
        
        for(int i = 0; i < nodes[0]; i++)
        {
        	output[0] += weights[0][0][i] * patternData[i];
        	output[1] += weights[0][1][i] * patternData[i];
        	output[2] += weights[0][2][i] * patternData[i];
        	output[3] += weights[0][3][i] * patternData[i];
        	output[4] += weights[0][4][i] * patternData[i];
        	output[5] += weights[0][5][i] * patternData[i];
        }
        
        // Thresholds
        output[0] -= thresholds[0][0];
        output[1] -= thresholds[0][1];
        output[2] -= thresholds[0][2];
        output[3] -= thresholds[0][3];
        output[4] -= thresholds[0][4];
        output[5] -= thresholds[0][5];
        
        /*
         * Not using Tanh in the fast functions, we hope that it converges fast anyway during training
        // Tanh (-1:1)	
        output[0] = Math.tanh(output[0]);
        output[1] = Math.tanh(output[1]);
        output[2] = Math.tanh(output[2]);
        output[3] = Math.tanh(output[3]);
        output[4] = Math.tanh(output[4]);
        output[5] = Math.tanh(output[5]);
        */
        return output;
    }
    
    private double[] computePatternNetwork_fast_7out(double[] patternData)
    {
        double[] output = new double[nodes[nodes.length-1]];
        
        for(int i = 0; i < nodes[0]; i++)
        {
        	output[0] += weights[0][0][i] * patternData[i];
        	output[1] += weights[0][1][i] * patternData[i];
        	output[2] += weights[0][2][i] * patternData[i];
        	output[3] += weights[0][3][i] * patternData[i];
        	output[4] += weights[0][4][i] * patternData[i];
        	output[5] += weights[0][5][i] * patternData[i];
        	output[6] += weights[0][6][i] * patternData[i];
        }
        
        // Thresholds
        output[0] -= thresholds[0][0];
        output[1] -= thresholds[0][1];
        output[2] -= thresholds[0][2];
        output[3] -= thresholds[0][3];
        output[4] -= thresholds[0][4];
        output[5] -= thresholds[0][5];
        output[6] -= thresholds[0][6];
        
        /*
         * Not using Tanh in the fast functions, we hope that it converges fast anyway during training
        // Tanh (-1:1)	
        output[0] = Math.tanh(output[0]);
        output[1] = Math.tanh(output[1]);
        output[2] = Math.tanh(output[2]);
        output[3] = Math.tanh(output[3]);
        output[4] = Math.tanh(output[4]);
        output[5] = Math.tanh(output[5]);
        output[6] = Math.tanh(output[6]);
        */
        return output;
    }
    
    /// Initiate random weights in [-1,1] for the network.
    /// <returns>Returns a set of random weights. </returns>
    public double[][][] initiateRandomWeights()
    {
        int layers = nodes.length;
        weights = new double[layers - 1][][];

        for (int i = 0; i < layers - 1; i++)
        {
            double[][] tempEdgeWeight = new double[nodes[i + 1]][];
            for (int j = 0; j < tempEdgeWeight.length; j++)
            {
                double[] tempWeight = new double[nodes[i]];
                tempEdgeWeight[j] = tempWeight;
            }
            weights[i] = tempEdgeWeight;
        }

        //Fills the weights with values from -1 to 1.
        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < weights[i].length; j++)
            {
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    int randomNr = rand.nextInt(2);
                    double value;

                    if (randomNr == 0)
                    {
                        value = rand.nextDouble();
                    }
                    else
                    {
                        value = -1 * rand.nextDouble();
                    }

                    weights[i][j][k] = value;
                }
            }
        }
        return weights;
       
    }
    
    /// Set the weights.
    public void setWeights(double[][][] theWeights)
    {
        weights = theWeights;
    }
    
    /// Method that creates thresholds with random values in [-1,1] for the network.
    public double[][] inititateRandomThresholds()
    {
        thresholds = new double[nodes.length - 1][];

        for (int i = 1; i < nodes.length; i++)
        {
            thresholds[i - 1] = new double[nodes[i]];
        }

        //Fills the thresholds with values from -1 to 1.
        for (int i = 0; i < thresholds.length; i++)
        {
            for (int j = 0; j < thresholds[i].length; j++)
            {
                int randomNr = rand.nextInt(2);
                double value;

                if (randomNr == 0)
                {
                    value = rand.nextDouble();
                }
                else
                {
                    value = -1 * rand.nextDouble();
                }

                thresholds[i][j] = value;
            }
        }
        return thresholds;
    }
    
    /// Method that creates thresholds with zero values for the network.
    public double[][] inititateNullThresholds()
    {
        thresholds = new double[nodes.length - 1][];

        for (int i = 1; i < nodes.length; i++)
        {
            thresholds[i - 1] = new double[nodes[i]];
        }

        //Fills the thresholds with values from -1 to 1.
        for (int i = 0; i < thresholds.length; i++)
        {
            for (int j = 0; j < thresholds[i].length; j++)
            {
                thresholds[i][j] = 0;
            }
        }
        return thresholds;
    }
    /// Set the thresholds for the network.
    public void setThresholds(double[][] theThresholds)
    {
        thresholds = theThresholds;
    }
    
    /// Creates networks for all pattern data. This function calls <see cref="computeNetwork"/> 
    /// for each pattern in the data set and then saves all networks obtained into <see cref="network"/>
    /// and <see cref="unModNetwork"/>.
    public void createData(double[][] data, double[][][] theWeights, double[][] theThresholds)
    {
        networks = new double[data.length][][];
        unModNetworks = new double[data.length][][];
        weights = theWeights;
        thresholds = theThresholds;
        for (int i = 0; i < data.length; i++)
        {
            computeNetwork(data[i], weights, thresholds);
            networks[i] = network;
            unModNetworks[i] = unModNetwork;
        }
    }
    /// <summary>
    /// Get method.
    /// </summary>
    /// <returns>Returns the Networks.</returns>
    public double[][][] getNetworks()
    {
        return networks;
    }
    /// <summary>
    /// Get method for unmodified networks.
    /// "Unmodified" means that the node values haven't been subject to
    /// tanh(x) to fit in [-1,1].</summary>
    /// <returns>Returns the unmodified networks.</returns>
    public double[][][] getUnModNetworks()
    {
        return unModNetworks;
    }
    /// <summary>
    /// Get method.
    /// </summary>
    /// <returns>Returns an array of the nodes in the network.</returns>
    public int[] getNodes()
    {
        return nodes;
    }
}

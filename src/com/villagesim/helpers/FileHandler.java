package com.villagesim.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.villagesim.areas.Area;
import com.villagesim.helpers.FileHeader.WeightType;
import com.villagesim.optimizer.ArtificialNeuralNetwork;
import com.villagesim.resources.Resource;

public class FileHandler {
	
	public static void writeWeightsToFile(double[][][] weights, String path, WeightType type)
	{
		FileHeader currentHeader = new FileHeader(type);
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, "UTF-8");
			
			// Write FileHeader to file
			writer.print(currentHeader.toString());
			
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
	
	public static void logScoreToFile(double[] score)
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter("score.txt", "UTF-8");
			
			for (int i = 0; i < score.length; i++)
            {
                writer.println(String.valueOf(score[i]));
            }
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void logResourcesToFile(Map<Integer, Area> areaMap)
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter("resources.txt", "UTF-8");
			
			for (Area area : areaMap.values())
            {
				for(Resource resource : area.getResourceList())
				{
					writer.println(resource.getDebugString());
				}
            }
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double[][][][] retrieveWeights(String basicPath, ArtificialNeuralNetwork basicNetwork, String gatherPath, ArtificialNeuralNetwork gatherNetwork,
			String movePath, ArtificialNeuralNetwork moveNetwork, String workPath, ArtificialNeuralNetwork workNetwork)
	{
		double[][][][] theWeights = new double[4][][][];
		
		theWeights[0] = retrieveWeights(basicPath, basicNetwork, WeightType.MAIN);
		theWeights[1] = retrieveWeights(gatherPath, gatherNetwork, WeightType.GATHER);
		theWeights[2] = retrieveWeights(movePath, moveNetwork, WeightType.MOVE);
		theWeights[3] = retrieveWeights(workPath, workNetwork, WeightType.WORK);
		
		return theWeights;
	}
	
	public static double[][][] retrieveWeights(String path, ArtificialNeuralNetwork network, WeightType type)
    {
		double[][][] theWeights = null;
		
		FileHeader currentHeader = new FileHeader(type);
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			
			// Read header
			FileHeader readHeader = new FileHeader();
			readHeader = readHeader.parseHeader(br);
			
			// Validate
			if(!currentHeader.equals(readHeader)) 
			{
				// It does validate if only missing actions or sensors.
				if(!currentHeader.validateInput(readHeader))
				{
					// Nothing we can do, probably error in header
					System.out.println("Invalid header detected");
					System.exit(0);
				}
			}
	        int[] read_nodes = readHeader.getNodes();
	        double[][][] theReadWeights = parseWeights(br, read_nodes);
	        
	        // Fix, re-order and add wights according to read header and current valid header
	        theWeights = reorderWeights(readHeader, currentHeader, theReadWeights);
 
		} catch (NumberFormatException e) {
			e.printStackTrace();
			theWeights = network.initiateRandomWeights();
		} catch (IOException e) {
			e.printStackTrace();
			theWeights = network.initiateRandomWeights();
		}
		return theWeights;
    }
	
	private static double[][][] reorderWeights(FileHeader readHeader, FileHeader currentHeader, double[][][] theReadWeights)
	{
		int[] currentNodes = currentHeader.getNodes();
		
		double[][][] theWeights = new double[currentNodes.length - 1][][];
	    
	    // Add Sensorlist and Actionlist
		theWeights[0] = new double[currentHeader.getActionList().size()][];
		List<String> currentSensorList = currentHeader.getSensorList();
		List<String> currentActionList = currentHeader.getActionList();
		List<String> readSensorList = readHeader.getSensorList();
		List<String> readActionList = readHeader.getActionList();
		
		// Sort actions
		int current_action_counter = 0;
		for(String current_action : currentActionList)
		{
			int read_action_counter = 0;
			for(String read_action : readActionList)
			{
				if(current_action.equals(read_action))
				{
					//theWeights[0][current_action_counter] = theReadWeights[0][read_action_counter];
					theWeights[0][current_action_counter] = new double[currentSensorList.size()];
					break;
				}
				read_action_counter++;
			}
			if(read_action_counter == readActionList.size())
			{
				theWeights[0][current_action_counter] = new double[currentSensorList.size()];
			}
			else
			{
				int current_sensor_counter = 0;
				for(String current_sensor : currentSensorList)
				{
					int read_sensor_counter = 0;
					for(String read_sensor : readSensorList)
					{
						if(current_sensor.equals(read_sensor))
						{
							theWeights[0][current_action_counter][current_sensor_counter] = theReadWeights[0][read_action_counter][read_sensor_counter];
							break;
						}
						read_sensor_counter++;
					}
					if(read_sensor_counter == readSensorList.size())
					{
						theWeights[0][current_action_counter][current_sensor_counter] = 0;
					}
					current_sensor_counter++;
				}
			}
			current_action_counter++;
		}
	    
	    return theWeights;
		
	}
	
	private static double[][][] parseWeights(BufferedReader br, int[] nodes) throws NumberFormatException, IOException
	{
		double[][][] theWeights = null;
		
		int theColumn = 0;
	    int theRow = 0;
	    int theDepth = 0;
	    double[][][] temp1 = new double[nodes.length - 1][][];
	    double[][] temp2 = new double[nodes[1]][];
	
	    while (br.ready())
	    {
	        if (theRow == temp2.length)
	        {
	            theRow = 0;
	            temp1[theDepth] = temp2;
	            theDepth++;
	            temp2 = new double[nodes[1 + theDepth]][];
	        }
	        String lineString = br.readLine();
	        String[] dataNumbers;
	
	        dataNumbers = lineString.split(" ");
	
	        double[] temp3 = new double[dataNumbers.length];
	
	        for(String dataNr : dataNumbers)
	        {
	            double number = Double.parseDouble(dataNr);
	            temp3[theColumn] = number;
	            theColumn++;
	        }
	        theColumn = 0;
	        temp2[theRow] = temp3;
	        theRow++;
	    }
	    temp1[theDepth] = temp2;
	    theWeights = temp1;
	
	    return theWeights;
	}
}

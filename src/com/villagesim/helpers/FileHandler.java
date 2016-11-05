package com.villagesim.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
				System.out.println("Invalid header detected");
				System.exit(0);
			}
 
	        int[] nodes = network.getNodes();
	
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
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			theWeights = network.initiateRandomWeights();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			theWeights = network.initiateRandomWeights();
		}
        return theWeights;
    }
}

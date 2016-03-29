package com.villagesim.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.villagesim.areas.Area;
import com.villagesim.optimizer.ArtificialNeuralNetwork;
import com.villagesim.resources.Resource;

public class FileHandler {
	
	public static void writeWeightsToFile(double[][][] weights)
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
	
	public static void logResourcesToFile(Set<Object> objectSet)
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter("resources.txt", "UTF-8");
			
			for (Object obj : objectSet)
            {
				if(obj instanceof Area)
				{
					Area area = (Area) obj;
					for(Resource resource : area.getResourceSet())
					{
						writer.println(resource.getDebugString());
					}
				}
            }
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double[][][] retrieveWeights(String path, ArtificialNeuralNetwork network)
    {
		double[][][] theWeights = null;
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
 
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return theWeights;
    }
}

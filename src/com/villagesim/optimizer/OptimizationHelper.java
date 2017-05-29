package com.villagesim.optimizer;

public class OptimizationHelper {

	public static double[][][][][] copy(double[][][][][] src)
    {
        double[][][][][] temp1 = new double[src.length][][][][];
        for (int i = 0; i < src.length; i++)
        {
        	temp1[i] = copy(src[i]);
        }
        return temp1;
    }
	
	public static double[][][][] copy(double[][][][] src)
    {
        double[][][][] temp1 = new double[src.length][][][];
        for (int i = 0; i < src.length; i++)
        {
        	temp1[i] = copy(src[i]);
        }
        return temp1;
    }
    
  /// Copy function for weights.
    /// <param name="weights">The weights to copy.</param>
    /// <returns>The copied weights.</returns>
	public static double[][][] copy(double[][][] src)
    {
        double[][][] temp1 = new double[src.length][][];
        for (int i = 0; i < src.length; i++)
        {
            double[][] temp2 = new double[src[i].length][];
            for (int j = 0; j < src[i].length; j++)
            {
                double[] temp3 = new double[src[i][j].length];
                for (int k = 0; k < src[i][j].length; k++)
                {
                    temp3[k] = src[i][j][k];
                }
                temp2[j] = temp3;
            }
            temp1[i] = temp2;
        }
        return temp1;
    }
}

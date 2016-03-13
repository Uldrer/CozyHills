package com.villagesim.helpers;

import java.util.Comparator;

public class ArrayIndexComparator implements Comparator<Integer> {

	private final double[] array;

    public ArrayIndexComparator(double[] array)
    {
        this.array = array;
    }

    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i;
        }
        return indexes;
    }
	
	@Override
	public int compare(Integer index1, Integer index2) {
		return ((Double)array[index1]).compareTo(array[index2]);
	}

}

package com.villagesim.sensors;

public class SensorArea {
	
	private int areaId;
	private double distance;
	
	public SensorArea(int areaId, double distance)
	{
		this.areaId = areaId;
		this.distance = distance;
	}

	public int getAreaId()
	{
		return areaId;
	}
	
	public double getDistance()
	{
		return distance;
	}
	
	
}

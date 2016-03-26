package com.villagesim.sensors;

import com.villagesim.areas.Area;

public class SensorArea {
	
	private Area area;
	private double distance;
	
	public SensorArea(Area area, double distance)
	{
		this.area = area;
		this.distance = distance;
	}

	public Area getArea()
	{
		return area;
	}
	
	public double getDistance()
	{
		return distance;
	}
	
	
}

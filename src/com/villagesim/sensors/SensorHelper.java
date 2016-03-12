package com.villagesim.sensors;

import java.awt.geom.Point2D;

import com.villagesim.areas.Area;

public final class SensorHelper {
	
	public static int SENSOR_INPUTS = 12;

	public static double computeDistanceToArea(Point2D coordinate, Area area)
	{
		Point2D areaCoordinate = area.getCoordinate();
		double areaWidth = area.getWidth();
		double areaHeight = area.getHeight();
		
		// For now use that it is a rectangle
		double dx = Math.max(Math.abs(coordinate.getX() - areaCoordinate.getX()) - areaWidth / 2, 0);
		double dy = Math.max(Math.abs(coordinate.getY()  - areaCoordinate.getY()) - areaHeight / 2, 0);
	
		return dx * dx + dy * dy;
	}
	

}

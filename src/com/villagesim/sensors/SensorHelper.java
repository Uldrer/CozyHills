package com.villagesim.sensors;

import java.awt.geom.Point2D;

import com.villagesim.Const;
import com.villagesim.areas.Area;

public final class SensorHelper {
	
	public static int SENSOR_INPUTS = Sensor.size;
	private static boolean max_computed = false;
	private static double maxValue = 0;

	public static double computeDistanceToArea(Point2D coordinate, Area area)
	{
		Point2D areaCoordinate = area.getCoordinate();
		double areaWidth = area.getWidth();
		double areaHeight = area.getHeight();
		
		// For now use that it is a rectangle
		double dx = Math.max(Math.abs(coordinate.getX() - areaCoordinate.getX()) - areaWidth / 2, 0);
		double dy = Math.max(Math.abs(coordinate.getY()  - areaCoordinate.getY()) - areaHeight / 2, 0);
	
		// TODO Use squared distance to avoid square root
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public static double computeDirectionToArea(Point2D coordinate, Area area)
	{
		Point2D areaCoordinate = area.getCoordinate();
		
		double xDiff = areaCoordinate.getX() - coordinate.getX();
		double yDiff = areaCoordinate.getY() - coordinate.getY();
		
		return Math.atan2(yDiff, xDiff);
	}


	public static double computeNormalizedDistanceToArea(Point2D coordinate, Area area)
	{
		double dist = computeDistanceToArea(coordinate, area);
		
		return dist/getMaxDistance();
	}
	
	public static double getMaxDistance()
	{
		if(!max_computed)
		{
			double maxDist = Const.WINDOW_WIDTH * Const.WINDOW_WIDTH + Const.WINDOW_HEIGHT * Const.WINDOW_HEIGHT;
			maxValue = Math.sqrt(maxDist);
			max_computed = true;
		}

		return maxValue;
	}
	
	public static double getNormalizedMaxDistance()
	{
		return 1;
	}
	
	public static boolean isNormalizedDistanceCloseEnoughForAction(double normalizedDist)
	{
		double dist = normalizedDist * getMaxDistance();
		return (dist <= Const.MIN_DISTANCE_FOR_ACTION_METER/Const.METER_PER_PIXEL);
	}

}

package com.villagesim.people;

import java.awt.Graphics;

import com.villagesim.interfaces.Drawable;

public class Person implements PersonInterface, Drawable {

	private int id; // unique person id
	private double nutrition; // low values are hunger and starvation, 0 is death
	private double aqua; // low values are thirst and dehydration, 0 is death
	private double nutrition_decline_rate;
	private double aqua_decline_rate;
	private double lifetime_days = 0;
	private final double MAX_NUTRITION_POINTS = 1000;
	private final double MAX_AQUA_POINTS = 1000;
	private final double NUTRITION_DECLINE_TIME_S = 18114400; // Assumption, death after 3 weeks without food
	private final double AQUA_DECLINE_TIME_S = 259200; // Assumption, death after 3 days without water
	private final double SECONDS_PER_DAY = 86400;
	
	private static int id_counter = 0;
	
	public Person()
	{
		id = ++id_counter;
		nutrition = MAX_NUTRITION_POINTS;
		aqua = MAX_AQUA_POINTS;
		nutrition_decline_rate = MAX_NUTRITION_POINTS/NUTRITION_DECLINE_TIME_S;
		aqua_decline_rate = MAX_AQUA_POINTS/AQUA_DECLINE_TIME_S;
	}
	
	public boolean IsAlive()
	{
		if(nutrition <= 0) return false;
		if(aqua <= 0) return false;
		
		return true;
	}
	
	public double getLifetime()
	{
		return lifetime_days;
	}
	
	@Override
	public void draw(Graphics bbg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int TakeAction() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void UpdateLifeStatus(int seconds) {

		nutrition -= nutrition_decline_rate*seconds;
		aqua -= aqua_decline_rate*seconds;
		lifetime_days += seconds/SECONDS_PER_DAY;
		
	}

	@Override
	public void UpdateSensorReadings() {
		// TODO Auto-generated method stub
		
	}

}

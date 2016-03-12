package com.villagesim.people;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.villagesim.Const;
import com.villagesim.actions.BasicAction;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Updateable;
import com.villagesim.optimizer.ArtificialNeuralNetwork;
import com.villagesim.sensors.SensorHelper;

public class Person implements Drawable, Updateable {

	private int id; // unique person id
	private Point2D coordinate; // location
	private double nutrition; // low values are hunger and starvation, 0 is death
	private double aqua; // low values are thirst and dehydration, 0 is death
	private double nutrition_decline_rate;
	private double aqua_decline_rate;
	private double lifetime_days = 0;
	private List<Double> sensorInputs;
	private boolean logDeath = true;
	private ArtificialNeuralNetwork neuralNetwork;
	
	// Constants
	private final double MAX_NUTRITION_POINTS = 1000;
	private final double MAX_AQUA_POINTS = 1000;
	private final double NUTRITION_DECLINE_TIME_S = 18114400; // Assumption, death after 3 weeks without food
	private final double AQUA_DECLINE_TIME_S = 259200; // Assumption, death after 3 days without water
	private final double SECONDS_PER_DAY = 86400;
	private final int PERSON_SIZE = 3;
	private final int ACTION_SIZE = BasicAction.values().length;
	
	private static int id_counter = 0;
	
	public Person()
	{
		id = ++id_counter;
		nutrition = MAX_NUTRITION_POINTS;
		aqua = MAX_AQUA_POINTS;
		nutrition_decline_rate = MAX_NUTRITION_POINTS/NUTRITION_DECLINE_TIME_S;
		aqua_decline_rate = MAX_AQUA_POINTS/AQUA_DECLINE_TIME_S;
		coordinate = generateCoordinate();
		
		// Init default list
		sensorInputs = new ArrayList<Double>();
		for(int i = 0; i < SensorHelper.SENSOR_INPUTS; i++)
		{
			sensorInputs.add(0.0);
		}
		
		neuralNetwork = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[] {ACTION_SIZE}, 1 );
	}
	
	public boolean isAlive()
	{
		if(nutrition <= 0) return false;
		if(aqua <= 0) return false;
		
		return true;
	}
	
	public double getLifetime()
	{
		return lifetime_days;
	}
	
	public int getId()
	{
		return id;
	}
	
	@Override
	public void draw(Graphics bbg) 
	{
		if(isAlive())
		{
			bbg.setColor(Color.BLACK);
		}
		else
		{
			bbg.setColor(Color.RED);
			if(logDeath)
			{
				System.out.println("Person id: " + id + " died of " + ((aqua <= 0) ? "dehydration" : "starvation") + " after " + lifetime_days + " days.");
				logDeath = false;
			}
		}
		bbg.fillOval((int)(coordinate.getX()+0.5), (int)(coordinate.getY()+0.5), PERSON_SIZE, PERSON_SIZE);
	}
	
	public double getThirstValue()
	{
		return 1 - aqua/MAX_AQUA_POINTS;
	}
	
	public double getHungerValue()
	{
		return 1 - nutrition/MAX_NUTRITION_POINTS;
	}
	
	public Point2D getCoordinate()
	{
		return coordinate;
	}
	
	private Point2D generateCoordinate()
	{
		Point2D coord = new Point2D.Double();
		Random rand = new Random();
		
		int x = rand.nextInt(Const.WINDOW_WIDTH-PERSON_SIZE);
		int y = rand.nextInt(Const.WINDOW_HEIGHT-PERSON_SIZE);
		coord.setLocation(x, y);
		return coord;
	}

	@Override
	public void update(int seconds) {
		
		if(!isAlive()) return;
		
		updateLifeStatus(seconds);
		takeAction();
	}
	
	public void updateSensorReadings(List<Double> sensorInputs)
	{
		if(!isAlive()) return;
		
		this.sensorInputs = sensorInputs;
	}
	
	private int takeAction() {

		// Send sensor inputs into ANN and let it decide which action to take
		double[] inputs = new double[sensorInputs.size()];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = sensorInputs.get(i);
		}
		
		// TODO get weights and thresholds from training sessions
		double [][][] weigths = neuralNetwork.initiateRandomWeights();
		double [][] thresholds = neuralNetwork.inititateNullThresholds();
		
		double[][] outputNetwork = neuralNetwork.computePatternNetwork(inputs, weigths, thresholds);
		int actionIndex = determineAction(outputNetwork);
		
		
		return 0;
	}
	
	private int determineAction(double[][] network)
	{
		int outputLayer = network.length - 1;
        int outputs = network[outputLayer].length;
        
        // With only one output
        double value = network[outputLayer][0];
        
        return (int)value;
        	
	}
	
	private void updateLifeStatus(int seconds) {

		nutrition -= nutrition_decline_rate*seconds;
		aqua -= aqua_decline_rate*seconds;
		lifetime_days += seconds/SECONDS_PER_DAY;
	}


}

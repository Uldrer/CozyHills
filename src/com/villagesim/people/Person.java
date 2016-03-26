package com.villagesim.people;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.villagesim.Const;
import com.villagesim.actions.ActionFactory;
import com.villagesim.actions.ActionMediator;
import com.villagesim.actions.BasicAction;
import com.villagesim.areas.Area;
import com.villagesim.helpers.ArrayIndexComparator;
import com.villagesim.helpers.FileHandler;
import com.villagesim.interfaces.Action;
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
	private double nutrition_increase_rate;
	private double aqua_increase_rate;
	private double lifetime_days = 0;
	private List<Double> sensorInputs;
	private List<Area> closestAreas;
	private ActionFactory actionFactory;
	
	// Debugging
	private boolean logDeath = true;
	private boolean logDebug = true;
	
	// Neural network
	private ArtificialNeuralNetwork neuralNetwork;
	private double [][][] weights;
	private double [][] thresholds;
	
	// Constants
	private final double MAX_NUTRITION_POINTS = 1000;
	private final double MAX_AQUA_POINTS = 1000;
	private final double NUTRITION_DECLINE_TIME_S = 18114400; // Assumption, death after 3 weeks without food
	private final double AQUA_DECLINE_TIME_S = 259200; // Assumption, death after 3 days without water
	private final double SECONDS_PER_DAY = 86400;
	private final double OLD_AGE_LIMIT_DAYS = 14600; // Everyone dies at 40 for now, hunter/gather was harsch!
	private final int PERSON_SIZE = 3;
	private final int ACTION_SIZE = BasicAction.values().length;
	private final int NUTRITION_INCREASE_TIME_S = 3600; // Assumption, eating for one hour restores 3 weeks of starvation, kinda crude
	private final int AQUA_INCREASE_TIME_S = 900; // Assumption, drinking for 15 min restores 3 days of dehydration, kinda crude
	
	
	private static int id_counter = 0;
	
	public Person()
	{
		init();
		
		neuralNetwork = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[]{}, ACTION_SIZE );
		
		//weigths = neuralNetwork.initiateRandomWeights();
		weights = FileHandler.retrieveWeights("weights.txt", neuralNetwork);
		neuralNetwork.setWeights(weights);
		thresholds = neuralNetwork.inititateNullThresholds(); // TODO evaluate thresholds as well
		//thresholds = neuralNetwork.inititateRandomThresholds();
		
		logDebug = true;
	}
	
	public Person(double[][][] weights)
	{
		init();
		
		neuralNetwork = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[]{}, ACTION_SIZE );
		
		this.weights = weights;
		neuralNetwork.setWeights(weights);
		thresholds = neuralNetwork.inititateNullThresholds(); // TODO evaluate thresholds as well
		//thresholds = neuralNetwork.inititateRandomThresholds();
		
		logDebug = false;
	}
	
	private void init()
	{
		id = ++id_counter;
		nutrition = MAX_NUTRITION_POINTS;
		aqua = MAX_AQUA_POINTS;
		nutrition_decline_rate = MAX_NUTRITION_POINTS/NUTRITION_DECLINE_TIME_S;
		aqua_decline_rate = MAX_AQUA_POINTS/AQUA_DECLINE_TIME_S;
		nutrition_increase_rate = MAX_NUTRITION_POINTS/NUTRITION_INCREASE_TIME_S;
		aqua_increase_rate = MAX_AQUA_POINTS/AQUA_INCREASE_TIME_S;
		coordinate = generateCoordinate();
		actionFactory = new ActionFactory(this);
		
		// Init default list
		sensorInputs = new ArrayList<Double>();
		for(int i = 0; i < SensorHelper.SENSOR_INPUTS; i++)
		{
			sensorInputs.add(0.0);
		}
	}
	
	public boolean isAlive()
	{
		if(nutrition <= 0) return false;
		if(aqua <= 0) return false;
		if(lifetime_days > OLD_AGE_LIMIT_DAYS) return false;
		
		return true;
	}
	
	public boolean isWeightsEqual(double[][][] weightsToCompare)
	{
		return Arrays.equals(weights, weightsToCompare);
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
		makeActionDecision();
	}
	
	public void updateSensorReadings(List<Double> sensorInputs, List<Area> closestAreas)
	{
		if(!isAlive()) return;
		
		this.sensorInputs = sensorInputs;
		this.closestAreas = closestAreas;
	}
	
	public double getSensorReading(int index)
	{
		if(index < 0) return 0;
		if(index >= sensorInputs.size()) return 0;
		
		return sensorInputs.get(index);
	}
	
	public Area getClosestArea(int index)
	{
		if(index < 0) return null;
		if(index >= closestAreas.size()) return null;
		
		return closestAreas.get(index);
	}
	
	private void makeActionDecision() {

		// Send sensor inputs into ANN and let it decide which action to take
		double[] inputs = new double[sensorInputs.size()];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = sensorInputs.get(i);
		}
		
		double[][] outputNetwork = neuralNetwork.computePatternNetwork(inputs, weights, thresholds);
		Integer[] actionIndexList = determineAction(outputNetwork);
		
		List<Action> actionList = new ArrayList<Action>();
		for(int actionIndex : actionIndexList)
		{
			actionList.add(actionFactory.getAction(actionIndex));
		}
		
		// Send action package in priority order
		ActionMediator.addActionList(actionList);
	}
	
	private Integer[] determineAction(double[][] network)
	{
		int outputLayer = network.length - 1;
        
        // With one output per basic action
        double[] output = network[outputLayer];
        
        ArrayIndexComparator comparator = new ArrayIndexComparator(output);
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, Collections.reverseOrder(comparator));
        
        return indexes;
        	
	}
	
	private void updateLifeStatus(int seconds) {

		nutrition -= nutrition_decline_rate*seconds;
		aqua -= aqua_decline_rate*seconds;
		lifetime_days += seconds/SECONDS_PER_DAY;
	}
	
	public double getPotentialNutrition(int seconds)
	{
		double nutrition_value = nutrition_increase_rate*seconds;
		return nutrition_value;
	}
	
	public void eat(double nutrition_value)
	{
		nutrition += nutrition_value;
		if(nutrition > MAX_NUTRITION_POINTS) nutrition = MAX_NUTRITION_POINTS;
	}
	
	public double getPotentialAqua(int seconds)
	{
		double aqua_value = aqua_increase_rate*seconds;
		return aqua_value;
	}
	
	public void drink(double aqua_value)
	{
		aqua += aqua_value;
		if(aqua > MAX_AQUA_POINTS) aqua = MAX_AQUA_POINTS;
	}
	
	public void move(double dx, double dy)
	{
		int x = (int) (coordinate.getX() + dx + 0.5);
		int y = (int) (coordinate.getY() + dy + 0.5);
		
		if(x > Const.WINDOW_WIDTH) x = Const.WINDOW_WIDTH;
		if(y > Const.WINDOW_HEIGHT) y = Const.WINDOW_HEIGHT;
		
		coordinate.setLocation(x, y);
	}
	
	public boolean printDebug()
	{
		return logDebug;
	}


}

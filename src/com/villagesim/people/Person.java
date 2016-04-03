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
import com.villagesim.actions.ActionHelper;
import com.villagesim.actions.ActionMediator;
import com.villagesim.actions.BasicAction;
import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.helpers.ArrayIndexComparator;
import com.villagesim.helpers.FileHandler;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Printable;
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
	private Storage personalStorage;
	
	// Debugging
	private boolean logDeath = true;
	private boolean logDebug = true;
	private List<Action> lastActionList;
	private DeathReason reasonOfDeath;
	
	// Neural network
	private ArtificialNeuralNetwork basicNeuralNetwork;
	private ArtificialNeuralNetwork gatherNeuralNetwork;
	private double [][][] basicWeights;
	private double [][][] gatherWeights;
	private double [][] thresholds;
	
	// Constants
	private final double MAX_NUTRITION_POINTS = 1000;
	private final double MAX_AQUA_POINTS = 1000;
	private final double LITER_PER_AQUA_POINT = 0.0075; // Assumption, drinking speed 0.5 l/min
	private final double KG_PER_NUTRITION_POINT = 0.001; // Assumption, eating speed 1 kg/h
	private final double NUTRITION_DECLINE_TIME_S = 1814400; // Assumption, death after 3 weeks without food
	private final double AQUA_DECLINE_TIME_S = 259200; // Assumption, death after 3 days without water
	private final double OLD_AGE_LIMIT_DAYS = 14600; // Everyone dies at 40 for now, hunter/gather was harsch!
	private final int PERSON_SIZE = 3;
	private final int BASIC_ACTION_SIZE = BasicAction.values().length;
	private final int NUTRITION_INCREASE_TIME_S = 3600; // Assumption, eating for one hour restores 3 weeks of starvation, kinda crude
	private final int AQUA_INCREASE_TIME_S = 900; // Assumption, drinking for 15 min restores 3 days of dehydration, kinda crude
	
	
	private static int id_counter = 0;
	
	public Person()
	{
		init();
		
		basicWeights = FileHandler.retrieveWeights("weights.txt", basicNeuralNetwork);
		basicNeuralNetwork.setWeights(basicWeights);

		gatherWeights = FileHandler.retrieveWeights("gatherWeights.txt", gatherNeuralNetwork);
		gatherNeuralNetwork.setWeights(gatherWeights);

		logDebug = false;
	}
	
	public Person(double[][][] basicWeights, double[][][] gatherWeights)
	{
		init();
		
		this.basicWeights = basicWeights;
		basicNeuralNetwork.setWeights(basicWeights);
		
		this.gatherWeights = gatherWeights;
		gatherNeuralNetwork.setWeights(gatherWeights);
		
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
		personalStorage = new Storage();
		
		// Init default list
		sensorInputs = new ArrayList<Double>();
		for(int i = 0; i < SensorHelper.SENSOR_INPUTS; i++)
		{
			sensorInputs.add(0.0);
		}
		
		basicNeuralNetwork = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[]{}, BASIC_ACTION_SIZE );
		thresholds = basicNeuralNetwork.inititateNullThresholds(); // TODO evaluate thresholds as well
		//thresholds = neuralNetwork.inititateRandomThresholds();
		
		gatherNeuralNetwork = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[]{}, ActionHelper.getAdvancedActionSize("Gather") );
		gatherNeuralNetwork.inititateNullThresholds(); // don't care to save thresholds for now
		
	}
	
	public boolean isAlive()
	{
		if(nutrition <= 0) 
		{
			reasonOfDeath = DeathReason.STARVATION;
			return false;
		}
		if(aqua <= 0)
		{
			reasonOfDeath = DeathReason.DEHYDRATION;
			return false;
		}
		if(lifetime_days > OLD_AGE_LIMIT_DAYS) 
		{
			reasonOfDeath = DeathReason.OLD_AGE;
			return false;
		}
		
		return true;
	}
	
	public boolean isWeightsEqual(double[][][] basicWeightsToCompare, double[][][] gatherWeightsToCompare)
	{
		return Arrays.equals(basicWeights, basicWeightsToCompare) && Arrays.equals(gatherWeights, gatherWeightsToCompare);
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
		
		if(!isAlive())
		{
			if(logDeath)
			{
				System.out.println("Person id: " + id + " died of " + reasonOfDeath.getName() + " after " + lifetime_days + " days.");
				printLastActionList();
				logDeath = false;
			}
			return;
		}
		
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
		
		double[] output = basicNeuralNetwork.computePatternNetwork_fast(inputs);
		Integer[] actionIndexList = determineAction(output);
		
		List<Action> actionList = new ArrayList<Action>();
		boolean done = false;
		for(int actionIndex : actionIndexList)
		{
			List<Action> actions = actionFactory.getActions(actionIndex);
			for(Action action : actions)
			{
				if(action.isValid())
				{
					actionList.add(action);
					done = true;
					break;
				}
			}
			if(done)
			{
				break;
			}
		}
		
		// Send action package in priority order
		ActionMediator.addActionList(actionList);
		lastActionList = actionList;
	}
	
	public List<Action> makeAdvancedActionDecision() 
	{
		double[] inputs = new double[sensorInputs.size()];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = sensorInputs.get(i);
		}
		
		// TODO use generic advanced action instead of just gather
		double[] output = gatherNeuralNetwork.computePatternNetwork_fast(inputs);
		Integer[] actionIndexList = determineAction(output);
		
		List<Action> actionList = new ArrayList<Action>();
		for(int actionIndex : actionIndexList)
		{
			actionList.add(actionFactory.getAdvancedAction(actionIndex, BasicAction.GATHER));
		}
		return actionList;
	}
	
	private void printLastActionList()
	{
		System.out.print("[");
		for(Action action : lastActionList)
		{
			if(action instanceof Printable)
			{
				Printable printableAction = (Printable) action;
				System.out.print(printableAction.getDebugPrint() + " ");
			}
		}
		System.out.println("]");
	}
	
	private Integer[] determineAction(double[] output)
	{
        ArrayIndexComparator comparator = new ArrayIndexComparator(output);
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, Collections.reverseOrder(comparator));
        
        return indexes;
        	
	}
	
	private void updateLifeStatus(int seconds) {

		nutrition -= nutrition_decline_rate*seconds;
		aqua -= aqua_decline_rate*seconds;
		lifetime_days += seconds/Const.SECONDS_PER_DAY;
	}
	
	public double getPotentialNutrition(int seconds)
	{
		double nutrition_value = nutrition_increase_rate*seconds*KG_PER_NUTRITION_POINT;
		return nutrition_value;
	}
	
	public void eat(double kg_value)
	{
		nutrition += kg_value/KG_PER_NUTRITION_POINT;
		if(nutrition > MAX_NUTRITION_POINTS) nutrition = MAX_NUTRITION_POINTS;
	}
	
	public double getPotentialAqua(int seconds)
	{
		double liter_value = aqua_increase_rate*seconds*LITER_PER_AQUA_POINT;
		return liter_value;
	}
	
	public void drink(double liter_value)
	{
		aqua += liter_value/LITER_PER_AQUA_POINT;
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
	
	public Storage getPersonalStorage()
	{
		return personalStorage;
	}
	
	public boolean printDebug()
	{
		return logDebug;
	}


}

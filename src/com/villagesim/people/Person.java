package com.villagesim.people;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.villagesim.Const;
import com.villagesim.actions.ActionFactory;
import com.villagesim.actions.ActionMediator;
import com.villagesim.actions.BasicAction;
import com.villagesim.areas.Area;
import com.villagesim.areas.Storage;
import com.villagesim.helpers.ArrayIndexComparator;
import com.villagesim.helpers.FileHandler;
import com.villagesim.helpers.FileHeader.WeightType;
import com.villagesim.helpers.LimitedQueue;
import com.villagesim.interfaces.Action;
import com.villagesim.interfaces.Drawable;
import com.villagesim.interfaces.Printable;
import com.villagesim.interfaces.Updateable;
import com.villagesim.optimizer.ArtificialNeuralNetwork;
import com.villagesim.resources.Resource;
import com.villagesim.sensors.SensorArea;
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
	private List<Double> measurementInputs;
	private List<Area> closestAreas;
	private ActionFactory actionFactory;
	private Storage personalStorage;
	private Map<Class<? extends Resource>, SensorArea> lastAreaMap = new HashMap<Class<? extends Resource>, SensorArea>();
	private Map<Class<? extends Resource>, Double> lastDirectionMap = new HashMap<Class<? extends Resource>, Double>();
	private boolean hasChangedCoordinate = true;
	
	// Debugging
	private boolean logDeath = true;
	private boolean logDebug = true;
	private boolean logActions = true;
	private LimitedQueue<Action> lastActionQueue = new LimitedQueue<Action>(30); // Save latest 30 actions
	private LimitedQueue<Integer> lastActionIndexQueue = new LimitedQueue<Integer>(BasicAction.size); // Save latest 30 actions
	private DeathReason reasonOfDeath;
	private List<Point2D> lifePath;
	private List<BasicAction> lifePathType;
	
	// Neural network
	private ArtificialNeuralNetwork basicNeuralNetwork;
	private double [][][] basicWeights;
	
	// Constants
	private final double MAX_NUTRITION_POINTS = 1000;
	private final double MAX_AQUA_POINTS = 1000;
	private final double LITER_PER_AQUA_POINT = 0.0075; // Assumption, drinking speed 0.5 l/min
	private final double KG_PER_NUTRITION_POINT = 0.001; // Assumption, eating speed 1 kg/h
	private final double NUTRITION_DECLINE_TIME_S = 1814400; // Assumption, death after 3 weeks without food
	private final double AQUA_DECLINE_TIME_S = 259200; // Assumption, death after 3 days without water
	private final double OLD_AGE_LIMIT_DAYS = 14600; // Everyone dies at 40 for now, hunter/gather was harsch!
	private final int PERSON_SIZE = 3;
	private final int PATH_SIZE = 2;
	private final int BASIC_ACTION_SIZE = BasicAction.values().length;
	private final int NUTRITION_INCREASE_TIME_S = 3600; // Assumption, eating for one hour restores 3 weeks of starvation, kinda crude
	private final int AQUA_INCREASE_TIME_S = 900; // Assumption, drinking for 15 min restores 3 days of dehydration, kinda crude
	private final double PERSONAL_STORAGE_LIMIT = 20; // Assumption, you can't carry more than 20 kgs on average
	
	
	private static int id_counter = 0;
	
	public Person()
	{
		init();
		
		basicWeights = FileHandler.retrieveWeights("weights.txt", basicNeuralNetwork, WeightType.MAIN);
		basicNeuralNetwork.setWeights(basicWeights);

		logDebug = false;
		logActions = true;
		logDeath = true;
	}
	
	public Person(double[][][] basicWeights)
	{
		init();
		
		this.basicWeights = basicWeights;
		basicNeuralNetwork.setWeights(basicWeights);
		
		logDebug = false;
		logActions = false;
		logDeath = false;
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
		// Personal storage can hold just so much
		personalStorage.setRestrictionActive(true);
		personalStorage.setRestrictionWeightLimit(PERSONAL_STORAGE_LIMIT);
		
		// Path list
		lifePath = new ArrayList<Point2D>();
		lifePath.add((Point2D)coordinate.clone());
		lifePathType = new ArrayList<BasicAction>();
		
		// Init default list
		sensorInputs = new ArrayList<Double>();
		for(int i = 0; i < SensorHelper.SENSOR_INPUTS; i++)
		{
			sensorInputs.add(0.0);
		}
		
		measurementInputs = new ArrayList<Double>();
		for(int i = 0; i < SensorHelper.MEASUREMENT_INPUTS; i++)
		{
			measurementInputs.add(0.0);
		}
		
		int networkSize = BASIC_ACTION_SIZE;
		basicNeuralNetwork = new ArtificialNeuralNetwork(SensorHelper.SENSOR_INPUTS, new int[]{}, networkSize );
		basicNeuralNetwork.inititateNullThresholds(); // TODO evaluate thresholds as well
	}
	
	public void enableLogging(boolean enableLogging)
	{
		if(!enableLogging)
		{
			logDebug = false;
			logActions = false;
			logDeath = false;
		}
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
	
	public boolean isWeightsEqual(double[][][] basicWeightsToCompare)
	{
		return Arrays.equals(basicWeights, basicWeightsToCompare);
	}
	
	public double getLifetime()
	{
		return lifetime_days;
	}
	
	public int getId()
	{
		return id;
	}
	private boolean hasLoggedPath = false;
	@Override
	public void draw(Graphics bbg) 
	{
		if(isAlive())
		{
			bbg.setColor(Color.BLACK);
			bbg.fillOval((int)(coordinate.getX()+0.5), (int)(coordinate.getY()+0.5), PERSON_SIZE, PERSON_SIZE);
		}
		else
		{
			bbg.setColor(Color.BLACK);
			int lastX = -1;
			int lastY = -1;
			int counter = 0;
			for(Point2D pt : lifePath)
			{
				int currentX = (int)(pt.getX()+0.5);
				int currentY = (int)(pt.getY()+0.5);
				
				if(lastX != -1 && lastY != -1)
				{
					if(lifePathType.get(counter) == BasicAction.WALK_DIRECTION_WATER)
					{
						bbg.setColor(Color.BLUE);
					}
					else if(lifePathType.get(counter) == BasicAction.WALK_DIRECTION_WOOD)
					{
						bbg.setColor(Color.GREEN);
					}
					else if(lifePathType.get(counter) == BasicAction.WALK_DIRECTION_FISH)
					{
						bbg.setColor(Color.YELLOW);
					}
					bbg.drawLine(currentX, currentY, lastX, lastY);
					counter++;
					bbg.setColor(Color.BLACK);
				}
				bbg.fillOval(currentX, currentY, PATH_SIZE, PATH_SIZE);
				lastX = currentX;
				lastY = currentY;
			}
			if(!hasLoggedPath)
			{
				System.out.println("Lifepath size: " + lifePath.size());
				System.out.println("LifePathType size: " + lifePathType.size());
				hasLoggedPath = true;
			}
			// Draw death spot in red
			bbg.setColor(Color.RED);
			bbg.fillOval((int)(coordinate.getX()+0.5), (int)(coordinate.getY()+0.5), PERSON_SIZE, PERSON_SIZE);
		}
		
	}
	
	public double getThirstValue()
	{
		return 1 - aqua/MAX_AQUA_POINTS;
	}
	
	public double getHungerValue()
	{
		return 1 - nutrition/MAX_NUTRITION_POINTS;
	}
	
	public boolean hasNewCoordinate()
	{
		return hasChangedCoordinate;
	}
	
	public void resetHasChangedCoordinate()
	{
		hasChangedCoordinate = false;
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
	public void update(int seconds)
	{
		// Update status for personal storage
		personalStorage.update(seconds);
		
		if(!isAlive())
		{
			if(logDeath)
			{
				System.out.println("END");
				System.out.println("Person id: " + id + " died of " + reasonOfDeath.getName() + " after " + lifetime_days + " days.");
				if(logActions)
				{
					printLastActionList();
					printLastActionIndexList();
					printLastSensorValues();
				}
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
	
	public void updateMeasurementReadings(List<Double> measurements)
	{
		if(!isAlive()) return;
		
		this.measurementInputs = measurements;
	}
	
	public double getSensorReading(int index)
	{
		if(index < 0) return 0;
		if(index >= sensorInputs.size()) return 0;
		
		return sensorInputs.get(index);
	}
	
	public double getMeasurementReading(int index)
	{
		if(index < 0) return 0;
		if(index >= measurementInputs.size()) return 0;
		
		return measurementInputs.get(index);
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
			List<Action> actions = actionFactory.getAction(actionIndex);
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
		if(logActions)
		{
			lastActionQueue.add(actionList.get(0));
			lastActionIndexQueue.addAll(Arrays.asList(actionIndexList));
		}
	}
	
	private void printLastActionList()
	{
		System.out.print("[");
		for(Action action : lastActionQueue)
		{
			if(action instanceof Printable)
			{
				Printable printableAction = (Printable) action;
				System.out.print(printableAction.getDebugPrint() + " ");
			}
		}
		System.out.println("]");
		System.out.println(lastActionQueue.printCounter());
	}
	
	private void printLastActionIndexList()
	{
		System.out.print("[");
		for(Integer action : lastActionIndexQueue)
		{
			System.out.print(BasicAction.getValueOfIndex(action)+ " ");
		}
		System.out.println("]");
	}
	
	private void printLastSensorValues()
	{
		System.out.print("SensorInputs[");
		int counter = 0;
		for(double val : sensorInputs)
		{
			System.out.print("("+ counter + ":" + val + ")");
			counter++;
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
	
	private boolean lifetimeDebugginWritten = false;
	
	private void updateLifeStatus(int seconds) {

		nutrition -= nutrition_decline_rate*seconds;
		aqua -= aqua_decline_rate*seconds;
		lifetime_days += seconds/Const.SECONDS_PER_DAY;
		if(lifetime_days > 1 && (int)(lifetime_days) % 365 == 0 && !lifetimeDebugginWritten) 
		{
			if(logDebug)
			{
				System.out.println("Person: " + id + " has age: " + lifetime_days/365);
			}
			lifetimeDebugginWritten = true;	
		}
		
		if((int)(lifetime_days) % 365 != 0)
		{
			lifetimeDebugginWritten = false;
		}
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
	
	public void move(double dx, double dy, BasicAction actionType)
	{
		int x = (int) (coordinate.getX() + dx + 0.5);
		int y = (int) (coordinate.getY() + dy + 0.5);
		
		if(x > Const.WINDOW_WIDTH) x = Const.WINDOW_WIDTH;
		if(y > Const.WINDOW_HEIGHT) y = Const.WINDOW_HEIGHT;
		
		coordinate.setLocation(x, y);
		if(logActions)
		{
			lifePath.add((Point2D)coordinate.clone());
			lifePathType.add(actionType);
		}
		
		hasChangedCoordinate = true;
	}
	
	public void setLastSensorArea(Class<? extends Resource> resourceClass, SensorArea newArea)
	{
		lastAreaMap.put(resourceClass, newArea);
	}
	
	public void setLastSensorDirection(Class<? extends Resource> resourceClass, double direction)
	{
		lastDirectionMap.put(resourceClass, direction);
	}
	
	public SensorArea getLastSensorArea(Class<? extends Resource> resourceClass)
	{
		if(!lastAreaMap.containsKey(resourceClass))
		{
			return new SensorArea(-1, 1);
		}
		return lastAreaMap.get(resourceClass);
	}
	
	public double getLastDirection(Class<? extends Resource> resourceClass)
	{
		if(!lastDirectionMap.containsKey(resourceClass))
		{
			return 0.0;
		}
		return lastDirectionMap.get(resourceClass);
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

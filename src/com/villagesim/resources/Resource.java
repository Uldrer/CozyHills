package com.villagesim.resources;

import java.util.ArrayList;
import java.util.List;

import com.villagesim.Const;
import com.villagesim.interfaces.Depletable;
import com.villagesim.interfaces.DepletedListener;
import com.villagesim.interfaces.Updateable;

public abstract class Resource implements Updateable, Depletable {
	
	private String name;
	private int id;
	private double initialAmount; // One standard unit
	private double amount; // One standard unit
	private double weightPerAmount; // kg / standard unit
	
	// You can eat everything, but only some stuff is nutritious
	private double nutritionPerAmount; // 0 - 1 (0 - 100%)
	
	 // You can drink everything, but only some stuff contains water
	private double aquaPerAmount; // 0 - 1 (0 - 100%)
	
	private double decreaseRate = 0; // Decrease per second
	private double increaseRate = 0; // Increase per second
	
	private double lifetime_days = 0;
	
	private final double SECONDS_PER_YEAR = 31536000;
	
	private boolean depleted = false;
	private boolean printDebug = true;
	
	private static int id_counter = 0;
	
	private List<DepletedListener> listeners = new ArrayList<DepletedListener>();
	
	// Normal constructor
	public Resource(String name, double amount, double weightPerAmount, double nutritionPerAmount, double aquaPerAmount)
	{
		System.out.println("Constructing resource " + name);
		this.name = name;
		this.amount = amount;
		this.initialAmount = amount;
		this.weightPerAmount = weightPerAmount;
		this.nutritionPerAmount = nutritionPerAmount;
		this.aquaPerAmount = aquaPerAmount;
		this.id = ++id_counter;
	}
	
	// Non edible and non drinkable
	public Resource(String name, double amount, double weightPerAmount)
	{
		System.out.println("Constructing resource " + name);
		this.name = name;
		this.amount = amount;
		this.initialAmount = amount;
		this.weightPerAmount = weightPerAmount;
		this.nutritionPerAmount = 0;
		this.aquaPerAmount = 0;
		this.id = ++id_counter;
	}
	
	public boolean consume(double amount)
	{
		double newAmount = this.amount - amount;
		if(newAmount < 0) return false;
		
		this.amount = newAmount;
		return true;
	}
	
	public void add(double amount)
	{
		// Only allow additions
		if(amount < 0) return;
		
		this.amount += amount;
	}
	
	@Override
	public void update(int seconds) 
	{
		// Hysteres function that nonly fires once
		if(!depleted && this.amount <= 0) 
		{
			depleted = true;
			fireDepletedEvent(true);
			if(printDebug)
			{
				System.out.println("Resource " + id + ": " + name + " depleted after lifetime: " + lifetime_days);
			}
		}
		if(depleted && this.amount > initialAmount*0.1) 
		{
			depleted = false;
			fireDepletedEvent(false);
			if(printDebug)
			{
				System.out.println("Resource: " + id + ": " + name + " un-depleted after lifetime: " + lifetime_days);
			}
		}
		
		// TODO add minor random element here
		this.amount -= seconds*amount*decreaseRate;
		this.amount += seconds*amount*increaseRate;
		
		lifetime_days += seconds/Const.SECONDS_PER_DAY;
	}
	
	@Override
	public void addDepletedListener(DepletedListener listener)
	{
		if(!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}
	
	@Override
	public void fireDepletedEvent(boolean depleted)
	{
		for(DepletedListener listener : listeners)
		{
			listener.depletedEvent(depleted, name);
		}
	}
	
	public String getDebugString()
	{
		String str = "Resource: " + id + ": " + name  + " amount left: " + amount;
		return str;
	}

	// Getters
	public String getName() {
		return name;
	}

	public double getAmount() {
		return amount;
	}

	public double getWeightPerAmount() {
		return weightPerAmount;
	}

	public double getNutritionPerAmount() {
		return nutritionPerAmount;
	}

	public double getAquaPerAmount() {
		return aquaPerAmount;
	}
	
	public double getDecreaseRate() {
		return decreaseRate;
	}
	
	public double getIncreaseRate() {
		return increaseRate;
	}
	
	public void reset()
	{
		amount = initialAmount;
		depleted = false;
		lifetime_days = 0;
	}
	
	// Setters
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	// Input decrease-rate is the amount decrease per year in percent/100
	public void setDecreaseRate(double decreaseRate) {
		this.decreaseRate = decreaseRate/SECONDS_PER_YEAR;
	}
	
	// Input increase-rate is the amount decrease per year in percent/100
	public void setIncreaseRate(double increaseRate) {
		this.increaseRate = increaseRate/SECONDS_PER_YEAR;
	}
	
	
}

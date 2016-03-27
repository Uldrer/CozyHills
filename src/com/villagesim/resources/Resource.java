package com.villagesim.resources;

import com.villagesim.interfaces.Updateable;

public abstract class Resource implements Updateable {
	
	private String name;
	private double initialAmount; // One standard unit
	private double amount; // One standard unit
	private double weightPerAmount; // kg / standard unit
	
	// You can eat everything, but only some stuff is nutritious
	private double nutritionPerAmount; // 0 - 1 (0 - 100%)
	
	 // You can drink everything, but only some stuff contains water
	private double aquaPerAmount; // 0 - 1 (0 - 100%)
	
	private double decreaseRate = 0; // Decrease per second
	private double increaseRate = 0; // Increase per second
	
	private final double SECONDS_PER_YEAR = 31536000;
	
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
		// TODO add minor random element here
		this.amount -= seconds*decreaseRate;
		this.amount += seconds*increaseRate;
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
	}
	
	// Setters
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	// Input decrease-rate is the amount decrease per year in percent/100
	public void setDecreaseRate(double decreaseRate) {
		decreaseRate *= amount;
		this.decreaseRate = decreaseRate/SECONDS_PER_YEAR;
	}
	
	// Input increase-rate is the amount decrease per year in percent/100
	public void setIncreaseRate(double increaseRate) {
		increaseRate *= amount;
		this.increaseRate = increaseRate/SECONDS_PER_YEAR;
	}
	
	
}

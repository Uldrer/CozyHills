package com.villagesim.resources;

import java.awt.Color;
import java.awt.Graphics;

import com.villagesim.interfaces.Drawable;

public abstract class Resource implements Drawable {
	
	private String name;
	private double amount; // One standard unit
	private double weightPerAmount; // kg / standard unit
	
	// You can eat everything, but only some stuff is nutritious
	private double nutritionPerAmount; // 0 - 1 (0 - 100%)
	
	 // You can drink everything, but only some stuff contains water
	private double waterPerAmount; // 0 - 1 (0 - 100%)
	
	// Normal constructor
	public Resource(String name, double amount, double weightPerAmount, double nutritionPerAmount, double waterPerAmount)
	{
		System.out.println("Constructing resource");
		this.name = name;
		this.amount = amount;
		this.weightPerAmount = weightPerAmount;
		this.nutritionPerAmount = nutritionPerAmount;
		this.waterPerAmount = waterPerAmount;
	}
	
	// Non edible and non drinkable
	public Resource(String name, double amount, double weightPerAmount)
	{
		System.out.println("Constructing resource");
		this.name = name;
		this.amount = amount;
		this.weightPerAmount = weightPerAmount;
		this.nutritionPerAmount = 0;
		this.waterPerAmount = 0;
	}
	
	public boolean collect(double amount)
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
	public void draw(Graphics bbg)
	{
		Color color = this.getColor();
		
		bbg.setColor(color);
		// TODO fix bbg.fillRect((int)type.xy[0], (int)type.xy[1], type.size, type.size);
	}
	
	// Abstract methods
	public abstract Color getColor();

	
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

	public double getWaterPerAmount() {
		return waterPerAmount;
	}
	
	// Setters
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}

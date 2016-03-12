package com.villagesim.resources;

import java.awt.Color;

import com.villagesim.interfaces.Drawable;

public abstract class Resource implements Drawable {
	
	private String name;
	private double amount;
	private double weightPerAmount;
	private double nutritionPerAmount; // You can eat everything, but only some stuff is nutritious
	private double waterPerAmount; // You can drink everything, but only some stuff contains water
	
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
	public void draw()
	{
		Color color = this.getColor();
		// TODO Use color to draw
	}
	
	// Abstract methosd
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
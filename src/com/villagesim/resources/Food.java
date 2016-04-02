package com.villagesim.resources;

public abstract class Food extends Resource {

	public Food(String name, double amount, double weightPerAmount,
			double nutritionPerAmount, boolean printDebug) {
		super(name, amount, weightPerAmount, nutritionPerAmount, 0, printDebug);
	}

}

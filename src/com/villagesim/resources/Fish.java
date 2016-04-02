package com.villagesim.resources;

public class Fish extends Food {

	public Fish(double amount)
	{
		this(amount,  true);
	}
	
	public Fish(double amount, boolean printDebug) {
		super("Fish", amount, /*weightPerAmount*/ 0.5, /*nutritionPerAmount*/ 0.5, printDebug);
		// TODO make this abstract and and different kind of fish
	}

}

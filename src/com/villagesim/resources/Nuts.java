package com.villagesim.resources;

public class Nuts extends Food {

	public Nuts(double amount)
	{
		this(amount,  true);
	}
	
	public Nuts(double amount, boolean printDebug) {
		super("Nuts", amount, /*weightPerAmount 1 litre*/ 0.65, /*nutritionPerAmount*/ 0.95, printDebug);
		// TODO make this abstract and and different kind of nuts
	}

}

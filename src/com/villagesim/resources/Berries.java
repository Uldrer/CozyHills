package com.villagesim.resources;

public class Berries extends Food {

	public Berries(double amount)
	{
		this(amount,  true);
	}
	
	public Berries(double amount, boolean printDebug) {
		super("Berries", amount, /*weightPerAmount 1 litre*/ 0.5, /*nutritionPerAmount*/ 0.35, printDebug);
		// TODO make this abstract and and different kind of berries
	}

}

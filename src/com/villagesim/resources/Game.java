package com.villagesim.resources;

public class Game extends Food {

	public Game(double amount)
	{
		this(amount,  true);
	}
	
	public Game(double amount, boolean printDebug) {
		super("Game", amount, /*weightPerAmount*/ 60, /*nutritionPerAmount*/ 0.55, printDebug);
		// TODO make this abstract and and different kind of game
	}

}

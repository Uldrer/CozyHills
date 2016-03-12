package com.villagesim.resources;

public class Game extends Food {

	public Game(double amount) {
		super("Game", amount, /*weightPerAmount*/ 60, /*nutritionPerAmount*/ 0.55);
		// TODO make this abstract and and different kind of game
	}

}

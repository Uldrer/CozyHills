package com.villagesim.resources;

public class Berries extends Food {

	public Berries(double amount) {
		super("Berries", amount, /*weightPerAmount 1 litre*/ 0.5, /*nutritionPerAmount*/ 0.35);
		// TODO make this abstract and and different kind of berries
	}

}

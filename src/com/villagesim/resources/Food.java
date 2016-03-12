package com.villagesim.resources;

import java.awt.Color;

public abstract class Food extends Resource {

	public Food(String name, double amount, double weightPerAmount,
			double nutritionPerAmount) {
		super(name, amount, weightPerAmount, nutritionPerAmount, 0);
	}

	@Override
	public Color getColor() {
		return Color.CYAN;
	}

}

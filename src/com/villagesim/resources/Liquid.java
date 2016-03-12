package com.villagesim.resources;

import java.awt.Color;

public abstract class Liquid extends Resource {

	public Liquid(String name, double amount, double weightPerAmount,
			double nutritionPerAmount, double waterPerAmount) {
		super(name, amount, weightPerAmount, 0, waterPerAmount);
	}

	@Override
	public Color getColor() {
		return Color.BLUE;
	}

}

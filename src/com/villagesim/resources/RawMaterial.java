package com.villagesim.resources;

import java.awt.Color;

public abstract class RawMaterial extends Resource {

	public RawMaterial(String name, double amount, double weightPerAmount) {
		super(name, amount, weightPerAmount);
	}

	@Override
	public abstract Color getColor();

}

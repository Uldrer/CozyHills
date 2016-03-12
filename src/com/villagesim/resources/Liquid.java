package com.villagesim.resources;

public abstract class Liquid extends Resource {

	public Liquid(String name, double amount, double weightPerAmount, double waterPerAmount) {
		super(name, amount, weightPerAmount, 0, waterPerAmount);
	}
}

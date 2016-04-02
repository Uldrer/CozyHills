package com.villagesim.resources;

public class Water extends Liquid {

	public Water(double amount)
	{
		this(amount,  true);
	}
	
	public Water(double amount, boolean printDebug) 
	{
		super("Water", amount, /*weightPerAmount*/ 1, /*waterPerAmount*/ 1, printDebug);
	}
	
	

}

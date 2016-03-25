package com.villagesim.sensors;

public enum Sensor {
	
	DIST_TO_WATER(1), 			// #1 Distance to nearest drinking water
	DIST_TO_FOOD_STORAGE(2), 	// #2 Distance to nearest food storage
	DIST_TO_WILD_FOOD(3),		// #3 Distance to nearest wild food gathering ground
	DIST_TO_GAME(4),			// #4 Distance to nearest wild game herd
	DIST_TO_FISH(5),			// #5 Distance to nearest fishing ground
	THIRST(6),					// #6 Current person thirst value
	HUNGER(7),					// #7 Current person hunger value
	AQUA_IN_WATER(8),			// #8 Amount of aqua in nearest drinking water
	NUTRITION_IN_STORAGE(9),	// #9 Amount of nutrition in nearest food storage
	NUTRITION_IN_WILD_FOOD(10), // #10 Amount of nutrition in nearest wild food gathering ground
	NUTRITION_IN_GAME(11),		// #11 Amount of nutrition in nearest wild game herd
	NUTRITION_IN_FISH(12);		// #12 Amount of nutrition in nearest fishing ground

	private final int index;
	
	private Sensor(int index) {
		 this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
}
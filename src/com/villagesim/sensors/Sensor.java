package com.villagesim.sensors;

// Only add sensors, don't edit or remove, affects weight headers
public enum Sensor {
	
	DIST_TO_WATER(0), 					// #1 Distance to nearest drinking water
	DIST_TO_STORAGE(1), 				// #2 Distance to nearest food storage
	DIST_TO_WILD_FOOD(2),				// #3 Distance to nearest wild food gathering ground
	DIST_TO_GAME(3),					// #4 Distance to nearest wild game herd
	DIST_TO_FISH(4),					// #5 Distance to nearest fishing ground
	THIRST(5),							// #6 Current person thirst value
	HUNGER(6),							// #7 Current person hunger value
	AQUA_IN_WATER(7),					// #8 Amount of aqua in nearest drinking water
	NUTRITION_IN_STORAGE(8),			// #9 Amount of nutrition in nearest food storage
	NUTRITION_IN_WILD_FOOD(9), 			// #10 Amount of nutrition in nearest wild food gathering ground
	NUTRITION_IN_GAME(10),				// #11 Amount of nutrition in nearest wild game herd
	NUTRITION_IN_FISH(11),				// #12 Amount of nutrition in nearest fishing ground
	NUTRITION_IN_PERSONAL_STORAGE(12),	// #13 Amount of nutrition in personal storage
	AQUA_IN_PERSONAL_STORAGE(13),		// #14 Amount of aqua in personal storage
	AQUA_IN_STORAGE(14),				// #15 Amount of aqua in nearest storage
	DIRECTION_TO_NEAREST_WATER(15),		// #16 Direction to nearest drinking water
	DIRECTION_TO_NEAREST_WOOD(16),		// #17 Direction to nearest wood
	DIRECTION_TO_NEAREST_FISH(17);		// #18 Direction to nearest fishing ground with fish left

	private final int index;
	
	private Sensor(int index) {
		 this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public static final int size = Sensor.values().length;
}

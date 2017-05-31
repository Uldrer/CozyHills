package com.villagesim.sensors;

public enum Measurement {
	DIRECTION_TO_NEAREST_WATER,		// Direction to nearest drinking water
	DIRECTION_TO_NEAREST_WOOD,		// Direction to nearest wood
	DIRECTION_TO_NEAREST_FISH;		// Direction to nearest fishing ground with fish left
	
	private int index;
	
	private Measurement() {}

	
	public int getIndex() {
		return index;
	}
	
	public static Measurement getValueOfIndex(int index)
	{
		for(Measurement measurement : Measurement.values())
		{
			if(measurement.getIndex() == index)
			{
				return measurement;
			}
		}
		return null;
	}

	
	private void setIndex(int index) {
		this.index = index;
	}
	
	static {
		int counter = 0;
		for(Measurement action : Measurement.values())
		{
			action.setIndex(counter);
			counter++;
		}
	}
	
	public static final int size = Measurement.values().length;
}

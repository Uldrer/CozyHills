package com.villageorganismsim;

// Basic resource types available
public enum ResourceType {

	RawMaterial("Raw_material"),
	BuildingMaterial("Building_material"),
	LandSpace("Land_space"),
	CitySpace("City_space"),
	DrinkingWater("Drinking_water"),
	Food("Food"),
	LivingSpace("Living_space"),
	Population("Population");

	
	private final String type;
	private int index;
	
	private ResourceType(final String type) {
        this.type = type;
    }

	public String getType() {
		return type;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static ResourceType getValueOfIndex(int index)
	{
		for(ResourceType resource : ResourceType.values())
		{
			if(resource.getIndex() == index)
			{
				return resource;
			}
		}
		return null;
	}

	
	private void setIndex(int index) {
		this.index = index;
	}
	
	static {
		int counter = 0;
		for(ResourceType action : ResourceType.values())
		{
			action.setIndex(counter);
			counter++;
		}
	}
	
	public static final int size = ResourceType.values().length;
}

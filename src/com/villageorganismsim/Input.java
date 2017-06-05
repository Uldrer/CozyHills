package com.villageorganismsim;

public class Input {

	private Resource resource;
	private double consumeRate;
	
	public Input(Resource resource, double consumeRate)
	{
		this.resource = resource;
		this.consumeRate = consumeRate;
	}
	
	public double getConsumeRate()
	{
		return consumeRate;
	}
	
	public ResourceType getResourceType()
	{
		return resource.getType();
	}
}

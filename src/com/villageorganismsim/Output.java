package com.villageorganismsim;

public class Output {

	private Resource resource;
	private double productionRate;
	
	public Output(Resource resource, double productionRate)
	{
		this.resource = resource;
		this.productionRate = productionRate;
	}
	
	public double getProductionRate()
	{
		return productionRate;
	}
	
	public ResourceType getResourceType()
	{
		return resource.getType();
	}
	
}

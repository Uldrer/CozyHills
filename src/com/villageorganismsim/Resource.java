package com.villageorganismsim;

public class Resource {

	private ResourceType type;
	private String name;
	
	public Resource(String name, ResourceType type)
	{
		this.name = name;
		this.type = type;
	}
	
	public ResourceType getType() { return type; }
	public String getName() { return name; }
}

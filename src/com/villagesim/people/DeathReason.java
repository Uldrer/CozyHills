package com.villagesim.people;

public enum DeathReason {
	STARVATION("Starvation"),
	DEHYDRATION("Dehydraton"),
	OLD_AGE("Old age");
	
	private final String name;
	
	private DeathReason(final String name) {
        this.name = name;
    }
	
	public String getName()
	{
		return name; 
	}
}

package com.villagesim.areas;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.villagesim.resources.Fish;
import com.villagesim.resources.Resource;
import com.villagesim.resources.Water;

public class Lake extends Area {
	
	private final double LITERS_PER_CUBIC_METER = 1000;
	private boolean waterDepleted = false;
	private boolean fishDepleted = false;

	public Lake(int width, int height) {
		super(Color.BLUE, width, height);
	}

	@Override
	protected void populateResourceSet() {
		
		Set<Resource> resourceSet = new HashSet<Resource>();
		// Add resources
		Resource water = new Water(getSize() * LITERS_PER_CUBIC_METER);
		water.setIncreaseRate(0.1);
		water.setDecreaseRate(0.1);
		water.addDepletedListener(this);
		resourceSet.add(water);
		
		Resource fish = new Fish(getSize());
		fish.setIncreaseRate(0.5);
		fish.addDepletedListener(this);
		resourceSet.add(fish);
		
		setResourceSet(resourceSet);
	}
	
	private void updateColor()
	{
		Color color = Color.BLUE; // All is fine
		if(waterDepleted && fishDepleted)
		{
			color = Color.BLACK;
		}
		else if(waterDepleted)
		{
			color = Color.GRAY;
		}
		else if(fishDepleted)
		{
			color = Color.CYAN;
		}
		
		setColor(color);
	}

	@Override
	public void depletedEvent(boolean depleted, String name) {
		
		// TODO switch on enum
		if(name.equals("Water"))
		{
			waterDepleted = depleted;
		}
		else if(name.equals("Fish"))
		{
			fishDepleted = depleted;
		}
		
		updateColor();
	}

}

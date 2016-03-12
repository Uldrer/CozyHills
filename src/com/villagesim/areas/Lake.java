package com.villagesim.areas;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.villagesim.resources.Fish;
import com.villagesim.resources.Resource;
import com.villagesim.resources.Water;

public class Lake extends Area {

	public Lake(int width, int height) {
		super(Color.BLUE, width, height);
	}

	@Override
	protected void populateResourceSet() {
		
		Set<Resource> resourceSet = new HashSet<Resource>();
		// Add resources
		Resource water = new Water(getSize());
		water.setIncreaseRate(0.1);
		water.setDecreaseRate(0.1);
		resourceSet.add(water);
		
		Resource fish = new Fish(getSize());
		water.setIncreaseRate(0.5);
		resourceSet.add(fish);
		
		setResourceSet(resourceSet);
	}

}

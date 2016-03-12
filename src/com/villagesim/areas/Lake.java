package com.villagesim.areas;

import java.awt.Color;
import java.awt.geom.Point2D;
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
		resourceSet.add(new Water(getSize()));
		resourceSet.add(new Fish(getSize()));
		
		setResourceSet(resourceSet);
	}

}

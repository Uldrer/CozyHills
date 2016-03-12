package com.villagesim.areas;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.villagesim.resources.Berries;
import com.villagesim.resources.Game;
import com.villagesim.resources.Nuts;
import com.villagesim.resources.Resource;

public class Wood extends Area {

	public Wood(int width, int height) {
		super(Color.GREEN, width, height);
	}

	@Override
	protected void populateResourceSet() {
		Set<Resource> resourceSet = new HashSet<Resource>();
		// Add resources
		resourceSet.add(new Game(getSize()/40));
		resourceSet.add(new Nuts(getSize()));
		resourceSet.add(new Berries(getSize()));
		
		setResourceSet(resourceSet);
	}

}

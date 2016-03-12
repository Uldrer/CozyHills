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
		Resource game = new Game(getSize()/40);
		game.setIncreaseRate(0.5);
		resourceSet.add(game);
		
		Resource nuts = new Nuts(getSize());
		nuts.setIncreaseRate(0.3);
		resourceSet.add(nuts);
		
		Resource berries = new Berries(getSize());
		berries.setIncreaseRate(0.7);
		resourceSet.add(berries);
		
		setResourceSet(resourceSet);
	}

}

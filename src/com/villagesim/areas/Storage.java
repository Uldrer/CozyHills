package com.villagesim.areas;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.villagesim.resources.Berries;
import com.villagesim.resources.Fish;
import com.villagesim.resources.Game;
import com.villagesim.resources.Nuts;
import com.villagesim.resources.Resource;
import com.villagesim.resources.Water;

public class Storage extends Area {
	
	private static final int STORAGE_SIZE = 4;

	public Storage() {
		super(Color.MAGENTA, STORAGE_SIZE, STORAGE_SIZE);
	}

	@Override
	protected void populateResourceSet() {
		// Populated empty
		// TODO add possibility for food/water spoilage
		Set<Resource> resourceSet = new HashSet<Resource>();
		
		Resource water = new Water(0, false);
		water.addDepletedListener(this);
		resourceSet.add(water);
		
		Resource fish = new Fish(0, false);
		fish.addDepletedListener(this);
		resourceSet.add(fish);
		
		Resource game = new Game(0, false);
		game.addDepletedListener(this);
		resourceSet.add(game);
		
		Resource nuts = new Nuts(0, false);
		nuts.addDepletedListener(this);
		resourceSet.add(nuts);
		
		Resource berries = new Berries(0, false);
		berries.addDepletedListener(this);
		resourceSet.add(berries);
		
		this.setResourceSet(resourceSet);
	}

	@Override
	public void depletedEvent(boolean depleted, String name) {
		// TODO Change color for drawn storages depending on what is and what isn't present
	}

}

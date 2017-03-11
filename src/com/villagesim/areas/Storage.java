package com.villagesim.areas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
		
		populateResourceList();
	}

	@Override
	protected void populateResourceList() {
		// Populated empty
		// TODO add possibility for food/water spoilage
		List<Resource> resourceList = new ArrayList<Resource>();
		
		Resource water = new Water(0, false);
		water.addDepletedListener(this);
		resourceList.add(water);
		
		Resource fish = new Fish(0, false);
		fish.addDepletedListener(this);
		resourceList.add(fish);
		
		Resource game = new Game(0, false);
		game.addDepletedListener(this);
		resourceList.add(game);
		
		Resource nuts = new Nuts(0, false);
		nuts.addDepletedListener(this);
		resourceList.add(nuts);
		
		Resource berries = new Berries(0, false);
		berries.addDepletedListener(this);
		resourceList.add(berries);
		
		this.setResourceList(resourceList);
	}

	@Override
	public void depletedEvent(boolean depleted, String name) {
		// TODO Change color for drawn storages depending on what is and what isn't present
	}

}

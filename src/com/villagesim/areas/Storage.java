package com.villagesim.areas;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.villagesim.resources.Resource;

public class Storage extends Area {
	
	private static final int STORAGE_SIZE = 4;

	public Storage() {
		super(Color.MAGENTA, STORAGE_SIZE, STORAGE_SIZE);
	}

	@Override
	protected void populateResourceSet() {
		// Populated empty
		Set<Resource> resourceSet = new HashSet<Resource>();
		this.setResourceSet(resourceSet);
	}

	@Override
	public void depletedEvent(boolean depleted, String name) {
		// TODO this seems wrong?
		
	}

}

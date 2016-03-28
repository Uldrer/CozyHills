package com.villagesim.areas;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.villagesim.resources.Berries;
import com.villagesim.resources.Game;
import com.villagesim.resources.Nuts;
import com.villagesim.resources.Resource;

public class Wood extends Area {

	private boolean gameDepleted = false;
	private boolean nutsDepleted = false;
	private boolean berriesDepleted = false;
	
	public Wood(int width, int height) {
		super(Color.GREEN, width, height);
	}

	@Override
	protected void populateResourceSet() {
		Set<Resource> resourceSet = new HashSet<Resource>();
		
		// Add resources
		Resource game = new Game(getSize()/40);
		game.setIncreaseRate(0.5);
		game.addDepletedListener(this);
		resourceSet.add(game);
		
		Resource nuts = new Nuts(getSize());
		nuts.setIncreaseRate(0.3);
		nuts.addDepletedListener(this);
		resourceSet.add(nuts);
		
		Resource berries = new Berries(getSize());
		berries.setIncreaseRate(0.7);
		berries.addDepletedListener(this);
		resourceSet.add(berries);
		
		setResourceSet(resourceSet);
	}

	private void updateColor()
	{
		// TODO do it nicer
		Color color = Color.GREEN; // All is fine
		if(gameDepleted && nutsDepleted && berriesDepleted)
		{
			color = Color.RED;
		}
		else if(gameDepleted && nutsDepleted)
		{
			color = Color.ORANGE;
		}
		else if(gameDepleted && berriesDepleted)
		{
			color = Color.ORANGE;
		}
		else if(nutsDepleted && berriesDepleted)
		{
			color = Color.ORANGE;
		}
		else if(gameDepleted)
		{
			color = Color.PINK;
		}
		else if(nutsDepleted)
		{
			color = Color.PINK;
		}
		else if(berriesDepleted)
		{
			color = Color.PINK;
		}
		
		setColor(color);
	}

	@Override
	public void depletedEvent(boolean depleted, String name) {
		
		// TODO switch on enum
		if(name.equals("Game"))
		{
			gameDepleted = depleted;
		}
		else if(name.equals("Nuts"))
		{
			nutsDepleted = depleted;
		}
		else if(name.equals("Berries"))
		{
			berriesDepleted = depleted;
		}
		
		updateColor();
	}
}

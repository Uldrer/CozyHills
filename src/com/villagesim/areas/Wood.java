package com.villagesim.areas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
		
		populateResourceList();
	}

	@Override
	protected void populateResourceList() {
		List<Resource> resourceList = new ArrayList<Resource>();
		
		// Add resources
		Resource game = new Game(getSize()/40);
		game.setIncreaseRate(0.5);
		game.addDepletedListener(this);
		resourceList.add(game);
		
		Resource nuts = new Nuts(getSize());
		nuts.setIncreaseRate(0.3);
		nuts.addDepletedListener(this);
		resourceList.add(nuts);
		
		Resource berries = new Berries(getSize());
		berries.setIncreaseRate(0.7);
		berries.addDepletedListener(this);
		resourceList.add(berries);
		
		setResourceList(resourceList);
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

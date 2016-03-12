package com.villagesim.people;

public interface PersonInterface {
	
	int TakeAction(); //returns action index of computed next action
	void UpdateLifeStatus(int seconds); // Determines if the person dies or lives on
	void UpdateSensorReadings(); // Determines current sensor readings for this person
	 
}

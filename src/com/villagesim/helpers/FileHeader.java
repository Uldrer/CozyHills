package com.villagesim.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.villagesim.actions.BasicAction;
import com.villagesim.sensors.Sensor;

public class FileHeader {
	
	private WeightType weightType;
	private int[] neuralNetworkNodes;
	private ArrayList<String> sensorList = new ArrayList<String>();
	private ArrayList<String> actionList = new ArrayList<String>();
	
	public enum WeightType {
		UNDEFINED, MAIN
    }
	
	public FileHeader()
	{
		weightType = WeightType.UNDEFINED;
		neuralNetworkNodes = new int[2];
	}
	
	public FileHeader(WeightType wType)
	{
		weightType = wType;
		neuralNetworkNodes = new int[2];
		
		// Setup Sensors and Actions
		init();
		
		neuralNetworkNodes[0] = sensorList.size();
		neuralNetworkNodes[1] = actionList.size();
	}
	
	public ArrayList<String> getSensorList()
	{
		return sensorList;
	}
	
	public ArrayList<String> getActionList()
	{
		return actionList;
	}
	
	private void init()
	{
		// Add sensors
		for(Sensor s : Sensor.values())
		{
			sensorList.add(s.toString());
		}
		
		// Add actions
		switch(weightType)
		{
		case MAIN:
			for(BasicAction a : BasicAction.values())
			{
				actionList.add(a.toString());
			}
			break;
		default:
			break;
		}
	}
	
	public int[] getNodes()
	{
		return neuralNetworkNodes.clone();
	}
	
	public FileHeader parseHeader(BufferedReader br)
	{
		try {
			while (br.ready())
			{
			    String lineString = br.readLine();
			    
			    String[] lineSplit = lineString.split(" ");
			    
			    if(lineSplit[0].equals("Header")) continue;
			    
			    if(lineSplit[0].equals("Type:"))
			    {
			    	weightType = WeightType.valueOf(lineSplit[1]);
			    	continue;
			    }
			    
			    if(lineSplit[0].equals("Sensors:"))
			    {
			    	neuralNetworkNodes[0] = Integer.parseInt(lineSplit[1]);
			    	continue;
			    }
			    
			    if(lineSplit[0].equals("Actions:"))
			    {
			    	neuralNetworkNodes[1] = Integer.parseInt(lineSplit[1]);
			    	continue;
			    }
			    
			    if(lineSplit[0].equals("SensorNames:"))
			    {
			    	for(int i = 1; i < lineSplit.length; i++)
			    	{
			    		sensorList.add(lineSplit[i]);
			    	}
			    	continue;
			    }
			    
			    if(lineSplit[0].equals("ActionNames:"))
			    {
			    	for(int i = 1; i < lineSplit.length; i++)
			    	{
			    		actionList.add(lineSplit[i]);
			    	}
			    	continue;
			    }
			    
			    // Exit parsing
			    if(lineSplit[0].equals("EndHeader"))
			    {
			    	break;
			    }
			    
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		
		if(!FileHeader.class.isAssignableFrom(obj.getClass())) return false;
		
		final FileHeader other = (FileHeader) obj;
		
		if(this.weightType != other.weightType) return false;
		if(other.getNodes() == null) return false;
		if(other.getNodes().length != 2) return false;
		if(this.neuralNetworkNodes[0] != other.getNodes()[0]) return false;
		if(this.neuralNetworkNodes[1] != other.getNodes()[1]) return false;
		
		return true;
	}
	
	public boolean validateInput(Object obj)
	{
		if(obj == null) return false;
		
		if(!FileHeader.class.isAssignableFrom(obj.getClass())) return false;
		
		final FileHeader other = (FileHeader) obj;
		
		if(this.weightType != other.weightType) return false;
		if(other.getNodes() == null) return false;
		if(other.getNodes().length != 2) return false;
		
		return true;
	}
	
	public String toString()
	{
		String output = "";
		
		// Write header info
		output += "Header" + "\r\n";
		output += "Type: " + weightType.toString() + "\r\n";
		output += "Sensors: " + neuralNetworkNodes[0] + "\r\n";
		output += "Actions: " + neuralNetworkNodes[1] + "\r\n";
		output += "SensorNames:";
		for(String s : sensorList)
		{
			output += " " + s.toString();
		}
		output += "\r\n";
		output += "ActionNames:";
		for(String a : actionList)
		{
			output += " " + a.toString();
		}
		output += "\r\n";
		output += "EndHeader" + "\r\n";
		
		return output;
	}

}

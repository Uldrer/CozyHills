package com.villagesim.helpers;

import java.io.BufferedReader;
import java.io.IOException;

import com.villagesim.actions.AdvancedAction;
import com.villagesim.actions.BasicAction;
import com.villagesim.sensors.Sensor;

public class FileHeader {
	
	private WeightType weightType;
	
	public enum WeightType {
		UNDEFINED, WORK, MAIN, MOVE, GATHER
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
		neuralNetworkNodes[0] = Sensor.size;
		
		int actions = 0;
		switch(wType)
		{
		case GATHER:
			actions = AdvancedAction.getSize(BasicAction.GATHER);
			break;
		case MAIN:
			actions = BasicAction.size;
			break;
		case MOVE:
			actions = AdvancedAction.getSize(BasicAction.MOVE);
			break;
		case WORK:
			actions = AdvancedAction.getSize(BasicAction.WORK);
			break;
		default:
			actions = 0;
			break;
		}
		
		neuralNetworkNodes[1] = actions;
	}
	
	public int[] getNodes()
	{
		return neuralNetworkNodes.clone();
	}
	
	private int[] neuralNetworkNodes;
	
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
			    
			    // TODO check order?
			    
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
	
	public String toString()
	{
		String output = "";
		
		// Write header info
		output += "Header" + "\r\n";
		output += "Type: " + weightType.toString() + "\r\n";
		output += "Sensors: " + neuralNetworkNodes[0] + "\r\n";
		output += "Actions: " + neuralNetworkNodes[1] + "\r\n";
		output += "SensorNames:";
		for(Sensor s : Sensor.values())
		{
			output += " " + s.toString();
		}
		output += "\r\n";
		output += "ActionNames:";
		switch(weightType)
		{
		case GATHER:
			for(AdvancedAction a : AdvancedAction.values())
			{
				if(a.getActionType() == BasicAction.GATHER.getActionType())
				{
					output += " " + a.toString();
				}	
			}
			break;
		case MAIN:
			for(BasicAction a : BasicAction.values())
			{
				output += " " + a.toString();
			}
			break;
		case MOVE:
			for(AdvancedAction a : AdvancedAction.values())
			{
				if(a.getActionType() == BasicAction.MOVE.getActionType())
				{
					output += " " + a.toString();
				}	
			}
			break;
		case WORK:
			for(AdvancedAction a : AdvancedAction.values())
			{
				if(a.getActionType() == BasicAction.WORK.getActionType())
				{
					output += " " + a.toString();
				}	
			}
			break;
		default:
			break;
		}
		output += "\r\n";
		output += "EndHeader" + "\r\n";
		
		return output;
	}

}

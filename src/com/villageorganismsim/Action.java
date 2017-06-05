package com.villageorganismsim;

import java.util.ArrayList;

public class Action {
	
	private String name;
	private int id;
	private ArrayList<Input> inputs;
	private ArrayList<Output> outputs;
	
	private static int id_counter = 0;
	
	public Action(String name)
	{
		this.name = name;
		this.id = id_counter++;
		inputs = new ArrayList<Input>();
		outputs = new ArrayList<Output>();
	}
	
	public void addInput(Input input)
	{
		inputs.add(input);
	}
	
	public void addOutput(Output output)
	{
		outputs.add(output);
	}
	
	ArrayList<Input> getInputs()
	{
		return inputs;
	}
	ArrayList<Output> getOutputs()
	{
		return outputs;
	}
	public String getName() 
	{
		return name;
	}
	
	public int getId()
	{
		return id;
	}


}

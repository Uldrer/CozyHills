package com.villagesim;

import com.villagesim.ui.GATrainer;
import com.villagesim.ui.GraphicsHandler;
import com.villagesim.ui.PSOTrainer;

public class Main {

	public static void main(String[] args) {
		if(args.length > 0)
		{
			if(args[0].toLowerCase().equals("ga"))
			{
				GATrainer gaTrainer = new GATrainer();
				gaTrainer.run();
			}
			else if(args[0].toLowerCase().equals("pso"))
			{
				PSOTrainer psoTrainer = new PSOTrainer();
				psoTrainer.run();
			}
			else
			{
				// Default is GA
				GATrainer gaTrainer = new GATrainer();
				gaTrainer.run();
			}
		}
		else
		{
			GraphicsHandler graphicsHandler = new GraphicsHandler();
	        graphicsHandler.run();
		}
		
        System.exit(0);
	}

}

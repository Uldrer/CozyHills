package com.villagesim;

import com.villagesim.ui.GATrainer;
import com.villagesim.ui.GraphicsHandler;

public class Main {

	public static void main(String[] args) {
		if(args.length > 0)
		{
			GATrainer gaTrainer = new GATrainer();
			gaTrainer.run();
		}
		else
		{
			GraphicsHandler graphicsHandler = new GraphicsHandler();
	        graphicsHandler.run();
		}
		
        System.exit(0);
	}

}

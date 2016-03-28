package com.villagesim.ui;

import javax.swing.*;

import com.cozyhills.ui.InputHandler;
import com.villagesim.Const;
import com.villagesim.VillageSimulator;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by pere5 on 21/12/15.
 */
public class GraphicsHandler extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isRunning = true;
    private int windowWidth = Const.WINDOW_WIDTH;
    private int windowHeight =  Const.WINDOW_HEIGHT;

    private BufferedImage backBuffer;
    private Insets insets;
    private InputHandler input;
    
    private boolean paused = false;

    private VillageSimulator villageSimulator = new VillageSimulator();

    int x = 0;

    /**
     * This method starts the game and runs it in a loop
     */
    public void run() {
        initialize();
        int character;
        int intendedFps = 6;

        while(isRunning) {

            long time = System.currentTimeMillis();
            update();
            if(!villageSimulator.isAlive())
            {
            	draw();
            }
            if(true)
            	continue;
            
            character = ThreadLocalRandom.current().nextInt(1, 3 + 1);
            System.out.print(character == 1 ? " - " : character == 2 ? " + " : " * ");
            System.out.println(System.currentTimeMillis() - time);

            //  delay for each frame  -   time it took for one frame
            time = (1000 / intendedFps) - (System.currentTimeMillis() - time);
            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch(Exception e) {
                    System.out.println("Woohah!");
                }
            }
        }
        setVisible(false);
    }

    /**
     * This method will set up everything need for the game to run
     */
    private void initialize() {
        setTitle("VillageSimulator");
        setSize(windowWidth, windowHeight);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        insets = getInsets();
        setSize(insets.left + windowWidth + insets.right,
                insets.top + windowHeight + insets.bottom);

        backBuffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
        input = new InputHandler(this);
    }

    /**
     * This method will check for input, move things
     * around and check for win conditions, etc
     */
   
    private void update() {
    	if(!paused)
    	{
    		villageSimulator.update();
    	}
    	else
    	{
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	if (input.isKeyDown(KeyEvent.VK_SPACE)) {
    	
    		paused = !paused;
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    /**
     * This method will draw everything
     */
    private void draw() {
        Graphics g = getGraphics();

        Graphics bbg = backBuffer.getGraphics();
        bbg.setColor(Color.WHITE);
        bbg.fillRect(0, 0, windowWidth, windowHeight);

        bbg.setColor(Color.BLACK);

        villageSimulator.drawAllObjects(bbg);

        g.drawImage(backBuffer, insets.left, insets.top, this);
    }
}

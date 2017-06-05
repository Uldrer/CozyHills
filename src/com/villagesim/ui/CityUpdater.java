package com.villagesim.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.villageorganismsim.City;
import com.villagesim.Const;

public class CityUpdater extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6569183957042804223L;
	private InputHandler input;
	private City city;
	private boolean isRunning = true;
    private int windowWidth = Const.WINDOW_WIDTH;
    private int windowHeight =  Const.WINDOW_HEIGHT;
    private Insets insets;
    private BufferedImage backBuffer;
    private boolean paused = false;
	
	public CityUpdater()
	{
		city = new City();
		initialize();
	}
	
	private void initialize() 
	{
		setTitle("CitySimulator");
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
	
	public void run()
	{
		initialize();
        int intendedFps = 8760;
        city.optimizePopulation();

        while(isRunning) {

            long time = System.currentTimeMillis();
            
            if(!paused)
        	{
            	city.update();
        	}
            
            if (input.isKeyDown(KeyEvent.VK_SPACE)) {
            	paused = !paused;
            	city.optimizePopulation();
        		
        		try {
    				Thread.sleep(100);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            
            draw();

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
     * This method will draw everything
     */
    private void draw() {
        Graphics g = getGraphics();

        Graphics bbg = backBuffer.getGraphics();
        bbg.setColor(Color.WHITE);
        bbg.fillRect(0, 0, windowWidth, windowHeight);

        bbg.setColor(Color.BLACK);

        city.draw(bbg);

        g.drawImage(backBuffer, insets.left, insets.top, this);
    }
}

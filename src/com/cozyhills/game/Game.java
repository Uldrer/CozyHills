package com.cozyhills.game;

import com.cozyhills.cozy.CozyHills;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Created by pere5 on 21/12/15.
 */
public class Game extends JFrame {
    boolean isRunning = true;
    int fps = 6;
    int windowWidth = 500;
    int windowHeight = 500;

    BufferedImage backBuffer;
    Insets insets;
    InputHandler input;

    CozyHills cozyHills = new CozyHills();

    int x = 0;

    /**
     * This method starts the game and runs it in a loop
     */
    public void run() {
        initialize();

        while(isRunning) {
            long time = System.currentTimeMillis();

            update();
            draw();

            //  delay for each frame  -   time it took for one frame
            time = (1000 / fps) - (System.currentTimeMillis() - time);

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
    void initialize() {
        setTitle("CozyHills");
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
    void update() {
        CozyHills.update();
        /*
        if (input.isKeyDown(KeyEvent.VK_RIGHT)) {
            x += 5;
        }
        if (input.isKeyDown(KeyEvent.VK_LEFT)) {
            x -= 5;
        }*/

    }

    /**
     * This method will draw everything
     */
    void draw() {
        Graphics g = getGraphics();

        Graphics bbg = backBuffer.getGraphics();
        bbg.setColor(Color.WHITE);
        bbg.fillRect(0, 0, windowWidth, windowHeight);

        bbg.setColor(Color.BLACK);

        drawAllObjects(bbg);

        g.drawImage(backBuffer, insets.left, insets.top, this);
    }

    private void drawAllObjects(Graphics bbg) {
        bbg.drawRect(x, 10, 20, 20);
        bbg.drawRect(x, 5, 10, 10);
    }
}

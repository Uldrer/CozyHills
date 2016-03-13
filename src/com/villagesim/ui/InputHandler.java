package com.villagesim.ui;

/**
 * Created by pere5 on 21/12/15.
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Makes handling input a lot simpler
 */
public class InputHandler implements KeyListener
{
    private boolean [] keys = new boolean[256];

    /**
     * Assigns the newly created InputHandler to a Component
     * @param c Component to get input from
     */
    public InputHandler(Component c) {
        c.addKeyListener(this);
    }

    /**
     * Checks whether a specific key is down
     * @param keyCode The key to check
     * @return Whether the key is pressed or not
     */
    public boolean isKeyDown(int keyCode) {
        return keyCode > 0 && keyCode < 256 && keys[keyCode];
    }

    /**
     * Called when a key is pressed while the component is focused
     * @param e KeyEvent sent by the component
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = true;
        }
    }

    /**
     * Called when a key is released while the component is focused
     * @param e KeyEvent sent by the component
     */
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = false;
        }
    }

    /**
     * Not used
     */
    public void keyTyped(KeyEvent e){}
}
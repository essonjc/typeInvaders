package com.zetcode.sprite;

import com.zetcode.Commons;

import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;

public class Player extends Sprite {

    private int width;
    private int currentLane;

    public Player(int lane, int x) {
        currentLane = lane;
        initPlayer(x);
    }

    public int getLane() {
        return currentLane;
    }

    private void initPlayer(int x) {

        var playerImg = "src/images/player.png";
        var ii = new ImageIcon(playerImg);

        width = ii.getImage().getWidth(null);
        setImage(ii.getImage());

        int START_X = x;
        setX(START_X);

        int START_Y = 700;
        setY(START_Y);
    }

    public void act() {

        x += dx;

        if (x <= 2) {

            x = 2;
        }

        if (x >= Commons.BOARD_WIDTH - 2 * width) {

            x = Commons.BOARD_WIDTH - 2 * width;
        }
    }

//    public void keyPressed(KeyEvent e) {
//
//        int key = e.getKeyCode();
//
//        if (key == KeyEvent.VK_LEFT) {
//
//            dx = -2;
//        }
//
//        if (key == KeyEvent.VK_RIGHT) {
//
//            dx = 2;
//        }
//    }
//
    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {

            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {

            dx = 0;
        }
    }

    public void setLane(int lane, int dx){
        currentLane = lane;
        this.dx = dx;
    }

    public int getCurrentLane() {
        return currentLane;
    }

    public void setCurrentLane(int currentLane) {
        this.currentLane = currentLane;
    }
}

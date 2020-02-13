package com.zetcode.sprite;

import com.zetcode.Commons;

import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;

public class Alien extends Sprite {

    private Bomb bomb;
    private String word;
    private int health;
    private int index;
    private boolean attacking;


    public Alien(int x, int y, String s) {
        word = s;
        health = s.length();
        index = 0;
        initAlien(x, y);
    }

    public int getIndex(){
        return this.index;
    }

    public int getHealth(){
        return this.health;
    }
    public String getWord(){
        return this.word;
    }

    private void initAlien(int x, int y) {

        this.x = x;
        this.y = y;

        var alienImg = "src/images/alien.png";
        var ii = new ImageIcon(alienImg);

        setImage(ii.getImage());

        attacking = false;
    }

    public int getHit(){
        health -= 1;
        return health;
    }

    public Bomb getBomb() {
        return bomb;
    }

    public void setBomb(Bomb b) {
        bomb = b;
    }

    public boolean isNextLetter(int key) {
        if(index < word.length() && key == KeyEvent.getExtendedKeyCodeForChar(word.charAt(index))){
            index++;
            return true;
        }
        return false;
    }

    public void startAttacking() {
        if(!attacking) {
            attacking = true;
            bomb = new Bomb(getX(), getY() + Commons.ALIEN_HEIGHT);
            bomb.setDestroyed(false);
        }
    }

    public boolean isAttacking() {
        return attacking;
    }
}

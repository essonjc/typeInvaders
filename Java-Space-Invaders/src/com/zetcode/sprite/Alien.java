package com.zetcode.sprite;

import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;

public class Alien extends Sprite {

    private Bomb bomb;
    private String word;
    private int health;
    private int index;

    public Alien(int x, int y, String s) {
        word = s;
        health = s.length();
        index = 0;
        initAlien(x, y);
    }

    public String getWord(){
        return this.word;
    }

    private void initAlien(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var alienImg = "src/images/alien.png";
        var ii = new ImageIcon(alienImg);

        setImage(ii.getImage());
    }

    public int getHit(){
        health -= 1;
        return health;
    }

    public Bomb getBomb() {

        return bomb;
    }

    public boolean isNextLetter(int key) {
        if(index < word.length() && key == KeyEvent.getExtendedKeyCodeForChar(word.charAt(index))){
            index++;
            return true;
        }
        return false;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
    }
}

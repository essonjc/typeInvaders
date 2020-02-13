package com.zetcode.sprite;

import javax.swing.*;

public class Planet extends Sprite {

    private int health;

    public Planet(){
        initPlanet();
    }

    private void initPlanet() {
        var planetImg = "src/images/planet.png";
        var ii = new ImageIcon(planetImg);
        setImage(ii.getImage());
        health = 10;
    }

    public void hit() {
        health -= 1;
    }

    public boolean dead() {
        return health<=0;
    }
}

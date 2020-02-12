package com.zetcode.sprite;

import javax.swing.*;

public class Planet extends Sprite {

    public Planet(){
        initPlanet();
    }

    private void initPlanet() {
        var planetImg = "src/images/planet.png";
        var ii = new ImageIcon(planetImg);
        setImage(ii.getImage());
    }

}

package com.zetcode.sprite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Lane {

    private int x_coordinate;
    private Queue<Alien> aliens;

    public Lane(int x){
        x_coordinate = x;
        aliens = new ArrayDeque<>();
    }

    public int getX_coordinate(){
        return x_coordinate;
    }

    public void spawnAlien(){
        aliens.add(new Alien(x_coordinate, 0, "test"));
    }

    public void killAlien(){
        aliens.poll();

        this.spawnAlien();
    }

    public Queue<Alien> getAliens(){
        return aliens;
    }

    public Alien getNextAlien(){
        return aliens.peek();
    }

}

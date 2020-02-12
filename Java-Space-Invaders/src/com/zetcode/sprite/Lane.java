package com.zetcode.sprite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

public class Lane {

    private String[] dictionary = {"alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", "india", "juliett", "kilo", "lima", "mike", "november", "oscar", "papa", "quebec", "romeo", "sierra", "tango", "uniform", "victor", "whiskey", "xray", "yankee", "zulu"};
    private final static int DICTIONARY_LENGTH = 26;

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
        Random r = new Random();
        int wordIndex = r.nextInt(dictionary.length);
        aliens.add(new Alien(x_coordinate, 0, dictionary[wordIndex]));
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

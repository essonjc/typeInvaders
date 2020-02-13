package com.zetcode;

import com.zetcode.sprite.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Board extends JPanel {

    private Dimension d;

    private Lane[] lanes;

    private Player player;
    private ArrayList<Shot> shots;
    private Planet planet;
    private boolean gameOver;

    private int deaths = 0;

    private boolean inGame = true;
    private String explImg = "src/images/explosion.png";
    private String gameOverImg = "src/images/gameover.png";
    private String message = "Game Over, you kind of suck at this.";

    private Timer timer;
    private int ticks;

    private Date initialTime;
    private Date finalTime;

    private long spawnTime = 2000; //2 seconds

    Random generator;

    public Board() {

        initBoard();
        gameInit();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        setBackground(Color.black);

        timer = new Timer(Commons.DELAY, new GameCycle());
        timer.start();
        System.out.println(timer.toString());
        ticks = 0;

        gameOver = false;
    }


    private void gameInit() {

        initialTime = new Date();
        generator = new Random();

        lanes = new Lane[4];
        int spaceBetween = Commons.BOARD_WIDTH/4;
        int firstLane = spaceBetween/2;
        lanes[0] = new Lane(firstLane);
        lanes[1] = new Lane(firstLane + spaceBetween);
        lanes[2] = new Lane(firstLane + spaceBetween*2);
        lanes[3] = new Lane(firstLane + spaceBetween*3);

        lanes[generator.nextInt(4)].spawnAlien();

        player = new Player(0, lanes[0].getX_coordinate());
      //  player.setLane(0,lanes[0].getX_coordinate());
        shots = new ArrayList<>();

        planet = new Planet();
    }

    private void drawAliens(Graphics g) {

        for (Lane lane: lanes){
            for (Alien alien : lane.getAliens()) {

                if (alien.isVisible()) {

                    g.drawImage(alien.getImage(), alien.getX() - (Commons.ALIEN_WIDTH / 2), alien.getY(), Commons.ALIEN_WIDTH, Commons.ALIEN_HEIGHT, this);

                    g.drawString(alien.getWord(), alien.getX() - (alien.getWord().length()), alien.getY() + 10 + Commons.ALIEN_HEIGHT);

                    //Highlighting
                    drawHighlight(g, alien);
                }

                if (alien.isDying()) {

                    alien.die();
                    lane.killAlien();
                }
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX() - (Commons.PLAYER_WIDTH/2), player.getY() - Commons.PLAYER_HEIGHT, Commons.PLAYER_WIDTH, Commons.PLAYER_HEIGHT, this);
        }

        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawPlanet(Graphics g) {
        g.drawImage(planet.getImage(), 0, Commons.GROUND, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT - Commons.GROUND, this);
    }

    private void drawShot(Shot shot, Graphics g) {

        if (shot.isVisible()) {

            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    private void drawBombing(Graphics g) {

        for (Lane lane: lanes) {
            for (Alien a : lane.getAliens()) {

                Bomb b = a.getBomb();

                if (b != null) {
                    if(b.isDestroyed()){
                        var ii = new ImageIcon(explImg);
                        g.drawImage(ii.getImage(), b.getX(), b.getY(), this);
                    }else {
                        g.drawImage(b.getImage(), b.getX(), b.getY(), this);
                    }
                }
            }
        }
    }

    private void drawShots(Graphics g){
        for (Shot shot: shots){
            drawShot(shot, g);
        }
    }

    private void drawHighlight(Graphics g, Alien alien){
        FontMetrics ft = g.getFontMetrics();
        Rectangle2D rect = ft.getStringBounds(alien.getWord(),
                0,
                alien.getWord().length()-alien.getHealth(),
                g);
        Color defaultColor = g.getColor();
        g.setColor(Color.BLACK);
        g.fillRect(alien.getX() - (alien.getWord().length()),
                alien.getY() + Commons.ALIEN_HEIGHT,
                (int)rect.getWidth(),
                (int)rect.getHeight());
        g.setColor(defaultColor);
    }

    @Override
    public void paintComponent(Graphics g) {
            super.paintComponent(g);
            doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        if (inGame) {

            g.setColor(Color.black);
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Color.green);

            g.drawLine(0, Commons.GROUND,
                    Commons.BOARD_WIDTH, Commons.GROUND);
            drawPlanet(g);

            drawAliens(g);
            drawPlayer(g);
            drawShots(g);
            drawBombing(g);

            Toolkit.getDefaultToolkit().sync();

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);

            Toolkit.getDefaultToolkit().sync();
        }

    }

    private void gameOver(Graphics g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        var ii = new ImageIcon(gameOverImg);
        g.drawImage(ii.getImage(), Commons.BOARD_WIDTH / 2 - ii.getIconWidth() / 2, Commons.BOARD_HEIGHT/2 - ii.getIconHeight()/2, ii.getIconWidth(), ii.getIconHeight(), this);

            g.setColor(new Color(0, 32, 48));
            g.fillRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);
            g.setColor(Color.white);
            g.drawRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);

            var small = new Font("Helvetica", Font.BOLD, 14);
            var fontMetrics = this.getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(message, (Commons.BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                    Commons.BOARD_WIDTH / 2);

    }

    private void update() {
        finalTime = new Date();

        if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {

            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
       // player.act();

        ticks = (ticks % 2) + 1;

        ArrayList<Shot> deadShots = new ArrayList<>();

        // shot
        for (Shot shot : shots) {
            if (shot.isVisible()) {

                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Lane lane: lanes) {
                    for (Alien alien : lane.getAliens()) {

                        int alienX = alien.getX();
                        int alienY = alien.getY();

                        if (alien.isVisible()) {
                            if (shotX >= (alienX)
                                    && shotX <= (alienX + Commons.ALIEN_WIDTH)
                                    && shotY >= (alienY)
                                    && shotY <= (alienY + Commons.ALIEN_HEIGHT)) {

                                var health = alien.getHit();

                                if (health == 0) {
                                    var ii = new ImageIcon(explImg);
                                    alien.setImage(ii.getImage());
                                    alien.setDying(true);
                                    deaths++;
                                }
                                shot.die();
                                deadShots.add(shot);
                            }
                        }
                    }
                }

                int y = shot.getY();
                y -= 4;

                if (y < 0) {
                    shot.die();
                    deadShots.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }

        for (Shot shot: deadShots) {
            shots.remove(shot);
        }

        // aliens


        if(finalTime.getTime() - initialTime.getTime() >= spawnTime){
            initialTime = finalTime;
            int select = generator.nextInt(4);
            lanes[select].spawnAlien();
        }

        for(Lane lane: lanes) {
            for (Alien alien : lane.getAliens()) {

                if (ticks == 2) {
                    if(!alien.isAttacking()) {
                        alien.setY(alien.getY() + 1);
                    }

                    if(alien.getY() >= Commons.GROUND - Commons.PLAYER_HEIGHT - Commons.ALIEN_HEIGHT - 10){
                        alien.startAttacking();
                    }
                }

            }
        }

        // bombs

        for(Lane lane: lanes) {
            for (Alien alien : lane.getAliens()) {

                int shot = generator.nextInt(15);
                Bomb bomb = alien.getBomb();

                if (bomb != null) {
                    if (alien.isVisible() && bomb.isDestroyed()) {
                        bomb.setDestroyed(false);
                        bomb.setX(alien.getX());
                        bomb.setY(alien.getY() + Commons.ALIEN_HEIGHT);
                    }

                    int bombY = bomb.getY();

                    if (player.isVisible() && !bomb.isDestroyed()) {

                        if (bombY >= Commons.BOARD_HEIGHT - 100) {

                            var ii = new ImageIcon(explImg);
                            bomb.setImage(ii.getImage());
                        }
                    }

                    if (!bomb.isDestroyed()) {

                        bomb.setY(bomb.getY() + 1);

                        if (bomb.getY() >= Commons.BOARD_HEIGHT - 100) {

                            bomb.setDestroyed(true);
                            planet.hit();
                        }
                    }
                }
            }
        }

        if(planet.dead()){
            player.setDying(true);
        }
    }

    private void doGameCycle() {
        if(!gameOver){
            update();
            repaint();
        }
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    private Alien getEnemyInLine(){
        return lanes[player.getCurrentLane()].getNextAlien();
    }

    private void movePlayer(KeyEvent e){
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            int currentLane = player.getLane();
            if (currentLane > 0) {
                player.setLane(currentLane - 1, lanes[currentLane - 1].getX_coordinate());
            }
        }

        if (key == KeyEvent.VK_RIGHT) {
            int currentLane = player.getLane();
            if (currentLane < lanes.length - 1) {
                player.setLane(currentLane + 1, lanes[currentLane + 1].getX_coordinate());
            }
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            //player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {

            movePlayer(e);

            int x = player.getX() - 5;
            int y = player.getY() - Commons.PLAYER_HEIGHT;

            int key = e.getKeyCode();

            if (key <= KeyEvent.VK_Z && key >= KeyEvent.VK_A) {

                if (inGame) {
                    var alien = getEnemyInLine();

                    if (alien != null && alien.isNextLetter(key)) {
                        Shot shot = new Shot(x, y);
                        shots.add(shot);
                    }
                }
            }
        }
    }
}

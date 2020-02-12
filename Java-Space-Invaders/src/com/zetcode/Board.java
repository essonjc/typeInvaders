package com.zetcode;

import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Lane;
import com.zetcode.sprite.Player;
import com.zetcode.sprite.Shot;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Board extends JPanel {

    private Dimension d;

    private Lane[] lanes;

    private Player player;
    private Shot lastShot;
    private ArrayList<Shot> shots;
    
    private int deaths = 0;

    private boolean inGame = true;
    private String explImg = "src/images/explosion.png";
    private String message = "Game Over";

    private Timer timer;
    private int ticks;


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
        ticks = 0;

        gameInit();
    }


    private void gameInit() {

        lanes = new Lane[4];
        int spaceBetween = Commons.BOARD_WIDTH/4;
        int firstLane = spaceBetween/2;
        lanes[0] = new Lane(firstLane);
        lanes[1] = new Lane(firstLane + spaceBetween);
        lanes[2] = new Lane(firstLane + spaceBetween*2);
        lanes[3] = new Lane(firstLane + spaceBetween*3);

        for(int i = 0; i < lanes.length; i++){
            lanes[i].spawnAlien();
        }

        player = new Player();
        shots = new ArrayList<>();
        lastShot = new Shot();
    }

    private void drawAliens(Graphics g) {

        for (Lane lane: lanes){
            for (Alien alien : lane.getAliens()) {

                if (alien.isVisible()) {

                    g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
                }

                if (alien.isDying()) {

                    alien.die();
                }
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    private void drawShot(Shot shot, Graphics g) {

        if (shot.isVisible()) {

            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    private void drawBombing(Graphics g) {

        for (Lane lane: lanes) {
            for (Alien a : lane.getAliens()) {

                Alien.Bomb b = a.getBomb();

                if (!b.isDestroyed()) {

                    g.drawImage(b.getImage(), b.getX(), b.getY(), this);
                }
            }
        }
    }

    private void drawShots(Graphics g){
        for (Shot shot: shots){
            drawShot(shot, g);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.green);

        if (inGame) {

            g.drawLine(0, Commons.GROUND,
                    Commons.BOARD_WIDTH, Commons.GROUND);

            drawAliens(g);
            drawPlayer(g);
            drawShots(g);
            drawBombing(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

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

        if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {

            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        player.act();

        ticks += 1;

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

        for(Lane lane: lanes) {
            for (Alien alien : lane.getAliens()) {

                if (ticks == 5) {
                    alien.setY(alien.getY() + 1);
                    ticks = 0;
                }

            }
        }

        // bombs
        var generator = new Random();

        for(Lane lane: lanes) {
            for (Alien alien : lane.getAliens()) {

            int shot = generator.nextInt(15);
            Alien.Bomb bomb = alien.getBomb();

            if (shot == Commons.CHANCE && alien.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(alien.getX());
                bomb.setY(alien.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()) {

                if (bombX >= (playerX)
                        && bombX <= (playerX + Commons.PLAYER_WIDTH)
                        && bombY >= (playerY)
                        && bombY <= (playerY + Commons.PLAYER_HEIGHT)) {

                    var ii = new ImageIcon(explImg);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                }
            }

            if (!bomb.isDestroyed()) {

                bomb.setY(bomb.getY() + 1);

                if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT) {

                    bomb.setDestroyed(true);
                }
            }
        }
        }
    }

    private void doGameCycle() {

        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    private Alien getEnemyInLine(){
        var alienToReturn = aliens.get(0);
        for (Alien alien: aliens){
            if (alien.getY() > alienToReturn.getY()){
                alienToReturn = alien;
            }
        }
        return alienToReturn;
    }

    private void movePlayer(KeyEvent e){
        //TODO determine next lane, call player.setLane

    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {

            movePlayer(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key <= KeyEvent.VK_Z && key >= KeyEvent.VK_A) {

                if (inGame) {
                    var alien = getEnemyInLine();

                    if (alien.isNextLetter(key)) {
                        Shot shot = new Shot(x, y);
                        shots.add(shot);
                        lastShot = shot;
                    }
                }
            }
        }
    }
}

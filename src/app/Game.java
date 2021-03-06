package app;

import audio.Music;
import controllers.Controller;
import enums.GameState;
import enums.StateSound;
import image.loaders.BufferedImageLoader;
import image.loaders.ImageLoader;
import inputs.KeyInput;
import inputs.MouseInput;
import interfaces.BossEntity;
import interfaces.EnemyEntity;
import interfaces.FriendlyEntity;
import menu.Menu;
import models.Battleground;
import models.Fire;
import models.Player;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 400;

    private static final int MINIMAL_ENEMIES_KILLED_BOSS_ONE_TO_SPAWN = 10;
    private static final int MINIMAL_ENEMIES_KILLED_BOSS_TWO_TO_SPAWN = 40;
    private static final int MINIMAL_ENEMIES_KILLED_LEVEL_THREE_TO_SPAWN_BOSS = 80;

    private static final String TITLE = "Mnementh the game";
    private static final int SCALE = 2;
    private static final long serialVersionUID = 1L;

    public static GameState gameState = GameState.MENU;
    public static StateSound stateSound = StateSound.ON;

    private Thread thread;

    private BufferedImage image = ImageLoader.loadImage("/resources/gfx/Mnementh-Dragon.jpg");
    private BufferedImage imageDead = ImageLoader.loadImage("/resources/gfx/Dragonborn6.jpg");
    private BufferedImage imageHelp = ImageLoader.loadImage("/resources/gfx/Dragon3.jpg");
    private BufferedImage battlegroundImage = ImageLoader.loadImage("/resources/gfx/battleGround.png");
    private BufferedImage battlegroundImage2 = ImageLoader.loadImage("/resources/gfx/CaveLevel.jpg");
    private BufferedImage battlegroundImage3 = ImageLoader.loadImage("/resources/gfx/sky1.png");
    private BufferedImage spriteSheetGorgon = null;
    private BufferedImage spriteSheetDragon = null;
    private BufferedImage spriteSheetBoss = null;
    private BufferedImage spriteSheetBossShot = null;
    private BufferedImage spriteSheetHealth = null;

    private Battleground battleground;
    private Battleground battlegroundNextLevel;
    private Battleground battlegroundLevelThree;
    private Player player1;
    private Menu menu;
    private Controller controller;

    private boolean isBossActive = false;
    private boolean isBossSpawned = false;
    private boolean isShooting = false;
    private boolean running = false;
    private boolean setHighScore = false;
    private boolean isPaused;

    private List<EnemyEntity> enemyEntities;
    private List<BossEntity> bossEntities;

    private int countEnemy = 5;
    private int enemyKilled = 0;
    private String highScore = "";

    public Game() {
    }

    public int getCountEnemy() {
        return this.countEnemy;
    }

    public void setCountEnemy(int countEnemy) {
        this.countEnemy = countEnemy;
    }

    public int getEnemyKilled() {
        return this.enemyKilled;
    }

    public void setEnemyKilled(int enemyKilled) {
        this.enemyKilled = enemyKilled;
    }

    public BufferedImage getBattlegroundImage() {
        return this.battlegroundImage;
    }

    public BufferedImage getBattlegroundNextImage() {
        return this.battlegroundImage2;
    }

    public BufferedImage getBattlegroundImageThree() {
        return this.battlegroundImage3;
    }

    public BufferedImage getSpriteSheetGorgon() {
        return this.spriteSheetGorgon;
    }

    public BufferedImage getSpriteSheetDragon() {
        return this.spriteSheetDragon;
    }

    public BufferedImage getSpriteSheetBoss() {
        return this.spriteSheetBoss;
    }

    public BufferedImage getSpriteSheetBossShot() {
        return this.spriteSheetBossShot;
    }

    public BufferedImage getSpriteSheetHealth() {
        return this.spriteSheetHealth;
    }

    public List<EnemyEntity> getEnemyEntities() {
        return Collections.unmodifiableList(this.enemyEntities);
    }

    public List<BossEntity> getBossEntities() {
        return Collections.unmodifiableList(this.bossEntities);
    }

    public int getPlayer1Health() {
        return this.player1.getHealth();
    }

    public void setPlayer1Health(int health) {
        this.player1.setHealth(health);
    }

    public FriendlyEntity getPlayer1() {
        FriendlyEntity playerEntity = this.player1;
        return playerEntity;
    }

    public double getPlayer1X() {
        return this.player1.getX();
    }

    public double getPlayer1Y() {
        return this.player1.getY();
    }

    public boolean isBossActive() {
        return this.isBossActive;
    }

    public void setBossActive(boolean bossActive) {
        this.isBossActive = bossActive;
    }

    public boolean isBossSpawned() {
        return this.isBossSpawned;
    }

    public void setBossSpawned(boolean bossSpawned) {
        this.isBossSpawned = bossSpawned;
    }

    public static void main(String[] args) throws Exception {
        Game game = new Game();

        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

        JFrame frame = new JFrame(Game.TITLE);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Music.music();
        game.start();
        if (Game.gameState == GameState.END) {
            game.stop();
        }
    }

    // GAME LOOP
    public void run() {
        this.init();
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double nanoSeconds = 1000000000 / amountOfTicks;
        double delta = 0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();
        while (this.running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nanoSeconds;
            lastTime = now;
            if (delta >= 1) {
                if(!this.isPaused) {
                    this.tick();
                }
                updates++;
                delta--;
            }
            this.render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("Ticks " + updates + " Frames " + frames);
                updates = 0;
                frames = 0;
            }
        }
        this.stop();
    }

    //Movement
    public void keyPressed(KeyEvent k) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        int key = k.getKeyCode();

        int direction;
        int damage = this.player1.getDamage();
        if (key == KeyEvent.VK_RIGHT) {
            this.player1.setVelX(2);
            if (!this.player1.getDirection().equals("right")) {
                this.player1.setDirection("right");
            }

        } else if (key == KeyEvent.VK_LEFT) {
            this.player1.setVelX(-2);
            if (!this.player1.getDirection().equals("left")) {
                this.player1.setDirection("left");
            }

        } else if (key == KeyEvent.VK_DOWN) {
            this.player1.setVelY(2);

        } else if (key == KeyEvent.VK_UP) {
            this.player1.setVelY(-2);

        } else if (key == KeyEvent.VK_D && !this.isShooting && !this.isPaused) {
            this.isShooting = true;
            direction = 1;
            Music.dragonFire();
            this.controller.addEntity(
                    new Fire(this.player1.getX(), this.player1.getY(), direction, this, this.controller, damage));

        } else if (key == KeyEvent.VK_A && !this.isShooting && !this.isPaused) {
            this.isShooting = true;
            direction = 2;
            Music.dragonFire();
            this.controller.addEntity(
                    new Fire(this.player1.getX(), this.player1.getY(), direction, this, this.controller, damage));

        } else if (key == KeyEvent.VK_S && !this.isShooting && !this.isPaused) {
            this.isShooting = true;
            direction = 3;
            Music.dragonFire();
            this.controller.addEntity(
                    new Fire(this.player1.getX(), this.player1.getY(), direction, this, this.controller, damage));

        } else if (key == KeyEvent.VK_W && !this.isShooting && !this.isPaused) {
            this.isShooting = true;
            direction = 4;
            Music.dragonFire();
            this.controller.addEntity(
                    new Fire(this.player1.getX(), this.player1.getY(), direction, this, this.controller, damage));

        } else if (key == KeyEvent.VK_ESCAPE) {
            if (!this.isPaused) {
                this.isPaused = true;
            } else {
                this.isPaused = false;
            }
        } else if (key == KeyEvent.VK_M) {
            Game.gameState = GameState.MENU;
        }
    }

    public void keyReleased(KeyEvent k) {
        int key = k.getKeyCode();

        if (Game.gameState == GameState.GAME_LEVEL_ONE || Game.gameState == GameState.GAME_LEVEL_TWO || Game.gameState == GameState.GAME_LEVEL_THREE) {
            if (key == KeyEvent.VK_RIGHT) {
                this.player1.setVelX(0);
            } else if (key == KeyEvent.VK_LEFT) {
                this.player1.setVelX(0);
            } else if (key == KeyEvent.VK_DOWN) {
                this.player1.setVelY(0);
            } else if (key == KeyEvent.VK_UP) {
                this.player1.setVelY(0);
            } else if (key == KeyEvent.VK_D) {
                this.isShooting = false;
            } else if (key == KeyEvent.VK_A) {
                this.isShooting = false;
            } else if (key == KeyEvent.VK_S) {
                this.isShooting = false;
            } else if (key == KeyEvent.VK_W) {
                this.isShooting = false;
            }
        }
    }

    public void getScores(ArrayList<String> scores) {
        File scoreFile = new File("highscore.dat");
        if (!scoreFile.exists()) {
            try {
                scoreFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileReader readFile = new FileReader("highscore.dat");
             BufferedReader reader = new BufferedReader(readFile)){

            String line = reader.readLine();
            while (line != null) {
                scores.add(line);
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        BufferedImageLoader loader = new BufferedImageLoader();
        try {
            this.spriteSheetGorgon = loader.loadImage("/resources/gfx/fixed_gorgon_sheet.png");
            this.spriteSheetDragon = loader.loadImage("/resources/gfx/fixed_dragon_sheet.png");
            this.spriteSheetBoss = loader.loadImage("/resources/gfx/fixed_boss_sprite.png");
            this.spriteSheetBossShot = loader.loadImage("/resources/gfx/boss_fireball.png");
            this.spriteSheetHealth = loader.loadImage("/resources/gfx/health.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.addKeyListener(new KeyInput(this));
        this.addMouseListener(new MouseInput(this));

        this.controller = new Controller(this);
        this.battleground = new Battleground(this.battlegroundImage);
        this.battlegroundNextLevel = new Battleground(this.battlegroundImage2);
        this.battlegroundLevelThree = new Battleground(this.battlegroundImage3);
        this.player1 = new Player(200, 200, this, this.controller, 200, 15);

        this.menu = new Menu(this);

        this.enemyEntities = this.controller.getEnemy();
        this.bossEntities = this.controller.getBoss();
    }

    private synchronized void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    private synchronized void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    private void tick() {
        if (Game.gameState == GameState.GAME_LEVEL_ONE || Game.gameState == GameState.GAME_LEVEL_TWO || Game.gameState == GameState.GAME_LEVEL_THREE) {
            this.player1.tick();
            this.controller.tick();

            if(this.setHighScore) {
                this.setHighScore = false;
            }

            // Controlling if boss is created under some enemy killed number
            if(this.getEnemyKilled() < MINIMAL_ENEMIES_KILLED_BOSS_ONE_TO_SPAWN && Game.gameState == GameState.GAME_LEVEL_ONE) {
                if (this.controller.getEnemy().size() == 0 && Game.gameState == GameState.GAME_LEVEL_ONE) {
                    this.setCountEnemy(this.getCountEnemy() + 1);
                    this.controller.createEnemy(this.countEnemy);
                }
            } else if(this.getEnemyKilled() < MINIMAL_ENEMIES_KILLED_BOSS_TWO_TO_SPAWN && Game.gameState == GameState.GAME_LEVEL_TWO) {
                if (this.controller.getEnemy().size() == 0 && Game.gameState == GameState.GAME_LEVEL_TWO) {
                    this.setCountEnemy(this.getCountEnemy() + 1);
                    this.controller.createEnemy(this.countEnemy);
                }
            } else if(this.getEnemyKilled() < MINIMAL_ENEMIES_KILLED_LEVEL_THREE_TO_SPAWN_BOSS && Game.gameState == GameState.GAME_LEVEL_THREE) {
                if (this.controller.getEnemy().size() == 0 && Game.gameState == GameState.GAME_LEVEL_THREE) {
                    this.setCountEnemy(this.getCountEnemy() + 1);
                    this.controller.createEnemy(this.countEnemy);
                }
            }

            else if(this.controller.getEnemy().size() == 0) {
                this.setBossActive(true);
                this.controller.createEnemy(1);
            }
        }
    }

    //Rendering the game.
    private void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        if (this.highScore.equals("")) {
            this.highScore = this.getHighScore();
        }

        Graphics graphics = bs.getDrawGraphics();

        if (Game.gameState == GameState.GAME_LEVEL_ONE) {
            this.battleground.render(graphics);
            this.renderElements(graphics);
        } else if (Game.gameState == GameState.MENU) {
            graphics.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
            this.menu.render(graphics);
        } else if (Game.gameState == GameState.HELP) {
            graphics.drawImage(this.imageHelp, 0, 0, this.getWidth(), this.getHeight(), this);
            this.menu.render(graphics);
        } else if (Game.gameState == GameState.END) {
            this.setBossSpawned(false);
            this.setBossActive(false);
            if (this.enemyKilled > Integer.parseInt(this.highScore.split(":")[1]) && !this.setHighScore) {
                this.checkScore();
            }

            this.player1.setVelX(0);
            this.player1.setVelY(0);
            this.player1.setX(200);
            this.player1.setY(200);
            this.player1.setHealth(200);
            this.setCountEnemy(5);
            this.controller.clearEnemies();
            graphics.drawImage(this.imageDead, 0, 0, this.getWidth(), this.getHeight(), this);
            this.menu.render(graphics);

        } else if (Game.gameState == GameState.GAME_LEVEL_TWO) {
            this.battlegroundNextLevel.render(graphics);
            this.renderElements(graphics);

        } else if (Game.gameState == GameState.GAME_LEVEL_THREE) {
            this.battlegroundLevelThree.render(graphics);
            this.renderElements(graphics);

        }

        graphics.dispose();
        bs.show();
    }

    private String getHighScore() {
        FileReader readFile = null;
        BufferedReader reader = null;
        try {
            readFile = new FileReader("highscore.dat");
            reader = new BufferedReader(readFile);
            return reader.readLine();
        } catch (Exception e) {
            return "Nobody:0";
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void checkScore() {

        if (this.enemyKilled >= Integer.parseInt(this.highScore.split(":")[1])) {
            //user has set a new record

            String name = JOptionPane.showInputDialog("You set a new highscore. What is your name?");
            if(name == null || name.isEmpty()) {
                name = "No name";
            }
            this.highScore = name + ":" + this.enemyKilled;

            File scoreFile = new File("highscore.dat");
            if (!scoreFile.exists()) {
                try {
                    scoreFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> scores = new ArrayList<>();
            this.getScores(scores);
            FileWriter writeFile = null;
            BufferedWriter writer = null;
            try {
                writeFile = new FileWriter(scoreFile);
                writer = new BufferedWriter(writeFile);
                writer.write(this.highScore + "\n");
                for (String score : scores) {
                    writer.write(score + "\n");
                }
            } catch (Exception e) {
                //errors
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e) {}

            }

            this.setHighScore = true;
        }
    }

    private void renderElements(Graphics graphics) {
        this.player1.render(graphics);
        this.controller.render(graphics);

        //Score
        graphics.setColor(Color.white);
        Font fnt1 = new Font("arial", Font.BOLD, 30);
        graphics.setFont(fnt1);
        graphics.drawString("Kills:" + this.enemyKilled, 1150, 35);

        if(this.isPaused) {
            graphics.drawString("PAUSED", 600, 400);
        }

        //highscore
        graphics.setColor(Color.white);
        Font fnt2 = new Font("arial", Font.BOLD, 25);
        graphics.setFont(fnt2);
        graphics.drawString("Highscore: " + this.highScore, 1020, 65);

        //Player HP BAR
        graphics.setColor(Color.RED);
        graphics.fillRect(5, 5, 200, 50);

        graphics.setColor(Color.GREEN);
        graphics.fillRect(5, 5, this.player1.getHealth(), 50);

        graphics.setColor(Color.WHITE);
        graphics.drawRect(5, 5, 200, 50);

        graphics.setColor(Color.white);
        graphics.setFont(fnt1);
        graphics.drawString(this.player1.getHealth() / 2 + "%", 75, 42);
    }
}


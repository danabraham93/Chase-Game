import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.*;

public class ChaseGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int playerX, playerY, playerSize = 30;
    private int speed = 4;
    private boolean up, down, left, right;
    private ArrayList<Enemy> enemies;
    private int enemySize = 30;
    private long levelStartTime;
    private boolean gameRunning = false;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean showMenu = true;
    private boolean showCredits = false;
    private int level = 1;
    private int maxLevels = 3;
    private boolean waitingForNextLevel = false;

    // Power-ups
    private Rectangle speedBoost;
    private Rectangle enemyGrow;
    private boolean hasSpeedBoost = false;
    private boolean enemiesGrown = false;
    private long powerUpSpawnTime;
    private long powerUpDuration = 5000; // 5 seconds
    private long speedBoostTime;
    private long enemyGrowTime;

    private Random rand = new Random();

    private double enemySpeed;

    private static class Enemy {
        double x, y;
        int size;

        Enemy(double x, double y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        Rectangle getRect() {
            return new Rectangle((int)x, (int)y, size, size);
        }
    }

    public ChaseGame() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();
        playMusic("megaman_intro.wav"); // Put your WAV file path here
    }

    private void startGame() {
        showMenu = false;
        showCredits = false;
        gameRunning = true;
        level = 1;          // Start fresh at level 1
        resetGame();
    }

    private void resetGame() {
        playerX = getWidth() / 2;
        playerY = getHeight() / 2;
        speed = 4;
        enemySize = 30;
        hasSpeedBoost = false;
        enemiesGrown = false;
        speedBoost = null;
        enemyGrow = null;
        powerUpSpawnTime = System.currentTimeMillis();

        enemySpeed = 1.5 + (level - 1) * 0.7;

        waitingForNextLevel = false;
        gameOver = false;
        gameWon = false;

        spawnEnemies();
        levelStartTime = System.currentTimeMillis();
        gameRunning = true;
    }

    private void spawnEnemies() {
        enemies = new ArrayList<>();
        int enemyCount;
        switch (level) {
    case 1:
        enemyCount = 3;
        break;
    case 2:
        enemyCount = 5;
        break;
    case 3:
        enemyCount = 7;
        break;
    default:
        enemyCount = 3;
        break;
}


        int tries = 0;
        while (enemies.size() < enemyCount && tries < 1000) {
            int x = rand.nextInt(getWidth() - enemySize);
            int y = rand.nextInt(getHeight() - enemySize);
            Rectangle newEnemyRect = new Rectangle(x, y, enemySize, enemySize);
            Rectangle playerRect = new Rectangle(playerX, playerY, playerSize, playerSize);

            boolean overlapsPlayer = newEnemyRect.intersects(playerRect);
            boolean overlapsOthers = false;
            for (Enemy e : enemies) {
                if (newEnemyRect.intersects(e.getRect())) {
                    overlapsOthers = true;
                    break;
                }
            }
            if (!overlapsPlayer && !overlapsOthers) {
                enemies.add(new Enemy(x, y, enemySize));
            }
            tries++;
        }
    }

    private void spawnPowerUps() {
        if (speedBoost == null && enemyGrow == null && System.currentTimeMillis() - powerUpSpawnTime > 10000) {
            if (rand.nextBoolean()) {
                speedBoost = new Rectangle(rand.nextInt(getWidth() - 20), rand.nextInt(getHeight() - 20), 20, 20);
            }
            if (rand.nextBoolean()) {
                enemyGrow = new Rectangle(rand.nextInt(getWidth() - 20), rand.nextInt(getHeight() - 20), 20, 20);
            }
            powerUpSpawnTime = System.currentTimeMillis();
        }
    }

    private void playMusic(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("Error playing audio: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameRunning || showMenu || showCredits) return;

        if (up) playerY -= speed;
        if (down) playerY += speed;
        if (left) playerX -= speed;
        if (right) playerX += speed;

        playerX = Math.max(0, Math.min(playerX, getWidth() - playerSize));
        playerY = Math.max(0, Math.min(playerY, getHeight() - playerSize));

        Rectangle playerRect = new Rectangle(playerX, playerY, playerSize, playerSize);

        for (Enemy enemy : enemies) {
            if (enemy.x < playerX) enemy.x = Math.min(enemy.x + enemySpeed, getWidth() - enemy.size);
            if (enemy.x > playerX) enemy.x = Math.max(enemy.x - enemySpeed, 0);
            if (enemy.y < playerY) enemy.y = Math.min(enemy.y + enemySpeed, getHeight() - enemy.size);
            if (enemy.y > playerY) enemy.y = Math.max(enemy.y - enemySpeed, 0);

            if (enemy.getRect().intersects(playerRect)) {
                gameRunning = false;
                gameOver = true;
            }
        }

        if (speedBoost != null && playerRect.intersects(speedBoost)) {
            hasSpeedBoost = true;
            speed = 8;
            speedBoostTime = System.currentTimeMillis();
            speedBoost = null;
        }

        if (enemyGrow != null && playerRect.intersects(enemyGrow)) {
            enemiesGrown = true;
            enemySize = 90;
            ArrayList<Enemy> newEnemies = new ArrayList<>();
            for (Enemy r : enemies) {
                newEnemies.add(new Enemy(r.x, r.y, enemySize));
            }
            enemies = newEnemies;
            enemyGrowTime = System.currentTimeMillis();
            enemyGrow = null;
        }

        if (hasSpeedBoost && System.currentTimeMillis() - speedBoostTime > powerUpDuration) {
            hasSpeedBoost = false;
            speed = 4;
        }

        if (enemiesGrown && System.currentTimeMillis() - enemyGrowTime > powerUpDuration) {
            enemiesGrown = false;
            enemySize = 30;
            ArrayList<Enemy> newEnemies = new ArrayList<>();
            for (Enemy r : enemies) {
                newEnemies.add(new Enemy(r.x, r.y, enemySize));
            }
            enemies = newEnemies;
        }

        spawnPowerUps();

        if (System.currentTimeMillis() - levelStartTime > 30000) {
            gameRunning = false;
            if (level < maxLevels) {
                waitingForNextLevel = true;
                level++;
            } else {
                gameWon = true;
            }
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        if (showMenu) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String title = "CHASE GAME";
            int titleWidth = g.getFontMetrics().stringWidth(title);
            g.drawString(title, (getWidth() - titleWidth) / 2, 150);

            g.setFont(new Font("Arial", Font.PLAIN, 28));
            String start = "Press ENTER to Start";
            int startWidth = g.getFontMetrics().stringWidth(start);
            g.drawString(start, (getWidth() - startWidth) / 2, 250);

            String credits = "Press C for Credits";
            int creditsWidth = g.getFontMetrics().stringWidth(credits);
            g.drawString(credits, (getWidth() - creditsWidth) / 2, 300);
        } else if (showCredits) {
            g.setFont(new Font("Arial", Font.PLAIN, 28));
            String creditText1 = "Game by Danny Abraham";
            int w1 = g.getFontMetrics().stringWidth(creditText1);
            g.drawString(creditText1, (getWidth() - w1) / 2, 250);

            String creditText2 = "Press ENTER to return to menu";
            int w2 = g.getFontMetrics().stringWidth(creditText2);
            g.drawString(creditText2, (getWidth() - w2) / 2, 300);
        } else if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String lost = "You Lost! Press ENTER to Restart";
            int w = g.getFontMetrics().stringWidth(lost);
            g.drawString(lost, (getWidth() - w) / 2, getHeight() / 2);
        } else if (gameWon) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String won = "You Won the Game! Press ENTER to Return to Menu";
            int w = g.getFontMetrics().stringWidth(won);
            g.drawString(won, (getWidth() - w) / 2, getHeight() / 2);
        } else if (waitingForNextLevel) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String lvlComplete = "Level " + (level - 1) + " Complete! Press ENTER for Next Level";
            int w = g.getFontMetrics().stringWidth(lvlComplete);
            g.drawString(lvlComplete, (getWidth() - w) / 2, getHeight() / 2);
        } else {
            g.setColor(Color.RED);
            g.fillOval(playerX, playerY, playerSize, playerSize);

            g.setColor(Color.BLUE);
            for (Enemy enemy : enemies) {
                Rectangle r = enemy.getRect();
                g.fillRect(r.x, r.y, r.width, r.height);
            }

            g.setColor(Color.GREEN);
            if (speedBoost != null)
                g.fillPolygon(new int[] {speedBoost.x, speedBoost.x + 10, speedBoost.x + 20}, new int[] {speedBoost.y + 20, speedBoost.y, speedBoost.y + 20}, 3);

            g.setColor(Color.ORANGE);
            if (enemyGrow != null)
                g.fillRect(enemyGrow.x, enemyGrow.y, enemyGrow.width, enemyGrow.height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Level: " + level, 10, 20);
            int secondsLeft = Math.max(0, 30 - (int)((System.currentTimeMillis() - levelStartTime)/1000));
            g.drawString("Time Left: " + secondsLeft, 10, 40);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ENTER) {
            if (showMenu) {
                startGame();
            } else if (showCredits) {
                showCredits = false;
                showMenu = true;
                repaint();
            } else if (gameOver || gameWon) {
                level = 1;
                resetGame();
            } else if (waitingForNextLevel) {
                resetGame();
            }
        }

        if (key == KeyEvent.VK_C && showMenu) {
            showCredits = true;
            showMenu = false;
            gameRunning = false;  // Pause game updates during credits
            repaint();
        }

        if (!showMenu && !showCredits && gameRunning) {
            if (key == KeyEvent.VK_UP) up = true;
            if (key == KeyEvent.VK_DOWN) down = true;
            if (key == KeyEvent.VK_LEFT) left = true;
            if (key == KeyEvent.VK_RIGHT) right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) up = false;
        if (key == KeyEvent.VK_DOWN) down = false;
        if (key == KeyEvent.VK_LEFT) left = false;
        if (key == KeyEvent.VK_RIGHT) right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chase Game");
        ChaseGame game = new ChaseGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

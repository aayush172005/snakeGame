import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 100;

    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX, appleY;
    ArrayList<Point> walls = new ArrayList<>();

    char direction = 'R';
    boolean running = false;
    boolean paused = false;
    Timer timer;
    Random random;
    int highScore;
    boolean hardMode;
    int applePulse = 0;
    boolean appleGrow = true;

    public GamePanel(boolean hardMode) {
        this.hardMode = hardMode;
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        if (hardMode) generateWalls();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        highScore = ScoreManager.loadHighScore();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            if (paused) {
                g.setColor(Color.yellow);
                g.setFont(new Font("Ink Free", Font.BOLD, 50));
                g.drawString("PAUSED", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("PAUSED")) / 2, SCREEN_HEIGHT / 2);
                return;
            }

            int appleSize = UNIT_SIZE + applePulse;
            g.setColor(Color.red);
            g.fillOval(appleX + (UNIT_SIZE - appleSize) / 2, appleY + (UNIT_SIZE - appleSize) / 2, appleSize, appleSize);

            if (hardMode) {
                g.setColor(Color.gray);
                for (Point wall : walls) {
                    g.fillRect(wall.x, wall.y, UNIT_SIZE, UNIT_SIZE);
                }
            }

            for (int i = 0; i < bodyParts; i++) {
                g.setColor(i == 0 ? new Color(0, 255, 100) : new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 24));
            g.drawString("Score: " + applesEaten, 10, g.getFont().getSize());
            g.drawString("High Score: " + highScore, SCREEN_WIDTH - 200, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (hardMode) {
            for (Point wall : walls) {
                if (x[0] == wall.x && y[0] == wall.y) {
                    running = false;
                    break;
                }
            }
        }

        if (!running) {
            timer.stop();
            ScoreManager.saveHighScore(applesEaten);
        }
    }

    public void generateWalls() {
        walls.clear();
        for (int i = 0; i < SCREEN_WIDTH; i += UNIT_SIZE) {
            walls.add(new Point(i, UNIT_SIZE * 3));
        }
        for (int i = UNIT_SIZE * 5; i < SCREEN_HEIGHT; i += UNIT_SIZE * 4) {
            walls.add(new Point(SCREEN_WIDTH / 2, i));
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        g.drawString("Game Over", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        g.drawString("Press ENTER to Restart", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Press ENTER to Restart")) / 2, SCREEN_HEIGHT / 2 + 50);
    }

    public void restartGame() {
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        for (int i = 0; i < x.length; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        startGame();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkApple();
            checkCollisions();
            applePulse += appleGrow ? 1 : -1;
            if (applePulse > 5) appleGrow = false;
            if (applePulse < 0) appleGrow = true;
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                case KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                case KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                case KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
                case KeyEvent.VK_ENTER -> { if (!running) restartGame(); }
                case KeyEvent.VK_P -> paused = !paused;
            }
        }
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 7;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    Direction direction = Direction.RIGHT;
    boolean running = false;
    Timer timer;
    Random random;

    int tickCounter = 0;
    int moveEvery = 10;

    JButton retryButton;
    JButton backButton;
    String playerName = "Anonymous";
    String mode = "Classic";

    enum Direction { UP, DOWN, LEFT, RIGHT }

    public GamePanel(String playerName, String mode) {
        this.playerName = playerName;
        this.mode = mode;

        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);
        this.addKeyListener(new MyKeyAdapter());

        retryButton = new JButton("Play again");
        retryButton.setBounds(SCREEN_WIDTH / 2 - 75, SCREEN_HEIGHT / 2 + 40, 150, 40);
        retryButton.setFocusable(false);
        retryButton.addActionListener(e -> restartGame());
        retryButton.setVisible(false);
        this.add(retryButton);

        backButton = new JButton("Back to Menu");
        backButton.setBounds(SCREEN_WIDTH / 2 - 75, SCREEN_HEIGHT / 2 + 90, 150, 40);
        backButton.setFocusable(false);
        backButton.setVisible(false);
        backButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame frame) {
                frame.getContentPane().removeAll();
                frame.add(new GameMenu((name, modeSelected) -> {
                    GamePanel newGame = new GamePanel(name, modeSelected);
                    frame.getContentPane().removeAll();
                    frame.add(newGame);
                    newGame.requestFocusInWindow();
                    frame.revalidate();
                    frame.repaint();
                }));
                frame.revalidate();
                frame.repaint();
            }
        });
        this.add(backButton);

        startGame();
    }

    public void startGame() {
        retryButton.setVisible(false);
        backButton.setVisible(false);
        if (timer != null) {
            timer.stop();
        }
        resetGameState();
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void resetGameState() {
        bodyParts = 6;
        applesEaten = 0;
        direction = Direction.RIGHT;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 100 - i * UNIT_SIZE;
            y[i] = 100;
        }
    }

    public void restartGame() {
        tickCounter = 0;
        startGame();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 24));
            g.drawString("Score: " + applesEaten, 10, 30);

        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case UP -> y[0] -= UNIT_SIZE;
            case DOWN -> y[0] += UNIT_SIZE;
            case LEFT -> x[0] -= UNIT_SIZE;
            case RIGHT -> x[0] += UNIT_SIZE;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            saveScore();
            retryButton.setVisible(true);
            backButton.setVisible(true);
        }
    }

    public void saveScore() {
        JSONObject playerData = new JSONObject();
        playerData.put("name", playerName);
        playerData.put("score", applesEaten);
        playerData.put("mode", mode);

        try (FileWriter file = new FileWriter("scores.json", true)) {
            file.write(playerData.toJSONString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        String message = "Game Over";
        FontMetrics metrics = getFontMetrics(g.getFont());
        int textWidth = metrics.stringWidth(message);
        g.drawString(message, (SCREEN_WIDTH - textWidth) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            tickCounter++;
            if (tickCounter % moveEvery == 0) {
                move();
                checkApple();
                checkCollisions();
            }
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != Direction.RIGHT) direction = Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != Direction.LEFT) direction = Direction.RIGHT;
                    break;
                case KeyEvent.VK_UP:
                    if (direction != Direction.DOWN) direction = Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != Direction.UP) direction = Direction.DOWN;
                    break;
            }
        }
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    private enum GameState { MENU, PLAYING, PAUSED }
    private GameState gameState = GameState.MENU;

    private Thread gameThread;
    private boolean running;
    private final int FPS = 60;

    private Player player;
    private List<Enemy> currentEnemies = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();

    private boolean[] keys = new boolean[256];
    private Point mousePosition = new Point();

    private Room currentRoom;
    private final int ROOM_WIDTH = 1200;
    private final int ROOM_HEIGHT = 600;

    private Font gameFont = new Font("Arial", Font.BOLD, 24);
    private Font hudFont = new Font("Arial", Font.BOLD, 16);

    private int cameraX, cameraY;

    private void updateCamera() {
        // Centrare cameră pe jucător, cu limitări pentru margini
        int playerCenterX = player.getX() + 16;
        int playerCenterY = player.getY() + 16;
        cameraX = playerCenterX - getWidth() / 2;
        cameraY = playerCenterY - getHeight() / 2;

        // Limitează camera să nu iasă din cameră
        cameraX = Math.max(0, Math.min(cameraX, ROOM_WIDTH - getWidth()));
        cameraY = Math.max(0, Math.min(cameraY, ROOM_HEIGHT - getHeight()));
    }

    public GamePanel() {
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.BLACK);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);

        createInitialRoom();
        player = new Player(currentRoom.getPlayerSpawn().x, currentRoom.getPlayerSpawn().y);
    }

    private void createInitialRoom() {
        currentRoom = new Room(ROOM_WIDTH, ROOM_HEIGHT, new Color(60, 60, 60), new Point(100, ROOM_HEIGHT - 100));
        currentRoom.addEnemy(new Goblin(300, 300));
        currentRoom.addEnemy(new Goblin(400, 400));
        currentRoom.addEnemy(new Orc(500, 300));
        currentEnemies = currentRoom.getEnemies();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        running = true;
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        switch(gameState) {
            case MENU: updateMenu(); break;
            case PLAYING: updateGame(); break;
            case PAUSED: updatePaused(); break;
        }
    }

    private void updateMenu() {
        // Logică pentru meniu
    }

    private void updatePaused() {
        // Logică pentru pauză
    }

    private void updateGame() {
        player.update(keys, mousePosition);

        for(int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.update();

            if(p.isOutOfBounds(ROOM_WIDTH, ROOM_HEIGHT)) {
                projectiles.remove(i);
                i--;
            }
        }

        for(int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            e.update(player);

            if(e.isDead()) {
                currentEnemies.remove(i);
                i--;
            }
        }

        checkCollisions();
    }

    private void checkCollisions() {
        for(int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            for(Enemy e : currentEnemies) {
                if(p.getBounds().intersects(e.getBounds())) {
                    e.takeDamage(p.getDamage());
                    projectiles.remove(i);
                    i--;
                    break;
                }
            }
        }

        for(Enemy e : currentEnemies) {
            if(player.getBounds().intersects(e.getBounds())) {
                player.takeDamage(e.getDamage());
            }
        }

        player.checkSwordCollisions(currentEnemies);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch(gameState) {
            case MENU: drawMenu(g2); break;
            case PLAYING: drawGame(g2); break;
            case PAUSED: drawPaused(g2); break;
        }
    }

    private void drawMenu(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(gameFont);
        g2.drawString("DRAGON SLAYER", 500, 200);
        g2.drawString("1. New Game", 550, 300);
        g2.drawString("2. Load Game", 550, 350);
        g2.drawString("3. Leaderboard", 550, 400);
        for(int i = 0; i < 3; i++) {
            if(menuOptionHovered == i) {
                g2.setColor(Color.YELLOW);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString(getMenuOptionText(i), 550, 300 + i*50);
        }

    }

    private String getMenuOptionText(int index) {
        switch(index) {
            case 0: return "1. New Game";
            case 1: return "2. Load Game";
            case 2: return "3. Leaderboard";
            default: return "";
        }
    }

    private void drawPaused(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(gameFont);
        g2.drawString("PAUSED", 600, 300);
        g2.drawString("Resume (ESC)", 550, 350);
        g2.drawString("Save", 550, 400);
        g2.drawString("Exit", 550, 450);
    }

    private void drawGame(Graphics2D g2) {
        updateCamera();
        g2.translate(-cameraX, -cameraY);

        // Transmitem cameraX și cameraY la metodele draw
        currentRoom.draw(g2, cameraX, cameraY);
        for(Enemy e : currentEnemies) e.draw(g2, cameraX, cameraY);
        for(Projectile p : projectiles) p.draw(g2, cameraX, cameraY);
        player.draw(g2, cameraX, cameraY);
        player.drawSwordAttacks(g2, cameraX, cameraY);

        g2.translate(cameraX, cameraY);
        drawHUD(g2);
    }
    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(hudFont);
        g2.drawString("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 20, 30);
        g2.drawString("Arrows: " + player.getArrows(), 20, 60);
        g2.drawString("Room: " + currentRoom.getName(), 20, 90);
    }

    private void togglePause() {
        gameState = (gameState == GameState.PLAYING) ? GameState.PAUSED : GameState.PLAYING;
    }

    private void resetRoom() {
        player.setPosition(currentRoom.getPlayerSpawn().x, currentRoom.getPlayerSpawn().y);
        currentEnemies = new ArrayList<>(currentRoom.getEnemies());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code < keys.length) keys[code] = true;

        if(code == KeyEvent.VK_ESCAPE) togglePause();
        if(code == KeyEvent.VK_R) resetRoom();

        if(gameState == GameState.MENU && code == KeyEvent.VK_1) {
            gameState = GameState.PLAYING;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code < keys.length) keys[code] = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(gameState == GameState.PLAYING) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                player.swingSword(mousePosition);
            }
            else if(e.getButton() == MouseEvent.BUTTON3 && player.getArrows() > 0) {
                projectiles.add(player.shootBow(mousePosition));
            }
        }
    }

    private int menuOptionHovered = -1;

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {
        if(gameState == GameState.MENU) {
            if(menuOptionHovered == 0) gameState = GameState.PLAYING;
            // Adaugă acțiuni pentru alte opțiuni (1, 2, etc.)
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
        if(gameState == GameState.MENU) {
            int yPos = e.getY();
            menuOptionHovered = -1;
            if(yPos >= 280 && yPos <= 310) menuOptionHovered = 0; // New Game
            if(yPos >= 330 && yPos <= 360) menuOptionHovered = 1; // Load Game
            // Adaugă verificări pentru alte opțiuni
        }
    }
    @Override public void mouseDragged(MouseEvent e) { mousePosition = e.getPoint(); }
}
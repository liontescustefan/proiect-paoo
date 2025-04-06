import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.io.IOException;
import java.util.Objects;

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int TILE_WIDTH = 224;
    private final int TILE_HEIGHT = 254;
    private final int WALL_HEIGHT = 0;
    private final int BEAN_SIZE = 64;

    private float worldX = 1.0f;
    private float worldY = 1.0f;
    private float velX = 0;
    private float velY = 0;
    private final float ACCEL = 5.0f;
    private final float MAX_SPEED = 10.0f;
    private final float FRICTION = 0.1f;

    private boolean[] keys = new boolean[256];
    private int[][] room = new int[10][10];

    // Sprite sheet variables
    private BufferedImage beanSpriteSheet;
    private int currentDirection = 2;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private final int FRAME_DELAY = 100;
    private final int SPRITE_COLS = 6;
    private final int SPRITE_ROWS = 8;

    // Image assets
    private BufferedImage wallTop;
    private BufferedImage wallFront;
    private BufferedImage floorTile;

    // Depth sorting interface
    private interface Renderable {
        double getDepth();
        void draw(Graphics2D g2d, int centerX, int centerY);
    }

    public GamePanel() {
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);
        addKeyListener(this);
        initializeRoom();
        loadImages();
        new Timer(16, this).start();
    }

    private void initializeRoom() {
        for (int[] row : room) Arrays.fill(row, 0);
        for (int i = 0; i < room.length; i++) {
            room[i][0] = 1;
            room[i][room[0].length-1] = 1;
            room[0][i] = 1;
            room[room.length-1][i] = 1;
        }
        room[4][4]=1;
    }

    private void loadImages() {
        try {
            wallTop = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("Tree.png")));
            wallFront = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("Tree.png")));
            floorTile = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("Tilemap_Flat.png")));
            beanSpriteSheet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("Warrior_Purple.png")));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Images not found, using placeholders");
            wallTop = createColoredImage(TILE_WIDTH, TILE_HEIGHT, new Color(80, 80, 100));
            wallFront = createColoredImage(TILE_WIDTH, WALL_HEIGHT, new Color(60, 60, 80));
            floorTile = createColoredImage(TILE_WIDTH, TILE_HEIGHT, new Color(50, 50, 70));
            beanSpriteSheet = createColoredImage(BEAN_SIZE * SPRITE_COLS, BEAN_SIZE * SPRITE_ROWS, new Color(160, 220, 120));
        }
    }

    private BufferedImage createColoredImage(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        drawFloorTiles(g2d, centerX, centerY);

        List<Renderable> renderables = new ArrayList<>();

        // Add walls with screen-space depth
        for (int x = -5; x < room.length + 5; x++) {
            for (int y = -5; y < room[0].length + 5; y++) {
                if (x < 0 || x >= room.length || y < 0 || y >= room[0].length || room[x][y] == 1) {
                    final int wx = x;
                    final int wy = y;
                    renderables.add(new Renderable() {
                        @Override
                        public double getDepth() {
                            // Calculate actual screen Y position for depth
                            return centerY + (wx - worldX + wy - worldY) * (TILE_HEIGHT/2);
                        }

                        @Override
                        public void draw(Graphics2D g2d, int centerX, int centerY) {
                            int isoX = centerX + (int)((wx - worldX - (wy - worldY)) * (TILE_WIDTH/2));
                            int isoY = centerY + (int)((wx - worldX + (wy - worldY)) * (TILE_HEIGHT/2));
                            drawWall(g2d, isoX, isoY);
                        }
                    });
                }
            }
        }

        // Add player with screen-space depth
        renderables.add(new Renderable() {
            @Override
            public double getDepth() {
                return centerY; // Player is drawn at screen center
            }

            @Override
            public void draw(Graphics2D g2d, int centerX, int centerY) {
                drawBeanCharacter(g2d, centerX, centerY);
            }
        });

        // Sort by actual screen Y position
        renderables.sort(Comparator.comparingDouble(Renderable::getDepth));

        // Draw all objects
        for (Renderable r : renderables) {
            r.draw(g2d, centerX, centerY);
        }
    }

    private void drawFloorTiles(Graphics2D g2d, int centerX, int centerY) {
        for (int x = -5; x < room.length + 5; x++) {
            for (int y = -5; y < room[0].length + 5; y++) {
                int isoX = centerX + (int)((x - worldX - (y - worldY)) * (TILE_WIDTH/2));
                int isoY = centerY + (int)((x - worldX + (y - worldY)) * (TILE_HEIGHT/2));
                g2d.drawImage(floorTile, isoX - TILE_WIDTH/2, isoY - TILE_HEIGHT/2, TILE_WIDTH, TILE_HEIGHT, null);
            }
        }
    }

    private void drawWall(Graphics2D g, int isoX, int isoY) {
        // Draw the top part of the wall with adjusted Y position to move it down
        g.drawImage(wallTop, isoX - TILE_WIDTH/2, isoY - TILE_HEIGHT/10, TILE_WIDTH, TILE_HEIGHT, null);
        // Draw the front part of the wall with adjusted Y position to align with the bottom of the top part
        g.drawImage(wallFront, isoX - TILE_WIDTH/2, isoY + TILE_HEIGHT/10, TILE_WIDTH, WALL_HEIGHT, null);
    }

    private void drawBeanCharacter(Graphics2D g2d, int centerX, int centerY) {
        int spriteWidth = beanSpriteSheet.getWidth() / SPRITE_COLS;
        int spriteHeight = beanSpriteSheet.getHeight() / SPRITE_ROWS;
        BufferedImage frame = beanSpriteSheet.getSubimage(
                currentFrame * spriteWidth,
                currentDirection * spriteHeight,
                spriteWidth,
                spriteHeight
        );
        g2d.drawImage(frame, centerX - spriteWidth/2, centerY - spriteHeight/2, null);
    }

    private void updateMovement() {
        float targetX = 0, targetY = 0;

        if (keys[KeyEvent.VK_W]) { targetY -= 1; targetX -= 1; }
        if (keys[KeyEvent.VK_S]) { targetY += 1; targetX += 1; }
        if (keys[KeyEvent.VK_A]) { targetX -= 1; targetY += 1; }
        if (keys[KeyEvent.VK_D]) { targetX += 1; targetY -= 1; }

        if (targetX != 0 || targetY != 0) {
            float len = (float) Math.sqrt(targetX * targetX + targetY * targetY);
            targetX /= len;
            targetY /= len;
        }

        velX += targetX * ACCEL;
        velY += targetY * ACCEL;

        float speed = (float) Math.sqrt(velX * velX + velY * velY);
        if (speed > MAX_SPEED) {
            velX = velX / speed * MAX_SPEED;
            velY = velY / speed * MAX_SPEED;
        }

        velX *= FRICTION;
        velY *= FRICTION;

        // Update direction
        if (velX != 0 || velY != 0) {
            float angle = (float) Math.toDegrees(Math.atan2(velY, velX));
            if (angle < 0) angle += 360;

            if (angle >= 45 && angle < 135) currentDirection = 0;
            else if (angle >= 135 && angle < 225) currentDirection = 3;
            else if (angle >= 225 && angle < 315) currentDirection = 2;
            else currentDirection = 1;
        }

        // Collision check
        float newX = worldX + velX * 0.1f;
        float newY = worldY + velY * 0.1f;
        if (!isCollision(newX, newY)) {
            worldX = newX;
            worldY = newY;
        } else {
            velX *= -0.5f;
            velY *= -0.5f;
        }
    }

    private boolean isCollision(float x, float y) {
        int gridX = (int) x;
        int gridY = (int) y;
        return gridX < 0 || gridX >= room.length || gridY < 0 || gridY >= room[0].length || room[gridX][gridY] == 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            currentFrame = (velX != 0 || velY != 0) ? (currentFrame + 1) % SPRITE_COLS : 0;
            lastFrameTime = currentTime;
        }
        updateMovement();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) { keys[e.getKeyCode()] = true; }
    @Override
    public void keyReleased(KeyEvent e) { keys[e.getKeyCode()] = false; }
    @Override
    public void keyTyped(KeyEvent e) {}
}
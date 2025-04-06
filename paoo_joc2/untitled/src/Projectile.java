import java.awt.*;

public class Projectile {
    private double x, y;
    private double dx, dy;
    private int damage;
    private boolean isPlayerProjectile;
    private static final int SIZE = 8;
    private static final int SPEED = 10;

    public Projectile(double x, double y, double angle, int damage, boolean isPlayerProjectile) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.isPlayerProjectile = isPlayerProjectile;
        this.dx = Math.cos(angle) * SPEED;
        this.dy = Math.sin(angle) * SPEED;
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public boolean isOutOfBounds(int roomWidth, int roomHeight) {
        return x < 0 || x > roomWidth || y < 0 || y > roomHeight;
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) { // Adăugăm parametrii
        g2.setColor(isPlayerProjectile ? Color.YELLOW : Color.RED);
        g2.fillOval((int)x - SIZE/2 - cameraX, (int)y - SIZE/2 - cameraY, SIZE, SIZE);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x - SIZE/2, (int)y - SIZE/2, SIZE, SIZE);
    }

    public int getDamage() { return damage; }
}
import java.awt.*;

public abstract class Enemy {
    protected int x, y;
    protected int health;
    protected int maxHealth;
    protected int damage;
    protected int speed;

    public abstract void update(Player player);
    public abstract void draw(Graphics2D g2, int cameraX, int cameraY);

    public void takeDamage(int amount) {
        health -= amount;
    }

    protected void drawHealthBar(Graphics2D g2, int cameraX, int cameraY) {
        g2.setColor(Color.RED);
        g2.fillRect(x - cameraX, y - cameraY - 10, 32, 5);
        g2.setColor(Color.GREEN);
        g2.fillRect(x - cameraX, y - cameraY - 10, (int)(32 * ((double)health/maxHealth)), 5);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    public int getDamage() { return damage; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
import java.awt.*;

public class Orc extends Enemy {
    public Orc(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = 12;
        this.maxHealth = 12;
        this.damage = 3;
        this.speed = 1;
    }

    @Override
    public void update(Player player) {
        if(Math.random() < 0.02) {
            if(x < player.getX()) x += speed;
            if(x > player.getX()) x -= speed;
            if(y < player.getY()) y += speed;
            if(y > player.getY()) y -= speed;
        }
    }

    @Override
    public void draw(Graphics2D g2, int cameraX, int cameraY) { // Adăugăm parametrii
        g2.setColor(new Color(0, 100, 0));
        g2.fillRect(x - cameraX, y - cameraY, 32, 32);
        drawHealthBar(g2, cameraX, cameraY);
    }

    protected void drawHealthBar(Graphics2D g2, int cameraX, int cameraY) {
        g2.setColor(Color.RED);
        g2.fillRect(x - cameraX, y - cameraY - 10, 32, 5);
        g2.setColor(Color.GREEN);
        g2.fillRect(x - cameraX, y - cameraY - 10, (int)(32 * ((double)health/maxHealth)), 5);
    }
}
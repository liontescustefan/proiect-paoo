import java.awt.*;

public class Goblin extends Enemy {
    public Goblin(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = 6;
        this.maxHealth = 6;
        this.damage = 2;
        this.speed = 2;
    }

    @Override
    public void update(Player player) {
        if(x < player.getX()) x += speed;
        if(x > player.getX()) x -= speed;
        if(y < player.getY()) y += speed;
        if(y > player.getY()) y -= speed;
    }

    @Override
    public void draw(Graphics2D g2, int cameraX, int cameraY) { // Adăugăm parametrii
        g2.setColor(Color.GREEN);
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
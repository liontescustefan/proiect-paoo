import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name = "Forest Room";
    private int width, height;
    private Color floorColor;
    private List<Enemy> enemies;
    private Point playerSpawn;

    public Room(int width, int height, Color floorColor, Point spawnPoint) {
        this.width = width;
        this.height = height;
        this.floorColor = floorColor;
        this.playerSpawn = spawnPoint;
        this.enemies = new ArrayList<>();
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    public Point getPlayerSpawn() {
        return playerSpawn;
    }

    public String getName() {
        return name;
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) { // Adăugăm parametrii
        g2.setColor(floorColor);
        g2.fillRect(-cameraX, -cameraY, width, height);

        // Margini
        g2.setColor(new Color(100, 100, 100));
        g2.fillRect(-cameraX, -cameraY, width, 20);
        g2.fillRect(-cameraX, height-20 - cameraY, width, 20);
        g2.fillRect(-cameraX, -cameraY, 20, height);
        g2.fillRect(width-20 - cameraX, -cameraY, 20, height);
    }
}
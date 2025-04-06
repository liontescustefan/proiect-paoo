import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Area;

public class Player {
    private int x, y;
    private int health = 20;
    private int maxHealth = 20;
    private int swordDamage = 2;
    private int bowDamage = 1;
    private int arrows = 5;
    private int speed = 5;
    private boolean isRolling = false;
    private long lastRollTime = 0;
    private final long ROLL_COOLDOWN = 2000;

    private List<SwordAttack> swordAttacks = new ArrayList<>();
    private long lastSwordAttack = 0;
    private final int SWORD_COOLDOWN = 500;
    private final int SWORD_RANGE = 80;
    private final int SWORD_ARC = 90;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(boolean[] keys, Point mousePos) {
        if(!isRolling) {
            if(keys[KeyEvent.VK_W]) y -= speed;
            if(keys[KeyEvent.VK_S]) y += speed;
            if(keys[KeyEvent.VK_A]) x -= speed;
            if(keys[KeyEvent.VK_D]) x += speed;

            if(keys[KeyEvent.VK_SPACE] && System.currentTimeMillis() - lastRollTime > ROLL_COOLDOWN) {
                startRoll();
            }
        } else {
            updateRoll();
        }

        for(int i = 0; i < swordAttacks.size(); i++) {
            SwordAttack attack = swordAttacks.get(i);
            attack.update();
            if(attack.isFinished()) {
                swordAttacks.remove(i);
                i--;
            }
        }

        x = Math.max(20, Math.min(1200 - 52, x));
        y = Math.max(20, Math.min(600 - 52, y));
    }

    private void startRoll() {
        isRolling = true;
        lastRollTime = System.currentTimeMillis();
    }

    private void updateRoll() {
        if(System.currentTimeMillis() - lastRollTime < 500) {
            y -= speed * 2;
            y += speed * 2;
            x -= speed * 2;
            x += speed * 2;
        } else {
            isRolling = false;
        }
    }

    public void swingSword(Point mousePos) {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastSwordAttack > SWORD_COOLDOWN) {
            swordAttacks.add(new SwordAttack(x + 16, y + 16, mousePos));
            lastSwordAttack = currentTime;
        }
    }

    public void checkSwordCollisions(List<Enemy> enemies) {
        for(SwordAttack attack : swordAttacks) {
            attack.checkCollisions(enemies, swordDamage);
        }
    }

    public Projectile shootBow(Point target) {
        arrows--;
        double angle = Math.atan2(target.y - (y + 16), target.x - (x + 16));
        return new Projectile(x + 16, y + 16, angle, bowDamage, true);
    }

    public void takeDamage(int amount) {
        if(!isRolling) {
            health = Math.max(0, health - amount);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) { // Adăugăm parametrii
        if(isRolling) g2.setColor(new Color(100, 100, 255));
        else g2.setColor(Color.BLUE);
        g2.fillRect(x - cameraX, y - cameraY, 32, 32);

        // Health bar
        g2.setColor(Color.RED);
        g2.fillRect(x - cameraX, y - cameraY - 10, 32, 5);
        g2.setColor(Color.GREEN);
        g2.fillRect(x - cameraX, y - cameraY - 10, (int)(32 * ((double)health/maxHealth)), 5);
    }

    public void drawSwordAttacks(Graphics2D g2, int cameraX, int cameraY) { // Adăugăm parametrii
        for(SwordAttack attack : swordAttacks) {
            attack.draw(g2, cameraX, cameraY);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getArrows() { return arrows; }

    public int getX() {
        return  x;
    }

    public int getY() {
        return y;
    }

    public class SwordAttack {
        private final double x, y;
        private final double angle;
        private final long startTime;
        private boolean hasHit = false;

        public SwordAttack(double x, double y, Point target) {
            this.x = x; // Inițializare corectă cu this.x
            this.y = y; // Inițializare corectă cu this.y
            this.angle = Math.atan2(target.y - y, target.x - x); // Inițializare angle
            this.startTime = System.currentTimeMillis(); // Inițializare startTime
        }

        // Metoda draw corectă cu 3 parametri
        public void draw(Graphics2D g2, int cameraX, int cameraY) {
            if (System.currentTimeMillis() - startTime < 200) {
                g2.setColor(new Color(255, 255, 0, 100));
                Arc2D arc = new Arc2D.Double(
                        this.x - cameraX - SWORD_RANGE / 2,
                        this.y - cameraY - SWORD_RANGE / 2,
                        SWORD_RANGE,
                        SWORD_RANGE,
                        Math.toDegrees(angle) - SWORD_ARC / 2,
                        SWORD_ARC,
                        Arc2D.PIE
                );
                g2.fill(arc);
            }
        }

        public void update() {}

        public void checkCollisions(List<Enemy> enemies, int damage) {
            if(!hasHit && System.currentTimeMillis() - startTime < 100) {
                Area attackArea = getAttackArea();

                for(Enemy enemy : enemies) {
                    Rectangle enemyBounds = enemy.getBounds();
                    if(attackArea.intersects(enemyBounds)) {
                        enemy.takeDamage(damage);
                        applyKnockback(enemy);
                    }
                }
                hasHit = true;
            }
        }

        private Area getAttackArea() {
            Arc2D arc = new Arc2D.Double(
                    x - SWORD_RANGE/2,
                    y - SWORD_RANGE/2,
                    SWORD_RANGE,
                    SWORD_RANGE,
                    Math.toDegrees(angle) - SWORD_ARC/2,
                    SWORD_ARC,
                    Arc2D.PIE
            );
            return new Area(arc);
        }

        private void applyKnockback(Enemy enemy) {
            double knockbackAngle = Math.atan2(enemy.getY() - y, enemy.getX() - x);
            double knockbackPower = 15;
            enemy.setPosition(
                    (int)(enemy.getX() + Math.cos(knockbackAngle) * knockbackPower),
                    (int)(enemy.getY() + Math.sin(knockbackAngle) * knockbackPower)
            );
        }

        public void draw(Graphics2D g2) {
            if(System.currentTimeMillis() - startTime < 200) {
                g2.setColor(new Color(255, 255, 0, 100));
                Arc2D arc = new Arc2D.Double(
                        x - SWORD_RANGE/2,
                        y - SWORD_RANGE/2,
                        SWORD_RANGE,
                        SWORD_RANGE,
                        Math.toDegrees(angle) - SWORD_ARC/2,
                        SWORD_ARC,
                        Arc2D.PIE
                );
                g2.fill(arc);
            }
        }

        public boolean isFinished() {
            return System.currentTimeMillis() - startTime > 200;
        }
    }
}
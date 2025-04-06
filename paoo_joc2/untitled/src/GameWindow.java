import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameWindow extends JFrame {
    private GamePanel gamePanel = new GamePanel() {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }
    };

    public GameWindow() {
        setTitle("Dragon Slayer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
    }

    public void start() {
        setVisible(true);
        gamePanel.startGameThread();
    }
}
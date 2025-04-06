import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel {
    private JButton newGameButton;
    private JButton exitButton;
    private GamePanel gamePanel;
    private JFrame frame;
    private boolean gameStarted = false;
    private Image backgroundImage;

    public MenuPanel(JFrame frame) {
        this.frame = frame;

        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("hq720.jpg"))); // Schimbă cu calea imaginii tale
        } catch (IOException e) {
            e.printStackTrace();
            // Dacă imaginea nu poate fi încărcată, folosește un fundal negru
            backgroundImage = null;
        }

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1280, 720));
        
        // Create and style the New Game button
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 24));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setBackground(new Color(60, 60, 60));
        newGameButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        newGameButton.setFocusPainted(false);

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(60, 60, 60));
        exitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        exitButton.setFocusPainted(false);

        
        // Add hover effect
        newGameButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                newGameButton.setBackground(new Color(80, 80, 80));
                newGameButton.repaint();
            }
            public void mouseExited(MouseEvent e) {
                newGameButton.setBackground(new Color(60, 60, 60));
                newGameButton.repaint();
            }
        });

        // Add hover effect for Exit button
        exitButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(new Color(80, 80, 80));
                exitButton.repaint();
            }
            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(new Color(60, 60, 60));
                exitButton.repaint();
            }
        });
        // Add click action
        newGameButton.addActionListener(e -> startGame());
        exitButton.addActionListener(e->exitGame());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0); // Add spacing

        // Add New Game button
        gbc.gridy = 0;
        add(newGameButton, gbc);

        // Add Exit button below with spacing
        gbc.gridy = 1;
        add(exitButton, gbc);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenează imaginea de fundal
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback dacă imaginea nu este încărcată
            g.setColor(new Color(30, 30, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            frame.remove(this);
            gamePanel = new GamePanel();
            frame.add(gamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            gamePanel.requestFocus();
        }
    }
    private void exitGame(){
        if(!gameStarted) {
            System.exit(0);
        }
    }
} 
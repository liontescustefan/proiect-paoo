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
    private Image imagine;

    //se creeaza meniul
    public MenuPanel(JFrame frame) {
        this.frame = frame;

        //se incarca imaginea de fundal pentru meniu
        try {
            imagine = ImageIO.read(Objects.requireNonNull(getClass().getResource("hq720.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
            // daca nu poate fi incarcata imaginea se foloseste un fundal negru
            imagine = null;
        }

        //dimensiune meniu
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1280, 720));

        // creeaza butonul new game
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 24));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setBackground(new Color(60, 60, 60));
        newGameButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        newGameButton.setFocusPainted(false);


        // creeaza butonul exit
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(60, 60, 60));
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        exitButton.setFocusPainted(false);


        // efectul pentru cand se da hover peste buton
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

        // actiune pentru click
        newGameButton.addActionListener(e -> startGame());
        exitButton.addActionListener(e->exitGame());

        //spatiu intre butoane
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        //afisarea butoanelor
        gbc.gridy = 0;
        add(newGameButton, gbc);

        gbc.gridy = 1;
        add(exitButton, gbc);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // deseneaza imaginea de fundal
        if (imagine != null) {
            g.drawImage(imagine, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(30, 30, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    //actiune pentru new game
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

    //actiune pentru exit
    private void exitGame(){
        if(!gameStarted) {
            System.exit(0);
        }
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel {
    private final JButton newGameButton;
    private final JButton exitButton;
    private final JFrame frame;
    private boolean gameStarted = false;
    private Image imagine;

    Color buttonColor = new Color(200, 40, 40);
    Color hoverColor = new Color(220, 80, 80);
    Color textColor = new Color(255, 215, 0);
    Color shadowColor = new Color(0, 0, 0, 150);

    Font font = new Font("georgia", Font.ITALIC, 36);

    //creeare universala butoane
    //https://stackoverflow.com/questions/56961921/creating-a-jbutton-with-the-click-of-a-jbutton

    private void creeareButon(JButton button, Font font, Color buttonColor, Color textColor, Dimension dim) {
        button.setFont(font);
        button.setForeground(textColor);
        button.setPreferredSize(dim);
        button.setMinimumSize(dim);
        button.setMaximumSize(dim);
        button.setSize(dim);
        button.setBackground(buttonColor);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
    }

    //se creeaza meniul
    //https://stackoverflow.com/questions/29061969/adding-menubar-to-jframe
    public MenuPanel(JFrame frame) {
        this.frame = frame;

        //se incarca imaginea de fundal pentru meniu
        try {
            imagine = ImageIO.read(Objects.requireNonNull(getClass().getResource("dragonmeniu.png")));
        } catch (IOException e) {
            // daca nu poate fi incarcata imaginea se foloseste un fundal negru
            imagine = null;
        }

        //dimensiune meniu
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1280, 720));

        // creeaza butonul new game
        newGameButton = new JButton("New Game"){

            //sa pot sa schimb culoarea si sa pot pune umbra
            //https://stackoverflow.com/questions/68461904/jlabel-text-shadow
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                // umbra
                g2.setColor(shadowColor);
                g2.fillRoundRect(4, 6, getWidth()-8, getHeight()-8, 25, 25);

                // desenare buton
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 25, 25);

                // desenare text
                g2.setFont(getFont());
                FontMetrics metrics = g2.getFontMetrics();
                int textX = (getWidth() - metrics.stringWidth(getText())) / 2;
                int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                // umbra textului
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), textX + 2, textY + 2);

                //text
                g2.setColor(getForeground());
                g2.drawString(getText(), textX, textY);
            }
        };

        creeareButon(newGameButton, font, buttonColor, textColor, new Dimension(350, 65));

        // creeaza butonul exit
        exitButton = new JButton("Exit"){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // umbra
                g2.setColor(shadowColor);
                g2.fillRoundRect(4, 6, getWidth()-8, getHeight()-8, 25, 25);

                // desenare buton
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 25, 25);

                // desenare text
                g2.setFont(getFont());
                FontMetrics metrics = g2.getFontMetrics();
                int textX = (getWidth() - metrics.stringWidth(getText())) / 2;
                int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                // umbra textului
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), textX + 2, textY + 2);

                // textul principal
                g2.setColor(getForeground());
                g2.drawString(getText(), textX, textY);
            }
        };

        creeareButon(exitButton, font, buttonColor, textColor, new Dimension(350, 65));

        // efectul pentru cand se da hover peste buton
        //https://stackoverflow.com/questions/22638926/how-to-put-hover-effect-on-jbutton
        newGameButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                newGameButton.setBackground(hoverColor);
                newGameButton.repaint();
            }
            public void mouseExited(MouseEvent e) {
                newGameButton.setBackground(buttonColor);
                newGameButton.repaint();
            }
        });

        exitButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(hoverColor);
                exitButton.repaint();
            }
            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(buttonColor);
                exitButton.repaint();
            }
        });

        // actiune pentru click
        newGameButton.addActionListener(e -> startGame());
        exitButton.addActionListener(e -> exitGame());

        //spatiu intre butoane
        //https://stackoverflow.com/questions/20238352/understanding-gridbaglayout-constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        //afisarea butoanelor
        gbc.gridy = 0;
        add(newGameButton, gbc);

        gbc.gridy = 1;
        add(exitButton, gbc);

    }


    //https://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // deseneaza imaginea de fundal
        if (imagine != null) {
            g.drawImage(imagine, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(0, 0, 0));
        }
    }

    //actiune pentru new game
    private void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            frame.remove(this);
            GamePanel gamePanel = new GamePanel();
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
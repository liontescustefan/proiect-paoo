import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Isometric Bean Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create and add the menu panel
        MenuPanel menuPanel = new MenuPanel(frame);
        frame.add(menuPanel);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


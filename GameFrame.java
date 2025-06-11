import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null); // centre la fenêtre

        // Crée et affiche le menu
        GameMenu menu = new GameMenu((name, mode) -> {
            this.getContentPane().removeAll(); // supprime le menu

            GamePanel gamePanel = new GamePanel(name, mode); // déclare ici
            this.add(gamePanel);
            gamePanel.requestFocusInWindow(); // force le focus clavier

            this.pack();
            this.revalidate();
            this.repaint();
        });

        this.add(menu);
        this.pack();
        this.setVisible(true);
    }
}

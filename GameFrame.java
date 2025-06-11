import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame() {
        this.add(new GamePanel());
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); // ajuste à la taille du panel
        this.setVisible(true);
        this.setLocationRelativeTo(null); // centre la fenêtre
    }
}

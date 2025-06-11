import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.Comparator;

public class GameMenu extends JPanel {

    public interface StartGameListener {
        void onStart(String name, String mode);
    }

    public GameMenu(StartGameListener listener) {
        this.setPreferredSize(new Dimension(600, 600));
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);

        JLabel titleLabel = new JLabel("Welcome to Snake Game");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(50, 40, 500, 40);
        this.add(titleLabel);

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(labelFont);
        nameLabel.setBounds(200, 120, 200, 30);
        this.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(200, 150, 200, 30);
        this.add(nameField);

        JLabel modeLabel = new JLabel("Select mode:");
        modeLabel.setFont(labelFont);
        modeLabel.setBounds(200, 190, 200, 30);
        this.add(modeLabel);

        String[] modes = {"Classic", "Hardcore"};
        JComboBox<String> modeBox = new JComboBox<>(modes);
        modeBox.setBounds(200, 220, 200, 30);
        this.add(modeBox);

        JButton startButton = new JButton("Start Game");
        startButton.setBounds(200, 270, 200, 40);
        startButton.setFocusable(false);
        this.add(startButton);

        JButton highScoresButton = new JButton("View High Scores");
        highScoresButton.setBounds(200, 320, 200, 40);
        highScoresButton.setFocusable(false);
        this.add(highScoresButton);

        JTextArea scoreArea = new JTextArea();
        scoreArea.setEditable(false);
        scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(scoreArea);
        scrollPane.setBounds(150, 380, 300, 150);
        scrollPane.setVisible(false);
        this.add(scrollPane);

        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "Anonymous";
            String mode = (String) modeBox.getSelectedItem();
            listener.onStart(name, mode);
        });

        highScoresButton.addActionListener(e -> {
            scrollPane.setVisible(true);
            File file = new File("scores.json");
            if (!file.exists()) {
                scoreArea.setText("No high scores yet.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                ArrayList<JSONObject> scores = new ArrayList<>();
                JSONParser parser = new JSONParser();
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        JSONObject obj = (JSONObject) parser.parse(line);
                        scores.add(obj);
                    } catch (ParseException ignored) {}
                }

                if (scores.isEmpty()) {
                    scoreArea.setText("No high scores yet.");
                } else {
                    scores.sort(Comparator.comparingInt(o -> -((Long) o.get("score")).intValue()));
                    StringBuilder sb = new StringBuilder();
                    sb.append("  Name         Score   Mode\n");
                    sb.append("-----------------------------\n");
                    for (int i = 0; i < Math.min(10, scores.size()); i++) {
                        JSONObject o = scores.get(i);
                        String name = (String) o.get("name");
                        long score = (Long) o.get("score");
                        String mode = (String) o.get("mode");
                        sb.append(String.format("%-13s %-7d %s\n", name, score, mode));
                    }
                    scoreArea.setText(sb.toString());
                }

            } catch (IOException ex) {
                scoreArea.setText("Error reading scores.");
                ex.printStackTrace();
            }
        });
    }
}
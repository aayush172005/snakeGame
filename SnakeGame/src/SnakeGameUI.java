import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnakeGameUI extends JFrame {
    public SnakeGameUI() {
        setTitle("🐍 Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new FlowLayout());

        JLabel difficultyLabel = new JLabel("Select Difficulty: ");
        String[] options = {"Easy", "Hard"};
        JComboBox<String> difficultyBox = new JComboBox<>(options);
        JButton startButton = new JButton("Start Game");

        selectionPanel.add(difficultyLabel);
        selectionPanel.add(difficultyBox);
        selectionPanel.add(startButton);

        add(selectionPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean hardMode = difficultyBox.getSelectedItem().equals("Hard");
                getContentPane().removeAll();
                GamePanel gamePanel = new GamePanel(hardMode);
                setContentPane(gamePanel);
                revalidate();
                repaint();
                pack();
                gamePanel.requestFocusInWindow();
            }
        });
    }
}

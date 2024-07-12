import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SudokuGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame startFrame = new JFrame("Sudoku Game");
            startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            startFrame.setSize(600, 600);
            startFrame.setLayout(new BorderLayout());

            // Custom JPanel for background with bubbles
            JPanel backgroundPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Draw bubbles
                    Random rand = new Random();
                    for (int i = 0; i < 50; i++) {
                        int x = rand.nextInt(getWidth());
                        int y = rand.nextInt(getHeight());
                        int diameter = rand.nextInt(50) + 10;
                        Color bubbleColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), 150);
                        g.setColor(bubbleColor);
                        g.fillOval(x, y, diameter, diameter);
                    }
                }
            };
            backgroundPanel.setLayout(new BorderLayout());
            startFrame.setContentPane(backgroundPanel);

            JLabel welcomeLabel = new JLabel("Welcome to Sudoku Game!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            welcomeLabel.setForeground(Color.BLACK); // Change font color to white
            backgroundPanel.add(welcomeLabel, BorderLayout.CENTER);

            JButton startButton = new JButton("Start Game");
            startButton.setFont(new Font("Arial", Font.BOLD, 20));
            startButton.setBackground(new Color(30, 144, 255)); // Dodger Blue background
            startButton.setForeground(Color.WHITE); // White font color
            startButton.addActionListener(e -> {
                SudokuFrame sudokuFrame = new SudokuFrame();
                sudokuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                sudokuFrame.setVisible(true);
                startFrame.dispose();
            });
            backgroundPanel.add(startButton, BorderLayout.SOUTH);

            startFrame.setVisible(true);
        });
    }
}

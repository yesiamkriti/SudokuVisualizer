import javax.swing.*;
import java.awt.*;

public class SudokuGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame startFrame = new JFrame("Sudoku Game");
            startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            startFrame.setSize(600, 600);
            startFrame.setLayout(new BorderLayout());

            JLabel welcomeLabel = new JLabel("Welcome to Sudoku Game!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            startFrame.add(welcomeLabel, BorderLayout.CENTER);

            JButton startButton = new JButton("Start Game");
            startButton.setFont(new Font("Arial", Font.BOLD, 20));
            startButton.addActionListener(e -> {
                SudokuFrame sudokuFrame = new SudokuFrame();
                sudokuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                sudokuFrame.setVisible(true);
                startFrame.dispose();
            });
            startFrame.add(startButton, BorderLayout.SOUTH);

            startFrame.setVisible(true);
        });
    }
}

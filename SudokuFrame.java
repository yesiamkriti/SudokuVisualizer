import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

public class SudokuFrame extends JFrame {
    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int delay = 500; // delay in milliseconds

    private int[][] initialBoard;

    private JButton EndButton; // Declare EndButton as a class variable
    private JButton stopButton;    // Declare stopButton as a class variable
    private JButton solveButton;
    private JButton hintButton;
    private JButton resetButton;
    private SolverWorker solverWorker; // Declare solverWorker to control solving process

    public SudokuFrame() {
        setTitle("Sudoku Visualizer");
        setSize(600, 700); // Adjusted size to accommodate slider
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                final int row = i;
                final int col = j;
                cells[i][j].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        validateInput(row, col);
                    }
                });
                gridPanel.add(cells[i][j]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY); // Set background color for button panel

        // Initialize solveButton
        solveButton = new JButton("Solve");
        customizeButton(solveButton, Color.GREEN);
        solveButton.addActionListener(e -> {
            solverWorker = new SolverWorker();
            solverWorker.execute();
            stopButton.setVisible(true); // Show stopButton when solving starts
        });
        buttonPanel.add(solveButton);

        // Initialize hintButton
        hintButton = new JButton("Hint");
        customizeButton(hintButton, Color.BLUE);
        hintButton.addActionListener(e -> provideHint());
        buttonPanel.add(hintButton);

        // Initialize resetButton
        resetButton = new JButton("Reset");
        customizeButton(resetButton, Color.ORANGE);
        resetButton.addActionListener(e -> resetBoard());
        buttonPanel.add(resetButton);

        // Initialize EndButton
        EndButton = new JButton("Exit Game");
        customizeButton(EndButton, Color.RED);
        EndButton.addActionListener(e -> dispose()); // Close the current SudokuFrame instance
        EndButton.setVisible(false); // Initially hide EndButton
        buttonPanel.add(EndButton);

        // Initialize stopButton
        stopButton = new JButton("Stop");
        customizeButton(stopButton, Color.MAGENTA);
        stopButton.addActionListener(e -> {
            if (solverWorker != null) {
                solverWorker.cancel(true); // Stop the solver worker if it's running
            }
        });
        stopButton.setVisible(false); // Initially hide stopButton
        buttonPanel.add(stopButton);

        add(buttonPanel, BorderLayout.SOUTH);

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, delay);
        speedSlider.setMajorTickSpacing(200);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setBackground(Color.LIGHT_GRAY);
        speedSlider.addChangeListener(e -> delay = speedSlider.getValue());
        add(speedSlider, BorderLayout.NORTH);

        initialBoard = generateRandomSudoku();
        resetBoard();
    }

    private void customizeButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void hideGameButtons() {
        hintButton.setVisible(false);
        solveButton.setVisible(false);
        stopButton.setVisible(false);
    }

    private void showGameButtons() {
        hintButton.setVisible(true);
        solveButton.setVisible(true);
        stopButton.setVisible(false);
    }

    private void provideHint() {
        int[][] board = getBoard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(board, i, j, num)) {
                            board[i][j] = num;
                            if (solve(board)) { // Check if this number leads to a solution
                                cells[i][j].setText(String.valueOf(num));
                                return;
                            }
                            board[i][j] = 0; // Reset the cell if it doesn't lead to a solution
                        }
                    }
                    JOptionPane.showMessageDialog(this, "No hint available", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Board is already solved or no empty cell found", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setText(initialBoard[i][j] == 0 ? "" : String.valueOf(initialBoard[i][j]));
                cells[i][j].setEditable(initialBoard[i][j] == 0);
                if (initialBoard[i][j] == 0) {
                    cells[i][j].setBackground(Color.WHITE);
                } else {
                    cells[i][j].setBackground(Color.LIGHT_GRAY); // Set a different background color for initial values
                }
            }
        }
        EndButton.setVisible(false); // Hide EndButton on reset
        showGameButtons(); // Show other game buttons on reset
    }

    private int[][] generateRandomSudoku() {
        int[][] board = new int[SIZE][SIZE];
        fillDiagonal(board);
        fillRemaining(board, 0, 3);
        removeDigits(board);
        return board;
    }

    private void fillDiagonal(int[][] board) {
        for (int i = 0; i < SIZE; i += 3) {
            fillBox(board, i, i);
        }
    }

    private boolean fillRemaining(int[][] board, int i, int j) {
        if (j >= SIZE && i < SIZE - 1) {
            i++;
            j = 0;
        }
        if (i >= SIZE && j >= SIZE)
            return true;

        if (i < 3) {
            if (j < 3)
                j = 3;
        } else if (i < SIZE - 3) {
            if (j == (i / 3) * 3)
                j += 3;
        } else {
            if (j == SIZE - 3) {
                i++;
                j = 0;
                if (i >= SIZE)
                    return true;
            }
        }

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(board, i, j, num)) {
                board[i][j] = num;
                if (fillRemaining(board, i, j + 1))
                    return true;
                board[i][j] = 0;
            }
        }
        return false;
    }

    private void fillBox(int[][] board, int row, int col) {
        Random random = new Random();
        int num;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                do {
                    num = random.nextInt(SIZE) + 1;
                } while (!isValidInBox(board, row, col, num));
                board[row + i][col + j] = num;
            }
        }
    }

    private boolean isValidInBox(int[][] board, int row, int col, int num) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[row + i][col + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeDigits(int[][] board) {
        int count = 50; // Number of cells to remove
        while (count != 0) {
            int cellId = new Random().nextInt(SIZE * SIZE);
            int i = cellId / SIZE;
            int j = cellId % SIZE;

            if (board[i][j] != 0) {
                board[i][j] = 0;
                count--;
            }
        }
    }

    private int[][] getBoard() {
        int[][] board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                try {
                    board[i][j] = Integer.parseInt(cells[i][j].getText());
                } catch (NumberFormatException e) {
                    board[i][j] = 0;
                }
            }
        }
        return board;
    }

    private boolean solve(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            if (solve(board)) {
                                return true;
                            } else {
                                board[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num ||
                board[row - row % 3 + i / 3][col - col % 3 + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    private void validateInput(int row, int col) {
        String text = cells[row][col].getText();
        if (text.isEmpty()) {
            cells[row][col].setBackground(Color.WHITE);
            return;
        }

        try {
            int value = Integer.parseInt(text);
            if (value < 1 || value > 9 || !isValid(getBoard(), row, col, value)) {
                cells[row][col].setBackground(Color.RED);
            } else {
                cells[row][col].setBackground(Color.YELLOW);
            }
        } catch (NumberFormatException e) {
            cells[row][col].setBackground(Color.RED);
        }
    }

    private class SolverWorker extends SwingWorker<Boolean, int[][]> {
        private int[][] board;

        public SolverWorker() {
            board = getBoard();
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            return solveWithVisualization(board);
        }

        @Override
        protected void process(java.util.List<int[][]> chunks) {
            int[][] latestBoard = chunks.get(chunks.size() - 1);
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    cells[i][j].setText(latestBoard[i][j] == 0 ? "" : String.valueOf(latestBoard[i][j]));
                }
            }
        }

        @Override
        protected void done() {
            try {
                boolean solved = get();
                if (solved) {
                    JOptionPane.showMessageDialog(SudokuFrame.this, "Congratulations! You won the game.", "Winner!", JOptionPane.INFORMATION_MESSAGE);
                    EndButton.setVisible(true); // Show EndButton on win
                    hideGameButtons(); // Hide other game buttons on win
                } else {
                    JOptionPane.showMessageDialog(SudokuFrame.this, "No solution exists", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (CancellationException e) {
                System.out.println("Solving process was cancelled.");
            }
        }

        private boolean solveWithVisualization(int[][] board) throws InterruptedException {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (board[row][col] == 0) {
                        for (int num = 1; num <= SIZE; num++) {
                            if (isValid(board, row, col, num)) {
                                board[row][col] = num;
                                cells[row][col].setBackground(Color.CYAN);
                                publish(copyBoard(board));
                                Thread.sleep(delay);
                                if (isCancelled()) { // Check if the task is cancelled
                                    return false;
                                }
                                if (solveWithVisualization(board)) {
                                    cells[row][col].setBackground(Color.GREEN);
                                    return true;
                                } else {
                                    board[row][col] = 0;
                                    publish(copyBoard(board));
                                    cells[row][col].setBackground(Color.PINK); 
                                    Thread.sleep(delay);
                                }
                            }
                        }
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuFrame frame = new SudokuFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}


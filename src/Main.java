import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int THREAD_POOL_SIZE = 1;

    private JFrame frame;
    private JButton[][] buttons;
    private JButton resetButton;
    private JLabel statusLabel;

    private boolean xTurn = true;
    private boolean gameOver = false;

    private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public Main() {
        frame = new JFrame("Enhanced Tic-Tac-Toe Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        buttons = new JButton[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 50));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                boardPanel.add(buttons[i][j]);
            }
        }

        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        statusLabel = new JLabel("X's Turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(resetButton, BorderLayout.SOUTH);
        frame.add(statusLabel, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {

        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameOver && buttons[row][col].getText().isEmpty()) {
                if (xTurn) {
                    buttons[row][col].setText("X");
                    statusLabel.setText("O's Turn");
                } else {
                    buttons[row][col].setText("O");
                    statusLabel.setText("X's Turn");
                }

                xTurn = !xTurn;

                executorService.execute(() -> {
                    if (checkWin()) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText((xTurn ? "O" : "X") + " Wins!");
                            gameOver = true;
                        });
                    } else if (checkDraw()) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("It's a Draw!");
                            gameOver = true;
                        });
                    }
                });
            }
        }
    }

    private boolean checkWin() {
        // Check rows, columns, and diagonals for a win
        return (checkRowCol(buttons[0][0].getText(), buttons[0][1].getText(), buttons[0][2].getText()) ||
                checkRowCol(buttons[1][0].getText(), buttons[1][1].getText(), buttons[1][2].getText()) ||
                checkRowCol(buttons[2][0].getText(), buttons[2][1].getText(), buttons[2][2].getText()) ||
                checkRowCol(buttons[0][0].getText(), buttons[1][0].getText(), buttons[2][0].getText()) ||
                checkRowCol(buttons[0][1].getText(), buttons[1][1].getText(), buttons[2][1].getText()) ||
                checkRowCol(buttons[0][2].getText(), buttons[1][2].getText(), buttons[2][2].getText()) ||
                checkRowCol(buttons[0][0].getText(), buttons[1][1].getText(), buttons[2][2].getText()) ||
                checkRowCol(buttons[0][2].getText(), buttons[1][1].getText(), buttons[2][0].getText()));
    }

    private boolean checkRowCol(String s1, String s2, String s3) {
        return (!s1.isEmpty() && s1.equals(s2) && s2.equals(s3));
    }

    private boolean checkDraw() {
        // Check if all buttons are clicked and no one has won
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        // Clear board and reset game variables
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        statusLabel.setText("X's Turn");
        xTurn = true;
        gameOver = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}

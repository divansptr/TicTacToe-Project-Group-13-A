package TTT;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The tictactoe.Board and tictactoe.Cell classes are separated in their own classes.
 */
public class TTT extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);  // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Timer constants
    private static final int TIME_LIMIT_SECONDS = 10;
    private static final Color COLOR_TIMER_FULL = new Color(76, 175, 80); // Green
    private static final Color COLOR_TIMER_MEDIUM = new Color(255, 152, 0); // Orange
    private static final Color COLOR_TIMER_LOW = new Color(244, 67, 54); // Red

    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message
    private JPanel timerPanel;   // Panel for visual timer representation

    // Timer-related variables
    private Timer gameTimer;
    private int remainingTime;
    private TimerTask currentTimerTask;

    /** Constructor to setup the UI and game components */
    public TTT() {
        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        // Update cells[][] and return the new game state after the move
                        currentState = board.stepGame(currentPlayer, row, col);
                        // Play the different sound effect
                        if (currentPlayer == Seed.CROSS) {
                            SoundEffect.CROSS.play();
                        } else {
                            SoundEffect.NOUGHT.play();
                        }

                        // Stop the current timer
                        stopTimer();

                        // Switch player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                        // Start timer for new player
                        startTimer();
                    }
                } else {        // game over
                    newGame();  // restart the game
                    SoundEffect.DIE.play();
                }
                // Refresh the drawing canvas
                repaint();  // Callback paintComponent().
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Create visual timer panel
        timerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Calculate timer visualization
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                // Calculate width based on remaining time
                double timeRatio = (double) remainingTime / TIME_LIMIT_SECONDS;
                int timerWidth = (int) (panelWidth * timeRatio);

                // Choose color based on remaining time
                Color timerColor;
                if (timeRatio > 0.5) {
                    timerColor = COLOR_TIMER_FULL;
                } else if (timeRatio > 0.25) {
                    timerColor = COLOR_TIMER_MEDIUM;
                } else {
                    timerColor = COLOR_TIMER_LOW;
                }

                // Draw timer bar
                g.setColor(timerColor);
                g.fillRect(0, 0, timerWidth, panelHeight);

                // Draw time text
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                String timeText = remainingTime + "s";
                FontMetrics fm = g.getFontMetrics();
                int textX = (panelWidth - fm.stringWidth(timeText)) / 2;
                int textY = (panelHeight + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(timeText, textX, textY);
            }
        };
        timerPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 20));
        timerPanel.setBackground(Color.LIGHT_GRAY);

        // Create a panel to hold both timer and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(timerPanel, BorderLayout.NORTH);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);

        super.setLayout(new BorderLayout());
        super.add(bottomPanel, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 50));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();  // allocate the game-board
        gameTimer = new Timer();
    }

    private void startTimer() {
        stopTimer();
        remainingTime = TIME_LIMIT_SECONDS;

        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                remainingTime--;

                SwingUtilities.invokeLater(() -> {
                    timerPanel.repaint();

                    if (remainingTime <= 0) {
                        // Switch player due to time out
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                        // Stop current timer
                        stopTimer();

                        // Start timer for new player
                        startTimer();

                        // Play sound effect
                        SoundEffect.DIE.play();

                        // Repaint to update status
                        repaint();
                    }
                });
            }
        };

        gameTimer.scheduleAtFixedRate(currentTimerTask, 1000, 1000);
    }

    private void stopTimer() {
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play

        // Reset and start timer
        stopTimer();
        startTimer();
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        setBackground(COLOR_BG); // set its background color

        board.paint(g);  // ask the game board to paint itself

        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            stopTimer();
            SoundEffect.DRAW.play();
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
            stopTimer();
            SoundEffect.YEAY.play();
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
            stopTimer();
            SoundEffect.YEAY.play();
        }
    }

    /** The entry "main" method */
    public static void play() {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                // Set the content-pane of the JFrame to an instance of main JPanel
                frame.setContentPane(new TTT());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // center the application window
                frame.setVisible(true);            // show it
            }
        });
    }
}
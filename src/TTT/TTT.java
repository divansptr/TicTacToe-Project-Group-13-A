/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #13
 * 1- 5026231020- Diva Nesia Putri
 * 2- 5026231114- Imanuel Dwi Prasetyo
 * 3- 5026231196- Ni Kadek Adelia Paramita Putri
 */

package TTT;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class TTT extends JPanel {
    private static final long serialVersionUID = 1L;

    private boolean userVsComputer;
    private JButton btnUserVsComputer;
    private JButton btnTwoPlayers;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private static final int TIME_LIMIT_SECONDS = 10;
    private static final Color COLOR_TIMER_FULL = new Color(76, 175, 80);
    private static final Color COLOR_TIMER_MEDIUM = new Color(255, 152, 0);
    private static final Color COLOR_TIMER_LOW = new Color(244, 67, 54);

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private JPanel timerPanel;
    private JButton restartButton;

    private Timer gameTimer;
    private int remainingTime;
    private TimerTask currentTimerTask;

    public TTT() {
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);

                        // Mainkan efek suara sesuai giliran pemain
                        if (currentPlayer == Seed.CROSS) {
                            SoundEffect.CROSS.play();
                        } else {
                            SoundEffect.NOUGHT.play();
                        }

                        repaint();

                        if (currentState == State.PLAYING) {
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                            if (userVsComputer && currentPlayer == Seed.NOUGHT) {
                                computerMove();
                            }
                        }
                    }
                } else {
                    newGame();
                    SoundEffect.DIE.play();
                }
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("OCR A Extended", Font.PLAIN, 13));
        restartButton.setMargin(new Insets(1, 4, 1, 4));
        restartButton.setPreferredSize(new Dimension(70, 20));
        restartButton.setFocusable(false);
        restartButton.setBackground(Color.LIGHT_GRAY);
        restartButton.addActionListener(e -> {
            SoundEffect.DIE.play();
            newGame();
            repaint();
        });

        timerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                double timeRatio = (double) remainingTime / TIME_LIMIT_SECONDS;
                int timerWidth = (int) (panelWidth * timeRatio);

                Color timerColor;
                if (timeRatio > 0.5) {
                    timerColor = COLOR_TIMER_FULL;
                } else if (timeRatio > 0.25) {
                    timerColor = COLOR_TIMER_MEDIUM;
                } else {
                    timerColor = COLOR_TIMER_LOW;
                }

                g.setColor(timerColor);
                g.fillRect(0, 0, timerWidth, panelHeight);

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

        btnUserVsComputer = new JButton("User vs Computer");
        btnTwoPlayers = new JButton("2 Players");

        btnUserVsComputer.addActionListener(e -> {
            userVsComputer = true;
            newGame();
        });

        btnTwoPlayers.addActionListener(e -> {
            userVsComputer = false;
            newGame();
        });

        JPanel modePanel = new JPanel();
        modePanel.add(btnUserVsComputer);
        modePanel.add(btnTwoPlayers);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusBar, BorderLayout.CENTER);
        statusPanel.add(restartButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(timerPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        super.setLayout(new BorderLayout());
        super.add(modePanel, BorderLayout.NORTH);
        super.add(bottomPanel, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 50));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    private void initGame() {
        board = new Board();
        currentState = State.PLAYING;
        currentPlayer = Seed.CROSS;
        gameTimer = new Timer();
    }

    private void startTimer() {
        remainingTime = TIME_LIMIT_SECONDS;

        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                remainingTime--;

                SwingUtilities.invokeLater(() -> {
                    timerPanel.repaint();

                    if (remainingTime <= 0) {
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        stopTimer();
                        startTimer();
                        SoundEffect.DIE.play();
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

    public void newGame() {
        board = new Board();
        board.newGame();
        currentState = State.PLAYING;
        currentPlayer = Seed.CROSS;
        stopTimer();
        startTimer();
        repaint();
    }

    public void computerMove() {
        if (userVsComputer) {
            int[] move = new AIPlayerMinimax(board).move(); // Placeholder for a real AI move
            int row = move[0];
            int col = move[1];
            currentState = board.stepGame(currentPlayer, row, col);
            SoundEffect.NOUGHT.play();
            repaint();
            currentPlayer = Seed.CROSS;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);

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

    public static void play() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new TTT());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
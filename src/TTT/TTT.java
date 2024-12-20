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
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TTT extends JPanel {
    private static final long serialVersionUID = 1L;

    public boolean userVsComputer;
    private JButton btnUserVsComputer;
    private JButton btnTwoPlayers;
    private boolean isGameModeSelected = false;

    public static final String TITLE = "Tic Tac Toe";
    //public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Image backgroundImage;

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
    private BGM bgm;

    private Timer gameTimer;
    private int remainingTime;
    private TimerTask currentTimerTask;

    public TTT() {
        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("src/images/bg2.jpg")); // Update the path to your image
        } catch (IOException e) {
            e.printStackTrace();
        }

        bgm = new BGM("src/audio/bgm-ttt.wav");
        bgm.play();

        board = new Board();

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

                        // Reset waktu
                        remainingTime = TIME_LIMIT_SECONDS;

                        repaint();

                        if (currentState == State.PLAYING) {
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                            // Jika mode User vs Computer dan giliran komputer, lakukan langkah dengan delay
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
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //setBackground(COLOR_BG);

                // Draw background image if it exists
                if (backgroundImage != null) {
                    //g.drawImage(backgroundImage, 0, 0, this);  // Draw image at (0, 0) with current JPanel's size
                    g.drawImage(backgroundImage, 0, 0, Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT, null);
                } else {
                    setBackground(Color.BLACK); // Fallback color in case image is not loaded
                }

                // Tentukan warna berdasarkan waktu tersisa
                if (remainingTime > 5) {
                    g.setColor(COLOR_TIMER_FULL);
                } else if (remainingTime > 2) {
                    g.setColor(COLOR_TIMER_MEDIUM);
                } else {
                    g.setColor(COLOR_TIMER_LOW);
                }

                // Gambar bar timer
                int barWidth = (getWidth() * remainingTime) / TIME_LIMIT_SECONDS;
                g.fillRect(0, 0, barWidth, getHeight());

                // Tampilkan waktu tersisa
                g.setColor(Color.BLACK);
                g.setFont(new Font("Roboto", Font.BOLD, 16));
                String timeText = "Time Left: " + remainingTime + "s";
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(timeText);
                int textHeight = fm.getAscent();
                g.drawString(timeText, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2);
            };


        private void showEndGameDialog() {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Do you want to play again?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    // Tampilkan pilihan mode permainan
                    Object[] options = {"User vs Computer", "2 Players"};
                    int modeChoice = JOptionPane.showOptionDialog(
                            this,
                            "Choose game mode:",
                            "Select Mode",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    if (modeChoice == 0) {
                        userVsComputer = true;
                    } else if (modeChoice == 1) {
                        userVsComputer = false;
                    }

                    isGameModeSelected = true;
                    newGame(); // Mulai permainan baru
                } else {
                    System.exit(0); // Keluar dari sistem
                }
            }
        };
        timerPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 20));
        timerPanel.setBackground(Color.LIGHT_GRAY);

        btnUserVsComputer = new JButton("User vs Computer");
        btnTwoPlayers = new JButton("2 Players");

        btnUserVsComputer.addActionListener(e -> {
            userVsComputer = true;
            isGameModeSelected = true;
            newGame();
        });

        btnTwoPlayers.addActionListener(e -> {
            userVsComputer = false;
            isGameModeSelected = true;
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
        if (isGameModeSelected) {
            startTimer();
        }
        repaint();
    }

    public void computerMove() {
        if (userVsComputer) {
            // Gunakan Timer untuk menunda langkah komputer selama 3 detik
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // Lakukan langkah komputer hanya jika permainan masih berlangsung
                    if (currentState == State.PLAYING && currentPlayer == Seed.NOUGHT) {
                        int[] move = new AIPlayerMinimax(board).move(); // Placeholder for langkah AI
                        int row = move[0];
                        int col = move[1];
                        currentState = board.stepGame(currentPlayer, row, col);

                        // Mainkan efek suara dan reset waktu
                        SoundEffect.NOUGHT.play();
                        remainingTime = TIME_LIMIT_SECONDS;

                        SwingUtilities.invokeLater(() -> {
                            repaint();
                            if (currentState == State.PLAYING) {
                                currentPlayer = Seed.CROSS;
                            }
                        });
                    }
                }
            }, 3000); // 3000 ms = 3 detik
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //setBackground(COLOR_BG);

        // Draw background image if it exists
        if (backgroundImage != null) {
            //g.drawImage(backgroundImage, 0, 0, this);  // Draw image at (0, 0) with current JPanel's size
            g.drawImage(backgroundImage, 0, 0, Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT, null);
        } else {
            setBackground(Color.BLACK); // Fallback color in case image is not loaded
        }

        board.paint(g);

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "Cat's Turn" : "Dog's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again");
            stopTimer();
            SoundEffect.DRAW.play();
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Cat Won! Click to play again");
            stopTimer();
            SoundEffect.YEAY.play();
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Dog Won! Click to play again");
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
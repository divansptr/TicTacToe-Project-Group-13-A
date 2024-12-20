package TTT;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Welcome to Tic Tac Toe");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            // Membaca gambar dan menyesuaikan ukurannya agar pas dengan ukuran frame
            ImageIcon originalIcon = new ImageIcon("src/images/bg2.jpg");
            Image img = originalIcon.getImage(); // Mengambil objek Image dari ImageIcon
            Image scaledImg = img.getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH); // Menyesuaikan ukuran gambar
            ImageIcon scaledIcon = new ImageIcon(scaledImg); // Membuat ImageIcon baru dari gambar yang sudah disesuaikan ukurannya

            // Membuat label background dengan gambar yang sudah disesuaikan ukurannya
            JLabel backgroundLabel = new JLabel(scaledIcon);
            backgroundLabel.setLayout(new BorderLayout());
            frame.setContentPane(backgroundLabel); // Menetapkan gambar sebagai background

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(false); // Agar panel transparan dan background terlihat

            JLabel titleLabel = new JLabel("Welcome to Tic Tac Toe");
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

            JButton startButton = new JButton("Start Game");
            startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            startButton.setFont(new Font("Arial", Font.PLAIN, 16));
            startButton.setBackground(new Color(76, 175, 80));
            startButton.setForeground(Color.WHITE);
            startButton.setFocusPainted(false);
            startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            JButton exitButton = new JButton("Exit");
            exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
            exitButton.setBackground(new Color(244, 67, 54));
            exitButton.setForeground(Color.WHITE);
            exitButton.setFocusPainted(false);
            exitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            startButton.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    frame.dispose(); // Tutup layar welcome

                    JFrame gameFrame = new JFrame(TTT.TITLE);
                    TTT gamePanel = new TTT(); // Mode permainan dipilih di dalam TTT
                    gameFrame.setContentPane(gamePanel);
                    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameFrame.pack();
                    gameFrame.setLocationRelativeTo(null);
                    gameFrame.setVisible(true);
                });
            });

            exitButton.addActionListener(e -> System.exit(0));

            // Menambahkan elemen UI ke panel
            panel.add(titleLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(startButton);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(exitButton);

            // Menambahkan panel ke backgroundLabel
            backgroundLabel.add(panel, BorderLayout.CENTER);

            // Menampilkan frame dengan background gambar
            frame.setVisible(true);
        });
    }
}

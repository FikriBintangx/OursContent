package com.ourscontent.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.ourscontent.dao.UserDAO;
import com.ourscontent.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LoginFrame extends JFrame {

    private UserDAO userDAO;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Login components
    private JTextField txtLoginUser;
    private JPasswordField txtLoginPass;
    private RoundedButton btnLogin;

    // Register components
    private JTextField txtRegName;
    private JTextField txtRegUser;
    private JPasswordField txtRegPass;

    public LoginFrame() {
        userDAO = new UserDAO();

        // Apply Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Panel.background", new Color(28, 28, 30));
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatDarkLaf in Login");
        }

        setTitle("OursContent - Login");
        setSize(400, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Background Panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(new Color(28, 28, 30));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header Panel (Logo & Title)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        // Logo
        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        File svgFile = new File("assets/img/logoourss.svg");
        if (svgFile.exists()) {
            FlatSVGIcon svgIcon = new FlatSVGIcon(svgFile);
            lblLogo.setIcon(svgIcon.derive(80, 80));
        } else {
            lblLogo.setText("OursContent");
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblLogo.setForeground(new Color(245, 245, 247));
        }
        headerPanel.add(lblLogo);
        headerPanel.add(Box.createVerticalStrut(10));

        JLabel lblSub = new JLabel("Sistem Manajemen Konten Kreator");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(161, 161, 170));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblSub);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Panel with CardLayout (Login / Register forms)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // 1. Create Login Panel
        JPanel loginCard = createLoginPanel();
        // 2. Create Register Panel
        JPanel registerCard = createRegisterPanel();

        cardPanel.add(loginCard, "LOGIN");
        cardPanel.add(registerCard, "REGISTER");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        add(mainPanel);
        cardLayout.show(cardPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 10, 0));

        // Username field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(161, 161, 170));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtLoginUser = new JTextField();
        txtLoginUser.setMaximumSize(new Dimension(340, 35));
        txtLoginUser.setBackground(new Color(44, 44, 46));
        txtLoginUser.setForeground(new Color(245, 245, 247));
        txtLoginUser.setCaretColor(Color.WHITE);
        txtLoginUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 58, 60), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtLoginUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(161, 161, 170));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel passWrapper = new JPanel(new BorderLayout());
        passWrapper.setMaximumSize(new Dimension(340, 35));
        passWrapper.setBackground(new Color(44, 44, 46));
        passWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 58, 60), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        passWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLoginPass = new JPasswordField();
        txtLoginPass.setBackground(new Color(44, 44, 46));
        txtLoginPass.setForeground(new Color(245, 245, 247));
        txtLoginPass.setCaretColor(Color.WHITE);
        txtLoginPass.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton btnReveal = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.setStroke(new BasicStroke(1.5f));
                
                int w = getWidth();
                int h = getHeight();
                int size = 16;
                int x = (w - size) / 2;
                int y = (h - size) / 2;
                
                boolean isRevealed = txtLoginPass.getEchoChar() == (char) 0;
                
                // Draw eye outline (upper and lower arcs)
                g2.drawArc(x, y + 1, size, size - 4, 10, 160); // upper lid
                g2.drawArc(x, y - 3, size, size - 4, 190, 160); // lower lid
                
                // Draw pupil (filled circle)
                g2.fillOval(x + size/2 - 2, y + size/2 - 2, 4, 4);
                
                if (!isRevealed) {
                    // Draw a slash line through the eye when hidden
                    g2.drawLine(x + 2, y + 2, x + size - 2, y + size - 2);
                }
                
                g2.dispose();
            }
        };
        btnReveal.setPreferredSize(new Dimension(35, 35));
        btnReveal.setForeground(new Color(161, 161, 170));
        btnReveal.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        btnReveal.setContentAreaFilled(false);
        btnReveal.setFocusPainted(false);
        btnReveal.setCursor(new Cursor(Cursor.HAND_CURSOR));

        char defaultEchoChar = txtLoginPass.getEchoChar();
        btnReveal.addActionListener(evt -> {
            if (txtLoginPass.getEchoChar() == (char) 0) {
                txtLoginPass.setEchoChar(defaultEchoChar);
                btnReveal.setForeground(new Color(161, 161, 170));
            } else {
                txtLoginPass.setEchoChar((char) 0);
                btnReveal.setForeground(new Color(250, 88, 106)); // Highlight red eye
            }
            btnReveal.repaint();
        });

        passWrapper.add(txtLoginPass, BorderLayout.CENTER);
        passWrapper.add(btnReveal, BorderLayout.EAST);

        // Login Button
        btnLogin = new RoundedButton("Login");
        btnLogin.setMaximumSize(new Dimension(340, 38));
        btnLogin.setBackground(new Color(250, 88, 106)); // Apple Red accent
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(this::handleLogin);

        // Add components with struts
        panel.add(lblUser);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtLoginUser);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblPass);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passWrapper);
        panel.add(Box.createVerticalStrut(25));
        panel.add(btnLogin);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Fullname field
        JLabel lblName = new JLabel("Nama Lengkap");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(new Color(161, 161, 170));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtRegName = new JTextField();
        txtRegName.setMaximumSize(new Dimension(340, 35));
        txtRegName.setBackground(new Color(44, 44, 46));
        txtRegName.setForeground(new Color(245, 245, 247));
        txtRegName.setCaretColor(Color.WHITE);
        txtRegName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 58, 60), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtRegName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(161, 161, 170));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtRegUser = new JTextField();
        txtRegUser.setMaximumSize(new Dimension(340, 35));
        txtRegUser.setBackground(new Color(44, 44, 46));
        txtRegUser.setForeground(new Color(245, 245, 247));
        txtRegUser.setCaretColor(Color.WHITE);
        txtRegUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 58, 60), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtRegUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(161, 161, 170));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel passWrapper = new JPanel(new BorderLayout());
        passWrapper.setMaximumSize(new Dimension(340, 35));
        passWrapper.setBackground(new Color(44, 44, 46));
        passWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 58, 60), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        passWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtRegPass = new JPasswordField();
        txtRegPass.setBackground(new Color(44, 44, 46));
        txtRegPass.setForeground(new Color(245, 245, 247));
        txtRegPass.setCaretColor(Color.WHITE);
        txtRegPass.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton btnReveal = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.setStroke(new BasicStroke(1.5f));
                
                int w = getWidth();
                int h = getHeight();
                int size = 16;
                int x = (w - size) / 2;
                int y = (h - size) / 2;
                
                boolean isRevealed = txtRegPass.getEchoChar() == (char) 0;
                
                // Draw eye outline (upper and lower arcs)
                g2.drawArc(x, y + 1, size, size - 4, 10, 160); // upper lid
                g2.drawArc(x, y - 3, size, size - 4, 190, 160); // lower lid
                
                // Draw pupil (filled circle)
                g2.fillOval(x + size/2 - 2, y + size/2 - 2, 4, 4);
                
                if (!isRevealed) {
                    // Draw a slash line through the eye when hidden
                    g2.drawLine(x + 2, y + 2, x + size - 2, y + size - 2);
                }
                
                g2.dispose();
            }
        };
        btnReveal.setPreferredSize(new Dimension(35, 35));
        btnReveal.setForeground(new Color(161, 161, 170));
        btnReveal.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        btnReveal.setContentAreaFilled(false);
        btnReveal.setFocusPainted(false);
        btnReveal.setCursor(new Cursor(Cursor.HAND_CURSOR));

        char defaultEchoChar = txtRegPass.getEchoChar();
        btnReveal.addActionListener(evt -> {
            if (txtRegPass.getEchoChar() == (char) 0) {
                txtRegPass.setEchoChar(defaultEchoChar);
                btnReveal.setForeground(new Color(161, 161, 170));
            } else {
                txtRegPass.setEchoChar((char) 0);
                btnReveal.setForeground(new Color(250, 88, 106)); // Highlight red eye
            }
            btnReveal.repaint();
        });

        passWrapper.add(txtRegPass, BorderLayout.CENTER);
        passWrapper.add(btnReveal, BorderLayout.EAST);

        // Register Button
        JButton btnRegister = new RoundedButton("Registrasi");
        btnRegister.setMaximumSize(new Dimension(340, 38));
        btnRegister.setBackground(new Color(16, 185, 129)); // Apple Green accent
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.addActionListener(this::handleRegister);

        // Switch to Login link
        JButton btnSwitch = new JButton("Sudah punya akun? Login disini");
        btnSwitch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSwitch.setForeground(new Color(14, 165, 233));
        btnSwitch.setBorderPainted(false);
        btnSwitch.setContentAreaFilled(false);
        btnSwitch.setOpaque(false);
        btnSwitch.setFocusPainted(false);
        btnSwitch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSwitch.addActionListener(e -> {
            clearFields();
            cardLayout.show(cardPanel, "LOGIN");
        });

        // Add components with struts
        panel.add(lblName);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtRegName);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblUser);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtRegUser);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblPass);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passWrapper);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnRegister);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnSwitch);

        return panel;
    }

    private void handleLogin(ActionEvent e) {
        String username = txtLoginUser.getText().trim();
        String password = new String(txtLoginPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            OurIsland.show(this, "Isi username & password!", OurIsland.IslandType.ERROR);
            return;
        }

        // Set UI loading state
        btnLogin.setLoading(true);
        txtLoginUser.setEnabled(false);
        txtLoginPass.setEnabled(false);

        // Authenticate background thread
        new Thread(() -> {
            User user = userDAO.authenticateUser(username, password);
            SwingUtilities.invokeLater(() -> {
                if (user != null) {
                    OurIsland.show(this, "Login berhasil! Selamat datang.", OurIsland.IslandType.SUCCESS);
                    // Hold 1.2s then launch mainframe
                    Timer timer = new Timer(1200, evt -> {
                        MainFrame mainFrame = new MainFrame(user);
                        mainFrame.setVisible(true);
                        dispose();
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    OurIsland.show(this, "Username atau Password salah!", OurIsland.IslandType.ERROR);
                    // Restore UI components state
                    btnLogin.setLoading(false);
                    txtLoginUser.setEnabled(true);
                    txtLoginPass.setEnabled(true);
                }
            });
        }).start();
    }

    private void handleRegister(ActionEvent e) {
        String fullname = txtRegName.getText().trim();
        String username = txtRegUser.getText().trim();
        String password = new String(txtRegPass.getPassword()).trim();

        if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            OurIsland.show(this, "Lengkapi semua data form!", OurIsland.IslandType.ERROR);
            return;
        }

        new Thread(() -> {
            boolean success = userDAO.registerUser(username, password, fullname);
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    OurIsland.show(this, "Registrasi berhasil! Silakan login.", OurIsland.IslandType.SUCCESS);
                    clearFields();
                    cardLayout.show(cardPanel, "LOGIN");
                } else {
                    if (userDAO.isUsernameExists(username)) {
                        OurIsland.show(this, "Username sudah digunakan!", OurIsland.IslandType.ERROR);
                    } else {
                        OurIsland.show(this, "Gagal melakukan registrasi!", OurIsland.IslandType.ERROR);
                    }
                }
            });
        }).start();
    }

    private void clearFields() {
        txtLoginUser.setText("");
        txtLoginPass.setText("");
        txtRegName.setText("");
        txtRegUser.setText("");
        txtRegPass.setText("");
    }
}

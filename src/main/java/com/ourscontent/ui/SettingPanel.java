package com.ourscontent.ui;

import com.ourscontent.db.DatabaseConnection;
import com.ourscontent.model.User;
import com.ourscontent.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SettingPanel extends JPanel {

    private MainFrame mainFrame;
    private User currentUser;
    private JLabel lblConnectionStatus;
    private JButton btnTestConnection;
    private JCheckBox chkAutoRefresh;
    private JCheckBox chkNotifications;
    private JComboBox<String> cbTheme;

    public SettingPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;
        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(28, 28, 30)); // Background Utama

        // Header Title
        JLabel titleLabel = new JLabel("Pengaturan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(245, 245, 247)); // Primary Text
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center Area (Scrollable settings list)
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);

        // 1. DATABASE CONNECTION CARD
        JPanel dbCard = createSettingCard("Konfigurasi Database");
        dbCard.setLayout(new BoxLayout(dbCard, BoxLayout.Y_AXIS));

        JLabel lblDbUrl = new JLabel("Database Host: aws-1-ap-southeast-1.pooler.supabase.com");
        lblDbUrl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDbUrl.setForeground(new Color(161, 161, 170));
        lblDbUrl.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblConnectionStatus = new JLabel("Status Koneksi: Menghubungkan...");
        lblConnectionStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConnectionStatus.setForeground(new Color(250, 180, 88));
        lblConnectionStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnTestConnection = new RoundedButton("Tes Koneksi Database");
        btnTestConnection.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTestConnection.addActionListener(e -> testDatabaseConnection());

        dbCard.add(lblDbUrl);
        dbCard.add(Box.createVerticalStrut(10));
        dbCard.add(lblConnectionStatus);
        dbCard.add(Box.createVerticalStrut(15));
        dbCard.add(btnTestConnection);

        contentContainer.add(dbCard);
        contentContainer.add(Box.createVerticalStrut(15));

        // 2. GENERAL PREFERENCES CARD
        JPanel prefCard = createSettingCard("Preferensi Aplikasi");
        prefCard.setLayout(new BoxLayout(prefCard, BoxLayout.Y_AXIS));

        chkAutoRefresh = new JCheckBox("Auto-refresh data Dashboard (setiap 30 detik)");
        chkAutoRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkAutoRefresh.setForeground(new Color(245, 245, 247));
        chkAutoRefresh.setOpaque(false);
        chkAutoRefresh.setFocusPainted(false);
        chkAutoRefresh.setSelected(true);
        chkAutoRefresh.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkNotifications = new JCheckBox("Aktifkan notifikasi desktop (Toast)");
        chkNotifications.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkNotifications.setForeground(new Color(245, 245, 247));
        chkNotifications.setOpaque(false);
        chkNotifications.setFocusPainted(false);
        chkNotifications.setSelected(true);
        chkNotifications.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Theme combobox
        JLabel lblTheme = new JLabel("Tema Tampilan");
        lblTheme.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTheme.setForeground(new Color(161, 161, 170));
        lblTheme.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbTheme = new JComboBox<>(new String[]{"FlatLaf Dark Apple (Aktif)", "FlatLaf Light Apple"});
        cbTheme.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTheme.setBackground(new Color(28, 28, 30));
        cbTheme.setForeground(new Color(245, 245, 247));
        cbTheme.setMaximumSize(new Dimension(200, 30));
        cbTheme.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbTheme.addActionListener(e -> {
            boolean isLight = cbTheme.getSelectedIndex() == 1;
            mainFrame.setTheme(isLight);
        });

        prefCard.add(chkAutoRefresh);
        prefCard.add(Box.createVerticalStrut(8));
        prefCard.add(chkNotifications);
        prefCard.add(Box.createVerticalStrut(15));
        prefCard.add(lblTheme);
        prefCard.add(Box.createVerticalStrut(4));
        prefCard.add(cbTheme);

        contentContainer.add(prefCard);
        contentContainer.add(Box.createVerticalStrut(15));

        // 3. ABOUT APPLICATION CARD
        JPanel aboutCard = createSettingCard("Tentang Aplikasi");
        aboutCard.setLayout(new BoxLayout(aboutCard, BoxLayout.Y_AXIS));

        JLabel lblVersion = new JLabel("OursContent Creator Dashboard v1.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVersion.setForeground(new Color(245, 245, 247));
        lblVersion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCopyright = new JLabel("© 2026 OursContent. Hak Cipta Dilindungi.");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(new Color(110, 110, 115));
        lblCopyright.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("Aplikasi dashboard manajemen performa konten multi-platform.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(161, 161, 170));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        aboutCard.add(lblVersion);
        aboutCard.add(Box.createVerticalStrut(4));
        aboutCard.add(lblCopyright);
        aboutCard.add(Box.createVerticalStrut(8));
        aboutCard.add(lblDesc);

        contentContainer.add(aboutCard);

        // User Management is now its own dedicated panel/menu in the sidebar.

        // Wrap content inside a JScrollPane for scrollability
        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Perform initial connection test asynchronously
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return DatabaseConnection.testConnection();
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    updateConnectionStatus(success);
                } catch (Exception ex) {
                    updateConnectionStatus(false);
                }
            }
        };
        worker.execute();
    }

    private void testDatabaseConnection() {
        lblConnectionStatus.setText("Status Koneksi: Menguji koneksi...");
        lblConnectionStatus.setForeground(new Color(250, 180, 88));
        btnTestConnection.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return DatabaseConnection.testConnection();
            }

            @Override
            protected void done() {
                btnTestConnection.setEnabled(true);
                try {
                    boolean success = get();
                    updateConnectionStatus(success);
                    if (success) {
                        OurIsland.show(SettingPanel.this, "Koneksi database berhasil!", OurIsland.IslandType.SUCCESS);
                    } else {
                        OurIsland.show(SettingPanel.this, "Koneksi database gagal!", OurIsland.IslandType.ERROR);
                    }
                } catch (Exception ex) {
                    updateConnectionStatus(false);
                    OurIsland.show(SettingPanel.this, "Error menguji koneksi: " + ex.getMessage(), OurIsland.IslandType.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void updateConnectionStatus(boolean connected) {
        if (connected) {
            lblConnectionStatus.setText("Status Koneksi: Terhubung ke Supabase Cloud DB");
            lblConnectionStatus.setForeground(new Color(16, 185, 129)); // Emerald Green
        } else {
            lblConnectionStatus.setText("Status Koneksi: Terputus / Gagal Terhubung");
            lblConnectionStatus.setForeground(new Color(250, 88, 106)); // Coral Red
        }
    }

    private JPanel createSettingCard(String titleText) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor); // Border
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(new Color(44, 44, 46));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(245, 245, 247));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(titleLabel);

        return card;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(28, 28, 30));
        tf.setForeground(new Color(245, 245, 247));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(58, 58, 60), 1),
                new EmptyBorder(5, 7, 5, 7)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBackground(new Color(28, 28, 30));
        pf.setForeground(new Color(245, 245, 247));
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(58, 58, 60), 1),
                new EmptyBorder(5, 7, 5, 7)
        ));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return pf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(new Color(28, 28, 30));
        cb.setForeground(new Color(245, 245, 247));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    private void addFormField(JPanel panel, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(161, 161, 170));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(component);
        panel.add(Box.createVerticalStrut(10));
    }
}

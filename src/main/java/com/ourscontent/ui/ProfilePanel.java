package com.ourscontent.ui;

import com.ourscontent.dao.UserDAO;
import com.ourscontent.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ProfilePanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO;
    private MainFrame mainFrame;

    private JTextField txtFullname;
    private JTextField txtUsername;
    private JPasswordField txtOldPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    private JLabel lblUserFullname;
    private JLabel lblUserUsername;
    private JLabel lblUserRole;
    private JLabel lblUserJoined;

    public ProfilePanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(28, 28, 30)); // Background Utama

        // Header Title
        JLabel titleLabel = new JLabel("Profil Anda");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(245, 245, 247)); // Primary Text
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center split: Form di Kiri, User Card di Kanan
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // 1. LEFT PANEL: EDIT FORM
        JPanel formCard = new JPanel() {
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
        formCard.setOpaque(false);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(new Color(44, 44, 46));
        formCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel formTitle = new JLabel("Perbarui Informasi Profil");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(new Color(245, 245, 247));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(15));

        txtFullname = createStyledTextField();
        txtUsername = createStyledTextField();
        txtOldPassword = createStyledPasswordField();
        txtNewPassword = createStyledPasswordField();
        txtConfirmPassword = createStyledPasswordField();

        addFormField(formCard, "Nama Lengkap", txtFullname);
        addFormField(formCard, "Username", txtUsername);
        addFormField(formCard, "Password Lama (untuk verifikasi)", txtOldPassword);
        addFormField(formCard, "Password Baru", txtNewPassword);
        addFormField(formCard, "Konfirmasi Password Baru", txtConfirmPassword);

        formCard.add(Box.createVerticalStrut(10));

        JButton btnSave = new RoundedButton("Simpan Perubahan");
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnSave.addActionListener(e -> handleUpdateProfile());
        formCard.add(btnSave);

        centerPanel.add(formCard);

        // 2. RIGHT PANEL: PREMIUM USER CARD
        JPanel infoCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor); // Border
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                
                // Draw a nice soft ambient lighting circle behind the avatar
                g2.setPaint(new RadialGradientPaint(
                        new Point(getWidth() / 2, 80),
                        80f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(250, 88, 106, 40), new Color(0, 0, 0, 0)}
                ));
                g2.fillOval(getWidth() / 2 - 80, 0, 160, 160);
                g2.dispose();
            }
        };
        infoCard.setOpaque(false);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(new Color(37, 37, 39));
        infoCard.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Profile Avatar Representation (Vector Circle with Initials)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 88, 106)); // Accent Red
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                String initials = "";
                if (currentUser != null && currentUser.getFullname() != null && !currentUser.getFullname().isEmpty()) {
                    String[] parts = currentUser.getFullname().split(" ");
                    if (parts.length > 0) initials += parts[0].substring(0, 1).toUpperCase();
                    if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
                } else {
                    initials = "U";
                }
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        avatarPanel.setMaximumSize(new Dimension(80, 80));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblUserFullname = new JLabel("-");
        lblUserFullname.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUserFullname.setForeground(new Color(245, 245, 247));
        lblUserFullname.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblUserUsername = new JLabel("@username");
        lblUserUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUserUsername.setForeground(new Color(161, 161, 170));
        lblUserUsername.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblUserRole = new JLabel("Role: -");
        lblUserRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserRole.setForeground(new Color(250, 88, 106));
        lblUserRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblUserJoined = new JLabel("Bergabung sejak: -");
        lblUserJoined.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblUserJoined.setForeground(new Color(110, 110, 115));
        lblUserJoined.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(avatarPanel);
        infoCard.add(Box.createVerticalStrut(15));
        infoCard.add(lblUserFullname);
        infoCard.add(Box.createVerticalStrut(5));
        infoCard.add(lblUserUsername);
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(lblUserRole);
        infoCard.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(MainFrame.borderColor);
        sep.setMaximumSize(new Dimension(150, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoCard.add(sep);
        infoCard.add(Box.createVerticalStrut(20));
        infoCard.add(lblUserJoined);

        centerPanel.add(infoCard);
        add(centerPanel, BorderLayout.CENTER);

        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            txtFullname.setText(currentUser.getFullname());
            txtUsername.setText(currentUser.getUsername());
            lblUserFullname.setText(currentUser.getFullname());
            lblUserUsername.setText("@" + currentUser.getUsername());
            lblUserRole.setText("Role: " + currentUser.getRole());
            
            if (currentUser.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                lblUserJoined.setText("Bergabung sejak: " + sdf.format(currentUser.getCreatedAt()));
            } else {
                lblUserJoined.setText("Bergabung sejak: -");
            }
        }
    }

    private void handleUpdateProfile() {
        if (currentUser == null) return;

        String fullname = txtFullname.getText().trim();
        String username = txtUsername.getText().trim();
        String oldPassword = new String(txtOldPassword.getPassword()).trim();
        String newPassword = new String(txtNewPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

        if (fullname.isEmpty() || username.isEmpty()) {
            OurIsland.show(this, "Nama dan Username tidak boleh kosong!", OurIsland.IslandType.ERROR);
            return;
        }

        // Verifikasi password lama jika username diubah atau jika password ingin diganti
        boolean isUsernameChanged = !username.equalsIgnoreCase(currentUser.getUsername());
        boolean isChangingPassword = !newPassword.isEmpty() || !confirmPassword.isEmpty();

        if (isUsernameChanged || isChangingPassword) {
            if (oldPassword.isEmpty()) {
                OurIsland.show(this, "Masukkan password lama untuk memverifikasi perubahan sensitif!", OurIsland.IslandType.ERROR);
                return;
            }
            User verified = userDAO.authenticateUser(currentUser.getUsername(), oldPassword);
            if (verified == null) {
                OurIsland.show(this, "Password lama tidak benar!", OurIsland.IslandType.ERROR);
                return;
            }
        }

        // Jalankan Update Profile
        if (isUsernameChanged && userDAO.isUsernameExists(username)) {
            OurIsland.show(this, "Username telah digunakan oleh orang lain!", OurIsland.IslandType.ERROR);
            return;
        }

        if (userDAO.updateProfile(currentUser.getIdUser(), fullname, username)) {
            currentUser.setFullname(fullname);
            currentUser.setUsername(username);
            
            // Password change flow
            if (isChangingPassword) {
                if (newPassword.length() < 4) {
                    OurIsland.show(this, "Password baru minimal 4 karakter!", OurIsland.IslandType.ERROR);
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    OurIsland.show(this, "Konfirmasi password baru tidak cocok!", OurIsland.IslandType.ERROR);
                    return;
                }
                if (userDAO.updatePassword(currentUser.getIdUser(), newPassword, username)) {
                    OurIsland.show(this, "Profil & Password berhasil diperbarui!", OurIsland.IslandType.SUCCESS);
                } else {
                    OurIsland.show(this, "Profil diperbarui tapi perubahan password gagal!", OurIsland.IslandType.ERROR);
                }
            } else {
                OurIsland.show(this, "Profil berhasil diperbarui!", OurIsland.IslandType.SUCCESS);
            }

            txtOldPassword.setText("");
            txtNewPassword.setText("");
            txtConfirmPassword.setText("");
            loadUserData();
            repaint();
        } else {
            OurIsland.show(this, "Gagal memperbarui profil!", OurIsland.IslandType.ERROR);
        }
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

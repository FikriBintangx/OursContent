package com.ourscontent.ui;

import com.ourscontent.dao.UserDAO;
import com.ourscontent.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private JTextField txtFullname, txtUsername, txtId;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JTable table;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;
    private MainFrame mainFrame;
    
    private RoundedButton btnSimpan, btnEdit, btnHapus, btnClear;
    private JPanel formPanel, tableWrapper;
    private JLabel formTitle, tableTitle, titleLabel;

    private Color themeBgColor = new Color(28, 28, 30);
    private Color cardBgColor = new Color(44, 44, 46);
    private Color textPrimaryColor = new Color(245, 245, 247);
    private Color textSecondaryColor = new Color(161, 161, 170);
    private Color borderColor = new Color(58, 58, 60);

    public UserManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(themeBgColor);

        // Header Title
        titleLabel = new JLabel("Manajemen Pengguna");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Left Panel: Form Input
        formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(cardBgColor);
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        formTitle = new JLabel("Form Pengguna");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(textPrimaryColor);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(15));

        // Hidden ID Field
        txtId = new JTextField();
        txtId.setVisible(false);

        // Input Fields
        txtFullname = createStyledTextField();
        txtUsername = createStyledTextField();
        txtPassword = createStyledPasswordField();
        cbRole = new JComboBox<>(new String[]{"MANAGER", "ADMIN"});
        styleComboBox(cbRole);

        addFormField(formPanel, "Nama Lengkap", txtFullname);
        addFormField(formPanel, "Username", txtUsername);
        addFormField(formPanel, "Password (Kosongkan jika tidak diubah)", txtPassword);
        addFormField(formPanel, "Role", cbRole);

        formPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(cardBgColor);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSimpan = new RoundedButton("Simpan");
        btnEdit = new RoundedButton("Edit");
        btnHapus = new RoundedButton("Hapus");
        btnClear = new RoundedButton("Clear");

        btnPanel.add(btnSimpan);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);

        formPanel.add(btnPanel);
        add(formPanel, BorderLayout.WEST);

        // Right Panel: Table of Users
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(themeBgColor);

        tableTitle = new JLabel("Daftar Pengguna Sistem");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(textPrimaryColor);
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "Nama Lengkap", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        tableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBackground(cardBgColor);
        tableWrapper.setBorder(new EmptyBorder(8, 8, 8, 8));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(tableWrapper, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        // Actions
        btnSimpan.addActionListener(e -> saveUser());
        btnEdit.addActionListener(e -> editUser());
        btnClear.addActionListener(e -> clearForm());
        btnHapus.addActionListener(e -> deleteUser());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtUsername.setText(table.getValueAt(row, 1).toString());
                txtFullname.setText(table.getValueAt(row, 2).toString());
                cbRole.setSelectedItem(table.getValueAt(row, 3).toString());
                txtPassword.setText(""); // Reset password field for edits
            }
        });

        loadTableData();
        updateTheme(false); // Apply initial theme
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<User> list = userDAO.getAllUsers();
        for (User u : list) {
            tableModel.addRow(new Object[]{
                    u.getIdUser(),
                    u.getUsername(),
                    u.getFullname(),
                    u.getRole()
            });
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtFullname.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(0);
        table.clearSelection();
    }

    private void saveUser() {
        String fullname = txtFullname.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString();

        if (fullname.isEmpty() || username.isEmpty()) {
            OurIsland.show(this, "Nama Lengkap dan Username tidak boleh kosong!", OurIsland.IslandType.ERROR);
            return;
        }

        if (!txtId.getText().isEmpty()) {
            OurIsland.show(this, "Bersihkan form terlebih dahulu untuk menambah pengguna baru!", OurIsland.IslandType.ERROR);
            return;
        }

        if (password.isEmpty()) {
            OurIsland.show(this, "Password diperlukan untuk pengguna baru!", OurIsland.IslandType.ERROR);
            return;
        }

        if (userDAO.isUsernameExists(username)) {
            OurIsland.show(this, "Username sudah digunakan!", OurIsland.IslandType.ERROR);
            return;
        }

        if (userDAO.registerUser(username, password, fullname, role)) {
            OurIsland.show(this, "Pengguna berhasil ditambahkan!", OurIsland.IslandType.SUCCESS);
            clearForm();
            loadTableData();
        } else {
            OurIsland.show(this, "Gagal menambahkan pengguna!", OurIsland.IslandType.ERROR);
        }
    }

    private void editUser() {
        if (txtId.getText().isEmpty()) {
            OurIsland.show(this, "Pilih baris pada tabel terlebih dahulu untuk diedit!", OurIsland.IslandType.ERROR);
            return;
        }

        String fullname = txtFullname.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString();

        if (fullname.isEmpty() || username.isEmpty()) {
            OurIsland.show(this, "Nama Lengkap dan Username tidak boleh kosong!", OurIsland.IslandType.ERROR);
            return;
        }

        Long idUser = Long.parseLong(txtId.getText());
        boolean success;
        if (password.isEmpty()) {
            success = userDAO.updateUser(idUser, username, fullname, role);
        } else {
            success = userDAO.updateUserWithPassword(idUser, username, password, fullname, role);
        }

        if (success) {
            OurIsland.show(this, "Data pengguna berhasil diperbarui!", OurIsland.IslandType.SUCCESS);
            clearForm();
            loadTableData();
        } else {
            OurIsland.show(this, "Gagal memperbarui pengguna!", OurIsland.IslandType.ERROR);
        }
    }

    private void deleteUser() {
        if (txtId.getText().isEmpty()) {
            OurIsland.show(this, "Pilih pengguna pada tabel untuk dihapus!", OurIsland.IslandType.ERROR);
            return;
        }

        Long idUser = Long.parseLong(txtId.getText());
        String username = txtUsername.getText();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus pengguna '" + username + "'?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(idUser)) {
                OurIsland.show(this, "Pengguna berhasil dihapus!", OurIsland.IslandType.SUCCESS);
                clearForm();
                loadTableData();
            } else {
                OurIsland.show(this, "Gagal menghapus pengguna!", OurIsland.IslandType.ERROR);
            }
        }
    }

    public void updateTheme(boolean isLight) {
        if (isLight) {
            themeBgColor = new Color(242, 242, 247);
            cardBgColor = new Color(255, 255, 255);
            textPrimaryColor = new Color(28, 28, 30);
            textSecondaryColor = new Color(110, 110, 115);
            borderColor = new Color(229, 229, 234);
        } else {
            themeBgColor = new Color(28, 28, 30);
            cardBgColor = new Color(44, 44, 46);
            textPrimaryColor = new Color(245, 245, 247);
            textSecondaryColor = new Color(161, 161, 170);
            borderColor = new Color(58, 58, 60);
        }
        
        setBackground(themeBgColor);
        
        if (formPanel != null) {
            formPanel.setBackground(cardBgColor);
        }
        if (tableWrapper != null) {
            tableWrapper.setBackground(cardBgColor);
        }
        
        if (titleLabel != null) titleLabel.setForeground(textPrimaryColor);
        if (formTitle != null) formTitle.setForeground(textPrimaryColor);
        if (tableTitle != null) tableTitle.setForeground(textPrimaryColor);

        // Style the input fields
        for (JTextField tf : new JTextField[]{txtFullname, txtUsername, txtPassword}) {
            if (tf != null) {
                tf.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
                tf.setForeground(textPrimaryColor);
                tf.setCaretColor(isLight ? Color.BLACK : Color.WHITE);
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(borderColor, 1),
                        new EmptyBorder(5, 7, 5, 7)
                ));
            }
        }

        if (cbRole != null) {
            cbRole.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
            cbRole.setForeground(textPrimaryColor);
        }

        // Style Table
        if (table != null) {
            table.setBackground(cardBgColor);
            table.setForeground(textPrimaryColor);
            table.setGridColor(borderColor);
            table.getTableHeader().setBackground(isLight ? new Color(225, 225, 228) : new Color(58, 58, 60));
            table.getTableHeader().setForeground(textPrimaryColor);
            table.setSelectionBackground(isLight ? new Color(210, 210, 215) : new Color(52, 52, 56));
            table.setSelectionForeground(isLight ? Color.BLACK : new Color(250, 88, 106));
        }

        // Style Form Labels
        if (formPanel != null) {
            for (Component c : formPanel.getComponents()) {
                if (c instanceof JLabel && c != formTitle) {
                    c.setForeground(textSecondaryColor);
                }
            }
        }
        
        repaint();
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return pf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    private void addFormField(JPanel panel, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(textSecondaryColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(component);
        panel.add(Box.createVerticalStrut(10));
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(25);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}

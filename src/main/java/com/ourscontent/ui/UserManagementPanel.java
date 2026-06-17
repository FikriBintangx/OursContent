package com.ourscontent.ui;

import com.ourscontent.dao.UserDAO;
import com.ourscontent.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends BasePanel {

    private JTextField txtFullname, txtUsername, txtId;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JTable table;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;

    private RoundedPanel formPanel, tableWrapper;
    private JLabel formTitle, tableTitle, titleLabel;

    public UserManagementPanel(MainFrame mainFrame) {
        userDAO = new UserDAO();

        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Manajemen Pengguna");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(MainFrame.textPrimaryColor);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        formPanel = new RoundedPanel(new BoxLayout(null, BoxLayout.Y_AXIS), 18);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(MainFrame.cardBgColor);
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        formTitle = makeCardTitle("Form Pengguna");
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(15));

        txtId = new JTextField();
        txtId.setVisible(false);

        txtFullname = createStyledTextField();
        txtUsername = createStyledTextField();
        txtPassword = createStyledPasswordField();
        cbRole = new JComboBox<>(new String[]{"MANAGER", "ADMIN"});
        styleComboBox(cbRole);

        addFormField(formPanel, "Nama Lengkap", txtFullname);
        addFormField(formPanel, "Username", txtUsername);
        addFormField(formPanel, "Password (kosongkan jika tidak diubah)", txtPassword);
        addFormField(formPanel, "Role", cbRole);

        formPanel.add(Box.createVerticalStrut(15));

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(MainFrame.cardBgColor);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton btnSimpan = new RoundedButton("Simpan");
        RoundedButton btnEdit   = new RoundedButton("Edit");
        RoundedButton btnHapus  = new RoundedButton("Hapus");
        RoundedButton btnClear  = new RoundedButton("Clear");

        btnPanel.add(btnSimpan);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel);

        add(formPanel, BorderLayout.WEST);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(MainFrame.mainBgColor);

        tableTitle = makeSectionTitle("Daftar Pengguna Sistem");
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        tableModel = createReadOnlyTableModel(new String[]{"ID", "Username", "Nama Lengkap", "Role"});

        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        tableWrapper = new RoundedPanel(18);
        tableWrapper.setBackground(MainFrame.cardBgColor);
        tableWrapper.setBorder(new EmptyBorder(8, 8, 8, 8));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(tableWrapper, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

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
                txtPassword.setText("");
            }
        });

        loadTableData();
        updateTheme(false);
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<User> list = userDAO.getAllUsers();
        for (User u : list) {
            tableModel.addRow(new Object[]{u.getIdUser(), u.getUsername(), u.getFullname(), u.getRole()});
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
        boolean ok = password.isEmpty()
                ? userDAO.updateUser(idUser, username, fullname, role)
                : userDAO.updateUserWithPassword(idUser, username, password, fullname, role);

        if (ok) {
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
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus pengguna '" + txtUsername.getText() + "'?",
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
        Color bg        = isLight ? new Color(242, 242, 247) : new Color(28, 28, 30);
        Color card      = isLight ? new Color(255, 255, 255) : new Color(44, 44, 46);
        Color textMain  = isLight ? new Color(28, 28, 30)    : new Color(245, 245, 247);
        Color textSub   = isLight ? new Color(110, 110, 115) : new Color(161, 161, 170);
        Color border    = isLight ? new Color(229, 229, 234) : new Color(58, 58, 60);
        Color inputBg   = isLight ? new Color(245, 245, 247) : new Color(28, 28, 30);

        setBackground(bg);
        if (formPanel  != null) formPanel.setBackground(card);
        if (tableWrapper != null) tableWrapper.setBackground(card);
        if (titleLabel != null) titleLabel.setForeground(textMain);
        if (formTitle  != null) formTitle.setForeground(textMain);
        if (tableTitle != null) tableTitle.setForeground(textMain);

        for (JTextField tf : new JTextField[]{txtFullname, txtUsername, txtPassword}) {
            if (tf == null) continue;
            tf.setBackground(inputBg);
            tf.setForeground(textMain);
            tf.setCaretColor(isLight ? Color.BLACK : Color.WHITE);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(border, 1),
                    new EmptyBorder(5, 7, 5, 7)
            ));
        }

        if (cbRole != null) {
            cbRole.setBackground(inputBg);
            cbRole.setForeground(textMain);
        }

        if (table != null) {
            table.setBackground(card);
            table.setForeground(textMain);
            table.setGridColor(border);
            table.getTableHeader().setBackground(isLight ? new Color(225, 225, 228) : new Color(58, 58, 60));
            table.getTableHeader().setForeground(textMain);
            table.setSelectionBackground(isLight ? new Color(210, 210, 215) : new Color(52, 52, 56));
            table.setSelectionForeground(isLight ? Color.BLACK : new Color(250, 88, 106));
        }

        if (formPanel != null) {
            for (Component c : formPanel.getComponents()) {
                if (c instanceof JLabel && c != formTitle) c.setForeground(textSub);
            }
        }

        repaint();
    }
}

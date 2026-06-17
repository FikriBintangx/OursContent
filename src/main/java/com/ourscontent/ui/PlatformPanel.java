package com.ourscontent.ui;

import com.ourscontent.dao.PlatformDAO;
import com.ourscontent.model.Platform;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlatformPanel extends JPanel {

    private JTextField txtNama, txtKategori, txtId;
    private JComboBox<String> cbStatus;
    private JTable table;
    private DefaultTableModel tableModel;
    private PlatformDAO dao;

    public PlatformPanel() {
        dao = new PlatformDAO();
        dao = new PlatformDAO();
        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(28, 28, 30)); // Background Utama

        // Left Panel: Form Input
        JPanel formPanel = new JPanel() {
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
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(44, 44, 46)); // Card Background
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel formTitle = new JLabel("Form Platform");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(new Color(245, 245, 247)); // Primary Text
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(15));

        // ID Field (hidden/read-only)
        txtId = new JTextField();
        txtId.setVisible(false);

        // Fields
        txtNama = createStyledTextField();
        txtKategori = createStyledTextField();
        cbStatus = new JComboBox<>(new String[]{"Aktif", "Tidak Aktif"});
        styleComboBox(cbStatus);

        addFormField(formPanel, "Nama Platform", txtNama);
        addFormField(formPanel, "Kategori", txtKategori);
        addFormField(formPanel, "Status", cbStatus);

        formPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(new Color(44, 44, 46));
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSimpan = createStyledButton("Simpan", new Color(16, 185, 129));
        JButton btnEdit = createStyledButton("Edit", new Color(58, 58, 60));
        JButton btnHapus = createStyledButton("Hapus", new Color(250, 88, 106));
        JButton btnClear = createStyledButton("Clear", new Color(58, 58, 60));

        btnPanel.add(btnSimpan);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);

        formPanel.add(btnPanel);
        add(formPanel, BorderLayout.WEST);

        // Right Panel: Table Platform
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(new Color(28, 28, 30));

        JLabel tableTitle = new JLabel("Daftar Platform");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(245, 245, 247));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Nama Platform", "Kategori", "Status"};
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

        JPanel tableWrapper = new JPanel(new BorderLayout()) {
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
        tableWrapper.setOpaque(false);
        tableWrapper.setBackground(new Color(44, 44, 46));
        tableWrapper.setBorder(new EmptyBorder(8, 8, 8, 8));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(tableWrapper, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        // Events
        btnSimpan.addActionListener(e -> {
            if (validateForm()) {
                Platform p = new Platform(null, txtNama.getText().trim(), txtKategori.getText().trim(), cbStatus.getSelectedItem().toString());
                if (dao.insertPlatform(p)) {
                    OurIsland.show(this, "Platform berhasil disimpan!", OurIsland.IslandType.SUCCESS);
                    clearForm();
                    loadTableData();
                }
            }
        });

        btnEdit.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                OurIsland.show(this, "Pilih baris pada tabel untuk diedit!", OurIsland.IslandType.ERROR);
                return;
            }
            if (validateForm()) {
                Platform p = new Platform(Long.parseLong(txtId.getText()), txtNama.getText().trim(), txtKategori.getText().trim(), cbStatus.getSelectedItem().toString());
                if (dao.updatePlatform(p)) {
                    OurIsland.show(this, "Platform berhasil diperbarui!", OurIsland.IslandType.SUCCESS);
                    clearForm();
                    loadTableData();
                }
            }
        });

        btnHapus.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                OurIsland.show(this, "Pilih baris pada tabel untuk dihapus!", OurIsland.IslandType.ERROR);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus platform ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deletePlatform(Long.parseLong(txtId.getText()))) {
                    OurIsland.show(this, "Platform berhasil dihapus!", OurIsland.IslandType.SUCCESS);
                    clearForm();
                    loadTableData();
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtNama.setText(table.getValueAt(row, 1).toString());
                txtKategori.setText(table.getValueAt(row, 2).toString());
                cbStatus.setSelectedItem(table.getValueAt(row, 3).toString());
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Platform> list = dao.getAllPlatforms();
        for (Platform p : list) {
            tableModel.addRow(new Object[]{p.getIdPlatform(), p.getNamaPlatform(), p.getKategori(), p.getStatus()});
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtNama.setText("");
        txtKategori.setText("");
        cbStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    private boolean validateForm() {
        if (txtNama.getText().trim().isEmpty()) {
            OurIsland.show(this, "Nama Platform tidak boleh kosong!", OurIsland.IslandType.ERROR);
            return false;
        }
        if (txtKategori.getText().trim().isEmpty()) {
            OurIsland.show(this, "Kategori tidak boleh kosong!", OurIsland.IslandType.ERROR);
            return false;
        }
        return true;
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

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(new Color(28, 28, 30));
        cb.setForeground(new Color(245, 245, 247));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    private JButton createStyledButton(String text, Color bg) {
        return new RoundedButton(text);
    }

    private void addFormField(JPanel panel, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(161, 161, 170)); // Secondary Text
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(component);
        panel.add(Box.createVerticalStrut(10));
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(44, 44, 46));
        t.setForeground(new Color(245, 245, 247));
        t.setGridColor(new Color(58, 58, 60));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(25);
        t.getTableHeader().setBackground(new Color(58, 58, 60));
        t.getTableHeader().setForeground(new Color(245, 245, 247));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setSelectionBackground(new Color(52, 52, 56));
        t.setSelectionForeground(new Color(250, 88, 106));
    }
}

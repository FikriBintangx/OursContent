package com.ourscontent.ui;

import com.ourscontent.dao.PlatformDAO;
import com.ourscontent.model.Platform;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlatformPanel extends BasePanel {

    private JTextField txtNama, txtKategori, txtId;
    private JComboBox<String> cbStatus;
    private JTable table;
    private DefaultTableModel tableModel;
    private PlatformDAO dao;

    public PlatformPanel() {
        dao = new PlatformDAO();
        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        RoundedPanel formPanel = new RoundedPanel(new BoxLayout(null, BoxLayout.Y_AXIS), 18);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(MainFrame.cardBgColor);
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        formPanel.add(makeCardTitle("Form Platform"));
        formPanel.add(Box.createVerticalStrut(15));

        txtId = new JTextField();
        txtId.setVisible(false);

        txtNama = createStyledTextField();
        txtKategori = createStyledTextField();
        cbStatus = new JComboBox<>(new String[]{"Aktif", "Tidak Aktif"});
        styleComboBox(cbStatus);

        addFormField(formPanel, "Nama Platform", txtNama);
        addFormField(formPanel, "Kategori", txtKategori);
        addFormField(formPanel, "Status", cbStatus);

        formPanel.add(Box.createVerticalStrut(15));

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(MainFrame.cardBgColor);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSimpan = createStyledButton("Simpan");
        JButton btnEdit   = createStyledButton("Edit");
        JButton btnHapus  = createStyledButton("Hapus");
        JButton btnClear  = createStyledButton("Clear");

        btnPanel.add(btnSimpan);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel);

        add(formPanel, BorderLayout.WEST);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(MainFrame.mainBgColor);
        tableContainer.add(makeSectionTitle("Daftar Platform"), BorderLayout.NORTH);

        tableModel = createReadOnlyTableModel(new String[]{"ID", "Nama Platform", "Kategori", "Status"});

        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        RoundedPanel tableWrapper = new RoundedPanel(18);
        tableWrapper.setBackground(MainFrame.cardBgColor);
        tableWrapper.setBorder(new EmptyBorder(8, 8, 8, 8));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(tableWrapper, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

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
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus platform ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && dao.deletePlatform(Long.parseLong(txtId.getText()))) {
                OurIsland.show(this, "Platform berhasil dihapus!", OurIsland.IslandType.SUCCESS);
                clearForm();
                loadTableData();
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
}

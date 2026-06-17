package com.ourscontent.ui;

import com.ourscontent.dao.ContentDAO;
import com.ourscontent.dao.PerformaDAO;
import com.ourscontent.model.Content;
import com.ourscontent.model.Performa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class PerformaPanel extends JPanel {

    private JComboBox<Content> cbContent;
    private JTextField txtTanggal, txtViews, txtLikes, txtKomentar, txtEngagement, txtId;
    private JTable table;
    private DefaultTableModel tableModel;
    private PerformaDAO performaDao;
    private ContentDAO contentDao;

    public PerformaPanel() {
        performaDao = new PerformaDAO();
        contentDao = new ContentDAO();

        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(28, 28, 30));

        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(44, 44, 46));
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel formTitle = new JLabel("Form Performa");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(new Color(245, 245, 247));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(15));

        txtId = new JTextField();
        txtId.setVisible(false);

        cbContent = new JComboBox<>();
        styleComboBox(cbContent);

        txtTanggal = createStyledTextField();
        txtViews = createStyledTextField();
        txtLikes = createStyledTextField();
        txtKomentar = createStyledTextField();

        txtEngagement = createStyledTextField();
        txtEngagement.setEditable(false);
        txtEngagement.setBackground(new Color(28, 28, 30));
        txtEngagement.setForeground(new Color(250, 88, 106));

        // pasang tanggal default hari ini
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtTanggal.setText(sdf.format(new java.util.Date()));

        addFormField(formPanel, "Konten (Published)", cbContent);
        addFormField(formPanel, "Tanggal Posting (YYYY-MM-DD)", txtTanggal);
        addFormField(formPanel, "Views", txtViews);
        addFormField(formPanel, "Likes", txtLikes);
        addFormField(formPanel, "Komentar", txtKomentar);
        addFormField(formPanel, "Engagement (Likes + Komentar)", txtEngagement);

        formPanel.add(Box.createVerticalStrut(15));

        // hitung otomatis engagement pas input berubah
        DocumentListener engagementCalc = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateEngagement(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateEngagement(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateEngagement(); }
        };
        txtLikes.getDocument().addDocumentListener(engagementCalc);
        txtKomentar.getDocument().addDocumentListener(engagementCalc);

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

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(new Color(28, 28, 30));

        JLabel tableTitle = new JLabel("Daftar Performa Konten");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(245, 245, 247));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"", "Judul Konten", "Platform", "Tanggal", "Views", "Likes", "Komentar", "Engagement"};
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
                g2.setColor(MainFrame.borderColor);
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
                Performa p = new Performa();
                Content selectedCont = (Content) cbContent.getSelectedItem();
                p.setIdContent(selectedCont.getIdContent());
                p.setTanggalPosting(Date.valueOf(txtTanggal.getText().trim()));
                p.setViews(Integer.parseInt(txtViews.getText().trim()));
                p.setLikes(Integer.parseInt(txtLikes.getText().trim()));
                p.setKomentar(Integer.parseInt(txtKomentar.getText().trim()));

                if (performaDao.insertPerforma(p)) {
                    OurIsland.show(this, "Data performa berhasil disimpan!", OurIsland.IslandType.SUCCESS);
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
                Performa p = new Performa();
                p.setIdPerforma(Long.parseLong(txtId.getText()));
                Content selectedCont = (Content) cbContent.getSelectedItem();
                p.setIdContent(selectedCont.getIdContent());
                p.setTanggalPosting(Date.valueOf(txtTanggal.getText().trim()));
                p.setViews(Integer.parseInt(txtViews.getText().trim()));
                p.setLikes(Integer.parseInt(txtLikes.getText().trim()));
                p.setKomentar(Integer.parseInt(txtKomentar.getText().trim()));

                if (performaDao.updatePerforma(p)) {
                    OurIsland.show(this, "Data performa berhasil diperbarui!", OurIsland.IslandType.SUCCESS);
                    clearForm();
                    loadTableData();
                }
            }
        });

        btnHapus.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                OurIsland.show(this, "Pilih baris pada tabel untuk dihapus!", OurIsland.IslandType.ERROR);
                return;
            }
            
            String confirmMsg = selectedRows.length == 1 
                ? "Apakah Anda yakin ingin menghapus data performa ini?" 
                : "Apakah Anda yakin ingin menghapus " + selectedRows.length + " data performa yang dipilih?";
                
            int confirm = JOptionPane.showConfirmDialog(this, confirmMsg, "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int successCount = 0;
                for (int row : selectedRows) {
                    long id = Long.parseLong(table.getValueAt(row, 0).toString());
                    if (performaDao.deletePerforma(id)) {
                        successCount++;
                    }
                }
                if (successCount > 0) {
                    OurIsland.show(this, successCount + " data performa berhasil dihapus!", OurIsland.IslandType.SUCCESS);
                    clearForm();
                    loadTableData();
                } else {
                    OurIsland.show(this, "Gagal menghapus data performa!", OurIsland.IslandType.ERROR);
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtTanggal.setText(table.getValueAt(row, 3).toString());
                txtViews.setText(table.getValueAt(row, 4).toString());
                txtLikes.setText(table.getValueAt(row, 5).toString());
                txtKomentar.setText(table.getValueAt(row, 6).toString());

                String contentTitle = table.getValueAt(row, 1).toString();
                for (int i = 0; i < cbContent.getItemCount(); i++) {
                    Content c = cbContent.getItemAt(i);
                    if (c.getJudulContent().equals(contentTitle)) {
                        cbContent.setSelectedItem(c);
                        break;
                    }
                }
            }
        });

        loadPublishedContents();
        loadTableData();
    }

    private void updateEngagement() {
        try {
            int likes = txtLikes.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtLikes.getText().trim());
            int komentar = txtKomentar.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtKomentar.getText().trim());
            txtEngagement.setText(String.valueOf(likes + komentar));
        } catch (NumberFormatException e) {
            txtEngagement.setText("0 (Input salah)");
        }
    }

    public void loadPublishedContents() {
        cbContent.removeAllItems();
        List<Content> list = contentDao.getPublishedContents();
        for (Content c : list) {
            cbContent.addItem(c);
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Performa> list = performaDao.getAllPerformas();
        for (Performa p : list) {
            tableModel.addRow(new Object[]{
                    p.getIdPerforma(),
                    p.getJudulContent(),
                    p.getNamaPlatform(),
                    p.getTanggalPosting(),
                    p.getViews(),
                    p.getLikes(),
                    p.getKomentar(),
                    p.getEngagement()
            });
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtViews.setText("");
        txtLikes.setText("");
        txtKomentar.setText("");
        txtEngagement.setText("0");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtTanggal.setText(sdf.format(new java.util.Date()));
        if (cbContent.getItemCount() > 0) cbContent.setSelectedIndex(0);
        table.clearSelection();
    }

    private boolean validateForm() {
        if (cbContent.getSelectedItem() == null) {
            OurIsland.show(this, "Silakan buat dan Publish konten terlebih dahulu!", OurIsland.IslandType.ERROR);
            return false;
        }
        try {
            Date.valueOf(txtTanggal.getText().trim());
        } catch (IllegalArgumentException e) {
            OurIsland.show(this, "Format tanggal salah! Gunakan format YYYY-MM-DD.", OurIsland.IslandType.ERROR);
            return false;
        }
        try {
            Integer.parseInt(txtViews.getText().trim());
            Integer.parseInt(txtLikes.getText().trim());
            Integer.parseInt(txtKomentar.getText().trim());
        } catch (NumberFormatException e) {
            OurIsland.show(this, "Views, Likes, dan Komentar harus berupa angka!", OurIsland.IslandType.ERROR);
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
        label.setForeground(new Color(161, 161, 170));
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
        t.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        t.getColumnModel().getColumn(0).setCellRenderer(new BulletIdRenderer());
        t.getColumnModel().getColumn(0).setPreferredWidth(30);
        t.getColumnModel().getColumn(0).setMaxWidth(30);
        t.getColumnModel().getColumn(0).setMinWidth(30);
    }

    private static class BulletIdRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int diameter = 10;
                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 2;
                    
                    if (table.isRowSelected(row)) {
                        g2.setColor(new Color(250, 88, 106));
                        g2.fillOval(x, y, diameter, diameter);
                    } else {
                        g2.setColor(new Color(28, 28, 30));
                        g2.fillOval(x, y, diameter, diameter);
                        g2.setColor(new Color(58, 58, 60));
                        g2.drawOval(x, y, diameter, diameter);
                    }
                    g2.dispose();
                }
            };
            panel.setBackground(table.getBackground());
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            }
            return panel;
        }
    }
}

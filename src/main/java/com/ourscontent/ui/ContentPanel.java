package com.ourscontent.ui;

import com.ourscontent.dao.ContentDAO;
import com.ourscontent.dao.PlatformDAO;
import com.ourscontent.model.Content;
import com.ourscontent.model.Platform;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ContentPanel extends BasePanel {

    private JTextField txtJudul, txtId, txtGambar;
    private JPanel previewContainer;
    private JTextArea txtDeskripsi;
    private JComboBox<String> cbKategori, cbStatus;
    private JComboBox<Platform> cbPlatform;
    private JTable table;
    private DefaultTableModel tableModel;
    private ContentDAO contentDao;
    private PlatformDAO platformDao;

    public ContentPanel() {
        contentDao = new ContentDAO();
        platformDao = new PlatformDAO();
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topRow = new JPanel(new BorderLayout(15, 0));
        topRow.setOpaque(false);

        previewContainer = new JPanel(new GridLayout(1, 3, 10, 0));
        previewContainer.setOpaque(false);
        previewContainer.setPreferredSize(new Dimension(380, 0));
        previewContainer.setMaximumSize(new Dimension(380, 200));
        topRow.add(previewContainer, BorderLayout.WEST);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.add(makeSectionTitle("Daftar Konten"), BorderLayout.NORTH);

        tableModel = createReadOnlyTableModel(new String[]{"", "Judul Konten", "Platform", "Kategori", "Status", "Deskripsi", "Gambar"});

        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        RoundedPanel tableWrapper = new RoundedPanel(18);
        tableWrapper.setBackground(MainFrame.cardBgColor);
        tableWrapper.setBorder(new EmptyBorder(8, 8, 8, 8));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(tableWrapper, BorderLayout.CENTER);
        topRow.add(tableContainer, BorderLayout.CENTER);

        JPanel topRowWrapper = new JPanel(new BorderLayout());
        topRowWrapper.setOpaque(false);
        topRowWrapper.setPreferredSize(new Dimension(0, 200));
        topRowWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        topRowWrapper.add(topRow, BorderLayout.CENTER);
        add(topRowWrapper, BorderLayout.CENTER);

        RoundedPanel formCard = new RoundedPanel(new BorderLayout(20, 10), 18);
        formCard.setBackground(MainFrame.cardBgColor);
        formCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        formCard.setPreferredSize(new Dimension(0, 250));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        txtId = new JTextField();
        txtId.setVisible(false);

        txtJudul = createStyledTextField();
        cbKategori = new JComboBox<>(new String[]{"Edukasi", "Promosi", "Hiburan", "Review", "Tips"});
        styleComboBox(cbKategori);

        cbStatus = new JComboBox<>(new String[]{"Draft", "Editing", "Published"});
        styleComboBox(cbStatus);

        cbPlatform = new JComboBox<>();
        styleComboBox(cbPlatform);

        txtDeskripsi = new JTextArea(1, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setBackground(MainFrame.mainBgColor);
        txtDeskripsi.setForeground(MainFrame.textPrimaryColor);
        txtDeskripsi.setCaretColor(MainFrame.textPrimaryColor);
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane descScroll = new JScrollPane(txtDeskripsi);
        descScroll.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MainFrame.borderColor, 1),
                new EmptyBorder(2, 2, 2, 2)
        ));
        descScroll.setPreferredSize(new Dimension(100, 60));
        descScroll.setMinimumSize(new Dimension(100, 60));

        txtGambar = createStyledTextField();
        txtGambar.setEditable(false);
        JButton btnPilih = createStyledButton("Pilih");
        btnPilih.setPreferredSize(new Dimension(70, 30));

        JPanel gambarInputPanel = new JPanel(new BorderLayout(5, 0));
        gambarInputPanel.setOpaque(false);
        gambarInputPanel.add(txtGambar, BorderLayout.CENTER);
        gambarInputPanel.add(btnPilih, BorderLayout.EAST);

        addFormFieldGbc(fieldsPanel, "Judul Konten",   txtJudul,        0, 0);
        addFormFieldGbc(fieldsPanel, "Kategori",       cbKategori,      1, 0);
        addFormFieldGbc(fieldsPanel, "Platform",       cbPlatform,      2, 0);
        addFormFieldGbc(fieldsPanel, "Status",         cbStatus,        0, 1);
        addFormFieldGbc(fieldsPanel, "Deskripsi",      descScroll,      1, 1);
        addFormFieldGbc(fieldsPanel, "Gambar Konten",  gambarInputPanel, 2, 1);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(240, 120));

        JButton btnSimpan = createStyledButton("Simpan");
        JButton btnEdit   = createStyledButton("Edit");
        JButton btnHapus  = createStyledButton("Hapus");
        JButton btnClear  = createStyledButton("Clear");

        btnPanel.add(btnSimpan);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);

        formCard.add(btnPanel, BorderLayout.EAST);
        add(formCard, BorderLayout.SOUTH);

        btnPilih.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

            java.io.File[] selectedFiles = chooser.getSelectedFiles();
            String current = txtGambar.getText().trim();
            StringBuilder sb = new StringBuilder(current);
            for (java.io.File f : selectedFiles) {
                String path = f.getAbsolutePath();
                if (current.contains(path)) continue;
                if (sb.length() > 0) sb.append(";");
                sb.append(path);
            }
            String newPaths = sb.toString();
            txtGambar.setText(newPaths);
            showPreviewImages(newPaths);
            if (!txtId.getText().isEmpty()) autoSaveImage(newPaths);
        });

        btnSimpan.addActionListener(e -> {
            if (!validateForm()) return;
            Content c = buildContentFromForm();
            if (contentDao.insertContent(c)) {
                OurIsland.show(this, "Konten berhasil disimpan!", OurIsland.IslandType.SUCCESS);
                clearForm();
                loadTableData();
            }
        });

        btnEdit.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                OurIsland.show(this, "Pilih baris pada tabel untuk diedit!", OurIsland.IslandType.ERROR);
                return;
            }
            if (!validateForm()) return;
            Content c = buildContentFromForm();
            c.setIdContent(Long.parseLong(txtId.getText()));
            if (contentDao.updateContent(c)) {
                OurIsland.show(this, "Konten berhasil diperbarui!", OurIsland.IslandType.SUCCESS);
                clearForm();
                loadTableData();
            }
        });

        btnHapus.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if (rows.length == 0) {
                OurIsland.show(this, "Pilih baris pada tabel untuk dihapus!", OurIsland.IslandType.ERROR);
                return;
            }
            String msg = rows.length == 1
                    ? "Yakin ingin menghapus konten ini?"
                    : "Yakin ingin menghapus " + rows.length + " konten yang dipilih?";
            if (JOptionPane.showConfirmDialog(this, msg, "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

            int deleted = 0;
            for (int row : rows) {
                long id = Long.parseLong(table.getValueAt(row, 0).toString());
                deletePerformaRef(id);
                if (contentDao.deleteContent(id)) deleted++;
            }
            if (deleted > 0) {
                OurIsland.show(this, deleted + " konten berhasil dihapus!", OurIsland.IslandType.SUCCESS);
                clearForm();
                loadTableData();
            } else {
                OurIsland.show(this, "Gagal menghapus konten!", OurIsland.IslandType.ERROR);
            }
        });

        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            txtId.setText(table.getValueAt(row, 0).toString());
            txtJudul.setText(table.getValueAt(row, 1).toString());
            cbKategori.setSelectedItem(table.getValueAt(row, 3).toString());
            cbStatus.setSelectedItem(table.getValueAt(row, 4).toString());
            txtDeskripsi.setText(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");
            String path = table.getValueAt(row, 6) != null ? table.getValueAt(row, 6).toString() : "";
            txtGambar.setText(path);
            showPreviewImages(path);
            String platName = table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "";
            for (int i = 0; i < cbPlatform.getItemCount(); i++) {
                if (cbPlatform.getItemAt(i).getNamaPlatform().equals(platName)) {
                    cbPlatform.setSelectedIndex(i);
                    break;
                }
            }
        });

        table.getColumnModel().getColumn(0).setCellRenderer(new BulletIdRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.getColumnModel().getColumn(0).setMinWidth(30);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        loadPlatforms();
        loadTableData();
    }

    private Content buildContentFromForm() {
        Content c = new Content();
        c.setJudulContent(txtJudul.getText().trim());
        c.setKategori(cbKategori.getSelectedItem().toString());
        c.setDeskripsi(txtDeskripsi.getText().trim());
        c.setStatus(cbStatus.getSelectedItem().toString());
        c.setGambar(txtGambar.getText().trim());
        Platform plat = (Platform) cbPlatform.getSelectedItem();
        if (plat != null) c.setIdPlatform(plat.getIdPlatform());
        return c;
    }

    private void deletePerformaRef(long idContent) {
        // Remove performa records first to avoid FK constraint violation
        try (java.sql.Connection conn = com.ourscontent.db.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM performa WHERE id_content = ?")) {
            ps.setLong(1, idContent);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showPreviewImages(String paths) {
        previewContainer.removeAll();
        String[] parts = (paths != null && !paths.trim().isEmpty()) ? paths.split(";") : new String[0];

        for (int i = 0; i < 3; i++) {
            if (i < parts.length && !parts[i].trim().isEmpty()) {
                previewContainer.add(buildImageCard(parts[i].trim()));
            } else {
                previewContainer.add(buildPlaceholderCard());
            }
        }
        previewContainer.revalidate();
        previewContainer.repaint();
    }

    private JPanel buildImageCard(String path) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(5, 5), 18);
        card.setBackground(MainFrame.cardBgColor);
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        try {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                Image img = new ImageIcon(path).getImage();
                imgLabel.addComponentListener(new java.awt.event.ComponentAdapter() {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent evt) {
                        int w = imgLabel.getWidth(), h = imgLabel.getHeight();
                        int iw = img.getWidth(null), ih = img.getHeight(null);
                        if (w > 0 && h > 0 && iw > 0 && ih > 0) {
                            double ratio = Math.min((double) w / iw, (double) h / ih);
                            Image scaled = img.getScaledInstance((int)(iw * ratio), (int)(ih * ratio), Image.SCALE_SMOOTH);
                            imgLabel.setIcon(new ImageIcon(scaled));
                        }
                    }
                });
            } else {
                imgLabel.setText("<html><center>Gambar<br>tidak ditemukan</center></html>");
                imgLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                imgLabel.setForeground(new Color(180, 83, 9));
            }
        } catch (Exception e) {
            imgLabel.setText("<html><center>Preview Error</center></html>");
            imgLabel.setForeground(new Color(239, 68, 68));
        }

        JButton btnDel = new JButton("X") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 68, 68));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("X", (getWidth() - fm.stringWidth("X")) / 2, ((getHeight() - fm.getHeight()) / 2) + fm.getAscent());
                g2.dispose();
            }
        };
        btnDel.setBorderPainted(false);
        btnDel.setContentAreaFilled(false);
        btnDel.setFocusPainted(false);
        btnDel.setOpaque(false);
        btnDel.setPreferredSize(new Dimension(20, 20));
        btnDel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDel.addActionListener(ev -> removeImage(path));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        topBar.add(btnDel);

        card.add(topBar, BorderLayout.NORTH);
        card.add(imgLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildPlaceholderCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainFrame.cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor);
                float[] dash = {6f, 4f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, dash, 0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lbl = new JLabel("<html><center>Preview<br>Gambar</center></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(MainFrame.textSecondaryColor);
        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    private void removeImage(String pathToRemove) {
        String current = txtGambar.getText().trim();
        if (current.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        for (String p : current.split(";")) {
            if (p.trim().isEmpty() || p.trim().equals(pathToRemove)) continue;
            if (sb.length() > 0) sb.append(";");
            sb.append(p.trim());
        }
        String newPaths = sb.toString();
        txtGambar.setText(newPaths);
        showPreviewImages(newPaths);
        if (!txtId.getText().isEmpty()) autoSaveImage(newPaths);
    }

    private void autoSaveImage(String paths) {
        try {
            Content c = buildContentFromForm();
            c.setIdContent(Long.parseLong(txtId.getText()));
            c.setGambar(paths.isEmpty() ? null : paths);
            if (contentDao.updateContent(c)) {
                loadTableData();
                long id = c.getIdContent();
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (Long.parseLong(table.getValueAt(i, 0).toString()) == id) {
                        table.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlatforms() {
        cbPlatform.removeAllItems();
        for (Platform p : platformDao.getAllPlatforms()) cbPlatform.addItem(p);
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (Content c : contentDao.getAllContents()) {
            tableModel.addRow(new Object[]{
                    c.getIdContent(), c.getJudulContent(), c.getNamaPlatform(),
                    c.getKategori(), c.getStatus(), c.getDeskripsi(), c.getGambar()
            });
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtJudul.setText("");
        txtDeskripsi.setText("");
        txtGambar.setText("");
        showPreviewImages("");
        cbKategori.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        if (cbPlatform.getItemCount() > 0) cbPlatform.setSelectedIndex(0);
        table.clearSelection();
    }

    private boolean validateForm() {
        if (txtJudul.getText().trim().isEmpty()) {
            OurIsland.show(this, "Judul Konten tidak boleh kosong!", OurIsland.IslandType.ERROR);
            return false;
        }
        if (cbPlatform.getSelectedItem() == null) {
            OurIsland.show(this, "Pilih platform terlebih dahulu!", OurIsland.IslandType.ERROR);
            return false;
        }
        return true;
    }

    public void updateTheme(boolean isLight) {
        Color bg    = isLight ? new Color(245, 245, 247) : new Color(28, 28, 30);
        Color fg    = isLight ? new Color(29, 29, 31)    : new Color(245, 245, 247);
        Color card  = isLight ? Color.WHITE              : new Color(44, 44, 46);
        Color border = isLight ? new Color(229, 229, 231) : new Color(58, 58, 60);

        setBackground(bg);

        for (JTextField tf : new JTextField[]{txtJudul, txtGambar}) {
            if (tf == null) continue;
            tf.setBackground(isLight ? card : bg);
            tf.setForeground(fg);
            tf.setCaretColor(isLight ? Color.BLACK : Color.WHITE);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(border, 1), new EmptyBorder(5, 7, 5, 7)
            ));
        }

        if (txtDeskripsi != null) {
            txtDeskripsi.setBackground(isLight ? card : bg);
            txtDeskripsi.setForeground(fg);
            txtDeskripsi.setCaretColor(isLight ? Color.BLACK : Color.WHITE);
            Container parent = txtDeskripsi.getParent();
            while (parent != null && !(parent instanceof JScrollPane)) parent = parent.getParent();
            if (parent != null) {
                ((JScrollPane) parent).setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(border, 1), new EmptyBorder(2, 2, 2, 2)
                ));
            }
        }

        for (JComboBox<?> cb : new JComboBox[]{cbKategori, cbStatus, cbPlatform}) {
            if (cb == null) continue;
            cb.setBackground(isLight ? card : bg);
            cb.setForeground(fg);
        }

        showPreviewImages(txtGambar != null ? txtGambar.getText() : "");
    }

    private static class BulletIdRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int d = 10, x = (getWidth() - d) / 2, y = (getHeight() - d) / 2;
                    if (table.isRowSelected(row)) {
                        g2.setColor(new Color(250, 88, 106));
                        g2.fillOval(x, y, d, d);
                    } else {
                        g2.setColor(new Color(28, 28, 30));
                        g2.fillOval(x, y, d, d);
                        g2.setColor(new Color(58, 58, 60));
                        g2.drawOval(x, y, d, d);
                    }
                    g2.dispose();
                }
            };
            p.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return p;
        }
    }
}
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

public class ContentPanel extends JPanel {

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
        setBackground(MainFrame.mainBgColor);

        // Top Row: Previews + Table
        JPanel topRow = new JPanel(new BorderLayout(15, 0));
        topRow.setOpaque(false);

        // Previews wrapper
        previewContainer = new JPanel(new GridLayout(1, 3, 10, 0));
        previewContainer.setOpaque(false);
        previewContainer.setPreferredSize(new Dimension(380, 0)); // Fixed width, vertical height stretches
        previewContainer.setMaximumSize(new Dimension(380, 200));

        topRow.add(previewContainer, BorderLayout.WEST);

        // Table Container
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        JLabel tableTitle = new JLabel("Daftar Konten");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(MainFrame.textPrimaryColor);
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"", "Judul Konten", "Platform", "Kategori", "Status", "Deskripsi", "Gambar"};
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
                g2.setColor(MainFrame.cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(8, 8, 8, 8));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(tableWrapper, BorderLayout.CENTER);
        topRow.add(tableContainer, BorderLayout.CENTER);

        // Constrain the top row height so previews appear shorter
        JPanel topRowWrapper = new JPanel(new BorderLayout());
        topRowWrapper.setOpaque(false);
        topRowWrapper.setPreferredSize(new Dimension(0, 200));
        topRowWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        topRowWrapper.add(topRow, BorderLayout.CENTER);
        add(topRowWrapper, BorderLayout.CENTER);

        // Bottom Row: CRUD Form Card
        JPanel formCard = new JPanel(new BorderLayout(20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainFrame.cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        formCard.setPreferredSize(new Dimension(0, 250)); // taller form card

        // Fields Panel with GridBagLayout (2 rows, 3 columns)
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        // Hidden ID
        txtId = new JTextField();
        txtId.setVisible(false);

        // Init Form Fields
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
        txtDeskripsi.setBackground(new Color(28, 28, 30));
        txtDeskripsi.setForeground(new Color(245, 245, 247));
        txtDeskripsi.setCaretColor(Color.WHITE);
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane descScroll = new JScrollPane(txtDeskripsi);
        descScroll.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MainFrame.borderColor, 1),
                new EmptyBorder(2, 2, 2, 2)
        ));
        descScroll.setPreferredSize(new Dimension(100, 60));
        descScroll.setMinimumSize(new Dimension(100, 60));

        // Image Selection Field
        txtGambar = createStyledTextField();
        txtGambar.setEditable(false);
        JButton btnPilih = createStyledButton("Pilih", new Color(71, 85, 105));
        btnPilih.setPreferredSize(new Dimension(70, 30));
        
        JPanel gambarInputPanel = new JPanel(new BorderLayout(5, 0));
        gambarInputPanel.setOpaque(false);
        gambarInputPanel.add(txtGambar, BorderLayout.CENTER);
        gambarInputPanel.add(btnPilih, BorderLayout.EAST);

        // Add to GridBagLayout (Row 0: col 0, 1, 2. Row 1: col 0, 1, 2)
        addFormFieldGbc(fieldsPanel, "Judul Konten", txtJudul, 0, 0);
        addFormFieldGbc(fieldsPanel, "Kategori", cbKategori, 1, 0);
        addFormFieldGbc(fieldsPanel, "Platform", cbPlatform, 2, 0);

        addFormFieldGbc(fieldsPanel, "Status", cbStatus, 0, 1);
        addFormFieldGbc(fieldsPanel, "Deskripsi", descScroll, 1, 1);
        addFormFieldGbc(fieldsPanel, "Gambar Konten", gambarInputPanel, 2, 1);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Panel: 2x2 grid aligned on the right
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(240, 120));

        JButton btnSimpan = createStyledButton("Simpan", new Color(16, 185, 129));
        JButton btnEdit = createStyledButton("Edit", new Color(58, 58, 60));
        JButton btnHapus = createStyledButton("Hapus", new Color(250, 88, 106));
        JButton btnClear = createStyledButton("Clear", new Color(58, 58, 60));

        btnPanel.add(btnSimpan);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);

        formCard.add(btnPanel, BorderLayout.EAST);

        add(formCard, BorderLayout.SOUTH);

        // Events
        btnPilih.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                java.io.File[] selectedFiles = chooser.getSelectedFiles();
                String currentPaths = txtGambar.getText().trim();
                StringBuilder sb = new StringBuilder(currentPaths);
                
                for (java.io.File file : selectedFiles) {
                    String path = file.getAbsolutePath();
                    if (currentPaths.contains(path)) continue;
                    
                    if (sb.length() > 0) sb.append(";");
                    sb.append(path);
                }
                
                String newPaths = sb.toString();
                txtGambar.setText(newPaths);
                showPreviewImages(newPaths);
                
                if (!txtId.getText().isEmpty()) {
                    autoSaveImageToDatabase(newPaths);
                }
            }
        });

        btnSimpan.addActionListener(e -> {
            if (validateForm()) {
                Content c = new Content();
                c.setJudulContent(txtJudul.getText().trim());
                c.setKategori(cbKategori.getSelectedItem().toString());
                c.setDeskripsi(txtDeskripsi.getText().trim());
                c.setStatus(cbStatus.getSelectedItem().toString());
                c.setGambar(txtGambar.getText().trim());
                Platform selectedPlat = (Platform) cbPlatform.getSelectedItem();
                if (selectedPlat != null) c.setIdPlatform(selectedPlat.getIdPlatform());

                if (contentDao.insertContent(c)) {
                    OurIsland.show(this, "Konten berhasil disimpan!", OurIsland.IslandType.SUCCESS);
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
                Content c = new Content();
                c.setIdContent(Long.parseLong(txtId.getText()));
                c.setJudulContent(txtJudul.getText().trim());
                c.setKategori(cbKategori.getSelectedItem().toString());
                c.setDeskripsi(txtDeskripsi.getText().trim());
                c.setStatus(cbStatus.getSelectedItem().toString());
                c.setGambar(txtGambar.getText().trim());
                Platform selectedPlat = (Platform) cbPlatform.getSelectedItem();
                if (selectedPlat != null) c.setIdPlatform(selectedPlat.getIdPlatform());

                if (contentDao.updateContent(c)) {
                    OurIsland.show(this, "Konten berhasil diperbarui!", OurIsland.IslandType.SUCCESS);
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
                ? "Apakah Anda yakin ingin menghapus konten ini?" 
                : "Apakah Anda yakin ingin menghapus " + selectedRows.length + " konten yang dipilih?";
                
            int confirm = JOptionPane.showConfirmDialog(this, confirmMsg, "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int successCount = 0;
                for (int row : selectedRows) {
                    long id = Long.parseLong(table.getValueAt(row, 0).toString());
                    // Delete referencing performa records first to avoid foreign key violations
                    try (java.sql.Connection conn = com.ourscontent.db.DatabaseConnection.getConnection();
                         java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM performa WHERE id_content = ?")) {
                        ps.setLong(1, id);
                        ps.executeUpdate();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (contentDao.deleteContent(id)) {
                        successCount++;
                    }
                }
                if (successCount > 0) {
                    OurIsland.show(this, successCount + " konten berhasil dihapus!", OurIsland.IslandType.SUCCESS);
                    clearForm();
                    loadTableData();
                } else {
                    OurIsland.show(this, "Gagal menghapus konten!", OurIsland.IslandType.ERROR);
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
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
                    Platform p = cbPlatform.getItemAt(i);
                    if (p.getNamaPlatform().equals(platName)) {
                        cbPlatform.setSelectedItem(p);
                        break;
                    }
                }
            }
        });

        loadPlatforms();
        loadTableData();
    }

    private void showPreviewImages(String paths) {
        previewContainer.removeAll();

        String[] parts = (paths != null && !paths.trim().isEmpty()) ? paths.split(";") : new String[0];
        
        // We always show exactly 3 cards
        for (int i = 0; i < 3; i++) {
            if (i < parts.length && !parts[i].trim().isEmpty()) {
                String path = parts[i].trim();
                final String currentPath = path;

                JPanel itemPanel = new JPanel(new BorderLayout(5, 5)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(MainFrame.cardBgColor);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                        g2.setColor(MainFrame.borderColor);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                        g2.dispose();
                    }
                };
                itemPanel.setOpaque(false);
                itemPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

                // Image Label
                JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
                
                try {
                    java.io.File file = new java.io.File(path);
                    if (file.exists()) {
                        ImageIcon originalIcon = new ImageIcon(path);
                        Image img = originalIcon.getImage();
                        
                        // Scale image dynamically when rendered/validated
                        imgLabel.addComponentListener(new java.awt.event.ComponentAdapter() {
                            @Override
                            public void componentResized(java.awt.event.ComponentEvent evt) {
                                int w = imgLabel.getWidth();
                                int h = imgLabel.getHeight();
                                if (w > 0 && h > 0) {
                                    int imgWidth = img.getWidth(null);
                                    int imgHeight = img.getHeight(null);
                                    if (imgWidth > 0 && imgHeight > 0) {
                                        double ratio = Math.min((double) w / imgWidth, (double) h / imgHeight);
                                        int width = (int) (imgWidth * ratio);
                                        int height = (int) (imgHeight * ratio);
                                        if (width > 0 && height > 0) {
                                            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                                            imgLabel.setIcon(new ImageIcon(scaledImg));
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        imgLabel.setText("<html><center>Image<br>not found</center></html>");
                        imgLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                        imgLabel.setForeground(new Color(180, 83, 9));
                    }
                } catch (Exception e) {
                    imgLabel.setText("<html><center>Preview<br>Error</center></html>");
                    imgLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                    imgLabel.setForeground(new Color(239, 68, 68));
                }

                // Delete button for this image
                JButton btnDeleteImage = new JButton("X") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(239, 68, 68));
                        g2.fillOval(0, 0, getWidth(), getHeight());
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth("X")) / 2;
                        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                        g2.drawString("X", x, y);
                        g2.dispose();
                    }
                };
                btnDeleteImage.setBorderPainted(false);
                btnDeleteImage.setContentAreaFilled(false);
                btnDeleteImage.setFocusPainted(false);
                btnDeleteImage.setOpaque(false);
                btnDeleteImage.setPreferredSize(new Dimension(20, 20));
                btnDeleteImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnDeleteImage.addActionListener(ev -> removeImage(currentPath));

                JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                topActionPanel.setOpaque(false);
                topActionPanel.add(btnDeleteImage);

                itemPanel.add(topActionPanel, BorderLayout.NORTH);
                itemPanel.add(imgLabel, BorderLayout.CENTER);

                previewContainer.add(itemPanel);
            } else {
                // Placeholder card
                JPanel placeholderCard = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Soft card background
                        g2.setColor(MainFrame.cardBgColor);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                        
                        // Dashed line border
                        g2.setColor(MainFrame.borderColor);
                        float[] dash = {6.0f, 4.0f};
                        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f));
                        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                        g2.dispose();
                    }
                };
                placeholderCard.setOpaque(false);
                placeholderCard.setBorder(new EmptyBorder(15, 15, 15, 15));

                JLabel placeholderLabel = new JLabel("<html><center>PREV<br>IEW<br>GAM<br>BAR<br>KON<br>TEN</center></html>", SwingConstants.CENTER);
                placeholderLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                placeholderLabel.setForeground(MainFrame.textSecondaryColor);
                placeholderCard.add(placeholderLabel, BorderLayout.CENTER);

                previewContainer.add(placeholderCard);
            }
        }
        previewContainer.revalidate();
        previewContainer.repaint();
    }

    private void removeImage(String pathToRemove) {
        String currentPaths = txtGambar.getText().trim();
        if (currentPaths.isEmpty()) return;
        
        String[] parts = currentPaths.split(";");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String path = p.trim();
            if (path.isEmpty() || path.equals(pathToRemove)) continue;
            if (sb.length() > 0) sb.append(";");
            sb.append(path);
        }
        
        String newPaths = sb.toString();
        txtGambar.setText(newPaths);
        showPreviewImages(newPaths);
        
        if (!txtId.getText().isEmpty()) {
            autoSaveImageToDatabase(newPaths);
        }
    }

    private void autoSaveImageToDatabase(String paths) {
        try {
            long id = Long.parseLong(txtId.getText());
            Content c = new Content();
            c.setIdContent(id);
            c.setJudulContent(txtJudul.getText().trim());
            c.setKategori(cbKategori.getSelectedItem().toString());
            c.setDeskripsi(txtDeskripsi.getText().trim());
            c.setStatus(cbStatus.getSelectedItem().toString());
            c.setGambar(paths.isEmpty() ? null : paths);
            Platform selectedPlat = (Platform) cbPlatform.getSelectedItem();
            if (selectedPlat != null) c.setIdPlatform(selectedPlat.getIdPlatform());
            
            if (contentDao.updateContent(c)) {
                loadTableData();
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
        List<Platform> list = platformDao.getAllPlatforms();
        for (Platform p : list) {
            cbPlatform.addItem(p);
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Content> list = contentDao.getAllContents();
        for (Content c : list) {
            tableModel.addRow(new Object[]{
                    c.getIdContent(),
                    c.getJudulContent(),
                    c.getNamaPlatform(),
                    c.getKategori(),
                    c.getStatus(),
                    c.getDeskripsi(),
                    c.getGambar()
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

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(28, 28, 30));
        tf.setForeground(new Color(245, 245, 247));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MainFrame.borderColor, 1),
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
    private void addFormFieldGbc(JPanel panel, String labelText, JComponent component, int gridx, int gridy) {
        JPanel fieldWrapper = new JPanel();
        fieldWrapper.setLayout(new BoxLayout(fieldWrapper, BoxLayout.Y_AXIS));
        fieldWrapper.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(MainFrame.textSecondaryColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldWrapper.add(label);
        fieldWrapper.add(Box.createVerticalStrut(4));
        fieldWrapper.add(component);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int leftInset = (gridx == 0) ? 0 : 10;
        int rightInset = (gridx == 2) ? 0 : 10;
        gbc.insets = new Insets(5, leftInset, 5, rightInset);
        
        panel.add(fieldWrapper, gbc);
    }

    public void updateTheme(boolean isLight) {
        Color bg = isLight ? new Color(245, 245, 247) : new Color(28, 28, 30);
        Color fg = isLight ? new Color(29, 29, 31) : new Color(245, 245, 247);
        Color secondaryFg = isLight ? new Color(110, 110, 115) : new Color(161, 161, 170);
        Color borderC = isLight ? new Color(229, 229, 231) : new Color(58, 58, 60);
        Color cardBg = isLight ? new Color(255, 255, 255) : new Color(44, 44, 46);

        setBackground(bg);

        if (txtJudul != null) {
            txtJudul.setBackground(isLight ? cardBg : bg);
            txtJudul.setForeground(fg);
            txtJudul.setCaretColor(isLight ? Color.BLACK : Color.WHITE);
            txtJudul.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderC, 1),
                    new EmptyBorder(5, 7, 5, 7)
            ));
        }
        if (txtGambar != null) {
            txtGambar.setBackground(isLight ? cardBg : bg);
            txtGambar.setForeground(fg);
            txtGambar.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderC, 1),
                    new EmptyBorder(5, 7, 5, 7)
            ));
        }
        if (txtDeskripsi != null) {
            txtDeskripsi.setBackground(isLight ? cardBg : bg);
            txtDeskripsi.setForeground(fg);
            txtDeskripsi.setCaretColor(isLight ? Color.BLACK : Color.WHITE);
            
            Container parent = txtDeskripsi.getParent();
            while (parent != null && !(parent instanceof JScrollPane)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                ((JScrollPane) parent).setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(borderC, 1),
                        new EmptyBorder(2, 2, 2, 2)
                ));
            }
        }
        if (cbKategori != null) {
            cbKategori.setBackground(isLight ? cardBg : bg);
            cbKategori.setForeground(fg);
        }
        if (cbStatus != null) {
            cbStatus.setBackground(isLight ? cardBg : bg);
            cbStatus.setForeground(fg);
        }
        if (cbPlatform != null) {
            cbPlatform.setBackground(isLight ? cardBg : bg);
            cbPlatform.setForeground(fg);
        }
        
        if (txtGambar != null) {
            showPreviewImages(txtGambar.getText());
        }
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

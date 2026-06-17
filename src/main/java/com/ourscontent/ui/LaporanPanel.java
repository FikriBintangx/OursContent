package com.ourscontent.ui;

import com.ourscontent.db.DatabaseConnection;
import com.ourscontent.dao.PlatformDAO;
import com.ourscontent.model.Platform;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaporanPanel extends JPanel {

    private JComboBox<String> cbJenisLaporan;
    private JComboBox<String> cbPlatform;
    private JTextField txtMulaiTanggal;
    private JTextField txtSampaiTanggal;
    
    private Date startDate;
    private Date endDate;
    
    private JLabel lblSummaryCount;
    private JLabel lblSummaryDetails;
    
    private Color themeBgColor = new Color(28, 28, 30);
    private Color cardBgColor = new Color(44, 44, 46);
    private Color textPrimaryColor = new Color(245, 245, 247);
    private Color textSecondaryColor = new Color(161, 161, 170);
    private Color borderColor = new Color(58, 58, 60);
    private Color accentRed = new Color(250, 88, 106);
    private SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy");

    public LaporanPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(themeBgColor);

        // Initialize Dates (Default to last 30 days)
        Calendar cal = Calendar.getInstance();
        endDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        startDate = cal.getTime();

        // --- HEADER AREA ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Laporan & Ekspor");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textPrimaryColor);

        JLabel subtitleLabel = new JLabel("Kelola dan ekspor data laporan sesuai kebutuhan Anda.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(textSecondaryColor);
        subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN WORKSPACE ---
        JPanel workspace = new JPanel();
        workspace.setLayout(new BoxLayout(workspace, BoxLayout.Y_AXIS));
        workspace.setOpaque(false);

        // Row 1: Filter Configuration (Left) & Summary (Right)
        JPanel row1 = new JPanel(new BorderLayout(20, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1.1 Filter Config Panel (Left Card)
        JPanel filterCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        filterCard.setOpaque(false);
        filterCard.setBackground(cardBgColor);
        filterCard.setLayout(new BoxLayout(filterCard, BoxLayout.Y_AXIS));
        filterCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header inside card
        JPanel filterCardHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterCardHeader.setOpaque(false);
        JLabel filterIcon = new JLabel(new FunnelIcon(accentRed));
        JLabel filterTitle = new JLabel("Konfigurasi Filter");
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterTitle.setForeground(textPrimaryColor);
        filterCardHeader.add(filterIcon);
        filterCardHeader.add(filterTitle);
        filterCardHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterCard.add(filterCardHeader);
        filterCard.add(Box.createVerticalStrut(15));

        // Filter Fields Container
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Jenis Laporan field
        cbJenisLaporan = new JComboBox<>(new String[]{"Laporan Konten", "Laporan Performa", "Laporan Platform"});
        styleComboBox(cbJenisLaporan);
        addFilterFormRow(fieldsPanel, "Jenis Laporan", cbJenisLaporan);

        // Platform field
        cbPlatform = new JComboBox<>();
        styleComboBox(cbPlatform);
        addFilterFormRow(fieldsPanel, "Platform", cbPlatform);

        // Mulai Tanggal field
        txtMulaiTanggal = createDateField(displaySdf.format(startDate));
        addFilterFormRow(fieldsPanel, "Mulai Tanggal", txtMulaiTanggal);

        // Sampai Tanggal field
        txtSampaiTanggal = createDateField(displaySdf.format(endDate));
        addFilterFormRow(fieldsPanel, "Sampai Tanggal", txtSampaiTanggal);

        filterCard.add(fieldsPanel);
        row1.add(filterCard, BorderLayout.CENTER);

        // 1.2 Summary Card (Right Card)
        JPanel summaryCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        summaryCard.setOpaque(false);
        summaryCard.setBackground(cardBgColor);
        summaryCard.setPreferredSize(new Dimension(300, 0));
        summaryCard.setLayout(new BorderLayout());
        summaryCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header inside summary card
        JPanel summaryHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        summaryHeader.setOpaque(false);
        JLabel summaryIcon = new JLabel(new BarChartIcon(accentRed));
        JLabel summaryTitleLabel = new JLabel("Ringkasan Data");
        summaryTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryTitleLabel.setForeground(textPrimaryColor);
        summaryHeader.add(summaryIcon);
        summaryHeader.add(summaryTitleLabel);
        summaryCard.add(summaryHeader, BorderLayout.NORTH);

        // Center Content of Summary
        JPanel summaryBody = new JPanel(new GridBagLayout());
        summaryBody.setOpaque(false);
        
        JPanel countPanel = new JPanel();
        countPanel.setLayout(new BoxLayout(countPanel, BoxLayout.Y_AXIS));
        countPanel.setOpaque(false);

        lblSummaryCount = new JLabel("0");
        lblSummaryCount.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblSummaryCount.setForeground(accentRed);
        lblSummaryCount.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSummaryDetails = new JLabel("Baris data ditemukan");
        lblSummaryDetails.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSummaryDetails.setForeground(textSecondaryColor);
        lblSummaryDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSummaryDetails.setBorder(new EmptyBorder(5, 0, 0, 0));

        countPanel.add(lblSummaryCount);
        countPanel.add(lblSummaryDetails);
        summaryBody.add(countPanel);
        summaryCard.add(summaryBody, BorderLayout.CENTER);

        row1.add(summaryCard, BorderLayout.EAST);
        workspace.add(row1);
        workspace.add(Box.createVerticalStrut(20));

        // Row 2: Export Actions (Bottom Panel)
        JPanel exportCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        exportCard.setOpaque(false);
        exportCard.setBackground(cardBgColor);
        exportCard.setLayout(new BoxLayout(exportCard, BoxLayout.Y_AXIS));
        exportCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        exportCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        JPanel exportHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        exportHeader.setOpaque(false);
        exportHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel exportIcon = new JLabel(new DownloadIcon(accentRed));
        JLabel exportTitle = new JLabel("Aksi Ekspor");
        exportTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportTitle.setForeground(textPrimaryColor);
        exportHeader.add(exportIcon);
        exportHeader.add(exportTitle);
        exportCard.add(exportHeader);
        exportCard.add(Box.createVerticalStrut(15));

        // Action items list
        JPanel actionsContainer = new JPanel();
        actionsContainer.setLayout(new BoxLayout(actionsContainer, BoxLayout.Y_AXIS));
        actionsContainer.setOpaque(false);
        actionsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel itemPreview = createActionRow("Preview Laporan (PDF)", "Lihat laporan sebelum mengunduh", new PdfIcon(accentRed), e -> generateReport(true, false));
        JPanel itemPdf = createActionRow("Download PDF", "Unduh laporan dalam format PDF", new PdfIcon(accentRed), e -> generateReport(false, true));
        JPanel itemCsv = createActionRow("Download CSV", "Unduh data laporan dalam format CSV", new CsvIcon(new Color(16, 185, 129)), e -> generateCSV());

        actionsContainer.add(itemPreview);
        actionsContainer.add(Box.createVerticalStrut(8));
        actionsContainer.add(itemPdf);
        actionsContainer.add(Box.createVerticalStrut(8));
        actionsContainer.add(itemCsv);

        exportCard.add(actionsContainer);
        workspace.add(exportCard);

        add(workspace, BorderLayout.CENTER);

        // Click actions for date inputs to trigger custom date range picker dialog
        MouseAdapter dateFieldClick = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openCustomDateRangePicker();
            }
        };
        txtMulaiTanggal.addMouseListener(dateFieldClick);
        txtSampaiTanggal.addMouseListener(dateFieldClick);

        // Load content providers and initial summaries
        loadPlatforms();
        cbJenisLaporan.addActionListener(e -> updateSummary());
        cbPlatform.addActionListener(e -> updateSummary());
        updateSummary();
    }

    private JTextField createDateField(String val) {
        JTextField tf = new JTextField(val);
        tf.setEditable(false);
        tf.setBackground(new Color(28, 28, 30));
        tf.setForeground(textPrimaryColor);
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add calendar symbol padding/outline
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1),
                BorderFactory.createCompoundBorder(
                        new EmptyBorder(5, 7, 5, 30), // Right padding to clear the icon
                        new EmptyBorder(0, 0, 0, 0)
                )
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setBackground(new Color(28, 28, 30));
        cb.setForeground(textPrimaryColor);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBorder(new LineBorder(borderColor, 1));
    }

    private void addFilterFormRow(JPanel container, String labelText, JComponent component) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(textSecondaryColor);
        label.setPreferredSize(new Dimension(100, 30));

        row.add(label, BorderLayout.WEST);
        row.add(component, BorderLayout.CENTER);
        container.add(row);
    }

    private JPanel createActionRow(String title, String desc, Icon icon, java.awt.event.ActionListener action) {
        JPanel row = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBackground(new Color(36, 36, 38));
        row.setBorder(new EmptyBorder(12, 16, 12, 16));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Left Icon block
        JLabel lblIcon = new JLabel(icon);

        // Center Text block
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(textPrimaryColor);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(textSecondaryColor);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblDesc);

        // Right Arrow Chevron block
        JLabel lblArrow = new JLabel(">");
        lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblArrow.setForeground(textSecondaryColor);

        row.add(lblIcon, BorderLayout.WEST);
        row.add(textPanel, BorderLayout.CENTER);
        row.add(lblArrow, BorderLayout.EAST);

        // Hover Effect Listener
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(52, 52, 56));
                lblArrow.setForeground(textPrimaryColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(new Color(36, 36, 38));
                lblArrow.setForeground(textSecondaryColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                action.actionPerformed(new java.awt.event.ActionEvent(row, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
            }
        });

        return row;
    }

    public void loadPlatforms() {
        cbPlatform.removeAllItems();
        cbPlatform.addItem("Semua Platform");
        try {
            PlatformDAO dao = new PlatformDAO();
            List<Platform> list = dao.getAllPlatforms();
            for (Platform p : list) {
                cbPlatform.addItem(p.getNamaPlatform());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateSummary() {
        String type = cbJenisLaporan.getSelectedItem() != null ? cbJenisLaporan.getSelectedItem().toString() : "Laporan Konten";
        String platform = cbPlatform.getSelectedItem() != null ? cbPlatform.getSelectedItem().toString() : "Semua Platform";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tglMulai = sdf.format(startDate);
        String tglSelesai = sdf.format(endDate);

        String sql = "";
        if (type.equals("Laporan Konten")) {
            sql = "SELECT COUNT(DISTINCT c.id_content) FROM content c " +
                  "LEFT JOIN platform p ON c.id_platform = p.id_platform " +
                  "LEFT JOIN performa perf ON c.id_content = perf.id_content WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND p.nama_platform = ?";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND perf.tanggal_posting >= ?";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND perf.tanggal_posting <= ?";
            }
        } else if (type.equals("Laporan Performa")) {
            sql = "SELECT COUNT(*) FROM performa p " +
                  "JOIN content c ON p.id_content = c.id_content " +
                  "LEFT JOIN platform pl ON c.id_platform = pl.id_platform WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND pl.nama_platform = ?";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND p.tanggal_posting >= ?";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND p.tanggal_posting <= ?";
            }
        } else if (type.equals("Laporan Platform")) {
            sql = "SELECT COUNT(DISTINCT p.id_platform) FROM platform p " +
                  "LEFT JOIN content c ON p.id_platform = c.id_platform " +
                  "LEFT JOIN performa perf ON c.id_content = perf.id_content WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND p.nama_platform = ?";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND perf.tanggal_posting >= ?";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND perf.tanggal_posting <= ?";
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (!platform.equals("Semua Platform")) {
                ps.setString(paramIndex++, platform);
            }
            if (!tglMulai.isEmpty()) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(tglMulai));
            }
            if (!tglSelesai.isEmpty()) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(tglSelesai));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    lblSummaryCount.setText(String.valueOf(count));
                    lblSummaryDetails.setText("Baris data ditemukan");
                }
            }
        } catch (SQLException ex) {
            lblSummaryCount.setText("Err");
            lblSummaryDetails.setText("Gagal mengambil ringkasan");
        }
    }

    private void generateReport(boolean isPreview, boolean isSavePdf) {
        String selected = cbJenisLaporan.getSelectedItem().toString();
        String platform = cbPlatform.getSelectedItem() != null ? cbPlatform.getSelectedItem().toString() : "Semua Platform";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tglMulai = sdf.format(startDate);
        String tglSelesai = sdf.format(endDate);

        String reportPath = "";
        String sql = "";

        if (selected.equals("Laporan Konten")) {
            reportPath = "/reports/laporan_konten.jrxml";
            sql = "SELECT DISTINCT c.judul_content, p.nama_platform, c.status FROM content c " +
                  "LEFT JOIN platform p ON c.id_platform = p.id_platform " +
                  "LEFT JOIN performa perf ON c.id_content = perf.id_content WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND p.nama_platform = '" + platform.replace("'", "''") + "'";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND perf.tanggal_posting >= '" + tglMulai.replace("'", "''") + "'";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND perf.tanggal_posting <= '" + tglSelesai.replace("'", "''") + "'";
            }
            sql += " ORDER BY c.judul_content ASC";
        } else if (selected.equals("Laporan Performa")) {
            reportPath = "/reports/laporan_performa.jrxml";
            sql = "SELECT c.judul_content, p.views, p.likes, p.komentar, (p.likes + p.komentar) as engagement " +
                  "FROM performa p JOIN content c ON p.id_content = c.id_content " +
                  "LEFT JOIN platform pl ON c.id_platform = pl.id_platform WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND pl.nama_platform = '" + platform.replace("'", "''") + "'";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND p.tanggal_posting >= '" + tglMulai.replace("'", "''") + "'";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND p.tanggal_posting <= '" + tglSelesai.replace("'", "''") + "'";
            }
            sql += " ORDER BY p.id_performa ASC";
        } else if (selected.equals("Laporan Platform")) {
            reportPath = "/reports/laporan_platform.jrxml";
            sql = "SELECT p.nama_platform, COUNT(DISTINCT c.id_content) as jumlah_konten FROM platform p " +
                  "LEFT JOIN content c ON p.id_platform = c.id_platform " +
                  "LEFT JOIN performa perf ON c.id_content = perf.id_content WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND p.nama_platform = '" + platform.replace("'", "''") + "'";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND perf.tanggal_posting >= '" + tglMulai.replace("'", "''") + "'";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND perf.tanggal_posting <= '" + tglSelesai.replace("'", "''") + "'";
            }
            sql += " GROUP BY p.nama_platform";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            InputStream reportStream = getClass().getResourceAsStream(reportPath);
            if (reportStream == null) {
                OurIsland.show(this, "Template laporan tidak ditemukan: " + reportPath, OurIsland.IslandType.ERROR);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", selected);
            parameters.put("CompanyName", "OursContent");
            parameters.put("Period", displaySdf.format(startDate) + " s/d " + displaySdf.format(endDate));
            parameters.put("DateGenerated", displaySdf.format(new Date()));

            JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(rs);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, resultSetDataSource);

            if (isPreview) {
                JasperViewer.viewReport(jasperPrint, false);
            }

            if (isSavePdf) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(selected.toLowerCase().replace(" ", "_") + ".pdf"));
                int userSelection = fileChooser.showSaveDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String path = fileToSave.getAbsolutePath();
                    if (!path.endsWith(".pdf")) {
                        path += ".pdf";
                    }
                    JasperExportManager.exportReportToPdfFile(jasperPrint, path);
                    OurIsland.show(this, "PDF berhasil disimpan di:\n" + path, OurIsland.IslandType.SUCCESS);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            OurIsland.show(this, "Gagal memproses laporan: " + ex.getMessage(), OurIsland.IslandType.ERROR);
        }
    }

    private void generateCSV() {
        String selected = cbJenisLaporan.getSelectedItem().toString();
        String platform = cbPlatform.getSelectedItem() != null ? cbPlatform.getSelectedItem().toString() : "Semua Platform";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tglMulai = sdf.format(startDate);
        String tglSelesai = sdf.format(endDate);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(selected.toLowerCase().replace(" ", "_") + ".csv"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        String path = fileToSave.getAbsolutePath();
        if (!path.endsWith(".csv")) {
            path += ".csv";
        }

        String sql = "";
        String[] headers = {};

        if (selected.equals("Laporan Konten")) {
            sql = "SELECT DISTINCT c.judul_content, p.nama_platform, c.status FROM content c " +
                  "LEFT JOIN platform p ON c.id_platform = p.id_platform " +
                  "LEFT JOIN performa perf ON c.id_content = perf.id_content WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND p.nama_platform = '" + platform.replace("'", "''") + "'";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND perf.tanggal_posting >= '" + tglMulai.replace("'", "''") + "'";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND perf.tanggal_posting <= '" + tglSelesai.replace("'", "''") + "'";
            }
            sql += " ORDER BY c.judul_content ASC";
            headers = new String[]{"Judul Konten", "Platform", "Status"};
        } else if (selected.equals("Laporan Performa")) {
            sql = "SELECT c.judul_content, p.views, p.likes, p.komentar, (p.likes + p.komentar) as engagement " +
                  "FROM performa p JOIN content c ON p.id_content = c.id_content " +
                  "LEFT JOIN platform pl ON c.id_platform = pl.id_platform WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND pl.nama_platform = '" + platform.replace("'", "''") + "'";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND p.tanggal_posting >= '" + tglMulai.replace("'", "''") + "'";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND p.tanggal_posting <= '" + tglSelesai.replace("'", "''") + "'";
            }
            sql += " ORDER BY p.id_performa ASC";
            headers = new String[]{"Judul Konten", "Views", "Likes", "Komentar", "Engagement"};
        } else if (selected.equals("Laporan Platform")) {
            sql = "SELECT p.nama_platform, COUNT(DISTINCT c.id_content) as jumlah_konten FROM platform p " +
                  "LEFT JOIN content c ON p.id_platform = c.id_platform " +
                  "LEFT JOIN performa perf ON c.id_content = perf.id_content WHERE (1=1)";
            if (!platform.equals("Semua Platform")) {
                sql += " AND p.nama_platform = '" + platform.replace("'", "''") + "'";
            }
            if (!tglMulai.isEmpty()) {
                sql += " AND perf.tanggal_posting >= '" + tglMulai.replace("'", "''") + "'";
            }
            if (!tglSelesai.isEmpty()) {
                sql += " AND perf.tanggal_posting <= '" + tglSelesai.replace("'", "''") + "'";
            }
            sql += " GROUP BY p.nama_platform";
            headers = new String[]{"Nama Platform", "Jumlah Konten"};
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             FileWriter fw = new FileWriter(path)) {

            // Write metadata header
            fw.append("OursContent - Sistem Manajemen Konten Kreator\n");
            fw.append("Laporan," + escapeCSV(selected) + "\n");
            fw.append("Platform," + escapeCSV(platform) + "\n");
            fw.append("Periode," + escapeCSV(displaySdf.format(startDate) + " s/d " + displaySdf.format(endDate)) + "\n");
            fw.append("Tanggal Unduh," + escapeCSV(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) + "\n");
            fw.append("\n");

            for (int i = 0; i < headers.length; i++) {
                fw.append(escapeCSV(headers[i]));
                if (i < headers.length - 1) fw.append(",");
            }
            fw.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= headers.length; i++) {
                    fw.append(escapeCSV(rs.getString(i)));
                    if (i < headers.length) fw.append(",");
                }
                fw.append("\n");
            }

            OurIsland.show(this, "CSV berhasil disimpan di:\n" + path, OurIsland.IslandType.SUCCESS);

        } catch (Exception ex) {
            ex.printStackTrace();
            OurIsland.show(this, "Gagal membuat CSV: " + ex.getMessage(), OurIsland.IslandType.ERROR);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        String output = value.replace("\"", "\"\"");
        if (output.contains(",") || output.contains("\n") || output.contains("\"")) {
            return "\"" + output + "\"";
        }
        return output;
    }

    // --- CUSTOM DATE RANGE PICKER POPUP DIALOG ---
    private void openCustomDateRangePicker() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Pilih Rentang Tanggal", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); // Transparent boundary

        // Main Dialog Wrapper with rounded corners & border
        JPanel mainWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(36, 36, 38)); // Dark popup background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        mainWrapper.setOpaque(false);
        mainWrapper.setLayout(new BorderLayout());
        mainWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Two Month Calendars Side-by-Side Panel
        JPanel calendarsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        calendarsPanel.setOpaque(false);

        // Get calendars for Left (previous/current) and Right (current/next) month views
        Calendar leftCal = Calendar.getInstance();
        leftCal.setTime(startDate);
        
        Calendar rightCal = Calendar.getInstance();
        rightCal.setTime(startDate);
        rightCal.add(Calendar.MONTH, 1);

        JPanel leftMonthView = createMonthCalendarPanel(leftCal, dialog);
        JPanel rightMonthView = createMonthCalendarPanel(rightCal, dialog);

        calendarsPanel.add(leftMonthView);
        calendarsPanel.add(rightMonthView);
        mainWrapper.add(calendarsPanel, BorderLayout.CENTER);

        // Preset Shortcut Buttons at the Bottom
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        footerPanel.setOpaque(false);

        JButton btnToday = createPresetButton("Hari Ini", dialog, 0);
        JButton btn7Days = createPresetButton("7 Hari Terakhir", dialog, -7);
        JButton btn30Days = createPresetButton("30 Hari Terakhir", dialog, -30);
        JButton btnThisMonth = createPresetButton("Bulan Ini", dialog, -99); // Special flag
        JButton btnLastMonth = createPresetButton("Bulan Lalu", dialog, -100); // Special flag
        JButton btnClose = createPresetButton("Tutup", dialog, -999);
        btnClose.setBackground(new Color(58, 58, 60));

        footerPanel.add(btnToday);
        footerPanel.add(btn7Days);
        footerPanel.add(btn30Days);
        footerPanel.add(btnThisMonth);
        footerPanel.add(btnLastMonth);
        footerPanel.add(btnClose);

        mainWrapper.add(footerPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainWrapper);
        dialog.pack();
        dialog.setSize(650, 370);
        dialog.setLocationRelativeTo(txtMulaiTanggal); // Show aligned with date input
        dialog.setVisible(true);
    }

    private JPanel createMonthCalendarPanel(Calendar monthCal, JDialog dialog) {
        JPanel monthPanel = new JPanel(new BorderLayout());
        monthPanel.setOpaque(false);

        // Month Title Label
        SimpleDateFormat monthYearSdf = new SimpleDateFormat("MMMM yyyy");
        JLabel lblMonthName = new JLabel(monthYearSdf.format(monthCal.getTime()), SwingConstants.CENTER);
        lblMonthName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMonthName.setForeground(textPrimaryColor);
        lblMonthName.setBorder(new EmptyBorder(0, 0, 10, 0));
        monthPanel.add(lblMonthName, BorderLayout.NORTH);

        // Days Grid (7 cols: Su, Mo, Tu, We, Th, Fr, Sa)
        JPanel daysGrid = new JPanel(new GridLayout(0, 7, 2, 2));
        daysGrid.setOpaque(false);

        String[] weekDays = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String wd : weekDays) {
            JLabel lblWd = new JLabel(wd, SwingConstants.CENTER);
            lblWd.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblWd.setForeground(textSecondaryColor);
            daysGrid.add(lblWd);
        }

        // Days calculation
        Calendar helperCal = Calendar.getInstance();
        helperCal.setTime(monthCal.getTime());
        helperCal.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = helperCal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = helperCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Empty items before first day of month
        for (int i = 1; i < firstDayOfWeek; i++) {
            daysGrid.add(new JLabel(""));
        }

        // Active day items
        SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy");
        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            Calendar targetDayCal = (Calendar) helperCal.clone();
            targetDayCal.set(Calendar.DAY_OF_MONTH, currentDay);
            Date dateVal = targetDayCal.getTime();

            JButton btnDay = new JButton(String.valueOf(day)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    boolean isSelected = false;
                    boolean inRange = false;
                    
                    if (dateVal != null) {
                        long targetTime = stripTime(dateVal).getTime();
                        if (startDate != null && endDate != null) {
                            long startTime = stripTime(startDate).getTime();
                            long endTime = stripTime(endDate).getTime();
                            if (targetTime == startTime || targetTime == endTime) {
                                isSelected = true;
                            } else if (targetTime > startTime && targetTime < endTime) {
                                inRange = true;
                            }
                        } else if (startDate != null) {
                            long startTime = stripTime(startDate).getTime();
                            if (targetTime == startTime) {
                                isSelected = true;
                            }
                        }
                    }

                    if (isSelected) {
                        g2.setColor(accentRed);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    } else if (inRange) {
                        g2.setColor(new Color(250, 88, 106, 40)); // Muted red tint
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnDay.setMargin(new Insets(0, 0, 0, 0));
            btnDay.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            
            // Text color based on range
            boolean isSelectedOrInRange = false;
            if (dateVal != null) {
                long targetTime = stripTime(dateVal).getTime();
                if (startDate != null && endDate != null) {
                    long startTime = stripTime(startDate).getTime();
                    long endTime = stripTime(endDate).getTime();
                    if (targetTime == startTime || targetTime == endTime) {
                        btnDay.setForeground(Color.WHITE);
                        isSelectedOrInRange = true;
                    } else if (targetTime > startTime && targetTime < endTime) {
                        btnDay.setForeground(accentRed);
                        isSelectedOrInRange = true;
                    } else {
                        btnDay.setForeground(textPrimaryColor);
                    }
                } else if (startDate != null) {
                    long startTime = stripTime(startDate).getTime();
                    if (targetTime == startTime) {
                        btnDay.setForeground(Color.WHITE);
                        isSelectedOrInRange = true;
                    } else {
                        btnDay.setForeground(textPrimaryColor);
                    }
                } else {
                    btnDay.setForeground(textPrimaryColor);
                }
            } else {
                btnDay.setForeground(textPrimaryColor);
            }

            btnDay.setContentAreaFilled(false);
            btnDay.setBorderPainted(false);
            btnDay.setFocusPainted(false);
            btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDay.setPreferredSize(new Dimension(24, 24));

            btnDay.addActionListener(e -> {
                // Clicking logic: Sets start date first, then end date
                if (startDate != null && endDate != null) {
                    startDate = dateVal;
                    endDate = null;
                } else if (startDate != null && endDate == null) {
                    if (dateVal.after(startDate)) {
                        endDate = dateVal;
                    } else {
                        endDate = startDate;
                        startDate = dateVal;
                    }
                    dialog.dispose();
                    txtMulaiTanggal.setText(displaySdf.format(startDate));
                    txtSampaiTanggal.setText(displaySdf.format(endDate));
                    updateSummary();
                } else {
                    startDate = dateVal;
                }
                btnDay.repaint();
            });

            daysGrid.add(btnDay);
        }

        monthPanel.add(daysGrid, BorderLayout.CENTER);
        return monthPanel;
    }

    private JButton createPresetButton(String text, JDialog dialog, int offsetDays) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(textPrimaryColor);
        btn.setBackground(new Color(52, 52, 56));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(68, 68, 72));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(52, 52, 56));
            }
        });

        btn.addActionListener(e -> {
            SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            if (offsetDays == -99) { // Bulan Ini
                endDate = cal.getTime();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
            } else if (offsetDays == -100) { // Bulan Lalu
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate = cal.getTime();
            } else if (offsetDays == -999) { // Close
                dialog.dispose();
                return;
            } else { // Normal presets (Today, Last 7, Last 30)
                endDate = cal.getTime();
                if (offsetDays < 0) {
                    cal.add(Calendar.DAY_OF_MONTH, offsetDays);
                }
                startDate = cal.getTime();
            }

            txtMulaiTanggal.setText(displaySdf.format(startDate));
            txtSampaiTanggal.setText(displaySdf.format(endDate));
            dialog.dispose();
            updateSummary();
        });

        return btn;
    }

    public void updateTheme(boolean isLight) {
        if (isLight) {
            themeBgColor = new Color(245, 245, 247);
            cardBgColor = new Color(255, 255, 255);
            textPrimaryColor = new Color(29, 29, 31);
            textSecondaryColor = new Color(110, 110, 115);
            borderColor = new Color(229, 229, 231);
        } else {
            themeBgColor = new Color(28, 28, 30);
            cardBgColor = new Color(44, 44, 46);
            textPrimaryColor = new Color(245, 245, 247);
            textSecondaryColor = new Color(161, 161, 170);
            borderColor = new Color(58, 58, 60);
        }
        setBackground(themeBgColor);
        if (txtMulaiTanggal != null) {
            txtMulaiTanggal.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
            txtMulaiTanggal.setForeground(textPrimaryColor);
            txtMulaiTanggal.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderColor, 1),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(5, 7, 5, 30),
                            BorderFactory.createEmptyBorder(0, 0, 0, 0)
                    )
            ));
        }
        if (txtSampaiTanggal != null) {
            txtSampaiTanggal.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
            txtSampaiTanggal.setForeground(textPrimaryColor);
            txtSampaiTanggal.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderColor, 1),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(5, 7, 5, 30),
                            BorderFactory.createEmptyBorder(0, 0, 0, 0)
                    )
            ));
        }
        if (cbPlatform != null) {
            cbPlatform.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
            cbPlatform.setForeground(textPrimaryColor);
            cbPlatform.setBorder(new LineBorder(borderColor, 1));
        }
        if (cbJenisLaporan != null) {
            cbJenisLaporan.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
            cbJenisLaporan.setForeground(textPrimaryColor);
            cbJenisLaporan.setBorder(new LineBorder(borderColor, 1));
        }
    }

    private Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}

class FunnelIcon implements Icon {
    private int width = 16;
    private int height = 16;
    private Color color;
    public FunnelIcon(Color color) { this.color = color; }
    public int getIconWidth() { return width; }
    public int getIconHeight() { return height; }
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        int[] px = {x + 2, x + 14, x + 9, x + 9, x + 7, x + 7};
        int[] py = {y + 2, y + 2, y + 8, y + 14, y + 14, y + 8};
        g2.fillPolygon(px, py, 6);
        g2.dispose();
    }
}

class BarChartIcon implements Icon {
    private int width = 16;
    private int height = 16;
    private Color color;
    public BarChartIcon(Color color) { this.color = color; }
    public int getIconWidth() { return width; }
    public int getIconHeight() { return height; }
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRoundRect(x + 2, y + 8, 3, 6, 1, 1);
        g2.fillRoundRect(x + 6, y + 3, 3, 11, 1, 1);
        g2.fillRoundRect(x + 10, y + 6, 3, 8, 1, 1);
        g2.dispose();
    }
}

class DownloadIcon implements Icon {
    private int width = 16;
    private int height = 16;
    private Color color;
    public DownloadIcon(Color color) { this.color = color; }
    public int getIconWidth() { return width; }
    public int getIconHeight() { return height; }
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 8, y + 2, x + 8, y + 10);
        g2.drawLine(x + 5, y + 7, x + 8, y + 10);
        g2.drawLine(x + 11, y + 7, x + 8, y + 10);
        g2.drawLine(x + 3, y + 13, x + 13, y + 13);
        g2.dispose();
    }
}

class PdfIcon implements Icon {
    private int width = 20;
    private int height = 20;
    private Color color;
    public PdfIcon(Color color) { this.color = color; }
    public int getIconWidth() { return width; }
    public int getIconHeight() { return height; }
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        int[] px = {x + 3, x + 13, x + 17, x + 17, x + 3};
        int[] py = {y + 2, y + 2, y + 6, y + 18, y + 18};
        g2.fillPolygon(px, py, 5);
        g2.setColor(c.getBackground() != null ? c.getBackground() : new Color(44, 44, 46));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x + 13, y + 2, x + 13, y + 6);
        g2.drawLine(x + 13, y + 6, x + 17, y + 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
        g2.drawString("PDF", x + 5, y + 14);
        g2.dispose();
    }
}

class CsvIcon implements Icon {
    private int width = 20;
    private int height = 20;
    private Color color;
    public CsvIcon(Color color) { this.color = color; }
    public int getIconWidth() { return width; }
    public int getIconHeight() { return height; }
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        int[] px = {x + 3, x + 13, x + 17, x + 17, x + 3};
        int[] py = {y + 2, y + 2, y + 6, y + 18, y + 18};
        g2.fillPolygon(px, py, 5);
        g2.setColor(c.getBackground() != null ? c.getBackground() : new Color(44, 44, 46));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x + 13, y + 2, x + 13, y + 6);
        g2.drawLine(x + 13, y + 6, x + 17, y + 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
        g2.drawString("CSV", x + 5, y + 14);
        g2.dispose();
    }
}

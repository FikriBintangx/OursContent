package com.ourscontent.ui;

import com.ourscontent.db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {

    private JLabel lblTotalKonten, lblPublished, lblDraft, lblEngagement;
    private JLabel lblBestTitle, lblBestViews, lblBestLikes, lblBestKomentar, lblBestPlatform;
    private JPanel panelPlatformAktif;
    private PerformanceChartPanel chartPanel;

    // status loading & shimmer
    private boolean isLoading = false;
    private int shimmerAlpha = 100;
    private boolean shimmerUp = true;
    private Timer shimmerTimer;

    private static class DashboardData {
        String totalKonten = "0";
        String published = "0";
        String draft = "0";
        String engagement = "0";
        
        String bestTitle = "Belum ada data";
        String bestPlatform = "-";
        String bestViews = "Views: 0";
        String bestLikes = "Likes: 0";
        String bestKomentar = "Komentar: 0";
        
        List<PlatformRowData> platformRows = new ArrayList<>();
    }

    private static class PlatformRowData {
        String name;
        int count;
        PlatformRowData(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(28, 28, 30));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(245, 245, 247));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(new Color(28, 28, 30));

        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsGrid.setBackground(new Color(28, 28, 30));

        lblTotalKonten = new JLabel("0");
        lblPublished = new JLabel("0");
        lblDraft = new JLabel("0");
        lblEngagement = new JLabel("0");

        cardsGrid.add(createStatCard("Total Konten", lblTotalKonten));
        cardsGrid.add(createStatCard("Published", lblPublished));
        cardsGrid.add(createStatCard("Draft", lblDraft));
        cardsGrid.add(createStatCard("Engagement", lblEngagement));

        JPanel mainGrid = new JPanel(new BorderLayout(0, 15));
        mainGrid.setBackground(new Color(28, 28, 30));
        mainGrid.add(cardsGrid, BorderLayout.NORTH);

        chartPanel = new PerformanceChartPanel();
        chartPanel.setPreferredSize(new Dimension(0, 200));
        mainGrid.add(chartPanel, BorderLayout.CENTER);

        centerPanel.add(mainGrid, BorderLayout.CENTER);

        JPanel bottomSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomSplit.setBackground(new Color(28, 28, 30));

        JPanel bestContentCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                
                if (isLoading) {
                    g2.setColor(new Color(63, 63, 70, shimmerAlpha));
                    g2.fillRoundRect(15, 15, 140, 12, 4, 4);
                    g2.fillRoundRect(15, 42, 180, 16, 4, 4);
                    g2.fillRoundRect(15, 68, 80, 12, 4, 4);
                    g2.fillRoundRect(15, 90, 60, 12, 4, 4);
                    g2.fillRoundRect(15, 107, 60, 12, 4, 4);
                    g2.fillRoundRect(15, 124, 60, 12, 4, 4);
                }
                g2.dispose();
            }
        };
        bestContentCard.setOpaque(false);
        bestContentCard.setLayout(new BoxLayout(bestContentCard, BoxLayout.Y_AXIS));
        bestContentCard.setBackground(new Color(44, 44, 46));
        bestContentCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblBestHeader = new JLabel("Konten Terbaik Bulan Ini");
        lblBestHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBestHeader.setForeground(new Color(245, 245, 247));
        lblBestHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBestTitle = new JLabel("Judul: -");
        lblBestTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBestTitle.setForeground(new Color(250, 88, 106));
        lblBestTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBestViews = new JLabel("Views: 0");
        lblBestViews.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBestViews.setForeground(new Color(161, 161, 170));
        lblBestViews.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBestLikes = new JLabel("Likes: 0");
        lblBestLikes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBestLikes.setForeground(new Color(161, 161, 170));
        lblBestLikes.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBestKomentar = new JLabel("Komentar: 0");
        lblBestKomentar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBestKomentar.setForeground(new Color(161, 161, 170));
        lblBestKomentar.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBestPlatform = new JLabel("Platform: -");
        lblBestPlatform.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblBestPlatform.setForeground(new Color(161, 161, 170));
        lblBestPlatform.setAlignmentX(Component.LEFT_ALIGNMENT);

        bestContentCard.add(lblBestHeader);
        bestContentCard.add(Box.createVerticalStrut(10));
        bestContentCard.add(lblBestTitle);
        bestContentCard.add(Box.createVerticalStrut(8));
        bestContentCard.add(lblBestPlatform);
        bestContentCard.add(Box.createVerticalStrut(5));
        bestContentCard.add(lblBestViews);
        bestContentCard.add(lblBestLikes);
        bestContentCard.add(lblBestKomentar);

        bottomSplit.add(bestContentCard);

        JPanel activePlatformCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(MainFrame.borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                
                if (isLoading) {
                    g2.setColor(new Color(63, 63, 70, shimmerAlpha));
                    g2.fillRoundRect(15, 15, 140, 12, 4, 4);
                    for (int i = 0; i < 4; i++) {
                        g2.fillRoundRect(15, 45 + (i * 25), 80, 12, 4, 4);
                        g2.fillRoundRect(getWidth() - 95, 45 + (i * 25), 60, 12, 4, 4);
                    }
                }
                g2.dispose();
            }
        };
        activePlatformCard.setOpaque(false);
        activePlatformCard.setBackground(new Color(44, 44, 46));
        activePlatformCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblActiveHeader = new JLabel("Platform Paling Aktif");
        lblActiveHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblActiveHeader.setForeground(new Color(245, 245, 247));
        activePlatformCard.add(lblActiveHeader, BorderLayout.NORTH);

        panelPlatformAktif = new JPanel();
        panelPlatformAktif.setLayout(new BoxLayout(panelPlatformAktif, BoxLayout.Y_AXIS));
        panelPlatformAktif.setBackground(new Color(44, 44, 46));
        activePlatformCard.add(panelPlatformAktif, BorderLayout.CENTER);

        bottomSplit.add(activePlatformCard);

        centerPanel.add(bottomSplit, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(MainFrame.borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                
                if (isLoading) {
                    g2.setColor(new Color(63, 63, 70, shimmerAlpha));
                    g2.fillRoundRect(15, 12, 80, 12, 4, 4);
                    g2.fillRoundRect(15, 32, 50, 18, 4, 4);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(new Color(44, 44, 46));
        card.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(161, 161, 170));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(new Color(245, 245, 247));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void startShimmer() {
        isLoading = true;
        setComponentsVisible(false);
        chartPanel.setLoading(true);
        if (shimmerTimer == null) {
            shimmerTimer = new Timer(30, e -> {
                if (shimmerUp) {
                    shimmerAlpha += 6;
                    if (shimmerAlpha >= 150) {
                        shimmerAlpha = 150;
                        shimmerUp = false;
                    }
                } else {
                    shimmerAlpha -= 6;
                    if (shimmerAlpha <= 40) {
                        shimmerAlpha = 40;
                        shimmerUp = true;
                    }
                }
                chartPanel.setShimmerAlpha(shimmerAlpha);
                repaint();
            });
        }
        shimmerAlpha = 100;
        shimmerUp = true;
        shimmerTimer.start();
    }

    private void stopShimmer() {
        isLoading = false;
        if (shimmerTimer != null) {
            shimmerTimer.stop();
        }
        setComponentsVisible(true);
        chartPanel.setLoading(false);
        repaint();
    }

    private void setComponentsVisible(boolean visible) {
        lblTotalKonten.setVisible(visible);
        lblPublished.setVisible(visible);
        lblDraft.setVisible(visible);
        lblEngagement.setVisible(visible);
        
        lblBestTitle.setVisible(visible);
        lblBestViews.setVisible(visible);
        lblBestLikes.setVisible(visible);
        lblBestKomentar.setVisible(visible);
        lblBestPlatform.setVisible(visible);
        
        panelPlatformAktif.setVisible(visible);
    }

    public void refreshData() {
        startShimmer();

        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                DashboardData data = new DashboardData();
                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement()) {

                    // 1. Total Konten
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM content");
                    if (rs.next()) data.totalKonten = rs.getString(1);

                    // 2. Published
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM content WHERE status = 'Published'");
                    if (rs.next()) data.published = rs.getString(1);

                    // 3. Draft
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM content WHERE status = 'Draft'");
                    if (rs.next()) data.draft = rs.getString(1);

                    // 4. Engagement
                    rs = stmt.executeQuery("SELECT SUM(likes + komentar) FROM performa");
                    if (rs.next()) {
                        data.engagement = String.format("%,d", rs.getInt(1));
                    }

                    // 5. Best Content
                    rs = stmt.executeQuery("SELECT c.judul_content, p.views, p.likes, p.komentar, pl.nama_platform " +
                            "FROM performa p JOIN content c ON p.id_content = c.id_content " +
                            "LEFT JOIN platform pl ON c.id_platform = pl.id_platform " +
                            "ORDER BY p.views DESC LIMIT 1");
                    if (rs.next()) {
                        data.bestTitle = rs.getString("judul_content");
                        data.bestPlatform = "Platform: " + rs.getString("nama_platform");
                        data.bestViews = String.format("Views: %,d", rs.getInt("views"));
                        data.bestLikes = String.format("Likes: %,d", rs.getInt("likes"));
                        data.bestKomentar = String.format("Komentar: %,d", rs.getInt("komentar"));
                    }

                    // 6. Platform Paling Aktif
                    rs = stmt.executeQuery("SELECT p.nama_platform, COUNT(c.id_content) as jumlah " +
                            "FROM platform p LEFT JOIN content c ON p.id_platform = c.id_platform " +
                            "GROUP BY p.nama_platform ORDER BY jumlah DESC");
                    int count = 0;
                    while (rs.next() && count < 4) {
                        data.platformRows.add(new PlatformRowData(rs.getString("nama_platform"), rs.getInt("jumlah")));
                        count++;
                    }
                }
                return data;
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    
                    lblTotalKonten.setText(data.totalKonten);
                    lblPublished.setText(data.published);
                    lblDraft.setText(data.draft);
                    lblEngagement.setText(data.engagement);
                    
                    lblBestTitle.setText(data.bestTitle);
                    lblBestPlatform.setText(data.bestPlatform);
                    lblBestViews.setText(data.bestViews);
                    lblBestLikes.setText(data.bestLikes);
                    lblBestKomentar.setText(data.bestKomentar);
                    
                    panelPlatformAktif.removeAll();
                    panelPlatformAktif.add(Box.createVerticalStrut(10));
                    for (PlatformRowData row : data.platformRows) {
                        JPanel panelRow = new JPanel(new BorderLayout());
                        panelRow.setBackground(new Color(44, 44, 46));
                        panelRow.setBorder(new EmptyBorder(5, 0, 5, 0));

                        JLabel name = new JLabel(row.name);
                        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        name.setForeground(new Color(245, 245, 247));

                        JLabel val = new JLabel(row.count + " Konten");
                        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        val.setForeground(new Color(161, 161, 170));

                        panelRow.add(name, BorderLayout.WEST);
                        panelRow.add(val, BorderLayout.EAST);
                        panelPlatformAktif.add(panelRow);
                    }
                    panelPlatformAktif.revalidate();
                    panelPlatformAktif.repaint();
                    
                    if (chartPanel != null) {
                        chartPanel.refreshChartData();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    stopShimmer();
                }
            }
        };
        worker.execute();
    }
}

package com.ourscontent.ui;

import com.ourscontent.db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PerformanceChartPanel extends JPanel {

    private List<ChartPoint> dataPoints = new ArrayList<>();
    private boolean isLoading = false;
    private int shimmerAlpha = 100;

    public void setLoading(boolean loading) {
        this.isLoading = loading;
        repaint();
    }

    public void setShimmerAlpha(int alpha) {
        this.shimmerAlpha = alpha;
        if (isLoading) {
            repaint();
        }
    }

    private static class ChartPoint {
        String label;
        int views;
        int engagement;

        ChartPoint(String label, int views, int engagement) {
            this.label = label;
            this.views = views;
            this.engagement = engagement;
        }
    }

    public PerformanceChartPanel() {
        setOpaque(false);
        setBackground(MainFrame.cardBgColor);
        refreshChartData();
    }

    public void refreshChartData() {
        dataPoints.clear();
        String sql = "SELECT p.tanggal_posting, SUM(p.views) as total_views, SUM(p.likes + p.komentar) as total_engagement " +
                     "FROM performa p " +
                     "GROUP BY p.tanggal_posting " +
                     "ORDER BY p.tanggal_posting ASC " +
                     "LIMIT 7"; // ambil maksimal 7 hari postingan terakhir
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
            while (rs.next()) {
                java.sql.Date date = rs.getDate("tanggal_posting");
                String label = date != null ? sdf.format(date) : "-";
                int views = rs.getInt("total_views");
                int engagement = rs.getInt("total_engagement");
                dataPoints.add(new ChartPoint(label, views, engagement));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // isi data contoh kalo db kosong biar chart gak kosong melompong
        if (dataPoints.isEmpty()) {
            dataPoints.add(new ChartPoint("01 Jun", 150, 45));
            dataPoints.add(new ChartPoint("04 Jun", 450, 110));
            dataPoints.add(new ChartPoint("08 Jun", 300, 80));
            dataPoints.add(new ChartPoint("12 Jun", 950, 240));
            dataPoints.add(new ChartPoint("15 Jun", 1200, 310));
            dataPoints.add(new ChartPoint("17 Jun", 800, 210));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, height, 18, 18);
        g2.setColor(MainFrame.borderColor);
        g2.drawRoundRect(0, 0, width - 1, height - 1, 18, 18);

        if (isLoading) {
            // bikin garis skeleton sama efek shimmer pas loading
            g2.setColor(new Color(245, 245, 247, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString("Memuat data performa...", 20, 25);

            g2.setColor(new Color(MainFrame.borderColor.getRed(), MainFrame.borderColor.getGreen(), MainFrame.borderColor.getBlue(), 100));
            int gridCount = 4;
            for (int i = 0; i <= gridCount; i++) {
                int y = padding + chartHeight - (i * chartHeight / gridCount);
                g2.drawLine(padding, y, padding + chartWidth, y);
            }

            g2.setColor(new Color(250, 88, 106, shimmerAlpha));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D.Double path = new Path2D.Double();
            path.moveTo(padding, padding + chartHeight / 2.0);
            for (int i = 1; i <= 6; i++) {
                double cx = padding + i * (chartWidth / 6.0);
                double cy = padding + chartHeight / 2.0 + Math.sin(i * 1.5) * 35.0;
                path.lineTo(cx, cy);
            }
            g2.draw(path);
            g2.dispose();
            return;
        }

        if (dataPoints.size() < 2) {
            g2.setColor(new Color(245, 245, 247));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.drawString("Belum cukup data performa", width / 2 - 80, height / 2);
            g2.dispose();
            return;
        }

        // Judul chart
        g2.setColor(new Color(245, 245, 247));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString("Tren Views & Engagement Terakhir", 20, 25);

        // Keterangan / legenda warna chart
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(250, 88, 106));
        g2.fillRect(width - 160, 15, 10, 10);
        g2.setColor(new Color(245, 245, 247));
        g2.drawString("Views", width - 145, 24);

        g2.setColor(new Color(161, 161, 170));
        g2.fillRect(width - 90, 15, 10, 10);
        g2.setColor(new Color(245, 245, 247));
        g2.drawString("Engagement", width - 75, 24);

        // cari nilai tertinggi biar bisa nentuin skala y
        int maxViews = 0;
        int maxEngagement = 0;
        for (ChartPoint cp : dataPoints) {
            if (cp.views > maxViews) maxViews = cp.views;
            if (cp.engagement > maxEngagement) maxEngagement = cp.engagement;
        }
        maxViews = (maxViews == 0) ? 100 : maxViews;
        maxViews = (int) (maxViews * 1.15); // kasih jarak biar ga mentok ke atas

        maxEngagement = (maxEngagement == 0) ? 100 : maxEngagement;
        maxEngagement = (int) (maxEngagement * 1.15); // kasih jarak biar ga mentok ke atas

        // gambar sumbu x
        g2.setColor(MainFrame.borderColor);
        g2.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight);

        // gambar garis grid horizontal (sumbu y)
        int gridCount = 4;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        for (int i = 0; i <= gridCount; i++) {
            int y = padding + chartHeight - (i * chartHeight / gridCount);
            g2.setColor(MainFrame.borderColor);
            g2.drawLine(padding, y, padding + chartWidth, y);

            // label angka sumbu y
            g2.setColor(new Color(161, 161, 170));
            int valView = (i * maxViews / gridCount);
            g2.drawString(String.format("%,d", valView), 5, y + 4);
        }

        double stepX = (double) chartWidth / (dataPoints.size() - 1);
        
        // jalur buat garis views (merah dengan gradasi area di bawahnya)
        Path2D.Double viewPath = new Path2D.Double();
        Path2D.Double viewFillPath = new Path2D.Double();
        
        // jalur buat garis engagement (abu-abu)
        Path2D.Double engPath = new Path2D.Double();

        for (int i = 0; i < dataPoints.size(); i++) {
            ChartPoint cp = dataPoints.get(i);
            double cx = padding + i * stepX;
            
            double cyView = padding + chartHeight - ((double) cp.views / maxViews * chartHeight);
            double cyEng = padding + chartHeight - ((double) cp.engagement / maxEngagement * chartHeight);

            if (i == 0) {
                viewPath.moveTo(cx, cyView);
                viewFillPath.moveTo(cx, padding + chartHeight);
                viewFillPath.lineTo(cx, cyView);

                engPath.moveTo(cx, cyEng);
            } else {
                viewPath.lineTo(cx, cyView);
                viewFillPath.lineTo(cx, cyView);

                engPath.lineTo(cx, cyEng);
            }

            if (i == dataPoints.size() - 1) {
                viewFillPath.lineTo(cx, padding + chartHeight);
                viewFillPath.closePath();
            }

            // label tanggal di sumbu x
            g2.setColor(new Color(161, 161, 170));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(cp.label, (int) (cx - 15), padding + chartHeight + 18);
        }

        // isi gradasi merah buat area views
        g2.setPaint(new GradientPaint(0, padding, new Color(250, 88, 106, 60), 0, padding + chartHeight, new Color(250, 88, 106, 0)));
        g2.fill(viewFillPath);

        // gambar garis views
        g2.setColor(new Color(250, 88, 106));
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(viewPath);

        // gambar garis engagement
        g2.setColor(new Color(161, 161, 170));
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(engPath);

        // gambar buletan/node di tiap titik data
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < dataPoints.size(); i++) {
            ChartPoint cp = dataPoints.get(i);
            double cx = padding + i * stepX;
            double cyView = padding + chartHeight - ((double) cp.views / maxViews * chartHeight);
            double cyEng = padding + chartHeight - ((double) cp.engagement / maxEngagement * chartHeight);

            g2.setColor(MainFrame.cardBgColor);
            g2.fill(new Ellipse2D.Double(cx - 4, cyView - 4, 8, 8));
            g2.setColor(new Color(250, 88, 106));
            g2.draw(new Ellipse2D.Double(cx - 4, cyView - 4, 8, 8));

            g2.setColor(MainFrame.cardBgColor);
            g2.fill(new Ellipse2D.Double(cx - 3, cyEng - 3, 6, 6));
            g2.setColor(new Color(161, 161, 170));
            g2.draw(new Ellipse2D.Double(cx - 3, cyEng - 3, 6, 6));
        }

        g2.dispose();
    }
}

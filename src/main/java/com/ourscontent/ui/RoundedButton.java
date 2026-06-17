package com.ourscontent.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private Color normalColor = new Color(55, 55, 58);
    private Color hoverColor = new Color(75, 75, 78);
    private Color activeColor = new Color(40, 40, 42);

    private boolean loading = false;
    private int angle = 0;
    private Timer timer;

    public RoundedButton(String text) {
        super(text);
        init();
    }

    public RoundedButton(String text, Color normal, Color hover, Color active) {
        super(text);
        this.normalColor = normal;
        this.hoverColor = hover;
        this.activeColor = active;
        init();
    }

    private void init() {
        setBackground(normalColor);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!loading) setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!loading) setBackground(normalColor);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (!loading) setBackground(activeColor);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (!loading) setBackground(hoverColor);
            }
        });
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        setEnabled(!loading);
        if (loading) {
            if (timer == null) {
                timer = new Timer(50, e -> {
                    angle = (angle + 30) % 360;
                    repaint();
                });
            }
            timer.start();
        } else {
            if (timer != null) {
                timer.stop();
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        
        if (loading) {
            g2.setColor(getForeground());
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int size = 16;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.drawArc(x, y, size, size, angle, 270);
            g2.dispose();
        } else {
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

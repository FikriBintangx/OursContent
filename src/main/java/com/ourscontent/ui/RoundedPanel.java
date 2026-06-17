package com.ourscontent.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    private final int radius;

    public RoundedPanel(LayoutManager layout, int radius) {
        super(layout);
        this.radius = radius;
        setOpaque(false);
    }

    public RoundedPanel(int radius) {
        this(new BorderLayout(), radius);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(MainFrame.borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
    }
}

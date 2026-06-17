package com.ourscontent.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class OurIsland extends JWindow {

    public enum IslandType {
        SUCCESS, INFO, ERROR
    }

    private int targetX, targetY;
    private int startY;
    private int currentY;
    private int islandWidth = 320;
    private int islandHeight = 50;
    private Timer slideTimer;
    private Timer dismissTimer;
    private JFrame parent;

    public static void show(JFrame parent, String message, IslandType type) {
        OurIsland island = new OurIsland(parent, message, type);
        island.startAnimation();
    }

    // fallback kalau component/panel yang dipassing bukan JFrame
    public static void show(Component comp, String message, IslandType type) {
        Window window = SwingUtilities.getWindowAncestor(comp);
        if (window instanceof JFrame) {
            show((JFrame) window, message, type);
        } else {
            // kalau ga ada parent JFrame, tampilkan dengan parent null
            show(null, message, type);
        }
    }

    private OurIsland(JFrame parent, String message, IslandType type) {
        super(parent);
        this.parent = parent;
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(44, 44, 46));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                g2.setColor(new Color(58, 58, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 18, 10, 18));

        JLabel lblIcon = new JLabel(new IslandIcon(type));

        JLabel lblMsg = new JLabel(message);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMsg.setForeground(new Color(245, 245, 247));

        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(lblMsg, BorderLayout.CENTER);
        
        setContentPane(panel);
        setSize(islandWidth, islandHeight);
        setShape(new RoundRectangle2D.Double(0, 0, islandWidth, islandHeight, 24, 24));

        // hitung posisi koordinat
        if (parent != null && parent.isVisible()) {
            targetX = parent.getX() + (parent.getWidth() - islandWidth) / 2;
            targetY = parent.getY() + 30;
            startY = parent.getY() - islandHeight;
        } else {
            // fallback ke tengah layar
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            targetX = (screen.width - islandWidth) / 2;
            targetY = 80;
            startY = -islandHeight;
        }
        
        currentY = startY;
        setLocation(targetX, currentY);
    }

    private void startAnimation() {
        setVisible(true);
        
        // animasi slide down
        slideTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentY < targetY) {
                    currentY += Math.max(1, (targetY - currentY) / 4); // efek transisi easing
                    setLocation(targetX, currentY);
                } else {
                    currentY = targetY;
                    setLocation(targetX, currentY);
                    slideTimer.stop();
                    
                    // mulai timer buat nutup toast setelah 2.5 detik
                    dismissTimer.start();
                }
            }
        });

        dismissTimer = new Timer(2500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dismissTimer.stop();
                // animasi slide up
                slideTimer = new Timer(10, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (currentY > startY) {
                            currentY -= Math.max(1, (currentY - startY) / 4); // efek transisi easing
                            setLocation(targetX, currentY);
                        } else {
                            slideTimer.stop();
                            dispose();
                        }
                    }
                });
                slideTimer.start();
            }
        });

        slideTimer.start();
    }

    private static class IslandIcon implements Icon {
        private IslandType type;
        
        public IslandIcon(IslandType type) {
            this.type = type;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (type == IslandType.SUCCESS) {
                g2.setColor(new Color(16, 185, 129));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 2, y + 8, x + 6, y + 12);
                g2.drawLine(x + 6, y + 12, x + 14, y + 3);
            } else if (type == IslandType.ERROR) {
                g2.setColor(new Color(250, 88, 106));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 3, y + 3, x + 13, y + 13);
                g2.drawLine(x + 13, y + 3, x + 3, y + 13);
            } else {
                g2.setColor(new Color(14, 165, 233));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawOval(x + 1, y + 1, 14, 14);
                g2.fillRect(x + 7, y + 4, 2, 2);
                g2.drawLine(x + 8, y + 7, x + 8, y + 11);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}

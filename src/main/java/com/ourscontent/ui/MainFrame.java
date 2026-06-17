package com.ourscontent.ui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel workspace;
    private Map<String, JButton> menuButtons;
    private String currentActiveKey = "dashboard";

    private DashboardPanel dashboardPanel;
    private PlatformPanel platformPanel;
    private ContentPanel contentPanel;
    private PerformaPanel performaPanel;
    private LaporanPanel laporanPanel;
    private ProfilePanel profilePanel;
    private SettingPanel settingPanel;
    private UserManagementPanel userManagementPanel;

    private com.ourscontent.model.User currentUser;
    public static boolean isLightTheme = false;
    public static Color mainBgColor = new Color(28, 28, 30);
    public static Color cardBgColor = new Color(44, 44, 46);
    public static Color borderColor = new Color(58, 58, 60);
    public static Color textPrimaryColor = new Color(245, 245, 247);
    public static Color textSecondaryColor = new Color(161, 161, 170);
    private JPanel sidebar;

    public MainFrame() {
        this(null);
    }

    public MainFrame(com.ourscontent.model.User user) {
        this.currentUser = user;
        // pasang tema gelap FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            // sesuaikan gaya ala Apple
            UIManager.put("Panel.background", new Color(28, 28, 30));
            UIManager.put("TableHeader.background", new Color(44, 44, 46));
            UIManager.put("TableHeader.foreground", new Color(245, 245, 247));
            UIManager.put("Table.background", new Color(44, 44, 46));
            UIManager.put("Table.foreground", new Color(245, 245, 247));
            UIManager.put("Table.gridColor", new Color(58, 58, 60));
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
            
            // bikin sudut membulat global
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ComboBox.arc", 10);
        } catch (Exception ex) {
            System.err.println("Gagal memuat Look and Feel");
        }

        setTitle("OursContent - Sistem Manajemen Konten Kreator");
        setSize(1000, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout(15, 0));
        mainContainer.setBackground(new Color(28, 28, 30));
        mainContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Navigasi Sidebar (Panel Kiri)
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(37, 37, 39));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel logoLabel = new JLabel();
        try {
            java.io.File svgFile = new java.io.File("assets/img/logoourss.svg");
            if (svgFile.exists()) {
                com.formdev.flatlaf.extras.FlatSVGIcon svgIcon = new com.formdev.flatlaf.extras.FlatSVGIcon(svgFile);
                logoLabel.setIcon(svgIcon.derive(140, 140));
            } else {
                logoLabel.setText("OursContent");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                logoLabel.setForeground(new Color(245, 245, 247));
            }
        } catch (Exception ex) {
            logoLabel.setText("OursContent");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            logoLabel.setForeground(new Color(245, 245, 247));
        }
        
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(20));

        menuButtons = new HashMap<>();
        java.util.List<String[]> menuList = new java.util.ArrayList<>();
        menuList.add(new String[]{"dashboard", "Dashboard"});
        menuList.add(new String[]{"platform", "Platform"});
        menuList.add(new String[]{"content", "Content"});
        menuList.add(new String[]{"performa", "Performa"});
        menuList.add(new String[]{"laporan", "Laporan"});

        if (currentUser != null && ("MANAGER".equalsIgnoreCase(currentUser.getRole()) || "ADMIN".equalsIgnoreCase(currentUser.getRole()))) {
            menuList.add(new String[]{"tambahkan_user", "Tambahkan User"});
        }

        for (String[] item : menuList) {
            final String key = item[0];
            JButton btn = createMenuButton(item[1], key);
            btn.addActionListener(e -> switchPanel(key));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
            menuButtons.put(key, btn);
        }
        
        // spacer biar tombol setting/profile/logout ke dorong ke bawah
        sidebar.add(Box.createVerticalGlue());
        
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(58, 58, 60));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(separator);
        sidebar.add(Box.createVerticalStrut(15));

        JButton btnSetting = createMenuButton("Setting", "setting");
        JButton btnProfile = createMenuButton("Profile", "profile");
        JButton btnLogout = createMenuButton("Logout", "logout");

        menuButtons.put("setting", btnSetting);
        menuButtons.put("profile", btnProfile);

        btnSetting.addActionListener(e -> switchPanel("setting"));
        btnProfile.addActionListener(e -> switchPanel("profile"));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        sidebar.add(btnSetting);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnProfile);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnLogout);

        mainContainer.add(sidebar, BorderLayout.WEST);

        // 2. Workspace (Panel Kanan)
        cardLayout = new CardLayout();
        workspace = new JPanel(cardLayout);
        workspace.setOpaque(false);

        dashboardPanel = new DashboardPanel();
        platformPanel = new PlatformPanel();
        contentPanel = new ContentPanel();
        performaPanel = new PerformaPanel();
        laporanPanel = new LaporanPanel();
        profilePanel = new ProfilePanel(this, currentUser);
        settingPanel = new SettingPanel(this, currentUser);
        userManagementPanel = new UserManagementPanel(this);

        workspace.add(dashboardPanel, "dashboard");
        workspace.add(platformPanel, "platform");
        workspace.add(contentPanel, "content");
        workspace.add(performaPanel, "performa");
        workspace.add(laporanPanel, "laporan");
        workspace.add(profilePanel, "profile");
        workspace.add(settingPanel, "setting");
        workspace.add(userManagementPanel, "tambahkan_user");

        mainContainer.add(workspace, BorderLayout.CENTER);

        add(mainContainer);
        switchPanel("dashboard");

        // sembunyiin menu platform kalo user bukan ADMIN
        if (currentUser != null && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JButton platformBtn = menuButtons.get("platform");
            if (platformBtn != null) {
                platformBtn.setVisible(false);
            }
        }
    }

    private ImageIcon getScaledIcon(Image srcImg, int w, int h) {
        java.awt.image.BufferedImage resizedImg = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return new ImageIcon(resizedImg);
    }

    private JButton createMenuButton(String text, final String key) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean isActive = key != null && key.equals(currentActiveKey);
                boolean isHovered = getClientProperty("hovered") != null && (Boolean) getClientProperty("hovered");
                
                boolean isLight = isLightTheme;
                if (isActive) {
                    g2.setColor(isLight ? new Color(210, 210, 215) : new Color(52, 52, 56));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else if (isHovered) {
                    g2.setColor(isLight ? new Color(225, 225, 230) : new Color(44, 44, 46));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                
                if (key != null) {
                    Color iconColor = isActive ? new Color(250, 88, 106) : (isHovered ? (isLight ? new Color(28, 28, 30) : new Color(245, 245, 247)) : (isLight ? new Color(110, 110, 115) : new Color(161, 161, 170)));
                    g2.setColor(iconColor);
                    g2.setStroke(new BasicStroke(1.5f));
                    
                    int x = 14;
                    int y = (getHeight() - 14) / 2;
                    int size = 14;
                    
                    if ("dashboard".equals(key)) {
                        g2.drawRect(x, y, size, size);
                    } else if ("platform".equals(key)) {
                        int h = size / 2 - 1;
                        g2.drawRect(x, y, h, h);
                        g2.drawRect(x + size/2 + 1, y, h, h);
                        g2.drawRect(x, y + size/2 + 1, h, h);
                        g2.drawRect(x + size/2 + 1, y + size/2 + 1, h, h);
                    } else if ("content".equals(key)) {
                        g2.drawRoundRect(x + 2, y, size - 4, size, 3, 3);
                        g2.drawLine(x + 5, y + 4, x + size - 5, y + 4);
                        g2.drawLine(x + 5, y + 7, x + size - 5, y + 7);
                    } else if ("performa".equals(key)) {
                        g2.drawOval(x, y, size, size);
                        g2.fillOval(x + 4, y + 4, size - 8, size - 8);
                    } else if ("laporan".equals(key)) {
                        int[] xPoints = {x + size / 2, x, x + size};
                        int[] yPoints = {y, y + size, y + size};
                        g2.drawPolygon(xPoints, yPoints, 3);
                    } else if ("tambahkan_user".equals(key)) {
                        g2.drawOval(x + 2, y + 1, size - 8, size - 8);
                        g2.drawArc(x, y + 7, size - 4, size - 4, 0, 180);
                        g2.drawLine(x + size - 2, y + 2, x + size - 2, y + 6);
                        g2.drawLine(x + size - 4, y + 4, x + size, y + 4);
                    } else if ("setting".equals(key)) {
                        g2.drawOval(x + 3, y + 3, size - 6, size - 6);
                        for (int i = 0; i < 8; i++) {
                            double angle = i * Math.PI / 4;
                            int cx = x + size / 2;
                            int cy = y + size / 2;
                            int x1 = (int) (cx + Math.cos(angle) * 3);
                            int y1 = (int) (cy + Math.sin(angle) * 3);
                            int x2 = (int) (cx + Math.cos(angle) * 6);
                            int y2 = (int) (cy + Math.sin(angle) * 6);
                            g2.drawLine(x1, y1, x2, y2);
                        }
                    } else if ("profile".equals(key)) {
                        g2.drawOval(x + 3, y + 1, size - 6, size - 6);
                        g2.drawArc(x + 1, y + 7, size - 2, size - 4, 0, 180);
                    } else if ("logout".equals(key)) {
                        g2.drawArc(x, y, size - 4, size, 90, 180);
                        g2.drawLine(x + 3, y + size / 2, x + size, y + size / 2);
                        g2.drawLine(x + size - 3, y + size / 2 - 3, x + size, y + size / 2);
                        g2.drawLine(x + size - 3, y + size / 2 + 3, x + size, y + size / 2);
                    }
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(161, 161, 170));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 38, 8, 16));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.putClientProperty("hovered", true);
                if (key != null && !key.equals(currentActiveKey)) {
                    btn.setForeground(new Color(245, 245, 247));
                } else if (key == null) {
                    btn.setForeground(new Color(245, 245, 247));
                }
                btn.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.putClientProperty("hovered", false);
                if (key != null && !key.equals(currentActiveKey)) {
                    btn.setForeground(new Color(161, 161, 170));
                } else if (key == null) {
                    btn.setForeground(new Color(161, 161, 170));
                }
                btn.repaint();
            }
        });

        return btn;
    }

    private void switchPanel(String key) {
        currentActiveKey = key;
        // update highlight menu aktif
        boolean isLight = isLightTheme;
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(key)) {
                btn.setForeground(new Color(250, 88, 106));
            } else {
                btn.setForeground(isLight ? new Color(110, 110, 115) : new Color(161, 161, 170));
            }
            btn.repaint();
        }

        // refresh pilihan list dinamis
        if (key.equals("dashboard")) {
            dashboardPanel.refreshData();
        } else if (key.equals("content")) {
            contentPanel.loadPlatforms();
        } else if (key.equals("performa")) {
            performaPanel.loadPublishedContents();
        } else if (key.equals("laporan")) {
            laporanPanel.loadPlatforms();
        }

        cardLayout.show(workspace, key);
    }

    public void setTheme(boolean isLight) {
        this.isLightTheme = isLight;
        if (isLight) {
            mainBgColor = new Color(245, 245, 247);
            cardBgColor = new Color(255, 255, 255);
            borderColor = new Color(229, 229, 231);
            textPrimaryColor = new Color(29, 29, 31);
            textSecondaryColor = new Color(110, 110, 115);
        } else {
            mainBgColor = new Color(28, 28, 30);
            cardBgColor = new Color(44, 44, 46);
            borderColor = new Color(58, 58, 60);
            textPrimaryColor = new Color(245, 245, 247);
            textSecondaryColor = new Color(161, 161, 170);
        }

        try {
            if (isLight) {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
                UIManager.put("Panel.background", mainBgColor);
                UIManager.put("TableHeader.background", mainBgColor);
                UIManager.put("TableHeader.foreground", textPrimaryColor);
                UIManager.put("Table.background", cardBgColor);
                UIManager.put("Table.foreground", textPrimaryColor);
                UIManager.put("Table.gridColor", borderColor);
                UIManager.put("ComboBox.background", cardBgColor);
                UIManager.put("ComboBox.foreground", textPrimaryColor);
                UIManager.put("TextField.background", cardBgColor);
                UIManager.put("TextField.foreground", textPrimaryColor);
                UIManager.put("Label.foreground", textPrimaryColor);
            } else {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
                UIManager.put("Panel.background", mainBgColor);
                UIManager.put("TableHeader.background", new Color(44, 44, 46));
                UIManager.put("TableHeader.foreground", textPrimaryColor);
                UIManager.put("Table.background", new Color(44, 44, 46));
                UIManager.put("Table.foreground", textPrimaryColor);
                UIManager.put("Table.gridColor", new Color(58, 58, 60));
            }

            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ComboBox.arc", 10);

            SwingUtilities.updateComponentTreeUI(this);

            this.getContentPane().setBackground(mainBgColor);
            if (sidebar != null) {
                sidebar.setBackground(isLight ? new Color(255, 255, 255) : new Color(37, 37, 39));
            }

            // update warna komponen secara dinamis
            updateComponentColors(this, isLight);

            // Specific panel updates
            if (userManagementPanel != null) {
                userManagementPanel.updateTheme(isLight);
            }
            if (laporanPanel != null) {
                laporanPanel.updateTheme(isLight);
            }
            if (contentPanel != null) {
                contentPanel.updateTheme(isLight);
            }

            // gambar ulang semua komponen
            SwingUtilities.invokeLater(() -> {
                this.invalidate();
                this.validate();
                this.repaint();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateComponentColors(Container container, boolean isLight) {
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                
                // cari panel berdasarkan warnanya biar aksen warna ga hilang
                Color bg = panel.getBackground();
                if (bg != null) {
                    if (bg.equals(new Color(28, 28, 30)) || bg.equals(new Color(240, 240, 242)) || bg.equals(new Color(242, 242, 247)) || bg.equals(new Color(245, 245, 247))) {
                        panel.setBackground(mainBgColor);
                    } else if (bg.equals(new Color(44, 44, 46)) || bg.equals(Color.WHITE) || bg.equals(new Color(255, 255, 255))) {
                        panel.setBackground(cardBgColor);
                    } else if (bg.equals(new Color(36, 36, 38)) || bg.equals(new Color(52, 52, 56))) {
                        panel.setBackground(isLight ? new Color(245, 245, 247) : new Color(36, 36, 38));
                    }
                }
                updateComponentColors(panel, isLight);
            } else if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                Color fg = label.getForeground();
                if (fg != null) {
                    if (fg.equals(new Color(245, 245, 247)) || fg.equals(Color.WHITE) || fg.equals(new Color(29, 29, 31))) {
                        label.setForeground(textPrimaryColor);
                    } else if (fg.equals(new Color(161, 161, 170)) || fg.equals(new Color(110, 110, 115))) {
                        label.setForeground(textSecondaryColor);
                    }
                }
            } else if (c instanceof JTextField || c instanceof JPasswordField) {
                JTextField tf = (JTextField) c;
                tf.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
                tf.setForeground(textPrimaryColor);
                tf.setCaretColor(isLight ? Color.BLACK : Color.WHITE);
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1),
                        BorderFactory.createEmptyBorder(5, 7, 5, 7)
                ));
            } else if (c instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) c;
                cb.setBackground(isLight ? new Color(245, 245, 247) : new Color(28, 28, 30));
                cb.setForeground(textPrimaryColor);
            } else if (c instanceof JCheckBox) {
                JCheckBox chk = (JCheckBox) c;
                chk.setForeground(textPrimaryColor);
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.setBackground(mainBgColor);
                sp.getViewport().setBackground(mainBgColor);
                updateComponentColors(sp.getViewport(), isLight);
            } else if (c instanceof Container) {
                updateComponentColors((Container) c, isLight);
            }
        }
    }
}

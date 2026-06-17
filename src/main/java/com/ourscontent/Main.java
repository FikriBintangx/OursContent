package com.ourscontent;

import com.ourscontent.ui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            com.ourscontent.ui.LoginFrame loginFrame = new com.ourscontent.ui.LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

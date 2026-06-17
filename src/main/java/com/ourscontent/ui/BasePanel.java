package com.ourscontent.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class BasePanel extends JPanel {

    protected BasePanel() {
        setBackground(MainFrame.mainBgColor);
    }

    protected JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        applyTextInputStyle(tf);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return tf;
    }

    protected JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        applyTextInputStyle(pf);
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return pf;
    }

    private void applyTextInputStyle(JTextField tf) {
        tf.setBackground(MainFrame.mainBgColor);
        tf.setForeground(MainFrame.textPrimaryColor);
        tf.setCaretColor(MainFrame.textPrimaryColor);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MainFrame.borderColor, 1),
                new EmptyBorder(5, 7, 5, 7)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    protected <T> void styleComboBox(JComboBox<T> cb) {
        cb.setBackground(MainFrame.mainBgColor);
        cb.setForeground(MainFrame.textPrimaryColor);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    protected void styleTable(JTable t) {
        t.setBackground(MainFrame.cardBgColor);
        t.setForeground(MainFrame.textPrimaryColor);
        t.setGridColor(MainFrame.borderColor);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(26);
        t.getTableHeader().setBackground(MainFrame.borderColor);
        t.getTableHeader().setForeground(MainFrame.textPrimaryColor);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setSelectionBackground(new Color(52, 52, 56));
        t.setSelectionForeground(new Color(250, 88, 106));
        t.setFillsViewportHeight(true);
    }

    protected JButton createStyledButton(String text) {
        return new RoundedButton(text);
    }

    protected DefaultTableModel createReadOnlyTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected void addFormField(JPanel panel, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(MainFrame.textSecondaryColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(component);
        panel.add(Box.createVerticalStrut(10));
    }

    protected void addFormFieldGbc(JPanel panel, String labelText, JComponent component, int col, int row) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(MainFrame.textSecondaryColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(component);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, col == 0 ? 0 : 10, 5, col == 2 ? 0 : 10);

        panel.add(wrapper, gbc);
    }

    protected JLabel makeSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(MainFrame.textPrimaryColor);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        return lbl;
    }

    protected JLabel makeCardTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(MainFrame.textPrimaryColor);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}

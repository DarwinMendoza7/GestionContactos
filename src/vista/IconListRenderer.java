package vista;

import javax.swing.*;

import java.awt.Color;
import java.awt.Component;

public class IconListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof IconComboItem) {
            IconComboItem item = (IconComboItem) value;
            label.setText(item.getText());
            label.setIcon(item.getIcon());
        }
        
        return label;
    }
}
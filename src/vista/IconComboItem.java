package vista;

import javax.swing.Icon;

public class IconComboItem {
    private String text;
    private Icon icon;
    
    public IconComboItem(String text, Icon icon) {
        this.text = text;
        this.icon = icon;
    }
    
    public String getText() {
        return text;
    }
    
    public Icon getIcon() {
        return icon;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
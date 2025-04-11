package main;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import vista.Ventana;

public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set look and feel to the system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    // Create and show the main window
                    Ventana ventana = new Ventana();
                    ventana.setVisible(true);
                    ventana.setLocationRelativeTo(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
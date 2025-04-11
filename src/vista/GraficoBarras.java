package vista;

import java.awt.*;
import javax.swing.JPanel;
import java.util.Map;

public class GraficoBarras extends JPanel {
    private Map<String, Integer> datos;
    private String titulo;

    public GraficoBarras(Map<String, Integer> datos, String titulo) {
        this.datos = datos;
        this.titulo = titulo;
        setBackground(Color.WHITE);
        // Establecer un tamaño preferido para asegurar visibilidad
        setPreferredSize(new Dimension(600, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        int margen = 50;
        int anchoBarras = (ancho - 2 * margen) / datos.size();

        // Encontrar el valor máximo para escalar
        int maxValor = datos.values().stream().max(Integer::compare).orElse(1);
        
        // Si el valor máximo es 0, usar 1 para evitar división por cero
        if (maxValor == 0) maxValor = 1;

        // Dibujar título
        g2d.setFont(new Font("Tahoma", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int anchoTitulo = fm.stringWidth(titulo);
        g2d.drawString(titulo, (ancho - anchoTitulo) / 2, 30);

        // Dibujar ejes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(margen, alto - margen, ancho - margen, alto - margen); // Eje X
        g2d.drawLine(margen, margen, margen, alto - margen); // Eje Y

        // Dibujar barras
        int x = margen + 10;
        g2d.setFont(new Font("Tahoma", Font.PLAIN, 12));

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            String categoria = entry.getKey();
            int valor = entry.getValue();

            // Calcular altura de la barra proporcional al valor
            int altoBarra = (int) (((alto - 2 * margen) * valor) / (double) maxValor);
            
            // Asegurar que la barra tenga al menos 1 pixel de altura si el valor es mayor que 0
            if (valor > 0 && altoBarra < 1) {
                altoBarra = 1;
            }

            // Dibujar barra
            switch (categoria) {
                case "Familia":
                    g2d.setColor(new Color(65, 105, 225)); // Azul real
                    break;
                case "Amigos":
                    g2d.setColor(new Color(34, 139, 34)); // Verde bosque
                    break;
                case "Trabajo":
                    g2d.setColor(new Color(178, 34, 34)); // Rojo ladrillo
                    break;
                case "Favoritos":
                    g2d.setColor(new Color(255, 215, 0)); // Dorado
                    break;
                default:
                    g2d.setColor(new Color(100, 100, 100)); // Gris
            }

            g2d.fillRect(x, alto - margen - altoBarra, anchoBarras - 20, altoBarra);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, alto - margen - altoBarra, anchoBarras - 20, altoBarra);

            // Dibujar etiqueta
            fm = g2d.getFontMetrics();
            int anchoEtiqueta = fm.stringWidth(categoria);
            g2d.drawString(categoria, x + (anchoBarras - 20 - anchoEtiqueta) / 2, alto - margen + 20);

            // Dibujar valor
            String valorStr = String.valueOf(valor);
            int anchoValor = fm.stringWidth(valorStr);
            g2d.drawString(valorStr, x + (anchoBarras - 20 - anchoValor) / 2, alto - margen - altoBarra - 5);

            x += anchoBarras;
        }
    }
}
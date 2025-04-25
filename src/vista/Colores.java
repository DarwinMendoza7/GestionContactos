package vista;

import java.awt.Color;


  //Clase que define los colores personalizados para la interfaz gráfica
	public class Colores {
    // Colores principales para la ventana
	    public static final Color FONDO = new Color(245, 245, 245); //Fondo principal claro
	    public static final Color FONDO_GENERAL = new Color(255, 255, 255); //blanco puro para fondos generales
	    public static final Color BARRA_TITULO = new Color(230, 230, 230); // gris claro para barra de título
	    public static final Color HOVER = new Color(126, 217, 87); // verde claro para resaltar elementos al pasar el cursos
	    
	    // Colores para paneles y componentes
	    public static final Color PANEL_FORMULARIO = new Color(249, 249, 249); //Fondo de formularios
	    public static final Color PANEL_BUSQUEDA = new Color(249, 249, 249); //Fondo del panel de búsqueda
	    
	    // Colores para textos
	    public static final Color TEXTO_PRINCIPAL = new Color(51, 51, 51);  //Texto principal oscuro para legibilidad
	    public static final Color TEXTO_SECUNDARIO = new Color(102, 102, 102); //Texto secundario más suave
	    
	    // Color para bordes
	    public static final Color BORDE = new Color(204, 204, 204);  //bordes suaves
	    
	    // Colores para botones
	    public static final Color BOTON_PRIMARIO = new Color(70, 130, 180); //Azuil acero para botones principales
	    public static final Color BOTON_TEXTO = new Color(255, 255, 255);  //Texto blanco sobre botones
	    
	    // Colores para tablas
	    public static final Color FILA_PAR = new Color(249, 249, 249); // Colores de filas pares
	    public static final Color FILA_IMPAR = new Color(255, 255, 255);  //Colores de filas impares (blanco)
	    public static final Color CABECERA_TABLA = new Color(70, 130, 180); //Fondo azul acero para cabecera
	    public static final Color TEXTO_CABECERA = new Color(255, 255, 255);  //Texto blanco en cabecera
	}
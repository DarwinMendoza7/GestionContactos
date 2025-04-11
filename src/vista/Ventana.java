package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import controlador.Logica_ventana;
import modelo.Persona;

public class Ventana extends JFrame {

    public JPanel contentPane; // Panel principal que contendrá todos los componentes de la interfaz.
    public JTextField txt_nombres; // Campo de texto para ingresar nombres.
    public JTextField txt_telefono; // Campo de texto para ingresar números de teléfono.
    public JTextField txt_email; // Campo de texto para ingresar direcciones de correo electrónico.
    public JTextField txt_buscar; // Campo de texto adicional.
    public JCheckBox chb_favorito; // Casilla de verificación para marcar un contacto como favorito.
    public JComboBox<String> cmb_categoria; //Menú desplegable para seleccionar la categoría de contacto.
    public JButton btn_add; // Botón para agregar un nuevo contacto.
    public JButton btn_modificar; // Botón para modificar un contacto existente.
    public JButton btn_eliminar; // Botón para eliminar un contacto.
    public JButton btn_exportar; //Botón para exportar contactos
    public JList<String> lst_contactos; // Lista para mostrar los contactos.
    public JScrollPane scrLista; // Panel de desplazamiento para la lista de contactos.
    public JTabbedPane tabbedPane; //Panel con pestañas para organizar las secciones de Contactos y Estadísticas
    public JPanel panelContactos; //Panel que contiene los componentes para la gestión de contactos.
    public JPanel panelEstadisticas; //Panel que contiene los componentes para mostrar las estadísticas.
    public JTable tablaContactos; //Tabla para mostrar la lista de contactos.
    public DefaultTableModel modeloTabla; //Modelo de tabla para la tabla de contactos.
    public JProgressBar progressBar; //Barra de progreso para mostrar el estado de las operaciones.
    public JPopupMenu popupMenu; //Menú contextual para la tabla de contactos.
    public JMenuItem menuItemEditar; //Item del menú contextual para editar un contacto.
    public JMenuItem menuItemEliminar; //Item del menú contextual para eliminar un contacto.
    public JPanel panelGraficas; //Panel para mostrar gráficos de las estadísticas.
    
    // Componentes para estadísticas
    public JLabel lblTotalContactos; //Label para mostrar el total de contactos.
    public JLabel lblFavoritos; //Label para mostrar el número de contactos favoritos.
    public JLabel lblFamilia; //Label para mostrar el número de contactos de la categoría Familia.
    public JLabel lblAmigos; //Label para mostrar el número de contactos de la categoría Amigos.
    public JLabel lblTrabajo; //Label para mostrar el número de contactos de la categoría Trabajo.
    
    //Constructor de la clase Ventana. Inicializa y configura todos los componentes de la interfaz gráfica
    public Ventana() {
        setTitle("GESTIÓN DE CONTACTOS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setBounds(100, 100, 1026, 748);
        
        // Configurar atajos de teclado globales
        configurarAtajos();
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        // Crear el JTabbedPane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel de Contactos
        panelContactos = new JPanel();
        tabbedPane.addTab("Contactos", null, panelContactos, "Gestionar contactos");
        panelContactos.setLayout(null);
        
        JLabel lbl_etiqueta1 = new JLabel("NOMBRES:");
        lbl_etiqueta1.setBounds(25, 41, 89, 13);
        lbl_etiqueta1.setFont(new Font("Tahoma", Font.BOLD, 15));
        panelContactos.add(lbl_etiqueta1);
        
        JLabel lbl_etiqueta2 = new JLabel("TELÉFONO:");
        lbl_etiqueta2.setBounds(25, 80, 89, 13);
        lbl_etiqueta2.setFont(new Font("Tahoma", Font.BOLD, 15));
        panelContactos.add(lbl_etiqueta2);
        
        JLabel lbl_etiqueta3 = new JLabel("EMAIL:");
        lbl_etiqueta3.setBounds(25, 122, 89, 13);
        lbl_etiqueta3.setFont(new Font("Tahoma", Font.BOLD, 15));
        panelContactos.add(lbl_etiqueta3);
        
        JLabel lbl_etiqueta4 = new JLabel("BUSCAR:");
        lbl_etiqueta4.setFont(new Font("Tahoma", Font.BOLD, 15));
        lbl_etiqueta4.setBounds(25, 215, 89, 13);
        panelContactos.add(lbl_etiqueta4);
        
        txt_nombres = new JTextField();
        txt_nombres.setBounds(124, 28, 427, 31);
        txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 15));
        panelContactos.add(txt_nombres);
        txt_nombres.setColumns(10);
        
        txt_telefono = new JTextField();
        txt_telefono.setBounds(124, 69, 427, 31);
        txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_telefono.setColumns(10);
        panelContactos.add(txt_telefono);
        
        txt_email = new JTextField();
        txt_email.setBounds(124, 110, 427, 31);
        txt_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_email.setColumns(10);
        panelContactos.add(txt_email);
        
        txt_buscar = new JTextField();
        txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_buscar.setColumns(10);
        txt_buscar.setBounds(124, 205, 427, 31);
        panelContactos.add(txt_buscar);
        
        chb_favorito = new JCheckBox("CONTACTO FAVORITO");
        chb_favorito.setBounds(24, 170, 193, 21);
        chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 15));
        panelContactos.add(chb_favorito);
        
        cmb_categoria = new JComboBox<>();
        cmb_categoria.setBounds(300, 167, 251, 31);
        panelContactos.add(cmb_categoria);
        
        String[] categorias = {"Elija una Categoría", "Familia", "Amigos", "Trabajo"};
        for (String categoria : categorias) {
            cmb_categoria.addItem(categoria);
        }
        
        btn_add = new JButton("AGREGAR");
        btn_add.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_add.setBounds(577, 28, 125, 50);
        panelContactos.add(btn_add);
        
        btn_modificar = new JButton("MODIFICAR");
        btn_modificar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_modificar.setBounds(712, 28, 125, 50);
        panelContactos.add(btn_modificar);
        
        btn_eliminar = new JButton("ELIMINAR");
        btn_eliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_eliminar.setBounds(847, 28, 125, 50);
        panelContactos.add(btn_eliminar);
        
        btn_exportar = new JButton("EXPORTAR");
        btn_exportar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_exportar.setBounds(712, 103, 125, 50);
        panelContactos.add(btn_exportar);
        
        progressBar = new JProgressBar();
        progressBar.setBounds(25, 640, 971, 20);
        progressBar.setStringPainted(true);
        panelContactos.add(progressBar);
        
        // Configurando tabla
        String[] columnas = {"Nombre", "Teléfono", "Email", "Categoría", "Favorito"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Boolean.class; // Columna "Favorito" como checkbox
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable directamente
            }
        };
        
        tablaContactos = new JTable(modeloTabla);
        tablaContactos.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tablaContactos.setRowHeight(25);
        tablaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Ordenamiento de tabla
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaContactos.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(tablaContactos);
        scrollPane.setBounds(25, 250, 705, 380);  // Reducido el ancho para hacer espacio para la lista
        panelContactos.add(scrollPane);
        
        // Inicializar la lista de contactos
        lst_contactos = new JList<>();
        lst_contactos.setFont(new Font("Tahoma", Font.PLAIN, 14));
        scrLista = new JScrollPane(lst_contactos);
        scrLista.setBounds(736, 250, 260, 380);
        panelContactos.add(scrLista);
        
        // Menú contextual para la tabla
        popupMenu = new JPopupMenu();
        menuItemEditar = new JMenuItem("Editar");
        menuItemEliminar = new JMenuItem("Eliminar");
        popupMenu.add(menuItemEditar);
        popupMenu.add(menuItemEliminar);
        
        // Panel de Estadísticas
        panelEstadisticas = new JPanel();
        tabbedPane.addTab("Estadísticas", null, panelEstadisticas, "Ver estadísticas de contactos");
        panelEstadisticas.setLayout(new BorderLayout(0, 0));
        
        JPanel panelInfoEstadisticas = new JPanel();
        panelInfoEstadisticas.setLayout(new GridLayout(5, 1, 10, 10));
        panelInfoEstadisticas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        lblTotalContactos = new JLabel("Total de contactos: 0");
        lblTotalContactos.setFont(new Font("Tahoma", Font.BOLD, 16));
        
        lblFavoritos = new JLabel("Contactos favoritos: 0");
        lblFavoritos.setFont(new Font("Tahoma", Font.PLAIN, 16));
        
        lblFamilia = new JLabel("Contactos de familia: 0");
        lblFamilia.setFont(new Font("Tahoma", Font.PLAIN, 16));
        
        lblAmigos = new JLabel("Contactos de amigos: 0");
        lblAmigos.setFont(new Font("Tahoma", Font.PLAIN, 16));
        
        lblTrabajo = new JLabel("Contactos de trabajo: 0");
        lblTrabajo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        
        panelInfoEstadisticas.add(lblTotalContactos);
        panelInfoEstadisticas.add(lblFavoritos);
        panelInfoEstadisticas.add(lblFamilia);
        panelInfoEstadisticas.add(lblAmigos);
        panelInfoEstadisticas.add(lblTrabajo);
        
        panelEstadisticas.add(panelInfoEstadisticas, BorderLayout.NORTH);
        
        panelGraficas = new JPanel();
        panelGraficas.setLayout(new BorderLayout());
        panelEstadisticas.add(panelGraficas, BorderLayout.CENTER);
        
        // Inicializar el controlador
        new Logica_ventana(this);
    }
    
    //Configura los atajos de teclado para acciones comunes
    private void configurarAtajos() {
        // Atajos de teclado globales
        KeyStroke keyStrokeAdd = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke keyStrokeDelete = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke keyStrokeModify = KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke keyStrokeExport = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK);
        
        // Registrar las acciones con nombres
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeAdd, "agregar");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeDelete, "eliminar");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeModify, "modificar");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeExport, "exportar");
        
        // Acciones
        getRootPane().getActionMap().put("agregar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn_add.doClick();
            }
        });
        
        getRootPane().getActionMap().put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn_eliminar.doClick();
            }
        });
        
        getRootPane().getActionMap().put("modificar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn_modificar.doClick();
            }
        });
        
        getRootPane().getActionMap().put("exportar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn_exportar.doClick();
            }
        });
    }
}
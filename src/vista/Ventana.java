package vista;

import javax.swing.*;
import vista.Colores;
import vista.Fuentes;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Locale;
import javax.swing.ImageIcon;

import controlador.Logica_ventana;
import modelo.Persona;
import util.ContactLockManager;
import util.InternationalizationManager;

public class Ventana extends JFrame {

    public JPanel contentPane;  //Panel principal que contiene todos los elementos de la ventana
    public JTextField txt_nombres; //Campo para el nombre del contacto
    public JTextField txt_telefono; //Campo para el teléfono del contacto
    public JTextField txt_email; //Campo para el email del contacto
    public JTextField txt_buscar; //Campo para buscar contactos por texto
    public JCheckBox chb_favorito; //Chexkbox para marcar un contacto como favorito
    public JComboBox<IconComboItem> cmb_categoria; // ComboBox para seleccionar la categoría del contacto (familia, amigos, trabajo, etc)
    public JButton btn_add; //Botón para agregar un nuevo contacto
    public JButton btn_modificar; //Botón para modificar el contacto seleccionado
    public JButton btn_eliminar; //Botón para eliminar el contacto seleccionado
    public JButton btn_exportar; //Botón para exportar los contactos a CSV
    public JList<String> lst_contactos; //Lista visual de contactos
    public JScrollPane scrLista; //ScrollPane que contiene la lista de contactos
    public JTabbedPane tabbedPane; //Pestañas principales (Contactos, Estadísticas)
    public JPanel panelContactos; //Panel para la gestión de contactos
    public JPanel panelEstadisticas; //Panel para mostrar estadísticas
    public JTable tablaContactos; //Tabla para mostrar los contactos en formato tabular
    public DefaultTableModel modeloTabla; //Modelo de datos para la tabla de contactos
    public JProgressBar progressBar; //Barra de progreso para mostrar operaciones en curso
    public JPopupMenu popupMenu; //Menú contextual (clic derecho) sobre la tabla de contactos
    public JMenuItem menuItemEditar; //Opción para editar contacto desde el menú contextual
    public JMenuItem menuItemEliminar; //Opción para eliminar contacto desde el menú contextual
    public JPanel panelGraficas; //Panel para mostrar gráficos
    //Etiquetas para los campos y la lista
    public JLabel lbl_nombres;  //Etiqueta para el campo de nombre del contacto
    public JLabel lbl_telefono; //Etiqueta para el campo de teléfono del contacto
    public JLabel lbl_email; //Etiqueta para el campo de email del contacto
    public JLabel lbl_buscar; //Etiqueta para el campo de búsqueda de contactos
    public JLabel lbl_listaContactos; //Etiqueta para la lista visual de contactos
    
    // Componentes para estadísticas
    public JLabel lblTotalContactos; //Total de contactos registrados
    public JLabel lblFavoritos; //Total de contactos marcados como favoritos
    public JLabel lblFamilia; //Total de contactos en la categoría familia
    public JLabel lblAmigos; //Total de contactos en la categoría amigos
    public JLabel lblTrabajo; //Total de contactos en la categoría trabajo
    
    public JMenuBar menuBar; //Barra de menú principal de la aplicación
    
    private Logica_ventana logica; //Referencia a la lógica/controlador principal que maneja los eventos y operaciones
    
    public Ventana() {
        // Configurar idioma inicial
        InternationalizationManager.setLocale(new Locale("es"));
        
        setTitle(InternationalizationManager.getString("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setBounds(100, 100, 800, 750);
        
        // Configurar menú de idiomas
        menuBar = new JMenuBar();
        JMenu languageMenu = new JMenu();
        
        ImageIcon languageIcon = loadIcon("/icons/idioma.png", 16, 16);
        languageMenu.setText(InternationalizationManager.getString("menu.language"));
        languageMenu.setIcon(languageIcon);
              
        //Crear items de menú con iconos
        ImageIcon spanishIcon = loadIcon("/icons/español.png",16 ,16);
        ImageIcon englishIcon = loadIcon("/icons/inglés.png",16, 16);
        ImageIcon frenchIcon = loadIcon("/icons/francés.png", 16, 16);
        
        JMenuItem spanishItem = new JMenuItem("Español",spanishIcon);
        JMenuItem englishItem = new JMenuItem("English", englishIcon);
        JMenuItem frenchItem = new JMenuItem("Français", frenchIcon);

        spanishItem.addActionListener(e -> {
            InternationalizationManager.setLocale(new Locale("es"));
            updateUITexts();
            logica.actualizarDatosIdioma();
        });

        englishItem.addActionListener(e -> {
            InternationalizationManager.setLocale(new Locale("en"));
            updateUITexts();
            logica.actualizarDatosIdioma();
        });

        frenchItem.addActionListener(e -> {
            InternationalizationManager.setLocale(new Locale("fr"));
            updateUITexts();
            logica.actualizarDatosIdioma();
        });

        languageMenu.add(spanishItem);
        languageMenu.add(englishItem);
        languageMenu.add(frenchItem);
        menuBar.add(languageMenu);
        setJMenuBar(menuBar);
        
        // Configurar atajos de teclado globales
        configurarAtajos();
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBackground(Colores.FONDO);
        
        // Crear el JTabbedPane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
             
        // Panel de Contactos
        panelContactos = new JPanel();
        panelContactos.setBackground(Colores.FONDO_GENERAL);
        tabbedPane.addTab(InternationalizationManager.getString("menu.contacts"), null, panelContactos, null);
        panelContactos.setLayout(new BorderLayout());
        
        // Panel superior para los campos de entrada y botones
        JPanel panelFormulario = new JPanel();
        panelFormulario.setBackground(Colores.PANEL_FORMULARIO);
        panelFormulario.setBorder(BorderFactory.createLineBorder(Colores.BORDE, 1));
        panelFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Panel para búsqueda y resultados
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(Colores.FONDO_GENERAL);
        panelInferior.setLayout(new BorderLayout(10, 10));
        
        // Configuramos los componentes con GridBagLayout para mejor alineación
        lbl_nombres = new JLabel(InternationalizationManager.getString("label.names"));
        lbl_nombres.setFont(Fuentes.TITULO);
        lbl_nombres.setForeground(Colores.TEXTO_PRINCIPAL);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(lbl_nombres, gbc);
        
        txt_nombres = new JTextField();
        txt_nombres.setFont(Fuentes.TEXTO_NORMAL);
        txt_nombres.setColumns(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panelFormulario.add(txt_nombres, gbc);
        
        lbl_telefono = new JLabel(InternationalizationManager.getString("label.phone"));
        lbl_telefono.setFont(Fuentes.TITULO);
        lbl_telefono.setForeground(Colores.TEXTO_PRINCIPAL);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(lbl_telefono, gbc);
        
        txt_telefono = new JTextField();
        txt_telefono.setFont(Fuentes.TEXTO_NORMAL);
        txt_telefono.setColumns(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panelFormulario.add(txt_telefono, gbc);
        
        lbl_email = new JLabel(InternationalizationManager.getString("label.email"));
        lbl_email.setFont(Fuentes.TITULO);
        lbl_email.setForeground(Colores.TEXTO_PRINCIPAL);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(lbl_email, gbc);
        
        txt_email = new JTextField();
        txt_email.setFont(Fuentes.TEXTO_NORMAL);
        txt_email.setColumns(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panelFormulario.add(txt_email, gbc);
        
        // Panel para checkbox y combobox
        JPanel panelOpciones = new JPanel();
        panelOpciones.setBackground(Colores.PANEL_FORMULARIO);
        panelOpciones.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        chb_favorito = new JCheckBox(InternationalizationManager.getString("checkbox.favorite"));
        chb_favorito.setFont(Fuentes.TEXTO_NORMAL);
        chb_favorito.setBackground(Colores.PANEL_FORMULARIO);
        chb_favorito.setForeground(Colores.TEXTO_PRINCIPAL);
        panelOpciones.add(chb_favorito);
        
        cmb_categoria = new JComboBox<>();
        cmb_categoria.setFont(Fuentes.TEXTO_NORMAL);
        cmb_categoria.setPreferredSize(new Dimension(220, 25));
        cmb_categoria.setBackground(Colores.BOTON_PRIMARIO);
        cmb_categoria.setForeground(Colores.TEXTO_SECUNDARIO);
        panelOpciones.add(cmb_categoria);
        
        actualizarComboCategorias();
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(panelOpciones, gbc);
        
        // Panel para botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Colores.PANEL_FORMULARIO);
        panelBotones.setLayout(new GridLayout(2, 2, 10, 10));
        
        btn_add = new JButton(InternationalizationManager.getString("button.add"));
        btn_add.setBackground(Colores.BOTON_PRIMARIO);
        btn_add.setForeground(Colores.BOTON_TEXTO);
        btn_add.setFont(Fuentes.TEXTO_NORMAL);
        btn_add.setPreferredSize(new Dimension(100, 50));
        btn_add.setOpaque(true);
        btn_add.setBorderPainted(false);
        btn_add.setFocusPainted(false);
        panelBotones.add(btn_add);
        
        btn_modificar = new JButton(InternationalizationManager.getString("button.modify"));
        btn_modificar.setBackground(Colores.BOTON_PRIMARIO);
        btn_modificar.setForeground(Colores.BOTON_TEXTO);
        btn_modificar.setFont(Fuentes.TEXTO_NORMAL);
        btn_modificar.setPreferredSize(new Dimension(100, 50));
        btn_modificar.setOpaque(true);
        btn_modificar.setBorderPainted(false);
        btn_modificar.setFocusPainted(false);
        panelBotones.add(btn_modificar);
        
        btn_eliminar = new JButton(InternationalizationManager.getString("button.delete"));
        btn_eliminar.setBackground(Colores.BOTON_PRIMARIO);
        btn_eliminar.setForeground(Colores.BOTON_TEXTO);
        btn_eliminar.setFont(Fuentes.TEXTO_NORMAL);
        btn_eliminar.setPreferredSize(new Dimension(100, 50));
        btn_eliminar.setOpaque(true);
        btn_eliminar.setBorderPainted(false);
        btn_eliminar.setFocusPainted(false);
        panelBotones.add(btn_eliminar);
        
        btn_exportar = new JButton(InternationalizationManager.getString("button.export"));
        btn_exportar.setBackground(Colores.BOTON_PRIMARIO);
        btn_exportar.setForeground(Colores.BOTON_TEXTO);
        btn_exportar.setFont(Fuentes.TEXTO_NORMAL);
        btn_exportar.setPreferredSize(new Dimension(100, 50));
        btn_exportar.setOpaque(true);
        btn_exportar.setBorderPainted(false);
        btn_exportar.setFocusPainted(false);
        panelBotones.add(btn_exportar);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panelFormulario.add(panelBotones, gbc);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBackground(Colores.BOTON_PRIMARIO);
        panelBusqueda.setBorder(BorderFactory.createLineBorder(Colores.BORDE, 1));
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setPreferredSize(new Dimension(760, 40));
        
        lbl_buscar = new JLabel(InternationalizationManager.getString("label.search"));
        lbl_buscar.setFont(Fuentes.TITULO);
        lbl_buscar.setForeground(Colores.BOTON_TEXTO);
        panelBusqueda.add(lbl_buscar);
        
        JLabel lblSearch = new JLabel();
        ImageIcon searchIcon = loadIcon("/icons/search.png",20,20);
        if(searchIcon != null) {
            lblSearch.setIcon(searchIcon);
            lblSearch.setPreferredSize(new Dimension(20, 20));
        }
        
        panelBusqueda.add(lblSearch);
        
        txt_buscar = new JTextField();
        txt_buscar.setFont(Fuentes.TEXTO_NORMAL);
        txt_buscar.setColumns(30);
        panelBusqueda.add(txt_buscar);
        
        panelInferior.add(panelBusqueda, BorderLayout.NORTH);
        
        // Panel para la tabla y lista
        JPanel panelTablaLista = new JPanel();
        panelTablaLista.setLayout(new BorderLayout(10, 0));
        panelTablaLista.setBackground(Colores.FONDO_GENERAL);
        
        // Configurando tabla
        String[] columnas = {
            InternationalizationManager.getString("table.header.name"),
            InternationalizationManager.getString("table.header.phone"),
            InternationalizationManager.getString("table.header.email"),
            InternationalizationManager.getString("table.header.category"),
            InternationalizationManager.getString("table.header.favorite")
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Boolean.class;
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaContactos = new JTable(modeloTabla);
        tablaContactos.setFont(Fuentes.TEXTO_NORMAL);
        tablaContactos.setRowHeight(25);
        tablaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaContactos.setGridColor(Colores.BORDE);

        tablaContactos.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Colores.BOTON_PRIMARIO);
                label.setForeground(Color.WHITE);
                label.setFont(Fuentes.TEXTO_NORMAL);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Colores.BORDE));
                return label;
            }
        });

        tablaContactos.getTableHeader().setOpaque(true);
        tablaContactos.getTableHeader().setBackground(Colores.BOTON_PRIMARIO);
        tablaContactos.getTableHeader().setForeground(Color.WHITE);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaContactos.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(tablaContactos);
        scrollPane.setBorder(BorderFactory.createLineBorder(Colores.BORDE, 1));
        panelTablaLista.add(scrollPane, BorderLayout.CENTER);
        
        // Inicializar la lista de contactos
        lst_contactos = new JList<>();
        lst_contactos.setFont(Fuentes.TEXTO_NORMAL);
        lst_contactos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Configurar panel para lista de contactos
        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createLineBorder(Colores.BORDE, 1));
        
        // Añadir cabecera para la lista
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Colores.CABECERA_TABLA);
        headerPanel.setPreferredSize(new Dimension(230, 30));
        
        JPanel headerContentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5, 0));
        headerContentPanel.setBackground(Colores.CABECERA_TABLA);
        
        ImageIcon contactListIcon = loadIcon("/icons/contactos.png",24,24);
        JLabel iconLabel = new JLabel();
        if(contactListIcon != null) {
            iconLabel.setIcon(contactListIcon);
        }
        headerContentPanel.add(iconLabel);
        
        lbl_listaContactos = new JLabel(InternationalizationManager.getString("list.title"));
        lbl_listaContactos.setForeground(Colores.TEXTO_CABECERA);
        lbl_listaContactos.setFont(Fuentes.TEXTO_NORMAL);
        headerContentPanel.add(lbl_listaContactos);
       
        headerPanel.add(headerContentPanel, BorderLayout.CENTER);
        
        panelLista.add(headerPanel, BorderLayout.NORTH);
        
        scrLista = new JScrollPane(lst_contactos);
        scrLista.setBorder(null);
        panelLista.add(scrLista, BorderLayout.CENTER);
        panelLista.setPreferredSize(new Dimension(230, 0));
        
        panelTablaLista.add(panelLista, BorderLayout.EAST);
        
        panelInferior.add(panelTablaLista, BorderLayout.CENTER);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setFont(Fuentes.TEXTO_PEQUENO);
        progressBar.setStringPainted(true);
        progressBar.setValue(25);
        progressBar.setForeground(Colores.BOTON_PRIMARIO);
        progressBar.setBorder(BorderFactory.createLineBorder(Colores.BORDE, 1));
        panelInferior.add(progressBar, BorderLayout.SOUTH);
        
        // Añadimos los paneles principales al panel de contactos
        panelContactos.add(panelFormulario, BorderLayout.NORTH);
        panelContactos.add(panelInferior, BorderLayout.CENTER);
        
        // Menú contextual para la tabla
        popupMenu = new JPopupMenu();
        menuItemEditar = new JMenuItem(InternationalizationManager.getString("popup.edit"));
        menuItemEditar.setFont(Fuentes.TEXTO_NORMAL);
        menuItemEliminar = new JMenuItem(InternationalizationManager.getString("popup.delete"));
        menuItemEliminar.setFont(Fuentes.TEXTO_NORMAL);
        popupMenu.add(menuItemEditar);
        popupMenu.add(menuItemEliminar);
        
        // Panel de Estadísticas
        panelEstadisticas = new JPanel();
        panelEstadisticas.setBackground(Colores.FONDO_GENERAL);
        tabbedPane.addTab(InternationalizationManager.getString("menu.stats"), null, panelEstadisticas, null);
        panelEstadisticas.setLayout(new BorderLayout(0, 0));
        
        // Personalizar las pestañas
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                lightHighlight = Colores.BORDE;
                shadow = Colores.BORDE;
                darkShadow = Colores.BORDE;
                focus = Colores.BOTON_PRIMARIO;
            }
            
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2D = (Graphics2D) g;
                if (isSelected) {
                    g2D.setColor(Colores.BOTON_PRIMARIO);
                } else {
                    g2D.setColor(Colores.BARRA_TITULO);
                }
                g2D.fillRect(x, y, w, h);
            }
            
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                g.setColor(Colores.BORDE);
                g.drawRect(x, y, w, h);
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // No pintar el borde del contenido
            }
        });
        
        tabbedPane.setForegroundAt(0, isSelectedIndex(0) ? Color.WHITE : Colores.TEXTO_PRINCIPAL);
        tabbedPane.setForegroundAt(1, isSelectedIndex(1) ? Color.WHITE : Colores.TEXTO_PRINCIPAL);
        
        JPanel panelInfoEstadisticas = new JPanel();
        panelInfoEstadisticas.setLayout(new GridLayout(5, 1, 10, 10));
        panelInfoEstadisticas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelInfoEstadisticas.setBackground(Colores.BOTON_PRIMARIO);
        
        lblTotalContactos = new JLabel(InternationalizationManager.getString("stats.total") + " 0");
        lblTotalContactos.setFont(Fuentes.TEXTO_NORMAL);
        lblTotalContactos.setForeground(Colores.TEXTO_CABECERA);
        
        lblFavoritos = new JLabel(InternationalizationManager.getString("stats.favorites") + " 0");
        lblFavoritos.setFont(Fuentes.TEXTO_NORMAL);
        lblFavoritos.setForeground(Colores.TEXTO_CABECERA);
        
        lblFamilia = new JLabel(InternationalizationManager.getString("stats.family") + " 0");
        lblFamilia.setFont(Fuentes.TEXTO_NORMAL);
        lblFamilia.setForeground(Colores.TEXTO_CABECERA);
        
        lblAmigos = new JLabel(InternationalizationManager.getString("stats.friends") + " 0");
        lblAmigos.setFont(Fuentes.TEXTO_NORMAL);
        lblAmigos.setForeground(Colores.TEXTO_CABECERA);
        
        lblTrabajo = new JLabel(InternationalizationManager.getString("stats.work") + " 0");
        lblTrabajo.setFont(Fuentes.TEXTO_NORMAL);
        lblTrabajo.setForeground(Colores.TEXTO_CABECERA);
        
        panelInfoEstadisticas.add(lblTotalContactos);
        panelInfoEstadisticas.add(lblFavoritos);
        panelInfoEstadisticas.add(lblFamilia);
        panelInfoEstadisticas.add(lblAmigos);
        panelInfoEstadisticas.add(lblTrabajo);
        
        panelEstadisticas.add(panelInfoEstadisticas, BorderLayout.NORTH);
        
        panelGraficas = new JPanel();
        panelGraficas.setLayout(new BorderLayout());
        panelGraficas.setBackground(Colores.PANEL_FORMULARIO);
        panelEstadisticas.add(panelGraficas, BorderLayout.CENTER);
        
        // Añadir íconos a los botones
        ImageIcon addIcon = loadIcon("/icons/add.png", 24, 24);
        if (addIcon != null) {
            btn_add.setIcon(addIcon);
            btn_add.setHorizontalTextPosition(SwingConstants.CENTER);
            btn_add.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        
        ImageIcon editIcon = loadIcon("/icons/edit.png", 24, 24);
        if (editIcon != null) {
            btn_modificar.setIcon(editIcon);
            btn_modificar.setHorizontalTextPosition(SwingConstants.CENTER);
            btn_modificar.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        
        ImageIcon deleteIcon = loadIcon("/icons/delete.png", 24, 24);
        if (deleteIcon != null) {
            btn_eliminar.setIcon(deleteIcon);
            btn_eliminar.setHorizontalTextPosition(SwingConstants.CENTER);
            btn_eliminar.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        
        ImageIcon exportIcon = loadIcon("/icons/export.png", 24, 24);
        if (exportIcon != null) {
            btn_exportar.setIcon(exportIcon);
            btn_exportar.setHorizontalTextPosition(SwingConstants.CENTER);
            btn_exportar.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Liberar todos los bloqueos al cerrar la ventana
                ContactLockManager.getInstance().releaseAllLocks();
                
                // Cerrar los hilos del ejecutor
                if (logica != null) {
                    logica.shutdown();
                }
                
                // Cierra la aplicación
                dispose();
                System.exit(0);
            }
        });
        // Inicializar el controlador
        logica = new Logica_ventana(this);
    }
    
    public void actualizarTablaContactos() {
        DefaultTableModel model = (DefaultTableModel) tablaContactos.getModel();
        model.setRowCount(0);
        
        for (Persona contacto : logica.getContactos()) {
            if (!contacto.getNombre().equals("NOMBRE")) {
                model.addRow(new Object[]{
                    contacto.getNombre(),
                    contacto.getTelefono(),
                    contacto.getEmail(),
                    InternationalizationManager.getString("category." + contacto.getCategoria().toLowerCase()),
                    contacto.isFavorito()
                });
            }
        }
    }
    
    private void updateUITexts() {
        setTitle(InternationalizationManager.getString("app.title"));
        
        menuBar.getMenu(0).setText(InternationalizationManager.getString("menu.language"));      
        // Actualizar pestañas
        tabbedPane.setTitleAt(0, InternationalizationManager.getString("menu.contacts"));
        tabbedPane.setTitleAt(1, InternationalizationManager.getString("menu.stats"));
        
        lbl_nombres.setText(InternationalizationManager.getString("label.names"));
        lbl_telefono.setText(InternationalizationManager.getString("label.phone"));
        lbl_email.setText(InternationalizationManager.getString("label.email"));
        lbl_buscar.setText(InternationalizationManager.getString("label.search"));
        lbl_listaContactos.setText(InternationalizationManager.getString("list.title"));
        
        // Actualizar checkbox
        chb_favorito.setText(InternationalizationManager.getString("checkbox.favorite"));
        
        // Actualizar botones
        btn_add.setText(InternationalizationManager.getString("button.add"));
        btn_modificar.setText(InternationalizationManager.getString("button.modify"));
        btn_eliminar.setText(InternationalizationManager.getString("button.delete"));
        btn_exportar.setText(InternationalizationManager.getString("button.export"));
        
        // Actualizar encabezados de tabla
        modeloTabla.setColumnIdentifiers(new String[] {
            InternationalizationManager.getString("table.header.name"),
            InternationalizationManager.getString("table.header.phone"),
            InternationalizationManager.getString("table.header.email"),
            InternationalizationManager.getString("table.header.category"),
            InternationalizationManager.getString("table.header.favorite")
        });
        
        // Actualizar menú contextual
        menuItemEditar.setText(InternationalizationManager.getString("popup.edit"));
        menuItemEliminar.setText(InternationalizationManager.getString("popup.delete"));
        
        // Actualizar estadísticas
        actualizarTextosEstadisticas();
        
        // Actualizar combo box de categorías
        actualizarComboCategorias();
      
        // Forzar repintado
        revalidate();
        repaint();
    }
    
    private void actualizarTextosEstadisticas() {
        // Obtener los valores actuales antes de actualizar los textos
        String totalText = lblTotalContactos.getText().replaceAll("[^0-9]", "");
        String favText = lblFavoritos.getText().replaceAll("[^0-9]", "");
        String famText = lblFamilia.getText().replaceAll("[^0-9]", "");
        String amigText = lblAmigos.getText().replaceAll("[^0-9]", "");
        String trabText = lblTrabajo.getText().replaceAll("[^0-9]", "");
        
        lblTotalContactos.setText(InternationalizationManager.getString("stats.total") + " " + (totalText.isEmpty() ? "0" : totalText));
        lblFavoritos.setText(InternationalizationManager.getString("stats.favorites") + " " + (favText.isEmpty() ? "0" : favText));
        lblFamilia.setText(InternationalizationManager.getString("stats.family") + " " + (famText.isEmpty() ? "0" : famText));
        lblAmigos.setText(InternationalizationManager.getString("stats.friends") + " " + (amigText.isEmpty() ? "0" : amigText));
        lblTrabajo.setText(InternationalizationManager.getString("stats.work") + " " + (trabText.isEmpty() ? "0" : trabText));
    }
    
    private void actualizarComboCategorias() {
        DefaultComboBoxModel<IconComboItem> model = new DefaultComboBoxModel<>();
        ImageIcon defaultIcon = loadIcon("/icons/default.png", 16, 16);
        ImageIcon familiaIcon = loadIcon("/icons/familia.png", 26, 26);
        ImageIcon amigosIcon = loadIcon("/icons/amigos.png", 26, 26);
        ImageIcon trabajoIcon = loadIcon("/icons/trabajo.png", 26, 26);
        
        model.addElement(new IconComboItem(InternationalizationManager.getString("category.choose"), defaultIcon));
        model.addElement(new IconComboItem(InternationalizationManager.getString("category.family"), familiaIcon));
        model.addElement(new IconComboItem(InternationalizationManager.getString("category.friends"), amigosIcon));
        model.addElement(new IconComboItem(InternationalizationManager.getString("category.work"), trabajoIcon));
        
        cmb_categoria.setModel(model);
        cmb_categoria.setRenderer(new IconListRenderer());
    }

    private boolean isSelectedIndex(int index) {
        return tabbedPane.getSelectedIndex() == index;
    }
    
    private String getLocalizedString(String key, String defaultValue) {
        try {
            return InternationalizationManager.getString(key);
        } catch (java.util.MissingResourceException e) {
            return defaultValue;
        }
    }
    
    private void configurarAtajos() {
        KeyStroke keyStrokeAdd = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke keyStrokeDelete = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke keyStrokeModify = KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke keyStrokeExport = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK);
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeAdd, "agregar");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeDelete, "eliminar");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeModify, "modificar");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeExport, "exportar");
        
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
    
    private ImageIcon loadIcon(String path, int width, int height) {
        URL url = getClass().getResource(path);
        if(url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("Imagen no encontrada: " + path);
            return null;
        }
    }
}

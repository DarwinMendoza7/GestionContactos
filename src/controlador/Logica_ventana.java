package controlador;

import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import vista.Ventana;
import vista.GraficoBarras;
import modelo.*;

//Definición de la clase Logica_ventana que implementa interfaces para manejar eventos.
public class Logica_ventana implements ActionListener, ListSelectionListener, ItemListener, KeyListener, MouseListener {
    private Ventana delegado;
    private String nombres, email, telefono, categoria = "";
    private Persona persona;
    private List<Persona> contactos = new ArrayList<>();
    private boolean favorito = false;
    private int selectedIndex = -1;

 // Constructor que inicializa la clase y configura los escuchadores de eventos para los componentes de la GUI.
    public Logica_ventana(Ventana delegado) {
        this.delegado = delegado;
        
        // Cargar contactos al iniciar
        cargarContactosRegistrados();
        
        // Agregar listeners
        this.delegado.btn_add.addActionListener(this);
        this.delegado.btn_eliminar.addActionListener(this);
        this.delegado.btn_modificar.addActionListener(this);
        this.delegado.btn_exportar.addActionListener(this);
        this.delegado.lst_contactos.addListSelectionListener(this);
        this.delegado.cmb_categoria.addItemListener(this);
        this.delegado.chb_favorito.addItemListener(this);
        this.delegado.txt_buscar.addKeyListener(this);
        this.delegado.tablaContactos.addMouseListener(this);
        
        // Configurar validación en tiempo real
        configurarValidacionEnTiempoReal();
        
        // Configurar menú contextual
        this.delegado.menuItemEditar.addActionListener(e -> {
            int row = delegado.tablaContactos.getSelectedRow();
            if (row >= 0) {
                row = delegado.tablaContactos.convertRowIndexToModel(row);
                
                String nombreSeleccionado = (String) delegado.tablaContactos.getModel().getValueAt(row, 0);
                for(int i = 0; i < contactos.size();i++) {
                	if(contactos.get(i).getNombre().equals(nombreSeleccionado)) {
                		 cargarContacto(i);
                		 break;
                	}
                }
            }
        });
        
        this.delegado.menuItemEliminar.addActionListener(e -> {
            int row = delegado.tablaContactos.getSelectedRow();
            if (row >= 0) {
                // Convertir índice de vista a modelo
                int modelRow = delegado.tablaContactos.convertRowIndexToModel(row);
                String nombreSeleccionado = (String) delegado.tablaContactos.getModel().getValueAt(modelRow, 0);
                
                // Buscar la persona por nombre en la lista de contactos
                Persona personaAEliminar = null;
                int indexAEliminar = -1;
                
                for (int i = 0; i < contactos.size(); i++) {
                    if (contactos.get(i).getNombre().equals(nombreSeleccionado)) {
                        personaAEliminar = contactos.get(i);
                        indexAEliminar = i;
                        break;
                    }
                }
                
                if (personaAEliminar != null) {
                    // Usar el mismo proceso de confirmación
                    int confirmacion = JOptionPane.showConfirmDialog(delegado, 
                        "¿Está seguro de eliminar el contacto " + personaAEliminar.getNombre() + "?",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                        
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        try {
                            // Eliminar directamente
                            contactos.remove(indexAEliminar);
                            new PersonaDAO(new Persona()).actualizarContactos(contactos);
                            
                            // Recargar toda la interfaz de manera completa
                            selectedIndex = -1;
                            limpiarCampos();
                            cargarContactosRegistrados();
                            
                            JOptionPane.showMessageDialog(delegado, "Contacto eliminado correctamente");
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(delegado, "Error al eliminar el contacto: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, "No se pudo encontrar el contacto a eliminar");
                }
            }
        });
        
        // Actualizar estadísticas
        actualizarEstadisticas();
        
        // Agregar listener para cambio de pestañas
        this.delegado.tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (delegado.tabbedPane.getSelectedIndex() == 1) {
                    actualizarEstadisticas();
                }
            }
        });
    }

    //Configura la validación en tiempo real para los campos de texto: nombres, teléfono y email.
    //Utiliza DocumentListener para detectar cambios en los campos y aplicar validaciones específicas
    private void configurarValidacionEnTiempoReal() {
        // Validación para el campo nombres
        delegado.txt_nombres.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validarCampoNombre();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarCampoNombre();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarCampoNombre();
            }
            
            private void validarCampoNombre() {
                String texto = delegado.txt_nombres.getText();
                if (!texto.isEmpty() && !validarNombre(texto)) {
                    delegado.txt_nombres.setBackground(new Color(255, 200, 200));
                    delegado.txt_nombres.setToolTipText("Solo se permiten letras y espacios");
                } else {
                    delegado.txt_nombres.setBackground(Color.WHITE);
                    delegado.txt_nombres.setToolTipText(null);
                }
            }
        });
        
        // Validación para el campo teléfono
        delegado.txt_telefono.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validarCampoTelefono();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarCampoTelefono();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarCampoTelefono();
            }
            
            private void validarCampoTelefono() {
                String texto = delegado.txt_telefono.getText();
                if (!texto.isEmpty() && !validarTelefono(texto)) {
                    delegado.txt_telefono.setBackground(new Color(255, 200, 200));
                    delegado.txt_telefono.setToolTipText("Solo se permiten números");
                } else {
                    delegado.txt_telefono.setBackground(Color.WHITE);
                    delegado.txt_telefono.setToolTipText(null);
                }
            }
        });
        
        // Validación para el campo email
        delegado.txt_email.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validarCampoEmail();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarCampoEmail();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarCampoEmail();
            }
            
            private void validarCampoEmail() {
                String texto = delegado.txt_email.getText();
                if (!texto.isEmpty() && !validarEmail(texto)) {
                    delegado.txt_email.setBackground(new Color(255, 200, 200));
                    delegado.txt_email.setToolTipText("Formato inválido de email");
                } else {
                    delegado.txt_email.setBackground(Color.WHITE);
                    delegado.txt_email.setToolTipText(null);
                }
            }
        });
    }

    //Valida los campos de entrada del formulario
    private boolean validarCampos() {
        boolean camposValidos = true;
        
        // Validar nombres (solo letras y espacios)
        if (!validarNombre(delegado.txt_nombres.getText())) {
            JOptionPane.showMessageDialog(delegado, 
                "El campo NOMBRES solo debe contener letras y espacios.", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            delegado.txt_nombres.requestFocus();
            return false;
        }
        
        // Validar teléfono (solo números)
        if (!validarTelefono(delegado.txt_telefono.getText())) {
            JOptionPane.showMessageDialog(delegado, 
                "El campo TELÉFONO solo debe contener números.", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            delegado.txt_telefono.requestFocus();
            return false;
        }
        
        // Validar email (formato correcto)
        if (!validarEmail(delegado.txt_email.getText())) {
            JOptionPane.showMessageDialog(delegado, 
                "El campo EMAIL debe tener un formato válido (ejemplo@dominio.com).", 
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            delegado.txt_email.requestFocus();
            return false;
        }
        
        return camposValidos;
    }

    private boolean validarNombre(String nombre) {
        // Expresión regular para validar que solo contenga letras y espacios
        // También permite caracteres acentuados comunes en español y otros idiomas
        String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$";
        return nombre.trim().matches(regex);
    }

    private boolean validarTelefono(String telefono) {
        // Expresión regular para validar que solo contenga números
        String regex = "^[0-9]+$";
        return telefono.trim().matches(regex);
    }

    private boolean validarEmail(String email) {
        // Expresión regular para validar formato de email básico
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.trim().matches(regex);
    }

    private void incializacionCampos() {
        nombres = delegado.txt_nombres.getText();
        email = delegado.txt_email.getText();
        telefono = delegado.txt_telefono.getText();
    }
    

	// Método privado para cargar los contactos almacenados 
    private void cargarContactosRegistrados() {
        try {
            delegado.progressBar.setValue(0);
            delegado.progressBar.setString("Cargando contactos...");
            
            SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    publish(20);
                    contactos = new PersonaDAO(new Persona()).leerArchivo();
                    publish(60);
                    // Agregar pausa simulada para mostrar progreso
                    Thread.sleep(300);
                    publish(90);
                    return null;
                }
                
                @Override
                protected void process(List<Integer> chunks) {
                    for (int progress : chunks) {
                        delegado.progressBar.setValue(progress);
                    }
                }
                
                @Override
                protected void done() {
                    // Actualizar tabla
                    DefaultTableModel model = (DefaultTableModel) delegado.tablaContactos.getModel();
                    model.setRowCount(0);
                    
                    // Actualizar lista antigua
                    DefaultListModel<String> listaModelo = new DefaultListModel<>();
                    
                    // Filtrar encabezado y agregar datos
                    for (Persona contacto : contactos) {
                        if (!contacto.getNombre().equals("NOMBRE")) {
                            model.addRow(contacto.toTableRow());
                            listaModelo.addElement(contacto.formatoLista());
                        }
                    }
                    
                    delegado.lst_contactos.setModel(listaModelo);
                    delegado.progressBar.setValue(100);
                    delegado.progressBar.setString("Contactos cargados correctamente");
                    
                    // Actualizar estadísticas
                    actualizarEstadisticas();
                }
            };
            
            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(delegado, "Existen problemas al cargar los contactos: " + e.getMessage());
            delegado.progressBar.setValue(0);
            delegado.progressBar.setString("Error al cargar contactos");
        }
    }

    // Método privado para limpiar los campos de entrada en la GUI y reiniciar variables.
    private void limpiarCampos() {
        delegado.txt_nombres.setText("");
        delegado.txt_telefono.setText("");
        delegado.txt_email.setText("");
        categoria = "";
        favorito = false;
        delegado.chb_favorito.setSelected(favorito);
        delegado.cmb_categoria.setSelectedIndex(0);
        incializacionCampos();
        selectedIndex = -1;
        
        // Restaurar colores de fondo
        delegado.txt_nombres.setBackground(Color.WHITE);
        delegado.txt_telefono.setBackground(Color.WHITE);
        delegado.txt_email.setBackground(Color.WHITE);
        
        // Eliminar tooltips
        delegado.txt_nombres.setToolTipText(null);
        delegado.txt_telefono.setToolTipText(null);
        delegado.txt_email.setToolTipText(null);
        
        cargarContactosRegistrados();
    }
 // Método privado para cargar los datos del contacto seleccionado en los campos de la GUI.
    private void cargarContacto(int index) {
        if (index >= 0 && index < contactos.size()) {
            selectedIndex = index;
            delegado.txt_nombres.setText(contactos.get(index).getNombre());
            delegado.txt_telefono.setText(contactos.get(index).getTelefono());
            delegado.txt_email.setText(contactos.get(index).getEmail());
            delegado.chb_favorito.setSelected(contactos.get(index).isFavorito());
            delegado.cmb_categoria.setSelectedItem(contactos.get(index).getCategoria());
        }
    }
    
  //Elimina un contacto de la lista
    private void eliminarContacto(int index) {
        if (index >= 0 && index < contactos.size()) {
            int confirmacion = JOptionPane.showConfirmDialog(delegado, 
                "¿Está seguro de eliminar el contacto " + contactos.get(index).getNombre() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    contactos.remove(index);
                    new PersonaDAO(new Persona()).actualizarContactos(contactos);
                    
                    // Recargar toda la interfaz
                    selectedIndex = -1;
                    cargarContactosRegistrados();
                    
                    JOptionPane.showMessageDialog(delegado, "Contacto eliminado correctamente");
                    
                    // Limpiar campos
                    delegado.txt_nombres.setText("");
                    delegado.txt_telefono.setText("");
                    delegado.txt_email.setText("");
                    delegado.chb_favorito.setSelected(false);
                    delegado.cmb_categoria.setSelectedIndex(0);
                    
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(delegado, "Error al eliminar el contacto: " + e.getMessage());
                }
            }
        }
    }
    
    //Actualiza las estadisticas mostradas en el panel de estadísticas, inluyendo el total de contactos, contactos favoritos y
    //contactos por categoría. También genera y muestra un gráfico de barras con estos datos
    private void actualizarEstadisticas() {
        try {
            PersonaDAO dao = new PersonaDAO(new Persona());
            List<Persona> todosContactos = dao.leerArchivo();
            int total = 0;
            
            // Filtrar encabezado correctamente
            for (Persona p : todosContactos) {
                if (!p.getNombre().equals("NOMBRE")) {
                    total++;
                }
            }
            
            int favoritos = dao.getCantidadFavoritos();
            int familia = dao.getCantidadPorCategoria("Familia");
            int amigos = dao.getCantidadPorCategoria("Amigos");
            int trabajo = dao.getCantidadPorCategoria("Trabajo");
            
            delegado.lblTotalContactos.setText("Total de contactos: " + total);
            delegado.lblFavoritos.setText("Contactos favoritos: " + favoritos);
            delegado.lblFamilia.setText("Contactos de familia: " + familia);
            delegado.lblAmigos.setText("Contactos de amigos: " + amigos);
            delegado.lblTrabajo.setText("Contactos de trabajo: " + trabajo);
            
            // Generar y mostrar gráfico de barras
            Map<String, Integer> datosGrafico = new HashMap<>();
            datosGrafico.put("Familia", familia);
            datosGrafico.put("Amigos", amigos);
            datosGrafico.put("Trabajo", trabajo);
            datosGrafico.put("Favoritos", favoritos);
            
            GraficoBarras grafico = new GraficoBarras(datosGrafico, "Estadísticas de Contactos");
            delegado.panelGraficas.removeAll();
            delegado.panelGraficas.add(grafico);
            delegado.panelGraficas.revalidate();
            delegado.panelGraficas.repaint();
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(delegado, "Error al actualizar estadísticas: " + e.getMessage());
        }
    }
    
    //Filtra los contactos mostrados en la tabla en base al texto ingresadoo en el campo de busqueda
    private void filtrarContactos() {
        String textoBusqueda = delegado.txt_buscar.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) delegado.tablaContactos.getModel());
        delegado.tablaContactos.setRowSorter(sorter);
        
        if (textoBusqueda.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusqueda));
        }
    }
    
 // Método que maneja los eventos de acción (clic) en los botones.
    @Override
    public void actionPerformed(ActionEvent e) {
        incializacionCampos();
        
        if (e.getSource() == delegado.btn_add) {
            if ((!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
                if ((!categoria.equals("Elija una Categoría")) && (!categoria.equals(""))) {
                    // Validar los campos antes de agregar
                    if (validarCampos()) {
                        persona = new Persona(nombres, telefono, email, categoria, favorito);
                        boolean exito = new PersonaDAO(persona).escribirArchivo();
                        if (exito) {
                            limpiarCampos();
                            JOptionPane.showMessageDialog(delegado, "Contacto Registrado!!!");
                        } else {
                            JOptionPane.showMessageDialog(delegado, "Error al registrar el contacto");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, "Elija una Categoría!!!");
                }
            } else {
                JOptionPane.showMessageDialog(delegado, "Todos los campos deben ser llenados!!!");
            }
        } else if (e.getSource() == delegado.btn_eliminar) {
            if (selectedIndex != -1) {
                eliminarContacto(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(delegado, "Seleccione un contacto para eliminar");
            }
        } else if (e.getSource() == delegado.btn_modificar) {
            if (selectedIndex != -1 && (!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
                if ((!categoria.equals("Elija una Categoría")) && (!categoria.equals(""))) {
                    // Validar los campos antes de modificar
                    if (validarCampos()) {
                        try {
                            // Actualizar el contacto en la lista
                            contactos.get(selectedIndex).setNombre(nombres);
                            contactos.get(selectedIndex).setTelefono(telefono);
                            contactos.get(selectedIndex).setEmail(email);
                            contactos.get(selectedIndex).setCategoria(categoria);
                            contactos.get(selectedIndex).setFavorito(favorito);
                            
                            // Actualizar el archivo
                            new PersonaDAO(new Persona()).actualizarContactos(contactos);
                            
                            limpiarCampos();
                            JOptionPane.showMessageDialog(delegado, "Contacto modificado correctamente");
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(delegado, "Error al modificar el contacto: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, "Elija una Categoría!!!");
                }
            } else {
                JOptionPane.showMessageDialog(delegado, "Seleccione un contacto y complete todos los campos");
            }
        } else if (e.getSource() == delegado.btn_exportar) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar contactos");
            fileChooser.setSelectedFile(new File("contactos_exportados.csv"));
            
            int seleccion = fileChooser.showSaveDialog(delegado);
            
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();
                
                // Asegurar que tenga extensión .csv
                if (!ruta.toLowerCase().endsWith(".csv")) {
                    ruta += ".csv";
                }
                
                final String rutaFinal = ruta;
                // Mostrar progreso
                delegado.progressBar.setValue(0);
                delegado.progressBar.setString("Exportando contactos...");
                
                
                SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        publish(25);
                        Thread.sleep(500); // Simular proceso
                        publish(50);
                        // Usa la ruta seleccionada por el usuario
                        boolean resultado = new PersonaDAO(new Persona()).exportarCSV(rutaFinal);
                        publish(75);
                        Thread.sleep(500); // Simular proceso
                        publish(100);
                        return resultado;
                    }
                    
                    @Override
                    protected void process(List<Integer> chunks) {
                        for (int progress : chunks) {
                            delegado.progressBar.setValue(progress);
                        }
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean exito = get();
                            if (exito) {
                                delegado.progressBar.setString("Contactos exportados correctamente");
                                JOptionPane.showMessageDialog(delegado, "Contactos exportados correctamente a: " + rutaFinal);
                            } else {
                                delegado.progressBar.setString("Error al exportar contactos");
                                JOptionPane.showMessageDialog(delegado, "Error al exportar contactos");
                            }
                        } catch (Exception e) {
                            delegado.progressBar.setString("Error al exportar contactos");
                            JOptionPane.showMessageDialog(delegado, "Error al exportar contactos: " + e.getMessage());
                        }
                    }
                };
                
                worker.execute();
            }
        }
    }

    // Método que maneja los eventos de selección en la lista de contactos.
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int index = delegado.lst_contactos.getSelectedIndex();
            if (index != -1) {
                // Buscar el índice real en la lista de contactos
                int realIndex = -1;
                int contador = 0;
                
                for (int i = 0; i < contactos.size(); i++) {
                    if (!contactos.get(i).getNombre().equals("NOMBRE")) {
                        if (contador == index) {
                            realIndex = i;
                            break;
                        }
                        contador++;
                    }
                }
                
                if (realIndex != -1) {
                    cargarContacto(realIndex);
                    
                    // Seleccionar la fila en la tabla
                    for (int i = 0; i < delegado.tablaContactos.getRowCount(); i++) {
                        if (delegado.tablaContactos.getValueAt(i, 0).equals(contactos.get(realIndex).getNombre())) {
                            delegado.tablaContactos.setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                }
            }
        }
    }

    // Método que maneja los eventos de cambio de estado en los componentes cmb_categoria y chb_favorito.
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == delegado.cmb_categoria) {
            categoria = delegado.cmb_categoria.getSelectedItem().toString();
        } else if (e.getSource() == delegado.chb_favorito) {
            favorito = delegado.chb_favorito.isSelected();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == delegado.txt_buscar) {
            filtrarContactos();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == delegado.tablaContactos) {
            int row = delegado.tablaContactos.rowAtPoint(e.getPoint());
            
            if (row >= 0) {
                // Para selección con clic izquierdo
                if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                    int modelRow = delegado.tablaContactos.convertRowIndexToModel(row);
                    for (int i = 0; i < contactos.size(); i++) {
                        if (contactos.get(i).getNombre().equals(delegado.tablaContactos.getModel().getValueAt(modelRow, 0))) {
                            cargarContacto(i);
                            break;
                        }
                    }
                }
                
                // Para menú contextual con clic derecho
                if (e.getButton() == MouseEvent.BUTTON3) {
                    delegado.tablaContactos.setRowSelectionInterval(row, row);
                    delegado.popupMenu.show(delegado.tablaContactos, e.getX(), e.getY());
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
        
}

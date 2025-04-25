package controlador;

import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import vista.Ventana;
import vista.GraficoBarras;
import vista.IconComboItem;
import modelo.*;
import util.InternationalizationManager;

public class Logica_ventana implements ActionListener, ListSelectionListener, ItemListener, KeyListener, MouseListener {
    private Ventana delegado;
    private String nombres, email, telefono, categoria = "";
    private Persona persona;
    private List<Persona> contactos = new ArrayList<>();
    private boolean favorito = false;
    private int selectedIndex = -1;

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
                int modelRow = delegado.tablaContactos.convertRowIndexToModel(row);
                String nombreSeleccionado = (String) delegado.tablaContactos.getModel().getValueAt(modelRow, 0);
                
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
                    int confirmacion = JOptionPane.showConfirmDialog(delegado, 
                        InternationalizationManager.getString("message.confirm.delete") + " " + personaAEliminar.getNombre() + "?",
                        InternationalizationManager.getString("title.confirm.delete"), 
                        JOptionPane.YES_NO_OPTION);
                        
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        try {
                            contactos.remove(indexAEliminar);
                            new PersonaDAO(new Persona()).actualizarContactos(contactos);
                            
                            selectedIndex = -1;
                            limpiarCampos();
                            cargarContactosRegistrados();
                            
                            JOptionPane.showMessageDialog(delegado, 
                                InternationalizationManager.getString("message.contact.deleted"));
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(delegado, 
                                InternationalizationManager.getString("message.error.delete") + ": " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, 
                        InternationalizationManager.getString("message.error.delete"));
                }
            }
        });
        
        // Actualizar estadísticas
        actualizarEstadisticas();
        
        // Agregar listener para cambio de pestañas
        this.delegado.tabbedPane.addChangeListener(e -> {
            if (delegado.tabbedPane.getSelectedIndex() == 1) {
                actualizarEstadisticas();
            }
        });
    }

    public void actualizarDatosIdioma() {
        cargarContactosRegistrados();
        actualizarEstadisticas();
    }
    
    public List<Persona> getContactos() {
        return contactos;
    }

    private void configurarValidacionEnTiempoReal() {
        delegado.txt_nombres.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarCampoNombre(); }
            @Override public void removeUpdate(DocumentEvent e) { validarCampoNombre(); }
            @Override public void changedUpdate(DocumentEvent e) { validarCampoNombre(); }
            
            private void validarCampoNombre() {
                String texto = delegado.txt_nombres.getText();
                if (!texto.isEmpty() && !validarNombre(texto)) {
                    delegado.txt_nombres.setBackground(new Color(255, 200, 200));
                    delegado.txt_nombres.setToolTipText(InternationalizationManager.getString("validation.name"));
                } else {
                    delegado.txt_nombres.setBackground(Color.WHITE);
                    delegado.txt_nombres.setToolTipText(null);
                }
            }
        });
        
        delegado.txt_telefono.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarCampoTelefono(); }
            @Override public void removeUpdate(DocumentEvent e) { validarCampoTelefono(); }
            @Override public void changedUpdate(DocumentEvent e) { validarCampoTelefono(); }
            
            private void validarCampoTelefono() {
                String texto = delegado.txt_telefono.getText();
                if (!texto.isEmpty() && !validarTelefono(texto)) {
                    delegado.txt_telefono.setBackground(new Color(255, 200, 200));
                    delegado.txt_telefono.setToolTipText(InternationalizationManager.getString("validation.phone"));
                } else {
                    delegado.txt_telefono.setBackground(Color.WHITE);
                    delegado.txt_telefono.setToolTipText(null);
                }
            }
        });
        
        delegado.txt_email.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarCampoEmail(); }
            @Override public void removeUpdate(DocumentEvent e) { validarCampoEmail(); }
            @Override public void changedUpdate(DocumentEvent e) { validarCampoEmail(); }
            
            private void validarCampoEmail() {
                String texto = delegado.txt_email.getText();
                if (!texto.isEmpty() && !validarEmail(texto)) {
                    delegado.txt_email.setBackground(new Color(255, 200, 200));
                    delegado.txt_email.setToolTipText(InternationalizationManager.getString("validation.email"));
                } else {
                    delegado.txt_email.setBackground(Color.WHITE);
                    delegado.txt_email.setToolTipText(null);
                }
            }
        });
    }

    private boolean validarCampos() {
        // Validar nombres (solo letras y espacios)
        if (!validarNombre(delegado.txt_nombres.getText())) {
            JOptionPane.showMessageDialog(delegado, 
                InternationalizationManager.getString("validation.name.message"), 
                InternationalizationManager.getString("validation.title"), 
                JOptionPane.ERROR_MESSAGE);
            delegado.txt_nombres.requestFocus();
            return false;
        }
        
        // Validar teléfono (solo números)
        if (!validarTelefono(delegado.txt_telefono.getText())) {
            JOptionPane.showMessageDialog(delegado, 
                InternationalizationManager.getString("validation.phone.message"), 
                InternationalizationManager.getString("validation.title"), 
                JOptionPane.ERROR_MESSAGE);
            delegado.txt_telefono.requestFocus();
            return false;
        }
        
        // Validar email (formato correcto)
        if (!validarEmail(delegado.txt_email.getText())) {
            JOptionPane.showMessageDialog(delegado, 
                InternationalizationManager.getString("validation.email.message"), 
                InternationalizationManager.getString("validation.title"), 
                JOptionPane.ERROR_MESSAGE);
            delegado.txt_email.requestFocus();
            return false;
        }
        
        return true;
    }

    private boolean validarNombre(String nombre) {
        String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$";
        return nombre.trim().matches(regex);
    }

    private boolean validarTelefono(String telefono) {
        String regex = "^[0-9]+$";
        return telefono.trim().matches(regex);
    }

    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.trim().matches(regex);
    }

    private void incializacionCampos() {
        nombres = delegado.txt_nombres.getText();
        email = delegado.txt_email.getText();
        telefono = delegado.txt_telefono.getText();
    }
    
    private void cargarContactosRegistrados() {
    	 try {
    	        delegado.progressBar.setValue(0);
    	        delegado.progressBar.setString(InternationalizationManager.getString("progress.loading"));
    	        
    	        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
    	            @Override
    	            protected Void doInBackground() throws Exception {
    	                publish(20);
    	                contactos = new PersonaDAO(new Persona()).leerArchivo();
    	                publish(60);
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
    	                try {
    	                    // Actualizar tabla
    	                    DefaultTableModel model = (DefaultTableModel) delegado.tablaContactos.getModel();
    	                    model.setRowCount(0);
    	                    
    	                    DefaultListModel<String> listaModelo = new DefaultListModel<>();
    	                    
    	                    for (Persona contacto : contactos) {
    	                        if (!contacto.getNombre().equals("NOMBRE")) {
    	                            model.addRow(new Object[]{
    	                                contacto.getNombre(),
    	                                contacto.getTelefono(),
    	                                contacto.getEmail(),
    	                                InternationalizationManager.getString("category." + contacto.getCategoria().toLowerCase()),
    	                                contacto.isFavorito()
    	                            });
    	                            listaModelo.addElement(contacto.formatoLista());
    	                        }
    	                    }
    	                    
    	                    delegado.lst_contactos.setModel(listaModelo);
    	                    delegado.progressBar.setValue(100);
    	                    delegado.progressBar.setString(InternationalizationManager.getString("progress.loaded"));
    	                    
    	                    // Actualizar estadísticas después de cargar contactos
    	                    actualizarEstadisticas();
    	                    
    	                } catch (Exception e) {
    	                    e.printStackTrace();
    	                }
    	            }
    	        };
    	        
    	        worker.execute();
    	    } catch (Exception e) {
    	        JOptionPane.showMessageDialog(delegado, 
    	            InternationalizationManager.getString("error.load.contacts") + ": " + e.getMessage());
    	        delegado.progressBar.setValue(0);
    	        delegado.progressBar.setString(InternationalizationManager.getString("error.loading"));
    	    }
    }

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
        
        delegado.txt_nombres.setBackground(Color.WHITE);
        delegado.txt_telefono.setBackground(Color.WHITE);
        delegado.txt_email.setBackground(Color.WHITE);
        
        delegado.txt_nombres.setToolTipText(null);
        delegado.txt_telefono.setToolTipText(null);
        delegado.txt_email.setToolTipText(null);
        
        cargarContactosRegistrados();
    }

    private void cargarContacto(int index) {
        if (index >= 0 && index < contactos.size()) {
            selectedIndex = index;
            delegado.txt_nombres.setText(contactos.get(index).getNombre());
            delegado.txt_telefono.setText(contactos.get(index).getTelefono());
            delegado.txt_email.setText(contactos.get(index).getEmail());
            delegado.chb_favorito.setSelected(contactos.get(index).isFavorito());
            
            // Actualizar combo box según la categoría almacenada
            String categoriaAlmacenada = contactos.get(index).getCategoria();
            for (int i = 0; i < delegado.cmb_categoria.getItemCount(); i++) {
                IconComboItem item = delegado.cmb_categoria.getItemAt(i);
                if (item.getText().equals(InternationalizationManager.getString("category." + categoriaAlmacenada.toLowerCase()))) {
                    delegado.cmb_categoria.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void eliminarContacto(int index) {
        if (index >= 0 && index < contactos.size()) {
            int confirmacion = JOptionPane.showConfirmDialog(delegado, 
                InternationalizationManager.getString("message.confirm.delete") + " " + contactos.get(index).getNombre() + "?",
                InternationalizationManager.getString("title.confirm.delete"), 
                JOptionPane.YES_NO_OPTION);
                
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    contactos.remove(index);
                    new PersonaDAO(new Persona()).actualizarContactos(contactos);
                    
                    selectedIndex = -1;
                    cargarContactosRegistrados();
                    
                    JOptionPane.showMessageDialog(delegado, 
                        InternationalizationManager.getString("message.contact.deleted"));
                    
                    delegado.txt_nombres.setText("");
                    delegado.txt_telefono.setText("");
                    delegado.txt_email.setText("");
                    delegado.chb_favorito.setSelected(false);
                    delegado.cmb_categoria.setSelectedIndex(0);
                    
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(delegado, 
                        InternationalizationManager.getString("message.error.delete") + ": " + e.getMessage());
                }
            }
        }
    }
    
    private void actualizarEstadisticas() {
    	 try {
    	        // Reiniciamos contadores
    	        int total = 0;
    	        int favoritos = 0;
    	        int familia = 0;
    	        int amigos = 0;
    	        int trabajo = 0;

    	        // Usamos directamente la lista de contactos en memoria
    	        for (Persona p : contactos) {
    	            if (!p.getNombre().equals("NOMBRE")) {
    	                total++;
    	                if (p.isFavorito()) {
    	                    favoritos++;
    	                }
    	                
    	                // Convertimos la categoría a minúsculas para comparación
    	                String categoria = p.getCategoria().toLowerCase();
    	                
    	                if (categoria.contains("family") || categoria.contains("familia")) {
    	                    familia++;
    	                } else if (categoria.contains("friends") || categoria.contains("amigos")) {
    	                    amigos++;
    	                } else if (categoria.contains("work") || categoria.contains("trabajo")) {
    	                    trabajo++;
    	                }
    	            }
    	        }

    	        // Actualizamos las etiquetas
    	        delegado.lblTotalContactos.setText(InternationalizationManager.getString("stats.total") + ": " + total);
    	        delegado.lblFavoritos.setText(InternationalizationManager.getString("stats.favorites") + ": " + favoritos);
    	        delegado.lblFamilia.setText(InternationalizationManager.getString("stats.family") + ": " + familia);
    	        delegado.lblAmigos.setText(InternationalizationManager.getString("stats.friends") + ": " + amigos);
    	        delegado.lblTrabajo.setText(InternationalizationManager.getString("stats.work") + ": " + trabajo);

    	        // Preparamos datos para el gráfico
    	        Map<String, Integer> datosGrafico = new LinkedHashMap<>();
    	        datosGrafico.put(InternationalizationManager.getString("category.family"), familia);
    	        datosGrafico.put(InternationalizationManager.getString("category.friends"), amigos);
    	        datosGrafico.put(InternationalizationManager.getString("category.work"), trabajo);
    	        datosGrafico.put(InternationalizationManager.getString("stats.favorites"), favoritos);

    	        // Actualizamos el gráfico
    	        actualizarGrafico(datosGrafico);

    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        JOptionPane.showMessageDialog(delegado, 
    	            InternationalizationManager.getString("error.stats") + ": " + e.getMessage());
    	    }
    }
    

	private void actualizarGrafico(Map<String, Integer> datos) {
	    GraficoBarras grafico = new GraficoBarras(datos, 
	        InternationalizationManager.getString("stats.title"));
	    delegado.panelGraficas.removeAll();
	    delegado.panelGraficas.add(grafico);
	    delegado.panelGraficas.revalidate();
	    delegado.panelGraficas.repaint();
	}
    
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
    
    @Override
    public void actionPerformed(ActionEvent e) {
        incializacionCampos();
        
        if (e.getSource() == delegado.btn_add) {
            if ((!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
                if ((!categoria.equals(InternationalizationManager.getString("category.choose"))) && (!categoria.equals(""))) {
                    if (validarCampos()) {
                        // Guardar la categoría en inglés para consistencia interna
                        String categoriaInterna = "";
                        if (categoria.equals(InternationalizationManager.getString("category.family"))) {
                            categoriaInterna = "family";
                        } else if (categoria.equals(InternationalizationManager.getString("category.friends"))) {
                            categoriaInterna = "friends";
                        } else if (categoria.equals(InternationalizationManager.getString("category.work"))) {
                            categoriaInterna = "work";
                        }
                        
                        persona = new Persona(nombres, telefono, email, categoriaInterna, favorito);
                        boolean exito = new PersonaDAO(persona).escribirArchivo();
                        if (exito) {
                            limpiarCampos();
                            JOptionPane.showMessageDialog(delegado, 
                                InternationalizationManager.getString("message.contact.added"));
                        } else {
                            JOptionPane.showMessageDialog(delegado, 
                                InternationalizationManager.getString("message.error.add"));
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, 
                        InternationalizationManager.getString("message.select.category"));
                }
            } else {
                JOptionPane.showMessageDialog(delegado, 
                    InternationalizationManager.getString("message.fill.fields"));
            }
        } else if (e.getSource() == delegado.btn_eliminar) {
            if (selectedIndex != -1) {
                eliminarContacto(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(delegado, 
                    InternationalizationManager.getString("message.select.contact.delete"));
            }
        } else if (e.getSource() == delegado.btn_modificar) {
            if (selectedIndex != -1 && (!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
                if ((!categoria.equals(InternationalizationManager.getString("category.choose"))) && (!categoria.equals(""))) {
                    if (validarCampos()) {
                        try {
                            // Guardar la categoría en inglés para consistencia interna
                            String categoriaInterna = "";
                            if (categoria.equals(InternationalizationManager.getString("category.family"))) {
                                categoriaInterna = "family";
                            } else if (categoria.equals(InternationalizationManager.getString("category.friends"))) {
                                categoriaInterna = "friends";
                            } else if (categoria.equals(InternationalizationManager.getString("category.work"))) {
                                categoriaInterna = "work";
                            }
                            
                            contactos.get(selectedIndex).setNombre(nombres);
                            contactos.get(selectedIndex).setTelefono(telefono);
                            contactos.get(selectedIndex).setEmail(email);
                            contactos.get(selectedIndex).setCategoria(categoriaInterna);
                            contactos.get(selectedIndex).setFavorito(favorito);
                            
                            new PersonaDAO(new Persona()).actualizarContactos(contactos);
                            
                            limpiarCampos();
                            JOptionPane.showMessageDialog(delegado, 
                                InternationalizationManager.getString("message.contact.modified"));
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(delegado, 
                                InternationalizationManager.getString("message.error.modify") + ": " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, 
                        InternationalizationManager.getString("message.select.category"));
                }
            } else {
                JOptionPane.showMessageDialog(delegado, 
                    InternationalizationManager.getString("message.select.contact.modify"));
            }
        } else if (e.getSource() == delegado.btn_exportar) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(InternationalizationManager.getString("button.export"));
            fileChooser.setSelectedFile(new File("contactos_exportados.csv"));
            
            int seleccion = fileChooser.showSaveDialog(delegado);
            
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();
                
                if (!ruta.toLowerCase().endsWith(".csv")) {
                    ruta += ".csv";
                }
                
                final String rutaFinal = ruta;
                delegado.progressBar.setValue(0);
                delegado.progressBar.setString(InternationalizationManager.getString("progress.exporting"));
                
                SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        publish(25);
                        Thread.sleep(500);
                        publish(50);
                        boolean resultado = new PersonaDAO(new Persona()).exportarCSV(rutaFinal);
                        publish(75);
                        Thread.sleep(500);
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
                                delegado.progressBar.setString(InternationalizationManager.getString("progress.exported"));
                                JOptionPane.showMessageDialog(delegado, 
                                    InternationalizationManager.getString("message.export.success") + " " + rutaFinal);
                            } else {
                                delegado.progressBar.setString(InternationalizationManager.getString("error.exporting"));
                                JOptionPane.showMessageDialog(delegado, 
                                    InternationalizationManager.getString("message.export.error"));
                            }
                        } catch (Exception e) {
                            delegado.progressBar.setString(InternationalizationManager.getString("error.exporting"));
                            JOptionPane.showMessageDialog(delegado, 
                                InternationalizationManager.getString("message.export.error") + ": " + e.getMessage());
                        }
                    }
                };
                
                worker.execute();
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int index = delegado.lst_contactos.getSelectedIndex();
            if (index != -1) {
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

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == delegado.cmb_categoria) {
           IconComboItem selectedItem = (IconComboItem) delegado.cmb_categoria.getSelectedItem();
           categoria = selectedItem.getText(); 
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
                if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                    int modelRow = delegado.tablaContactos.convertRowIndexToModel(row);
                    for (int i = 0; i < contactos.size(); i++) {
                        if (contactos.get(i).getNombre().equals(delegado.tablaContactos.getModel().getValueAt(modelRow, 0))) {
                            cargarContacto(i);
                            break;
                        }
                    }
                }
                
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


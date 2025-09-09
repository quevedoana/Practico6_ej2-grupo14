/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DeTodoSA;

import clases.Producto;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Anitabonita
 */
public class GestionDeProductos extends javax.swing.JInternalFrame {

    private DefaultTableModel tabla;

    /**
     * Creates new form GestionDeProductos
     */
    public GestionDeProductos() {
        initComponents();
        initTabla();
        cargarCombo();
        actualizarTabla();
        limpiarSeleccion();

        comboFiltroCategorias.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filtrarPorCategoria();
            }
        });

        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarProductoDeTabla();
            }
        });
        btnGuardar.setEnabled(false);
    }

    //inicializar tabla
    private void initTabla() {
        tabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla.addColumn("Codigo");
        tabla.addColumn("Descripcion");
        tabla.addColumn("Precio");
        tabla.addColumn("Categoria");
        tabla.addColumn("Stock");

        tablaProductos.setModel(tabla);
    }

    //cargar lo combo box
    public void cargarCombo() {
        // Modelo para el combo de filtro
        DefaultComboBoxModel<String> categoriaFiltro = new DefaultComboBoxModel<>();
        categoriaFiltro.addElement("Seleccionar una categoria");
        if (DeTodo.listaCategoria.isEmpty()) {
            categoriaFiltro.addElement("No hay categorias cargadas");
        } else {
            for (String c : DeTodo.listaCategoria) {
                categoriaFiltro.addElement(c);
            }
        }
        comboFiltroCategorias.setModel(categoriaFiltro);

        // Modelo para el combo de rubro
        DefaultComboBoxModel<String> categoriaRubro = new DefaultComboBoxModel<>();
        categoriaRubro.addElement("Seleccionar una categoria");
        if (DeTodo.listaCategoria.isEmpty()) {
            categoriaRubro.addElement("No hay categorias cargadas");
        } else {
            for (String c : DeTodo.listaCategoria) {
                categoriaRubro.addElement(c);
            }
        }
        comboRubro.setModel(categoriaRubro);
    }

    // seleccionar un producto de la tabla
    private void seleccionarProductoDeTabla() {
        btnBuscar.setEnabled(false);
        btnAgregarNuevo.setEnabled(false);
        btnAlctualizar.setEnabled(false);
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            int codigo = (Integer) tabla.getValueAt(filaSeleccionada, 0);

            for (Producto p : DeTodo.listaProductos) {
                if (p.getCodigo() == codigo) {
                    textCodigo.setText(String.valueOf(p.getCodigo()));
                    textDescripcion.setText(p.getDescripcion());
                    textPrecio.setText(String.valueOf(p.getPrecio()));
                    spinerStock.setValue(p.getStock());
                    comboRubro.setSelectedItem(p.getCategoria());
                    habilitarBotonesEdicion();
                    break;
                }
            }
        }
    }

    //habilitar botones
    private void habilitarBotonesEdicion() {
        btnGuardar.setEnabled(true);
        btnAlctualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    //limpiar campos
    private void limpiarCampos() {
        textCodigo.setText("");
        textDescripcion.setText("");
        textPrecio.setText("");
        spinerStock.setValue(1);
        comboRubro.setSelectedIndex(0);
        tablaProductos.clearSelection();
    }

    //fitro por categoria
    private void filtrarPorCategoria() {
        String categoriaSeleccionada = comboFiltroCategorias.getSelectedItem().toString();

        if (categoriaSeleccionada.equals("Seleccionar una categoria")
                || categoriaSeleccionada.equals("No hay categorias cargadas")) {
            actualizarTabla();
            return;
        }

        tabla.setRowCount(0);
        for (Producto p : DeTodo.listaProductos) {
            if (p.getCategoria().equals(categoriaSeleccionada)) {
                tabla.addRow(new Object[]{
                    p.getCodigo(),
                    p.getDescripcion(),
                    p.getPrecio(),
                    p.getCategoria(),
                    p.getStock()
                });
            }
        }
    }

    //acualizar tabla con todos los productos
    private void actualizarTabla() {
        tabla.setRowCount(0);
        for (Producto p : DeTodo.listaProductos) {
            tabla.addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getCategoria(),
                p.getStock()
            });
        }
    }

    //restaurar botones 
    private void limpiarSeleccion() {
        tablaProductos.clearSelection();
        btnAgregarNuevo.setEnabled(true);
        btnBuscar.setEnabled(true);
        btnGuardar.setEnabled(false);
        btnAlctualizar.setEnabled(true);
        btnEliminar.setEnabled(false);
    }

    private void agregarProducto() {
        try {
            //Controles
            if (textCodigo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el codigo del producto", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (textPrecio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el precio del producto", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (textDescripcion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la descripcion del producto", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double precio = Double.parseDouble(textPrecio.getText());
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio invalido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (comboRubro.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoria valida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int codigo = Integer.parseInt(textCodigo.getText().trim());
            String descripcion = textDescripcion.getText().trim();
            double precio = Double.parseDouble(textPrecio.getText().trim());
            String categoria = comboRubro.getSelectedItem().toString();
            int stock = (Integer) spinerStock.getValue();

            // Verifica si el codigo ya existe
            for (Producto p : DeTodo.listaProductos) {
                if (p.getCodigo() == codigo) {
                    JOptionPane.showMessageDialog(this, "El código " + codigo + " ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Verifica que se seleccion una categoria valida
            if (categoria.equals("Seleccionar una categoria") || categoria.equals("No hay categorias cargadas")) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoria valida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto nuevoProducto = new Producto(codigo, descripcion, precio, stock, categoria);
            DeTodo.listaProductos.add(nuevoProducto);

            JOptionPane.showMessageDialog(this, "Producto agregado exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en el formato de los numeros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int codigo = (Integer) tabla.getValueAt(filaSeleccionada, 0);
        String descripcion = tabla.getValueAt(filaSeleccionada, 1).toString();

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Esta seguro de que desea eliminar el producto:\n"
                + descripcion + " (Codigo: " + codigo + ")?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Buscar y eliminar el producto
            Iterator<Producto> iterator = DeTodo.listaProductos.iterator();
            while (iterator.hasNext()) {
                Producto p = iterator.next();
                if (p.getCodigo() == codigo) {
                    iterator.remove();
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();
            limpiarSeleccion();
        }
    }

    private void guardarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int codigoOriginal = (Integer) tabla.getValueAt(filaSeleccionada, 0);
            int nuevoCodigo = Integer.parseInt(textCodigo.getText().trim());
            String descripcion = textDescripcion.getText().trim();
            double precio = Double.parseDouble(textPrecio.getText().trim());
            String categoria = comboRubro.getSelectedItem().toString();
            int stock = (Integer) spinerStock.getValue();

            // Verifica si el nuevo codigo ya existe (excepto para el producto actual)
            for (Producto p : DeTodo.listaProductos) {
                if (nuevoCodigo != codigoOriginal && p.getCodigo() == codigoOriginal) {
                    JOptionPane.showMessageDialog(this, "El código " + nuevoCodigo + " ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Buscar y actualizar el producto
            for (Producto p : DeTodo.listaProductos) {
                if (p.getCodigo() == codigoOriginal) {
                    p.setCodigo(nuevoCodigo);
                    p.setDescripcion(descripcion);
                    p.setPrecio(precio);
                    p.setCategoria(categoria);
                    p.setStock(stock);
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Producto guardado exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();
            limpiarSeleccion();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error en el formato de los numeros", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarProducto() {
        if (textCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un codigo para buscar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int codigo = Integer.parseInt(textCodigo.getText().trim());
            tabla.setRowCount(0); //Limpiar tabla

            for (Producto p : DeTodo.listaProductos) {
                if (p.getCodigo() == codigo) {
                    tabla.addRow(new Object[]{
                        p.getCodigo(),
                        p.getDescripcion(),
                        p.getPrecio(),
                        p.getCategoria(),
                        p.getStock()
                    });
                    JOptionPane.showMessageDialog(this, "Producto Encontrado: " + p.getCodigo() + "(" + p.getDescripcion() + ")", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

            }

            JOptionPane.showMessageDialog(this, "No se encontro ningun producto con el codigo " + codigo, "Busqueda", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Codigo invalido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tablaProductosPerformed(java.awt.event.ActionEvent evt) {
        seleccionarProductoDeTabla();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        comboRubro = new javax.swing.JComboBox<>();
        spinerStock = new javax.swing.JSpinner();
        textCodigo = new javax.swing.JTextField();
        textDescripcion = new javax.swing.JTextField();
        textPrecio = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        btnBuscar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        comboFiltroCategorias = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        btnAgregarNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnAlctualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setResizable(true);
        setTitle("De Todo S.A.: Productos");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Codigo:");

        jLabel2.setText("Rubro:");

        jLabel3.setText("Stock:");

        jLabel4.setText("Descripcion:");

        jLabel5.setText("Precio:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textPrecio, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textDescripcion, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textCodigo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboRubro, javax.swing.GroupLayout.Alignment.LEADING, 0, 273, Short.MAX_VALUE))
                        .addGap(25, 25, 25))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(spinerStock, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(textCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(textDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(textPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboRubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spinerStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaProductos);

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/lupa.png"))); // NOI18N
        btnBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBuscarMouseClicked(evt);
            }
        });

        btnCerrar.setText("Cerrar");
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCerrarMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Filtrar por Categoria:");

        btnAgregarNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-producto-usado-50.png"))); // NOI18N
        btnAgregarNuevo.setText("Nuevo");
        btnAgregarNuevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAgregarNuevoMouseClicked(evt);
            }
        });

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-caja-de-producto-de-pelo-corto-50.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarMouseClicked(evt);
            }
        });

        btnAlctualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-marca-doble-30.png"))); // NOI18N
        btnAlctualizar.setText("Actualizar");
        btnAlctualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAlctualizarMouseClicked(evt);
            }
        });

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEliminarMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("Gestión de Productos");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(comboFiltroCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(129, 129, 129))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnAgregarNuevo)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnGuardar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnAlctualizar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEliminar))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(jLabel7)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(comboFiltroCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(btnCerrar)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarNuevo)
                    .addComponent(btnGuardar)
                    .addComponent(btnAlctualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        // TODO add your handling code here:
        buscarProducto();
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void btnAlctualizarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAlctualizarMouseClicked
        // TODO add your handling code here:
        actualizarTabla();
    }//GEN-LAST:event_btnAlctualizarMouseClicked

    private void btnAgregarNuevoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarNuevoMouseClicked
        // TODO add your handling code here:
        agregarProducto();
    }//GEN-LAST:event_btnAgregarNuevoMouseClicked

    private void btnEliminarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEliminarMouseClicked
        // TODO add your handling code here:
        eliminarProducto();
    }//GEN-LAST:event_btnEliminarMouseClicked

    private void btnGuardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarMouseClicked
        // TODO add your handling code here:
        guardarProducto();
    }//GEN-LAST:event_btnGuardarMouseClicked

    private void btnCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarMouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnCerrarMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarNuevo;
    private javax.swing.JButton btnAlctualizar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> comboFiltroCategorias;
    private javax.swing.JComboBox<String> comboRubro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spinerStock;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextField textCodigo;
    private javax.swing.JTextField textDescripcion;
    private javax.swing.JTextField textPrecio;
    // End of variables declaration//GEN-END:variables
}

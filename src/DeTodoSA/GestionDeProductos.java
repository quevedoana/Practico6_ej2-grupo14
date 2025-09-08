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
        cargarCombo();

        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarProductoDeTabla();
            }
        });
        btnGuardar.setEnabled(false);
        initTabla();
        validaciones();
    }

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

    public void cargarCombo() {
        DefaultComboBoxModel<String> categoria = new DefaultComboBoxModel<>();
        categoria.addElement("Seleccionar una categoria");
        if (DeTodo.listaCategoria.isEmpty()) {
            categoria.addElement("No hay categorias cargadas");
        } else {
            for (String c : DeTodo.listaCategoria) {
                categoria.addElement(c);
            }
        }
        comboFiltroCategorias.setModel(categoria);
        comboRubro.setModel(categoria);
    }

    private void seleccionarProductoDeTabla() {
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

    private void habilitarBotonesEdicion() {
        btnGuardar.setEnabled(false);
        btnAlctualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }


    private void habilitarCampos(boolean habilitar) {
        textCodigo.setEnabled(habilitar);
        textDescripcion.setEnabled(habilitar);
        textPrecio.setEnabled(habilitar);
        spinerStock.setEnabled(habilitar);
        comboRubro.setEnabled(habilitar);
    }

    private void limpiarCampos() {
        textCodigo.setText("");
        textDescripcion.setText("");
        textPrecio.setText("");
        spinerStock.setValue(1);
        comboRubro.setSelectedIndex(0);
        tablaProductos.clearSelection();
    }

    

    private void filtrarPorCategoria() {
        String categoriaSeleccionada = comboFiltroCategorias.getSelectedItem().toString();

        if (categoriaSeleccionada.equals("Seleccionar una categoria")
                || categoriaSeleccionada.equals("No hay categorias cargadas")) {
            actualizarTabla(); // Mostrar todos
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

    private void nuevoProducto() {
        limpiarCampos();
        habilitarCampos(true);
        btnGuardar.setEnabled(true);
        btnAlctualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        textCodigo.requestFocus();
    }

    private boolean existeCodigo(int codigo) {
        for (Producto p : DeTodo.listaProductos) {
            if (p.getCodigo() == codigo) {
                return true;
            }
        }
        return false;
    }

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


    private boolean validarCampos() {
        if (textCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del producto", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (textDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la descripción del producto", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (textPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el precio del producto", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            double precio = Double.parseDouble(textPrecio.getText());
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (comboRubro.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría válida", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void validaciones() {
        // Validar que solo se ingresen números en código
        textCodigo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    JOptionPane.showMessageDialog(null, "Solo se permiten números en el código", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Validar que solo se ingresen números y punto en precio
        textPrecio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String texto = textPrecio.getText();

                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    JOptionPane.showMessageDialog(null, "Solo se permiten números y punto decimal en el precio", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Validar que solo haya un punto decimal
                if (c == '.' && texto.contains(".")) {
                    e.consume();
                    JOptionPane.showMessageDialog(null, "Solo se permite un punto decimal", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Eventos para limpiar selección al escribir
        /*textCodigo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                limpiarSeleccion();
            }
        });

        textDescripcion.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                limpiarSeleccion();
            }
        });

        textPrecio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                limpiarSeleccion();
            }
        });

        // Evento para el combo de filtro
        comboFiltroCategorias.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filtrarPorCategoria();
            }
        });*/

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
        jScrollPane2 = new javax.swing.JScrollPane();
        textCodigo = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        textDescripcion = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        textPrecio = new javax.swing.JTextPane();
        comboRubro = new javax.swing.JComboBox<>();
        spinerStock = new javax.swing.JSpinner();
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

        jScrollPane2.setViewportView(textCodigo);

        jScrollPane3.setViewportView(textDescripcion);

        jScrollPane4.setViewportView(textPrecio);

        comboRubro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboRubroActionPerformed(evt);
            }
        });

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane4)
                            .addComponent(comboRubro, 0, 273, Short.MAX_VALUE))
                        .addGap(25, 25, 25))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(spinerStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28))
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
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
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
        btnAgregarNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarNuevoActionPerformed(evt);
            }
        });

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-caja-de-producto-de-pelo-corto-50.png"))); // NOI18N
        btnGuardar.setText("Guardar");

        btnAlctualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-marca-doble-30.png"))); // NOI18N
        btnAlctualizar.setText("Actualizar");
        btnAlctualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAlctualizarMouseClicked(evt);
            }
        });
        btnAlctualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlctualizarActionPerformed(evt);
            }
        });

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        btnEliminar.setText("Eliminar");

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

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void comboRubroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboRubroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboRubroActionPerformed

    private void btnAgregarNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarNuevoActionPerformed
        // TODO add your handling code here:  
    }//GEN-LAST:event_btnAgregarNuevoActionPerformed

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void btnAlctualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlctualizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAlctualizarActionPerformed

    private void btnAlctualizarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAlctualizarMouseClicked
        // TODO add your handling code here:
        actualizarTabla();
    }//GEN-LAST:event_btnAlctualizarMouseClicked

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnAgregarNuevoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarNuevoMouseClicked
        // TODO add your handling code here:
        try {
            int codigo = Integer.parseInt(textCodigo.getText());
            String descrip = textDescripcion.getText();
            double precio = Double.parseDouble(textPrecio.getText());
            String cate = comboFiltroCategorias.getSelectedItem().toString();
            int stock = (Integer) spinerStock.getValue();

            // Verificar si el código ya existe
            if (existeCodigo(codigo)) {
                JOptionPane.showMessageDialog(this, "El código " + codigo + " ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar que se seleccionó una categoría válida
            if (cate.equals("Seleccionar una categoria") || cate.equals("No hay categorias cargadas")) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoría válida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto cargado = new Producto(codigo, descrip, precio, stock, cate);
            DeTodo.listaProductos.add(cargado);

            JOptionPane.showMessageDialog(this, "Producto guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            actualizarTabla();
            limpiarCampos();
            btnGuardar.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error en el formato de los números", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAgregarNuevoMouseClicked


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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner spinerStock;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextPane textCodigo;
    private javax.swing.JTextPane textDescripcion;
    private javax.swing.JTextPane textPrecio;
    // End of variables declaration//GEN-END:variables
}

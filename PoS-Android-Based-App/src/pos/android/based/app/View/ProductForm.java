package pos.android.based.app.View;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Desi
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;
import pos.android.based.app.product.ProductService;
import java.net.URL;
import pos.android.based.app.product.Product;
import com.toedter.calendar.JDateChooser;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductForm extends JFrame {
 
private DefaultTableModel tableModel;
    
    public ProductForm() throws MalformedURLException {
        setTitle("Product Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        initComponents();
        productTypeComboBoxActionPerformed(null);     
        String[] columnNames = {"ID", "Name", "Price", "Stock", "Type"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable.setModel(tableModel);
        loadProduct();
       
    }
    
  
    
 //TAMBAH PRODUK
 private void addProduct(ActionEvent evt) throws MalformedURLException {
    String name = productNameField.getText();
    double price = Double.parseDouble(productPriceField.getText());
    int stock = Integer.parseInt(productStockField.getText());
    String type = (String) productTypeComboBox.getSelectedItem();
    LocalDate expiryDate = null;
    if ("Perishable".equals(type)) {
        expiryDate = expiryDateChooser.getDate().toInstant().atZone(java.time.ZoneOffset.UTC).toLocalDate();
    }
    boolean success = false;
    switch (type) {
        case "Non-Perishable":
            success = ProductService.addNonPerishable(name, price, stock);
            break;
        case "Perishable":
            success = ProductService.addPerishable(name, price, stock, expiryDate);
            break;
        case "Digital":
            try {
                URL url = new URL("https://somevendor.com/product");
                success = ProductService.addDigital(name, price, stock, url, "VendorX");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid URL.");
                ex.printStackTrace();
            }
            break;
    }
    if (success) {
        JOptionPane.showMessageDialog(this, "Product added successfully!");
        loadProduct();  
    } else {
        JOptionPane.showMessageDialog(this, "Error adding product.");
    }
}
 
    //hapus product
    private void deleteProduct (ActionEvent evt) throws MalformedURLException {
    int selectedRow = productTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a product to delete.");
        return;
    }
    String productId = (String) productTable.getValueAt(selectedRow, 0); 
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = ProductService.deleteProduct(productId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            loadProduct();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete product.");
        }
    }
 }

    //update product
    private void updateProduct(ActionEvent evt) throws MalformedURLException {
    int selectedRow = productTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a product to update.");
        return;
    }
    String id = (String) productTable.getValueAt(selectedRow, 0);
    String oldName = (String) productTable.getValueAt(selectedRow, 1);
    double oldPrice = Double.parseDouble(productTable.getValueAt(selectedRow, 2).toString());
    int oldStock = Integer.parseInt(productTable.getValueAt(selectedRow, 3).toString());
    String oldType = (String) productTable.getValueAt(selectedRow, 4);
    String nameInput = productNameField.getText().trim();
    String priceInput = productPriceField.getText().replace(",", ".").trim();
    String stockInput = productStockField.getText().trim();
    String typeInput = (String) productTypeComboBox.getSelectedItem();
    String name = nameInput.isEmpty() ? oldName : nameInput;
    double price;
    int stock;
    try {
        price = priceInput.isEmpty() ? oldPrice : Double.parseDouble(priceInput);
        stock = stockInput.isEmpty() ? oldStock : Integer.parseInt(stockInput);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid price or stock format.");
        return;
    }
    String type = (typeInput == null || typeInput.isEmpty()) ? oldType : typeInput;
    LocalDate expiryDate = null;
    if ("Perishable".equals(type)) {
        if (expiryDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select an expiration date.");
            return;
        }
        expiryDate = expiryDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    boolean success = ProductService.updateProduct(id, name, price, stock, type, expiryDate);
    if (success) {
        JOptionPane.showMessageDialog(this, "Product updated successfully!");
        loadProduct();
        productNameField.setText("");
        productPriceField.setText("");
        productStockField.setText("");
        productTypeComboBox.setSelectedIndex(0);
        expiryDateChooser.setDate(null);
    } else {
        JOptionPane.showMessageDialog(this, "Failed to update product.");
    }   
    }

    //jdialog
    private void openBundleDialog() throws MalformedURLException {
    String input = JOptionPane.showInputDialog(this, "Masukkan jumlah produk untuk bundle:");
    int jumlahProduk;
    try {
        jumlahProduk = Integer.parseInt(input);
        if (jumlahProduk <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah produk harus lebih dari 0.");
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Masukkan angka yang valid.");
        return;
    }
    JDialog dialog = new JDialog(this, "Pilih " + jumlahProduk + " Produk untuk Bundle", true);
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(this);
    List<Product> productList = ProductService.getAllProducts();
    DefaultListModel<Product> listModel = new DefaultListModel<>();
    for (Product p : productList) listModel.addElement(p);
    JList<Product> productJList = new JList<>(listModel);
    productJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    JScrollPane scrollPane = new JScrollPane(productJList);
    JButton confirmButton = new JButton("Lanjut");
    confirmButton.addActionListener(e -> {
        List<Product> selected = productJList.getSelectedValuesList();
        if (selected.size() != jumlahProduk) {
            JOptionPane.showMessageDialog(dialog, "Anda harus memilih tepat " + jumlahProduk + " produk.");
            return;
        }
        String namaBundle = JOptionPane.showInputDialog(this, "Masukkan nama bundle:");
        if (namaBundle == null || namaBundle.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama bundle tidak boleh kosong.");
            return;
        }
        String hargaStr = JOptionPane.showInputDialog(this, "Masukkan harga bundle:");
        double hargaBundle;
        try {
            hargaBundle = Double.parseDouble(hargaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga tidak valid.");
            return;
        }
        boolean success = ProductService.addBundle(namaBundle, hargaBundle, 1, selected);
        if (success) {
            JOptionPane.showMessageDialog(this, "Bundle berhasil ditambahkan.");
            try {
                loadProduct();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan bundle.");
        }
    });
    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(confirmButton, BorderLayout.SOUTH);
    dialog.setVisible(true);
}

    
    //create bundle
    private void createBundle(List<Product> items) {
    String name = JOptionPane.showInputDialog(this, "Bundle Name:");
    double bundlePrice = Double.parseDouble(JOptionPane.showInputDialog(this, "Bundle Price:"));
    double totalNormalPrice = items.stream().mapToDouble(Product::getPrice).sum();
    int stock = 1;
    boolean success = ProductService.addBundle(name, bundlePrice, stock, items); // ✅ DI SINI dibuat
    if (success) {
        JOptionPane.showMessageDialog(this, "Bundle added!\nTotal: " + totalNormalPrice + "\nDiscounted: " + bundlePrice);
        try {
            loadProduct();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Failed to add bundle.");
    }
}






     
     
 
    
    
   
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        productNameField = new javax.swing.JTextField();
        productPriceField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        productTypeComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        expiryDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        productStockField = new javax.swing.JTextField();
        addToBundleButton = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel3.setText("Price");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 720));

        productTable.setBackground(new java.awt.Color(153, 153, 153));
        productTable.setForeground(new java.awt.Color(0, 0, 0));
        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Price", "Stock", "Type"
            }
        ));
        productTable.setSelectionBackground(new java.awt.Color(204, 204, 204));
        productTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        productTable.setShowGrid(false);
        jScrollPane1.setViewportView(productTable);

        addButton.setBackground(new java.awt.Color(250, 193, 217));
        addButton.setForeground(new java.awt.Color(30, 30, 30));
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        updateButton.setBackground(new java.awt.Color(250, 193, 217));
        updateButton.setForeground(new java.awt.Color(30, 30, 30));
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setBackground(new java.awt.Color(250, 193, 217));
        deleteButton.setForeground(new java.awt.Color(30, 30, 30));
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        productNameField.setBackground(new java.awt.Color(204, 204, 204));
        productNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productNameFieldActionPerformed(evt);
            }
        });

        productPriceField.setBackground(new java.awt.Color(204, 204, 204));
        productPriceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productPriceFieldActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Name");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Price");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Type");

        productTypeComboBox.setBackground(new java.awt.Color(204, 204, 204));
        productTypeComboBox.setForeground(new java.awt.Color(0, 0, 0));
        productTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Non-Perishable", "Perishable", "Digital" }));
        productTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productTypeComboBoxActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Product Management");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Exp");

        expiryDateChooser.setBackground(new java.awt.Color(153, 153, 153));
        expiryDateChooser.setForeground(new java.awt.Color(0, 0, 0));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Stock");

        productStockField.setBackground(new java.awt.Color(204, 204, 204));
        productStockField.setCaretColor(new java.awt.Color(154, 154, 154));

        addToBundleButton.setBackground(new java.awt.Color(250, 193, 217));
        addToBundleButton.setForeground(new java.awt.Color(0, 0, 0));
        addToBundleButton.setText("Add to Bundle");
        addToBundleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToBundleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 905, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addGap(35, 35, 35))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(productNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(70, 70, 70)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(productStockField))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(productTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)))
                        .addComponent(expiryDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(356, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(450, 450, 450)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(updateButton)
                        .addGap(18, 18, 18)
                        .addComponent(deleteButton)
                        .addGap(32, 32, 32)
                        .addComponent(addToBundleButton)))
                .addContainerGap(623, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(productNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(productTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(productPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel7)
                                        .addComponent(productStockField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(expiryDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addButton)
                            .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(addToBundleButton)))
                        .addGap(64, 64, 64))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(802, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //tombol add product
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String name = productNameField.getText();
        double price = Double.parseDouble(productPriceField.getText());
        int stock = Integer.parseInt(productStockField.getText());
        String type = (String) productTypeComboBox.getSelectedItem();
        LocalDate expiryDate = null;
        
        if ("Perishable".equals(type)) {
            if (expiryDateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Please select an expiration date.");
                return;
            }
            expiryDate = expiryDateChooser.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
        } else {
        expiryDate = null; 
        }        
        boolean success = false;
        System.out.println("ADD PERISHABLE:");
        System.out.println("Name: " + name);
        System.out.println("Price: " + price);
        System.out.println("Stock: " + stock);
        System.out.println("Type: " + type);
        System.out.println("Expiry: " + expiryDate);
        switch (type) {
        case "Non-Perishable":
            success = ProductService.addNonPerishable(name, price, stock);
            break;
        case "Perishable":
            success = ProductService.addPerishable(name, price, stock, expiryDate);
            break;
        case "Digital":
            try {
                URL url = new URL("https://somevendor.com/product");
                success = ProductService.addDigital(name, price, stock, url, "VendorX");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid URL.");
            }
            break;
        case "Bundle":
             
            break;
    }
    if (success) {
        JOptionPane.showMessageDialog(this, "Product added successfully!");
          try {  
                loadProduct();
            } catch (MalformedURLException ex) {
                Logger.getLogger(ProductForm.class.getName()).log(Level.SEVERE, null, ex);
            }
           productNameField.setText("");
        productPriceField.setText("");
        productStockField.setText("");
        productTypeComboBox.setSelectedIndex(0);  
        expiryDateChooser.setDate(null);  
    } else {
        JOptionPane.showMessageDialog(this, "Error adding product.");
    } 
    }//GEN-LAST:event_addButtonActionPerformed

    private void productPriceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productPriceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productPriceFieldActionPerformed

    //DROPDOWN PERISHABLE, NON-PERISHABLE, DIGITAL
    private void productTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productTypeComboBoxActionPerformed
        // TODO add your handling code here:
        String selectedType = (String) productTypeComboBox.getSelectedItem();
        if ("Perishable".equalsIgnoreCase(selectedType)) {
            expiryDateChooser.setEnabled(true);
        } else {
            expiryDateChooser.setEnabled(false);
            expiryDateChooser.setDate(null);  // bersihkan isinya
        }
    }//GEN-LAST:event_productTypeComboBoxActionPerformed

    //TOMBOL DELETE
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        try {
        deleteProduct(evt);
        } catch (MalformedURLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage());
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void productNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productNameFieldActionPerformed

    //TOMBOL UPDATE
    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        // TODO add your handling code here:
    try {
        updateProduct(evt);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid price or stock format.");
    } catch (MalformedURLException e) {
        JOptionPane.showMessageDialog(this, "URL error: " + e.getMessage());
    }

    }//GEN-LAST:event_updateButtonActionPerformed

    //TOMBOL BUNDLE
    private void addToBundleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToBundleButtonActionPerformed
    try {
        // TODO add your handling code here:
        openBundleDialog();
    } catch (MalformedURLException ex) {
        Logger.getLogger(ProductForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_addToBundleButtonActionPerformed

  

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProductForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ProductForm().setVisible(true);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ProductForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton addToBundleButton;
    private javax.swing.JButton deleteButton;
    private com.toedter.calendar.JDateChooser expiryDateChooser;
    private javax.swing.JComboBox<String> jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField productNameField;
    private javax.swing.JTextField productPriceField;
    private javax.swing.JTextField productStockField;
    private javax.swing.JTable productTable;
    private javax.swing.JComboBox<String> productTypeComboBox;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables


   
    
    //lOAD
    private void loadProduct() throws MalformedURLException {
    tableModel.setRowCount(0); 
    List<Product> products = ProductService.getAllProducts();
    for (Product product : products) {
        tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getType()});
    }
}

}

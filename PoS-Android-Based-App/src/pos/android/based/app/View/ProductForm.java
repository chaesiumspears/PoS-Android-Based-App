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
import pos.android.based.app.product.DigitalProduct;
import com.toedter.calendar.JDateChooser;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import pos.android.based.app.View.MainUI;
import static pos.android.based.app.View.ManageUserForm.role;
import static pos.android.based.app.View.ManageUserForm.username;


public class ProductForm extends JFrame {
 
private DefaultTableModel tableModel;
private String loggedInUsername;
private String userRole;
    
    public ProductForm(String username, String role) throws MalformedURLException {      
        setTitle("Product Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        this.loggedInUsername = username;
        this.userRole = role;
        initComponents();
        productTypeComboBoxActionPerformed(null);     
        String[] columnNames = {"ID", "Name", "Price", "Stock","Vendor","URL","Type"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable.setModel(tableModel);
        loadProduct();

       
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
    JDialog dialog = new JDialog(this, "Pilih Produk untuk Bundle", true);
    dialog.setSize(600, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JTable table = createBundleSelectionTable();
    JScrollPane scrollPane = new JScrollPane(table);
    JButton confirmButton = new JButton("Buat Bundle");

    confirmButton.addActionListener(e -> {
        try {
            List<Product> selectedItems = collectSelectedProducts(table);
            if (selectedItems.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Pilih minimal 1 produk dengan Qty > 0.");
                return;
            }

            String[] bundleInfo = promptBundleDetails(dialog);
            if (bundleInfo == null) return;

            String namaBundle = bundleInfo[0];
            double hargaBundle = Double.parseDouble(bundleInfo[1]);
            
            boolean confirmed = showBundlePreview(namaBundle, hargaBundle, selectedItems);
            if (!confirmed) return;

            confirmAndSaveBundle(namaBundle, hargaBundle, selectedItems, dialog);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Terjadi kesalahan: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    dialog.add(new JLabel("Isi kolom Qty untuk memilih produk"), BorderLayout.NORTH);
    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(confirmButton, BorderLayout.SOUTH);
    dialog.setVisible(true);
    }

    private JTable createBundleSelectionTable() throws MalformedURLException {
    List<Product> productList = ProductService.getAllProducts();
    String[] columns = {"ID", "Name", "Price", "Qty"};

    DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 3 ? Integer.class : String.class;
        }
    };

    for (Product p : productList) {
        tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), 0});
    }

    return new JTable(tableModel);
    }

    private List<Product> collectSelectedProducts(JTable table) {
    List<Product> selectedItems = new ArrayList<>();
    DefaultTableModel model = (DefaultTableModel) table.getModel();

    for (int i = 0; i < model.getRowCount(); i++) {
        int qty;
        try {
            qty = Integer.parseInt(model.getValueAt(i, 3).toString());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Qty harus berupa angka.");
        }

        if (qty > 0) {
            String id = model.getValueAt(i, 0).toString();
            Product product = ProductService.getProductById(id);
            for (int j = 0; j < qty; j++) {
                selectedItems.add(product);
            }
        }
    }
    return selectedItems;
    }
    
    //preview bundle
    private boolean showBundlePreview(String namaBundle, double hargaBundle, List<Product> items) {
    Map<String, Long> grouped = items.stream().collect(Collectors.groupingBy(Product::getName, Collectors.counting()));
    double totalHargaNormal = items.stream().mapToDouble(Product::getPrice).sum();
    StringBuilder preview = new StringBuilder("Bundle Name: " + namaBundle + "\n");
    preview.append("Total Normal Price: ").append(String.format("%.2f", totalHargaNormal)).append("\n");
    preview.append("Bundle Price: ").append(String.format("%.2f", hargaBundle)).append("\n");
    preview.append("Discount: ").append(String.format("%.2f", totalHargaNormal - hargaBundle)).append("\n\n");
    for (Map.Entry<String, Long> entry : grouped.entrySet()) {
        preview.append("- ").append(entry.getKey()).append(" x").append(entry.getValue()).append("\n");
    }
    int confirm = JOptionPane.showConfirmDialog(this, new JTextArea(preview.toString()), "Konfirmasi Bundle", JOptionPane.OK_CANCEL_OPTION);
    return confirm == JOptionPane.OK_OPTION;
    }


    private String[] promptBundleDetails(Component parent) {
    String namaBundle = JOptionPane.showInputDialog(parent, "Nama bundle:");
    if (namaBundle == null || namaBundle.trim().isEmpty()) {
        JOptionPane.showMessageDialog(parent, "Nama bundle tidak boleh kosong.");
        return null;
    }

    String hargaStr = JOptionPane.showInputDialog(parent, "Harga bundle:");
    try {
        Double.parseDouble(hargaStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(parent, "Harga tidak valid.");
        return null;
    }

    return new String[]{namaBundle.trim(), hargaStr.trim()};
}

    private void confirmAndSaveBundle(String namaBundle, double hargaBundle, List<Product> items, JDialog dialog) {
    boolean success = ProductService.addBundle(namaBundle, hargaBundle, 1, items);
    if (success) {
        JOptionPane.showMessageDialog(this, "Bundle berhasil ditambahkan.");
        try {
            loadProduct();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        dialog.dispose();
    } else {
        JOptionPane.showMessageDialog(this, "Gagal menambahkan bundle.");
    }
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        updateButtpn = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        productNameField = new javax.swing.JTextField();
        productPriceField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        productTypeComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        productStockField = new javax.swing.JTextField();
        expiryDateChooser = new org.netbeans.modules.form.InvalidComponent();
        urlField = new javax.swing.JTextField();
        vendorField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        addToBundleButton = new javax.swing.JButton();
        BackButton = new javax.swing.JButton();

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
        jPanel1.setPreferredSize(new java.awt.Dimension(660, 550));

        productTable.setBackground(new java.awt.Color(204, 204, 204));
        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Price", "Stock", "Vendor", "URL", "Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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

        updateButtpn.setBackground(new java.awt.Color(250, 193, 217));
        updateButtpn.setForeground(new java.awt.Color(30, 30, 30));
        updateButtpn.setText("Update");
        updateButtpn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtpnActionPerformed(evt);
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

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Name");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Price");

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

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Exp");

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Stock");

        productStockField.setBackground(new java.awt.Color(204, 204, 204));
        productStockField.setCaretColor(new java.awt.Color(154, 154, 154));

        urlField.setBackground(new java.awt.Color(204, 204, 204));
        urlField.setForeground(new java.awt.Color(0, 0, 0));

        vendorField.setBackground(new java.awt.Color(204, 204, 204));
        vendorField.setForeground(new java.awt.Color(0, 0, 0));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("URL");

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Vendor");

        addToBundleButton.setBackground(new java.awt.Color(250, 193, 217));
        addToBundleButton.setForeground(new java.awt.Color(0, 0, 0));
        addToBundleButton.setText("Add to Bundle");
        addToBundleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToBundleButtonActionPerformed(evt);
            }
        });

        BackButton.setBackground(new java.awt.Color(250, 193, 217));
        BackButton.setForeground(new java.awt.Color(0, 0, 0));
        BackButton.setText("Back");
        BackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(updateButtpn)
                        .addGap(18, 18, 18)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addToBundleButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(productPriceField)
                            .addComponent(productNameField)
                            .addComponent(productTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jLabel7)
                                .addGap(22, 22, 22))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(vendorField)
                            .addComponent(urlField)
                            .addComponent(productStockField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(expiryDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(BackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(229, 229, 229))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(BackButton)))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(productStockField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(urlField)
                                .addComponent(jLabel8))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(productPriceField)
                                .addComponent(jLabel2))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expiryDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vendorField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(productTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButtpn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addToBundleButton))
                .addGap(54, 54, 54))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //untuk tombol add product
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String name = productNameField.getText().trim();
        String priceStr = productPriceField.getText().trim().replace(",", ".");
        String stockStr = productStockField.getText().trim();
        String type = (String) productTypeComboBox.getSelectedItem();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, harga, dan stok harus diisi.");
            return;
        }

        double price;
        int stock;

        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format harga atau stok tidak valid.");
        return;
        }

        LocalDate expiryDate = null;
        boolean success = false;

        switch (type) {
            case "Non-Perishable":
                success = ProductService.addNonPerishable(name, price, stock);
                break;
            case "Perishable":
                if (expiryDateChooser.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Silakan pilih tanggal kadaluarsa.");
                    return;
                }
                expiryDate = expiryDateChooser.getDate().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                success = ProductService.addPerishable(name, price, stock, expiryDate);
                break;
            case "Digital":
                String urlStr = urlField.getText().trim();
                String vendor = vendorField.getText().trim();

                if (urlStr.isEmpty() || vendor.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "URL dan Vendor harus diisi untuk produk digital.");
                    return;
                }
                try {
                    URL url = new URL(urlStr);
                    success = ProductService.addDigital(name, price, stock, url, vendor);
                } catch (MalformedURLException e) {
                    JOptionPane.showMessageDialog(this, "Format URL tidak valid.");
                    return;
                }
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
            urlField.setText("");
            vendorField.setText("");
            productTypeComboBox.setSelectedIndex(0);  
            expiryDateChooser.setDate(null);  
        } else {
            JOptionPane.showMessageDialog(this, "Error adding product.");
    
        }
    
    }//GEN-LAST:event_addButtonActionPerformed

    private void productPriceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productPriceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productPriceFieldActionPerformed

    private void productTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productTypeComboBoxActionPerformed
        // TODO add your handling code here:
        String selectedType = (String) productTypeComboBox.getSelectedItem();
        boolean isPerishable = "Perishable".equalsIgnoreCase(selectedType);
        boolean isDigital = "Digital".equalsIgnoreCase(selectedType);

        expiryDateChooser.setEnabled(isPerishable);
        expiryDateChooser.setDate(isPerishable ? expiryDateChooser.getDate() : null);

        urlField.setEnabled(isDigital);
        vendorField.setEnabled(isDigital);
            if (!isDigital) {
        urlField.setText("");
        vendorField.setText("");
    }
    }//GEN-LAST:event_productTypeComboBoxActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        try {
        deleteProduct(evt);
        } catch (MalformedURLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage());
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void updateButtpnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtpnActionPerformed
        // TODO add your handling code here:
        try {
            updateProduct(evt);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price or stock format.");
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(this, "URL error: " + e.getMessage());
        }
    }//GEN-LAST:event_updateButtpnActionPerformed

    private void addToBundleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToBundleButtonActionPerformed
        // TODO add your handling code here
         try {
        // TODO add your handling code here:
        openBundleDialog();
    } catch (MalformedURLException ex) {
        Logger.getLogger(ProductForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_addToBundleButtonActionPerformed

    private void productNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productNameFieldActionPerformed

    private void BackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackButtonActionPerformed
        // TODO add your handling code here:
        new MainUI(loggedInUsername, userRole).setVisible(true);
        dispose();
    }//GEN-LAST:event_BackButtonActionPerformed

  

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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    String username = "demoUser";
                    String role = "admin";
                    new ProductForm(username,role).setVisible(true);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ProductForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackButton;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addToBundleButton;
    private javax.swing.JButton deleteButton;
    private org.netbeans.modules.form.InvalidComponent expiryDateChooser;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JButton updateButtpn;
    private javax.swing.JTextField urlField;
    private javax.swing.JTextField vendorField;
    // End of variables declaration//GEN-END:variables


 
 //Load
   private void loadProduct() throws MalformedURLException {
    tableModel.setRowCount(0); 
    List<Product> products = ProductService.getAllProducts();
    for (Product product : products) {
        tableModel.addRow(new Object[]{
            product.getId(), 
            product.getName(), 
            product.getPrice(), 
            product.getStock(), 
            product.getType(),
            (product instanceof DigitalProduct) ? ((DigitalProduct) product).getVendorName() : "",
            (product instanceof DigitalProduct) ? ((DigitalProduct) product).getUrl().toString() : ""
        });
    }
}
}

package pos.android.based.app.View;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import pos.android.based.app.product.Product;
import pos.android.based.app.product.ProductService;
import pos.android.based.app.transactions.*;
import pos.android.based.app.View.PurchaseUI;

public class TransactionUI extends javax.swing.JFrame {

    private Map<Integer, Transactions> transactionMap = new HashMap<>();
    private ProductService productService;
    private static int currentTransactionId = 1; 
    
    private TransactionDAO transactionDAO; 
    
    public TransactionUI() {
        initComponents();
        productService = new ProductService();
        transactionDAO = new TransactionDAO();
        setupTableListener();
    }

    private void setupTableListener() {
    DefaultTableModel model = (DefaultTableModel) daftarBelanjaTable.getModel();
        model.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 4) { // Kolom Qty
                try {
                    int qty = Integer.parseInt(model.getValueAt(row, 4).toString());
                    BigDecimal price = new BigDecimal(model.getValueAt(row, 3).toString());
                    model.setValueAt(price.multiply(BigDecimal.valueOf(qty)), row, 5);

                    
                    int transactionId = Integer.parseInt(transactionIdTextField.getText());
                    Transactions transaction = transactionMap.get(transactionId);

                    if (transaction != null) {
                        String productId = model.getValueAt(row, 1).toString();
                        for (TransactionItem item : transaction.getItems()) {
                            if (item.getProductId().equals(productId)) {
                                item.setQuantity(qty);
                                item.setUnitPrice(price); 
                                break;
                            }
                       }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Qty harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(1, row, 4);
                    double price = Double.parseDouble(model.getValueAt(row, 3).toString());
                    model.setValueAt(price, row, 5);
                }
            }
        });
    }

    private void updateTotalPrice() {
        DefaultTableModel model = (DefaultTableModel) daftarBelanjaTable.getModel();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (int i = 0; i < model.getRowCount(); i++) {
            int quantity = (int) model.getValueAt(i, 4);
            BigDecimal unitPrice = (BigDecimal) model.getValueAt(i, 3);
            BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            model.setValueAt(subTotal, i, 5);  // Update subtotal in the table
            totalPrice = totalPrice.add(subTotal);
        }
    }

    private int generateTransactionId() {
        return currentTransactionId++;
    }
    
    private void addItemToCart(TransactionItem item, Product product) {
        DefaultTableModel model = (DefaultTableModel) daftarBelanjaTable.getModel();
        model.addRow(new Object[]{
                model.getRowCount() + 1,
                item.getProductId(),
                product.getName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubTotal()
        });
    }
    
    private void refreshTransactionTable() {
        DefaultTableModel model = (DefaultTableModel) daftarBelanjaTable.getModel();
        model.setRowCount(0);
    }
    
    private void showPurchaseUI(Transactions transaction) {
        PurchaseUI purchaseUI = new PurchaseUI();
        purchaseUI.setTransactionId(transaction.getTransactionId());
        purchaseUI.setTotal(String.valueOf(transaction.calculateTotalPrice()));
        purchaseUI.setDate(""); // Admin input manual
        purchaseUI.setVisible(true);
    }
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        purchaseBtn1 = new javax.swing.JButton();
        TransactionPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        idLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        nameLabel = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        idTextField = new javax.swing.JTextField();
        nameTextField = new javax.swing.JTextField();
        priceTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        daftarBelanjaTable = new javax.swing.JTable();
        checkoutBtn = new javax.swing.JButton();
        addToCartBtn = new javax.swing.JButton();
        refundBtn = new javax.swing.JButton();
        transactionIdLabel = new javax.swing.JLabel();
        transactionIdTextField = new javax.swing.JTextField();

        purchaseBtn1.setBackground(new java.awt.Color(251, 193, 217));
        purchaseBtn1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        purchaseBtn1.setForeground(new java.awt.Color(31, 31, 31));
        purchaseBtn1.setText("Purchase");
        purchaseBtn1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        purchaseBtn1.setPreferredSize(new java.awt.Dimension(70, 30));
        purchaseBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchaseBtn1ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TransactionPanel.setBackground(new java.awt.Color(0, 0, 0));
        TransactionPanel.setName(""); // NOI18N
        TransactionPanel.setPreferredSize(new java.awt.Dimension(660, 560));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        idLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        idLabel.setForeground(new java.awt.Color(251, 193, 217));
        idLabel.setText("ID");

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        nameLabel.setForeground(new java.awt.Color(251, 193, 217));
        nameLabel.setText("Name");

        priceLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        priceLabel.setForeground(new java.awt.Color(251, 193, 217));
        priceLabel.setText("Price");

        idTextField.setBackground(new java.awt.Color(200, 200, 200));
        idTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idTextFieldActionPerformed(evt);
            }
        });

        nameTextField.setBackground(new java.awt.Color(200, 200, 200));

        priceTextField.setBackground(new java.awt.Color(200, 200, 200));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(idTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(priceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(priceTextField))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(nameTextField)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idLabel)
                    .addComponent(idTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(priceLabel)
                    .addComponent(priceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        daftarBelanjaTable.setBackground(new java.awt.Color(31, 31, 31));
        daftarBelanjaTable.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        daftarBelanjaTable.setForeground(new java.awt.Color(250, 250, 250));
        daftarBelanjaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Name", "Price", "Qty", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        daftarBelanjaTable.setGridColor(new java.awt.Color(251, 193, 217));
        daftarBelanjaTable.setName(""); // NOI18N
        daftarBelanjaTable.setShowGrid(true);
        daftarBelanjaTable.setSurrendersFocusOnKeystroke(true);
        jScrollPane1.setViewportView(daftarBelanjaTable);
        if (daftarBelanjaTable.getColumnModel().getColumnCount() > 0) {
            daftarBelanjaTable.getColumnModel().getColumn(0).setResizable(false);
            daftarBelanjaTable.getColumnModel().getColumn(0).setPreferredWidth(5);
            daftarBelanjaTable.getColumnModel().getColumn(1).setResizable(false);
            daftarBelanjaTable.getColumnModel().getColumn(1).setPreferredWidth(7);
            daftarBelanjaTable.getColumnModel().getColumn(2).setResizable(false);
            daftarBelanjaTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            daftarBelanjaTable.getColumnModel().getColumn(3).setResizable(false);
            daftarBelanjaTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            daftarBelanjaTable.getColumnModel().getColumn(4).setResizable(false);
            daftarBelanjaTable.getColumnModel().getColumn(4).setPreferredWidth(7);
            daftarBelanjaTable.getColumnModel().getColumn(5).setResizable(false);
            daftarBelanjaTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        checkoutBtn.setBackground(new java.awt.Color(251, 193, 217));
        checkoutBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        checkoutBtn.setForeground(new java.awt.Color(31, 31, 31));
        checkoutBtn.setText("Checkout");
        checkoutBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        checkoutBtn.setPreferredSize(new java.awt.Dimension(70, 30));
        checkoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkoutBtnActionPerformed(evt);
            }
        });

        addToCartBtn.setBackground(new java.awt.Color(251, 193, 217));
        addToCartBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addToCartBtn.setForeground(new java.awt.Color(31, 31, 31));
        addToCartBtn.setText("Add to Cart");
        addToCartBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addToCartBtn.setPreferredSize(new java.awt.Dimension(70, 30));
        addToCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToCartBtnActionPerformed(evt);
            }
        });

        refundBtn.setBackground(new java.awt.Color(251, 193, 217));
        refundBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        refundBtn.setForeground(new java.awt.Color(31, 31, 31));
        refundBtn.setText("Refund");
        refundBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        refundBtn.setPreferredSize(new java.awt.Dimension(70, 30));
        refundBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refundBtnActionPerformed(evt);
            }
        });

        transactionIdLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        transactionIdLabel.setForeground(new java.awt.Color(251, 193, 217));
        transactionIdLabel.setText("Transaction ID");

        transactionIdTextField.setBackground(new java.awt.Color(200, 200, 200));
        transactionIdTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionIdTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TransactionPanelLayout = new javax.swing.GroupLayout(TransactionPanel);
        TransactionPanel.setLayout(TransactionPanelLayout);
        TransactionPanelLayout.setHorizontalGroup(
            TransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TransactionPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(TransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TransactionPanelLayout.createSequentialGroup()
                        .addComponent(transactionIdLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(transactionIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addToCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(refundBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))
                    .addGroup(TransactionPanelLayout.createSequentialGroup()
                        .addGroup(TransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addGap(23, 23, 23))))
        );
        TransactionPanelLayout.setVerticalGroup(
            TransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TransactionPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TransactionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(checkoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(addToCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(refundBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(transactionIdLabel))
                    .addComponent(transactionIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(TransactionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(TransactionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkoutBtnActionPerformed
        String transactionIdStr = transactionIdTextField.getText();
        if (transactionIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transaction to purchase.");
            return;
        }

        int transactionId = Integer.parseInt(transactionIdStr);
        Transactions transaction = transactionMap.get(transactionId);

        if (transaction == null || transaction.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        transaction.setStatus("unpaid"); 

        boolean success = transactionDAO.addTransaction(transaction);
        if (success) {
            showPurchaseUI(transaction);
        } else {
            JOptionPane.showMessageDialog(this, "Transaction failed to save.");
        }
    }//GEN-LAST:event_checkoutBtnActionPerformed

    private void idTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idTextFieldActionPerformed
        String productId = idTextField.getText().trim();
        if (!productId.isEmpty()) {
            Product product = productService.getProductById(productId);
            if (product != null) {
                nameTextField.setText(product.getName());
                priceTextField.setText(String.valueOf(product.getPrice()));
            } else {
                JOptionPane.showMessageDialog(this, "Product not found!", "Not Found", JOptionPane.WARNING_MESSAGE);
                nameTextField.setText("");
                priceTextField.setText("");
            }
        }
    }//GEN-LAST:event_idTextFieldActionPerformed

    private void purchaseBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purchaseBtn1ActionPerformed
     
    }//GEN-LAST:event_purchaseBtn1ActionPerformed

    private void refundBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refundBtnActionPerformed
        
    }//GEN-LAST:event_refundBtnActionPerformed

    private void addToCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToCartBtnActionPerformed
        String id = idTextField.getText().trim();
        String name = nameTextField.getText().trim();
        String priceText = priceTextField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter product details.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText); 
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price.");
            return;
        }

        // Get product from the service
        Product product = productService.getProductById(id);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product not found in the system.");
            return;
        }

        // Validate stock
        if (product.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "Product is out of stock.");
            return;
        }

        int transactionId;
        if (transactionIdTextField.getText().isEmpty()) {
            transactionId = generateTransactionId();
            transactionIdTextField.setText(String.valueOf(transactionId));
            Transactions newTransaction = new Transactions(transactionId);
            newTransaction.setTransactionDate(LocalDate.now()); // Set transaction date
            transactionMap.put(transactionId, newTransaction);
        } else {
            transactionId = Integer.parseInt(transactionIdTextField.getText());
            if (!transactionMap.containsKey(transactionId)) {
                Transactions newTransaction = new Transactions(transactionId);
                newTransaction.setTransactionDate(LocalDate.now()); // Set transaction date
                transactionMap.put(transactionId, newTransaction);
            }
        }

        Transactions transaksi = transactionMap.get(transactionId);

        TransactionItem item = new TransactionItem(product.getId(), 1, price);
        transaksi.addItem(item);

        // Add item to cart table
        addItemToCart(item, product);

        // Clear input fields
        idTextField.setText("");
        nameTextField.setText("");
        priceTextField.setText("");
    }//GEN-LAST:event_addToCartBtnActionPerformed

    private void transactionIdTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionIdTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transactionIdTextFieldActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransactionUI().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel TransactionPanel;
    private javax.swing.JButton addToCartBtn;
    private javax.swing.JButton checkoutBtn;
    private javax.swing.JTable daftarBelanjaTable;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idTextField;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JTextField priceTextField;
    private javax.swing.JButton purchaseBtn1;
    private javax.swing.JButton refundBtn;
    private javax.swing.JLabel transactionIdLabel;
    private javax.swing.JTextField transactionIdTextField;
    // End of variables declaration//GEN-END:variables

}

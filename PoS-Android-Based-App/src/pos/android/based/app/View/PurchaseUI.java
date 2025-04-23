package pos.android.based.app.View;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pos.android.based.app.transactions.PurchaseTransaction;
import pos.android.based.app.transactions.TransactionDAO;
import pos.android.based.app.transactions.TransactionItem;
import pos.android.based.app.transactions.TransactionLogService;
import pos.android.based.app.transactions.Transactions;

public class PurchaseUI extends javax.swing.JFrame {
    private String loggedInUsername;
    private String userRole;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String today = LocalDate.now().format(formatter);
    private javax.swing.JTable daftarBelanjaTable;

    public PurchaseUI(String username, String role) throws MalformedURLException {
        initComponents();
        dateTextField.setText(today);
        purchaseBtn.addActionListener(e -> processPurchase());
        cashTextField.addActionListener(e -> calculateChange());
        
        if (daftarBelanjaTable == null) {
            daftarBelanjaTable = new javax.swing.JTable();
        }
    }
        
        private void calculateChange() {
        try {
            BigDecimal total = new BigDecimal(totalTextField.getText());
            BigDecimal cash = new BigDecimal(cashTextField.getText());

            if (cash.compareTo(total) < 0) {
                JOptionPane.showMessageDialog(this, "Cash tidak mencukupi!");
                return;
            }

            BigDecimal change = cash.subtract(total);
            chageTextField.setText(change.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Input tidak valid: " + ex.getMessage());
        }
    }

    private void processPurchase() {
        try {
            int transactionId = Integer.parseInt(transactionIdTextField.getText());
            BigDecimal cash = new BigDecimal(cashTextField.getText());

            Transactions transaction = TransactionDAO.getTransactionById(transactionId);
            if (transaction == null) {
                JOptionPane.showMessageDialog(this, "Transaksi tidak ditemukan.");
                return;
            }

            // Create PurchaseTransaction instance
            PurchaseTransaction purchaseTransaction = new PurchaseTransaction(transaction.getItems());
            purchaseTransaction.setTransactionId(transactionId);
            purchaseTransaction.setTransactionDate(LocalDate.now());
            
            // Process payment using PurchaseTransaction
            if (purchaseTransaction.processPayment(cash)) {
                statusTextField.setText(Transactions.STATUS_PAID);
                BigDecimal change = cash.subtract(transaction.getTotalPrice());
                chageTextField.setText(change.toString());
                
                purchaseTransaction.serializeTransaction();
                
                TransactionLogService.logTransaction(
                transaction.getTransactionId(),
                        "checkout",
                        loggedInUsername,
                        "Transaction paid successfully. Total: " + transaction.getTotalPrice()
                );
                
                JOptionPane.showMessageDialog(this, 
                    "Transaksi berhasil dibayar.\nKembalian: " + change);
                this.dispose();this.dispose();
                new MainUI(loggedInUsername, userRole).setVisible(true);
                JOptionPane.showMessageDialog(this, "Pembayaran gagal.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    public void setTransactionData(Transactions transaction) {
        setTransactionId(transaction.getTransactionId());
        setDate(transaction.getTransactionDate().toString());
        setTotal(transaction.getTotalPrice());
        setItemsFromList(transaction.getItems());
        statusTextField.setText(transaction.getStatus());
    }
    
    public void setTransactionId(int transactionId) {
        transactionIdTextField.setText(String.valueOf(transactionId));
    }
    
    public void setTransactionId(String transactionId) {
        transactionIdTextField.setText(transactionId);
    }

    public void setTotal(BigDecimal total) {
        totalTextField.setText(total.toString());
    }

    public void setDate(String date) {
        dateTextField.setText(date);
    }
    
    public void setItemsFromList(List<TransactionItem> items) {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Product ID", "Quantity", "Unit Price", "Subtotal"}
        );
        
        daftarBelanjaTable.setModel(model);
        model.setRowCount(0);

        StringBuilder itemsText = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;

        for (TransactionItem item : items) {
            model.addRow(new Object[]{
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubTotal()
            });

            itemsText.append(item.toString()).append("\n");
            total = total.add(item.getSubTotal());
        }

        itemsTextField.setText(itemsText.toString());
        totalTextField.setText(total.toString());
        statusTextField.setText(Transactions.STATUS_NOT_PAID);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        transactionIdLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        totalLabel = new javax.swing.JLabel();
        cashLabel = new javax.swing.JLabel();
        chageLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        itemsLabel = new javax.swing.JLabel();
        itemsTextField = new javax.swing.JTextField();
        totalTextField = new javax.swing.JTextField();
        dateTextField = new javax.swing.JTextField();
        transactionIdTextField = new javax.swing.JTextField();
        cashTextField = new javax.swing.JTextField();
        chageTextField = new javax.swing.JTextField();
        statusTextField = new javax.swing.JTextField();
        purchaseBtn = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(251, 193, 217));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Purchase Transaction");

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        transactionIdLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        transactionIdLabel.setForeground(new java.awt.Color(250, 193, 217));
        transactionIdLabel.setText("Transaction ID");

        dateLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        dateLabel.setForeground(new java.awt.Color(250, 193, 217));
        dateLabel.setText("Date");

        totalLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalLabel.setForeground(new java.awt.Color(250, 193, 217));
        totalLabel.setText("Total");

        cashLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cashLabel.setForeground(new java.awt.Color(250, 193, 217));
        cashLabel.setText("Cash");

        chageLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        chageLabel.setForeground(new java.awt.Color(250, 193, 217));
        chageLabel.setText("Change");

        statusLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(250, 193, 217));
        statusLabel.setText("Status");

        itemsLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        itemsLabel.setForeground(new java.awt.Color(250, 193, 217));
        itemsLabel.setText("Items");

        cashTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashTextFieldActionPerformed(evt);
            }
        });

        purchaseBtn.setBackground(new java.awt.Color(51, 51, 51));
        purchaseBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        purchaseBtn.setForeground(new java.awt.Color(250, 193, 217));
        purchaseBtn.setText("Purchase");
        purchaseBtn.setToolTipText("");
        purchaseBtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        purchaseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchaseBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cashLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(itemsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(transactionIdLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(transactionIdTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(purchaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(chageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(totalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(itemsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cashTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 76, Short.MAX_VALUE))))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transactionIdLabel)
                    .addComponent(dateLabel)
                    .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(transactionIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(itemsLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(itemsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLabel)
                    .addComponent(totalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cashLabel)
                    .addComponent(cashTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chageLabel)
                    .addComponent(chageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(purchaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(240, 240, 240)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cashTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cashTextFieldActionPerformed

    private void purchaseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purchaseBtnActionPerformed
        String transactionId = transactionIdTextField.getText().trim();

    try {
        int transactionIdInt = Integer.parseInt(transactionId);

        // Validasi keberadaan transaksi di database
        Transactions transaction = TransactionDAO.getTransactionById(transactionIdInt);
        if (transaction == null) {
            JOptionPane.showMessageDialog(this, "Transaksi tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tampilkan data transaksi
        setTransactionData(transaction);

        // Cek status
        if (!transaction.getStatus().equalsIgnoreCase(Transactions.STATUS_NOT_PAID)) {
            JOptionPane.showMessageDialog(this, "Transaksi sudah diproses atau direfund.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Proses pembayaran dilakukan di tombol pembayaran, bukan di sini
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID Transaksi tidak valid. Masukkan angka.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_purchaseBtnActionPerformed

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
            java.util.logging.Logger.getLogger(PurchaseUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PurchaseUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PurchaseUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PurchaseUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                            try {
                new PurchaseUI("admin", "admin").setVisible(true); // test data
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cashLabel;
    private javax.swing.JTextField cashTextField;
    private javax.swing.JLabel chageLabel;
    private javax.swing.JTextField chageTextField;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextField dateTextField;
    private javax.swing.JLabel itemsLabel;
    private javax.swing.JTextField itemsTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JButton purchaseBtn;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JTextField totalTextField;
    private javax.swing.JLabel transactionIdLabel;
    private javax.swing.JTextField transactionIdTextField;
    // End of variables declaration//GEN-END:variables
}

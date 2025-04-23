/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pos.android.based.app.View;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JOptionPane;
import pos.android.based.app.transactions.TransactionDAO;
import pos.android.based.app.transactions.TransactionItem;
import pos.android.based.app.transactions.Transactions;

public class RefundUI extends javax.swing.JFrame {

    private final TransactionDAO transactionDAO;
    
    public RefundUI() {
        initComponents();
        
        transactionDAO = new TransactionDAO();
        
        // Set current date as default refund date
        refundDateTextField.setText(LocalDate.now().toString());
        
        // Action when transaction ID is entered
        transactionIdTextField.addActionListener(e -> loadTransactionDetails());
        
        // Action for refund button
        refundBtn.addActionListener(e -> processRefund());
    }

    private void loadTransactionDetails() {
        try {
            String transactionIdText = transactionIdTextField.getText().trim();
            if (transactionIdText.isEmpty()) {
                return;
            }
            
            int transactionId = Integer.parseInt(transactionIdText);
            Transactions trx = transactionDAO.getTransactionById(transactionId);
            
            if (trx == null) {
                JOptionPane.showMessageDialog(this, "Transaction not found.", "Error", JOptionPane.ERROR_MESSAGE);
                clearFields();
                return;
            }
            
            // Display transaction details
            refundDateTextField.setText(trx.getTransactionDate().toString());
            itemsTextField.setText(formatItems(trx.getItems()));
            totalTextField.setText(trx.getTotalPrice().toString());
            statusTextField.setText(trx.getStatus());
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Transaction ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading transaction: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            clearFields();
        }
    }
    
    private String formatItems(List<TransactionItem> items) {
        StringBuilder sb = new StringBuilder();
        for (TransactionItem item : items) {
            sb.append(String.format("%s (Qty: %d, Price: %s), ", 
                item.getProductId(), 
                item.getQuantity(), 
                item.getSubTotal()));
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "No items";
    }
    
    private void clearFields() {
        dateTextField.setText("");
        itemsTextField.setText("");
        totalTextField.setText("");
        statusTextField.setText("");
    }
    
    private void processRefund() {
        try {
            // Validate transaction ID
            String transactionIdText = transactionIdTextField.getText().trim();
            if (transactionIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a transaction ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int transactionId = Integer.parseInt(transactionIdText);
            Transactions trx = transactionDAO.getTransactionById(transactionId);
            
            if (trx == null) {
                JOptionPane.showMessageDialog(this, "Transaction not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate transaction status
            if (!Transactions.STATUS_PAID.equalsIgnoreCase(trx.getStatus())) {
                JOptionPane.showMessageDialog(this, 
                    "Only paid transactions can be refunded. Current status: " + trx.getStatus(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate refund date
            LocalDate refundDate;
            try {
                refundDate = LocalDate.parse(refundDateTextField.getText());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid refund date format. Please use YYYY-MM-DD.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check refund validity
            if (!transactionDAO.isRefundValid(transactionId, refundDate)) {
                JOptionPane.showMessageDialog(this, 
                    "Refund is not valid. Either the refund date is more than 3 days after purchase or before purchase date.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Confirm refund
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to refund this transaction?\nTransaction ID: " + transactionId,
                "Confirm Refund",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Process refund
            boolean statusUpdated = transactionDAO.updateTransactionStatus(transactionId, Transactions.STATUS_CANCELLED);
            boolean stockUpdated = transactionDAO.updateStockAfterRefund(trx);
            
            if (statusUpdated && stockUpdated) {
                statusTextField.setText(Transactions.STATUS_CANCELLED);
                JOptionPane.showMessageDialog(this, 
                    "Refund processed successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to complete refund. Please check system logs.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid Transaction ID format.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error processing refund: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        transactionIdLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        totalLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        itemsLabel = new javax.swing.JLabel();
        itemsTextField = new javax.swing.JTextField();
        totalTextField = new javax.swing.JTextField();
        refundDateTextField = new javax.swing.JTextField();
        transactionIdTextField = new javax.swing.JTextField();
        statusTextField = new javax.swing.JTextField();
        dateLabel2 = new javax.swing.JLabel();
        dateTextField = new javax.swing.JTextField();
        refundBtn = new javax.swing.JButton();

        dateLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        dateLabel1.setForeground(new java.awt.Color(250, 193, 217));
        dateLabel1.setText("Date");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        transactionIdLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        transactionIdLabel.setForeground(new java.awt.Color(250, 193, 217));
        transactionIdLabel.setText("Transaction ID");

        dateLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        dateLabel.setForeground(new java.awt.Color(250, 193, 217));
        dateLabel.setText("Refund Date");

        totalLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalLabel.setForeground(new java.awt.Color(250, 193, 217));
        totalLabel.setText("Total");

        statusLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(250, 193, 217));
        statusLabel.setText("Status");

        itemsLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        itemsLabel.setForeground(new java.awt.Color(250, 193, 217));
        itemsLabel.setText("Items");

        transactionIdTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionIdTextFieldActionPerformed(evt);
            }
        });

        dateLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        dateLabel2.setForeground(new java.awt.Color(250, 193, 217));
        dateLabel2.setText("Date");

        dateTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(itemsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(transactionIdLabel)
                    .addComponent(dateLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dateTextField)
                        .addGap(245, 245, 245))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(transactionIdTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refundDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(itemsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 76, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transactionIdLabel)
                    .addComponent(dateLabel)
                    .addComponent(refundDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(transactionIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateLabel2)
                    .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(itemsLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(itemsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLabel)
                    .addComponent(totalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(115, 115, 115)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        refundBtn.setText("Refund");
        refundBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refundBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(529, Short.MAX_VALUE)
                .addComponent(refundBtn)
                .addGap(70, 70, 70))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(489, Short.MAX_VALUE)
                .addComponent(refundBtn)
                .addGap(63, 63, 63))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void transactionIdTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionIdTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transactionIdTextFieldActionPerformed

    private void dateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateTextFieldActionPerformed

    private void refundBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refundBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_refundBtnActionPerformed

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
            java.util.logging.Logger.getLogger(RefundUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RefundUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RefundUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RefundUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RefundUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel dateLabel1;
    private javax.swing.JLabel dateLabel2;
    private javax.swing.JTextField dateTextField;
    private javax.swing.JLabel itemsLabel;
    private javax.swing.JTextField itemsTextField;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton refundBtn;
    private javax.swing.JTextField refundDateTextField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JTextField totalTextField;
    private javax.swing.JLabel transactionIdLabel;
    private javax.swing.JTextField transactionIdTextField;
    // End of variables declaration//GEN-END:variables
}

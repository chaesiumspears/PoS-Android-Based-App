package pos.android.based.app.transactions;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pos.android.based.app.DatabaseConnection;

public class TransactionDAO {

    public boolean addTransaction(Transactions transaction) {
    String insertTransactionQuery = "INSERT INTO transactions (transaction_date, total_price, status) VALUES (?, ?, ?) RETURNING transaction_id";
    String insertItemQuery = "INSERT INTO transaction_items (transaction_id, product_id, quantity, subtotal) VALUES (?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement transactionStmt = conn.prepareStatement(insertTransactionQuery);
         PreparedStatement itemStmt = conn.prepareStatement(insertItemQuery)) {

        conn.setAutoCommit(false); // untuk batch execution

        // Insert transaksi utama
        transactionStmt.setDate(1, transaction.getTransactionDate());
        transactionStmt.setBigDecimal(2, transaction.getTotalPrice());
        transactionStmt.setString(3, transaction.getStatus());

        ResultSet rs = transactionStmt.executeQuery();
        int transactionId = -1;
        if (rs.next()) {
            transactionId = rs.getInt(1);
        }

        if (transactionId == -1) {
            conn.rollback();
            System.out.println("Failed to get generated transaction ID.");
            return false;
        }

        // Insert tiap item
        for (TransactionItem item : transaction.getItems()) {
            itemStmt.setInt(1, transactionId);
            itemStmt.setString(2, item.getProductId());
            itemStmt.setInt(3, item.getQuantity());
            itemStmt.setBigDecimal(4, item.getSubTotal());
            itemStmt.addBatch();
        }

        itemStmt.executeBatch(); // jalankan batch insert item
        conn.commit();
        return true;

    } catch (SQLException e) {
        System.out.println("Database error (addTransaction): " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    public static List<Transactions> getAllTransactions() {
        List<Transactions> transactions = new ArrayList<>();
        String queryTransaction = "SELECT * FROM transactions";
        String queryItems = "SELECT * FROM transaction_items WHERE transaction_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement psTransaction = conn.prepareStatement(queryTransaction);
             PreparedStatement psItems = conn.prepareStatement(queryItems)) {

            ResultSet rsTrans = psTransaction.executeQuery();

            while (rsTrans.next()) {
                int transactionId = rsTrans.getInt("transaction_id");
                Date transactionDate = rsTrans.getDate("transaction_date");
                BigDecimal totalPrice = rsTrans.getBigDecimal("total_price");
                String status = rsTrans.getString("status");

                // Fetch item details
                psItems.setInt(1, transactionId);
                ResultSet rsItems = psItems.executeQuery();
                List<TransactionItem> items = new ArrayList<>();

                while (rsItems.next()) {
                    String productId = rsItems.getString("product_id");
                    int quantity = rsItems.getInt("quantity");
                    double subtotal = rsItems.getDouble("subtotal");

                    items.add(new TransactionItem(productId, quantity, subtotal / quantity));
                }

                // Gunakan constructor lengkap
                transactions.add(new Transactions(transactionId, transactionDate, items, totalPrice, status));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching transactions (getAllTransactions): " + e.getMessage());
        }

        return transactions;
    }

    public static Transactions getTransactionById(int transactionId) {
        String queryTransaction = "SELECT * FROM transactions WHERE transaction_id = ?";
        String queryItems = "SELECT * FROM transaction_items WHERE transaction_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement psTransaction = conn.prepareStatement(queryTransaction);
             PreparedStatement psItems = conn.prepareStatement(queryItems)) {

            psTransaction.setInt(1, transactionId);
            ResultSet rsTrans = psTransaction.executeQuery();

            if (rsTrans.next()) {
                Date transactionDate = rsTrans.getDate("transaction_date");
                BigDecimal totalPrice = rsTrans.getBigDecimal("total_price");
                String status = rsTrans.getString("status");

                psItems.setInt(1, transactionId);
                ResultSet rsItems = psItems.executeQuery();

                List<TransactionItem> items = new ArrayList<>();
                while (rsItems.next()) {
                    String productId = rsItems.getString("product_id");
                    int quantity = rsItems.getInt("quantity");
                    double subtotal = rsItems.getDouble("subtotal");

                    items.add(new TransactionItem(productId, quantity, subtotal / quantity));
                }

                return new Transactions(transactionId, transactionDate, items, totalPrice, status);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching transaction by ID (getTransactionById): " + e.getMessage());
        }

        return null;
    }

    public static boolean deleteTransaction(int transactionId) {
        String deleteItems = "DELETE FROM transaction_items WHERE transaction_id = ?";
        String deleteTransaction = "DELETE FROM transactions WHERE transaction_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psDeleteItems = conn.prepareStatement(deleteItems)) {
                psDeleteItems.setInt(1, transactionId);
                psDeleteItems.executeUpdate();
            }

            int rowsDeleted;
            try (PreparedStatement psDeleteTransaction = conn.prepareStatement(deleteTransaction)) {
                psDeleteTransaction.setInt(1, transactionId);
                rowsDeleted = psDeleteTransaction.executeUpdate();
            }

            conn.commit();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting transaction (deleteTransaction): " + e.getMessage());
            return false;
        }
    }

    public static boolean updateTransactionStatus(int transactionId, String status) {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, transactionId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("⚠️ No transaction found with ID: " + transactionId);
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating transaction status: " + e.getMessage());
            return false;
        }
    }
    
public static void saveTransaction(Transactions transaction) {
    new TransactionDAO().addTransaction(transaction); // ✅ simpan ke database
}
    

    public boolean updateStockAfterRefund(Transactions transaction) {
        String query = "UPDATE products SET stock = stock + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false);
            
            for (TransactionItem item : transaction.getItems()) {
                stmt.setInt(1, item.getQuantity());
                stmt.setString(2, item.getProductId());
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            // Verify all updates were successful
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection conn = DatabaseConnection.connect();
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean isRefundValid(int transactionId, LocalDate refundDate) {
        String query = "SELECT transaction_date FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                LocalDate purchaseDate = rs.getDate("transaction_date").toLocalDate();
                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(purchaseDate, refundDate);
                return daysBetween >= 0 && daysBetween <= 3;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int getNewTransactionId() {
    String sql = "SELECT MAX(transaction_id) FROM transactions";
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
    } catch (SQLException e) {
        System.out.println("Gagal mendapatkan ID transaksi berikutnya: " + e.getMessage());
    }
    return 1; // default jika tabel kosong
}

}

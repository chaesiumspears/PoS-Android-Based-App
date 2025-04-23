package pos.android.based.app.transactions;
import pos.android.based.app.product.ProductService;
import java.util.*;
import java.sql.*;
import pos.android.based.app.product.Product;
import java.util.ArrayList;
import pos.android.based.app.DatabaseConnection;

public class TransactionService {
    public List<Product> getAllProductsForTransaction() {
        try {
            // Fetch all products from ProductService
            return ProductService.getAllProducts();
        } catch (Exception e) {
            System.out.println("Error fetching products for transaction: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Method to calculate the total price of a transaction
    public double calculateTotal(List<Product> products, List<Integer> quantities) {
        double total = 0.0;
        
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            total += product.getPrice() * quantity;
        }

        return total;
    }

    // Method to create a new transaction and store it in the database
    public boolean createTransaction(List<Product> products, List<Integer> quantities, double totalAmount) {
        String insertTransactionSQL = "INSERT INTO transactions (total_amount, transaction_date) VALUES (?, ?)";
        String insertTransactionItemSQL = "INSERT INTO transaction_items (transaction_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            
            // Insert new transaction
            PreparedStatement transactionStmt = conn.prepareStatement(insertTransactionSQL, Statement.RETURN_GENERATED_KEYS);
            transactionStmt.setDouble(1, totalAmount);
            transactionStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            transactionStmt.executeUpdate();

            ResultSet generatedKeys = transactionStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                conn.rollback();
                return false;
            }
            int transactionId = generatedKeys.getInt(1); // Get the generated transaction ID

            // Insert transaction items
            try (PreparedStatement itemStmt = conn.prepareStatement(insertTransactionItemSQL)) {
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    int quantity = quantities.get(i);
                    itemStmt.setInt(1, transactionId);
                    itemStmt.setString(2, product.getId());
                    itemStmt.setInt(3, quantity);
                    itemStmt.setDouble(4, product.getPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
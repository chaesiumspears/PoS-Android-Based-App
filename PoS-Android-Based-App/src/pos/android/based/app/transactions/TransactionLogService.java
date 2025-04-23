/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import pos.android.based.app.DatabaseConnection;

public class TransactionLogService {

    public static void logTransaction(int transactionId, String actionType, String username, String details) {
        String sql = "INSERT INTO transaction_logs (transaction_id, action_type, username, log_time, details) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            stmt.setString(2, actionType);
            stmt.setString(3, username);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setString(5, details);

            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Failed to log transaction: " + e.getMessage());
        }
    }
}

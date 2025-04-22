

package pos.android.based.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthLogService {
    // Method to log login attempts
    public static void logLoginAttempt(String username, boolean isSuccess) {
        String sql = "INSERT INTO login_logs (username, status) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, isSuccess ? "success" : "failed");
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Failed to save login attempt: " + e.getMessage());
        }
    }

    // Method to get all auth logs
    public static List<UserAuthLog> getAuthLogs() {
        List<UserAuthLog> logs = new ArrayList<>();
        
        String sql = "SELECT id, username, login_time, status FROM login_logs ORDER BY login_time DESC";
        
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(new UserAuthLog(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getTimestamp("login_time"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
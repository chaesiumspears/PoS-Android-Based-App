/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pos.android.based.app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String HOST = "aws-0-ap-southeast-1.pooler.supabase.com"; 
    private static final String PORT = "6543";       
    private static final String DATABASE = "Point of Sales";
    private static final String USERNAME = "postgres.ilxnjpbevbkpmassrtec";
    private static final String PASSWORD = "poskelompok2";

    public static Connection connect() {
        Connection connection = null;
        try {
            String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
            System.out.println("Koneksi ke database berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }

        return connection;
    }

    public static void main(String[] args) {
        connect();
    }
}


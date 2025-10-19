package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Cấu hình kết nối SQL Server
    private static final String SERVER = "localhost\\\\SQLEXPRESS";
    private static final String PORT = "1433";
    private static final String DATABASE = "ShoeStoreDB";
    private static final String USERNAME = "sa"; // Thay username của bạn
    private static final String PASSWORD = "anhkhoa020305"; // Thay password của bạn
    
    private static final String URL = 
    	    "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=" + DATABASE + ";encrypt=false;trustServerCertificate=true";

    
    private static Connection connection = null;
    
    // Kết nối database
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load SQL Server JDBC Driver
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Kết nối SQL Server thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver SQL Server: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    // Đóng kết nối
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối database!");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test kết nối
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Test kết nối thành công!");
            closeConnection();
        } else {
            System.out.println("Test kết nối thất bại!");
        }
    }
}
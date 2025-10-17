package DAO;

import model.NhaCungCap;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_NhaCungCap {
    
    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NHACUNGCAP";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setStatus(rs.getString("status"));
                list.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll NhaCungCap: " + e.getMessage());
        }
        return list;
    }
 
    public NhaCungCap getById(String id) {
        String sql = "SELECT * FROM NHACUNGCAP WHERE idNCC = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setStatus(rs.getString("status"));
                return ncc;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById NhaCungCap: " + e.getMessage());
        }
        return null;
    }
    
    public boolean insert(NhaCungCap ncc) {
        String sql = "INSERT INTO NHACUNGCAP (idNCC, tenNCC, sdt, email, diaChi, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ncc.getIdNCC());
            ps.setString(2, ncc.getTenNCC());
            ps.setString(3, ncc.getSdt());
            ps.setString(6, ncc.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert NhaCungCap: " + e.getMessage());
            return false;
        }
    }
    public boolean update(NhaCungCap ncc) {
        String sql = "UPDATE NHACUNGCAP SET tenNCC = ?, sdt = ?, email = ?, diaChi = ?, status = ? WHERE idNCC = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getSdt());
            ps.setString(5, ncc.getStatus());
            ps.setString(6, ncc.getIdNCC());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update NhaCungCap: " + e.getMessage());
            return false;
        }
    }
    
    public boolean delete(String id) {
        String sql = "DELETE FROM NHACUNGCAP WHERE idNCC = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete NhaCungCap: " + e.getMessage());
            return false;
        }
    }
    
    public List<NhaCungCap> searchByName(String name) {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NHACUNGCAP WHERE tenNCC LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setStatus(rs.getString("status"));
                list.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName NhaCungCap: " + e.getMessage());
        }
        return list;
    }
 
    public NhaCungCap getBySDT(String sdt) {
        String sql = "SELECT * FROM NHACUNGCAP WHERE sdt = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setStatus(rs.getString("status"));
                return ncc;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getBySDT: " + e.getMessage());
        }
        return null;
    }
}
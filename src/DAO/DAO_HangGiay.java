package DAO;

import model.HangGiay;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_HangGiay {
    
    public List<HangGiay> getAll() {
        List<HangGiay> list = new ArrayList<>();
        String sql = "SELECT * FROM HANGGIAY";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HangGiay hg = new HangGiay();
                hg.setIdHangGiay(rs.getString("idHangGiay"));
                hg.setTenHangGiay(rs.getString("tenHangGiay"));
                hg.setMoTa(rs.getString("moTa"));
                hg.setStatus(rs.getString("status"));
                list.add(hg);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll HangGiay: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy theo ID
    public HangGiay getById(String id) {
        String sql = "SELECT * FROM HANGGIAY WHERE idHangGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	HangGiay hg = new HangGiay();
                hg.setIdHangGiay(rs.getString("idHangGiay"));
                hg.setTenHangGiay(rs.getString("tenHangGiay"));
                hg.setMoTa(rs.getString("moTa"));
                hg.setStatus(rs.getString("status"));
                return hg;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById HangGiay: " + e.getMessage());
        }
        return null;
    }
    
    // Thêm mới
    public boolean insert(HangGiay hg) {
        String sql = "INSERT INTO HANGGIAY (idHangGiay, tenHangGiay, moTa, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hg.getIdHangGiay());
            ps.setString(2, hg.getTenHangGiay());
            ps.setString(3, hg.getMoTa());
            ps.setString(4, hg.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert HangGiay: " + e.getMessage());
            return false;
        }
    }
    
    public boolean update(HangGiay hg) {
        String sql = "UPDATE HANGGIAY SET tenHangGiay = ?, moTa = ?, status = ? WHERE idHangGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hg.getTenHangGiay());
            ps.setString(2, hg.getMoTa());
            ps.setString(3, hg.getStatus());
            ps.setString(4, hg.getIdHangGiay());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update HangGiay: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa
    public boolean delete(String id) {
        String sql = "DELETE FROM HANGGIAY WHERE idHangGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete HangGiay: " + e.getMessage());
            return false;
        }
    }
    
    // Tìm kiếm theo tên
    public List<HangGiay> searchByName(String name) {
        List<HangGiay> list = new ArrayList<>();
        String sql = "SELECT * FROM HANGGIAY WHERE tenHangGiay LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	HangGiay hg = new HangGiay();
                hg.setIdHangGiay(rs.getString("idHangGiay"));
                hg.setTenHangGiay(rs.getString("tenHangGiay"));
                hg.setMoTa(rs.getString("moTa"));
                hg.setStatus(rs.getString("status"));
                list.add(hg);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName HangGiay: " + e.getMessage());
        }
        return list;
    }
}

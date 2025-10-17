package DAO;

import model.LoaiGiay;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_LoaiGiay {
    
    // Lấy tất cả loại giày
    public List<LoaiGiay> getAll() {
        List<LoaiGiay> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAIGIAY";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LoaiGiay lg = new LoaiGiay();
                lg.setIdLoaiGiay(rs.getString("idLoaiGiay"));
                lg.setTenLoaiGiay(rs.getString("tenLoaiGiay"));
                lg.setMoTa(rs.getString("moTa"));
                lg.setStatus(rs.getString("status"));
                list.add(lg);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll LoaiGiay: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy theo ID
    public LoaiGiay getById(String id) {
        String sql = "SELECT * FROM LOAIGIAY WHERE idLoaiGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                LoaiGiay lg = new LoaiGiay();
                lg.setIdLoaiGiay(rs.getString("idLoaiGiay"));
                lg.setTenLoaiGiay(rs.getString("tenLoaiGiay"));
                lg.setMoTa(rs.getString("moTa"));
                lg.setStatus(rs.getString("status"));
                return lg;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById LoaiGiay: " + e.getMessage());
        }
        return null;
    }
    
    // Thêm mới
    public boolean insert(LoaiGiay lg) {
        String sql = "INSERT INTO LOAIGIAY (idLoaiGiay, tenLoaiGiay, moTa, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, lg.getIdLoaiGiay());
            ps.setString(2, lg.getTenLoaiGiay());
            ps.setString(3, lg.getMoTa());
            ps.setString(4, lg.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert LoaiGiay: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật
    public boolean update(LoaiGiay lg) {
        String sql = "UPDATE LOAIGIAY SET tenLoaiGiay = ?, moTa = ?, status = ? WHERE idLoaiGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, lg.getTenLoaiGiay());
            ps.setString(2, lg.getMoTa());
            ps.setString(3, lg.getStatus());
            ps.setString(4, lg.getIdLoaiGiay());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update LoaiGiay: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa
    public boolean delete(String id) {
        String sql = "DELETE FROM LOAIGIAY WHERE idLoaiGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete LoaiGiay: " + e.getMessage());
            return false;
        }
    }
    
    // Tìm kiếm theo tên
    public List<LoaiGiay> searchByName(String name) {
        List<LoaiGiay> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAIGIAY WHERE tenLoaiGiay LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                LoaiGiay lg = new LoaiGiay();
                lg.setIdLoaiGiay(rs.getString("idLoaiGiay"));
                lg.setTenLoaiGiay(rs.getString("tenLoaiGiay"));
                lg.setMoTa(rs.getString("moTa"));
                lg.setStatus(rs.getString("status"));
                list.add(lg);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName LoaiGiay: " + e.getMessage());
        }
        return list;
    }
}
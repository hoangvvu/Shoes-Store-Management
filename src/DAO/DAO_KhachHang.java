package DAO;

import model.KhachHang;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_KhachHang {
    
    // Lấy tất cả khách hàng
    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setSdt(rs.getString("sdt"));
                kh.setDiaChi(rs.getString("diaChi"));
                kh.setTongTien(rs.getFloat("tongTien"));
                kh.setStatus(rs.getString("status"));
                list.add(kh);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll KhachHang: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy theo ID
    public KhachHang getById(String id) {
        String sql = "SELECT * FROM KHACHHANG WHERE idKH = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setSdt(rs.getString("sdt"));
                kh.setDiaChi(rs.getString("diaChi"));
                kh.setTongTien(rs.getFloat("tongTien"));
                kh.setStatus(rs.getString("status"));
                return kh;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById KhachHang: " + e.getMessage());
        }
        return null;
    }
    
    // Thêm mới
    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KHACHHANG (idKH, tenKH, sdt, diaChi, tongTien, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getIdKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getDiaChi());
            ps.setFloat(5, kh.getTongTien());
            ps.setString(6, kh.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert KhachHang: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật
    public boolean update(KhachHang kh) {
        String sql = "UPDATE KHACHHANG SET tenKH = ?, sdt = ?, diaChi = ?, tongTien = ?, status = ? WHERE idKH = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getDiaChi());
            ps.setFloat(4, kh.getTongTien());
            ps.setString(5, kh.getStatus());
            ps.setString(6, kh.getIdKH());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update KhachHang: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa
    public boolean delete(String id) {
        String sql = "DELETE FROM KHACHHANG WHERE idKH = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete KhachHang: " + e.getMessage());
            return false;
        }
    }
    
    // Tìm kiếm theo tên
    public List<KhachHang> searchByName(String name) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG WHERE tenKH LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setSdt(rs.getString("sdt"));
                kh.setDiaChi(rs.getString("diaChi"));
                kh.setTongTien(rs.getFloat("tongTien"));
                kh.setStatus(rs.getString("status"));
                list.add(kh);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName KhachHang: " + e.getMessage());
        }
        return list;
    }
    
    // Tìm kiếm theo số điện thoại
    public KhachHang getBySDT(String sdt) {
        String sql = "SELECT * FROM KHACHHANG WHERE sdt = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setSdt(rs.getString("sdt"));
                kh.setDiaChi(rs.getString("diaChi"));
                kh.setTongTien(rs.getFloat("tongTien"));
                kh.setStatus(rs.getString("status"));
                return kh;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getBySDT: " + e.getMessage());
        }
        return null;
    }
    
    // Cập nhật tổng tiền
    public boolean updateTongTien(String idKH, float tongTien) {
        String sql = "UPDATE KHACHHANG SET tongTien = tongTien + ? WHERE idKH = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setFloat(1, tongTien);
            ps.setString(2, idKH);
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateTongTien: " + e.getMessage());
            return false;
        }
    }
}
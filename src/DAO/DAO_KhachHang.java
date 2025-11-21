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
        // Cập nhật câu SQL để lấy đủ các cột mới
        String sql = "SELECT idKH, tenKH, ngaySinh, gioiTinh, sdt, diaChi, tongTien, status FROM KHACHHANG";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setNgaySinh(rs.getDate("ngaySinh")); // Lấy ngày sinh
                kh.setGioiTinh(rs.getString("gioiTinh")); // Lấy giới tính
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
        String sql = "SELECT idKH, tenKH, ngaySinh, gioiTinh, sdt, diaChi, tongTien, status FROM KHACHHANG WHERE idKH = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setNgaySinh(rs.getDate("ngaySinh"));
                kh.setGioiTinh(rs.getString("gioiTinh"));
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
        // Cập nhật câu SQL để thêm 2 cột mới
        String sql = "INSERT INTO KHACHHANG (idKH, tenKH, ngaySinh, gioiTinh, sdt, diaChi, tongTien, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getIdKH());
            ps.setString(2, kh.getTenKH());
            ps.setDate(3, kh.getNgaySinh()); // Set ngày sinh
            ps.setString(4, kh.getGioiTinh()); // Set giới tính
            ps.setString(5, kh.getSdt());
            ps.setString(6, kh.getDiaChi());
            ps.setFloat(7, kh.getTongTien());
            ps.setString(8, kh.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert KhachHang: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật
    public boolean update(KhachHang kh) {
        // Cập nhật câu SQL để cập nhật 2 cột mới
        String sql = "UPDATE KHACHHANG SET tenKH = ?, ngaySinh = ?, gioiTinh = ?, sdt = ?, diaChi = ?, tongTien = ?, status = ? WHERE idKH = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getTenKH());
            ps.setDate(2, kh.getNgaySinh()); // Set ngày sinh
            ps.setString(3, kh.getGioiTinh()); // Set giới tính
            ps.setString(4, kh.getSdt());
            ps.setString(5, kh.getDiaChi());
            ps.setFloat(6, kh.getTongTien());
            ps.setString(7, kh.getStatus());
            ps.setString(8, kh.getIdKH());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update KhachHang: " + e.getMessage());
            return false;
        }
    }
    
    // Các phương thức delete, updateTongTien, searchByName, getBySDT được giữ nguyên về logic
    // nhưng cần được cập nhật SQL SELECT đầy đủ nếu bạn cần trả về object KhachHang đầy đủ

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
    
    public List<KhachHang> searchByName(String name) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT idKH, tenKH, ngaySinh, gioiTinh, sdt, diaChi, tongTien, status FROM KHACHHANG WHERE tenKH LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setNgaySinh(rs.getDate("ngaySinh"));
                kh.setGioiTinh(rs.getString("gioiTinh"));
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
    
    public KhachHang getBySDT(String sdt) {
        String sql = "SELECT idKH, tenKH, ngaySinh, gioiTinh, sdt, diaChi, tongTien, status FROM KHACHHANG WHERE sdt = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	KhachHang kh = new KhachHang();
                kh.setIdKH(rs.getString("idKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setNgaySinh(rs.getDate("ngaySinh"));
                kh.setGioiTinh(rs.getString("gioiTinh"));
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
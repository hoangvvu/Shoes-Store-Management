package DAO;

import model.ChiTietHoaDon;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_ChiTietHoaDon {
    
    public List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETHOADON";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	ChiTietHoaDon cthd = new ChiTietHoaDon();
                cthd.setIdCTHD(rs.getString("idCTHD"));
                cthd.setIdHD(rs.getString("idHD"));
                cthd.setIdGiay(rs.getString("idGiay"));
                cthd.setSoLuong(rs.getInt("soLuong"));
                cthd.setDonGia(rs.getFloat("donGia"));
                cthd.setThanhTien(rs.getFloat("thanhTien"));
                cthd.setStatus(rs.getString("status"));
                list.add(cthd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll ChiTietHoaDon: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy theo ID
    public ChiTietHoaDon getById(String id) {
        String sql = "SELECT * FROM CHITIETHOADON WHERE idCTHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	ChiTietHoaDon cthd = new ChiTietHoaDon();
                cthd.setIdCTHD(rs.getString("idCTHD"));
                cthd.setIdHD(rs.getString("idHD"));
                cthd.setIdGiay(rs.getString("idGiay"));
                cthd.setSoLuong(rs.getInt("soLuong"));
                cthd.setDonGia(rs.getFloat("donGia"));
                cthd.setThanhTien(rs.getFloat("thanhTien"));
                cthd.setStatus(rs.getString("status"));
                return cthd;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById ChiTietHoaDon: " + e.getMessage());
        }
        return null;
    }
 
    public boolean insert(ChiTietHoaDon cthd) {
        String sql = "INSERT INTO CHITIETHOADON (idCTHD, idHD, idGiay, soLuong, donGia, thanhTien, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cthd.getIdCTHD());
            ps.setString(2, cthd.getIdHD());
            ps.setString(3, cthd.getIdGiay());
            ps.setInt(4, cthd.getSoLuong());
            ps.setFloat(5, cthd.getDonGia());
            ps.setFloat(6, cthd.getThanhTien());
            ps.setString(7, cthd.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert ChiTietHoaDon: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật
    public boolean update(ChiTietHoaDon cthd) {
        String sql = "UPDATE CHITIETHOADON SET idHD = ?, idGiay = ?, soLuong = ?, donGia = ?, " +
                     "thanhTien = ?, status = ? WHERE idCTHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cthd.getIdHD());
            ps.setString(2, cthd.getIdGiay());
            ps.setInt(3, cthd.getSoLuong());
            ps.setFloat(4, cthd.getDonGia());
            ps.setFloat(5, cthd.getThanhTien());
            ps.setString(6, cthd.getStatus());
            ps.setString(7, cthd.getIdCTHD());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update ChiTietHoaDon: " + e.getMessage());
            return false;
        }
    }
    
  
    public boolean delete(String id) {
        String sql = "DELETE FROM CHITIETHOADON WHERE idCTHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete ChiTietHoaDon: " + e.getMessage());
            return false;
        }
    }
 
    public List<ChiTietHoaDon> getByHoaDon(String idHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETHOADON WHERE idHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idHD);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	ChiTietHoaDon cthd = new ChiTietHoaDon();
                cthd.setIdHD(rs.getString("idHD"));
                cthd.setIdGiay(rs.getString("idGiay"));
                cthd.setSoLuong(rs.getInt("soLuong"));
                cthd.setDonGia(rs.getFloat("donGia"));
                cthd.setThanhTien(rs.getFloat("thanhTien"));
                cthd.setStatus(rs.getString("status"));
                list.add(cthd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByHoaDon: " + e.getMessage());
        }
        return list;
    }
 
    public boolean deleteByHoaDon(String idHD) {
        String sql = "DELETE FROM CHITIETHOADON WHERE idHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idHD);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi deleteByHoaDon: " + e.getMessage());
            return false;
        }
    }
    
    public float getTongTienByHoaDon(String idHD) {
        String sql = "SELECT SUM(thanhTien) as total FROM CHITIETHOADON WHERE idHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idHD);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getFloat("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTongTienByHoaDon: " + e.getMessage());
        }
        return 0;
    }
}
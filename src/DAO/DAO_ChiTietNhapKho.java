package DAO;

import model.ChiTietNhapKho;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_ChiTietNhapKho {

    public List<ChiTietNhapKho> getAll() {
        List<ChiTietNhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETNHAPKHO";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	ChiTietNhapKho ctnk = new ChiTietNhapKho();
                ctnk.setIdCTNK(rs.getString("idCTNK"));
                ctnk.setIdNhapKho(rs.getString("tenNhapKho"));
                ctnk.setIdGiay(rs.getString("idGiay"));
                ctnk.setSoLuong(rs.getInt("soLuong"));
                ctnk.setDonGia(rs.getFloat("donGia"));
                ctnk.setThanhTien(rs.getFloat("thanhTien"));
                ctnk.setStatus(rs.getString("status"));
                list.add(ctnk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll ChiTietNhapKho: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy theo ID
    public ChiTietNhapKho getById(String id) {
        String sql = "SELECT * FROM CHITIETNHAPKHO WHERE idCTNK = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	ChiTietNhapKho ctnk = new ChiTietNhapKho();
                ctnk.setIdCTNK(rs.getString("idCTNK"));
                ctnk.setIdNhapKho(rs.getString("tenNhapKho"));
                ctnk.setIdGiay(rs.getString("idGiay"));
                ctnk.setSoLuong(rs.getInt("soLuong"));
                ctnk.setDonGia(rs.getFloat("donGia"));
                ctnk.setThanhTien(rs.getFloat("thanhTien"));
                ctnk.setStatus(rs.getString("status"));
                return ctnk;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById ChiTietNhapKho: " + e.getMessage());
        }
        return null;
    }
    
    // Thêm mới
    public boolean insert(ChiTietNhapKho ctnk) {
        String sql = "INSERT INTO CHITIETNHAPKHO (idCTNK, tenNhapKho, idGiay, soLuong, donGia, thanhTien, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ctnk.getIdCTNK());
            ps.setString(2, ctnk.getIdNhapKho());
            ps.setString(3, ctnk.getIdGiay());
            ps.setInt(4, ctnk.getSoLuong());
            ps.setFloat(5, ctnk.getDonGia());
            ps.setFloat(6, ctnk.getThanhTien());
            ps.setString(7, ctnk.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert ChiTietNhapKho: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật
    public boolean update(ChiTietNhapKho ctnk) {
        String sql = "UPDATE CHITIETNHAPKHO SET tenNhapKho = ?, idGiay = ?, soLuong = ?, donGia = ?, " +
                     "thanhTien = ?, status = ? WHERE idCTNK = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ctnk.getIdNhapKho());
            ps.setString(2, ctnk.getIdGiay());
            ps.setInt(3, ctnk.getSoLuong());
            ps.setFloat(4, ctnk.getDonGia());
            ps.setFloat(5, ctnk.getThanhTien());
            ps.setString(6, ctnk.getStatus());
            ps.setString(7, ctnk.getIdCTNK());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update ChiTietNhapKho: " + e.getMessage());
            return false;
        }
    }
 
    public boolean delete(String id) {
        String sql = "DELETE FROM CHITIETNHAPKHO WHERE idCTNK = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete ChiTietNhapKho: " + e.getMessage());
            return false;
        }
    }
 
    public List<ChiTietNhapKho> getByNhapKho(String tenNhapKho) {
        List<ChiTietNhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETNHAPKHO WHERE tenNhapKho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenNhapKho);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	ChiTietNhapKho ctnk = new ChiTietNhapKho();
                ctnk.setIdCTNK(rs.getString("idCTNK"));
                ctnk.setIdNhapKho(rs.getString("tenNhapKho"));
                ctnk.setIdGiay(rs.getString("idGiay"));
                ctnk.setSoLuong(rs.getInt("soLuong"));
                ctnk.setDonGia(rs.getFloat("donGia"));
                ctnk.setThanhTien(rs.getFloat("thanhTien"));
                ctnk.setStatus(rs.getString("status"));
                list.add(ctnk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByNhapKho: " + e.getMessage());
        }
        return list;
    }
     
    public boolean deleteByNhapKho(String tenNhapKho) {
        String sql = "DELETE FROM CHITIETNHAPKHO WHERE tenNhapKho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenNhapKho);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi deleteByNhapKho: " + e.getMessage());
            return false;
        }
    }
}
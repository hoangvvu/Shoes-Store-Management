package DAO;

import model.NhapKho;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_NhapKho {

    public List<NhapKho> getAll() {
        List<NhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAPKHO";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	NhapKho nk = new NhapKho();
                nk.setIdNhapKho(rs.getString("idNhapKho"));
                nk.setNgayNhap(rs.getDate("ngayNhap"));
                nk.setTongTien(rs.getFloat("tongTien"));
                nk.setIdNCC(rs.getString("idNCC"));
                nk.setIdNV(rs.getString("idNV"));
                nk.setStatus(rs.getString("status"));
                list.add(nk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll NhapKho: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy theo ID
    public NhapKho getById(String id) {
        String sql = "SELECT * FROM NHAPKHO WHERE idNhapKho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	NhapKho nk = new NhapKho();
                nk.setIdNhapKho(rs.getString("idNhapKho"));
                nk.setNgayNhap(rs.getDate("ngayNhap"));
                nk.setTongTien(rs.getFloat("tongTien"));
                nk.setIdNCC(rs.getString("idNCC"));
                nk.setIdNV(rs.getString("idNV"));
                nk.setStatus(rs.getString("status"));
                return nk;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById NhapKho: " + e.getMessage());
        }
        return null;
    }
 
    public boolean insert(NhapKho nk) {
        String sql = "INSERT INTO NHAPKHO (idNhapKho, ngayNhap, tongTien, idNCC, idNV, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nk.getIdNhapKho());
            ps.setDate(2, new java.sql.Date(nk.getNgayNhap().getTime()));
            ps.setFloat(3, nk.getTongTien());
            ps.setString(4, nk.getIdNCC());
            ps.setString(5, nk.getIdNV());
            ps.setString(6, nk.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert NhapKho: " + e.getMessage());
            return false;
        }
    }
 
    public boolean update(NhapKho nk) {
        String sql = "UPDATE NHAPKHO SET ngayNhap = ?, tongTien = ?, idNCC = ?, idNV = ?, status = ? WHERE idNhapKho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(nk.getNgayNhap().getTime()));
            ps.setFloat(2, nk.getTongTien());
            ps.setString(3, nk.getIdNCC());
            ps.setString(4, nk.getIdNV());
            ps.setString(5, nk.getStatus());
            ps.setString(6, nk.getIdNhapKho());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update NhapKho: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa
    public boolean delete(String id) {
        String sql = "DELETE FROM NHAPKHO WHERE idNhapKho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete NhapKho: " + e.getMessage());
            return false;
        }
    }
 
    public List<NhapKho> getByNCC(String idNCC) {
        List<NhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAPKHO WHERE idNCC = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idNCC);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	NhapKho nk = new NhapKho();
                nk.setIdNhapKho(rs.getString("idNhapKho"));
                nk.setNgayNhap(rs.getDate("ngayNhap"));
                nk.setTongTien(rs.getFloat("tongTien"));
                nk.setIdNCC(rs.getString("idNCC"));
                nk.setIdNV(rs.getString("idNV"));
                nk.setStatus(rs.getString("status"));
                list.add(nk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByNCC: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy phiếu nhập theo ngày
    public List<NhapKho> getByDate(Date fromDate, Date toDate) {
        List<NhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAPKHO WHERE ngayNhap BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ps.setDate(2, new java.sql.Date(toDate.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	NhapKho nk = new NhapKho();
                nk.setIdNhapKho(rs.getString("idNhapKho"));
                nk.setNgayNhap(rs.getDate("ngayNhap"));
                nk.setTongTien(rs.getFloat("tongTien"));
                nk.setIdNCC(rs.getString("idNCC"));
                nk.setIdNV(rs.getString("idNV"));
                nk.setStatus(rs.getString("status"));
                list.add(nk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByDate: " + e.getMessage());
        }
        return list;
    }
}
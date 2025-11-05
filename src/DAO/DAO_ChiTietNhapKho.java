package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ChiTietNhapKho;

public class DAO_ChiTietNhapKho {
    
    // INSERT
    public boolean insert(ChiTietNhapKho ct) {
        String sql = "INSERT INTO CHITIETNHAPKHO (idCTNK, idNhapKho, idGiay, soLuong, giaNhap, thanhTien, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ct.getIdCTNK());
            ps.setString(2, ct.getIdNhapKho());
            ps.setString(3, ct.getIdGiay());
            ps.setInt(4, ct.getSoLuong());
            ps.setFloat(5, ct.getGiaNhap());
            ps.setFloat(6, ct.getThanhTien());
            ps.setString(7, ct.getStatus());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi insert CTNK: " + e.getMessage());
            return false;
        }
    }
    
    // GET BY NHAP KHO
    public List<ChiTietNhapKho> getByNhapKho(String idNhapKho) {
        List<ChiTietNhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETNHAPKHO WHERE idNhapKho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idNhapKho);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ChiTietNhapKho ct = new ChiTietNhapKho();
                ct.setIdCTNK(rs.getString("idCTNK"));
                ct.setIdNhapKho(rs.getString("idNhapKho"));
                ct.setIdGiay(rs.getString("idGiay"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setGiaNhap(rs.getFloat("giaNhap"));  // ← Đúng tên cột
                ct.setThanhTien(rs.getFloat("thanhTien"));
                ct.setStatus(rs.getString("status"));
                list.add(ct);
            }
            
            System.out.println("✓ Đã load " + list.size() + " chi tiết cho phiếu " + idNhapKho);
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi getByNhapKho: " + e.getMessage());
        }
        
        return list;
    }
    
    // GET ALL
    public List<ChiTietNhapKho> getAll() {
        List<ChiTietNhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETNHAPKHO";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ChiTietNhapKho ct = new ChiTietNhapKho();
                ct.setIdCTNK(rs.getString("idCTNK"));
                ct.setIdNhapKho(rs.getString("idNhapKho"));
                ct.setIdGiay(rs.getString("idGiay"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setGiaNhap(rs.getFloat("giaNhap"));
                ct.setThanhTien(rs.getFloat("thanhTien"));
                ct.setStatus(rs.getString("status"));
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    // GET BY ID
    public ChiTietNhapKho getById(String id) {
        String sql = "SELECT * FROM CHITIETNHAPKHO WHERE idCTNK = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ChiTietNhapKho ct = new ChiTietNhapKho();
                ct.setIdCTNK(rs.getString("idCTNK"));
                ct.setIdNhapKho(rs.getString("idNhapKho"));
                ct.setIdGiay(rs.getString("idGiay"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setGiaNhap(rs.getFloat("giaNhap"));
                ct.setThanhTien(rs.getFloat("thanhTien"));
                ct.setStatus(rs.getString("status"));
                return ct;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // UPDATE
    public boolean update(ChiTietNhapKho ct) {
        String sql = "UPDATE CHITIETNHAPKHO SET idNhapKho=?, idGiay=?, soLuong=?, " +
                     "giaNhap=?, thanhTien=?, status=? WHERE idCTNK=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ct.getIdNhapKho());
            ps.setString(2, ct.getIdGiay());
            ps.setInt(3, ct.getSoLuong());
            ps.setFloat(4, ct.getGiaNhap());
            ps.setFloat(5, ct.getThanhTien());
            ps.setString(6, ct.getStatus());
            ps.setString(7, ct.getIdCTNK());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // DELETE
    public boolean delete(String id) {
        String sql = "DELETE FROM CHITIETNHAPKHO WHERE idCTNK = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteByNhapKhoId(String idNhapKho) {
        String sql = "DELETE FROM CHITIETNHAPKHO WHERE idNhapKho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idNhapKho);
            int rows = ps.executeUpdate();
            
            // Trả về true nếu có ít nhất 1 dòng bị ảnh hưởng (bị xóa)
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi deleteByNhapKhoId: " + e.getMessage());
            return false;
        }
    }
}
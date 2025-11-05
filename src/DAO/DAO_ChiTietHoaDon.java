package DAO;

import model.ChiTietHoaDon;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;


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
    
    public boolean deleteByHoaDonId(String idHD) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE IdHD = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idHD);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi xóa chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
    
    public List<ChiTietHoaDon> getByHoaDon(String idHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETHOADON WHERE idHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idHD);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setIdCTHD(rs.getString("idCTHD"));
                ct.setIdHD(rs.getString("idHD"));
                ct.setIdGiay(rs.getString("idGiay"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getFloat("donGia"));
                ct.setThanhTien(rs.getFloat("thanhTien"));
                ct.setStatus(rs.getString("status"));
                list.add(ct);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByHoaDon: " + e.getMessage());
        }
        return list;
    }

    // Thống kê sản phẩm bán chạy
    public Map<String, Integer> getSanPhamBanChay(Date fromDate, Date toDate) {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT c.idGiay, SUM(c.soLuong) AS tongSL " +
                     "FROM CHITIETHOADON c " +
                     "JOIN HOADON h ON c.idHD = h.idHD " +
                     "WHERE h.ngayLap BETWEEN ? AND ? " +
                     "GROUP BY c.idGiay " +
                     "ORDER BY tongSL DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ps.setDate(2, new java.sql.Date(toDate.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                result.put(rs.getString("idGiay"), rs.getInt("tongSL"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getSanPhamBanChay: " + e.getMessage());
        }
        return result;
    }
}
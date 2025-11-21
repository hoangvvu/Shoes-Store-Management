package DAO;

import model.HoaDon;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DAO_HoaDon {
    
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	HoaDon hd = new HoaDon();
                hd.setIdHD(rs.getString("idHD"));
                hd.setIdNV(rs.getString("idNV"));
                hd.setIdKH(rs.getString("idKH"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setTongTien(rs.getFloat("tongTien"));
                hd.setStatus(rs.getString("status"));
                list.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll HoaDon: " + e.getMessage());
        }
        return list;
    }
    
    public HoaDon getById(String id) {
        String sql = "SELECT * FROM HOADON WHERE idHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	HoaDon hd = new HoaDon();
                hd.setIdHD(rs.getString("idHD"));
                hd.setIdNV(rs.getString("idNV"));
                hd.setIdKH(rs.getString("idKH"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setTongTien(rs.getFloat("tongTien"));
                hd.setStatus(rs.getString("status"));
                return hd;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById HoaDon: " + e.getMessage());
        }
        return null;
    }
    
    // Thêm mới
    public boolean insert(HoaDon hd) {
        String sql = "INSERT INTO HOADON (idHD, idNV, idKH, ngayLap, tongTien, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hd.getIdHD());
            ps.setString(2, hd.getIdNV());
            ps.setString(3, hd.getIdKH());
            ps.setDate(4, new java.sql.Date(hd.getNgayLap().getTime()));
            ps.setFloat(5, hd.getTongTien());
            ps.setString(6, hd.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert HoaDon: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật
    public boolean update(HoaDon hd) {
        String sql = "UPDATE HOADON SET idNV = ?, idKH = ?, ngayLap = ?, tongTien = ?, status = ? WHERE idHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hd.getIdNV());
            ps.setString(2, hd.getIdKH());
            ps.setDate(3, new java.sql.Date(hd.getNgayLap().getTime()));
            ps.setFloat(4, hd.getTongTien());
            ps.setString(5, hd.getStatus());
            ps.setString(6, hd.getIdHD());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update HoaDon: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa
    public boolean delete(String id) {
        String sql = "DELETE FROM HOADON WHERE idHD = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete HoaDon: " + e.getMessage());
            return false;
        }
    }
    
    // Lấy hóa đơn theo nhân viên
    public List<HoaDon> getByNhanVien(String idNV) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE idNV = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idNV);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	HoaDon hd = new HoaDon();
                hd.setIdHD(rs.getString("idHD"));
                hd.setIdNV(rs.getString("idNV"));
                hd.setIdKH(rs.getString("idKH"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setTongTien(rs.getFloat("tongTien"));
                hd.setStatus(rs.getString("status"));
                list.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByNhanVien: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy hóa đơn theo khách hàng
    public List<HoaDon> getByKhachHang(String idKH) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE idKH = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idKH);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	HoaDon hd = new HoaDon();
                hd.setIdHD(rs.getString("idHD"));
                hd.setIdNV(rs.getString("idNV"));
                hd.setIdKH(rs.getString("idKH"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setTongTien(rs.getFloat("tongTien"));
                hd.setStatus(rs.getString("status"));
                list.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByKhachHang: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy hóa đơn theo ngày
    public List<HoaDon> getByDate(Date fromDate, Date toDate) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE ngayLap BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ps.setDate(2, new java.sql.Date(toDate.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	HoaDon hd = new HoaDon();
                hd.setIdHD(rs.getString("idHD"));
                hd.setIdNV(rs.getString("idNV"));
                hd.setIdKH(rs.getString("idKH"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setTongTien(rs.getFloat("tongTien"));
                hd.setStatus(rs.getString("status"));
                list.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByDate: " + e.getMessage());
        }
        return list;
    }
    
    public float getTongDoanhThu() {
        String sql = "SELECT SUM(tongTien) as total FROM HOADON WHERE status = 'active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getFloat("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTongDoanhThu: " + e.getMessage());
        }
        return 0;
    }
    
    public List<HoaDon> getByDateRange(Date fromDate, Date toDate) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE ngayLap BETWEEN ? AND ? ORDER BY ngayLap DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ps.setDate(2, new java.sql.Date(toDate.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setIdHD(rs.getString("idHD"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setTongTien(rs.getFloat("tongTien"));
                hd.setIdKH(rs.getString("idKH"));
                hd.setIdNV(rs.getString("idNV"));
                hd.setStatus(rs.getString("status"));
                list.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByDateRange HoaDon: " + e.getMessage());
        }
        return list;
    }

    // Thống kê doanh thu theo ngày
    public Map<String, Float> getDoanhThuTheoNgay(Date fromDate, Date toDate) {
        Map<String, Float> result = new TreeMap<>();
        String sql = "SELECT CONVERT(VARCHAR(10), ngayLap, 103) AS ngay, SUM(tongTien) AS tongDoanhThu " +
                     "FROM HOADON WHERE ngayLap BETWEEN ? AND ? " +
                     "GROUP BY CONVERT(VARCHAR(10), ngayLap, 103) " +
                     "ORDER BY CONVERT(VARCHAR(10), ngayLap, 103)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ps.setDate(2, new java.sql.Date(toDate.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                result.put(rs.getString("ngay"), rs.getFloat("tongDoanhThu"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getDoanhThuTheoNgay: " + e.getMessage());
        }
        return result;
    }

    // Thống kê doanh thu theo tháng
    public Map<String, Float> getDoanhThuTheoThang() {
        Map<String, Float> result = new TreeMap<>();
        String sql = "SELECT FORMAT(ngayLap, 'MM/yyyy') AS thang, SUM(tongTien) AS tongDoanhThu " +
                     "FROM HOADON " +
                     "GROUP BY FORMAT(ngayLap, 'MM/yyyy') " +
                     "ORDER BY FORMAT(ngayLap, 'MM/yyyy')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                result.put(rs.getString("thang"), rs.getFloat("tongDoanhThu"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getDoanhThuTheoThang: " + e.getMessage());
        }
        return result;
    }
   
}
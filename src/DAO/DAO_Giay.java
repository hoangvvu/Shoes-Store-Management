package DAO;

import model.Giay;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_Giay {
    
    public List<Giay> getAll() {
        List<Giay> list = new ArrayList<>();
        String sql = "SELECT * FROM GIAY";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Giay g = new Giay();
                g.setIdGiay(rs.getString("idGiay"));
                g.setTenGiay(rs.getString("tenGiay"));
                g.setSize(rs.getFloat("size"));
                g.setSoLuong(rs.getInt("soLuong"));
                g.setGiaBan(rs.getFloat("giaBan"));
                g.setMoTa(rs.getString("moTa"));
                g.setHinhAnh(rs.getString("hinhAnh"));
                g.setIdLoaiGiay(rs.getString("idLoaiGiay"));
                g.setIdHangGiay(rs.getString("idHangGiay"));
                g.setStatus(rs.getString("status"));
                list.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll Giay: " + e.getMessage());
        }
        return list;
    }
    
    // Lấy theo ID
    public Giay getById(String id) {
        String sql = "SELECT * FROM GIAY WHERE idGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Giay g = new Giay();
                g.setIdGiay(rs.getString("idGiay"));
                g.setTenGiay(rs.getString("tenGiay"));
                g.setSize(rs.getFloat("size"));
                g.setSoLuong(rs.getInt("soLuong"));
                g.setGiaBan(rs.getFloat("giaBan"));
                g.setMoTa(rs.getString("moTa"));
                g.setHinhAnh(rs.getString("hinhAnh"));
                g.setIdLoaiGiay(rs.getString("idLoaiGiay"));
                g.setIdHangGiay(rs.getString("idHangGiay"));
                g.setStatus(rs.getString("status"));
                return g;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById Giay: " + e.getMessage());
        }
        return null;
    }
    
    // Thêm mới
    public boolean insert(Giay g) {
        String sql = "INSERT INTO GIAY (idGiay, tenGiay, size, soLuong, giaBan, moTa, hinhAnh, idLoaiGiay, idHangGiay, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, g.getIdGiay());
            ps.setString(2, g.getTenGiay());
            ps.setFloat(3, g.getSize());
            ps.setInt(4, g.getSoLuong());
            ps.setFloat(5, g.getGiaBan());
            ps.setString(6, g.getMoTa());
            ps.setString(7, g.getHinhAnh());
            ps.setString(8, g.getIdLoaiGiay());
            ps.setString(9, g.getIdHangGiay());
            ps.setString(10, g.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert Giay: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật
    public boolean update(Giay g) {
        String sql = "UPDATE GIAY SET tenGiay = ?, size = ?, soLuong = ?, giaBan = ?, moTa = ?, " +
                     "hinhAnh = ?, idLoaiGiay = ?, idHangGiay = ?, status = ? WHERE idGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, g.getTenGiay());
            ps.setFloat(2, g.getSize());
            ps.setInt(3, g.getSoLuong());
            ps.setFloat(4, g.getGiaBan());
            ps.setString(5, g.getMoTa());
            ps.setString(6, g.getHinhAnh());
            ps.setString(7, g.getIdLoaiGiay());
            ps.setString(8, g.getIdHangGiay());
            ps.setString(9, g.getStatus());
            ps.setString(10, g.getIdGiay());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update Giay: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa
    public boolean delete(String id) {
        String sql = "DELETE FROM GIAY WHERE idGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete Giay: " + e.getMessage());
            return false;
        }
    }
    
    // Tìm kiếm theo tên
    public List<Giay> searchByName(String name) {
        List<Giay> list = new ArrayList<>();
        String sql = "SELECT * FROM GIAY WHERE tenGiay LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Giay g = new Giay();
                g.setIdGiay(rs.getString("idGiay"));
                g.setTenGiay(rs.getString("tenGiay"));
                g.setSize(rs.getFloat("size"));
                g.setSoLuong(rs.getInt("soLuong"));
                g.setGiaBan(rs.getFloat("giaBan"));
                g.setMoTa(rs.getString("moTa"));
                g.setHinhAnh(rs.getString("hinhAnh"));
                g.setIdLoaiGiay(rs.getString("idLoaiGiay"));
                g.setIdHangGiay(rs.getString("idHangGiay"));
                g.setStatus(rs.getString("status"));
                list.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName Giay: " + e.getMessage());
        }
        return list;
    }
    
    public List<Giay> getByLoaiGiay(String idLoaiGiay) {
        List<Giay> list = new ArrayList<>();
        String sql = "SELECT * FROM GIAY WHERE idLoaiGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idLoaiGiay);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Giay g = new Giay();
                g.setIdGiay(rs.getString("idGiay"));
                g.setTenGiay(rs.getString("tenGiay"));
                g.setSize(rs.getFloat("size"));
                g.setSoLuong(rs.getInt("soLuong"));
                g.setGiaBan(rs.getFloat("giaBan"));
                g.setMoTa(rs.getString("moTa"));
                g.setHinhAnh(rs.getString("hinhAnh"));
                g.setIdLoaiGiay(rs.getString("idLoaiGiay"));
                g.setIdHangGiay(rs.getString("idHangGiay"));
                g.setStatus(rs.getString("status"));
                list.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByLoaiGiay: " + e.getMessage());
        }
        return list;
    }
    
    // Cập nhật số lượng
    public boolean updateSoLuong(String idGiay, int soLuong) {
        String sql = "UPDATE GIAY SET soLuong = ? WHERE idGiay = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, soLuong);
            ps.setString(2, idGiay);
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateSoLuong: " + e.getMessage());
            return false;
        }
    }
    
    
}
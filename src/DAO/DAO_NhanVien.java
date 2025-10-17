package DAO;

import model.NhanVien;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_NhanVien {

    // Lấy tất cả nhân viên
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NHANVIEN";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setEmail(rs.getString("email"));
                nv.setPhanQuyen(rs.getString("phanQuyen"));
                nv.setStatus(rs.getString("status"));
                list.add(nv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll NhanVien: " + e.getMessage());
        }
        return list;
    }

    // Lấy nhân viên theo ID
    public NhanVien getById(String idNV) {
        String sql = "SELECT * FROM NHANVIEN WHERE idNV = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idNV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setEmail(rs.getString("email"));
                nv.setPhanQuyen(rs.getString("phanQuyen"));
                nv.setStatus(rs.getString("status"));
                return nv;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById NhanVien: " + e.getMessage());
        }
        return null;
    }

    // Thêm mới nhân viên
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NHANVIEN (idNV, tenNV, username, password, email, phanQuyen, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?,)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getIdNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getUsername());
            ps.setString(4, nv.getPassword());
            ps.setString(5, nv.getEmail());
            ps.setString(6, nv.getPhanQuyen());
            ps.setString(7, nv.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert NhanVien: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật nhân viên
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NHANVIEN SET tenNV = ?, username = ?, password = ?, email = ?, phanQuyen = ?, status = ?, WHERE idNV = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getUsername());
            ps.setString(3, nv.getPassword());
            ps.setString(4, nv.getEmail());
            ps.setString(5, nv.getPhanQuyen());
            ps.setString(6, nv.getStatus());
            ps.setString(9, nv.getIdNV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update NhanVien: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String idNV) {
        String sql = "DELETE FROM NHANVIEN WHERE idNV = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete NhanVien: " + e.getMessage());
            return false;
        }
    }

    public NhanVien login(String username, String password) {
        String sql = "SELECT * FROM NHANVIEN WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setEmail(rs.getString("email"));
                nv.setPhanQuyen(rs.getString("phanQuyen"));
                nv.setStatus(rs.getString("status"));
                return nv;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi login NhanVien: " + e.getMessage());
        }
        return null;
    }

    public List<NhanVien> searchByName(String tenNV) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NHANVIEN WHERE tenNV LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tenNV + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setEmail(rs.getString("email"));
                nv.setPhanQuyen(rs.getString("phanQuyen"));
                nv.setStatus(rs.getString("status"));
                list.add(nv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName NhanVien: " + e.getMessage());
        }
        return list;
    }
}

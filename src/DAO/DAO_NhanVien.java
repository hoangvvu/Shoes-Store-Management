package DAO;

import model.NhanVien;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_NhanVien {
    
    // Danh sách đầy đủ các cột trong bảng NHANVIEN (đã cập nhật)
    private static final String FULL_COLUMNS = "idNV, tenNV, ngaySinh, gioiTinh, sdt, diaChi, ngayVaoLam, username, password, idPQ, status";

    // Lấy tất cả nhân viên
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT " + FULL_COLUMNS + " FROM NHANVIEN";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setNgaySinh(rs.getDate("ngaySinh")); // Lấy ngày sinh
                nv.setGioiTinh(rs.getString("gioiTinh")); // Lấy giới tính
                nv.setSdt(rs.getString("sdt")); // Lấy sdt
                nv.setDiaChi(rs.getString("diaChi")); // Lấy địa chỉ
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam")); // Lấy ngày vào làm
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setIdPQ(rs.getString("idPQ")); // Đổi từ phanQuyen thành idPQ
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
        String sql = "SELECT " + FULL_COLUMNS + " FROM NHANVIEN WHERE idNV = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idNV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setNgaySinh(rs.getDate("ngaySinh"));
                nv.setGioiTinh(rs.getString("gioiTinh"));
                nv.setSdt(rs.getString("sdt"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setIdPQ(rs.getString("idPQ"));
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
        // Cập nhật câu SQL để thêm 5 cột mới và sửa idPQ
        String sql = "INSERT INTO NHANVIEN (" + FULL_COLUMNS + ") "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // 11 tham số

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getIdNV());
            ps.setString(2, nv.getTenNV());
            ps.setDate(3, nv.getNgaySinh()); // Set ngày sinh
            ps.setString(4, nv.getGioiTinh()); // Set giới tính
            ps.setString(5, nv.getSdt()); // Set sdt
            ps.setString(6, nv.getDiaChi()); // Set địa chỉ
            ps.setDate(7, nv.getNgayVaoLam()); // Set ngày vào làm
            ps.setString(8, nv.getUsername());
            ps.setString(9, nv.getPassword());
            ps.setString(10, nv.getIdPQ()); // Set idPQ
            ps.setString(11, nv.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert NhanVien: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật nhân viên
    public boolean update(NhanVien nv) {
        // Cập nhật câu SQL để cập nhật 5 cột mới và sửa idPQ
        String sql = "UPDATE NHANVIEN SET tenNV = ?, ngaySinh = ?, gioiTinh = ?, sdt = ?, diaChi = ?, ngayVaoLam = ?, username = ?, password = ?, idPQ = ?, status = ? WHERE idNV = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getTenNV());
            ps.setDate(2, nv.getNgaySinh()); // Set ngày sinh
            ps.setString(3, nv.getGioiTinh()); // Set giới tính
            ps.setString(4, nv.getSdt()); // Set sdt
            ps.setString(5, nv.getDiaChi()); // Set địa chỉ
            ps.setDate(6, nv.getNgayVaoLam()); // Set ngày vào làm
            ps.setString(7, nv.getUsername());
            ps.setString(8, nv.getPassword());
            ps.setString(9, nv.getIdPQ()); // Set idPQ
            ps.setString(10, nv.getStatus());
            ps.setString(11, nv.getIdNV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update NhanVien: " + e.getMessage());
            return false;
        }
    }
    
    // Các phương thức login, searchByName, isUsernameExist được cập nhật SQL SELECT đầy đủ

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
        String sql = "SELECT " + FULL_COLUMNS + " FROM NHANVIEN WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setNgaySinh(rs.getDate("ngaySinh"));
                nv.setGioiTinh(rs.getString("gioiTinh"));
                nv.setSdt(rs.getString("sdt"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setIdPQ(rs.getString("idPQ"));
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
        String sql = "SELECT " + FULL_COLUMNS + " FROM NHANVIEN WHERE tenNV LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tenNV + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	NhanVien nv = new NhanVien();
                nv.setIdNV(rs.getString("idNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setNgaySinh(rs.getDate("ngaySinh"));
                nv.setGioiTinh(rs.getString("gioiTinh"));
                nv.setSdt(rs.getString("sdt"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setIdPQ(rs.getString("idPQ"));
                nv.setStatus(rs.getString("status"));
                list.add(nv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName NhanVien: " + e.getMessage());
        }
        return list;
    }
    
    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM NHANVIEN WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi isUsernameExist NhanVien: " + e.getMessage());
        }
        return false;
    }
}
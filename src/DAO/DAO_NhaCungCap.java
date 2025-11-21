package DAO;

import model.NhaCungCap;
import DAO.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_NhaCungCap {
    
    /**
     * ĐÃ SỬA: Đổi tên thành getAllActive và thêm điều kiện WHERE
     * Dùng để tải lên JComboBox
     */
    public List<NhaCungCap> getAllActive() {
        List<NhaCungCap> list = new ArrayList<>();
        // Chỉ lấy các NCC đang "Hoạt động"
        String sql = "SELECT * FROM NHACUNGCAP WHERE status = N'Hoạt động'"; 
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setEmail(rs.getString("email")); // Thêm email
                ncc.setDiaChi(rs.getString("diaChi")); // Thêm diaChi
                ncc.setStatus(rs.getString("status"));
                list.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllActive NhaCungCap: " + e.getMessage());
        }
        return list;
    }
    
    // Giữ lại hàm getAll() cũ nếu bạn vẫn dùng ở đâu đó
    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NHACUNGCAP";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setEmail(rs.getString("email"));
                ncc.setDiaChi(rs.getString("diaChi"));
                ncc.setStatus(rs.getString("status"));
                list.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll NhaCungCap: " + e.getMessage());
        }
        return list;
    }
    
    // ... (Các hàm getById, getByIdOrName, searchByName, getBySDT giữ nguyên như file của bạn) ...
    public NhaCungCap getById(String id) {
        String sql = "SELECT * FROM NHACUNGCAP WHERE idNCC = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setEmail(rs.getString("email"));
                ncc.setDiaChi(rs.getString("diaChi"));
                ncc.setStatus(rs.getString("status"));
                return ncc;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById NhaCungCap: " + e.getMessage());
        }
        return null;
    }

    /**
     * ĐÃ SỬA: Sửa lại hàm insert cho đúng với model và CSDL
     */
    public boolean insert(NhaCungCap ncc) {
        String sql = "INSERT INTO NHACUNGCAP (idNCC, tenNCC, sdt, email, diaChi, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ncc.getIdNCC());
            ps.setString(2, ncc.getTenNCC());
            ps.setString(3, ncc.getSdt());
            ps.setString(4, ncc.getEmail());   // Thêm tham số
            ps.setString(5, ncc.getDiaChi());  // Thêm tham số
            ps.setString(6, ncc.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert NhaCungCap: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * ĐÃ SỬA: Sửa lại hàm update
     */
    public boolean update(NhaCungCap ncc) {
        String sql = "UPDATE NHACUNGCAP SET tenNCC = ?, sdt = ?, email = ?, diaChi = ?, status = ? WHERE idNCC = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getSdt());
            ps.setString(3, ncc.getEmail());   // Thêm tham số
            ps.setString(4, ncc.getDiaChi());  // Thêm tham số
            ps.setString(5, ncc.getStatus());
            ps.setString(6, ncc.getIdNCC());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update NhaCungCap: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * MỚI: Thêm hàm tự động tạo ID
     */
    public String generateNextId() {
        String sql = "SELECT MAX(idNCC) FROM NHACUNGCAP";
        int maxId = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null && lastId.startsWith("NCC")) {
                    try {
                        maxId = Integer.parseInt(lastId.substring(3));
                    } catch (NumberFormatException e) {
                        maxId = 0; // Bỏ qua nếu có lỗi
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi generateNextId NCC: " + e.getMessage());
        }
        
        // Trả về ID tiếp theo, ví dụ: NCC001 -> NCC002
        return "NCC" + String.format("%03d", maxId + 1);
    }
    
    // ... (Các hàm còn lại của bạn: delete, searchByName, getBySDT, getByIdOrName) ...
    
    public boolean delete(String id) {
        String sql = "DELETE FROM NHACUNGCAP WHERE idNCC = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete NhaCungCap: " + e.getMessage());
            return false;
        }
    }
    
    public List<NhaCungCap> searchByName(String name) {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NHACUNGCAP WHERE tenNCC LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setEmail(rs.getString("email"));
                ncc.setDiaChi(rs.getString("diaChi"));
                ncc.setStatus(rs.getString("status"));
                list.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName NhaCungCap: " + e.getMessage());
        }
        return list;
    }
 
    public NhaCungCap getBySDT(String sdt) {
        String sql = "SELECT * FROM NHACUNGCAP WHERE sdt = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	NhaCungCap ncc = new NhaCungCap();
                ncc.setIdNCC(rs.getString("idNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setEmail(rs.getString("email"));
                ncc.setDiaChi(rs.getString("diaChi"));
                ncc.setStatus(rs.getString("status"));
                return ncc;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getBySDT: " + e.getMessage());
        }
        return null;
    }
    
    public NhaCungCap getByIdOrName(String query) {
        NhaCungCap ncc = this.getById(query);
        if (ncc != null) {
            return ncc; 
        }
        List<NhaCungCap> list = this.searchByName(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0); 
        }
        return null;
    }
}
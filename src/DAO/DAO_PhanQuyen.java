package DAO;

import model.ChiTietPhanQuyen;
import model.PhanQuyen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAO_PhanQuyen {

    // ✅ Method getAll() - GUI gọi method này
    public List<PhanQuyen> getAll() {
        List<PhanQuyen> list = new ArrayList<>();
        String sql = "SELECT idPQ, tenQuyen, moTa, status FROM PHANQUYEN WHERE status = N'Hoạt động'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                PhanQuyen pq = new PhanQuyen(
                    rs.getString("idPQ"),
                    rs.getString("tenQuyen"),
                    rs.getString("moTa"),
                    rs.getString("status")
                );
                list.add(pq);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll PhanQuyen: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Method getAllPhanQuyen() - Giữ lại để tương thích với code cũ (nếu có)
    public List<PhanQuyen> getAllPhanQuyen() {
        return getAll(); // Gọi method getAll()
    }

    // ✅ Method insert() - GUI gọi method này
    public boolean insert(PhanQuyen pq) {
        String sql = "INSERT INTO PHANQUYEN (idPQ, tenQuyen, moTa, status) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pq.getIdPQ());
            pst.setString(2, pq.getTenQuyen());
            pst.setString(3, pq.getMoTa());
            pst.setString(4, pq.getStatus());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert PhanQuyen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Method addPhanQuyen() - Giữ lại để tương thích với code cũ (nếu có)
    public boolean addPhanQuyen(PhanQuyen pq) {
        return insert(pq); // Gọi method insert()
    }
    
    // Phương thức cập nhật thông tin Phân Quyền
    public boolean update(PhanQuyen pq) {
        String sql = "UPDATE PHANQUYEN SET tenQuyen = ?, moTa = ?, status = ? WHERE idPQ = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, pq.getTenQuyen());
            pst.setString(2, pq.getMoTa());
            pst.setString(3, pq.getStatus());
            pst.setString(4, pq.getIdPQ());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update PhanQuyen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Method updatePhanQuyen() - Giữ lại để tương thích
    public boolean updatePhanQuyen(PhanQuyen pq) {
        return update(pq);
    }
    
    // Phương thức cập nhật trạng thái (Xóa mềm)
    public boolean updateStatus(String idPQ, String newStatus) {
        String sql = "UPDATE PHANQUYEN SET status = ? WHERE idPQ = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, newStatus);
            pst.setString(2, idPQ);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateStatus PhanQuyen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Phương thức lấy một Phân Quyền theo ID
    public PhanQuyen getById(String idPQ) {
        String sql = "SELECT idPQ, tenQuyen, moTa, status FROM PHANQUYEN WHERE idPQ = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idPQ);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new PhanQuyen(
                        rs.getString("idPQ"),
                        rs.getString("tenQuyen"),
                        rs.getString("moTa"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById PhanQuyen: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Method getPhanQuyenByID() - Giữ lại để tương thích
    public PhanQuyen getPhanQuyenByID(String idPQ) {
        return getById(idPQ);
    }

    // ✅ Method lấy tất cả phân quyền (bao gồm cả ngừng hoạt động)
    public List<PhanQuyen> getAllWithInactive() {
        List<PhanQuyen> list = new ArrayList<>();
        String sql = "SELECT idPQ, tenQuyen, moTa, status FROM PHANQUYEN";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                PhanQuyen pq = new PhanQuyen(
                    rs.getString("idPQ"),
                    rs.getString("tenQuyen"),
                    rs.getString("moTa"),
                    rs.getString("status")
                );
                list.add(pq);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllWithInactive PhanQuyen: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    // Phương thức xóa cứng (Hard delete)
    // Lưu ý: Đã bật ON DELETE CASCADE trong SQL nên CHITIETPHANQUYEN sẽ tự động bị xóa.
    public boolean delete(String idPQ) {
        String sql = "DELETE FROM PHANQUYEN WHERE idPQ = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, idPQ);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete PhanQuyen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Thêm mới một Phân Quyền và các Chi Tiết Phân Quyền của nó
     * trong cùng một giao dịch (transaction).
     * @param pq Đối tượng PhanQuyen (chưa có trong DB)
     * @param details Danh sách các ChiTietPhanQuyen (chỉ cần idCN và các quyền boolean)
     * @return true nếu thành công, false nếu thất bại (sẽ tự động rollback)
     */
    public boolean insertWithDetails(PhanQuyen pq, List<ChiTietPhanQuyen> details) {
        String sqlPQ = "INSERT INTO PHANQUYEN (idPQ, tenQuyen, moTa, status) VALUES (?, ?, ?, ?)";
        Connection con = null;
        
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction
            
            // 1. Thêm Phân Quyền
            try (PreparedStatement pst = con.prepareStatement(sqlPQ)) {
                pst.setString(1, pq.getIdPQ());
                pst.setString(2, pq.getTenQuyen());
                pst.setString(3, pq.getMoTa());
                pst.setString(4, pq.getStatus());
                pst.executeUpdate();
            }

            // 2. Thêm Chi Tiết Phân Quyền
            DAO_ChiTietPhanQuyen daoCT = new DAO_ChiTietPhanQuyen();
            for (ChiTietPhanQuyen ct : details) {
                ct.setIdPQ(pq.getIdPQ()); // Đảm bảo chi tiết có đúng idPQ
                daoCT.insert(ct, con); // Gọi phương thức insert có tham số Connection
            }

            con.commit(); // Hoàn tất transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi insertWithDetails PhanQuyen: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    System.err.println("Lỗi rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Cập nhật thông tin Phân Quyền và Chi Tiết Phân Quyền
     * trong cùng một giao dịch (transaction).
     * @param pq Đối tượng PhanQuyen đã được cập nhật thông tin (tên, mô tả, status)
     * @param details Danh sách ChiTietPhanQuyen MỚI (sẽ xóa tất cả chi tiết cũ)
     * @return true nếu thành công, false nếu thất bại (sẽ tự động rollback)
     */
    public boolean updateWithDetails(PhanQuyen pq, List<ChiTietPhanQuyen> details) {
        String sqlPQ = "UPDATE PHANQUYEN SET tenQuyen = ?, moTa = ?, status = ? WHERE idPQ = ?";
        Connection con = null;

        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Cập nhật thông tin Phân Quyền
            try (PreparedStatement pst = con.prepareStatement(sqlPQ)) {
                pst.setString(1, pq.getTenQuyen());
                pst.setString(2, pq.getMoTa());
                pst.setString(3, pq.getStatus());
                pst.setString(4, pq.getIdPQ());
                pst.executeUpdate();
            }

            DAO_ChiTietPhanQuyen daoCT = new DAO_ChiTietPhanQuyen();

            // 2. Xóa tất cả Chi Tiết Phân Quyền cũ
            daoCT.deleteByIdPQ(pq.getIdPQ(), con);

            // 3. Thêm Chi Tiết Phân Quyền mới
            for (ChiTietPhanQuyen ct : details) {
                ct.setIdPQ(pq.getIdPQ()); // Đảm bảo chi tiết có đúng idPQ
                daoCT.insert(ct, con); // Gọi phương thức insert có tham số Connection
            }

            con.commit(); // Hoàn tất transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi updateWithDetails PhanQuyen: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    System.err.println("Lỗi rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
package DAO;

import model.ChucNang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAO_ChucNang {

    /**
     * Lấy tất cả chức năng đang ở trạng thái "Hoạt động" từ CSDL.
     * Dùng để xây dựng giao diện phân quyền.
     * @return Danh sách các đối tượng ChucNang
     */
    public List<ChucNang> getAllActive() {
        List<ChucNang> list = new ArrayList<>();
        // Lấy các trường allowThem, allowSua để biết có nên hiển thị checkbox Thêm/Sửa không
        String sql = "SELECT idCN, tenChucNang, moTa, allowThem, allowSua, status " +
                     "FROM CHUCNANG WHERE status = N'Hoạt động'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                ChucNang cn = new ChucNang(
                    rs.getString("idCN"),
                    rs.getString("tenChucNang"),
                    rs.getString("moTa"),
                    rs.getBoolean("allowThem"), // Lấy giá trị boolean từ cột allowThem
                    rs.getBoolean("allowSua"),   // Lấy giá trị boolean từ cột allowSua
                    rs.getString("status")
                );
                list.add(cn);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllActive ChucNang: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}
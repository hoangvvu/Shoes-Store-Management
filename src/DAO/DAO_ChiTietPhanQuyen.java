package DAO;

import java.sql.*;
import java.util.*;
import model.ChiTietPhanQuyen;
import DAO.DatabaseConnection;

public class DAO_ChiTietPhanQuyen {

    // Lấy chi tiết quyền theo idPQ
    public List<ChiTietPhanQuyen> getByIdPQ(String idPQ) {
        List<ChiTietPhanQuyen> list = new ArrayList<>();
        String sql = """
            SELECT ctpq.idPQ, cn.idCN, cn.tenChucNang,
                   ctpq.duocXem, ctpq.duocThem, ctpq.duocSua
            FROM CHITIETPHANQUYEN ctpq
            JOIN CHUCNANG cn ON ctpq.idCN = cn.idCN
            WHERE ctpq.idPQ = ?
        """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idPQ);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new ChiTietPhanQuyen(
                    rs.getString("idPQ"),
                    rs.getString("idCN"),
                    rs.getString("tenChucNang"),
                    rs.getBoolean("duocXem"),
                    rs.getBoolean("duocThem"),
                    rs.getBoolean("duocSua")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm chi tiết quyền (trong transaction)
    public boolean insert(ChiTietPhanQuyen ct, Connection con) throws SQLException {
        String sql = "INSERT INTO CHITIETPHANQUYEN (idPQ, idCN, duocXem, duocThem, duocSua) VALUES (?,?,?,?,?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, ct.getIdPQ());
            pst.setString(2, ct.getIdCN());
            pst.setBoolean(3, ct.isDuocXem());
            pst.setBoolean(4, ct.isDuocThem());
            pst.setBoolean(5, ct.isDuocSua());
            return pst.executeUpdate() > 0;
        }
    }

    // Xóa chi tiết quyền theo idPQ
    public void deleteByIdPQ(String idPQ, Connection con) throws SQLException {
        String sql = "DELETE FROM CHITIETPHANQUYEN WHERE idPQ = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idPQ);
            pst.executeUpdate();
        }
    }
}

package model;

import java.util.Objects;

public class NhaCungCap {
    private String idNCC;
    private String tenNCC;
    private String sdt;
    private String email; // Thêm trường này
    private String diaChi; // Thêm trường này
    private String status;

    public NhaCungCap() {}

    // Cập nhật Constructor đầy đủ
    public NhaCungCap(String idNCC, String tenNCC, String sdt, String email, String diaChi, String status) {
        this.idNCC = idNCC;
        this.tenNCC = tenNCC;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.status = status;
    }

    public String getIdNCC() { return idNCC; }
    public void setIdNCC(String idNCC) { this.idNCC = idNCC; }
    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Thêm Getters/Setters cho trường mới
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    /**
     * RẤT QUAN TRỌNG: Giúp JComboBox hiển thị Tên NCC
     * thay vì "model.NhaCungCap@1a2b3c"
     */
    @Override
    public String toString() {
        return this.tenNCC; // Trả về tên để hiển thị
    }

    /**
     * Quan trọng: Giúp JComboBox so sánh các đối tượng
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NhaCungCap that = (NhaCungCap) obj;
        return Objects.equals(idNCC, that.idNCC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNCC);
    }
}
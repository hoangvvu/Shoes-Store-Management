package model;

import java.sql.Date;

public class NhanVien {
    private String idNV;
    private String tenNV;
    private Date ngaySinh;
    private String gioiTinh;
    private String sdt;
    private String diaChi;
    private Date ngayVaoLam;
    private String username;
    private String password;
    private String idPQ;
    private String status;

    public NhanVien() {}

    public NhanVien(String idNV, String tenNV, Date ngaySinh, String gioiTinh, String sdt, String diaChi, Date ngayVaoLam, String username, String password, String idPQ, String status) {
        this.idNV = idNV;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.ngayVaoLam = ngayVaoLam;
        this.username = username;
        this.password = password;
        this.idPQ = idPQ;
        this.status = status;
    }

    // Getters and Setters
    public String getIdNV() { return idNV; }
    public void setIdNV(String idNV) { this.idNV = idNV; }
    
    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    public Date getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(Date ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getIdPQ() { return idPQ; }
    public void setIdPQ(String idPQ) { this.idPQ = idPQ; }
    
    // ✅ THÊM method getPhanQuyen() để tương thích với GUI
    public String getPhanQuyen() { return idPQ; }
    public void setPhanQuyen(String phanQuyen) { this.idPQ = phanQuyen; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
package model;

import java.sql.Date;

public class KhachHang {
    private String idKH;
    private String tenKH;
    private Date ngaySinh;
    private String gioiTinh;
    private String sdt;
    private String diaChi;
    private float tongTien;
    private String status;

    public KhachHang() {}

    public KhachHang(String idKH, String tenKH, Date ngaySinh, String gioiTinh, String sdt, String diaChi, float tongTien, String status) {
        this.idKH = idKH;
        this.tenKH = tenKH;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.tongTien = tongTien;
        this.status = status;
    }

    // Getters and Setters
    public String getIdKH() { return idKH; }
    public void setIdKH(String idKH) { this.idKH = idKH; }
    
    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    
    public Date getNgaySinh() { return ngaySinh; }
    
    // ✅ Setter nhận java.sql.Date
    public void setNgaySinh(Date ngaySinh) { 
        this.ngaySinh = ngaySinh; 
    }
    
    // ✅ THÊM setter nhận java.util.Date từ JDateChooser
    public void setNgaySinh(java.util.Date utilDate) {
        if (utilDate != null) {
            this.ngaySinh = new Date(utilDate.getTime());
        } else {
            this.ngaySinh = null;
        }
    }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    public float getTongTien() { return tongTien; }
    public void setTongTien(float tongTien) { this.tongTien = tongTien; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
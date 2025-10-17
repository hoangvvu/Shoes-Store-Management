package model;

public class KhachHang {
    private String idKH;
    private String tenKH;
    private String sdt;
    private String diaChi;
    private float tongTien;
    private String status;

    public KhachHang() {}

    public KhachHang(String idKH, String tenKH, String sdt, String diaChi, float tongTien, String status) {
        this.idKH = idKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.tongTien = tongTien;
        this.status = status;
    }

    public String getIdKH() { return idKH; }
    public void setIdKH(String idKH) { this.idKH = idKH; }
    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public float getTongTien() { return tongTien; }
    public void setTongTien(float tongTien) { this.tongTien = tongTien; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

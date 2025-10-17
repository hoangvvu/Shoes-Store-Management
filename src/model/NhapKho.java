package model;

import java.util.Date;

public class NhapKho {
    private String idNhapKho;
    private Date ngayNhap;
    private float tongTien;
    private String idNCC;
    private String idNV;
    private String status;

    public NhapKho() {}

    public NhapKho(String idNhapKho, Date ngayNhap, float tongTien, String idNCC, String idNV, String status) {
        this.idNhapKho = idNhapKho;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
        this.idNCC = idNCC;
        this.idNV = idNV;
        this.status = status;
    }

    public String getIdNhapKho() { return idNhapKho; }
    public void setIdNhapKho(String idNhapKho) { this.idNhapKho = idNhapKho; }
    public Date getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(Date ngayNhap) { this.ngayNhap = ngayNhap; }
    public float getTongTien() { return tongTien; }
    public void setTongTien(float tongTien) { this.tongTien = tongTien; }
    public String getIdNCC() { return idNCC; }
    public void setIdNCC(String idNCC) { this.idNCC = idNCC; }
    public String getIdNV() { return idNV; }
    public void setIdNV(String idNV) { this.idNV = idNV; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

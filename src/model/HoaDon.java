package model;

import java.util.Date;

public class HoaDon {
    private String idHD;
    private String idNV;
    private String idKH;
    private Date ngayLap;
    private float tongTien;
    private String status;

    public HoaDon() {}

    public HoaDon(String idHD, String idNV, String idKH, Date ngayLap, float tongTien, String status) {
        this.idHD = idHD;
        this.idNV = idNV;
        this.idKH = idKH;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.status = status;
    }

    public String getIdHD() { return idHD; }
    public void setIdHD(String idHD) { this.idHD = idHD; }
    public String getIdNV() { return idNV; }
    public void setIdNV(String idNV) { this.idNV = idNV; }
    public String getIdKH() { return idKH; }
    public void setIdKH(String idKH) { this.idKH = idKH; }
    public Date getNgayLap() { return ngayLap; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }
    public float getTongTien() { return tongTien; }
    public void setTongTien(float tongTien) { this.tongTien = tongTien; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

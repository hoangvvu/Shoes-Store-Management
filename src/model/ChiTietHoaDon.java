package model;

public class ChiTietHoaDon {
    private String idCTHD;
    private String idHD;
    private String idGiay;
    private int soLuong;
    private float donGia;
    private float thanhTien;
    private String status;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(String idCTHD, String idHD, String idGiay, int soLuong, float donGia, float thanhTien, String status) {
        this.idCTHD = idCTHD;
        this.idHD = idHD;
        this.idGiay = idGiay;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.status = status;
    }

    public String getIdCTHD() { return idCTHD; }
    public void setIdCTHD(String idCTHD) { this.idCTHD = idCTHD; }
    public String getIdHD() { return idHD; }
    public void setIdHD(String idHD) { this.idHD = idHD; }
    public String getIdGiay() { return idGiay; }
    public void setIdGiay(String idGiay) { this.idGiay = idGiay; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public float getDonGia() { return donGia; }
    public void setDonGia(float donGia) { this.donGia = donGia; }
    public float getThanhTien() { return thanhTien; }
    public void setThanhTien(float thanhTien) { this.thanhTien = thanhTien; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

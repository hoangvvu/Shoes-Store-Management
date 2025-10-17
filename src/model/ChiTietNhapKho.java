package model;

public class ChiTietNhapKho {
    private String idCTNK;
    private String idNhapKho;
    private String idGiay;
    private int soLuong;
    private float donGia;
    private float thanhTien;
    private String status;

    public ChiTietNhapKho() {}

    public ChiTietNhapKho(String idCTNK, String idNhapKho, String idGiay, int soLuong,
                          float donGia, float thanhTien, String status) {
        this.idCTNK = idCTNK;
        this.idNhapKho = idNhapKho;
        this.idGiay = idGiay;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.status = status;
    }

    public String getIdCTNK() { return idCTNK; }
    public void setIdCTNK(String idCTNK) { this.idCTNK = idCTNK; }
    public String getIdNhapKho() { return idNhapKho; }
    public void setIdNhapKho(String idNhapKho) { this.idNhapKho = idNhapKho; }
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

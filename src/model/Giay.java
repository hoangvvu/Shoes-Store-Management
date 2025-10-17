package model;

public class Giay {
    private String idGiay;
    private String tenGiay;
    private float size;
    private int soLuong;
    private float giaBan;
    private String moTa;
    private String hinhAnh;
    private String idLoaiGiay;
    private String idHangGiay;
    private String status;

    public Giay() {}

    public Giay(String idGiay, String tenGiay, float size, int soLuong, float giaBan,
                String moTa, String hinhAnh, String idLoaiGiay, String idHangGiay, String status) {
        this.idGiay = idGiay;
        this.tenGiay = tenGiay;
        this.size = size;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.idLoaiGiay = idLoaiGiay;
        this.idHangGiay = idHangGiay;
        this.status = status;
    }

    public String getIdGiay() { return idGiay; }
    public void setIdGiay(String idGiay) { this.idGiay = idGiay; }
    public String getTenGiay() { return tenGiay; }
    public void setTenGiay(String tenGiay) { this.tenGiay = tenGiay; }
    public float getSize() { return size; }
    public void setSize(float size) { this.size = size; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public float getGiaBan() { return giaBan; }
    public void setGiaBan(float giaBan) { this.giaBan = giaBan; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public String getIdLoaiGiay() { return idLoaiGiay; }
    public void setIdLoaiGiay(String idLoaiGiay) { this.idLoaiGiay = idLoaiGiay; }
    public String getIdHangGiay() { return idHangGiay; }
    public void setIdHangGiay(String idHangGiay) { this.idHangGiay = idHangGiay; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

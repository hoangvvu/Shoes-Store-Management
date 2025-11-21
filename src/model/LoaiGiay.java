package model;

public class LoaiGiay {
    private String idLoaiGiay;
    private String tenLoaiGiay;
    private String moTa;
    private String status;

    public LoaiGiay() {}

    public LoaiGiay(String idLoaiGiay, String tenLoaiGiay, String moTa, String status) {
        this.idLoaiGiay = idLoaiGiay;
        this.tenLoaiGiay = tenLoaiGiay;
        this.moTa = moTa;
        this.status = status;
    }

    public String getIdLoaiGiay() { return idLoaiGiay; }
    public void setIdLoaiGiay(String idLoaiGiay) { this.idLoaiGiay = idLoaiGiay; }
    public String getTenLoaiGiay() { return tenLoaiGiay; }
    public void setTenLoaiGiay(String tenLoaiGiay) { this.tenLoaiGiay = tenLoaiGiay; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

package model;

public class HangGiay {
    private String idHangGiay;
    private String tenHangGiay;
    private String moTa;
    private String status;

    public HangGiay() {}

    public HangGiay(String idHangGiay, String tenHangGiay, String moTa, String status) {
        this.idHangGiay = idHangGiay;
        this.tenHangGiay = tenHangGiay;
        this.moTa = moTa;
        this.status = status;
    }

    public String getIdHangGiay() { return idHangGiay; }
    public void setIdHangGiay(String idHangGiay) { this.idHangGiay = idHangGiay; }
    public String getTenHangGiay() { return tenHangGiay; }
    public void setTenHangGiay(String tenHangGiay) { this.tenHangGiay = tenHangGiay; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

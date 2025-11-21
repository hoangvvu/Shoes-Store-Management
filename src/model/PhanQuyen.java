package model;

public class PhanQuyen {
    private String idPQ;
    private String tenQuyen;
    private String moTa;
    private String status;

    public PhanQuyen() {}

    public PhanQuyen(String idPQ, String tenQuyen, String moTa, String status) {
        this.idPQ = idPQ;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
        this.status = status;
    }

    // Getters and Setters
    public String getIdPQ() { return idPQ; }
    public void setIdPQ(String idPQ) { this.idPQ = idPQ; }

    public String getTenQuyen() { return tenQuyen; }
    public void setTenQuyen(String tenQuyen) { this.tenQuyen = tenQuyen; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
package model;

public class ChucNang {
    private String idCN;
    private String tenChucNang;
    private String moTa;
    private boolean allowThem;
    private boolean allowSua;
    private String status;

    public ChucNang() {}

    public ChucNang(String idCN, String tenChucNang, String moTa, boolean allowThem, boolean allowSua, String status) {
        this.idCN = idCN;
        this.tenChucNang = tenChucNang;
        this.moTa = moTa;
        this.allowThem = allowThem;
        this.allowSua = allowSua;
        this.status = status;
    }

    // Getters and Setters
    public String getIdCN() { return idCN; }
    public void setIdCN(String idCN) { this.idCN = idCN; }

    public String getTenChucNang() { return tenChucNang; }
    public void setTenChucNang(String tenChucNang) { this.tenChucNang = tenChucNang; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public boolean isAllowThem() { return allowThem; }
    public void setAllowThem(boolean allowThem) { this.allowThem = allowThem; }

    public boolean isAllowSua() { return allowSua; }
    public void setAllowSua(boolean allowSua) { this.allowSua = allowSua; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
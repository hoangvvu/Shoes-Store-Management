package model;

public class NhaCungCap {
    private String idNCC;
    private String tenNCC;
    private String sdt;
    private String status;

    public NhaCungCap() {}

    public NhaCungCap(String idNCC, String tenNCC, String sdt, String status) {
        this.idNCC = idNCC;
        this.tenNCC = tenNCC;
        this.sdt = sdt;
        this.status = status;
    }

    public String getIdNCC() { return idNCC; }
    public void setIdNCC(String idNCC) { this.idNCC = idNCC; }
    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

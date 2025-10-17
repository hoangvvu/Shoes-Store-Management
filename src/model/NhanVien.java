package model;

public class NhanVien {
    private String idNV;
    private String tenNV;
    private String username;
    private String password;
    private String email;
    private String phanQuyen;
    private String status;

    public NhanVien() {}

    public NhanVien(String idNV, String tenNV, String username, String password,
                    String email, String phanQuyen, String status) {
        this.idNV = idNV;
        this.tenNV = tenNV;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phanQuyen = phanQuyen;
        this.status = status;
    }

    public String getIdNV() { return idNV; }
    public void setIdNV(String idNV) { this.idNV = idNV; }
    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhanQuyen() { return phanQuyen; }
    public void setPhanQuyen(String phanQuyen) { this.phanQuyen = phanQuyen; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

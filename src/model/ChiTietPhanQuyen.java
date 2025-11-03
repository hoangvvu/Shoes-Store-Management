package model;

public class ChiTietPhanQuyen {
    private String idPQ;
    private String idCN;
    private String tenChucNang;
    private boolean duocXem;
    private boolean duocThem;
    private boolean duocSua;

    public ChiTietPhanQuyen() {}

    public ChiTietPhanQuyen(String idPQ, String idCN, String tenChucNang, boolean duocXem, boolean duocThem, boolean duocSua) {
        this.idPQ = idPQ;
        this.idCN = idCN;
        this.tenChucNang = tenChucNang;
        this.duocXem = duocXem;
        this.duocThem = duocThem;
        this.duocSua = duocSua;
    }

    // Getters và Setters
    public String getIdPQ() { return idPQ; }
    public void setIdPQ(String idPQ) { this.idPQ = idPQ; }

    public String getIdCN() { return idCN; }
    public void setIdCN(String idCN) { this.idCN = idCN; }

    public String getTenChucNang() { return tenChucNang; }
    public void setTenChucNang(String tenChucNang) { this.tenChucNang = tenChucNang; }

    public boolean isDuocXem() { return duocXem; }
    public void setDuocXem(boolean duocXem) { this.duocXem = duocXem; }

    public boolean isDuocThem() { return duocThem; }
    public void setDuocThem(boolean duocThem) { this.duocThem = duocThem; }

    public boolean isDuocSua() { return duocSua; }
    public void setDuocSua(boolean duocSua) { this.duocSua = duocSua; }
}

package iss.kienephongthuyfvix.uniportal.model;

public class NhanVien {
    private String manv;
    private String hoten;
    private String phai;
    private String ngsinh;
    private double luong;
    private double phucap;
    private String dt;
    private String vaitro;
    private String madv;

    public NhanVien(String manv, String hoten, String phai, String ngsinh, double luong, double phucap, String dt, String vaitro, String madv) {
        this.manv = manv;
        this.hoten = hoten;
        this.phai = phai;
        this.ngsinh = ngsinh;
        this.luong = luong;
        this.phucap = phucap;
        this.dt = dt;
        this.vaitro = vaitro;
        this.madv = madv;
    }

    public String getManv() {
        return manv;
    }

    public String getHoten() {
        return hoten;
    }

    public String getPhai() {
        return phai;
    }

    public String getNgsinh() {
        return ngsinh;
    }

    public double getLuong() {
        return luong;
    }

    public double getPhucap() {
        return phucap;
    }

    public String getDt() {
        return dt;
    }

    public String getVaitro() {
        return vaitro;
    }

    public String getMadv() {
        return madv;
    }
}
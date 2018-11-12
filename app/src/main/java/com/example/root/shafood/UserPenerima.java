package com.example.root.shafood;

public class UserPenerima {
    public String id_user;
    public String nama;
    public String noktp;
    public String nohp;
    public String alamat;
    public String tanggallahir;
    public String latitude;
    public String longitude;
    public String transaksi;
    public String request;
    public String verifikasi;
    public int level;

    public UserPenerima(String id_user, String nama, String noktp, String nohp, String alamat, String tanggallahir, String latitude, String longitude, String transaksi, String request, String verifikasi) {
        this.id_user = id_user;
        this.nama = nama;
        this.noktp = noktp;
        this.nohp = nohp;
        this.alamat = alamat;
        this.tanggallahir = tanggallahir;
        this.latitude = latitude;
        this.longitude = longitude;
        this.transaksi = transaksi;
        this.request = request;
        this.verifikasi = verifikasi;
        this.level = 4;
    }
}

package com.example.root.shafood;

public class UserKurir {
    public String id_user;
    public String nama;
    public String nohp;
    public String alamat;
    public String tanggallahir;
    public String noplat;
    public int level;
    public String status;
    public String narik;
    public int jumlah_narik;
    private String latitude;
    private String longitude;
    private String verifikasi;

    public UserKurir(String id_user, String nama, String nohp, String alamat, String tanggallahir, String noplat, int level, String status, int jumlah_narik, String latitude, String longitude) {
        this.id_user = id_user;
        this.nama = nama;
        this.nohp = nohp;
        this.alamat = alamat;
        this.tanggallahir = tanggallahir;
        this.noplat = noplat;
        this.level = level;
        this.status = status;
        this.jumlah_narik = jumlah_narik;
        this.latitude = latitude;
        this.longitude = longitude;
        this.narik = "false";
        this.verifikasi = "false";
    }
}

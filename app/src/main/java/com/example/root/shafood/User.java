package com.example.root.shafood;

public class User {
    public String id_user;
    public String nama;
    public String nohp;
    public String alamat;
    public String tanggallahir;
    public String latitude;
    public String longitude;
    public int level;

public User(String id_user, String nama, String nohp, String alamat, String tanggallahir,String latitude,String longitude, int level){
        this.id_user = id_user;
        this.nama = nama;
        this.nohp = nohp;
        this.alamat = alamat;
        this.tanggallahir = tanggallahir;
        this.latitude = latitude;
        this.longitude = longitude;
        this.level = level;
    }
}

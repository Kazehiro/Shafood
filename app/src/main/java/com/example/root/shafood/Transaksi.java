package com.example.root.shafood;

public class Transaksi {
    public String id_transaksi;
    public String id_donatur;
    public String id_kurir;
    public String id_penerima;
    public String alamat;
    public String nama_donatur;
    public String nama_kurir;
    public String nama_penerima;
    public String nama_barang;
    public int kuantitas;

    public Transaksi(String id_transaksi, String id_donatur, String id_penerima, String id_kurir, String alamat, String nama_donatur, String nama_kurir, String nama_penerima, String nama_barang, int kuantitas){
        this.id_transaksi = id_transaksi;
        this.id_donatur = id_donatur;
        this.id_kurir = id_kurir;
        this.id_penerima = id_penerima;
        this.id_donatur = id_donatur;
        this.alamat = alamat;
        this.nama_donatur = nama_donatur;
        this.nama_kurir = nama_kurir;
        this.nama_penerima = nama_penerima;
        this.nama_barang = nama_barang;
        this.kuantitas = kuantitas;
    }
}

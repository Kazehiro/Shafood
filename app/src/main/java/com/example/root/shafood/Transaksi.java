package com.example.root.shafood;

public class Transaksi {
    public String id_transaksi;
    public String id_donatur;
    public String id_kurir;
    public String id_penerima;
    public String alamat_penerima_lat;
    public String alamat_penerima_lng;
    public String alamat_donatur_lat;
    public String alamat_donatur_lng;
    public String nama_donatur;
    public String nama_kurir;
    public String nama_penerima;
    public String nama_barang;
    public String success;
    public int kuantitas;
    public String pesan;

    public Transaksi
            (String id_transaksi,
             String id_donatur,
             String id_penerima,
             String id_kurir,
             String alamat_penerima_lat,
             String alamat_penerima_lng,
             String alamat_donatur_lat,
             String alamat_donatur_lng,
             String nama_donatur,
             String nama_kurir,
             String nama_penerima,
             String nama_barang,
             String Pesan,
             int kuantitas,
             String success) {
        this.id_transaksi = id_transaksi;
        this.id_donatur = id_donatur;
        this.id_kurir = id_kurir;
        this.id_penerima = id_penerima;
        this.alamat_penerima_lat = alamat_penerima_lat;
        this.alamat_penerima_lng = alamat_penerima_lng;
        this.alamat_donatur_lat = alamat_donatur_lat;
        this.alamat_donatur_lng = alamat_donatur_lng;
        this.nama_donatur = nama_donatur;
        this.nama_kurir = nama_kurir;
        this.nama_penerima = nama_penerima;
        this.nama_barang = nama_barang;
        this.pesan = Pesan;
        this.kuantitas = kuantitas;
        this.success = success;
    }
}
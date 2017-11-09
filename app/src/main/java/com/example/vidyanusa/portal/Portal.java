package com.example.vidyanusa.portal;

/**
 * Created by wanda on 04/11/2017.
 */

public class Portal {
    private String nama_makanan;
    private String harga;
    private String stok_barang;
    private String sisa_barang;
    private String owner;

    public Portal() {
        // TODO Auto-generated constructor stub
    }

    public String getNama_makanan(){return nama_makanan;}
    public void setNama_makanan(String nama_makanan) {this.nama_makanan = nama_makanan;}

    public String getHarga(){return harga;}
    public void setHarga(String harga) {this.harga = harga;}

    public String getStok_barang(){return stok_barang;}
    public void setStok_barang(String stok_barang) {this.stok_barang = stok_barang;}

    public String getSisa_barang(){return sisa_barang;}
    public void setSisa_barang(String sisa_barang) {this.sisa_barang = sisa_barang;}

    public String getOwner(){return owner;}
    public void setOwner(String owner) {this.owner = owner;}

}

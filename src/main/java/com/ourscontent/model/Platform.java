package com.ourscontent.model;

public class Platform {
    private Long idPlatform;
    private String namaPlatform;
    private String kategori;
    private String status;

    public Platform() {}

    public Platform(Long idPlatform, String namaPlatform, String kategori, String status) {
        this.idPlatform = idPlatform;
        this.namaPlatform = namaPlatform;
        this.kategori = kategori;
        this.status = status;
    }

    public Long getIdPlatform() {
        return idPlatform;
    }

    public void setIdPlatform(Long idPlatform) {
        this.idPlatform = idPlatform;
    }

    public String getNamaPlatform() {
        return namaPlatform;
    }

    public void setNamaPlatform(String namaPlatform) {
        this.namaPlatform = namaPlatform;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return namaPlatform; // Untuk JComboBox display
    }
}

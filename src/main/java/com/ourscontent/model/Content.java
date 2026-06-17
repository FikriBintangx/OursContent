package com.ourscontent.model;

public class Content {
    private Long idContent;
    private String judulContent;
    private String kategori;
    private String deskripsi;
    private String status;
    private Long idPlatform;
    private String namaPlatform; // Helper field for tables
    private String gambar; // File path or URL to image

    public Content() {}

    public Long getIdContent() {
        return idContent;
    }

    public void setIdContent(Long idContent) {
        this.idContent = idContent;
    }

    public String getJudulContent() {
        return judulContent;
    }

    public void setJudulContent(String judulContent) {
        this.judulContent = judulContent;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    @Override
    public String toString() {
        return judulContent; // JComboBox helper
    }
}

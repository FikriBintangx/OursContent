package com.ourscontent.model;

import java.sql.Date;

public class Performa {
    private Long idPerforma;
    private Long idContent;
    private Date tanggalPosting;
    private Integer views;
    private Integer likes;
    private Integer komentar;

    // Helpers for UI tables
    private String judulContent;
    private String namaPlatform;

    public Performa() {
        this.views = 0;
        this.likes = 0;
        this.komentar = 0;
    }

    public Long getIdPerforma() {
        return idPerforma;
    }

    public void setIdPerforma(Long idPerforma) {
        this.idPerforma = idPerforma;
    }

    public Long getIdContent() {
        return idContent;
    }

    public void setIdContent(Long idContent) {
        this.idContent = idContent;
    }

    public Date getTanggalPosting() {
        return tanggalPosting;
    }

    public void setTanggalPosting(Date tanggalPosting) {
        this.tanggalPosting = tanggalPosting;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getKomentar() {
        return komentar;
    }

    public void setKomentar(Integer komentar) {
        this.komentar = komentar;
    }

    public String getJudulContent() {
        return judulContent;
    }

    public void setJudulContent(String judulContent) {
        this.judulContent = judulContent;
    }

    public String getNamaPlatform() {
        return namaPlatform;
    }

    public void setNamaPlatform(String namaPlatform) {
        this.namaPlatform = namaPlatform;
    }

    public Integer getEngagement() {
        return (likes != null ? likes : 0) + (komentar != null ? komentar : 0);
    }
}

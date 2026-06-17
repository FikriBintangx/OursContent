package com.ourscontent.dao;

import com.ourscontent.db.DatabaseConnection;
import com.ourscontent.model.Content;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContentDAO {

    public ContentDAO() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE content ADD COLUMN IF NOT EXISTS gambar TEXT;");
        } catch (SQLException e) {
            System.err.println("Migration of table content (gambar column) failed: " + e.getMessage());
        }
    }

    public List<Content> getAllContents() {
        List<Content> list = new ArrayList<>();
        String sql = "SELECT c.*, p.nama_platform FROM content c " +
                     "LEFT JOIN platform p ON c.id_platform = p.id_platform " +
                     "ORDER BY c.id_content ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Content c = new Content();
                c.setIdContent(rs.getLong("id_content"));
                c.setJudulContent(rs.getString("judul_content"));
                c.setKategori(rs.getString("kategori"));
                c.setDeskripsi(rs.getString("deskripsi"));
                c.setStatus(rs.getString("status"));
                c.setIdPlatform(rs.getObject("id_platform") != null ? rs.getObject("id_platform") instanceof Long ? (Long)rs.getObject("id_platform") : ((Number)rs.getObject("id_platform")).longValue() : null);
                c.setNamaPlatform(rs.getString("nama_platform"));
                c.setGambar(rs.getString("gambar"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Content> getPublishedContents() {
        List<Content> list = new ArrayList<>();
        String sql = "SELECT c.*, p.nama_platform FROM content c " +
                     "LEFT JOIN platform p ON c.id_platform = p.id_platform " +
                     "WHERE c.status = 'Published' " +
                     "ORDER BY c.id_content ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Content c = new Content();
                c.setIdContent(rs.getLong("id_content"));
                c.setJudulContent(rs.getString("judul_content"));
                c.setKategori(rs.getString("kategori"));
                c.setDeskripsi(rs.getString("deskripsi"));
                c.setStatus(rs.getString("status"));
                c.setIdPlatform(rs.getObject("id_platform") != null ? rs.getObject("id_platform") instanceof Long ? (Long)rs.getObject("id_platform") : ((Number)rs.getObject("id_platform")).longValue() : null);
                c.setNamaPlatform(rs.getString("nama_platform"));
                c.setGambar(rs.getString("gambar"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertContent(Content c) {
        String sql = "INSERT INTO content (judul_content, kategori, deskripsi, status, id_platform, gambar) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getJudulContent());
            ps.setString(2, c.getKategori());
            ps.setString(3, c.getDeskripsi());
            ps.setString(4, c.getStatus());
            if (c.getIdPlatform() != null) {
                ps.setLong(5, c.getIdPlatform());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setString(6, c.getGambar());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateContent(Content c) {
        String sql = "UPDATE content SET judul_content = ?, kategori = ?, deskripsi = ?, status = ?, id_platform = ?, gambar = ? WHERE id_content = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getJudulContent());
            ps.setString(2, c.getKategori());
            ps.setString(3, c.getDeskripsi());
            ps.setString(4, c.getStatus());
            if (c.getIdPlatform() != null) {
                ps.setLong(5, c.getIdPlatform());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setString(6, c.getGambar());
            ps.setLong(7, c.getIdContent());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteContent(Long id) {
        String sql = "DELETE FROM content WHERE id_content = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.ourscontent.dao;

import com.ourscontent.db.DatabaseConnection;
import com.ourscontent.model.Performa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerformaDAO {

    public List<Performa> getAllPerformas() {
        List<Performa> list = new ArrayList<>();
        String sql = "SELECT p.*, c.judul_content, pl.nama_platform FROM performa p " +
                     "JOIN content c ON p.id_content = c.id_content " +
                     "LEFT JOIN platform pl ON c.id_platform = pl.id_platform " +
                     "ORDER BY p.id_performa ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Performa perf = new Performa();
                perf.setIdPerforma(rs.getLong("id_performa"));
                perf.setIdContent(rs.getLong("id_content"));
                perf.setTanggalPosting(rs.getDate("tanggal_posting"));
                perf.setViews(rs.getInt("views"));
                perf.setLikes(rs.getInt("likes"));
                perf.setKomentar(rs.getInt("komentar"));
                perf.setJudulContent(rs.getString("judul_content"));
                perf.setNamaPlatform(rs.getString("nama_platform"));
                list.add(perf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertPerforma(Performa p) {
        String sql = "INSERT INTO performa (id_content, tanggal_posting, views, likes, komentar) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, p.getIdContent());
            ps.setDate(2, p.getTanggalPosting());
            ps.setInt(3, p.getViews() != null ? p.getViews() : 0);
            ps.setInt(4, p.getLikes() != null ? p.getLikes() : 0);
            ps.setInt(5, p.getKomentar() != null ? p.getKomentar() : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePerforma(Performa p) {
        String sql = "UPDATE performa SET id_content = ?, tanggal_posting = ?, views = ?, likes = ?, komentar = ? WHERE id_performa = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, p.getIdContent());
            ps.setDate(2, p.getTanggalPosting());
            ps.setInt(3, p.getViews() != null ? p.getViews() : 0);
            ps.setInt(4, p.getLikes() != null ? p.getLikes() : 0);
            ps.setInt(5, p.getKomentar() != null ? p.getKomentar() : 0);
            ps.setLong(6, p.getIdPerforma());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePerforma(Long id) {
        String sql = "DELETE FROM performa WHERE id_performa = ?";
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

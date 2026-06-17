package com.ourscontent.dao;

import com.ourscontent.db.DatabaseConnection;
import com.ourscontent.model.Platform;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatformDAO {

    public List<Platform> getAllPlatforms() {
        List<Platform> list = new ArrayList<>();
        String sql = "SELECT * FROM platform ORDER BY id_platform ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Platform p = new Platform();
                p.setIdPlatform(rs.getLong("id_platform"));
                p.setNamaPlatform(rs.getString("nama_platform"));
                p.setKategori(rs.getString("kategori"));
                p.setStatus(rs.getString("status"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertPlatform(Platform p) {
        String sql = "INSERT INTO platform (nama_platform, kategori, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNamaPlatform());
            ps.setString(2, p.getKategori());
            ps.setString(3, p.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePlatform(Platform p) {
        String sql = "UPDATE platform SET nama_platform = ?, kategori = ?, status = ? WHERE id_platform = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNamaPlatform());
            ps.setString(2, p.getKategori());
            ps.setString(3, p.getStatus());
            ps.setLong(4, p.getIdPlatform());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePlatform(Long id) {
        String sql = "DELETE FROM platform WHERE id_platform = ?";
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

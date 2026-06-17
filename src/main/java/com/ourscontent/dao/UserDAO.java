package com.ourscontent.dao;

import com.ourscontent.db.DatabaseConnection;
import com.ourscontent.model.User;

import java.sql.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public UserDAO() {
        // Run database migration to ensure users table exists
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id_user SERIAL PRIMARY KEY, " +
                     "username VARCHAR(50) UNIQUE NOT NULL, " +
                     "password_hash VARCHAR(64) NOT NULL, " +
                     "fullname VARCHAR(100), " +
                     "role VARCHAR(20) DEFAULT 'MANAGER', " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ");";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            // Run ALTER table in case the users table was already created without the role column
            stmt.executeUpdate("ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'MANAGER';");
            
            // Seed default users (using upsert to guarantee correct password hashes/roles)
            stmt.executeUpdate("INSERT INTO users (username, password_hash, fullname, role) VALUES " +
                               "('admin', '1eb1afa20dc454d6ef3b6dc6abcbd7dca7e519b698fdf073f4625ded09d74807', 'Administrator Utama', 'ADMIN') " +
                               "ON CONFLICT (username) DO UPDATE SET password_hash = EXCLUDED.password_hash, role = EXCLUDED.role, fullname = EXCLUDED.fullname;");
            
            stmt.executeUpdate("INSERT INTO users (username, password_hash, fullname, role) VALUES " +
                               "('manager', 'a7a3df459dfbafaeed582d8af962f51ece9fc9b46d45cd7bcb2d27c0e2d28c4d', 'Manager Konten', 'MANAGER') " +
                               "ON CONFLICT (username) DO UPDATE SET password_hash = EXCLUDED.password_hash, role = EXCLUDED.role, fullname = EXCLUDED.fullname;");
        } catch (SQLException e) {
            System.err.println("Migration of table users or seeding failed: " + e.getMessage());
        }
    }

    private String hashPassword(String password, String username) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Use username as salt to ensure unique hashes per user
            String salt = username.toLowerCase();
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean registerUser(String username, String password, String fullname) {
        // First check if username already exists
        if (isUsernameExists(username)) {
            return false;
        }

        String sql = "INSERT INTO users (username, password_hash, fullname, role) VALUES (?, ?, ?, 'MANAGER')";
        String hashed = hashPassword(password, username);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hashed);
            ps.setString(3, fullname.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(String username, String password, String fullname, String role) {
        if (isUsernameExists(username)) {
            return false;
        }

        String sql = "INSERT INTO users (username, password_hash, fullname, role) VALUES (?, ?, ?, ?)";
        String hashed = hashPassword(password, username);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hashed);
            ps.setString(3, fullname.trim());
            ps.setString(4, role.trim().toUpperCase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getLong("id_user"));
                user.setUsername(rs.getString("username"));
                user.setFullname(rs.getString("fullname"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = hashPassword(password, username);
                    if (storedHash.equals(inputHash)) {
                        User user = new User();
                        user.setIdUser(rs.getLong("id_user"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(storedHash);
                        user.setFullname(rs.getString("fullname"));
                        user.setRole(rs.getString("role"));
                        user.setCreatedAt(rs.getTimestamp("created_at"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProfile(Long idUser, String fullname, String username) {
        String sql = "UPDATE users SET fullname = ?, username = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullname.trim());
            ps.setString(2, username.trim());
            ps.setLong(3, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(Long idUser, String newPassword, String username) {
        String sql = "UPDATE users SET password_hash = ? WHERE id_user = ?";
        String hashed = hashPassword(newPassword, username);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setLong(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(Long idUser) {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(Long idUser, String username, String fullname, String role) {
        String sql = "UPDATE users SET username = ?, fullname = ?, role = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, fullname.trim());
            ps.setString(3, role.trim().toUpperCase());
            ps.setLong(4, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserWithPassword(Long idUser, String username, String password, String fullname, String role) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, fullname = ?, role = ? WHERE id_user = ?";
        String hashed = hashPassword(password, username);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hashed);
            ps.setString(3, fullname.trim());
            ps.setString(4, role.trim().toUpperCase());
            ps.setLong(5, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

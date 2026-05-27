package com.taskmanager.db;

import com.taskmanager.model.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:tasks.db";

    public DatabaseHandler() {
        createTable();
        migrateTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "title TEXT NOT NULL," +
                     "priority TEXT NOT NULL," +
                     "status TEXT DEFAULT 'To Do'," +
                     "due_date TEXT DEFAULT ''," +
                     "completed INTEGER DEFAULT 0)";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void migrateTable() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            try { stmt.execute("ALTER TABLE tasks ADD COLUMN status TEXT DEFAULT 'To Do'"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE tasks ADD COLUMN due_date TEXT DEFAULT ''"); } catch (SQLException ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTask(Task task) {
        String sql = "INSERT INTO tasks(title, priority, status, due_date, completed) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getPriority());
            pstmt.setString(3, task.getStatus());
            pstmt.setString(4, task.getDueDate());
            pstmt.setInt(5, task.isCompleted() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Task t = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getString("due_date")
                );
                t.setCompleted(rs.getInt("completed") == 1);
                tasks.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Task Deleted Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET title=?, priority=?, status=?, due_date=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getPriority());
            pstmt.setString(3, task.getStatus());
            pstmt.setString(4, task.getDueDate());
            pstmt.setInt(5, task.getId());
            pstmt.executeUpdate();
            System.out.println("Task Updated Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> searchTasks(String keyword) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE title LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Task t = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getString("due_date")
                );
                t.setCompleted(rs.getInt("completed") == 1);
                tasks.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
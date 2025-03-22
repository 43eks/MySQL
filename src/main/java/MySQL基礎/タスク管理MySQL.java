package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class タスク管理MySQL {
    private static final String URL = "jdbc:mysql://localhost:3306/task_manage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("MYSQL_PASSWORD");

    public static void main(String[] args) {
        if (PASSWORD == null || PASSWORD.isEmpty()) {
            System.out.println("エラー: MYSQL_PASSWORD 環境変数が設定されていません。");
            System.exit(1);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== タスク管理システム ===");
                System.out.println("1. タスク一覧表示");
                System.out.println("2. タスク追加");
                System.out.println("3. タスク削除");
                System.out.println("4. タスク更新");
                System.out.println("5. 終了");
                System.out.print("選択してください: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // 改行を消費

                switch (choice) {
                    case 1:
                        showTasks();
                        break;
                    case 2:
                        System.out.print("タスク名を入力: ");
                        String taskName = scanner.nextLine();
                        addTask(taskName);
                        break;
                    case 3:
                        System.out.print("削除するタスクIDを入力: ");
                        int deleteId = scanner.nextInt();
                        deleteTask(deleteId);
                        break;
                    case 4:
                        System.out.print("更新するタスクIDを入力: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine(); // 改行を消費
                        System.out.print("新しいタスク名を入力: ");
                        String newTaskName = scanner.nextLine();
                        updateTask(updateId, newTaskName);
                        break;
                    case 5:
                        System.out.println("終了します。");
                        return;
                    default:
                        System.out.println("無効な選択です。");
                }
            }
        }
    }

    // タスク一覧表示
    private static void showTasks() {
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== タスク一覧 ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", タスク名: " + rs.getString("task_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // タスク追加
    private static void addTask(String taskName) {
        String sql = "INSERT INTO tasks (task_name) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, taskName);
            int rows = pstmt.executeUpdate();
            System.out.println(rows + " 件のタスクを追加しました。");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // タスク削除
    private static void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            int rows = pstmt.executeUpdate();
            System.out.println(rows + " 件のタスクを削除しました。");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // タスク更新
    private static void updateTask(int taskId, String newTaskName) {
        String sql = "UPDATE tasks SET task_name = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTaskName);
            pstmt.setInt(2, taskId);
            int rows = pstmt.executeUpdate();
            System.out.println(rows + " 件のタスクを更新しました。");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
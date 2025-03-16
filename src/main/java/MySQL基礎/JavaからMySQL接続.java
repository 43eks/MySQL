package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JavaからMySQL接続 {
    public static void main(String[] args) {
        // MySQL接続のURL、ユーザー名、パスワードを設定
        String url = "jdbc:mysql://localhost:3306/task_manager";  // データベース名task_manager
        String user = "root"; // ユーザー名
        String password = "password"; // パスワード（適宜変更）

        try {
            // JDBCドライバの読み込み（必要な場合）
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 接続の確立
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("接続成功");

            // SQL文の実行
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tasks");

            // 結果の表示
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", タスク名: " + rs.getString("task_name"));
            }

            // 接続を閉じる
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
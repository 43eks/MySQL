package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class タスク管理MySQL {
    public static void main(String[] args) {
        // MySQL接続のURL、ユーザー名を設定
        String url = "jdbc:mysql://localhost:3306/task_manage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root"; // ユーザー名
        
        // 環境変数からMySQLパスワードを取得
        String password = System.getenv("MYSQL_PASSWORD");

        if (password == null) {
            System.out.println("エラー: MYSQL_PASSWORD 環境変数が設定されていません。");
            return;
        }

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
package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JavaからMySQL接続 {
    private static final String URL = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USER = "root";  // MySQLのユーザー名
    private static final String PASSWORD = "";  // パスワード（設定している場合）

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Connection conn = connect();
        if (conn != null) {
            System.out.println("データベース接続成功！");
        } else {
            System.out.println("接続失敗...");
        }
    }
}

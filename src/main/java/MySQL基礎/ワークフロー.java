
package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ワークフロー {
    private static final String URL = "jdbc:mysql://localhost:3306/workflow_system";
    private static final String USER = System.getenv("DB_USER"); // 環境変数から取得
    private static final String PASSWORD = System.getenv("DB_PASSWORD"); // 環境変数から取得

    public static Connection getConnection() throws SQLException {
        if (USER == null || PASSWORD == null) {
            throw new IllegalStateException("環境変数 DB_USER または DB_PASSWORD が設定されていません。");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
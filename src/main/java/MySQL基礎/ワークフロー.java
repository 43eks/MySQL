package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ワークフロー {
    private static final String URL = System.getenv("DB_URL"); // 環境変数から取得
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new IllegalStateException("環境変数 DB_URL, DB_USER, DB_PASSWORD のいずれかが設定されていません。");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
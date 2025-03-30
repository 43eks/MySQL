package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class レシピ検索 {

    // MySQL接続設定
    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipe_db";
    private static final String DB_USER = "root";  // MySQLのユーザー名
    private static final String DB_PASSWORD = "password";  // MySQLのパスワード

    public static void main(String[] args) {
        try {
            // MySQL接続
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();

            // 不足している食材の取得クエリ
            String query = "SELECT DISTINCT i.name " +
                           "FROM ingredients i " +
                           "LEFT JOIN user_ingredients u ON i.name = u.name " +
                           "WHERE u.name IS NULL";

            ResultSet rs = stmt.executeQuery(query);

            // 不足している食材のリストを作成
            List<String> missingIngredients = new ArrayList<>();
            while (rs.next()) {
                missingIngredients.add(rs.getString("name"));
            }

            // 不足している食材を表示
            if (missingIngredients.isEmpty()) {
                System.out.println("すべての食材が揃っています！");
            } else {
                System.out.println("不足している食材:");
                for (String ingredient : missingIngredients) {
                    System.out.println("- " + ingredient);
                }
            }

            // リソースをクローズ
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

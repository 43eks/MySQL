package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class レシピ検索 {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipe_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";  // MySQLのユーザー名
    private static final String DB_PASSWORD = System.getenv("MYSQL_PASSWORD");  // MySQLのパスワード（環境変数を使う場合はSystem.getenv("MYSQL_PASSWORD")）

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // MySQL接続
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();

            // ユーザーの冷蔵庫の食材を取得
            String fridgeQuery = "SELECT name FROM user_ingredients";
            rs = stmt.executeQuery(fridgeQuery);

            // 冷蔵庫にある食材リストを作成
            List<String> fridgeIngredients = new ArrayList<>();
            while (rs.next()) {
                fridgeIngredients.add(rs.getString("name"));
            }

            // 冷蔵庫にある食材をクォート付きでリストにする
            List<String> quotedIngredients = new ArrayList<>();
            for (String ingredient : fridgeIngredients) {
                quotedIngredients.add("'" + ingredient + "'");
            }

            // 作れるレシピを検索
            String recipeQuery = "SELECT r.name " +
                                 "FROM recipes r " +
                                 "JOIN ingredients i ON r.id = i.recipe_id " +
                                 "WHERE NOT EXISTS ( " +
                                 "  SELECT 1 FROM ingredients i2 " +
                                 "  WHERE i2.recipe_id = r.id " +
                                 "  AND i2.name NOT IN (" + String.join(",", quotedIngredients) + ")" +
                                 ")";
            rs = stmt.executeQuery(recipeQuery);

            // 作れるレシピを表示
            List<String> availableRecipes = new ArrayList<>();
            while (rs.next()) {
                availableRecipes.add(rs.getString("name"));
            }

            if (availableRecipes.isEmpty()) {
                System.out.println("冷蔵庫にある食材では作れるレシピがありません！");
            } else {
                System.out.println("冷蔵庫にある食材で作れるレシピ:");
                for (String recipe : availableRecipes) {
                    System.out.println("- " + recipe);
                }
            }

        } catch (SQLException e) {
            System.out.println("データベース接続エラー: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // リソースを確実にクローズ
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("リソースのクローズエラー: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
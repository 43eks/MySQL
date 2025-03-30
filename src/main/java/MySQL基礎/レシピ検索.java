package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class レシピ検索 {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipe_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";  // MySQLのユーザー名
    private static final String DB_PASSWORD = System.getenv("MYSQL_PASSWORD");  // MySQLのパスワード（環境変数を使う場合はSystem.getenv("MYSQL_PASSWORD")）

    public static void main(String[] args) {
        // ユーザから冷蔵庫の食材を入力してもらう
        Scanner scanner = new Scanner(System.in);
        System.out.println("冷蔵庫にある食材をカンマ区切りで入力してください:");
        String input = scanner.nextLine();
        
        // 入力された食材をリストに分割
        String[] ingredients = input.split(",");
        List<String> userIngredients = new ArrayList<>();
        for (String ingredient : ingredients) {
            userIngredients.add(ingredient.trim());
        }

        // MySQL接続
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // MySQL接続
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();

            // 入力された食材に基づいて作れるレシピを検索するクエリ
            StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT r.name FROM recipes r " +
                    "JOIN ingredients i ON r.id = i.recipe_id WHERE ");
            for (int i = 0; i < userIngredients.size(); i++) {
                queryBuilder.append("i.name = '").append(userIngredients.get(i)).append("'");
                if (i < userIngredients.size() - 1) {
                    queryBuilder.append(" OR ");
                }
            }

            String query = queryBuilder.toString();
            rs = stmt.executeQuery(query);

            // 作れるレシピをリストに追加
            List<String> availableRecipes = new ArrayList<>();
            while (rs.next()) {
                availableRecipes.add(rs.getString("name"));
            }

            // 作れるレシピを表示
            if (availableRecipes.isEmpty()) {
                System.out.println("現在、冷蔵庫にある食材では作れるレシピはありません。");
            } else {
                System.out.println("作れるレシピ:");
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
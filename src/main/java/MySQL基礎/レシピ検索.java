package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class レシピ検索 {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipe_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";  
    private static final String DB_PASSWORD = System.getenv("MYSQL_PASSWORD");  

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // ユーザーに食材を入力してもらう
            System.out.println("冷蔵庫にある食材をカンマ区切りで入力してください (例: 玉ねぎ,にんじん,じゃがいも):");
            String input = scanner.nextLine().trim();

            // 終了条件
            if (input.equalsIgnoreCase("終了")) {
                System.out.println("終了します。");
                break;
            }

            // 食材をリスト化
            String[] ingredients = input.split(",");
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = ingredients[i].trim();
            }

            // SQLクエリ作成
            String sql = "SELECT DISTINCT r.name FROM recipes r " +
                         "JOIN ingredients i ON r.id = i.recipe_id " +
                         "WHERE i.name IN (" + String.join(",", Collections.nCopies(ingredients.length, "?")) + ")";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // プレースホルダに値を設定
                for (int i = 0; i < ingredients.length; i++) {
                    pstmt.setString(i + 1, ingredients[i]);
                }

                // クエリ実行
                ResultSet rs = pstmt.executeQuery();
                List<String> availableRecipes = new ArrayList<>();

                while (rs.next()) {
                    availableRecipes.add(rs.getString("name"));
                }

                // 結果表示
                if (availableRecipes.isEmpty()) {
                    System.out.println("現在の食材では作れるレシピはありません。");
                } else {
                    System.out.println("作れるレシピ:");
                    for (String recipe : availableRecipes) {
                        System.out.println("- " + recipe);
                    }
                }

            } catch (SQLException e) {
                System.out.println("データベース接続エラー: " + e.getMessage());
                e.printStackTrace();
            }

            // 継続確認
            System.out.println("もう一度食材を入力しますか？ (y/n)");
            String continueInput = scanner.nextLine().trim();
            if (!continueInput.equalsIgnoreCase("y")) {
                break;
            }
        }

        scanner.close();
    }
}
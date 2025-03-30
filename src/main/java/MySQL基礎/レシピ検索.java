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
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // 冷蔵庫の食材を入力
            System.out.println("冷蔵庫にある食材をカンマ区切りで入力してください:");
            String input = scanner.nextLine().trim();

            // ユーザーが終了を希望した場合
            if (input.equalsIgnoreCase("終了")) {
                System.out.println("終了します。");
                break;
            }

            // MySQL接続
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                // 食材が存在するレシピを検索するクエリ
                String query = "SELECT DISTINCT r.name " +
                               "FROM recipes r " +
                               "JOIN ingredients i ON r.id = i.recipe_id " +
                               "WHERE i.name IN ('" + input.replace(",", "','") + "') " +
                               "GROUP BY r.id " +
                               "HAVING COUNT(DISTINCT i.name) = (SELECT COUNT(*) FROM ingredients WHERE recipe_id = r.id)";
                
                ResultSet rs = stmt.executeQuery(query);

                // 作れるレシピをリストに追加
                List<String> availableRecipes = new ArrayList<>();
                while (rs.next()) {
                    availableRecipes.add(rs.getString("name"));
                }

                // 結果を表示
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

            // 次の入力を促す
            System.out.println("もう一度食材を入力するには、Enterキーを押してください。終了するには「終了」と入力してください。");
            String continueInput = scanner.nextLine().trim();
            if (continueInput.equalsIgnoreCase("終了")) {
                break;
            }
        }

        scanner.close();
    }
}
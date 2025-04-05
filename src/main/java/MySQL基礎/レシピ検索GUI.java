package MySQL基礎;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class レシピ検索GUI extends Application {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipe_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = System.getenv("MYSQL_PASSWORD");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("レシピ検索アプリ");

        Label label = new Label("冷蔵庫にある食材をカンマ区切りで入力してください:");
        TextField inputField = new TextField();
        Button searchButton = new Button("検索");
        ListView<String> resultListView = new ListView<>();

        searchButton.setOnAction(e -> {
            String input = inputField.getText().trim();
            if (!input.isEmpty()) {
                List<String> recipes = searchRecipes(input);
                resultListView.getItems().clear();
                if (recipes.isEmpty()) {
                    resultListView.getItems().add("現在の食材では作れるレシピはありません。");
                } else {
                    resultListView.getItems().addAll(recipes);
                }
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(label, inputField, searchButton, resultListView);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<String> searchRecipes(String ingredients) {
        List<String> availableRecipes = new ArrayList<>();
        String[] ingredientList = ingredients.split(",");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT DISTINCT r.name " +
                           "FROM recipes r " +
                           "JOIN ingredients i ON r.id = i.recipe_id " +
                           "WHERE i.name IN (" + String.join(",", ingredientList) + ") " +
                           "GROUP BY r.id " +
                           "HAVING COUNT(DISTINCT i.name) = (SELECT COUNT(*) FROM ingredients WHERE recipe_id = r.id)";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    availableRecipes.add(rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            availableRecipes.add("データベース接続エラー: " + e.getMessage());
        }

        return availableRecipes;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
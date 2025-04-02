package MySQL基礎;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ワークフロー {
    public static void addUser(String username, String email, String hashedPassword, String role) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = ワークフロー.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role);

            stmt.executeUpdate();
            System.out.println("ユーザーを追加しました: " + username);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	private static Connection getConnection() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
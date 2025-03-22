package MySQL基礎;

import java.awt.Button;
import java.awt.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.TableColumn;
import javax.swing.text.TableView;

import com.apple.eawt.Application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class タスク管理MySQLGUI extends Application {

    private static final String URL = "jdbc:mysql://localhost:3306/task_manage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("MYSQL_PASSWORD");

    private TableView<Task> table;
    private TextField taskNameField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // テーブルの作成
        table = new TableView<>();
        TableColumn<Task, String> taskColumn = new TableColumn<>("タスク名");
        taskColumn.setCellValueFactory(data -> data.getValue().taskNameProperty());

        table.getColumns().add(taskColumn);

        // テキストフィールドの作成
        taskNameField = new TextField();
        taskNameField.setPromptText("タスク名");

        // ボタンの作成
        Button addButton = new Button("タスク追加");
        addButton.setOnAction(event -> addTask());

        Button deleteButton = new Button("タスク削除");
        deleteButton.setOnAction(event -> deleteTask());

        Button updateButton = new Button("タスク更新");
        updateButton.setOnAction(event -> updateTask());

        // レイアウト
        VBox vbox = new VBox();
        vbox.getChildren().addAll(taskNameField, addButton, deleteButton, updateButton, table);

        // シーンの作成
        Scene scene = new Scene(vbox, 400, 400);
        stage.setScene(scene);
        stage.setTitle("タスク管理アプリ");
        stage.show();

        // データベースからタスクを取得してテーブルに表示
        loadTasks();
    }

    // タスクを追加するメソッド
    private void addTask() {
        String taskName = taskNameField.getText();
        if (taskName.isEmpty()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks (task_name) VALUES (?)")) {
            stmt.setString(1, taskName);
            stmt.executeUpdate();
            taskNameField.clear();
            loadTasks(); // タスクを更新
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // タスクを削除するメソッド
    private void deleteTask() {
        Task selectedTask = table.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
                stmt.setInt(1, selectedTask.getId());
                stmt.executeUpdate();
                loadTasks(); // タスクを更新
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // タスクを更新するメソッド
    private void updateTask() {
        Task selectedTask = table.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            String newTaskName = taskNameField.getText();
            if (!newTaskName.isEmpty()) {
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement("UPDATE tasks SET task_name = ? WHERE id = ?")) {
                    stmt.setString(1, newTaskName);
                    stmt.setInt(2, selectedTask.getId());
                    stmt.executeUpdate();
                    taskNameField.clear();
                    loadTasks(); // タスクを更新
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // データベースからタスクを読み込み、テーブルに表示するメソッド
    private void loadTasks() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            while (rs.next()) {
                taskList.add(new Task(rs.getInt("id"), rs.getString("task_name")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(taskList);
    }

    // タスクを表すクラス
    public static class Task {
        private final int id;
        private final SimpleStringProperty taskName;

        public Task(int id, String taskName) {
            this.id = id;
            this.taskName = new SimpleStringProperty(taskName);
        }

        public int getId() {
            return id;
        }

        public String getTaskName() {
            return taskName.get();
        }

        public SimpleStringProperty taskNameProperty() {
            return taskName;
        }
    }
}
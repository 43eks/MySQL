package MySQL基礎;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class タスク管理MySQLGUI extends Application {

    private TableView<Task> table;
    private TextField taskNameField;
    private Button addButton, deleteButton, updateButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // テーブルの設定
        table = new TableView<>();
        TableColumn<Task, String> taskColumn = new TableColumn<>("タスク名");
        taskColumn.setCellValueFactory(cellData -> cellData.getValue().taskNameProperty());
        table.getColumns().add(taskColumn);

        // テキストフィールド
        taskNameField = new TextField();
        taskNameField.setPromptText("タスク名");

        // ボタンの設定
        addButton = new Button("追加");
        addButton.setOnAction(event -> addTask());

        deleteButton = new Button("削除");
        deleteButton.setOnAction(event -> deleteTask());

        updateButton = new Button("更新");
        updateButton.setOnAction(event -> updateTask());

        // レイアウト
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(taskNameField, addButton, deleteButton, updateButton);

        // シーンの設定
        Scene scene = new Scene(hbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("タスク管理");
        primaryStage.show();
    }

    private void addTask() {
        String taskName = taskNameField.getText();
        if (!taskName.isEmpty()) {
            Task task = new Task(taskName);
            table.getItems().add(task);
            taskNameField.clear(); // 入力フィールドをクリア
        }
    }

    private void deleteTask() {
        Task selectedTask = table.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            table.getItems().remove(selectedTask);
        }
    }

    private void updateTask() {
        Task selectedTask = table.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setTaskName(taskNameField.getText());
            table.refresh();
            taskNameField.clear(); // 入力フィールドをクリア
        }
    }
}

// Taskクラスを定義
class Task {
    private SimpleStringProperty taskName;

    public Task(String taskName) {
        this.taskName = new SimpleStringProperty(taskName);
    }

    public String getTaskName() {
        return taskName.get();
    }

    public void setTaskName(String taskName) {
        this.taskName.set(taskName);
    }

    public SimpleStringProperty taskNameProperty() {
        return taskName;
    }
}
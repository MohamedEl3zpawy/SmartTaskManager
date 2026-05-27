package com.taskmanager.controller;

import com.taskmanager.db.DatabaseHandler;
import com.taskmanager.exception.InvalidTaskException;
import com.taskmanager.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.beans.property.SimpleStringProperty;

public class TaskController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleCol;
    @FXML private TableColumn<Task, String> priorityCol;
    @FXML private TableColumn<Task, String> statusCol;
    @FXML private TableColumn<Task, String> dueDateCol;
    @FXML private TextField titleField;
    @FXML private TextField searchField;
    @FXML private TextField dueDateField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private Label statusLabel;
    @FXML private Label totalLabel;
    @FXML private Label doneLabel;
    @FXML private Label inProgressLabel;
    @FXML private Button themeToggleBtn;

    private boolean isDarkMode = true;
    private DatabaseHandler db = new DatabaseHandler();
    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        priorityBox.setItems(FXCollections.observableArrayList("High", "Medium", "Low"));
        statusBox.setItems(FXCollections.observableArrayList("To Do", "In Progress", "Done"));

        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        priorityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriority()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        dueDateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDueDate()));

        taskTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                titleField.setText(newVal.getTitle());
                priorityBox.setValue(newVal.getPriority());
                statusBox.setValue(newVal.getStatus());
                dueDateField.setText(newVal.getDueDate());
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) loadTasksFromDB();
            else {
                taskList.clear();
                taskList.addAll(db.searchTasks(newVal));
                taskTable.setItems(taskList);
                updateStats();
            }
        });

        loadTasksFromDB();
    }

    @FXML
    public void handleAddTask() {
        String title = titleField.getText().trim();
        String priority = priorityBox.getValue();
        String status = statusBox.getValue() != null ? statusBox.getValue() : "To Do";
        String dueDate = dueDateField.getText().trim();

        javafx.concurrent.Task<Void> bgTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                if (title.isEmpty()) throw new InvalidTaskException("Title cannot be empty!");
                if (priority == null) throw new InvalidTaskException("Please select a priority!");
                Task newTask = new Task(0, title, priority, status, dueDate);
                db.addTask(newTask);
                return null;
            }
        };

        bgTask.setOnSucceeded(e -> {
            statusLabel.setText("✓ Task added successfully!");
            loadTasksFromDB();
            clearFields();
        });

        bgTask.setOnFailed(e -> statusLabel.setText("✗ " + bgTask.getException().getMessage()));

        new Thread(bgTask).start();
    }

    @FXML
    private void handleEditTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            String title = titleField.getText().trim();
            String priority = priorityBox.getValue();
            String status = statusBox.getValue();
            String dueDate = dueDateField.getText().trim();

            if (title.isEmpty() || priority == null) {
                statusLabel.setText("✗ Title or Priority cannot be empty.");
                return;
            }

            selectedTask.setTitle(title);
            selectedTask.setPriority(priority);
            selectedTask.setStatus(status != null ? status : "To Do");
            selectedTask.setDueDate(dueDate);

            db.updateTask(selectedTask);
            loadTasksFromDB();
            statusLabel.setText("✓ Task updated successfully!");
            clearFields();
        } else {
            statusLabel.setText("✗ Please select a task to edit.");
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            db.deleteTask(selectedTask.getId());
            loadTasksFromDB();
            statusLabel.setText("✓ Task deleted successfully!");
            clearFields();
        } else {
            statusLabel.setText("✗ Please select a task to delete.");
        }
    }

    @FXML
    private void handleThemeToggle() {
        Scene scene = taskTable.getScene();
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
            themeToggleBtn.setText("🌙 Dark Mode");
            isDarkMode = false;
        } else {
            scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            themeToggleBtn.setText("☀ Light Mode");
            isDarkMode = true;
        }
    }

    private void loadTasksFromDB() {
        taskList.clear();
        taskList.addAll(db.getAllTasks());
        taskTable.setItems(taskList);
        updateStats();
    }

    private void updateStats() {
        long total = taskList.size();
        long done = taskList.stream().filter(t -> t.getStatus().equals("Done")).count();
        long inProgress = taskList.stream().filter(t -> t.getStatus().equals("In Progress")).count();
        totalLabel.setText("Total: " + total);
        doneLabel.setText("Done: " + done);
        inProgressLabel.setText("In Progress: " + inProgress);
    }

    private void clearFields() {
        titleField.clear();
        priorityBox.setValue(null);
        statusBox.setValue(null);
        dueDateField.clear();
    }
}
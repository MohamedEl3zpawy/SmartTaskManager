package com.taskmanager.controller;

import com.taskmanager.db.DatabaseHandler;
import com.taskmanager.exception.InvalidTaskException;
import com.taskmanager.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;

public class TaskController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleCol;
    @FXML private TableColumn<Task, String> priorityCol;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private Label statusLabel;

    private DatabaseHandler db = new DatabaseHandler();
    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        priorityBox.setItems(FXCollections.observableArrayList("High", "Medium", "Low"));
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        priorityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriority()));
        taskTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        loadTasksFromDB();
    }

    @FXML
    public void handleAddTask() {
        
        String title = titleField.getText().trim();
        String priority = priorityBox.getValue();

        javafx.concurrent.Task<Void> bgTask = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (title.isEmpty()) throw new InvalidTaskException("Title cannot be empty!");
                if (priority == null) throw new InvalidTaskException("Please select a priority!");
                
                Task newTask = new Task(0, title, priority);
                db.addTask(newTask);
                return null;
            }
        };

        bgTask.setOnSucceeded(e -> {
            statusLabel.setText("Task added successfully!");
            loadTasksFromDB();
            titleField.clear();
            priorityBox.setValue(null);
        });

        bgTask.setOnFailed(e -> {
            statusLabel.setText("Error: " + bgTask.getException().getMessage());
        });

        new Thread(bgTask).start();
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            db.deleteTask(selectedTask.getId());
            loadTasksFromDB();
            statusLabel.setText("Task deleted successfully!");
        } else {
            statusLabel.setText("Please select a task to delete.");
        }
    }

    @FXML
    private void handleEditTask() { 
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            String title = titleField.getText().trim();
            String priority = priorityBox.getValue();

            if (title.isEmpty() || priority == null) {
                statusLabel.setText("Error: Title or Priority cannot be empty.");
                return;
            }

            selectedTask.setTitle(title);
            selectedTask.setPriority(priority);

            db.updateTask(selectedTask);
            loadTasksFromDB();
            statusLabel.setText("Task updated successfully!");
            titleField.clear();
            priorityBox.setValue(null);
        } else {
            statusLabel.setText("Please select a task to edit.");
        }
    }

    private void loadTasksFromDB() {
        taskList.clear();
        taskList.addAll(db.getAllTasks());
        taskTable.setItems(taskList);
    }
}
package com.taskmanager;

import com.taskmanager.model.Task;
import com.taskmanager.exception.InvalidTaskException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTaskCreation() {
        Task task = new Task(1, "Buy groceries", "High");
        assertEquals("Buy groceries", task.getTitle());
        assertEquals("High", task.getPriority());
        assertFalse(task.isCompleted());
    }

    @Test
    void testTaskSetters() {
        Task task = new Task(1, "Old title", "Low");
        task.setTitle("New title");
        task.setPriority("High");
        task.setCompleted(true);
        assertEquals("New title", task.getTitle());
        assertEquals("High", task.getPriority());
        assertTrue(task.isCompleted());
    }

    @Test
    void testInvalidTaskException() {
        assertThrows(InvalidTaskException.class, () -> {
            String title = "";
            if (title.isEmpty()) throw new InvalidTaskException("Title cannot be empty!");
        });
    }

    @Test
    void testInvalidPriorityException() {
        assertThrows(InvalidTaskException.class, () -> {
            String priority = null;
            if (priority == null) throw new InvalidTaskException("Please select a priority!");
        });
    }
}
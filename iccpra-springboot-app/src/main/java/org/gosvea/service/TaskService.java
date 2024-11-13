package org.gosvea.service;

import org.gosvea.pojo.Task;

import java.util.List;

public interface TaskService {


    void addNewTask(Task task);
    void editTask(Task task);
    void deleteTask(Integer id);
    List<Task> getTasksByIcpisManager(String icpisName);
}

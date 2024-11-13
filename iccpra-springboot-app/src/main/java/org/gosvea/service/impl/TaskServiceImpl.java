package org.gosvea.service.impl;

import org.gosvea.mapper.TaskMapper;
import org.gosvea.pojo.Task;
import org.gosvea.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public void addNewTask( Task task) {

        taskMapper.addNewTask(task);
    }

    @Override
    public void editTask( Task task) {

        taskMapper.editTask(task);
    }

    @Override
    public void deleteTask(Integer id) {
        taskMapper.deleteTask(id);
    }

    @Override
    public List<Task> getTasksByIcpisManager(String icpisName) {
        return taskMapper.getTasksByIcpisManager(icpisName);
    }
}

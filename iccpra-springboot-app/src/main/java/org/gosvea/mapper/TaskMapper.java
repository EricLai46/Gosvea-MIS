package org.gosvea.mapper;

import org.apache.ibatis.annotations.*;
import org.gosvea.pojo.Task;

import java.util.List;

@Mapper
public interface TaskMapper {


    @Insert("insert into task(description, is_completed, icpis_manager) values(#{description},#{isCompleted},#{icpisManager})")
    void addNewTask(Task task);
    @Update("update task set description=#{description},is_completed=#{isCompleted},icpis_manager=#{icpisManager} where id=#{id}")
    void editTask(Task task);
    @Delete("delete from task where id=#{id}")
    void deleteTask(Integer id);
    @Select("select * from task where icpis_manager=#{icpisName}")
    List<Task> getTasksByIcpisManager(String icpisName);

    void insertListTasks(List<Task> taskList);

}

package org.gosvea.controller;

import org.apache.ibatis.annotations.Update;
import org.gosvea.pojo.Result;
import org.gosvea.pojo.Task;
import org.gosvea.service.EmailService;
import org.gosvea.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping("/task")
@CrossOrigin(origins =  { "https://allcprmanage.com"}, allowedHeaders = "*")
public class TaskController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskService taskService;

    @PostMapping
    public Result addNewTask(@RequestBody Task task)
    {
        try{
            taskService.addNewTask(task);
            return Result.success();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
        }
    }

    @GetMapping
    public Result getTasksByIcpisManager(String icpisName)
    {
        try{
            List<Task> newtask=taskService.getTasksByIcpisManager(icpisName);
            System.out.println(newtask);
            return Result.success(newtask);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
        }

    }

    @DeleteMapping
    public Result deleteTask(Integer id)
    {
        try{
            taskService.deleteTask(id);
            return Result.success();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return Result.error(e.getMessage()+"\n"+ getStackTrace(e));
        }
    }

   @PutMapping
   public Result editTask(@RequestBody Task task)
   {
       try{
           taskService.editTask(task);
           return Result.success();
       }
       catch(Exception e)
       {

           e.printStackTrace();
           return Result.error(e.getMessage()+"\n"+getStackTrace(e));
       }

   }

    

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}

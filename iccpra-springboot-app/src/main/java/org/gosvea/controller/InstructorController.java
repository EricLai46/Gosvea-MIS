package org.gosvea.controller;


import org.gosvea.pojo.*;
import org.gosvea.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instructor")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;
    //添加新的instructor
    @PostMapping
    public Result addInstructor(@RequestBody Instructor instructor)
    {
        instructorService.addInstructor(instructor);
        return Result.success("Add instructor successfully");
    }
    //获取页面Instructor信息
    @GetMapping
    public Result<PageResponse<Instructor>> getInstructor(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer instructorId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer wageHour
    )
    {
        try {
            PageResponse<Instructor> ps=instructorService.getInstructor(pageNum,pageSize,state,city,instructorId,phoneNumber,email,wageHour);
            return Result.success(ps);
        }
        catch (Exception e) {
            e.printStackTrace();

            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
        }

    }



    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
  //删除Instructor
   @DeleteMapping
    public Result deleteInstructor(Integer instructorId)
   {
       instructorService.deleteInstructor(instructorId);
       return Result.success("Delete the instructor successfully");
   }
   //获取instructor name
   @GetMapping("/instructorname")
   public List<Map<String,Object>> getInstructorNameList()
   {
       return instructorService.getInstructorNameList();
   }

   //更新Instructor信息
   @PutMapping
    public Result updateInstructor(@RequestBody Instructor instructor)
   {
       try {
           instructorService.updateInstructor(instructor);
           return Result.success("Update the instructor information successfully");
       } catch (Exception e) {
           e.printStackTrace();

           return Result.error(e.getMessage() + "\n" + getStackTrace(e));
       }
   }
   //更新Instructor schedule
   @PutMapping("/schedule")
    public Result updateInstructorSchedule(@RequestBody InstructorSchedule instructorSchedule)
   {

       try {
           instructorService.updateInstructorSchedule(instructorSchedule);
           return Result.success("Update the schedule of instructor successfully");
       } catch (Exception e) {
           e.printStackTrace();
           return Result.error(e.getMessage()+"\n"+getStackTrace(e));
       }
   }

   //获取instructor schedule
   @GetMapping("/schedule")
    public Result<List<InstructorSchedule>> getInstructorSchedule(Integer instructorId)
   {

       try {
           return Result.success(instructorService.getInstructorSchedule(instructorId));
       } catch (Exception e) {
           e.printStackTrace();
           return Result.error((e.getMessage()+"\n"));
       }
   }

   //删除Instructor schedule
    @DeleteMapping("/schedule")
    public Result<InstructorSchedule> deleteInstructorSchedule(Integer id)
    {
        instructorService.deleteInstructorSchedule(id);
        return Result.success();
    }

    //添加新的Instructor Schedule
    @PostMapping("/schedule")
    public Result<InstructorSchedule> addInstructorSchedule(@RequestBody InstructorSchedule instructorSchedule)
    {
        instructorService.addInstructorSchedule(instructorSchedule);
        return Result.success();
    }
}



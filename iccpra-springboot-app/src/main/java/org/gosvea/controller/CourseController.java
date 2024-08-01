package org.gosvea.controller;


import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Result;
import org.gosvea.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public Result<PageResponse<CourseSchedule>> getCourseSchedule(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false)Integer instructorId,
            @RequestParam(required = false)Integer venueId,
            @RequestParam(required = false)LocalDate date,
            @RequestParam(required = false)LocalTime startTime,
            @RequestParam(required = false)LocalTime endTime,
            @RequestParam(required = false)Boolean isActive
            )
    {
        try {
            boolean activeStatus = (isActive != null) ? isActive : false;
            return  Result.success(courseService.getCourseSchedule(pageNum,pageSize,instructorId,venueId,date,startTime,endTime,isActive));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage()+"\n"+getStackTrace(e));
        }
    }

    @PostMapping
    public Result addNewCourse(@RequestBody List<CourseSchedule> courseSchedules)
    {
        try {
            courseService.insertCourseSchedule(courseSchedules);
            return Result.success("Added new course successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage()+"\n"+getStackTrace(e));
        }
    }

    @PutMapping
    public Result updateCourseInformation(@RequestBody CourseSchedule courseSchedule)
    {
        try {
            courseService.updateCourseInformation(courseSchedule);
            return Result.success("Update course information scuuessfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage()+"\n"+getStackTrace(e));
        }
    }

    @DeleteMapping
    public Result deleteCourse(Integer instructorId, Integer venueId)
    {
        courseService.deleteCourse(instructorId,venueId);
        return Result.success("Delete the course successfully");
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}

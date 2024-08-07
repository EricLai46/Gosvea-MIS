package org.gosvea.controller;


import org.gosvea.pojo.*;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private VenueService venueService;

    @Autowired
    private InstructorService instructorService;

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
            checkVenueInstructorInformation();
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


    //检查venue和instructor是否有匹配的时间
    public Result checkVenueInstructorInformation() {
        List<Venue> venues = venueService.getAllVenues();
        List<CourseSchedule> commonSchedules=new ArrayList<>();
        //警告列表
        Collection<String> warnings = new ArrayList<>();

        //添加数据前先清除原有的数据
        courseService.deleteAllSchedule();
        for (Venue venue : venues) {
            Instructor instructor = instructorService.getInstructorById(venue.getInstructor());


            if (instructor == null) {
                warnings.add("Venue ID " + venue.getId() + " does not have an assigned instructor.");
                continue;
            }
            instructor.setScheduleList(instructorService.getInstructorSchedule(venue.getInstructor()));


            // 打印调试信息
//            System.out.println("Venue: " + venue);
//            System.out.println("Instructor: " + instructor);
//            System.out.println("Venue Schedules: " + venue.getScheduleList());
//            System.out.println("Instructor Schedules: " + instructor.getScheduleList());
//            System.out.println("Venue Address: " + venue.getAddress());


            commonSchedules=getCommonSchedules(instructor.getId(),venue.getId(),venue.getAddress(),venue.getScheduleList(),instructor.getScheduleList());
            if (commonSchedules.isEmpty()) {
                warnings.add("Venue ID " + venue.getId() + " does not have matching schedules with its instructor.");
            } else {
                commonSchedules.forEach(courseSchedule -> System.out.println(courseSchedule.toString()));
                courseService.insertCourseSchedule(commonSchedules);
            }
        }

        Result result;
        if (!warnings.isEmpty()) {
            result = Result.success(commonSchedules, warnings);
        } else {
            result = Result.success(commonSchedules);
        }
        //System.out.println(result); // 打印调试信息
        // Debug output
        System.out.println("Result Code: " + result.getCode());
        System.out.println("Result Message: " + result.getMessage());
        System.out.println("Result Data: " + result.getData());
        System.out.println("Result Warnings: " + result.getWarnings());
        return result;
    }
    //获取公共的schedule
    public List<CourseSchedule> getCommonSchedules(Integer instructorId, Integer venueId,String address,List<VenueSchedule> venueSchedules, List<InstructorSchedule> instructorSchedules) {
        List<CourseSchedule> commonSchedules = new ArrayList<>();

        for (VenueSchedule venueSchedule : venueSchedules) {
            LocalDate venueDate = venueSchedule.getDate();
            LocalTime venueStartTime = venueSchedule.getStartTime();
            LocalTime venueEndTime = venueSchedule.getEndTime();
            boolean matchFound = false; // 增加一个标记变量

            for (InstructorSchedule instructorSchedule : instructorSchedules) {
                LocalDate instructorDate = instructorSchedule.getDate();
                if (!venueDate.equals(instructorDate)) {
                    continue;
                }

                LocalTime instructorStartTime = instructorSchedule.getStartTime();
                LocalTime instructorEndTime = instructorSchedule.getEndTime();

                LocalTime commonStartTime = venueStartTime.isAfter(instructorStartTime) ? venueStartTime : instructorStartTime;
                LocalTime commonEndTime = venueEndTime.isBefore(instructorEndTime) ? venueEndTime : instructorEndTime;

                if (!commonStartTime.isAfter(commonEndTime)) {
                    commonSchedules.add(new CourseSchedule(instructorId, venueId, instructorDate, commonStartTime, commonEndTime, address, venueSchedule.getCourseTitle()));
                    matchFound = true; // 找到匹配时设置为 true
                    break; // 跳出内层循环
                }
            }

            if (matchFound) {
                break; // 如果找到匹配的时间段，跳出外层循环
            }
        }
        return commonSchedules;
    }

}

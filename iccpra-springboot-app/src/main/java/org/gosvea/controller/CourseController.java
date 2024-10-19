package org.gosvea.controller;


import org.gosvea.utils.ExcelExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.gosvea.pojo.*;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/icpie/course")
@CrossOrigin(origins =  {"http://54.175.129.180:80", "http://allcprmanage.com"}, allowedHeaders = "*")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private VenueService venueService;

    @Autowired
    private InstructorService instructorService;

//获取课程广告表
   // @PreAuthorize("hasRole('ICPIE')")
    @GetMapping
    public Result<PageResponse<CourseSchedule>> getCourseSchedule(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false)String icpisManager,
            @RequestParam(required = false)String venueId,
            @RequestParam(required = false)LocalDate date,
            @RequestParam(required = false)LocalTime startTime,
            @RequestParam(required = false)LocalTime endTime,
            @RequestParam(required = false)Boolean isActive,
            @RequestParam(required = false)Boolean isProcessed,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate fromDate,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate toDate
            )
    {
        try {
           // Map<Integer,String> warnings=checkVenueInstructorInformation();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 打印用户信息，确保认证信息正确设置
            //System.out.println("Authenticated user: " + authentication.getName());
           // System.out.println("Authorities: " + authentication.getAuthorities());
            boolean activeStatus = (isActive != null) ? isActive : false;
            return  Result.success(courseService.getCourseSchedule(pageNum,pageSize,icpisManager,venueId,date,startTime,endTime,isActive,isProcessed,fromDate,toDate));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage()+"\n"+getStackTrace(e));
        }
    }
//添加一个新课程，广告
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
//更新广告，课程的信息
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

//删除课程，广告的信息
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


//    //检查venue和instructor是否有匹配的时间
//    public Map<Integer,String> checkVenueInstructorInformation() {
//        List<Venue> venues = venueService.getAllVenues();
//        List<CourseSchedule> commonSchedules=new ArrayList<>();
//        //警告列表
//        Map<Integer,String> warnings=new HashMap<Integer,String>();
//
//
//        //添加数据前先清除原有的数据
//        //courseService.deleteAllSchedule();
//        for (Venue venue : venues) {
//            Instructor instructor = instructorService.getInstructorById(venue.getInstructor());
//
//
//            if (instructor == null) {
//                warnings.put(venue.getId(),"Venue ID " + venue.getId() + " does not have an assigned instructor.");
//                //更新场地状态为INSTRUCTORISSUE
//                venueService.updateVenueStatus(venue.getId(),Venue.VenueStatus.INSTRUCTORISSUE);
//
//                //venue.setVenueStatus(Venue.VenueStatus.INSTRUCTORISSUE);
//                continue;
//            }
//            instructor.setScheduleList(instructorService.getInstructorSchedule(venue.getInstructor()));
//
//
//            // 打印调试信息
////            System.out.println("Venue: " + venue);
////            System.out.println("Instructor: " + instructor);
////            System.out.println("Venue Schedules: " + venue.getScheduleList());
////            System.out.println("Instructor Schedules: " + instructor.getScheduleList());
////            System.out.println("Venue Address: " + venue.getAddress());
//
//
//            commonSchedules=getCommonSchedules(instructor.getId(),venue.getId(),venue.getAddress(),venue.getScheduleList(),instructor.getScheduleList());
//            if (commonSchedules.isEmpty()) {
//                warnings.put(venue.getId(),"Venue ID " + venue.getId() + " does not have matching schedules with its instructor.");
//                //更新场地状态为VENUEISSUE
//                venueService.updateVenueStatus(venue.getId(), Venue.VenueStatus.VENUEISSUE);
//            } else {
//                //更新场地状态为NORMAL
//                venueService.updateVenueStatus(venue.getId(),Venue.VenueStatus.NORMAL);
//
//                commonSchedules.forEach(courseSchedule -> System.out.println(courseSchedule.toString()));
//                courseService.insertCourseSchedule(commonSchedules);
//            }
//        }
//
//        Result result;
//        if (!warnings.isEmpty()) {
//            return  warnings;
//        } else {
//            return null;
//        }
//        //System.out.println(result); // 打印调试信息
//        // Debug output
//       // System.out.println("Result Code: " + result.getCode());
//       // System.out.println("Result Message: " + result.getMessage());
//      //  System.out.println("Result Data: " + result.getData());
//       // System.out.println("Result Warnings: " + result.getWarnings());
//
//    }
//    //获取公共的schedule
//    public List<CourseSchedule> getCommonSchedules(Integer instructorId, Integer venueId,String address,List<VenueSchedule> venueSchedules, List<InstructorSchedule> instructorSchedules) {
//        List<CourseSchedule> commonSchedules = new ArrayList<>();
//
//        for (VenueSchedule venueSchedule : venueSchedules) {
//            LocalDate venueDate = venueSchedule.getDate();
//            LocalTime venueStartTime = venueSchedule.getStartTime();
//            LocalTime venueEndTime = venueSchedule.getEndTime();
//            boolean matchFound = false; // 增加一个标记变量
//
//            for (InstructorSchedule instructorSchedule : instructorSchedules) {
//                LocalDate instructorDate = instructorSchedule.getDate();
//                if (!venueDate.equals(instructorDate)) {
//                    continue;
//                }
//
//                LocalTime instructorStartTime = instructorSchedule.getStartTime();
//                LocalTime instructorEndTime = instructorSchedule.getEndTime();
//
//                LocalTime commonStartTime = venueStartTime.isAfter(instructorStartTime) ? venueStartTime : instructorStartTime;
//                LocalTime commonEndTime = venueEndTime.isBefore(instructorEndTime) ? venueEndTime : instructorEndTime;
//
//                if (!commonStartTime.isAfter(commonEndTime)) {
//                    commonSchedules.add(new CourseSchedule(instructorId, venueId, instructorDate, commonStartTime, commonEndTime, address, venueSchedule.getCourseTitle()));
//                    matchFound = true; // 找到匹配时设置为 true
//                    break; // 跳出内层循环
//                }
//            }
//
//            if (matchFound) {
//                break; // 如果找到匹配的时间段，跳出外层循环
//            }
//        }
//        return commonSchedules;
//    }
    //获取当前场地的广告日历
    @GetMapping("/coursecalendar")
    public List<CourseSchedule> getCourseCalendar(String venueId)
    {
        return courseService.getCourseCalendar(venueId);


    }

    //获取所有场地地址以及id
    @GetMapping("/coursevenueIdaddress")
    public List<Venue> getAllVenueIdAndAddress()
    {
        return courseService.getAllVenueIdAndAddress();
    }
    //导出广告Summary
    @GetMapping("/coursesummary")
    public ResponseEntity<byte[]> getCoursesSummary(LocalDate date)
    {
        //通过日期获取24天后的两个礼拜的课程
       List<CourseSchedule> courseScheduleList=courseService.getCourseScheduleSummary(date);
       //导出excel表格
       //ExcelExporter.exportCoursesToExcel(date,courseScheduleList);

        //通过日期获取24天后的两个李白的课程，按照venueid顺序排列，

        List<CourseSchedule> courseScheduleListByVenueId=courseService.getCourseScheduleSummaryByVenueId(date);

        System.out.println("origial: "+courseScheduleListByVenueId);

        //获取所有场地

       Map<String, String> venueList=getAllVenueIdandAddress();


        Map<String,Venue> venueListMap=venueService.getVenueListMap();

        //获取场地，场地状态的hashmap

        Map<String, String> venueStatusmap=getAllVenueStatus();



        // 导出excel表格
        ByteArrayInputStream excelStream = ExcelExporter.exportCoursesToExcel( venueListMap, courseScheduleListByVenueId,venueList,venueStatusmap);



        // 设置响应头，文件名可以根据需要动态生成
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=AD_Summary.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);


            // 将Excel文件流返回给客户端
            byte[] excelBytes = excelStream.readAllBytes();
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(excelBytes);

    }
    public Map<String, String> getAllVenueStatus() {
        List<Map<String, String>> resultList = venueService.getAllVenueStatus();
        Map<String, String> resultMap = new HashMap<>();
        for (Map<String, String> row : resultList) {
            String id = row.get("id");
            String status = row.get("venue_status");

            // 调用 VenueStatus.fromValue() 方法，将数据库中的字符串转换为 VenueStatus 枚举
            Venue.VenueStatus venueStatusEnum = Venue.VenueStatus.fromValue(status);

            // 使用 getValue() 方法获取枚举的字符串表示形式
            resultMap.put(id, venueStatusEnum.getValue());
        }
        return resultMap;
    }


    public Map<String, String> getAllVenueIdandAddress(){
        List<Map<String, String>> resultList = venueService.getAllVenueAddress();
        Map<String, String> resultMap = new HashMap<>();
        for (Map<String, String> row : resultList) {
            String id = row.get("id");
            String address = row.get("address");



            // 使用 getValue() 方法获取枚举的字符串表示形式
            resultMap.put(id, address);
        }
        return resultMap;
    }
}

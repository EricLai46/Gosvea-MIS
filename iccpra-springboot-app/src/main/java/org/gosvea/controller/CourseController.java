package org.gosvea.controller;


import org.gosvea.service.RedisService;
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
@CrossOrigin(origins =  { "https://allcprmanage.com","http://localhost:3000"}, allowedHeaders = "*")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class CourseController {

    @Autowired
    private RedisService redisService;

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
            System.out.println("email address XXXXXXXXX" + redisService.getData("email"));
            System.out.println("name   OOOOOOOOOOOOO" + redisService.getData("name"));
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

        //System.out.println("origial: "+courseScheduleListByVenueId);

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
    //导出广告Summarytable
    @GetMapping("/coursesummaytable")
    public ResponseEntity<List<Map<String, Object>>> getCoursesSummaryTable(LocalDate date) {
        // 获取24天后两周内的课程数据并按 venueId 排序
        List<CourseSchedule> courseScheduleListByVenueId = courseService.getCourseScheduleSummaryByVenueId(date);
        List<CourseSchedule> activedCourseScheduleListByVenueId = courseService.getActivedCourseScheduleSummaryByVenyeId(date);
        System.out.println("Actived Course Schedule List: " + activedCourseScheduleListByVenueId);
        // 获取所有场地
        Map<String, String> venueList = getAllVenueIdandAddress();
        Map<String, Venue> venueListMap = venueService.getVenueListMap();
        Map<String, String> venueStatusMap = getAllVenueStatus();

        // 准备 JSON 格式的列表数据
        List<Map<String, Object>> tableData = new ArrayList<>();
        Map<String, Map<String, Object>> uniqueEntryMap = new HashMap<>();  // 用于存储合并数据
        Map<String, Map<String, Object>> activedEntryMap = new HashMap<>(); // 用于存储活动课程数据

        // 先处理 activedCourseScheduleListByVenueId 生成 activedEntryMap
        for (CourseSchedule activeCourse : activedCourseScheduleListByVenueId) {
            String venueId = activeCourse.getVenueId();
            String courseTitle = (activeCourse.getCourseTitle() != null) ? activeCourse.getCourseTitle().toUpperCase() : "";
            String uniqueKey = venueId + "|" + courseTitle;

            // 检查 activedEntryMap 中是否已存在该记录
            if (activedEntryMap.containsKey(uniqueKey)) {
                Map<String, Object> existingActiveRow = activedEntryMap.get(uniqueKey);
                String existingActiveDates = (String) existingActiveRow.get("实际开课日期");
                existingActiveRow.put("实际开课日期", existingActiveDates + ", " + activeCourse.getDate().toString());

                int updatedActiveAdCount = (int) existingActiveRow.get("实际广告次数") + 1;
                existingActiveRow.put("实际广告次数", updatedActiveAdCount);
            } else {
                Map<String, Object> activeRow = new HashMap<>();
                activeRow.put("实际开课日期", activeCourse.getDate() != null ? activeCourse.getDate().toString() : "No Date");
                activeRow.put("实际广告次数", 1);  // 初始化实际广告次数为1
                activedEntryMap.put(uniqueKey, activeRow);
            }
        }

        // 处理 courseScheduleListByVenueId 并合并 activedEntryMap 的数据
        for (CourseSchedule course : courseScheduleListByVenueId) {
            String venueId = course.getVenueId();
            String courseTitle = (course.getCourseTitle() != null) ? course.getCourseTitle().toUpperCase() : "";
            String uniqueKey = venueId + "|" + courseTitle;

            // 获取地址
            String address = course.getAddress() != null ? course.getAddress() : venueList.getOrDefault(venueId, "地址未知");

            // 检查是否已存在相同的记录
            if (uniqueEntryMap.containsKey(uniqueKey)) {
                // 如果存在相同的记录，合并数据
                Map<String, Object> existingRow = uniqueEntryMap.get(uniqueKey);
                String existingDates = (String) existingRow.get("开课日期计划");
                existingRow.put("开课日期计划", existingDates + ", " + course.getDate().toString());

                int updatedPlanCount = (int) existingRow.get("计划次数") + 1;
                existingRow.put("计划次数", updatedPlanCount);

            } else {
                // 如果不存在相同的记录，创建新条目
                Map<String, Object> row = new HashMap<>();
                row.put("课程编号", course.getId());
                row.put("培训点编码", venueId);
                row.put("地址", address);
                row.put("课程名称", courseTitle);
                row.put("时区", course.getTimeZone());
                row.put("AllCPR售价", course.getPrice());
                row.put("开课日期计划", course.getDate() != null ? course.getDate().toString() : "No Date");
                row.put("计划次数", 1);  // 初始化计划次数为1
                row.put("备注", course.isEnrollwareAdded() ? "TRUE" : "FALSE");
                row.put("负责人", venueListMap.getOrDefault(venueId, new Venue()).getIcpisManager());
                row.put("补打广告次数", course.isEnrollwareAdded() ? "TRUE" : "FALSE");
                row.put("发布人", venueListMap.getOrDefault(venueId, new Venue()).getIcpisManager());
                row.put("场地状态", venueStatusMap.getOrDefault(venueId, "N/A"));

                // 从 activedEntryMap 获取 "实际开课日期" 和 "实际广告次数"
                if (activedEntryMap.containsKey(uniqueKey)) {
                    Map<String, Object> activeData = activedEntryMap.get(uniqueKey);
                    row.put("实际开课日期", activeData.get("实际开课日期"));
                    row.put("实际广告次数", activeData.get("实际广告次数"));
                } else {
                    // 如果没有活动数据，则初始化为 "No Date" 和 0
                    row.put("实际开课日期", "No Date");
                    row.put("实际广告次数", 0);
                }

                // 添加到 uniqueEntryMap 和 tableData
                uniqueEntryMap.put(uniqueKey, row);
                tableData.add(row);
            }
        }

        // 对 tableData 按 "培训点编码" 排序
        tableData.sort(Comparator.comparing(row -> (String) row.get("培训点编码")));

        return ResponseEntity.ok().body(tableData);
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

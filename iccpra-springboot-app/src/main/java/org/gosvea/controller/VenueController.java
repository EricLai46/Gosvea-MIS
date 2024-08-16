package org.gosvea.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.gosvea.pojo.*;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
import org.gosvea.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/venue")
//@CrossOrigin(origins = "http://54.175.129.180:80", allowedHeaders = "*")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @Autowired
    private CourseService courseService;


    @Autowired
    private InstructorService instructorService;

    //    @GetMapping("/list")
//    public Result<String> lists(@RequestHeader(name="Authorization") String token, HttpServletResponse response){
//        try {
//            Map<String,Object> claims=JwtUtil.parseToken(token);
//            return Result.success("All venue information");
//        } catch (Exception e) {
//            //http response code:401
//            response.setStatus(401);
//            return Result.error("not login");
//        }
//    }
    //添加新场地
    @PostMapping
    public Result add(@RequestBody Venue venue) {
        try {
            //生成新场地
            venueService.add(venue);
            //生成新场地默认schedule
            //venueService.addVenueSchedule(venue.getId());
            return Result.success();
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();

            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
        }
    }

    //获取场地信息
    @GetMapping
    public Result<PageResponse<Venue>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer instructor,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String timeZone) {

        try {
            PageResponse<Venue> ps = venueService.list(pageNum, pageSize, state, city, instructor, paymentMethod, timeZone);
            return Result.success(ps);
        } catch (Exception e) {
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

    //更新场地
    @PutMapping
    public Result updateVenue(@RequestBody Venue venue) {

        try {
            venueService.updateVenue(venue);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();

            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
        }
    }

    //删除场地
    @DeleteMapping
    public Result deleteVenue(Integer venueId) {
        try {
            //删除场地
            venueService.deleteVenue(venueId);
            //删除场地schedule
            venueService.deleteVenueSchedule(venueId);
            return Result.success("Delete venue successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n");
        }
    }

    //更新场地schedule
    @PutMapping("/schedule")
    public Result updateVenueSchedule(@RequestBody VenueSchedule venueSchedule) {
        LocalDate date = venueSchedule.getDate();
        LocalTime startTime = venueSchedule.getStartTime();
        LocalTime endTime = venueSchedule.getEndTime();

        try {

            venueService.updateVenueSchedule(date, startTime, endTime, venueSchedule.getVenueId());
           // checkVenueInstructorInformation();
            return Result.success("update schedule succcessfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n");
        }
    }

    //获取场地schedule
    @GetMapping("/schedule")
    public Result<List<VenueSchedule>> getVenueSchedule(Integer venueId) {

        try {
            return Result.success(venueService.getVenueSchedule(venueId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n");
        }
    }
  //获取所有场地信息from map
    @GetMapping("/venuemap")
     public Result<List<Venue>> getAllVenueFromMap()
    {
        List<Venue> venues=venueService.getAllVenues();

        //System.out.println(venues);
        for(Venue venue : venues)
        {
            double[] latlon=venueService.getLatLon(venue.getAddress());
            if(latlon!=null){
                venue.setLatitude(latlon[0]);
                venue.setLongitude(latlon[1]);
                venueService.saveLatLon(latlon,venue.getId());
            }

        }
        return Result.success(venues);
    }

    //添加新场地schedule
    @PostMapping("/schedule")
    public Result<VenueSchedule> addVenueSchedule(@RequestBody VenueSchedule venueSchedule) {
        venueService.addVenueSchedule(venueSchedule);
       // checkVenueInstructorInformation();
        return Result.success();
    }

    //删除场地schedule
    @DeleteMapping("/schedule")
    public Result<VenueSchedule> deleteVenueScheduleSingle(Integer id) {
        venueService.deleteVenueScheduleSingle(id);
        //checkVenueInstructorInformation();
        return Result.success();
    }

    //文件上传
    @PostMapping("/import")
    public ResponseEntity<?> importVenueData(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            //venueService.clearAllData(); // 清除所有现有数据
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // 跳过表头
                }
                Venue venue = new Venue();
                String state = row.getCell(1).getStringCellValue();
                String city = row.getCell(2).getStringCellValue();
                String address = row.getCell(3).getStringCellValue();
                String uniqueId = generateUniqueId(state, city, address);
                //venue.setId(uniqueId);
                venue.setState(state);
                venue.setCity(city);
                venue.setAddress(address);
                // 设置其他属性...
                venueService.add(venue);
            }
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"error\": \"An error occurred while processing the file.\"}");
        }
    }

    //生成id
    public String generateUniqueId(String state, String city, String address) throws NoSuchAlgorithmException {
        String input = state + city + address;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String fullHash = formatter.toString();
        return fullHash.substring(0, 8); // 返回前八位作为唯一ID
    }

    //文件导出
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportVenueData() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Venues");
            // 创建表头
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("State");
            header.createCell(2).setCellValue("City");
            header.createCell(3).setCellValue("Address");
            // 其他表头...

            // 从数据库获取数据
            List<Venue> venues = new ArrayList<Venue>();
            int rowIdx = 1;
            for (Venue venue : venues) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(venue.getId());
                row.createCell(1).setCellValue(venue.getState());
                row.createCell(2).setCellValue(venue.getCity());
                row.createCell(3).setCellValue(venue.getAddress());
                // 其他列...
            }

            workbook.write(out);
            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

//    //检查venue和instructor是否有匹配的时间
//    public Result checkVenueInstructorInformation() {
//        List<Venue> venues = venueService.getAllVenues();
//        List<CourseSchedule> commonSchedules=new ArrayList<>();
//        //警告列表
//        List<String> warnings = new ArrayList<>();
//        //添加数据前先清除原有的数据
//        courseService.deleteAllSchedule();
//        for (Venue venue : venues) {
//            Instructor instructor = instructorService.getInstructorById(venue.getInstructor());
//
//
//            if (instructor == null) {
//                warnings.add("Venue ID " + venue.getId() + " does not have an assigned instructor.");
//                continue;
//            }
//            instructor.setScheduleList(instructorService.getInstructorSchedule(venue.getInstructor()));
//
//
//            // 打印调试信息
//            System.out.println("Venue: " + venue);
//            System.out.println("Instructor: " + instructor);
//            System.out.println("Venue Schedules: " + venue.getScheduleList());
//            System.out.println("Instructor Schedules: " + instructor.getScheduleList());
//            System.out.println("Venue Address: " + venue.getAddress());
//
//
//            commonSchedules=getCommonSchedules(instructor.getId(),venue.getId(),venue.getAddress(),venue.getScheduleList(),instructor.getScheduleList());
//            if (commonSchedules.isEmpty()) {
//                warnings.add("Venue ID " + venue.getId() + " does not have matching schedules with its instructor.");
//            } else {
//                commonSchedules.forEach(courseSchedule -> System.out.println(courseSchedule.toString()));
//                courseService.insertCourseSchedule(commonSchedules);
//            }
//        }
//
//        if (!warnings.isEmpty()) {
//            // 返回警告信息到前端
//            return Result.error("Course search failed!", warnings);
//
//        }
//
//        return Result.success();
//    }
//   //获取公共的schedule
//    public List<CourseSchedule> getCommonSchedules(Integer instructorId, Integer venueId,String address,List<VenueSchedule> venueSchedules, List<InstructorSchedule> instructorSchedules) {
//        List<CourseSchedule> commonSchedules = new ArrayList<>();
//
//        for (VenueSchedule venueSchedule : venueSchedules) {
//            LocalDate venueDate = venueSchedule.getDate();
//            LocalTime venueStartTime = venueSchedule.getStartTime();
//            LocalTime venueEndTime = venueSchedule.getEndTime();
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
//                    commonSchedules.add(new CourseSchedule(instructorId,venueId,instructorDate,commonStartTime,commonEndTime,address,venueSchedule.getCourseTitle()));
//                }
//            }
//        }
//        return commonSchedules;
//    }



}
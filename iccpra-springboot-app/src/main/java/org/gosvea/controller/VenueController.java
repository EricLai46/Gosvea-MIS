package org.gosvea.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.gosvea.pojo.*;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
import org.gosvea.utils.JwtUtil;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSession;
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
@CrossOrigin(origins =  {"http://54.175.129.180:80", "http://allcprmanage.com","http://localhost:3000"}, allowedHeaders = "*")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    private String[] headers={"ID","State","City","Address","Instructor","Time Zone","Time Zone","Cancellation Policy","Payment Mode",
                              "Nonrefundable Fee","Fob Key","Deposit","Membership Fee","Usage Fee","Refundable Status","Book Method",
                               "Registration Link","Venue Status"};
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

        //需要更新update的Venue list表
        List<Venue> updateVenuesList=new ArrayList<>();
        //需要添加的Venue List表
        List<Venue> insertVenuesList=new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            //venueService.clearAllData(); // 清除所有现有数据
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // 跳过表头
                }
                Venue venue = new Venue();
                   // 处理ID
                if (row.getCell(0) != null) {
                    if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                        venue.setId((int) row.getCell(0).getNumericCellValue());
                    } else if (row.getCell(0).getCellType() == CellType.STRING) {
                        venue.setId(Integer.parseInt(row.getCell(0).getStringCellValue().trim()));
                    }
                }

// 处理State
                if (row.getCell(1) != null) {
                    if (row.getCell(1).getCellType() == CellType.STRING) {
                        venue.setState(row.getCell(1).getStringCellValue().trim());
                    } else {
                        // 处理其他可能的情况，比如State是数值类型
                        venue.setState(String.valueOf(row.getCell(1).getNumericCellValue()));
                    }
                }

// 处理City
                if (row.getCell(2) != null) {
                    if (row.getCell(2).getCellType() == CellType.STRING) {
                        venue.setCity(row.getCell(2).getStringCellValue().trim());
                    } else {
                        venue.setCity(String.valueOf(row.getCell(2).getNumericCellValue()));
                    }
                }

// 处理Address
                if (row.getCell(3) != null) {
                    if (row.getCell(3).getCellType() == CellType.STRING) {
                        venue.setAddress(row.getCell(3).getStringCellValue().trim());
                    } else {
                        venue.setAddress(String.valueOf(row.getCell(3).getNumericCellValue()));
                    }
                }

                if (row.getCell(4) != null && !row.getCell(4).getStringCellValue().isEmpty()) {
                    String fullName = row.getCell(4).getStringCellValue().trim();
                    String[] nameParts = fullName.split(" ");
                    if (nameParts.length == 2) {
                        String firstName = nameParts[0];
                        String lastName = nameParts[1];
                        Integer instructorId = instructorService.findIdByName(firstName, lastName);
                        if (instructorId != null) {
                            venue.setInstructor(instructorId);
                        }
                    }
                }

                venue.setTimeZone(row.getCell(5).getStringCellValue().trim());
                venue.setCancellationPolicy(row.getCell(6) != null ? row.getCell(6).getStringCellValue().trim() : null);
                venue.setPaymentMode(row.getCell(7) != null ? row.getCell(7).getStringCellValue().trim() : null);

                // 检查nonrefundableFee单元格类型
                if (row.getCell(8) != null) {
                    if (row.getCell(8).getCellType() == CellType.NUMERIC) {
                        venue.setNonrefundableFee(row.getCell(8).getNumericCellValue());
                    } else if (row.getCell(8).getCellType() == CellType.STRING) {
                        venue.setNonrefundableFee(Double.valueOf(row.getCell(8).getStringCellValue().trim()));
                    }
                } else {
                    venue.setNonrefundableFee(0.0);  // 默认值
                }

                if (row.getCell(9) != null) {
                    if (row.getCell(9).getCellType() == CellType.NUMERIC) {
                        venue.setFobKey(String.valueOf(row.getCell(9).getNumericCellValue()));
                    } else if (row.getCell(9).getCellType() == CellType.STRING) {
                        venue.setFobKey(row.getCell(9).getStringCellValue().trim());
                    }
                }

                if (row.getCell(10) != null) {
                    if (row.getCell(10).getCellType() == CellType.NUMERIC) {
                        venue.setDeposit(row.getCell(10).getNumericCellValue());
                    } else if (row.getCell(10).getCellType() == CellType.STRING) {
                        venue.setDeposit(Double.valueOf(row.getCell(10).getStringCellValue().trim()));
                    }
                }

                if (row.getCell(11) != null) {
                    if (row.getCell(11).getCellType() == CellType.NUMERIC) {
                        venue.setMembershipFee(row.getCell(11).getNumericCellValue());
                    } else if (row.getCell(11).getCellType() == CellType.STRING) {
                        venue.setMembershipFee(Double.valueOf(row.getCell(11).getStringCellValue().trim()));
                    }
                }

                if (row.getCell(12) != null) {
                    if (row.getCell(12).getCellType() == CellType.NUMERIC) {
                        venue.setUsageFee(row.getCell(12).getNumericCellValue());
                    } else if (row.getCell(12).getCellType() == CellType.STRING) {
                        venue.setUsageFee(Double.valueOf(row.getCell(12).getStringCellValue().trim()));
                    }
                }

                venue.setRefundableStatus(row.getCell(13) != null ? row.getCell(13).getStringCellValue().trim() : null);
                venue.setBookMethod(row.getCell(14) != null ? row.getCell(14).getStringCellValue().trim() : null);
                venue.setRegistrationLink(row.getCell(15) != null ? row.getCell(15).getStringCellValue().trim() : null);

                if (row.getCell(16) != null) {
                    String statusString = row.getCell(16).getStringCellValue().trim().toUpperCase();
                    try {
                        Venue.VenueStatus status = Venue.VenueStatus.valueOf(statusString);
                        venue.setVenueStatus(status);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unknown venue status: " + statusString);
                        venue.setVenueStatus(null);
                    }
                } else {
                    venue.setVenueStatus(null);
                }

                Venue existingVenue = venueService.getVenueById(venue.getId());
                if (existingVenue != null) {
                    updateVenuesList.add(venue);

                } else {
                    insertVenuesList.add(venue);

                }
            }

            //循环结束分别添加，更新venuelist
            if(!insertVenuesList.isEmpty())
            {
                System.out.println(insertVenuesList);
                venueService.insertListVenues(insertVenuesList);
            }


            if(!updateVenuesList.isEmpty())
            {
                //System.out.println(updateVenuesList);

                venueService.updateListVenues(updateVenuesList);
            }


            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            e.printStackTrace();
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

            for(int i=0;i<headers.length;i++) {

                header.createCell(i).setCellValue(headers[i]);
            }


            // 从数据库获取数据
            List<Venue> venues = venueService.getAllVenues();
            int rowIdx = 1;
            for (Venue venue : venues) {
                Row row = sheet.createRow(rowIdx++);
                System.out.println(venue);
                row.createCell(0).setCellValue(venue.getId() != null ? String.valueOf(venue.getId()) : "1");
                row.createCell(1).setCellValue(venue.getState() != null ? venue.getState() : "");
                row.createCell(2).setCellValue(venue.getCity() != null ? venue.getCity() : "");
                row.createCell(3).setCellValue(venue.getAddress() != null ? venue.getAddress() : "");
                String instructorName = "";
                if (venue.getInstructor() != null) {
                    Instructor instructor = instructorService.getInstructorById(venue.getInstructor());
                    if (instructor != null) {
                        instructorName = instructor.getFirstname() + " " + instructor.getLastname();
                    }
                }
                row.createCell(4).setCellValue(instructorName);
                row.createCell(5).setCellValue(venue.getTimeZone() != null ? venue.getTimeZone() : "");
                row.createCell(6).setCellValue(venue.getCancellationPolicy() != null ? venue.getCancellationPolicy() : "");
                row.createCell(7).setCellValue(venue.getPaymentMode() != null ? venue.getPaymentMode() : "");
                row.createCell(8).setCellValue(venue.getNonrefundableFee() != null ? venue.getNonrefundableFee() : 0.0);
                row.createCell(9).setCellValue(venue.getFobKey() != null ? venue.getFobKey()  : "");
                row.createCell(10).setCellValue(venue.getDeposit()!= null ? venue.getDeposit(): 0.0);
                row.createCell(11).setCellValue(venue.getMembershipFee() != null ? venue.getMembershipFee(): 0.0);
                row.createCell(12).setCellValue(venue.getUsageFee()!= null ? venue.getUsageFee() : 0.0);
                row.createCell(13).setCellValue(venue.getRefundableStatus() != null ? venue.getRefundableStatus() : "");
                row.createCell(14).setCellValue(venue.getBookMethod()!= null ? venue.getBookMethod() : "");
                row.createCell(15).setCellValue(venue.getRegistrationLink() != null ? venue.getRegistrationLink() : "");
                row.createCell(16).setCellValue(String.valueOf(venue.getVenueStatus()!= null ? venue.getVenueStatus() : ""));

            }

            workbook.write(out);
            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=venues.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(file);
        } catch (Exception e) {
            e.printStackTrace();
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
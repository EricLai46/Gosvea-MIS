package org.gosvea.controller;


import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gosvea.pojo.*;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instructor")
@CrossOrigin(origins =  {"http://54.175.129.180:80", "http://allcprmanage.com","http://localhost:3000"}, allowedHeaders = "*")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private VenueService venueService;


    private String[] headers={"ID","Venue","FirstName","LastName", "State","City","PhoneNumber","Email","WagePerHour","TotalClassTimes",
                               "Deposit","Rent Manikin Numbers","Finance","Rent Status","Fob Key"};

    @Autowired
    private CourseService courseService;
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
            @RequestParam(required = false) Integer wageHour,
            @RequestParam(required = false) String venueId,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname
    )
    {
        try {
            PageResponse<Instructor> ps=instructorService.getInstructor(pageNum,pageSize,state,city,instructorId,phoneNumber,email,wageHour,venueId,firstname,lastname);
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


   //通过instructorid来获取 instructor信息
   @GetMapping("/instructorid")
   public Instructor getInstructorById(Integer instructor)
   {
       return instructorService.getInstructorById(instructor);
   }


   //更新Instructor信息
   @PutMapping
    public Result updateInstructor(@RequestBody Instructor instructor)
   {
       try {
           instructorService.updateInstructor(instructor);
           String venueId=instructor.getVenueId();
           if(venueId!=null)
           {
               courseService.generateOrUpdateCourseSchedules(venueId,instructor.getId());
           }

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
           //更新检查course ,ad表
          // courseService.checkVenueInstructorInformation();

           //checkVenueInstructorInformation();
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
        //checkVenueInstructorInformation();
        return Result.success();
    }

    //添加新的Instructor Schedule
    @PostMapping("/schedule")
    public Result<InstructorSchedule> addInstructorSchedule(@RequestBody InstructorSchedule instructorSchedule)
    {
        instructorService.addInstructorSchedule(instructorSchedule);
        //checkVenueInstructorInformation();
        return Result.success();
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
            List<Instructor> instructors = instructorService.getAllInstructors();
            int rowIdx = 1;
            for (Instructor instructor : instructors) {
                Row row = sheet.createRow(rowIdx++);
                System.out.println(instructor);
                row.createCell(0).setCellValue(instructor.getId() != null ? String.valueOf(instructor.getId()) : "1");
                String venueName = "";
                if (instructor.getVenueId() != null) {
                    Venue venue = venueService.getVenueById(instructor.getVenueId());
                    if (venue != null) {
                        venueName = venue.getAddress();
                    }
                }
                row.createCell(1).setCellValue(venueName);
                row.createCell(2).setCellValue(instructor.getFirstname() != null ? instructor.getFirstname() : "");
                row.createCell(3).setCellValue(instructor.getLastname() != null ? instructor.getLastname() : "");
                row.createCell(4).setCellValue(instructor.getState() != null ? instructor.getState() : "");
                row.createCell(5).setCellValue(instructor.getCity() != null ? instructor.getCity() : "");
                row.createCell(6).setCellValue(instructor.getPhoneNumber() != null ? instructor.getPhoneNumber() : "");
                row.createCell(7).setCellValue(instructor.getEmail() != null ? instructor.getEmail()  : "");
                row.createCell(8).setCellValue(instructor.getWageHour() != null ? String.valueOf(instructor.getWageHour()): "");
                row.createCell(9).setCellValue(instructor.getTotalClassTimes() != null ? String.valueOf(instructor.getTotalClassTimes())   : "");
                row.createCell(10).setCellValue(instructor.getDeposit()!= null ? String.valueOf(instructor.getDeposit()): "");
                row.createCell(11).setCellValue(instructor.getRentManikinNumbers() != null ? String.valueOf(instructor.getRentManikinNumbers()): "");
                row.createCell(12).setCellValue(instructor.getFinance()!= null ? String.valueOf(instructor.getFinance()) : "");
                row.createCell(13).setCellValue(instructor.getRentStatus() != null ? instructor.getRentStatus()  : "");
                row.createCell(14).setCellValue(instructor.getFobKey()!= null ? instructor.getFobKey() : "");

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

    //文件上传
    @PostMapping("/import")
    public ResponseEntity<?> importInstructorData(@RequestParam("file") MultipartFile file) {

        //需要更新update的Instructor list表
        List<Instructor> updateVenuesList=new ArrayList<>();
        //需要添加的Instructor List表
        List<Instructor> insertVenuesList=new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            //venueService.clearAllData(); // 清除所有现有数据
            instructorService.clearAllData();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // 跳过表头
                }
                Instructor instructor = new Instructor();
                // 处理ID
                if (row.getCell(0) != null) {
                    if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                        instructor.setId((int) row.getCell(0).getNumericCellValue());
                    } else if (row.getCell(0).getCellType() == CellType.STRING) {
                        instructor.setId(Integer.parseInt(row.getCell(0).getStringCellValue().trim()));
                    }
                }

// 处理venue Id
                if (row.getCell(1) != null && !row.getCell(1).getStringCellValue().isEmpty()) {
                    String address = row.getCell(1).getStringCellValue().trim();


                        String venueId = venueService.getVenueIdByAddress(address);
                        if (venueId != null)
                            instructor.setVenueId(venueId);


                }

// 处理firstname
                if (row.getCell(2) != null) {
                    if (row.getCell(2).getCellType() == CellType.STRING) {
                        instructor.setFirstname(row.getCell(2).getStringCellValue().trim());
                    } else {
                        instructor.setFirstname(String.valueOf(row.getCell(2).getNumericCellValue()));
                    }
                }

// 处理lastname
                if (row.getCell(3) != null) {
                    if (row.getCell(3).getCellType() == CellType.STRING) {
                        instructor.setLastname(row.getCell(3).getStringCellValue().trim());
                    } else {
                        instructor.setLastname(String.valueOf(row.getCell(3).getNumericCellValue()));
                    }
                }

//处理state
                instructor.setState(row.getCell(4) != null ? row.getCell(4).getStringCellValue().trim() : null);
                //处理city
                instructor.setCity(row.getCell(5) != null ? row.getCell(5).getStringCellValue().trim() : null);
                //处理phonenumber
                if (row.getCell(6) != null) {
                    if (row.getCell(6).getCellType() == CellType.STRING) {
                        instructor.setPhoneNumber(row.getCell(6).getStringCellValue().trim());
                    } else {
                        instructor.setPhoneNumber(String.valueOf(row.getCell(6).getNumericCellValue()));
                    }
                }

               //处理email
                instructor.setEmail(row.getCell(7) != null ? row.getCell(7).getStringCellValue().trim() : null);
                //处理wagehour
                if (row.getCell(8) != null) {
                    if (row.getCell(8).getCellType() == CellType.STRING) {
                        instructor.setWageHour(row.getCell(8).getStringCellValue().trim());
                    } else {
                        instructor.setWageHour(String.valueOf(row.getCell(8).getNumericCellValue()));
                    }
                }
                //处理salaryinfo
                if (row.getCell(9) != null) {
                    if (row.getCell(9).getCellType() == CellType.STRING) {
                        instructor.setSalaryInfo(row.getCell(9).getStringCellValue().trim());
                    } else {
                        instructor.setSalaryInfo(String.valueOf(row.getCell(9).getNumericCellValue()));
                    }
                }
                //处理totalclasstimes
                if (row.getCell(10) != null) {
                    if (row.getCell(10).getCellType() == CellType.STRING) {
                        instructor.setTotalClassTimes(Integer.valueOf(row.getCell(10).getStringCellValue().trim()));
                    } else {
                        instructor.setTotalClassTimes((int)(row.getCell(10).getNumericCellValue()));
                    }
                }
                //处理deposit
                if (row.getCell(11) != null) {
                    if (row.getCell(11).getCellType() == CellType.STRING) {
                        instructor.setDeposit(row.getCell(11).getStringCellValue().trim());
                    } else {
                        instructor.setDeposit(String.valueOf(row.getCell(11).getNumericCellValue()));
                    }
                }
                //处理rentmanikinnumbers
                if (row.getCell(12) != null) {
                    if (row.getCell(12).getCellType() == CellType.STRING) {
                        instructor.setRentManikinNumbers(row.getCell(12).getStringCellValue().trim());
                    } else {
                        instructor.setRentManikinNumbers(String.valueOf(row.getCell(12).getNumericCellValue()));
                    }
                }
                //处理finance
                if (row.getCell(13) != null) {
                    if (row.getCell(13).getCellType() == CellType.STRING) {
                        instructor.setFinance(row.getCell(13).getStringCellValue().trim());
                    } else {
                        instructor.setFinance(String.valueOf(row.getCell(13).getNumericCellValue()));
                    }
                }
                //处理rent status
                if (row.getCell(14) != null) {
                    if (row.getCell(14).getCellType() == CellType.STRING) {
                        instructor.setRentStatus(row.getCell(14).getStringCellValue().trim());
                    } else {
                        instructor.setRentStatus(String.valueOf(row.getCell(14).getNumericCellValue()));
                    }
                }
               //处理fobkey
                if (row.getCell(15) != null) {
                    if (row.getCell(15).getCellType() == CellType.STRING) {
                        instructor.setFobKey(row.getCell(15).getStringCellValue().trim());
                    } else {
                        instructor.setFobKey(String.valueOf(row.getCell(15).getNumericCellValue()));
                    }
                }



                Instructor existingInstructor = instructorService.getInstructorById(instructor.getId());
                if (existingInstructor != null) {
                    updateVenuesList.add(instructor);

                } else {
                    insertVenuesList.add(instructor);

                }
            }

            //循环结束分别添加，更新venuelist
            if(!insertVenuesList.isEmpty())
            {
                System.out.println(insertVenuesList);
               instructorService.insertListInstructors(insertVenuesList);
            }


            if(!updateVenuesList.isEmpty())
            {
                //System.out.println(updateVenuesList);

               instructorService.updateListInstructors(updateVenuesList);
            }


            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"success\": false, \"error\": \"An error occurred while processing the file.\"}");
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
//    //获取公共的schedule
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



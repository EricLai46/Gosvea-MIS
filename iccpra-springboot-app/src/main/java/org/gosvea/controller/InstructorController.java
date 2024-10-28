package org.gosvea.controller;


import ch.qos.logback.core.joran.spi.ElementSelector;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gosvea.dto.InstructorDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instructor")
@CrossOrigin(origins =  { "https://allcprmanage.com"}, allowedHeaders = "*")
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
            String role,
            String icpisname,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String instructorId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String wageHour,
            @RequestParam(required = false) String venueId,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname
    )
    {
        try {

            PageResponse<Instructor> ps=new PageResponse<>();
            if(role.equals("ROLE_ICPIE"))
            {
                ps=instructorService.getInstructor(pageNum,pageSize,state,city,instructorId,phoneNumber,wageHour,venueId,firstname,lastname);
            }
            else if(role.equals("ROLE_ICPIS"))
            {
                ps=instructorService.getInstructorByIcpisName(pageNum,pageSize,state,city,instructorId,phoneNumber,venueId,firstname,lastname,icpisname);
            }
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
    public Result deleteInstructor(String instructorId)
   {
       instructorService.deleteInstructor(instructorId);
       return Result.success("Delete the instructor successfully");
   }
   //获取instructor name
   @GetMapping("/instructorname")
   public List<Map<String,Object>> getInstructorNameList(@RequestParam String role,@RequestParam(required = false) String icpisName)
   {
            if(role.equals("ROLE_ICPIE"))
            {
                return instructorService.getInstructorNameList();
            }
            else
            {
                return instructorService.getInstructorNameListByIcpis(icpisName);
            }
   }


   //通过instructorid来获取 instructor信息
   @GetMapping("/instructorid")
   public Instructor getInstructorById(String instructor)
   {
       return instructorService.getInstructorById(instructor);
   }


   //更新Instructor信息
   @PutMapping
    public Result updateInstructor(@RequestBody InstructorDTO instructorDTO)
   {
//       try {
//           instructorService.updateInstructor(instructor);
//          List<Venue> currentVenues=venueService.getVenueByInstructorId(instructor.getId());
//          List<Venue> newVenues=instructor.getVenues();
//
//          if(instructorService.isVenueListChanged(currentVenues,newVenues))
//          {
//              venueService.deleteInstructorVenueRelationsByInstructorId(instructor.getId());
//
//              if(newVenues!=null&&!newVenues.isEmpty())
//              {
//                  for(Venue venue:newVenues)
//                  {
//                      venueService.addInstructorVenueRelation(instructor.getId(),venue.getId());
//                  }
//              }
//          }
//          if(newVenues!=null&&!newVenues.isEmpty())
//          {
//              for(Venue venue:newVenues)
//              {
//                  courseService.generateOrUpdateCourseSchedules(venue.getId(), instructor.getId());
//              }
//          }
//
//           return Result.success("Update the instructor information successfully");
//       } catch (Exception e) {
//           e.printStackTrace();
//
//           return Result.error(e.getMessage() + "\n" + getStackTrace(e));
//       }
       try {
           // 获取现有的 Venue 实体
           //System.out.println(instructorDTO);
           Instructor instructor = instructorService.getInstructorById(instructorDTO.getId());
           //System.out.println("venue "+instructor);
           List<String> instructorDTOvenueIds=instructorDTO.getVenueIds();
         if(instructorDTOvenueIds!=null&&!instructorDTOvenueIds.isEmpty())
         {
             for(String venueDToInstructorId:instructorDTOvenueIds) {
                 Venue venue = venueService.getVenueById(venueDToInstructorId);
                 if (venue != null) {
                     courseService.generateOrUpdateCourseSchedules(venue.getId(), instructor.getId());
                 }
             }
         }
            instructor.setFirstname(instructorDTO.getFirstname());
            instructor.setLastname(instructorDTO.getLastname());
            instructor.setPhoneNumber(instructorDTO.getPhoneNumber());
            instructor.setState(instructorDTO.getState());
            instructor.setCity(instructorDTO.getCity());
            instructor.setEmail(instructorDTO.getEmail());
            instructor.setWageHour(instructorDTO.getWageHour());
            instructor.setSalaryInfo(instructorDTO.getSalaryInfo());
            instructor.setFobKey(instructorDTO.getFobKey());
            instructor.setFinance(instructorDTO.getFinance());
            instructor.setDeposit(instructorDTO.getDeposit());
            instructor.setRentManikinNumbers(instructorDTO.getRentManikinNumbers());
            instructor.setTotalClassTimes(instructorDTO.getTotalClassTimes());
            instructor.setRentStatus(instructorDTO.getRentStatus());
           instructorService.updateInstructor(instructor); return Result.success();
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
    public Result<List<InstructorSchedule>> getInstructorSchedule(String instructorId)
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
    public Result<InstructorSchedule> deleteInstructorSchedule(String id)
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

            Sheet sheet = workbook.createSheet("Instructors");

            // 创建表头
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            // 从数据库获取所有讲师
            List<Instructor> instructors = instructorService.getAllInstructors();
            int rowIdx = 1;

            // 遍历每个讲师
            for (Instructor instructor : instructors) {
                Row row = sheet.createRow(rowIdx++);

                // 获取与讲师关联的多个场地
                List<Venue> venues = venueService.getVenueByInstructorId(instructor.getId());
                StringBuilder venueNames = new StringBuilder();

                // 将多个场地的地址组合成一个字符串
                if (venues != null && !venues.isEmpty()) {
                    for (Venue venue : venues) {
                        if (venueNames.length() > 0) {
                            venueNames.append(", ");
                        }
                        venueNames.append(venue.getAddress());
                    }
                }

                // 填写讲师及其关联的场地信息
                row.createCell(0).setCellValue(instructor.getId() != null ? String.valueOf(instructor.getId()) : "1");
                row.createCell(1).setCellValue(venueNames.toString());
                row.createCell(2).setCellValue(instructor.getFirstname() != null ? instructor.getFirstname() : "");
                row.createCell(3).setCellValue(instructor.getLastname() != null ? instructor.getLastname() : "");
                row.createCell(4).setCellValue(instructor.getState() != null ? instructor.getState() : "");
                row.createCell(5).setCellValue(instructor.getCity() != null ? instructor.getCity() : "");
                row.createCell(6).setCellValue(instructor.getPhoneNumber() != null ? instructor.getPhoneNumber() : "");
                row.createCell(7).setCellValue(instructor.getEmail() != null ? instructor.getEmail() : "");
                row.createCell(8).setCellValue(instructor.getWageHour() != null ? String.valueOf(instructor.getWageHour()) : "");
                row.createCell(9).setCellValue(instructor.getTotalClassTimes() != null ? String.valueOf(instructor.getTotalClassTimes()) : "");
                row.createCell(10).setCellValue(instructor.getDeposit() != null ? String.valueOf(instructor.getDeposit()) : "");
                row.createCell(11).setCellValue(instructor.getRentManikinNumbers() != null ? String.valueOf(instructor.getRentManikinNumbers()) : "");
                row.createCell(12).setCellValue(instructor.getFinance() != null ? String.valueOf(instructor.getFinance()) : "");
                row.createCell(13).setCellValue(instructor.getRentStatus() != null ? instructor.getRentStatus() : "");
                row.createCell(14).setCellValue(instructor.getFobKey() != null ? instructor.getFobKey() : "");
            }

            // 写入数据到输出流
            workbook.write(out);
            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=instructors.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(file);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    //文件上传
//    @PostMapping("/import")
//    public ResponseEntity<?> importInstructorData(@RequestParam("file") MultipartFile file) {
//        // 需要更新的 Instructor 列表
//        List<Instructor> updateInstructorsList = new ArrayList<>();
//        // 需要插入的 Instructor 列表
//        List<Instructor> insertInstructorsList = new ArrayList<>();
//        // 存放中间表关联的 venue_id 和 instructor_id
//        Map<String, List<String>> instructorVenueMap = new HashMap<>();
//
//        try (InputStream inputStream = file.getInputStream();
//             Workbook workbook = new XSSFWorkbook(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) {
//                    continue; // 跳过表头
//                }
//
//                Instructor instructor = new Instructor();
//
//                // 处理 ID
//                if (row.getCell(0) != null) {
//                    instructor.setId(getStringCellValue(row.getCell(0)));
//                }
//
//                // 处理 venue Id, 可能是多对多关系，所以需要处理多个场地
//                if (row.getCell(1) != null && !row.getCell(1).getStringCellValue().isEmpty()) {
//                    String venueAddress = row.getCell(1).getStringCellValue().trim();
//                    String venueId = venueService.getVenueIdByAddress(venueAddress);
//
//                    if (venueId != null) {
//                        instructorVenueMap.computeIfAbsent(instructor.getId(), k -> new ArrayList<>()).add(venueId);
//                    }
//                }
//
//                // 处理其它 Instructor 字段
//                instructor.setFirstname(getStringCellValue(row.getCell(2)));
//                instructor.setLastname(getStringCellValue(row.getCell(3)));
//                instructor.setState(getStringCellValue(row.getCell(4)));
//                instructor.setCity(getStringCellValue(row.getCell(5)));
//                instructor.setPhoneNumber(getStringCellValue(row.getCell(6)));
//                instructor.setEmail(getStringCellValue(row.getCell(7)));
//                instructor.setWageHour(getStringCellValue(row.getCell(8)));
//                instructor.setSalaryInfo(getStringCellValue(row.getCell(9)));
//                instructor.setTotalClassTimes(getIntCellValue(row.getCell(10)));
//                instructor.setDeposit(getStringCellValue(row.getCell(11)));
//                instructor.setRentManikinNumbers(getStringCellValue(row.getCell(12)));
//                instructor.setFinance(getStringCellValue(row.getCell(13)));
//                instructor.setRentStatus(getStringCellValue(row.getCell(14)));
//                instructor.setFobKey(getStringCellValue(row.getCell(15)));
//
//                // 检查讲师是否已经存在
//                Instructor existingInstructor = instructorService.getInstructorById(instructor.getId());
//                if (existingInstructor != null) {
//                    updateInstructorsList.add(instructor);
//                } else {
//                    insertInstructorsList.add(instructor);
//                }
//            }
//
//            // 批量插入新讲师
//            if (!insertInstructorsList.isEmpty()) {
//                instructorService.insertListInstructors(insertInstructorsList);
//            }
//
//            // 批量更新现有讲师
//            if (!updateInstructorsList.isEmpty()) {
//                instructorService.updateListInstructors(updateInstructorsList);
//            }
//
//            // 更新讲师与场地的关联关系 (instructor_venue 表)
//            for (Map.Entry<String, List<String>> entry : instructorVenueMap.entrySet()) {
//                String instructorId = entry.getKey();
//                List<String> venueIds = entry.getValue();
//
//                // 删除旧的关联
//                venueService.deleteInstructorVenueRelationsByInstructorId(instructorId);
//
//                // 插入新的关联
//                for (String venueId : venueIds) {
//                    venueService.addInstructorVenueRelation(instructorId, venueId);
//                }
//            }
//
//            return ResponseEntity.ok().body("{\"success\": true}");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("{\"success\": false, \"error\": \"An error occurred while processing the file.\"}");
//        }
//    }

    private String getStringCellValue(Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            }
        }
        return null;
    }

    private Integer getIntCellValue(Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
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



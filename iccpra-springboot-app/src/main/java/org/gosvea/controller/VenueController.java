package org.gosvea.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.usermodel.*;
import org.gosvea.dto.VenueDTO;
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
@CrossOrigin(origins =  {"http://54.175.129.180:80", "http://allcprmanage.com"}, allowedHeaders = "*")
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

    //检测场地id是否重复
    @GetMapping("/verifyVenueId/{id}")
    public  Boolean verifyVenueId(@PathVariable String id)
    {
        try{
           return venueService.verifyVenueId(id);
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }





    //添加新场地
    @PostMapping
    public Result add(@RequestBody Venue venue) {
        try {
            // 生成新场地
            venueService.add(venue);
            System.out.println("venue"+venue.getInstructors());
            // 获取关联的讲师列表
//            List<Instructor> instructors = venue.getInstructors();
//            if (instructors != null && !instructors.isEmpty()) {
//                // 遍历每个讲师，建立与场地的关联
//                for (Instructor instructor : instructors) {
//                    // 确保讲师已存在于数据库中，如果没有，需要先添加讲师
//                    Instructor existingInstructor = instructorService.getInstructorById(instructor.getId());
//                    if (existingInstructor == null) {
//                        // 处理讲师不存在的情况，可能需要抛出异常或添加讲师
//                        return Result.error("Instructor with ID " + instructor.getId() + " does not exist.");
//                    }
//                    // 建立场地和讲师之间的关联关系
//                    venueService.addInstructorVenueRelation(venue.getId(), instructor.getId());
//                }
//            }
            List<String> instructorIds=new ArrayList<>();
            List<Instructor> instructorList=venue.getInstructors();

            if(instructorList!=null)
            {
                for(Instructor instructor:instructorList)
                {
                    String instructorid=instructorService.getInstructorIdByInstructorName(instructor.getFirstname(),instructor.getLastname());
                    if(instructorid!=null)
                    {
                        instructorIds.add(instructorid);
                    }
                }
            }



            venueService.addInstructorVenueRelation(instructorIds, venue.getId());
            // 如果需要生成新场地的默认日程，可以在这里调用
            // venueService.addVenueSchedule(venue.getId());

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
        }
    }

    //获取场地信息
    @GetMapping
    public Result<PageResponse<Venue>> list(
            Integer pageNum,
            Integer pageSize,
            String role,
            String icpisname,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String icpisManager,
            @RequestParam(required = false) String timeZone,
            @RequestParam(required = false) String venueId) {

        try {
            PageResponse<Venue> ps = new PageResponse<>();
            if(role.equals("ROLE_ICPIE"))
            {
                ps = venueService.list(pageNum, pageSize, state, city, icpisManager, timeZone,venueId);
            }
            else if (role.equals("ROLE_ICPIS"))
            {
                ps=venueService.icpislist(pageNum,pageSize,state,city,icpisname,timeZone,venueId);
            }

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
    public Result updateVenue(@RequestBody VenueDTO venueDTO) {
        try {
            // 获取现有的 Venue 实体
            System.out.println("venueDTO"+venueDTO);
            Venue venue = venueService.getVenueById(venueDTO.getId());
            System.out.println("venue "+venue);
            List<String> venueDTOInstructorIds=venueDTO.getInstructorIds();
            venueService.updtaeLatLonInformationForOneVenue(venue);

            List<String> previousInstructorIds=instructorService.getInstructorIdsByVenueId(venue.getId());
            System.out.println("previous instructor Id"+previousInstructorIds);
            //检查是否改变了instructor和venue的关系
            if(venueService.isInstructrorListChanged(venueDTOInstructorIds,previousInstructorIds))
            {
                //删除旧的关联
                venueService.deleteInstructorVenueRelationsByVenueId(venueDTO.getId());
                //添加新的关联

                    venueService.addInstructorVenueRelation(venueDTOInstructorIds, venue.getId());


            }

           List<String>  afterInstructorIds=instructorService.getInstructorIdsByVenueId(venue.getId());
            //重新生成新的course schedule
            for(String afterInstructorId:afterInstructorIds)
            {
                Instructor instructor=instructorService.getInstructorById(afterInstructorId);
                if(instructor!=null)
                {
                    //生成course schedule
                    courseService.generateOrUpdateCourseSchedules(venue.getId(), instructor.getId());

                }
            }
//            if(venue.getInstructors()!=null)
//            {
//                System.out.println("venue instructors"+venue.getInstructors());
//                // 获取当前 Venue 关联的 Instructor 列表
//                List<Instructor> currentInstructors = venue.getInstructors();
//                // 处理 instructorIds 列表
//                List<Instructor> newInstructors = new ArrayList<>();
//                for (String instructorId : venueDTO.getInstructorIds()) {
//                    List<Instructor> instructors = instructorService.getInstructorById(instructorId);
//                    if (instructors != null && !instructors.isEmpty()) {
//                        for (Instructor instructor : instructors) {
//                            newInstructors.add(instructor);
//                            courseService.generateOrUpdateCourseSchedules(venue.getId(), instructor.getId());
//                        }
//                    }
//                }
//
//                // 检查是否 Instructor 列表发生变化
//                if (venueService.isInstructrorListChanged(currentInstructors, newInstructors)) {
//                    // 删除旧的关联关系
//                    venueService.deleteInstructorVenueRelationsByVenueId(venue.getId());
//                    System.out.println("aa");
//                    // 添加新的关联关系
//                    for (Instructor instructor : newInstructors) {
//                        venueService.addInstructorVenueRelation(instructor.getId(), venue.getId());
//                        System.out.println("ss");
//                    }
//                }
//
//                // 更新 Venue 实体的 Instructor 列表
//                venue.setInstructors(newInstructors);
//            }


            // 更新 Venue
            venue.setIcpisManager(venueDTO.getIcpisManager());
            venue.setAddress(venueDTO.getAddress());
            venueService.updtaeLatLonInformationForOneVenue(venue);
            venueService.updateVenue(venue);

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
        }
    }
    //删除场地
    @DeleteMapping
    public Result deleteVenue(String venueId) {
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

            //更新course schedule

           // courseService.checkVenueInstructorInformation();

           // checkVenueInstructorInformation();
            return Result.success("update schedule succcessfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n");
        }
    }

    //获取场地schedule
    @GetMapping("/schedule")
    public Result<List<VenueSchedule>> getVenueSchedule(String venueId) {

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
//        for(Venue venue : venues)
//        {
//            double[] latlon=venueService.getLatLon(venue.getAddress());
//            if(latlon!=null){
//                venue.setLatitude(latlon[0]);
//                venue.setLongitude(latlon[1]);
//                venueService.saveLatLon(latlon,venue.getId());
//            }
//
//        }
        List<Venue> venueHasLaLon=new ArrayList<>();
        for(Venue venue:venues)
        {
            if(venue.getLatitude()!=null&&venue.getLongitude()!=null)
            {
                venueHasLaLon.add(venue);
            }
        }
        System.out.println("The Venue hasLalon"+venueHasLaLon);
        return Result.success(venueHasLaLon);
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
    public Result<VenueSchedule> deleteVenueScheduleSingle(String id) {
        venueService.deleteVenueScheduleSingle(id);
        //checkVenueInstructorInformation();
        return Result.success();
    }

    //文件上传
    @PostMapping("/import")
    public ResponseEntity<?> importVenueData(@RequestParam("file") MultipartFile file) {

        List<Venue> updateVenuesList = new ArrayList<>();
// 需要添加的Venue List表
        List<Venue> insertVenuesList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 跳过表头并开始处理实际数据行
            for (Row row : sheet) {
                if (row.getRowNum() == 0 || isRowEmpty(row)) {
                    continue; // 跳过表头和空行
                }

                Venue venue = new Venue();

                // 处理ID
                Cell idCell = row.getCell(0);
                if (idCell != null && idCell.getCellType() != CellType.BLANK) {
                    String id;
                    if (idCell.getCellType() == CellType.NUMERIC) {
                        id = String.valueOf(idCell.getNumericCellValue());
                    } else if (idCell.getCellType() == CellType.STRING) {
                        id = idCell.getStringCellValue().trim();
                    } else {
                        id = "UNKNOWN";
                        System.out.println("Row " + row.getRowNum() + " ID has an unexpected cell type: " + idCell.getCellType());
                    }
                    System.out.println("Row " + row.getRowNum() + " ID: " + id);
                    venue.setId(id);
                } else {
                    System.out.println("Row " + row.getRowNum() + " ID cell is null or empty");
                    continue; // 如果ID为空或null，跳过该行
                }

                // 处理ICPISManager
                Cell icpisManagerCell = row.getCell(1);
                if (icpisManagerCell != null && icpisManagerCell.getCellType() != CellType.BLANK) {
                    if (icpisManagerCell.getCellType() == CellType.NUMERIC) {
                        venue.setIcpisManager(String.valueOf(icpisManagerCell.getNumericCellValue()));
                    } else if (icpisManagerCell.getCellType() == CellType.STRING) {
                        venue.setIcpisManager(icpisManagerCell.getStringCellValue().trim());
                    }
                }

                // 处理State
                Cell stateCell = row.getCell(2);
                if (stateCell != null && stateCell.getCellType() != CellType.BLANK) {
                    if (stateCell.getCellType() == CellType.STRING) {
                        venue.setState(stateCell.getStringCellValue().trim());
                    } else {
                        venue.setState(String.valueOf(stateCell.getNumericCellValue()));
                    }
                }

                // 处理City
                Cell cityCell = row.getCell(3);
                if (cityCell != null && cityCell.getCellType() != CellType.BLANK) {
                    if (cityCell.getCellType() == CellType.STRING) {
                        venue.setCity(cityCell.getStringCellValue().trim());
                    } else {
                        venue.setCity(String.valueOf(cityCell.getNumericCellValue()));
                    }
                }

                // 处理Address
                Cell addressCell = row.getCell(4);
                if (addressCell != null && addressCell.getCellType() != CellType.BLANK) {
                    if (addressCell.getCellType() == CellType.STRING) {
                        venue.setAddress(addressCell.getStringCellValue().trim());
                    } else {
                        venue.setAddress(String.valueOf(addressCell.getNumericCellValue()));
                    }
                }

                // 处理Instructor
                Cell instructorCell = row.getCell(5);
                if (instructorCell != null && instructorCell.getCellType() == CellType.STRING && !instructorCell.getStringCellValue().isEmpty()) {
                    String fullName = instructorCell.getStringCellValue().trim();
                    String[] nameParts = fullName.split(" ");
                    if (nameParts.length == 2) {
                        String firstName = nameParts[0];
                        String lastName = nameParts[1];
                        String instructorId = instructorService.findIdByName(firstName, lastName);
                        Instructor instructor=instructorService.getInstructorById(instructorId);
                        if(instructor!=null)
                        {

                                venue.getInstructors().add(instructor);

                        }
                    }
                }

                // 处理TimeZone
                Cell timeZoneCell = row.getCell(6);
                if (timeZoneCell != null && timeZoneCell.getCellType() != CellType.BLANK) {
                    String timeZone = timeZoneCell.getStringCellValue().trim();
                    System.out.println("Time zone at row " + row.getRowNum() + ": " + timeZone);
                    venue.setTimeZone(timeZone);
                } else {
                    System.out.println("Time zone cell is null or empty at row " + row.getRowNum());
                    venue.setTimeZone(""); // 或者设置为默认的时区
                }

                // 处理其他字段...
                venue.setCancellationPolicy(row.getCell(7) != null ? row.getCell(7).getStringCellValue().trim() : null);
                venue.setPaymentMode(row.getCell(8) != null ? row.getCell(8).getStringCellValue().trim() : null);

                if (row.getCell(9) != null) {
                    if (row.getCell(9).getCellType() == CellType.NUMERIC) {
                        venue.setNonrefundableFee(String.valueOf(row.getCell(9).getNumericCellValue()));
                    } else if (row.getCell(9).getCellType() == CellType.STRING) {
                        venue.setNonrefundableFee(row.getCell(9).getStringCellValue().trim());
                    }
                } else {
                    venue.setNonrefundableFee("N/A");  // 默认值
                }

                if (row.getCell(10) != null) {
                    if (row.getCell(10).getCellType() == CellType.NUMERIC) {
                        venue.setFobKey(String.valueOf(row.getCell(10).getNumericCellValue()));
                    } else if (row.getCell(10).getCellType() == CellType.STRING) {
                        venue.setFobKey(row.getCell(10).getStringCellValue().trim());
                    }
                }

                if (row.getCell(11) != null) {
                    if (row.getCell(11).getCellType() == CellType.NUMERIC) {
                        venue.setDeposit(String.valueOf(row.getCell(11).getNumericCellValue()));
                    } else if (row.getCell(11).getCellType() == CellType.STRING) {
                        venue.setDeposit(row.getCell(11).getStringCellValue().trim());
                    }
                }

                if (row.getCell(12) != null) {
                    if (row.getCell(12).getCellType() == CellType.NUMERIC) {
                        venue.setMembershipFee(String.valueOf(row.getCell(12).getNumericCellValue()));
                    } else if (row.getCell(12).getCellType() == CellType.STRING) {
                        venue.setMembershipFee(row.getCell(12).getStringCellValue().trim());
                    }
                }

                if (row.getCell(13) != null) {
                    if (row.getCell(13).getCellType() == CellType.NUMERIC) {
                        venue.setUsageFee(String.valueOf(row.getCell(13).getNumericCellValue()));
                    } else if (row.getCell(13).getCellType() == CellType.STRING) {
                        venue.setUsageFee(row.getCell(13).getStringCellValue().trim());
                    }
                }

                venue.setRefundableStatus(row.getCell(14) != null ? row.getCell(14).getStringCellValue().trim() : null);
                venue.setBookMethod(row.getCell(15) != null ? row.getCell(15).getStringCellValue().trim() : null);
                venue.setRegistrationLink(row.getCell(16) != null ? row.getCell(16).getStringCellValue().trim() : null);

                if (row.getCell(17) != null) {
                    String statusString = row.getCell(17).getStringCellValue().trim().toUpperCase();
                    try {
                        Venue.VenueStatus status = Venue.VenueStatus.valueOf(statusString);
                        venue.setVenueStatus(status);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unknown venue status: " + statusString);
                        venue.setVenueStatus(Venue.VenueStatus.NORMAL);
                    }
                } else {
                    venue.setVenueStatus(Venue.VenueStatus.NORMAL);
                }

                // 根据ID检查现有的venue是更新还是插入
                Venue existingVenue = venueService.getVenueById(venue.getId());
                if (existingVenue != null) {
                    updateVenuesList.add(venue);
                } else {
                    insertVenuesList.add(venue);
                }
            }

            // 循环结束后分别添加和更新venue列表
            if (!insertVenuesList.isEmpty()) {
                System.out.println(insertVenuesList);
                venueService.insertListVenues(insertVenuesList);
                venueService.addLatLonInformationForListVenues(insertVenuesList);
            }

            if (!updateVenuesList.isEmpty()) {
                // System.out.println(updateVenuesList);
                venueService.updateListVenues(updateVenuesList);
                for(Venue venue:updateVenuesList)
                {
                    venueService.updtaeLatLonInformationForOneVenue(venue);
                }
            }

            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"success\": false, \"error\": \"An error occurred while processing the file.\"}");
        }
    }

    // 辅助方法：检查行是否为空
    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
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

            // 创建工作表
            Sheet sheet = workbook.createSheet("Venues");

            // 创建表头
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            // 从数据库获取数据
            List<Venue> venues = venueService.getAllVenues();
            int rowIdx = 1;

            // 遍历每个场地
            for (Venue venue : venues) {
                Row row = sheet.createRow(rowIdx++);

                // 填写基本信息
                row.createCell(0).setCellValue(venue.getId() != null ? String.valueOf(venue.getId()) : "1");
                row.createCell(1).setCellValue(venue.getState() != null ? venue.getState() : "");
                row.createCell(2).setCellValue(venue.getCity() != null ? venue.getCity() : "");
                row.createCell(3).setCellValue(venue.getAddress() != null ? venue.getAddress() : "");

                // 获取并处理关联的讲师
                List<Instructor> instructors = instructorService.getInstructorsByVenueId(venue.getId());
                StringBuilder instructorNames = new StringBuilder();
                if (instructors != null && !instructors.isEmpty()) {
                    for (Instructor instructor : instructors) {
                        if (instructorNames.length() > 0) {
                            instructorNames.append(", ");
                        }
                        instructorNames.append(instructor.getFirstname()).append(" ").append(instructor.getLastname());
                    }
                }
                row.createCell(4).setCellValue(instructorNames.toString());

                // 填写其余的字段
                row.createCell(5).setCellValue(venue.getTimeZone() != null ? venue.getTimeZone() : "");
                row.createCell(6).setCellValue(venue.getCancellationPolicy() != null ? venue.getCancellationPolicy() : "");
                row.createCell(7).setCellValue(venue.getPaymentMode() != null ? venue.getPaymentMode() : "");
                row.createCell(8).setCellValue(venue.getNonrefundableFee() != null ? venue.getNonrefundableFee() : "N/A");
                row.createCell(9).setCellValue(venue.getFobKey() != null ? venue.getFobKey() : "");
                row.createCell(10).setCellValue(venue.getDeposit() != null ? venue.getDeposit() : "N/A");
                row.createCell(11).setCellValue(venue.getMembershipFee() != null ? venue.getMembershipFee() : "N/A");
                row.createCell(12).setCellValue(venue.getUsageFee() != null ? venue.getUsageFee() : "N/A");
                row.createCell(13).setCellValue(venue.getRefundableStatus() != null ? venue.getRefundableStatus() : "");
                row.createCell(14).setCellValue(venue.getBookMethod() != null ? venue.getBookMethod() : "");
                row.createCell(15).setCellValue(venue.getRegistrationLink() != null ? venue.getRegistrationLink() : "");
                row.createCell(16).setCellValue(String.valueOf(venue.getVenueStatus() != null ? venue.getVenueStatus() : ""));
            }

            // 写入输出流
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

    //获取所有Venue status为Normal的场地
    @GetMapping("/normal")
    public Result<PageResponse<Venue>> getNormalStatusVenues( Integer pageNum,
                                                              Integer pageSize, @RequestParam(required = false) String state,
                                                             @RequestParam(required = false) String timeZone)
    {

        try {
            PageResponse<Venue> ps = venueService.getNormalStatusVenues(pageNum, pageSize, state,timeZone);
            return Result.success(ps);
        } catch (Exception e) {
            e.printStackTrace();

            return Result.error(e.getMessage() + "\n" + getStackTrace(e));
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
package org.gosvea.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.Venue;
import org.gosvea.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelExporter {


    public static ByteArrayInputStream exportCoursesToExcel(Map<String,Venue> venueListMap, List<CourseSchedule> courses, Map<String,String> venuelist, Map<String,String> venueidVenueStatusmap) {


        System.out.println("所有课程及场地"+courses);
        // 按venueId和课程名称分组（包含所有课程）
        Map<String, Map<String, List<CourseSchedule>>> allGroupedData = new HashMap<>();

    // 按venueId和课程名称分组（只包含isActive为true的课程）
        Map<String, Map<String, List<CourseSchedule>>> activeGroupedData = new HashMap<>();
        // 用于计数venueId和courseTitle组合的出现次数
        Map<String, Integer> countMap = new HashMap<>();
        Map<String,Integer> activelyCountMap=new HashMap<>();
        //计数课程场地的分别日期
        Map<String,String> coursedateMap=new HashMap<>();
        Map<String,Integer> activeCountMap=new HashMap<>();
        //计数active课程场地的分别日期
        Map<String,String> activeCoursedateMap=new HashMap<>();

// 遍历courseSchedules列表，进行分组
        for (CourseSchedule schedule : courses) {
            String venueId = schedule.getVenueId();
            String courseTitle;
            if(schedule.getCourseTitle()!=null)
            {
                courseTitle = schedule.getCourseTitle().toUpperCase();
            }
            else
            {
                courseTitle=schedule.getCourseTitle();
            }


            // ----------------------- 分组1：所有课程 ----------------------- //
            // 如果外部Map中没有当前venueId，则添加一个新的Map
            allGroupedData.putIfAbsent(venueId, new HashMap<>());

            // 获取当前venueId对应的内部Map（按课程名称分组的Map）
            Map<String, List<CourseSchedule>> allCourseMap = allGroupedData.get(venueId);

            // 如果内部Map中没有当前课程名称，则添加一个新的List
            allCourseMap.putIfAbsent(courseTitle, new ArrayList<>());

            // 生成唯一键，用于计数
            String uniqueKey = venueId + "|" + courseTitle;

            // 更新该组合的计数
            countMap.put(uniqueKey, countMap.getOrDefault(uniqueKey, 0) + 1);

            if (coursedateMap.containsKey(uniqueKey)) {
                // 如果 key 已存在，拼接日期
                String existingDates = coursedateMap.get(uniqueKey);
                String newDate = schedule.getDate().toString();

                // 如果需要拼接，使用逗号分隔
                coursedateMap.put(uniqueKey, existingDates + ", " + newDate);
            }
            else {
                    // 如果 key 不存在，检查日期是否为 null
                if (schedule.getDate() != null) {
                    // 如果日期不为 null，添加日期字符串
                    coursedateMap.put(uniqueKey, schedule.getDate().toString());
                } else {
                    // 如果日期为 null，添加一个占位符或执行其他逻辑
                    coursedateMap.put(uniqueKey, "No Date");  // 或者你可以选择用其他合适的值代替
                }
            }
            // 遍历已经存在的课程，检查是否有相同的venueId和courseTitle
            boolean isDuplicate = false;
            for (CourseSchedule existingSchedule : allCourseMap.get(courseTitle==null?courseTitle:courseTitle.toUpperCase())) {
                if (existingSchedule.getVenueId().equals(schedule.getVenueId())
                        && existingSchedule.getCourseTitle().toUpperCase().equals(schedule.getCourseTitle().toUpperCase())) {
                    isDuplicate = true;
                    break;
                }
            }

            // 如果没有重复，才添加到列表
            if (!isDuplicate) {

                //allCourseMap.get(courseTitle.toUpperCase()).add(schedule);
                if(courseTitle!=null)
                {
                    allCourseMap.get(courseTitle.toUpperCase()).add(schedule);
                }
                else
                {
                    allCourseMap.get(courseTitle).add(schedule);
                }
            }


            // ----------------------- 分组2：只包含isActive为true的课程 ----------------------- //
            if (schedule.isActive()) {
                // 如果外部Map中没有当前venueId，则添加一个新的Map
                activeGroupedData.putIfAbsent(venueId, new HashMap<>());

                // 获取当前venueId对应的内部Map（按课程名称分组的Map）
                Map<String, List<CourseSchedule>> activeCourseMap = activeGroupedData.get(venueId);

                // 如果内部Map中没有当前课程名称，则添加一个新的List
                activeCourseMap.putIfAbsent(courseTitle, new ArrayList<>());

                // 将当前课程加入相应的List
                activeCourseMap.get(courseTitle).add(schedule);
                // 生成唯一键，用于计数
                String activeuniqueKey = venueId + "|" + courseTitle;



                // 更新该组合的计数
               activeCountMap.put(activeuniqueKey, activeCountMap.getOrDefault(activeuniqueKey, 0) + 1);

                if (activeCoursedateMap.containsKey(uniqueKey)) {
                    // 如果 key 已存在，拼接日期
                    String existingDates = activeCoursedateMap.get(uniqueKey);
                    String newDate = schedule.getDate().toString();

                    // 如果需要拼接，使用逗号分隔
                    activeCoursedateMap.put(uniqueKey, existingDates + ", " + newDate);
                }
                else {
                    // 如果 key 不存在，直接添加新的日期
                   activeCoursedateMap.put(uniqueKey, schedule.getDate().toString());
                }
            }
        }
       System.out.println("已打广告的"+activeGroupedData);
        System.out.println("计划打广告的"+coursedateMap);
//        System.out.println("场地状态"+venueidVenueStatusmap);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Courses");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] columns = {"课程编号", "培训点编码", "地址","课程名称", "时区", "AllCPR售价", "开课日期计划", "计划次数", "备注", "负责人", "实际开课日期", "实际广告次数", "备注", "发布人","场地状态"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);

                // 设置表头样式
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(headerStyle);
            }

//            // 填充数据
//            int rowIdx = 1;
//            for (String adAddress : allGroupedData.keySet()) {
//                Map<String, List<CourseSchedule>> courseMap = allGroupedData.get(adAddress);
//
//                for (String courseTitle : courseMap.keySet()) {
//                    List<CourseSchedule> courseList = courseMap.get(courseTitle);
//                    // 计算相同address和coursetitle的课程总数
//                    int planTimes = courseList.size();  // 计划次数为相同课程的总数
//                    for (CourseSchedule course : courseList) {
//                        Row row = sheet.createRow(rowIdx++);
//
//                        // 填充每列的数据
//                        row.createCell(0).setCellValue(course.getId().toString());   // 课程编号
//                        row.createCell(1).setCellValue(course.getVenueId());    // 培训点编码
//                        row.createCell(2).setCellValue(course.getAddress());    //城市
//                        row.createCell(3).setCellValue(course.getCourseTitle());    // 课程名称
//                        row.createCell(4).setCellValue(course.getTimeZone());   // 时区
//                        row.createCell(5).setCellValue(course.getPrice()); // ALLCPR售价
//
//                        // 广告计划
////                        StringBuilder adPlans = new StringBuilder();
////                        List<CourseSchedule> dss = allGroupedData.get(course.getVenueId()).get(course.getCourseTitle());
////                        System.out.println("course信息" + dss);
////
////                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
////
////                        for (int i = 0; i < dss.size(); i++) {
////                            if (i > 0) {
////                                adPlans.append(", ");  // 在每个日期之间添加逗号和空格
////                            }
////                            adPlans.append(dss.get(i).getDate().format(formatter));  // 使用DateTimeFormatter格式化日期
////                        }
//                        row.createCell(6).setCellValue(coursedateMap.get(course.getVenueId()+"|"+course.getCourseTitle()));   // 开班日期计划
//                        row.createCell(7).setCellValue(countMap.get(course.getVenueId()+"|"+course.getCourseTitle()));    // 计划次数
//                        row.createCell(8).setCellValue(course.isEnrollwareAdded());  // 备注
//                        row.createCell(9).setCellValue(course.getIcpisManager());   // 负责人
//
//                        // 检查是否有对应的 activeGroupedData 数据
//                        if (activeGroupedData.containsKey(adAddress) && activeGroupedData.get(adAddress).containsKey(courseTitle)) {
//                            List<CourseSchedule> activeCourseList = activeGroupedData.get(adAddress).get(courseTitle);
//                            int activePlanTimes = activeCourseList.size();  // 计算计划次数
//
//                            // 将 activeGroupedData 的数据添加到右侧列
//                            row.createCell(10).setCellValue(activeCoursedateMap.get(course.getVenueId()+"|"+course.getCourseTitle()));   // 实际开班日期计划
//                            row.createCell(11).setCellValue(activeCountMap.get(course.getVenueId()+"|"+course.getCourseTitle()));    // 实际广告次数
//                            row.createCell(12).setCellValue(course.isEnrollwareAdded()); // 补打广告次数
//                            row.createCell(13).setCellValue(course.getIcpisManager());   // 发布人
//                            row.createCell(14).setCellValue(venueidVenueStatusmap.get(course.getVenueId()));  //获取当前场地的状态
//                        }
//                    }
//                }
//            }
            int rowIdx = 1;
            for (String adAddress : allGroupedData.keySet()) {  // 遍历所有的 venue
                Map<String, List<CourseSchedule>> courseMap = allGroupedData.get(adAddress);

                if (courseMap == null || courseMap.isEmpty()) {
                    // 如果没有课程，生成空行
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(1).setCellValue(adAddress); // 培训点编码
                    //row.createCell(13).setCellValue(venueidVenueStatusmap.get(adAddress)); // 场地状态

                    String venueStatus = venueidVenueStatusmap.get(adAddress);

                    row.createCell(13).setCellValue(venueStatus != null ? venueStatus : "N/A"); // 场地状态
                    // 其他列留空
                } else {
                    for (String courseTitle : courseMap.keySet()) {
                        List<CourseSchedule> courseList = courseMap.get(courseTitle);
                        // 计算相同address和coursetitle的课程总数
                        int planTimes = courseList.size();  // 计划次数为相同课程的总数
                        for (CourseSchedule course : courseList) {
                            Row row = sheet.createRow(rowIdx++);

                            // 填充每列的数据
                            row.createCell(0).setCellValue(course.getId() != null ? course.getId().toString() : "");   // 课程编号
                            row.createCell(1).setCellValue(course.getVenueId() != null ? course.getVenueId() : adAddress);    // 培训点编码
                            //row.createCell(2).setCellValue(course.getAddress() != null ? course.getAddress() : venuelist.get(adAddress));    // 地址
                            row.createCell(3).setCellValue(course.getCourseTitle() != null ? course.getCourseTitle() : "");    // 课程名称
                            row.createCell(4).setCellValue(course.getTimeZone() != null ? course.getTimeZone() : "");   // 时区
                            row.createCell(5).setCellValue(course.getPrice() != null ? Double.parseDouble(course.getPrice().toString()) : 0.0); // ALLCPR售价
                            row.createCell(6).setCellValue(
                                    coursedateMap.get(course.getVenueId() + "|" + (course.getCourseTitle() != null ? course.getCourseTitle().toUpperCase() : "")) != null
                                            ? coursedateMap.get(course.getVenueId() + "|" + (course.getCourseTitle() != null ? course.getCourseTitle().toUpperCase() : ""))+" "+course.getStartTime()+'-'+course.getEndTime()
                                            : ""
                            );
                            //row.createCell(6).setCellValue(coursedateMap.get(course.getVenueId() + "|" + course.getCourseTitle().toUpperCase()) != null ? coursedateMap.get(course.getVenueId() + "|" + course.getCourseTitle().toUpperCase()) : "");   // 开班日期计划
                            //row.createCell(7).setCellValue(countMap.get(course.getVenueId() + "|" + course.getCourseTitle()) != null ? countMap.get(course.getVenueId() + "|" + course.getCourseTitle()) : 0);    // 计划次数

                            if(coursedateMap.get(course.getVenueId() + "|" + (course.getCourseTitle() != null ? course.getCourseTitle().toUpperCase() : "<No Course Title>")) != null)
                            {
                                if(coursedateMap.get(course.getVenueId() + "|" + (course.getCourseTitle() != null ? course.getCourseTitle().toUpperCase() : "<No Course Title>")).equals("No Date"))
                                {
                                    row.createCell(7).setCellValue(0);
                                }
                                else
                                {
                                    row.createCell(7).setCellValue(countMap.get(course.getVenueId() + "|" + (course.getCourseTitle() != null ? course.getCourseTitle().toUpperCase() : "<No Course Title>")));
                                }
                            }

                            row.createCell(8).setCellValue(course.isEnrollwareAdded() ? "TRUE" : "FALSE");  // 备注
                            row.createCell(9).setCellValue(venueListMap.get(adAddress).getIcpisManager());   // 负责人

                            // 检查是否有对应的 activeGroupedData 数据
                            if (activeGroupedData.containsKey(adAddress) && activeGroupedData.get(adAddress).containsKey(courseTitle)) {
                                List<CourseSchedule> activeCourseList = activeGroupedData.get(adAddress).get(courseTitle);
                                int activePlanTimes = activeCourseList.size();  // 计算计划次数

                                // 将 activeGroupedData 的数据添加到右侧列
                                row.createCell(10).setCellValue(activeCoursedateMap.get(course.getVenueId() + "|" + course.getCourseTitle().toUpperCase()));   // 实际开班日期计划
                                row.createCell(11).setCellValue(activeCountMap.get(course.getVenueId() + "|" + course.getCourseTitle().toUpperCase()));    // 实际广告次数
                                row.createCell(12).setCellValue(course.isEnrollwareAdded() ? "TRUE" : "FALSE"); // 补打广告次数

                                //System.out.println("adadress"+adAddress);
                                 // 获取当前场地的状态
                            }
                            row.createCell(2).setCellValue(course.getAddress() != null ? course.getAddress() : venuelist.get(adAddress));    // 地址
                            row.createCell(13).setCellValue(venueListMap.get(adAddress).getIcpisManager());   // 发布人
                            row.createCell(14).setCellValue(venueidVenueStatusmap.get(adAddress)); // 获取当前场地的状态
                        }
                    }
                }
            }
            // 自动调整列宽
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 将数据写入输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("生成Excel文件时出错", e);
        }


    }

    public static String extractCityWithRegex(String address) {
        // 改进后的正则表达式，匹配包含逗号的城市部分
        // 此正则表达式允许多余或缺少的逗号，并捕获包含字母和数字的城市名称
        Pattern pattern = Pattern.compile(",\\s*([a-zA-Z0-9 ]+)\\s*,\\s*[A-Z]{2}\\s*\\d{5}");
        Matcher matcher = pattern.matcher(address);

        if (matcher.find()) {
            return matcher.group(1).trim(); // 返回匹配到的城市名称
        } else {
            return "City not found";
        }
    }
}
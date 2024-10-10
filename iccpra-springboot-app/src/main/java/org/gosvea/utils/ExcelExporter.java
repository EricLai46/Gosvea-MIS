package org.gosvea.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gosvea.pojo.CourseSchedule;
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

public class ExcelExporter {


    public static ByteArrayInputStream exportCoursesToExcel(LocalDate date, List<CourseSchedule> courses) {

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
            String courseTitle = schedule.getCourseTitle();

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
                // 如果 key 不存在，直接添加新的日期
                coursedateMap.put(uniqueKey, schedule.getDate().toString());
            }
            // 遍历已经存在的课程，检查是否有相同的venueId和courseTitle
            boolean isDuplicate = false;
            for (CourseSchedule existingSchedule : allCourseMap.get(courseTitle)) {
                if (existingSchedule.getVenueId().equals(schedule.getVenueId())
                        && existingSchedule.getCourseTitle().equals(schedule.getCourseTitle())) {
                    isDuplicate = true;
                    break;
                }
            }

            // 如果没有重复，才添加到列表
            if (!isDuplicate) {
                allCourseMap.get(courseTitle).add(schedule);
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
        System.out.println("计划打广告的"+allGroupedData);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Courses");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] columns = {"课程编号", "培训点编码", "课程名称", "时区", "AllCPR售价", "开课日期计划", "计划次数", "备注", "负责人", "实际开课日期", "实际广告次数", "备注", "发布人"};
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

            // 填充数据
            int rowIdx = 1;
            for (String adAddress : allGroupedData.keySet()) {
                Map<String, List<CourseSchedule>> courseMap = allGroupedData.get(adAddress);

                for (String courseTitle : courseMap.keySet()) {
                    List<CourseSchedule> courseList = courseMap.get(courseTitle);
                    // 计算相同address和coursetitle的课程总数
                    int planTimes = courseList.size();  // 计划次数为相同课程的总数
                    for (CourseSchedule course : courseList) {
                        Row row = sheet.createRow(rowIdx++);

                        // 填充每列的数据
                        row.createCell(0).setCellValue(course.getId().toString());   // 课程编号
                        row.createCell(1).setCellValue(course.getVenueId());    // 培训点编码
                        row.createCell(2).setCellValue(course.getCourseTitle());    // 课程名称
                        row.createCell(3).setCellValue(course.getTimeZone());   // 时区
                        row.createCell(4).setCellValue(course.getPrice()); // ALLCPR售价

                        // 广告计划
//                        StringBuilder adPlans = new StringBuilder();
//                        List<CourseSchedule> dss = allGroupedData.get(course.getVenueId()).get(course.getCourseTitle());
//                        System.out.println("course信息" + dss);
//
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
//
//                        for (int i = 0; i < dss.size(); i++) {
//                            if (i > 0) {
//                                adPlans.append(", ");  // 在每个日期之间添加逗号和空格
//                            }
//                            adPlans.append(dss.get(i).getDate().format(formatter));  // 使用DateTimeFormatter格式化日期
//                        }
                        row.createCell(5).setCellValue(coursedateMap.get(course.getVenueId()+"|"+course.getCourseTitle()));   // 开班日期计划
                        row.createCell(6).setCellValue(countMap.get(course.getVenueId()+"|"+course.getCourseTitle()));    // 计划次数
                        row.createCell(7).setCellValue(course.isEnrollwareAdded());  // 备注
                        row.createCell(8).setCellValue(course.getIcpisManager());   // 负责人

                        // 检查是否有对应的 activeGroupedData 数据
                        if (activeGroupedData.containsKey(adAddress) && activeGroupedData.get(adAddress).containsKey(courseTitle)) {
                            List<CourseSchedule> activeCourseList = activeGroupedData.get(adAddress).get(courseTitle);
                            int activePlanTimes = activeCourseList.size();  // 计算计划次数

                            // 将 activeGroupedData 的数据添加到右侧列
                            row.createCell(9).setCellValue(activeCoursedateMap.get(course.getVenueId()+"|"+course.getCourseTitle()));   // 实际开班日期计划
                            row.createCell(10).setCellValue(activeCountMap.get(course.getVenueId()+"|"+course.getCourseTitle()));    // 实际广告次数
                            row.createCell(11).setCellValue(course.isEnrollwareAdded()); // 补打广告次数
                            row.createCell(12).setCellValue(course.getIcpisManager());   // 发布人
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
}
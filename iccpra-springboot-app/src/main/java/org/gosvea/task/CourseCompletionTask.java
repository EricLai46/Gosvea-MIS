package org.gosvea.task;

import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.Instructor;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CourseCompletionTask {


    @Autowired
    private InstructorService instructorService;

    @Autowired
    private CourseService courseService;
    @Scheduled(fixedRate = 60000*60*24) // 每天执行一次
    public void checkCourseCompletion() {
        LocalDate currentDate = LocalDate.now();
        List<CourseSchedule> schedules =courseService.getAllCourseSchedule();
       // System.out.println(schedules);
        for (CourseSchedule schedule : schedules) {
           // System.out.println("endtime"+schedule.getDate().isBefore(currentDate));
            //System.out.println(schedule.isActive());
            if (schedule.getDate().isBefore(currentDate) && schedule.isActive()&&!schedule.isProcessed()) {
                // 更新总上课次数

                Instructor instructor=instructorService.getInstructorById(schedule.getInstructorId());
               // System.out.println(instructor);
                System.out.println("previous toltal class times:"+instructor.getTotalClassTimes());
                instructor.setTotalClassTimes(instructor.getTotalClassTimes()+1);
                System.out.println("Right now total class times:"+instructor.getTotalClassTimes());
                // 检查并更新 wage_hour

                //检查是否有salary梯度
                if(instructor.getSalaryInfo()!=null)
                {

                    String salaryinfos=instructor.getSalaryInfo();
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(salaryinfos);

                    // 使用 ArrayList 存储提取出的数字
                    ArrayList<Integer> numbers = new ArrayList<>();

                    // 找到所有匹配的数字并存入 ArrayList
                    while (matcher.find()) {
                        numbers.add(Integer.parseInt(matcher.group()));
                    }

                    int wageIncrease = 0;

                    for(int i=2;i< numbers.size();i++)
                    {
                        if(instructor.getTotalClassTimes()==numbers.get(i))
                        {
                            String wageHour = instructor.getWageHour();
                            String numericPart = wageHour.replaceAll("[^\\d]", "");  // 只保留数字字符

                            if (!numericPart.isEmpty()) {
                                int currentWage = Integer.parseInt(numericPart);
                                int threshold = numbers.get(1);

                                if (currentWage < threshold && currentWage + 5 <= threshold) {
                                    wageIncrease += 5;
                                }
                            } else {
                                System.out.println("Error: Unable to extract numeric value from wageHour: " + wageHour);
                            }
                        }
                    }
                    String wageHour = instructor.getWageHour();  // 例如 "40/h"

                    // 提取纯数字部分
                    String numericPart = wageHour.replaceAll("[^\\d]", "");  // 只保留数字字符

                    if (!numericPart.isEmpty()) {
                        int currentWage = Integer.parseInt(numericPart);  // 将纯数字部分转换为整数
                        instructor.setWageHour((currentWage + wageIncrease) + "/h");
                        System.out.println(instructor.getWageHour());
                    } else {
                        System.out.println("Error: Unable to extract numeric value from wageHour: " + wageHour);
                    }
                    //schedule.setCourseMarkedAsCompleted(true); // 标记课程已处理
                    instructorService.updateInstructor(instructor);
                    courseService.updateCourseScheduleProcessed(schedule,true);
                }



            }
        }
    }
}

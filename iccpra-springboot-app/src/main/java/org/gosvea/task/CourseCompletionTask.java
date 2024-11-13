package org.gosvea.task;

import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.Icpie;
import org.gosvea.pojo.Instructor;
import org.gosvea.pojo.Venue;
import org.gosvea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.temporal.ChronoUnit;
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
    private EmailService emailService;

    @Autowired
    private IccpraService iccpraService;
    @Autowired
    private InstructorService instructorService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private VenueService venueService;

    // 起始日期：11/09/2024
    private static final LocalDate START_DATE = LocalDate.of(2024, 11, 9);

    @Scheduled(cron = "0 0 0 * * ?") // 每天午夜执行一次
    public void checkCourseCompletion() {
        LocalDate currentDate = LocalDate.now();
        List<CourseSchedule> schedules = courseService.getAllCourseSchedule();

        for (CourseSchedule schedule : schedules) {
            if (schedule.getDate().isBefore(currentDate) && schedule.isActive() && !schedule.isProcessed()) {
                Instructor instructor = instructorService.getInstructorById(schedule.getInstructorId());

                        if (instructor != null) {
                            instructor.setTotalClassTimes(instructor.getTotalClassTimes() + 1);
                            updateInstructorWage(instructor);
                            instructorService.updateInstructor(instructor);
                            courseService.updateCourseScheduleProcessed(schedule, true);
                        }

                }


        }
    }

    private void updateInstructorWage(Instructor instructor) {
        if (instructor.getSalaryInfo() != null) {
            String salaryinfos = instructor.getSalaryInfo();
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(salaryinfos);

            ArrayList<Integer> numbers = new ArrayList<>();
            while (matcher.find()) {
                numbers.add(Integer.parseInt(matcher.group()));
            }

            int wageIncrease = 0;
            for (int i = 2; i < numbers.size(); i++) {
                if (instructor.getTotalClassTimes() == numbers.get(i)) {
                    String wageHour = instructor.getWageHour();
                    String numericPart = wageHour.replaceAll("[^\\d]", "");
                    if (!numericPart.isEmpty()) {
                        int currentWage = Integer.parseInt(numericPart);
                        int threshold = numbers.get(1);

                        if (currentWage < threshold && currentWage + 5 <= threshold) {
                            wageIncrease += 5;
                        }
                    }
                }
            }

            String wageHour = instructor.getWageHour();
            String numericPart = wageHour.replaceAll("[^\\d]", "");
            if (!numericPart.isEmpty()) {
                int currentWage = Integer.parseInt(numericPart);
                instructor.setWageHour((currentWage + wageIncrease) + "/h");
            }
        }
    }


    @Scheduled(cron = "0 0 1 * * ?")
    public void CheckIfInvestigationSiteShouldBeNormal() {
        //获取所有观察点的信息
        List<Venue> investigationVenueList = venueService.getAllSpecStatusVenues(Venue.VenueStatus.INVESTIGATION);
        for (Venue venue : investigationVenueList) {
            // 获取与该场地关联的所有讲师
            List<Instructor> instructors = instructorService.getInstructorsByVenueId(venue.getId());

            if (instructors != null ) {
                // 遍历讲师列表，检查是否有讲师的课程次数 >= 2
                for (Instructor instructor : instructors) {
                    if (instructor.getTotalClassTimes() >= 2) {
                        // 将场地状态改为NORMAL
                        venue.setVenueStatus(Venue.VenueStatus.NORMAL);
                        venueService.updateVenue(venue);
                        break; // 找到一个满足条件的讲师后，跳出循环
                    }
                }
            }
        }
    }

    //检查新点准备补打广告
    @Scheduled(cron = "0 0 2 * * ?")
    public void CheckIfNeedAddedAD(){
        List<Venue> investigationVenueList=venueService.getAllSpecStatusVenues(Venue.VenueStatus.INVESTIGATION);
        for(Venue venue:investigationVenueList)
        {
            Icpie icpie =iccpraService.findByIcpieName(venue.getIcpisManager());
            if(icpie!=null)
            {
                String email=icpie.getEmail();
                emailService.noticeIcpisManagerAddAD(email,venue);
            }

        }
    }
    //广告异常通知
    @Scheduled(cron = "0 20 2 * * ?")
    public void CheckADWired(){
        LocalDate today = LocalDate.now();

        // 计算从起始日期到今天的天数差
        long daysBetween = ChronoUnit.DAYS.between(START_DATE, today);


        // 检查是否为两周的倍数
        if (daysBetween % 14 == 0) {
            System.out.println("Executing task on: " + today);
            // 执行任务的代码逻辑
            LocalDate fromDate=today.plusDays(22);
            LocalDate toDate=fromDate.plusDays(13);
            List<Venue> venueList=courseService.getNoCourseSchedleVenueFromTo(fromDate,toDate);
            if(venueList!=null&&!venueList.isEmpty())
            {
                for(Venue venue:venueList)
                {
                    emailService.noticeIcpisManagerAdWired(venue);
                }
            }
        }
    }
}

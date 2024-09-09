package org.gosvea.task;

import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.Instructor;
import org.gosvea.pojo.Venue;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
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

    @Autowired
    private VenueService venueService;
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
    public void CheckIfInvestigationSiteShouldBeNormal()
    {
        //获取所有观察点的信息
        List<Venue> investigationVenueList=venueService.getAllSpecStatusVenues(Venue.VenueStatus.INVESTIGATION);
        for(Venue venue:investigationVenueList)
        {
            if(venue.getInstructor()!=null)
            {
                Instructor instructor=instructorService.getInstructorById(venue.getInstructor());
                if(instructor!=null)
                {
                    if(instructor.getTotalClassTimes()>=2)
                    {
                        venue.setVenueStatus(Venue.VenueStatus.NORMAL);
                        venueService.updateVenue(venue);
                    }

                }
            }
        }

    }
}

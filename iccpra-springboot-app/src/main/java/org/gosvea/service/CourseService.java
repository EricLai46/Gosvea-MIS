package org.gosvea.service;


import org.gosvea.pojo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;


public interface CourseService {
    void insertCourseSchedule(List<CourseSchedule> courseSchedules);

    void updateCourseInformation(CourseSchedule courseSchedule);

    void deleteCourse(Integer instructorId, Integer venueId);

    PageResponse<CourseSchedule> getCourseSchedule(Integer pageNum, Integer pageSize, String icpisManager, String venueId, LocalDate date, LocalTime startTime, LocalTime endTime, Boolean isActive,Boolean isProcessed,LocalDate fromDate,LocalDate toDate);

    void deleteAllSchedule();
    //检查venue和instructor是否有匹配的时间
   //Map<Integer,String> checkVenueInstructorInformation(Integer venueId, Integer instructorId);

    Map<Integer, String> generateOrUpdateCourseSchedules(String venueId, String instructorId);
    void deleteCourseSchedules(List<CourseSchedule> schedulesToDelete);
    void insertCourseSchedules(List<CourseSchedule> schedulesToInsert);
    void updateCourseSchedules(List<CourseSchedule> schedulesToUpdate);

    List<CourseSchedule> findCourseSchedulesByVenueAndDateRange(String venueId, List<VenueSchedule> venueSchedules);

    List<CourseSchedule> getAllCourseSchedule();

    void updateCourseScheduleProcessed(CourseSchedule courseSchedule,Boolean isProcessed);


}

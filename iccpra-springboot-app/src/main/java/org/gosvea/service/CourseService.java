package org.gosvea.service;


import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.PageResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface CourseService {
    void insertCourseSchedule(List<CourseSchedule> courseSchedules);

    void updateCourseInformation(CourseSchedule courseSchedule);

    void deleteCourse(Integer instructorId, Integer venueId);

    PageResponse<CourseSchedule> getCourseSchedule(Integer pageNum, Integer pageSize, Integer instructorId, Integer venueId, LocalDate date, LocalTime startTime,LocalTime endTime,Boolean isActive);

    void deleteAllSchedule();
}

package org.gosvea.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.VenueSchedule;
import org.springframework.web.bind.annotation.RequestBody;

@Mapper
public interface CourseScheduleMapper {

    List<CourseSchedule> getCourseSchedule(String icpisManager, String venueId, LocalDate date, LocalTime startTime, LocalTime endTime, Boolean isActive,Boolean isProcessed,LocalDate fromDate,LocalDate toDate);

    @Update("update coursesschedule set is_active=False where id=#{courseId}")
    void deActivateCourseSchedule(Integer courseId);

    @Insert({
            "<script>",
            "INSERT INTO coursesschedule (date, instructor_id, venue_id, start_time, end_time, is_active,address,course_title) VALUES ",
            "<foreach collection='courseSchedules' item='courseSchedule' separator=','>",
            "(#{courseSchedule.date}, #{courseSchedule.instructorId}, #{courseSchedule.venueId}, #{courseSchedule.startTime}, #{courseSchedule.endTime}, #{courseSchedule.isActive},#{courseSchedule.address},#{courseSchedule.courseTitle})",
            "</foreach>",
            "</script>"
    })
    //@Insert("insert into coursesschedule (date,instructor_id,venue_id,start_time,end_time,is_active) values(#{date},#{instructorId},#{venueId},#{startTime},#{endTime},#{isActive})")
    void insertCoursesSchedule( @Param("courseSchedules") List<CourseSchedule> courseSchedules);
    @Delete("delete from coursesschedule where instructor_id=#{instructorId} and venue_id=#{venueId}")
    void deleteCourse(Integer instructorId, Integer venueId);

     @Update("update coursesschedule set instructor_id=#{instructorId},venue_id=#{venueId},start_time=#{startTime},end_time=#{endTime},date=#{date},is_active=#{isActive},is_enrollwareAdded=#{isEnrollwareAdded},comments=#{comments} where id=#{id}")
    void updateCourseInformation(CourseSchedule courseSchedule);
    @Delete("delete from coursesschedule")
    void deleteAllSchedule();

    void deleteCourseSchedules(List<CourseSchedule> schedulesToDelete);

    void insertCourseSchedules(List<CourseSchedule> schedulesToInsert);

    void updateCourseSchedules(List<CourseSchedule> schedulesToUpdate);

    List<CourseSchedule> findCourseSchedulesByVenueAndDateRange(String venueId, List<VenueSchedule> venueSchedules);

    @Select("select * from coursesschedule")
    List<CourseSchedule> getAllCourseSchedule();
    @Update("update coursesschedule set is_processed=#{isProcessed} where id=#{courseSchedule.id}")
    void updateCourseScheduleProcessed(CourseSchedule courseSchedule, Boolean isProcessed);
}

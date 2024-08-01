package org.gosvea.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.gosvea.mapper.CourseScheduleMapper;
import org.gosvea.pojo.CourseSchedule;
import org.gosvea.pojo.PageResponse;
import org.gosvea.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;
    @Override
    public void insertCourseSchedule(List<CourseSchedule> courseSchedules) {
        courseScheduleMapper.insertCoursesSchedule(courseSchedules);
    }

    @Override
    public void updateCourseInformation(CourseSchedule courseSchedule) {
        courseScheduleMapper.updateCourseInformation(courseSchedule);
    }

    @Override
    public void deleteCourse(Integer instructorId, Integer venueId) {
        courseScheduleMapper.deleteCourse(instructorId,venueId);
    }

    @Override
    public PageResponse<CourseSchedule> getCourseSchedule(Integer pageNum, Integer pageSize, Integer instructorId, Integer venueId, LocalDate date, LocalTime startTime, LocalTime endTime, Boolean isActive) {
        PageResponse<CourseSchedule> prc=new PageResponse<>();
        PageHelper.startPage(pageNum,pageSize);

        Boolean activeStatus = (isActive != null) ? isActive : false;

        List<CourseSchedule> lic=courseScheduleMapper.getCourseSchedule(instructorId,venueId,date,startTime,endTime,isActive);
        Page<CourseSchedule> pgc=(Page<CourseSchedule>) lic;
        prc.setItems(pgc.getResult());
        prc.setTotalElement(pgc.getTotal());
        return prc;
    }
}

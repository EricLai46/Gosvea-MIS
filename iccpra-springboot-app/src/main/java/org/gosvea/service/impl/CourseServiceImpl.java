package org.gosvea.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.gosvea.mapper.CourseScheduleMapper;
import org.gosvea.pojo.*;
import org.gosvea.service.CourseService;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private VenueService venueService;

    //添加课程schedule
    @Override
    public void insertCourseSchedule(List<CourseSchedule> courseSchedules) {
        courseScheduleMapper.insertCoursesSchedule(courseSchedules);
    }
    //更新课程schedule
    @Override
    public void updateCourseInformation(CourseSchedule courseSchedule) {
        courseScheduleMapper.updateCourseInformation(courseSchedule);
    }
    //删除课程schedule
    @Override
    public void deleteCourse(Integer instructorId, Integer venueId) {
        courseScheduleMapper.deleteCourse(instructorId,venueId);
    }
    //获取courseschedule
    @Override
    public PageResponse<CourseSchedule> getCourseSchedule(Integer pageNum, Integer pageSize, String icpisManager, String venueId, LocalDate date, LocalTime startTime, LocalTime endTime, Boolean isActive,Boolean isProcessed,LocalDate fromDate,LocalDate toDate) {
        PageResponse<CourseSchedule> prc=new PageResponse<>();
        PageHelper.startPage(pageNum,pageSize);

        Boolean activeStatus = (isActive != null) ? isActive : false;

        List<CourseSchedule> lic=courseScheduleMapper.getCourseSchedule(icpisManager,venueId,date,startTime,endTime,isActive,isProcessed,fromDate,toDate);
        Page<CourseSchedule> pgc=(Page<CourseSchedule>) lic;
        prc.setItems(pgc.getResult());
        prc.setTotalElement(pgc.getTotal());
        return prc;
    }
    //删除所有课程schedule
    @Override
    public void deleteAllSchedule() {
        courseScheduleMapper.deleteAllSchedule();
    }
//    //检查场地和教师的信息
//    @Override
//    public Map<Integer, String> checkVenueInstructorInformation(Integer venueId, Integer instructorId) {
//        Map<Integer, String> warnings = new HashMap<>();
//
//        Venue venue = null;
//        Instructor instructor = null;
//
//        if (venueId != null) {
//            venue = venueService.getVenueById(venueId);
//            if (venue == null) {
//                warnings.put(venueId, "Venue ID " + venueId + " does not exist.");
//                return warnings;
//            }
//            instructor = instructorService.getInstructorById(venue.getInstructor());
//        } else if (instructorId != null) {
//            instructor = instructorService.getInstructorById(instructorId);
//            if (instructor == null) {
//                warnings.put(instructorId, "Instructor ID " + instructorId + " does not exist.");
//                return warnings;
//            }
//            venue = venueService.getVenueByInstructorId(instructorId);
//            if (venue == null) {
//                warnings.put(instructorId, "Instructor ID " + instructorId + " is not associated with any venue.");
//                return warnings;
//            }
//        }
//
//        if (venue == null || instructor == null) {
//            warnings.put(venueId != null ? venueId : instructorId, "Either the venue or instructor is not available.");
//            return warnings;
//        }
//
//        instructor.setScheduleList(instructorService.getInstructorSchedule(instructor.getId()));
//
//        for (Schedule venueSchedule : venue.getScheduleList()) {
//            CourseSchedule commonSchedule = getMatchingSchedule(venue, instructor, venueSchedule);
//            if (commonSchedule == null) {
//                warnings.put(venue.getId(), "Venue ID " + venue.getId() + " does not have a matching schedule with its instructor for time slot: " + venueSchedule.getStartTime() + " - " + venueSchedule.getEndTime());
//                //venueService.updateVenueStatus(venue.getId(), Venue.VenueStatus.VENUEISSUE);
//            } else {
//                handleCourseSchedule(commonSchedule);
//            }
//        }
//
//        return warnings.isEmpty() ? null : warnings;
//    }
//
//
//
//    //获取matched公共schedule
//    private CourseSchedule getMatchingSchedule(Venue venue, Instructor instructor, Schedule venueSchedule) {
//        for (Schedule instructorSchedule : instructor.getScheduleList()) {
//            // 检查教师的时间段是否覆盖场地的时间段
//            if (instructorSchedule.getDate().equals(venueSchedule.getDate()) &&
//                    !instructorSchedule.getStartTime().isAfter(venueSchedule.getStartTime()) && // 教师开始时间不晚于场地开始时间
//                    !instructorSchedule.getEndTime().isBefore(venueSchedule.getEndTime())) {   // 教师结束时间不早于场地结束时间
//
//                return new CourseSchedule(instructor.getId(), venue.getId(), venueSchedule.getDate(),
//                        venueSchedule.getStartTime(), venueSchedule.getEndTime(), venue.getAddress(),"cpr");
//            }
//        }
//        return null;
//    }
//   //处理课程schedule,看是否需要更新或者添加
//    private void handleCourseSchedule(CourseSchedule newSchedule) {
//        CourseSchedule existingSchedule = findCourseScheduleByVenueAndTime(newSchedule.getVenueId(),
//                newSchedule.getStartTime(), newSchedule.getEndTime(), newSchedule.getDate());
//
//        if (existingSchedule != null) {
//            // 如果找到相同时间段的课程，检查是否需要更新
//            if (!existingSchedule.getInstructorId().equals(newSchedule.getInstructorId())) {
//                existingSchedule.setInstructorId(newSchedule.getInstructorId());
//                updateCourseSchedule(existingSchedule);
//            }
//        } else {
//            // 如果没有找到相同的课程，则添加新的课程
//            insertCourseSchedule(newSchedule);
//        }
//    }
//
//    public CourseSchedule findCourseScheduleByVenueAndTime(Integer venueId, LocalTime startTime, LocalTime endTime, LocalDate date) {
//       return courseScheduleMapper.findCourseScheduleByVenueAndTime(venueId,)
//    }
//
//
//    @Override
//    public List<CourseSchedule> getCommonSchedules(Integer instructorId, Integer venueId, String address, List<VenueSchedule> venueSchedules, List<InstructorSchedule> instructorSchedules) {
//        List<CourseSchedule> commonSchedules = new ArrayList<>();
//
//        for (VenueSchedule venueSchedule : venueSchedules) {
//            LocalDate venueDate = venueSchedule.getDate();
//            LocalTime venueStartTime = venueSchedule.getStartTime();
//            LocalTime venueEndTime = venueSchedule.getEndTime();
//            boolean matchFound = false; // 增加一个标记变量
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
//                    commonSchedules.add(new CourseSchedule(instructorId, venueId, instructorDate, commonStartTime, commonEndTime, address, venueSchedule.getCourseTitle()));
//                    matchFound = true; // 找到匹配时设置为 true
//                    break; // 跳出内层循环
//                }
//            }
//
//            if (matchFound) {
//                break; // 如果找到匹配的时间段，跳出外层循环
//            }
//        }
//        return commonSchedules;
//    }
public Map<String, String> generateOrUpdateCourseSchedules(String venueId, String instructorId) {
    Map<String, String> warnings = new HashMap<>();

    Venue venue = venueService.getVenueById(venueId);
    Instructor instructor = instructorService.getInstructorById(instructorId);
    System.out.println("venue: "+venue);
    System.out.println("Instructor: "+instructor);
    if (venue == null) {
        warnings.put(venueId, "Venue ID " + venueId + " does not exist.");
        return warnings;
    }

    if (instructor == null) {
        warnings.put(instructorId, "Instructor ID " + instructorId + " does not exist.");
        return warnings;
    }

    List<VenueSchedule> venueSchedules = venueService.getVenueSchedule(venueId);
    System.out.println("venueschedule:"+venueSchedules);
    List<InstructorSchedule> instructorSchedules = instructorService.getInstructorSchedule(instructorId);

    if (venueSchedules.isEmpty()) {
        warnings.put(venueId, "Venue ID " + venueId + " has no schedules defined.");
    }

    if (instructorSchedules.isEmpty()) {
        warnings.put(instructorId, "Instructor ID " + instructorId + " has no schedules defined.");
    }

    // 批量查询所有可能的课程安排
    List<CourseSchedule> existingSchedules = findCourseSchedulesByVenueAndDateRange(venueId, venueSchedules);
    System.out.println("existingschedules:"+existingSchedules);
    List<CourseSchedule> schedulesToUpdate = new ArrayList<>();
    List<CourseSchedule> schedulesToInsert = new ArrayList<>();
    List<CourseSchedule> schedulesToDelete = new ArrayList<>();

    for (VenueSchedule venueSchedule : venueSchedules) {
        CourseSchedule existingSchedule = existingSchedules.stream()
                .filter(cs -> cs.getStartTime().equals(venueSchedule.getStartTime()) &&
                        cs.getEndTime().equals(venueSchedule.getEndTime()) &&
                        cs.getDate().equals(venueSchedule.getDate()))
                .findFirst()
                .orElse(null);

        boolean isMatchFound = false;
        for (Schedule instructorSchedule : instructorSchedules) {
            if (instructorSchedule.getDate().equals(venueSchedule.getDate()) &&
                    !instructorSchedule.getEndTime().isBefore(venueSchedule.getStartTime()) &&
                    !instructorSchedule.getStartTime().isAfter(venueSchedule.getEndTime()))  {

                isMatchFound = true;
                if (existingSchedule != null) {
                    if (!existingSchedule.getInstructorId().equals(instructorId) ||
                            !existingSchedule.getStartTime().equals(venueSchedule.getStartTime()) ||
                            !existingSchedule.getEndTime().equals(venueSchedule.getEndTime()) ||
                            !existingSchedule.getCourseTitle().equals(venueSchedule.getCourseTitle())||(existingSchedule.getRegistrationLink()==null||!existingSchedule.getRegistrationLink().equals(venue.getRegistrationLink()))||!existingSchedule.getTimeZone().equals(venue.getTimeZone())||
                            !existingSchedule.getIcpisManager().equals(venue.getIcpisManager())) { // 添加对 courseTitle 的比较

                        existingSchedule.setInstructorId(instructorId);
                        existingSchedule.setStartTime(venueSchedule.getStartTime());
                        existingSchedule.setEndTime(venueSchedule.getEndTime());
                        existingSchedule.setCourseTitle(venueSchedule.getCourseTitle());
                        existingSchedule.setRegistrationLink(venue.getRegistrationLink());
                        existingSchedule.setIcpisManager(venue.getIcpisManager());
                        existingSchedule.setTimeZone(venue.getTimeZone());// 更新 courseTitle
                        schedulesToUpdate.add(existingSchedule);
                    }
                } else {
                    CourseSchedule newSchedule = new CourseSchedule(instructorId, venueId,
                            venueSchedule.getDate(), venueSchedule.getStartTime(), venueSchedule.getEndTime(),
                            venue.getAddress(), venueSchedule.getCourseTitle(),venueSchedule.getPrice(),venue.getRegistrationLink(),venue.getTimeZone(),"",venue.getIcpisManager(),false);
                    schedulesToInsert.add(newSchedule);
                }
                break;
            }
        }

        if (!isMatchFound && existingSchedule != null) {
            schedulesToDelete.add(existingSchedule);
        }
    }

    // 批量更新、插入和删除课程表
    if (!schedulesToUpdate.isEmpty()) {
        updateCourseSchedules(schedulesToUpdate);
        System.out.println("updated schedules:"+schedulesToUpdate);
    }
    if (!schedulesToInsert.isEmpty()) {
        insertCourseSchedules(schedulesToInsert);
        System.out.println("Insert schedule:"+instructorSchedules);
    }
    if (!schedulesToDelete.isEmpty()) {
        deleteCourseSchedules(schedulesToDelete);
        System.out.println("Delete schedule:"+schedulesToDelete);
    }

    return warnings.isEmpty() ? null : warnings;
}

    public List<CourseSchedule> findCourseSchedulesByVenueAndDateRange(String venueId, List<VenueSchedule> venueSchedules) {
        return courseScheduleMapper.findCourseSchedulesByVenueAndDateRange(venueId,venueSchedules);
    }

    @Override
    public List<CourseSchedule> getAllCourseSchedule() {
        return courseScheduleMapper.getAllCourseSchedule();
    }

    @Override
    public void updateCourseScheduleProcessed(CourseSchedule courseSchedule, Boolean isProcessed) {
        courseScheduleMapper.updateCourseScheduleProcessed(courseSchedule,isProcessed);
    }

    @Override
    public List<CourseSchedule> getCourseCalendar(String venueId) {
        return courseScheduleMapper.getCourseCalendar(venueId);
    }

    @Override
    public List<Venue> getAllVenueIdAndAddress() {
        return courseScheduleMapper.getAllVenueIdAndAddress();
    }

    @Override
    public List<CourseSchedule> getCourseScheduleSummary(LocalDate date) {
        LocalDate fromDate=date.plusDays(24);
        LocalDate toDate=fromDate.plusDays(13);
        return courseScheduleMapper.getCourseScheduleSummary(fromDate,toDate);
    }

    public void deleteCourseSchedules(List<CourseSchedule> schedulesToDelete) {
        courseScheduleMapper.deleteCourseSchedules(schedulesToDelete);
    }

    public void insertCourseSchedules(List<CourseSchedule> schedulesToInsert) {
        courseScheduleMapper.insertCourseSchedules(schedulesToInsert);
    }

    public void updateCourseSchedules(List<CourseSchedule> schedulesToUpdate) {
        courseScheduleMapper.updateCourseSchedules(schedulesToUpdate);
    }
}

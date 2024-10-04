package org.gosvea.mapper;


import org.apache.ibatis.annotations.*;
import org.gosvea.pojo.Instructor;
import org.gosvea.pojo.InstructorSchedule;
import org.gosvea.pojo.Venue;

import java.util.List;
import java.util.Map;

@Mapper
public interface InstructorMapper {
    @Insert("insert into instructors (id,firstname,lastname,state,city,phone_number,email,wage_hour,total_class_times,deposit,rent_manikin_numbers,finance,rent_status,fob_key,icpis_manager)"+
            "values(#{id},#{firstname},#{lastname},#{state},#{city},#{phoneNumber},#{email},#{wageHour},#{totalClassTimes},#{deposit},#{rentManikinNumbers},#{finance},#{rentStatus},#{fobKey},#{icpisManager})")
    void addInstructor(Instructor instructor);

    List<Instructor> getInstructor(String state, String city, String instructorId, String phoneNumber, String wageHour,String venueId,String firstname, String lastname);
    @Delete("delete from instructors where id=#{instructorId}")
    void deleteInstructor(String instructorId);
    //@Update("update instructors set venue_id=#{venueId},firstname=#{firstname},lastname=#{lastname},state=#{state},city=#{city},phone_number=#{phoneNumber},email=#{email},wage_hour=#{wageHour},total_class_times=#{totalClassTimes},deposit=#{deposit},rent_manikin_numbers=#{rentManikinNumbers},finance=#{finance},rent_status=#{rentStatus},fob_key=#{fobKey} where id=#{id}")
    void updateInstructor(Instructor instructor);
    @Update("update instructorschedule set date=#{date},start_time=#{startTime},end_time=#{endTime}  where instructor_id=#{instructorId}")
    void updateInstructorSchedule(InstructorSchedule instructorSchedule);
    @Select("select * from instructorschedule where instructor_id=#{instructorId}")
    List<InstructorSchedule> getInstructorSchedudle(String instructorId);
    @Select("select id,concat(firstname,' ',lastname) as fullname from instructors")
    List<Map<String,Object>> getInstructorNameList();
    @Delete("delete from instructorschedule where id=#{id}")
    void deleteInstructorSchedule(String id);
    @Insert("insert into instructorschedule (date,instructor_id,start_time,end_time) values(#{date},#{instructorId},#{startTime},#{endTime})")
    void addInstructorSchedule(InstructorSchedule instructorSchedule);
    @Select("select * from instructors where id=#{instructor}")
    Instructor getInstructorById(String instructor);
    @Select("select * from instructors")
    List<Instructor> getAllInstructors();
    @Select("select id from instructors where firstname=#{firstname} and lastname=#{lastname}")
    String findIdByName(String firstname,String lastname);

    @Select("select concat(firstname.' ',lastname) as fullName from instructors ")
    List<String> getAllInstructorNames();

    void insertListInstructors(List<Instructor> instructorList);

    void updateListInstructors(List<Instructor> instructorList);
    @Delete("DELETE FROM instructors")
    void clearAllData();
    @Select("select i* from instructors i join instructor_venue iv on i.id=iv.instructor_id where iv.venue_id=#{id}")
    List<Instructor> getInstructorsByVenueId(String id);
    @Select("select instructor_id from instructor_venue  where venue_id=#{venueId}")
    List<String> getInstructorIdsByVenueId(String venueId);
    @Select("select id from instructors where lastname=#{lastname} and firstname=#{firstname}")
    String getInstructorIdByInstructorName(String firstname, String lastname);

    List<Instructor> getInstructorByIcpisName(String state, String city, String instructorId, String phoneNumber, String venueId, String firstname, String lastname, String icpisname);
}

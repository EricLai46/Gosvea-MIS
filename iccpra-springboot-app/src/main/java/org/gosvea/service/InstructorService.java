package org.gosvea.service;

import org.gosvea.pojo.Instructor;
import org.gosvea.pojo.InstructorSchedule;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Venue;

import java.util.List;
import java.util.Map;

public interface InstructorService {
    void addInstructor(Instructor instructor);

    PageResponse<Instructor> getInstructor(Integer pageNum, Integer pageSize, String state, String city, Integer instructorId, String phoneNumber, String email, Integer wageHour);

    void deleteInstructor(Integer instructorId);

    void updateInstructor(Instructor instructor);

    void updateInstructorSchedule(InstructorSchedule instructorSchedule);

    List<InstructorSchedule> getInstructorSchedule(Integer instructorId);

    List<Map<String,Object>> getInstructorNameList();

    void deleteInstructorSchedule(Integer id);

    void addInstructorSchedule(InstructorSchedule instructorSchedule);

    Instructor getInstructorById(Integer instructor);

    List<Instructor> getAllInstructors();

    List<String> getAllInstructorNames();

    Integer findIdByName(String firstName, String lastName);


    void updateListInstructors(List<Instructor> instructorList);

    void insertListInstructors(List<Instructor> instructorList);
}

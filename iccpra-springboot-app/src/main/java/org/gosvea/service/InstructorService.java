package org.gosvea.service;

import org.gosvea.pojo.Instructor;
import org.gosvea.pojo.InstructorSchedule;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Venue;

import java.util.List;
import java.util.Map;

public interface InstructorService {
    void addInstructor(Instructor instructor);

    PageResponse<Instructor> getInstructor(Integer pageNum, Integer pageSize, String state, String city, String instructorId, String phoneNumber, String email, String wageHour,String venueId,String firstname,String lastname);

    void deleteInstructor(String instructorId);

    void updateInstructor(Instructor instructor);

    void updateInstructorSchedule(InstructorSchedule instructorSchedule);

    List<InstructorSchedule> getInstructorSchedule(String instructorId);

    List<Map<String,Object>> getInstructorNameList();

    void deleteInstructorSchedule(String id);

    void addInstructorSchedule(InstructorSchedule instructorSchedule);

   Instructor getInstructorById(String instructor);

    List<Instructor> getAllInstructors();

    List<String> getAllInstructorNames();

    String findIdByName(String firstName, String lastName);


    void updateListInstructors(List<Instructor> instructorList);

    void insertListInstructors(List<Instructor> instructorList);

    void clearAllData();

    boolean isVenueListChanged(List<Venue> currentVenues, List<Venue> newVenues);

    List<Instructor> getInstructorsByVenueId(String id);

    List<String> getInstructorIdsByVenueId(String venueId);


    String getInstructorIdByInstructorName(String firstname,String lastname);
}

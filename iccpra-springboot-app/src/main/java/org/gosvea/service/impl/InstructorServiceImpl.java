package org.gosvea.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.gosvea.mapper.InstructorMapper;
import org.gosvea.pojo.Instructor;
import org.gosvea.pojo.InstructorSchedule;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Venue;
import org.gosvea.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InstructorServiceImpl implements InstructorService {


    @Autowired
    private InstructorMapper instructorMapper;
    @Override
    public void addInstructor(Instructor instructor) {
        instructorMapper.addInstructor(instructor);
    }

    @Override
    public PageResponse<Instructor> getInstructor(Integer pageNum, Integer pageSize, String state, String city, String instructorId, String phoneNumber, String wageHour,String venueId,String firstname, String lastname) {
        PageResponse<Instructor> pi=new PageResponse<Instructor>();

        PageHelper.startPage(pageNum,pageSize);
        List<Instructor> li=instructorMapper.getInstructor(state,city,instructorId,phoneNumber,wageHour,venueId,firstname,lastname);
        Page<Instructor> pgi=(Page<Instructor>)li;

        pi.setTotalElement(pgi.getTotal());
        pi.setItems(pgi.getResult());
        return pi;

    }

    @Override
    public void deleteInstructor(String instructorId) {
        instructorMapper.deleteInstructor(instructorId);
    }

    @Override
    public void updateInstructor(Instructor instructor) {
        instructorMapper.updateInstructor(instructor);
    }

    @Override
    public void updateInstructorSchedule(InstructorSchedule instructorSchedule) {
        instructorMapper.updateInstructorSchedule(instructorSchedule);
    }

    @Override
    public List<InstructorSchedule> getInstructorSchedule(String instructorId) {
        List<InstructorSchedule> instructorScheduleList=new ArrayList<>();

        instructorScheduleList=instructorMapper.getInstructorSchedudle(instructorId);

        return instructorScheduleList;
    }

    @Override
    public List<Map<String,Object>> getInstructorNameList() {
        List<Map<String,Object>> instructorAllNameList=new ArrayList<>();
        instructorAllNameList=instructorMapper.getInstructorNameList();
        return instructorAllNameList;
    }

    @Override
    public void deleteInstructorSchedule(String id) {
        instructorMapper.deleteInstructorSchedule(id);
    }

    @Override
    public void addInstructorSchedule(InstructorSchedule instructorSchedule) {
        instructorMapper.addInstructorSchedule(instructorSchedule);
    }

    @Override
    public Instructor getInstructorById(String instructor) {

        return instructorMapper.getInstructorById(instructor);
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorMapper.getAllInstructors();
    }

    @Override
    public List<String> getAllInstructorNames() {
        return instructorMapper.getAllInstructorNames();
    }

    @Override
    public String findIdByName(String firstName, String lastName) {
        return  instructorMapper.findIdByName(firstName,lastName);
    }

    @Override
    public void updateListInstructors(List<Instructor> instructorList) {
        instructorMapper.updateListInstructors(instructorList);
    }

    @Override
    public void insertListInstructors(List<Instructor> instructorList) {
        instructorMapper.insertListInstructors(instructorList);
    }

    @Override
    public void clearAllData() {
        instructorMapper.clearAllData();
    }

    @Override
    public boolean isVenueListChanged(List<Venue> currentVenues, List<Venue> newVenues) {
        if(currentVenues==null||newVenues==null)
        {
            return !(currentVenues==null&&newVenues==null);
        }
        if(currentVenues.size()!= newVenues.size())
        {
            return true;
        }
        Set<String> currentVenueIds=currentVenues.stream().map(Venue::getId).collect(Collectors.toSet());
        Set<String> newVenueIds=newVenues.stream().map(Venue::getId).collect(Collectors.toSet());
        return !currentVenueIds.equals(newVenueIds);
    }

    @Override
    public List<Instructor> getInstructorsByVenueId(String id) {
       return instructorMapper.getInstructorsByVenueId(id);
    }

    @Override
    public List<String> getInstructorIdsByVenueId(String venueId) {
        return instructorMapper.getInstructorIdsByVenueId(venueId);
    }

    @Override
    public String getInstructorIdByInstructorName(String firstname, String lastname) {
        return instructorMapper.getInstructorIdByInstructorName(firstname,lastname);
    }

    @Override
    public PageResponse<Instructor> getInstructorByIcpisName(Integer pageNum, Integer pageSize, String state, String city, String instructorId, String phoneNumber, String venueId, String firstname, String lastname, String icpisname) {
        PageResponse<Instructor> pi=new PageResponse<Instructor>();

        PageHelper.startPage(pageNum,pageSize);
        List<Instructor> li=instructorMapper.getInstructorByIcpisName(state,city,instructorId,phoneNumber,venueId,firstname,lastname,icpisname);
        Page<Instructor> pgi=(Page<Instructor>)li;

        pi.setTotalElement(pgi.getTotal());
        pi.setItems(pgi.getResult());
        return pi;
    }


}

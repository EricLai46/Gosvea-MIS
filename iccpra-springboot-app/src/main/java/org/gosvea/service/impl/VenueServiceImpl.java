package org.gosvea.service.impl;

import org.gosvea.mapper.VenueMapper;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Venue;
import org.gosvea.pojo.VenueSchedule;
import org.gosvea.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VenueServiceImpl implements VenueService {


    @Autowired
    private VenueMapper venueMapper;
    @Override
    public void add(Venue venue) {


        venueMapper.add(venue);
    }

    @Override
    public PageResponse<Venue> list(Integer pageNum, Integer pageSize,String state, String city, Integer instructor, String paymentMethod, String timeZone) {
        PageResponse<Venue> ps=new PageResponse<>();
        // 打印分页参数
        System.out.println("Page number: " + pageNum);
        System.out.println("Page size: " + pageSize);

        PageHelper.startPage(pageNum,pageSize);
        List<Venue> lv =venueMapper.list(state,city,instructor,paymentMethod,timeZone);
        Page<Venue> pv=(Page<Venue>) lv;

        ps.setTotalElement(pv.getTotal());
        ps.setItems(pv.getResult());


        return ps;
    }

    @Override
    public void updateVenue(Venue venue) {
        venueMapper.updateVenue(venue);
    }

    @Override
    public void deleteVenue(Integer venueId) {
        venueMapper.deleteVenue(venueId);
    }

    @Override
    public void updateVenueSchedule(LocalDate date, LocalTime startTime, LocalTime endTime, Integer venueId) {
        venueMapper.updateVenueSchedule(date,startTime,endTime,venueId);
    }

    @Override
    public void addVenueSchedule(VenueSchedule venueSchedule) {
        venueMapper.addVenueSchedule(venueSchedule);
    }

    @Override
    public void deleteVenueSchedule(Integer venueId) {
        venueMapper.deleteVenueSchedule(venueId);
    }

    @Override
    public List<VenueSchedule> getVenueSchedule(Integer venueId) {
        List<VenueSchedule> venueScheduleslist=new ArrayList<>();
        venueScheduleslist=venueMapper.getVenueSchedule(venueId);

        return venueScheduleslist;
    }

    @Override
    public void deleteVenueScheduleSingle(Integer id) {
        venueMapper.deleteVenueScheduleSingle(id);
    }

    @Override
    public List<Venue> getAllVenues() {
        List<Venue> venues=venueMapper.getAllVenues();
        for (Venue venue : venues) {
            List<VenueSchedule> scheduleList = getVenueSchedule(venue.getId());
            venue.setScheduleList(scheduleList);
        }
        return venues;
    }


}

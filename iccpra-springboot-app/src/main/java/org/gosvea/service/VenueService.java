package org.gosvea.service;

import org.apache.ibatis.annotations.Param;
import org.gosvea.pojo.*;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface VenueService {
    void add(Venue venue);

    PageResponse<Venue> list(Integer pageNum, Integer pageSize, String state, String city, String icpisManager, String timeZone,String venueId);

    void updateVenue(Venue venue);

    void deleteVenue(String venueId);

    void updateVenueSchedule(LocalDate date, LocalTime startTime, LocalTime endTime, String venueId);

    void addVenueSchedule(VenueSchedule venueSchedule);

    void deleteVenueSchedule(String venueId);

    List<VenueSchedule> getVenueSchedule(String venueId);

    void deleteVenueScheduleSingle(String id);

     List<Venue> getAllVenues();

     double[] getLatLon(String address);

    void saveLatLon(double[] latlon, String id);

    String cleanAddress(String address);

    void updateVenueStatus(Integer id, Venue.VenueStatus venueStatus);

    Venue getVenueById(String venueId);

    void insertListVenues(List<Venue> venueList);

    void updateListVenues(List<Venue> venueList);

    String getVenueIdByAddress(String address);

    PageResponse<Venue> getNormalStatusVenues(Integer pageNum, Integer pageSize,String state,String timeZone);

    List<Venue> getVenueByInstructorId(String instructorId);
    @Async
    void addLatLonInformationForListVenues(List<Venue> venueList);
    @Async
    void updtaeLatLonInformationForOneVenue(Venue venue);

    List<Venue> getAllSpecStatusVenues(Venue.VenueStatus venueStatus);

    void addInstructorVenueRelation(List<String> instructorIds, String venueId);

    void deleteInstructorVenueRelationsByInstructorId(String id);

    void deleteInstructorVenueRelationsByVenueId(@Param("venueId") String venueId);

    boolean isInstructrorListChanged(List<String> currentInstructorIds,List<String> previousInstructorIds);

    boolean verifyVenueId(String id);

    PageResponse<Venue> icpislist(Integer pageNum, Integer pageSize, String state, String city, String icpisname, String timeZone, String venueId);
}

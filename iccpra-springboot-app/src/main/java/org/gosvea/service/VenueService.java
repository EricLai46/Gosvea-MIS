package org.gosvea.service;

import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Result;
import org.gosvea.pojo.Venue;
import org.gosvea.pojo.VenueSchedule;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface VenueService {
    void add(Venue venue);

    PageResponse<Venue> list(Integer pageNum, Integer pageSize, String state, String city, String icpisManager, String paymentMethod, String timeZone);

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

    Venue getVenueByInstructorId(Integer instructorId);
    @Async
    void addLatLonInformationForListVenues(List<Venue> venueList);
    @Async
    void updtaeLatLonInformationForOneVenue(Venue venue);
}

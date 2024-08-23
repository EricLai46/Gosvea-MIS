package org.gosvea.service;

import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Result;
import org.gosvea.pojo.Venue;
import org.gosvea.pojo.VenueSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface VenueService {
    void add(Venue venue);

    PageResponse<Venue> list(Integer pageNum, Integer pageSize, String state, String city, Integer instructor, String paymentMethod, String timeZone);

    void updateVenue(Venue venue);

    void deleteVenue(Integer venueId);

    void updateVenueSchedule(LocalDate date, LocalTime startTime, LocalTime endTime, Integer venueId);

    void addVenueSchedule(VenueSchedule venueSchedule);

    void deleteVenueSchedule(Integer venueId);

    List<VenueSchedule> getVenueSchedule(Integer venueId);

    void deleteVenueScheduleSingle(Integer id);

     List<Venue> getAllVenues();

     double[] getLatLon(String address);

    void saveLatLon(double[] latlon, Integer id);

    String cleanAddress(String address);

    void updateVenueStatus(Integer id, Venue.VenueStatus venueStatus);

    Venue getVenueById(Integer venueId);

    void insertListVenues(List<Venue> venueList);

    void updateListVenues(List<Venue> venueList);

    Integer getVenueIdByAddress(String address);
}

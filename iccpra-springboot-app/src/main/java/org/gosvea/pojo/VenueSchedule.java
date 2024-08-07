package org.gosvea.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class VenueSchedule extends Schedule{
    private Integer venueId;
    private String courseTitle;
    public  VenueSchedule(LocalDate date, Integer venueId, LocalTime startTime, LocalTime endTime,String courseTitle){
        super(date,startTime,endTime);
        this.venueId=venueId;
        this.courseTitle=courseTitle;
    }


    public VenueSchedule(){

    }
}

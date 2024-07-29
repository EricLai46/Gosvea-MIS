package org.gosvea.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class VenueSchedule extends Schedule{
    private Integer venueId;
    public  VenueSchedule(LocalDate date, Integer venueId, LocalTime startTime, LocalTime endTime){
        super(date,startTime,endTime);
        this.venueId=venueId;
    }


    public VenueSchedule(){

    }
}

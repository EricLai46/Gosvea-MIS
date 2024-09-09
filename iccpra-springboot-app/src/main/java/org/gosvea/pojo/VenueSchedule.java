package org.gosvea.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class VenueSchedule extends Schedule{
    private String venueId;
    private String courseTitle;
    private Double price;
    public  VenueSchedule(LocalDate date, String venueId, LocalTime startTime, LocalTime endTime,String courseTitle,Double price){
        super(date,startTime,endTime);
        this.venueId=venueId;
        this.courseTitle=courseTitle;
        this.price=price;
    }


    public VenueSchedule(){

    }
}

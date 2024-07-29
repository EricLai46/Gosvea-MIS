package org.gosvea.pojo;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CourseSchedule {

    private Integer id;
    private Integer instructorId;
    private Integer venueId;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isActive;
    private LocalDate date;

    public CourseSchedule(){

    }

    public CourseSchedule(Integer id,Integer instructorId, Integer venueId, LocalDate date, LocalTime startTime, LocalTime endTime){
        this.id=id;
        this.date=date;
        this.startTime=startTime;
        this.endTime=endTime;
        this.instructorId=instructorId;
        this.venueId=venueId;
        this.isActive=true;
    }
}

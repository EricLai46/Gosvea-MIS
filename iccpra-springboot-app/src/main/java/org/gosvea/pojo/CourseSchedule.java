package org.gosvea.pojo;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CourseSchedule extends Schedule{
    private Integer instructorId;
    private Integer venueId;
    private boolean isActive;


    public CourseSchedule(){

    }

    public CourseSchedule(Integer instructorId, Integer venueId, LocalDate date, LocalTime startTime, LocalTime endTime){
        super(date,startTime,endTime);
        this.instructorId=instructorId;
        this.venueId=venueId;
        this.isActive=true;
    }
}

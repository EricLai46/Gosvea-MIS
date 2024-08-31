package org.gosvea.pojo;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CourseSchedule extends Schedule{
    private Integer instructorId;
    private String venueId;
    private boolean isActive;
    private String address;
    private String courseTitle;
    private boolean isProcessed;
    public CourseSchedule(){

    }

    public CourseSchedule(Integer instructorId, String venueId, LocalDate date, LocalTime startTime, LocalTime endTime,String address,String courseTitle){
        super(date,startTime,endTime);
        this.instructorId=instructorId;
        this.venueId=venueId;
        this.isActive=false;
        this.address=address;
        this.courseTitle=courseTitle;

    }
}

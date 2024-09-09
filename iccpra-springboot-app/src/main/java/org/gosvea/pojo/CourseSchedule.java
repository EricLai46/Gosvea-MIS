package org.gosvea.pojo;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CourseSchedule extends Schedule{
    private String instructorId;
    private String venueId;
    private boolean isActive;
    private String address;
    private String courseTitle;
    private boolean isProcessed;
    private String registrationLink;
    private  Double price;
    private String timeZone;
    private String comments;
    private String icpisManager;
    private boolean isEnrollwareAdded;
    public CourseSchedule(){

    }

    public CourseSchedule(String instructorId, String venueId, LocalDate date, LocalTime startTime, LocalTime endTime,String address,String courseTitle,Double price,String registrationLink, String timeZone,String comments,String icpisManager,Boolean isEnrollwareAdded){
        super(date,startTime,endTime);
        this.instructorId=instructorId;
        this.venueId=venueId;
        this.isActive=false;
        this.address=address;
        this.courseTitle=courseTitle;
        this.price=price;
        this.registrationLink=registrationLink;
        this.timeZone=timeZone;
        this.comments=comments;
        this.icpisManager=icpisManager;
        this.isEnrollwareAdded=isEnrollwareAdded;
    }
}

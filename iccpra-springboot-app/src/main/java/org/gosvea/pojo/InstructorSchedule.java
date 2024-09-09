package org.gosvea.pojo;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class InstructorSchedule extends Schedule{

    private String instructorId;
    public InstructorSchedule(){

    }
    public InstructorSchedule(String instructorId, LocalDate date, LocalTime startTime,LocalTime endTime)
    {
        super(date,startTime,endTime);
        this.instructorId=instructorId;
    }
}

package org.gosvea.pojo;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class InstructorSchedule extends Schedule{

    private Integer instructorId;
    public InstructorSchedule(){

    }
    public InstructorSchedule(Integer instructorId, LocalDate date, LocalTime startTime,LocalTime endTime)
    {
        super(date,startTime,endTime);
        this.instructorId=instructorId;
    }
}

package org.gosvea.pojo;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Schedule {
    private LocalDate date;
    private Integer id;
    private LocalTime startTime;
    private LocalTime endTime;
    public Schedule(LocalDate date,LocalTime startTime,LocalTime endTime){
        this.date=date;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public Schedule(){

    }
}

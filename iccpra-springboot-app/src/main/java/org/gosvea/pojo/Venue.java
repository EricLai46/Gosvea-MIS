package org.gosvea.pojo;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Venue {
    private Integer id;
    @NotNull
    private Integer instructor;
    private String timeZone;
    @NotEmpty
    private String state;
    @NotEmpty
    private String city;
    @NotEmpty
    private String address;
    private String cancellationPolicy;
    private String paymentMode;
    private Double nonrefundableFee;
    private String fobKey;
    private Double deposit;
    private Double membershipFee;
    private Double usageFee;
    private String refundableStatus;
    private String bookMethod;
    private String registrationLink;
    private List<VenueSchedule> scheduleList;

    private static final Map<String,String> TIME_ZONE_MAP=new HashMap<>();

    static {
        TIME_ZONE_MAP.put("PTC","America/Los_Angeles");
        TIME_ZONE_MAP.put("EST","America/New_York");
        TIME_ZONE_MAP.put("MST","America/Denver");
        TIME_ZONE_MAP.put("CST","America/Chicago");
        TIME_ZONE_MAP.put("MNT","America/Boise");
    }

    public void generateSchedule(Integer venueid){
        ZoneId zoneId=ZoneId.of(TIME_ZONE_MAP.get(timeZone));
        ZonedDateTime startDateTime=ZonedDateTime.now(zoneId);
        ZonedDateTime endDateTime=startDateTime.plus(1, ChronoUnit.MONTHS);

        scheduleList.clear();

        for(ZonedDateTime dateTime=startDateTime;dateTime.isAfter(endDateTime); dateTime=dateTime.plusDays(1)){
            scheduleList.add(new VenueSchedule(dateTime.toLocalDate(),venueid, LocalTime.of(15,0),LocalTime.of(17,0)));

        }
    }


    public boolean needUpdateReminder(){
        ZoneId zoneId=ZoneId.of(TIME_ZONE_MAP.get(timeZone));
        LocalDate today=LocalDate.now(zoneId);
        LocalDate reminderDate=today.plus(3,ChronoUnit.WEEKS);

        return scheduleList.stream().anyMatch(schedule -> schedule.getDate().isBefore(reminderDate));
    }

}

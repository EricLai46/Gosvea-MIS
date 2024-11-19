package org.gosvea.pojo;


import jakarta.persistence.*;
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
    private String id;
    @NotNull
    //private String instructor;
    private String timeZone;
    @NotEmpty
    private String state;
    @NotEmpty
    private String city;
    @NotEmpty
    private String address;
    private String icpisManager;
    private String cancellationPolicy;
    private String paymentMode;
    private String nonrefundableFee;
    private String fobKey;
    private String deposit;
    private String membershipFee;
    private String usageFee;
    private String refundableStatus;
    private String bookMethod;
    private String registrationLink;
    private List<VenueSchedule> scheduleList;

    private Double latitude;
    private Double longitude;
    private  String instructorNames;

    //课程价格
    private Double cprPrice;
    private Double blsPrice;
    private Double cpradultPrice;
    private Double cprinstructorPrice;
    // VenueStatus 枚举类型的属性
    @Enumerated(EnumType.STRING)
    private VenueStatus venueStatus;


    @ManyToMany(mappedBy = "venues",fetch = FetchType.LAZY)

    private List<Instructor> instructors;


    // 静态的时区映射
    private static final Map<String, String> TIME_ZONE_MAP = new HashMap<>();

    static {
        TIME_ZONE_MAP.put("PTC", "America/Los_Angeles");
        TIME_ZONE_MAP.put("EST", "America/New_York");
        TIME_ZONE_MAP.put("MST", "America/Denver");
        TIME_ZONE_MAP.put("CST", "America/Chicago");
        TIME_ZONE_MAP.put("MNT", "America/Boise");
        TIME_ZONE_MAP.put("AKST", "America/Anchorage");
    }

    // 需要更新提醒的方法
    public boolean needUpdateReminder() {
        ZoneId zoneId = ZoneId.of(TIME_ZONE_MAP.get(timeZone));
        LocalDate today = LocalDate.now(zoneId);
        LocalDate reminderDate = today.plus(3, ChronoUnit.WEEKS);

        return scheduleList.stream().anyMatch(schedule -> schedule.getDate().isBefore(reminderDate));
    }



    // VenueStatus 枚举类
    public enum VenueStatus {
        NORMAL("NORMAL"),
        INSTRUCTORISSUE("INSTRUCTORISSUE"),
        VENUEISSUE("VENUEISSUE"),
        CLOSED("CLOSED"),
        INVESTIGATION("INVESTIGATION");

        private final String value;

        VenueStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static VenueStatus fromValue(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Value cannot be null");
            }
            // 转换输入值为大写，确保与枚举常量的比较一致
            String uppercaseValue = value.toUpperCase();
            for (VenueStatus status : values()) {
                if (status.value.equalsIgnoreCase(uppercaseValue)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }

    // 正确的 Getter 和 Setter 方法
    public VenueStatus getVenueStatus() {
        return venueStatus;
    }

    public void setVenueStatus(VenueStatus venueStatus) {
        this.venueStatus = venueStatus;
    }

}
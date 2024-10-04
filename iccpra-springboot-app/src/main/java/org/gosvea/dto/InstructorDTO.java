package org.gosvea.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
@Data
public class InstructorDTO {
    private String id;
    private String name;
    private List<String> venueIds; // 用于接收前端传递的 Instructor ID 列表

    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @NotEmpty
    private String state;
    @NotEmpty
    private String city;
    private String phoneNumber;

    private String email;
    @NotEmpty
    private String wageHour;
    private String salaryInfo;
    private Integer totalClassTimes;

    private String deposit;

    private String rentManikinNumbers;

    private String finance;

    private String rentStatus;

    private String fobKey;

    // Getters and Setters
    public List<String> getVenueIds() {
        return venueIds;
    }

    public void setVenueIds(List<String> venueIds) {
        this.venueIds = venueIds;
    }
}

package org.gosvea.pojo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Instructor {
    private Integer id;

    private Integer venueId;
    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @NotEmpty
    private String state;
    @NotEmpty
    private String city;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private String email;
    @NotEmpty
    private Integer wageHour;

    @NotEmpty
    private Integer totalClassTimes;

    private String deposit;

    private String rentManikinNumbers;

    private String finance;

    private String rentStatus;

    private String fobKey;


}

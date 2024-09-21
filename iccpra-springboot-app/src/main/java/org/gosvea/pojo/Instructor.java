package org.gosvea.pojo;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.annotations.ManyToAny;

import java.util.ArrayList;
import java.util.List;

@Data
public class Instructor {
    private String id;

    //private String venueId;
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
    private List<InstructorSchedule> scheduleList;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="instructor_venue",
            joinColumns = @JoinColumn(name="instructor_id"),
            inverseJoinColumns = @JoinColumn(name="venue_id")
    )
    private List<Venue> venues;

}

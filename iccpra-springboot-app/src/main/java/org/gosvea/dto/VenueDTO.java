package org.gosvea.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.gosvea.pojo.Venue;

import java.util.List;

@Data
public class VenueDTO {
    private String id;
    private String name;
    private List<String> instructorIds;
    private Venue.VenueStatus venueStatus;// 用于接收前端传递的 Instructor ID 列表
    private String icpisManager;
    private String address;
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
    private String city;
    private String timeZone;
    private String state;


    //课程价格
    private Double cprPrice;
    private Double blsPrice;
    private Double cpradultPrice;
    private Double cprinstructorPrice;
}

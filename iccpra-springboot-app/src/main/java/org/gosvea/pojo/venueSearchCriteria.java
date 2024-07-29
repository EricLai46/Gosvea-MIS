package org.gosvea.pojo;

import lombok.Data;

@Data
public class venueSearchCriteria {
    private String state;
    private String City;
    private String timeZone;
    private String paymentMethod;
    private Integer instructor;
}

package org.gosvea.dto;

import lombok.Data;

import java.util.List;
@Data
public class InstructorDTO {
    private String id;
    private String name;
    private List<String> venueIds; // 用于接收前端传递的 Instructor ID 列表

    // Getters and Setters
    public List<String> getVenueIds() {
        return venueIds;
    }

    public void setVenueIds(List<String> venueIds) {
        this.venueIds = venueIds;
    }
}

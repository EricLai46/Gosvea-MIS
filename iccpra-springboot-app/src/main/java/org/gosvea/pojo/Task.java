package org.gosvea.pojo;


import lombok.Data;
import org.apache.ibatis.annotations.Insert;

@Data
public class Task {


    private Integer id;

    private String description;

    private Boolean isCompleted;

    private String icpisManager;
}

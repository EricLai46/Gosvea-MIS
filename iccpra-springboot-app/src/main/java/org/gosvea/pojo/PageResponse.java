package org.gosvea.pojo;


import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    private List<T> items;
    private long totalElement;



}

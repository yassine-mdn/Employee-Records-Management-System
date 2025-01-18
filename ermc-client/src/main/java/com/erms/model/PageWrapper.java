package com.erms.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageWrapper<T> {
    @JsonProperty("totalPages")
    private int totalPages;
    @JsonProperty("totalElements")
    private long totalElements;
    @JsonProperty("hasNext")
    private boolean hasNext;
    @JsonProperty("hasPrevious")
    private boolean hasPrevious;
    @JsonProperty("page")
    private List<T> page;

}

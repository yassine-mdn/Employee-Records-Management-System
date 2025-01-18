package com.erms.model;


import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageWrapper<T> {
  private int totalPages;
  private long totalElements;
  private boolean hasNext;
  private boolean hasPrevious;
  private List<T> page;

}

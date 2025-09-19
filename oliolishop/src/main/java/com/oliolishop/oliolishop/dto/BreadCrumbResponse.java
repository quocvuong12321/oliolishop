package com.oliolishop.oliolishop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BreadCrumbResponse {
    String url;
    String name;
    String categoryId;
}

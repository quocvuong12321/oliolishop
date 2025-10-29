package com.oliolishop.oliolishop.dto.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNextPage;
    private boolean hasPreviousPage;

    public static <T> PaginatedResponse<T> fromSpringPage(Page<T> springPage) {
        return new PaginatedResponse<>(
                springPage.getContent(),             // content
                springPage.getNumber(),              // page (số trang hiện tại, bắt đầu từ 0)
                springPage.getSize(),                // size (kích thước trang)
                springPage.getTotalElements(),       // totalElements (tổng số record)
                springPage.getTotalPages(),          // totalPages (tổng số trang)
                springPage.hasNext(),                // hasNextPage (còn trang kế tiếp không)
                springPage.hasPrevious()             // hasPreviousPage (còn trang trước không)
        );
    }
}

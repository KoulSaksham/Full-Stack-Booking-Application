package com.saksham.booking_application.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaginatedResponse<T> {

    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("totalElements")
    private long totalElements;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("limit")
    private int limit;

    @JsonProperty("orderBy")
    private String orderBy;

    @JsonProperty("order")
    private String order;

    @JsonProperty("offset")
    private Long offset;

    public PaginatedResponse(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.limit = page.getSize();
        this.offset = page.getPageable().getOffset();
        Sort sort = page.getSort();
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order firstOrder = sort.iterator().next();
            this.orderBy = firstOrder.getProperty();
            this.order = firstOrder.getDirection().name();
        } else {
            this.order = null;
            this.orderBy = null;
        }
    }

    public PaginatedResponse(List<T> content, long totalElements, int limit, long offset, String order,
            String orderBy) {
        this.content = content;
        this.totalElements = totalElements;
        this.limit = limit;
        this.offset = offset;
        this.order = order;
        this.orderBy = orderBy;
        this.totalPages = totalElements % limit == 0 ? (int) totalElements / limit : (int) totalElements / limit + 1;
    }

    public static <T> PaginatedResponse<T> builder() {
        return new PaginatedResponse<T>();
    }

    public PaginatedResponse<T> content(List<T> content) {
        this.content = content;
        return this;
    }

    public PaginatedResponse<T> totalElements(long totalElements) {
        this.totalElements = totalElements;
        return this;
    }

    public PaginatedResponse<T> totalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public PaginatedResponse<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public PaginatedResponse<T> offset(Long offset) {
        this.offset = offset;
        return this;
    }

    public PaginatedResponse<T> orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public PaginatedResponse<T> order(String order) {
        this.order = order;
        return this;
    }

    public PaginatedResponse<T> build() {
        return this;
    }
}
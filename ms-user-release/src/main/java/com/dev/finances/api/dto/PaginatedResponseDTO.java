package com.dev.finances.api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PaginatedResponseDTO<T> {
    private final List<T> items;
    private final Long totalRecords;
}

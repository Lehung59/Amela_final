package com.example.demo.form;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SearchForm {
    private Integer month;
    private Integer year;
    private Integer page;
    private Integer size;
    private String keyword;
    private LocalDate fromDate;
    private LocalDate toDate;
}

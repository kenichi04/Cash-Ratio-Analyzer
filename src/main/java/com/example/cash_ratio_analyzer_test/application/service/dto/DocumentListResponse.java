package com.example.cash_ratio_analyzer_test.application.service.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DocumentListResponse {
    private Metadata metadata;
    private List<Result> results;

    public int getCount() {
        return metadata.getResultSet().getCount();
    }

    public String getStatus() {
        return metadata.getStatus();
    }
}

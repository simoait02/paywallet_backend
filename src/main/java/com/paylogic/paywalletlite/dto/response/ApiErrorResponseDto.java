package com.paylogic.paywalletlite.dto.response;

import java.util.Map;

public class ApiErrorResponseDto {

    private String status;
    private String message;
    private Map<String, String> details;
    private long timestamp;

    public ApiErrorResponseDto() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiErrorResponseDto(String status, String message, Map<String, String> details) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = System.currentTimeMillis();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, String> getDetails() { return details; }
    public void setDetails(Map<String, String> details) { this.details = details; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
package com.overops.report.service.model;

import java.util.List;

public class QualityReportTestResults {
    private boolean passed = false;
    private String message = "";
    private long errorCount = 0;
    private List<EventVisualizationModel> events = null;

    public QualityReportTestResults() {

    }

    public QualityReportTestResults(boolean passed, String message) {
        this.passed = passed;
        this.message = message;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<EventVisualizationModel> getEvents() {
        return events;
    }

    public void setEvents(List<EventVisualizationModel> events) {
        this.events = events;
        errorCount = events != null ? events.size() : 0;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        if (this.events != null) {
            throw new IllegalStateException("Can not set error count when event list is not null.");
        }
        this.errorCount = errorCount;
    }
}
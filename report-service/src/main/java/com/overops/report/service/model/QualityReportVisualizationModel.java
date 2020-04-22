package com.overops.report.service.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QualityReportVisualizationModel {
  
    public enum ReportStatus {
        PASSED,
        FAILED,
        WARNING
    }

    private enum WebResource {
        DANGER_ICON("web/img/icn-danger.svg"), 
        QUESTION_ICON("web/img/icn-question.svg"), 
        SUCCESS_ICON("web/img/icn-success.svg"),
        TIMES_ICON("web/img/icn-times.svg"), 
        WARNING_ICON("web/img/icn-warning.svg"),
        LOGO_ICON("web/img/overops-logo.svg"),
        CSS("web/css/style.css"),
        BODY_HTML("web/html/body.html"),
        ERROR_GATE_HEADER_HTML("web/html/errorGateHeader.html"),
        ERROR_GATE_SUMMARY_HTML("web/html/errorGateSummary.html"),
        EVENT_DETAILS_HTML("web/html/eventDetails.html"),
        EVENTS_TABLE_HTML("web/html/eventsTable.html"),
        EXCEPTION_HTML("web/html/exception.html"),
        REPORT_HTML("web/html/report.html");

        private final String filePath;

        WebResource(String filePath) {
            this.filePath = filePath;
        }

        public String getFilePath() {
            return filePath;
        }
    };

    private enum TestType {
        NEW_ERRORS, 
        RESURFACED_ERRORS, 
        TOTAL_ERRORS,
        UNIQUE_ERRORS, 
        CRITICAL_ERRORS,
        REGRESSION_ERRORS
    };

    ReportStatus statusCode = ReportStatus.FAILED;
    String statusMsg = "";

    QualityReportTestResults newErrorsTestResults;
    QualityReportTestResults resurfacedErrorsTestResults;
    QualityReportTestResults criticalErrorsTestResults;
    QualityReportTestResults regressionErrorsTestResults;
    QualityReportTestResults totalErrorsTestResults;
    QualityReportTestResults uniqueErrorsTestResults;

    List<EventVisualizationModel> topEvents = Collections.emptyList();

    QualityReportExceptionDetails exceptionDetails;

    private transient String[] webResources = new String[WebResource.values().length];

    public QualityReportVisualizationModel() {
    }

    public ReportStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(ReportStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public QualityReportTestResults getNewErrorsTestResults() {
        return newErrorsTestResults;
    }

    public void setNewErrorsTestResults(QualityReportTestResults newErrorsTestResults) {
        this.newErrorsTestResults = newErrorsTestResults;
    }

    public QualityReportTestResults getResurfacedErrorsTestResults() {
        return resurfacedErrorsTestResults;
    }

    public void setResurfacedErrorsTestResults(QualityReportTestResults resurfacedErrorsTestResults) {
        this.resurfacedErrorsTestResults = resurfacedErrorsTestResults;
    }

    public QualityReportTestResults getCriticalErrorsTestResults() {
        return criticalErrorsTestResults;
    }

    public void setCriticalErrorsTestResults(QualityReportTestResults criticalErrorsTestResults) {
        this.criticalErrorsTestResults = criticalErrorsTestResults;
    }

    public QualityReportTestResults getRegressionErrorsTestResults() {
        return regressionErrorsTestResults;
    }

    public void setRegressionErrorsTestResults(QualityReportTestResults regressionErrorsTestResults) {
        this.regressionErrorsTestResults = regressionErrorsTestResults;
    }

    public QualityReportTestResults getTotalErrorsTestResults() {
        return totalErrorsTestResults;
    }

    public void setTotalErrorsTestResults(QualityReportTestResults totalErrorsTestResults) {
        this.totalErrorsTestResults = totalErrorsTestResults;
    }

    public QualityReportTestResults getUniqueErrorsTestResults() {
        return uniqueErrorsTestResults;
    }

    public void setUniqueErrorsTestResults(QualityReportTestResults uniqueErrorsTestResults) {
        this.uniqueErrorsTestResults = uniqueErrorsTestResults;
    }

    public List<EventVisualizationModel> getTopEvents() {
        return topEvents;
    }

    public void setTopEvents(List<EventVisualizationModel> topEvents) {
        this.topEvents = topEvents;
    }

    public HtmlParts getHtmlParts() throws IOException {
        HtmlParts htmlParts = new HtmlParts();
        htmlParts.setBody(getHtmlBody());
        htmlParts.setCss(getWebResource(WebResource.CSS));
        return htmlParts;
    }

    public String toHtml() throws IOException {
        HtmlParts htmlParts = getHtmlParts();
        String html = getWebResource(WebResource.REPORT_HTML).replace("<style></style>", "<style>" + htmlParts.getCss() + "</style>");
        html = html.replace("<body></body>", "<body>" + htmlParts.getBody() + "</body>");
        return html;
    }

    private String getWebResource(WebResource resourceType) {
        String resource = "";
        if (resourceType != null) {
            resource = webResources[resourceType.ordinal()];
            if (resource == null) {
                try {
                    resource = readFile(resourceType.getFilePath());
                } catch (IOException e) {
                    resource = "";
                }
                webResources[resourceType.ordinal()] = resource;
            }
        }
        return resource;
    }

    private String readFile(String relativeFilePath) throws IOException {
        String fileContents = "";

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(relativeFilePath);
        Reader reader = new InputStreamReader(stream);
        int intValueOfChar;
        while ((intValueOfChar = reader.read()) != -1) {
            fileContents += (char) intValueOfChar;
        }
        reader.close();
        return fileContents;
    }

    private String getHtmlBody() {
        String htmlBody = getWebResource(WebResource.BODY_HTML);
        htmlBody = htmlBody.replace("<img id=\"logo\"></img>", getWebResource(WebResource.LOGO_ICON));

        String cssClass = null;
        String icon = null; 
        ReportStatus statusCode = getStatusCode();
        switch (statusCode) {
            case PASSED:
                cssClass = "alert-success";
                icon = getWebResource(WebResource.SUCCESS_ICON);
                break;
            case WARNING:
                cssClass = "alert-warning";
                icon = getWebResource(WebResource.WARNING_ICON);
                break;
            default:
                cssClass = "alert-danger";
                icon = getWebResource(WebResource.DANGER_ICON);
                break;
        }
        htmlBody = htmlBody.replace("report-status", cssClass);
        htmlBody = htmlBody.replace("<img id=\"reportStatusIcon\"></img>", icon);
        htmlBody = htmlBody.replace("<span id=\"reportStatusMsg\"></span>", getStatusMsg());

        htmlBody = htmlBody.replace("<tr id=\"newErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.NEW_ERRORS));
        htmlBody = htmlBody.replace("<tr id=\"resurfacedErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.RESURFACED_ERRORS));
        htmlBody = htmlBody.replace("<tr id=\"totalErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.TOTAL_ERRORS));
        htmlBody = htmlBody.replace("<tr id=\"uniqueErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.UNIQUE_ERRORS));
        htmlBody = htmlBody.replace("<tr id=\"criticalErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.CRITICAL_ERRORS));
        htmlBody = htmlBody.replace("<tr id=\"regressionErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.REGRESSION_ERRORS));

        htmlBody = htmlBody.replace("<div id=\"newErrorsHeader\"></div>", getEventsHeader(TestType.NEW_ERRORS));
        htmlBody = htmlBody.replace("<table id=\"newErrorEvents\"></table>", getEventsTable(TestType.NEW_ERRORS));
        htmlBody = htmlBody.replace("<div id=\"resurfacedErrorsHeader\"></div>", getEventsHeader(TestType.RESURFACED_ERRORS));
        htmlBody = htmlBody.replace("<table id=\"resurfacedErrorEvents\"></table>", getEventsTable(TestType.RESURFACED_ERRORS));
        htmlBody = htmlBody.replace("<div id=\"totalErrorsHeader\"></div>", getEventsHeader(TestType.TOTAL_ERRORS));
        htmlBody = htmlBody.replace("<div id=\"uniqueErrorsHeader\"></div>", getEventsHeader(TestType.UNIQUE_ERRORS));
        htmlBody = htmlBody.replace("<table id=\"topErrorEvents\"></table>", getEventsTable(TestType.TOTAL_ERRORS));
        htmlBody = htmlBody.replace("<div id=\"criticalErrorsHeader\"></div>", getEventsHeader(TestType.CRITICAL_ERRORS));
        htmlBody = htmlBody.replace("<table id=\"criticalErrorEvents\"></table>", getEventsTable(TestType.CRITICAL_ERRORS));
        htmlBody = htmlBody.replace("<div id=\"regressionErrorsHeader\"></div>", getEventsHeader(TestType.REGRESSION_ERRORS));
        htmlBody = htmlBody.replace("<table id=\"regressionErrorEvents\"></table>", getEventsTable(TestType.REGRESSION_ERRORS));

        return htmlBody;
    }

    private String getErrorGateSummaryHtml(TestType testType) {
        String html = "";
        QualityReportTestResults testResults = null;
    
        boolean passed = false;
        String anchorRef = "";
        String anchorDesc = "";
        long errorCount = 0;
        String icon = "";

        switch (testType) {
            case NEW_ERRORS:
                testResults = getNewErrorsTestResults(); 
                anchorRef = "#new-gate";
                anchorDesc = "New";
                break;
            case CRITICAL_ERRORS:
                testResults = getCriticalErrorsTestResults(); 
                anchorRef = "#critical-gate";
                anchorDesc = "Critical";
                break;
            case REGRESSION_ERRORS:
                testResults = getRegressionErrorsTestResults(); 
                anchorRef = "#increasing-gate";
                anchorDesc = "Increasing";
               break;
            case RESURFACED_ERRORS:
                testResults = getResurfacedErrorsTestResults(); 
                anchorRef = "#resurfaced-gate";
                anchorDesc = "Resurfaced";
                break;
            case TOTAL_ERRORS:
                testResults = getTotalErrorsTestResults(); 
                anchorRef = "#total-gate";
                anchorDesc = "Total";
                break;
            case UNIQUE_ERRORS:
                testResults = getUniqueErrorsTestResults(); 
                anchorRef = "#unique-gate";
                anchorDesc = "Unique";
                break;
        }

        if (testResults != null) {
            passed = testResults.isPassed();
            errorCount = testResults.getErrorCount();
            icon = passed ? getWebResource(WebResource.SUCCESS_ICON) : getWebResource(WebResource.TIMES_ICON);

            html = getWebResource(WebResource.ERROR_GATE_SUMMARY_HTML);

            html = html.replace("Failed", passed ? "Passed" : "Failed");
            html = html.replace("0", Long.toString(errorCount));
            html = html.replace("anchor-ref", anchorRef);
            html = html.replace("anchor-desc", anchorDesc);
            html = html.replace("<img></img>", icon);    
        }
       
        return html;
    }

    private String getEventsHeader(TestType testType) {
        String html = "";
        QualityReportTestResults testResults = null;
    
        String anchorRef = "";

        switch (testType) {
            case NEW_ERRORS:
                testResults = getNewErrorsTestResults(); 
                anchorRef = "#new-gate";
                break;
            case CRITICAL_ERRORS:
                testResults = getCriticalErrorsTestResults(); 
                anchorRef = "#critical-gate";
                break;
            case REGRESSION_ERRORS:
                testResults = getRegressionErrorsTestResults(); 
                anchorRef = "#increasing-gate";
               break;
            case RESURFACED_ERRORS:
                testResults = getResurfacedErrorsTestResults(); 
                anchorRef = "#resurfaced-gate";
                break;
            case TOTAL_ERRORS:
                testResults = getTotalErrorsTestResults(); 
                anchorRef = "#total-gate";
                break;
            case UNIQUE_ERRORS:
                testResults = getUniqueErrorsTestResults(); 
                anchorRef = "#unique-gate";
                break;
        }

        if (testResults != null) {

            html = getWebResource(WebResource.ERROR_GATE_HEADER_HTML);
            html = html.replace("anchor-ref", anchorRef);
            html = html.replace("summary", testResults.getMessage());

            if (testResults.isPassed()) {
                if ((testType == TestType.NEW_ERRORS) || (testType == TestType.RESURFACED_ERRORS) || (testType == TestType.RESURFACED_ERRORS)|| (testType == TestType.REGRESSION_ERRORS)) {
                    html = html.replace("mb-2", "");
                }
                html = html.replace("<img></img>", getWebResource(WebResource.SUCCESS_ICON));
                html += "<p class=\"ml-2 muted\">Nothing to report</p>";
            }  else {
                html = html.replace("<img></img>", getWebResource(WebResource.DANGER_ICON));
            }  
        }    
        return html;
    }

    private String getEventsTable(TestType testType) {
        String html = "";  
        QualityReportTestResults testResults = null;
        List<EventVisualizationModel> events = new ArrayList<>();

        switch (testType) {
            case NEW_ERRORS:
                testResults = getNewErrorsTestResults();
                if (testResults != null) {
                    events = testResults.getEvents();
                }
                break;
            case CRITICAL_ERRORS:
                testResults = getCriticalErrorsTestResults();
                if (testResults != null) {
                    events = testResults.getEvents();
                }
                break;
            case REGRESSION_ERRORS:
                testResults = getRegressionErrorsTestResults();
                if (testResults != null) {
                    events = testResults.getEvents();
                }
                break;
            case RESURFACED_ERRORS:
                testResults = getRegressionErrorsTestResults();
                if (testResults != null) {
                    events = testResults.getEvents();
                }
                break;
            case TOTAL_ERRORS:
                testResults = getTotalErrorsTestResults();
                if (testResults != null) {
                    events = getTopEvents();
                }
                break;
            case UNIQUE_ERRORS:
                testResults = getUniqueErrorsTestResults();
                if (testResults != null) {
                    events = getTopEvents();
                }
                break;
        }

        if ((testResults != null) && !testResults.isPassed()){
            html = getWebResource(WebResource.EVENTS_TABLE_HTML);
            if (testType == TestType.REGRESSION_ERRORS) {
                html = html.replace("<th>Volume</th>", "<th>Volume / Rate</th>");
                html = html.replace("<th>Type</th>", "");
            }
            String eventsHtml = "";
            if (events != null) {
                for (EventVisualizationModel event : events) {
                    String eventHtml = getWebResource(WebResource.EVENT_DETAILS_HTML);
                    eventHtml = eventHtml.replace("arcLink", convertNullToString(event.getArcLink()));
                    eventHtml = eventHtml.replace("summary", convertNullToString(event.getEventSummary()));
                    eventHtml = eventHtml.replace("applications", convertNullToString(event.getApplications()));
                    eventHtml = eventHtml.replace("introducedBy", convertNullToString(event.getIntroducedBy()));
                    eventHtml = eventHtml.replace("eventRate", convertNullToString(event.getEventRate()));
                    eventHtml = eventHtml.replace("eventType", convertNullToString(event.getType()));
    
                    eventsHtml += eventHtml;
                }
            }
            html = html.replace("<tr class=\"events\"></tr>", eventsHtml);
        }
        return html;
    }

    private String convertNullToString(String string) {
        return string == null ? "" : string;
    }
}

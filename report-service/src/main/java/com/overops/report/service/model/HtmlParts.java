package com.overops.report.service.model;

public class HtmlParts {
    private String css = "";
    private String body = "";

    public HtmlParts() {
    }

    public HtmlParts(String body, String css) {
        this.css = css;
        this.body = body;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
}
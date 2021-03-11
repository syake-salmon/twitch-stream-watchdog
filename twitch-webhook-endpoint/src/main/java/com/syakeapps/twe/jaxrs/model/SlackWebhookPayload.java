package com.syakeapps.twe.jaxrs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SlackWebhookPayload {

    @JsonProperty("username")
    private String userName;

    @JsonProperty("text")
    private String text;

    public SlackWebhookPayload() {
        // NOP
    }

    public SlackWebhookPayload(String userName, String text) {
        this.userName = userName;
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

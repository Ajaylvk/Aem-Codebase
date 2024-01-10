package com.softwareag.core.services.table;

import java.net.HttpURLConnection;

public class TableActionResult {
    private boolean success = true;
    private String message = "";
    private long durationInMillis;
    private int responseCode = HttpURLConnection.HTTP_OK;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public long getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(final long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
    }

    public void setFailureMessage(final String message) {
        setSuccess(false);
        setMessage(message);
    }

}

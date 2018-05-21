package com.signlanguage.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by marcos on 17/03/18.
 */

public class ServerJsonResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("jobs")
    @Expose
    private List<JobJson> jobs;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<JobJson> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobJson> jobs) {
        this.jobs = jobs;
    }
}

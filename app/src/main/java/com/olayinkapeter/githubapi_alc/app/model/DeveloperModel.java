package com.olayinkapeter.githubapi_alc.app.model;

/**
 * Created by Olayinka_Peter on 3/4/2017.
 */

public class DeveloperModel {
    private String developerID, developerUserName, developerImageURL, developerHTMLURL;

    public DeveloperModel(String developerID, String developerUserName, String developerImageURL, String developerHTMLURL) {
        this.developerID = developerID;
        this.developerUserName = developerUserName;
        this.developerImageURL = developerImageURL;
        this.developerHTMLURL = developerHTMLURL;
    }

    public String getDeveloperID() {
        return developerID;
    }

    public String getDeveloperUserName() {
        return developerUserName;
    }

    public String getDeveloperImageURL() {
        return developerImageURL;
    }

    public String getDeveloperHTMLURL() {
        return developerHTMLURL;
    }

    public void setDeveloperID(String developerID) {
        this.developerID = developerID;
    }

    public void setDeveloperUserName(String developerUserName) {
        this.developerUserName = developerUserName;
    }

    public void setDeveloperImageURL(String developerImageURL) {
        this.developerImageURL = developerImageURL;
    }

    public void setDeveloperHTMLURL(String developerHTMLURL) {
        this.developerHTMLURL = developerHTMLURL;
    }
}

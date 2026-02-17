package com.mindwaresrl.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostMoldelFacebook {
    private String Title;
    private String UserName;
    private String UrlImageProfile;
    private String TextContend;
    private List<String> Reations;
    private Map<String,String> Comments;


    public PostMoldelFacebook() {
        Title = "";
        UserName = "";
        UrlImageProfile = "";
        TextContend = "";
        Reations = new ArrayList<>();
        Comments = new HashMap<>();
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUrlImageProfile() {
        return UrlImageProfile;
    }

    public void setUrlImageProfile(String urlImageProfile) {
        UrlImageProfile = urlImageProfile;
    }

    public String getTextContend() {
        return TextContend;
    }

    public void setTextContend(String textContend) {
        TextContend = textContend;
    }

    public List<String> getReations() {
        return Reations;
    }

    public void setReations(List<String> reations) {
        Reations = reations;
    }

    public Map<String, String> getComments() {
        return Comments;
    }

    public void setComments(Map<String, String> comments) {
        Comments = comments;
    }

    
}

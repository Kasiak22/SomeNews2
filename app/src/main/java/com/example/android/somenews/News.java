package com.example.android.somenews;

import java.util.Date;

public class News {
    private String title;
    private String sectionName;
    private Date date;
    private String url;
    private String authorName;


    public News(String theTitle, String theSectionName, Date theDate, String theUrl, String theAuthorName) {
        title = theTitle;
        sectionName = theSectionName;
        date = theDate;
        url = theUrl;
        authorName = theAuthorName;
    }

    //returns title
    public String getTitle() {
        return title;
    }

    //returns section name
    public String getSectionName() {
        return sectionName;
    }

    //returns date
    public Date getDate() {
        return date;
    }
    // returns news url
    public String getUrl() {
        return url;
    }
    //returns Author name
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String toString() {
        return "News{" +
                "title'" + title + '\'' +
                ", section name" + sectionName + '\'' +
                ", date" + date +
                ", url" + url +

                '}';
    }
}

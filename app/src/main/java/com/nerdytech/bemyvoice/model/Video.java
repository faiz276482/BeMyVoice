package com.nerdytech.bemyvoice.model;

public class Video {
    String video;
    String author;
    String upload_date;
    int upvotes;
    int downvotes;

    public  Video(){}
    public Video(String video, String author, String upload_date, int upvotes, int downvotes) {
        this.video = video;
        this.author = author;
        this.upload_date = upload_date;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }
}

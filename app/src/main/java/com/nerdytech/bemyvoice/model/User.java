package com.nerdytech.bemyvoice.model;

public class User {
    private String name;
    private String email;
    private String uid;
    private String mobile;
    private String dob;
    private String profile_pic;

    public User() {
    }

    public User(String name, String email, String uid, String mobile, String dob, String profile_pic) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.mobile = mobile;
        this.dob = dob;
        this.profile_pic = profile_pic;
    }

    //Getter


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDob() {
        return dob;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    //Setter


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}

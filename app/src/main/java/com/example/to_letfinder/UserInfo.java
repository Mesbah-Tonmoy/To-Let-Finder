package com.example.to_letfinder;

public class UserInfo {
    private String userName;
    private String phoneNo;
    private String ppUrl;
    private String Uid;

    public UserInfo(String userName, String phoneNo, String ppUrl, String uid) {
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.ppUrl = ppUrl;
        Uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPpUrl() {
        return ppUrl;
    }

    public void setPpUrl(String ppUrl) {
        this.ppUrl = ppUrl;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}

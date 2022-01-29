package com.example.to_letfinder;

public class Data {

    private String UID;
    private String Daddress;
    private String Dnoofrooms;
    private String Ddescription;
    private String Drent;
    private String Darea;
    private String imageUrl;
    private String userName;
    private String phoneNo;
    private String ppUrl;
    private String Deadline;
    private String Latitude;
    private String Longitude;
    private String Category;

    public Data() {
    }

    public Data(String uid, String daddress, String dnoofrooms, String ddescription, String drent, String darea, String imageUrl, String userName, String phoneNo, String ppUrl, String deadline, String latitude, String longitude, String category) {
        UID = uid;
        Daddress = daddress;
        Dnoofrooms = dnoofrooms;
        Ddescription = ddescription;
        Drent = drent;
        Darea = darea;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.ppUrl = ppUrl;
        Deadline = deadline;
        Latitude = latitude;
        Longitude = longitude;
        Category = category;
    }

    public String getDaddress() {
        return Daddress;
    }

    public void setDaddress(String daddress) {
        Daddress = daddress;
    }

    public String getDnoofrooms() {
        return Dnoofrooms;
    }

    public void setDnoofrooms(String dnoofrooms) {
        Dnoofrooms = dnoofrooms;
    }

    public String getDdescription() {
        return Ddescription;
    }

    public void setDdescription(String ddescription) {
        Ddescription = ddescription;
    }

    public String getDrent() {
        return Drent;
    }

    public void setDrent(String drent) {
        Drent = drent;
    }

    public String getDarea() {
        return Darea;
    }

    public void setDarea(String darea) {
        Darea = darea;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public String getDeadline() {
        return Deadline;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}

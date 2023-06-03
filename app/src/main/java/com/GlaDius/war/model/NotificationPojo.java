package com.GlaDius.war.model;

public class NotificationPojo {

    private String id,title,message,image,url,created;

    public NotificationPojo() {
    }

    public NotificationPojo(String id, String title, String message, String image, String url, String created) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.image = image;
        this.url = url;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

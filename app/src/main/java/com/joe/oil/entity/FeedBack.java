package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by liangxiaojiang on 2017/5/8.
 */
@Table(name = "feedback")
public class FeedBack {
    private int id;
    private String title;
    private String userName;
    private String picId;
    private String description;
    private String isUploadSuccess; // 1：上传成功 2：上传失败
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsUploadSuccess() {
        return isUploadSuccess;
    }

    public void setIsUploadSuccess(String isUploadSuccess) {
        this.isUploadSuccess = isUploadSuccess;
    }
}

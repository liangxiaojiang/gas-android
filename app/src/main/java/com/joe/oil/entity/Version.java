package com.joe.oil.entity;

import java.io.Serializable;

/**
 * 版本检测请求返回的数据
 * Created by scar1et on 15-5-12.
 */
public class Version implements Serializable {

    private static final long serialVersionUID = -3121984612868819134L;

    private boolean enforce;
    private String ver;
    private String description;
    private String createdDate;
    private String downloadUrl;
    private String publishDate;

    public boolean isEnforce() {
        return enforce;
    }

    public void setEnforce(boolean enforce) {
        this.enforce = enforce;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}

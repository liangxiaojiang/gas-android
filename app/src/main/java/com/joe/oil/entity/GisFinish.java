package com.joe.oil.entity;

import android.R.integer;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * @author joe
 * @Description 管线巡护完毕后保存taskNo和管线名称
 */
@Table(name = "gis_finish")
public class GisFinish {

    private integer id;
    private String taskNo;
    private String lineName;
    private String userId;
    private String creatTime;
    private String status; // 上传状态 0：未上传 1：上传失败 2：上传成功
    private String endTime;
    private int gisNum; // 此条巡护记录对应的Gis条数

    /**
     * category=0//管线巡护
     * category=1//道路巡护
     * category=2//异常巡护
     */
    private int category;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public integer getId() {
        return id;
    }

    public void setId(integer id) {
        this.id = id;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getGisNum() {
        return gisNum;
    }

    public void setGisNum(int gisNum) {
        this.gisNum = gisNum;
    }


}

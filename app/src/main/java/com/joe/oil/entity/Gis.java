package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;

@Table(name = "gis")
public class Gis implements Serializable {

    private static final long serialVersionUID = -6489623606348674617L;

    private int id;
    private String gisId; //服务器返回的数据记录主键ID
    private String userId;
    private String time;
    private String longitude;    //经度
    private String latitude;    //纬度
    private String status;     //0:本条数据未上传  1：本条数据已上传失败 2:数据上传成功
    private String exceptionStatus;    //状态  1：正常   2：异常  
    private String num;        //编号
    private String tag;        //数据状态，第一条数据位start， 中间数据位doing， 最后一条为end
    private String deviceId;    //采集器设备号
    private String pics;    //上报异常信息的图片ID
    private String memo;    //上报异常信息的异常内容
    private String lindId;
    private String taskType;
    private String isPicIdUpload; // 0表示图片未上传  1表示图片已上传

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExceptionStatus() {
        return exceptionStatus;
    }

    public void setExceptionStatus(String exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getLindId() {
        return lindId;
    }

    public void setLindId(String lindId) {
        this.lindId = lindId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

	public String getGisId() {
		return gisId;
	}

	public void setGisId(String gisId) {
		this.gisId = gisId;
	}

	public String getIsPicIdUpload() {
		return isPicIdUpload;
	}

	public void setIsPicIdUpload(String isPicIdUpload) {
		this.isPicIdUpload = isPicIdUpload;
	}
}

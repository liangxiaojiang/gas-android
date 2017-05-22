package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;

@Table(name = "exception")
public class UploadException implements Serializable {

	private static final long serialVersionUID = 8178392829745895809L;

	private int id;
	private String deviceName;
	private String pointName;
	private String deviceCode;
	private String workTypeId;
	private String workTypeName;
	private String userId;
	private String category;
	private String picId;
	private String description;
	private String isUploadSuccess; // 1：上传成功 2：上传失败
	private String time;
	private String workId; // 由异常生成的任务的Id，来自服务器返回
	private String historyId; // 服务器上记录此条异常数据的Id，与workId一并返回
	private String result;	//参数异常派工时巡检结果
	private String treatmentAdvice;		//参数异常派工时的处理意见
	private String itemId;		//参数异常派工时的巡检项id
	private String fromWhere;		//异常来源   1：本地    2：网络   3：将来自网络的异常重新编辑过
	private String patrolTime; // 一天中的某个巡检时间





	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getWorkTypeId() {
		return workTypeId;
	}

	public void setWorkTypeId(String workTypeId) {
		this.workTypeId = workTypeId;
	}

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}

	public String getWorkId() {
		return workId;
	}

	public String getHistoryId() {
		return historyId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTreatmentAdvice() {
		return treatmentAdvice;
	}

	public void setTreatmentAdvice(String treatmentAdvice) {
		this.treatmentAdvice = treatmentAdvice;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getFromWhere() {
		return fromWhere;
	}

	public void setFromWhere(String fromWhere) {
		this.fromWhere = fromWhere;
	}

	public String getPatrolTime() {
		return patrolTime;
	}

	public void setPatrolTime(String patrolTime) {
		this.patrolTime = patrolTime;
	}




}

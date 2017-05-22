package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "picture")
public class Picture {
	private int id; // 本地数据库的图片id
	private String picId; // 服务器返回的图片id
	private String name;
	private int type; // 0:巡检输入界面图片 1:巡检选择界面图片 2:上报异常界面图片 3:完成任务界面图片 4:管线巡护界面图片
	private String typeOfId; // 图片所对应类型的id
	private String url; // 图片存放的路径
	private String createTime;
	private String chargerId;
	private String patrolTime; // 一天中的某个巡检时间
	private int isUploadSuccess; // 0：未上传成功 1：上传成功 2：后台上传成功
	private int isWrokUpdate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTypeOfId() {
		return typeOfId;
	}

	public void setTypeOfId(String typeOfId) {
		this.typeOfId = typeOfId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}

	public int getIsUploadSuccess() {
		return isUploadSuccess;
	}

	public void setIsUploadSuccess(int isUploadSuccess) {
		this.isUploadSuccess = isUploadSuccess;
	}

	public int getIsWrokUpdate() {
		return isWrokUpdate;
	}

	public void setIsWrokUpdate(int isWrokUpdate) {
		this.isWrokUpdate = isWrokUpdate;
	}

	public String getPatrolTime() {
		return patrolTime;
	}

	public void setPatrolTime(String patrolTime) {
		this.patrolTime = patrolTime;
	}

	
}
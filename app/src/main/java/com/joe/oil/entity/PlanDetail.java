package com.joe.oil.entity;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

@SuppressWarnings("serial")
@Table(name = "plan_detail")
public class PlanDetail implements Serializable, Comparable<PlanDetail> {
	private int id;
	private String planDetailId;
	private String planId;
	private String itemId;
	private String itemName;
	private Integer pointId;
	private String pointName;
	private String chargerId;
	private String officeId;
	private String officeName;
	private String status; // 1、未巡检 2、巡检完成 3、巡检第二次读卡完成 4、巡检且提交
	private String result;
	private String memo;
	private String sort;
	private String code;
	private String handleAdvice;
	private String updateTime;
	private String picId;
	private String upValue;
	private String tag; // 启停项必填判断
	private String downValue;
//	private String unit;
	private String itemType;
	private String videoId;
	private String itemUnit; // 巡检项单位
	private String duration; // 巡检时间波动范围
	private String planType; // 计划类型 1：日计划 2：周计划 3：月计划
	private String upTime; // 实际巡检时间上限
	private String downTime; // 实际巡检时间下限
	private String type; // 计划类型， 1：巡站 2：巡井
	private String createTime; // 创建时间
	private String patrolDate; // 巡检时间（某一天）
	private String patrolTime; // 一天中的某个巡检时间
	private String createById; // 创建人Id
	private String exceptionStatus; // 异常状态 1正常， 2异常， 3漏巡
	private String handleMemo; // 异常处理结果
	private String handleMemoUpload; // 异常处理结果是否上传 1：异常结果上传， 0：异常结果未上传
	private String workId;
	private String isPicIdUpdate; // 标志此条计划如果有图片时，是否已经将图片Id更新至服务器, 0表示否 1表示是
	private String isRequiredToWrite; //标志是否为必填项
	private String tips;
	private String logging;

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlanDetailId() {
		return planDetailId;
	}

	public void setPlanDetailId(String planDetailId) {
		this.planDetailId = planDetailId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getPointId() {
		return pointId;
	}

	public void setPointId(Integer pointId) {
		this.pointId = pointId;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHandleAdvice() {
		return handleAdvice;
	}

	public void setHandleAdvice(String handleAdvice) {
		this.handleAdvice = handleAdvice;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

//	public String getUnit() {
//		return unit;
//	}
//
//	public void setUnit(String unit) {
//		this.unit = unit;
//	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getUpValue() {
		return upValue;
	}

	public void setUpValue(String upValue) {
		this.upValue = upValue;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getDownValue() {
		return downValue;
	}

	public void setDownValue(String downValue) {
		this.downValue = downValue;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}

	public String getDownTime() {
		return downTime;
	}

	public void setDownTime(String downTime) {
		this.downTime = downTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPatrolDate() {
		return patrolDate;
	}

	public void setPatrolDate(String patrolDate) {
		this.patrolDate = patrolDate;
	}

	public String getPatrolTime() {
		return patrolTime;
	}

	public void setPatrolTime(String patrolTime) {
		this.patrolTime = patrolTime;
	}

	public String getCreateById() {
		return createById;
	}

	public void setCreateById(String createById) {
		this.createById = createById;
	}

	public String getExceptionStatus() {
		return exceptionStatus;
	}

	public void setExceptionStatus(String exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}

	public String getHandleMemo() {
		return handleMemo;
	}

	public void setHandleMemo(String handleMemo) {
		this.handleMemo = handleMemo;
	}

	public int compareTo(PlanDetail p) {
		if (p.getSort() == null) {
			return this.getPointId().compareTo(p.getPointId());
		} else {
			return this.getSort().compareTo(p.getSort());
		}
	}

	public String getHandleMemoUpload() {
		return handleMemoUpload;
	}

	public void setHandleMemoUpload(String handleMemoUpload) {
		this.handleMemoUpload = handleMemoUpload;
	}

	public String getItemUnit() {
		return itemUnit;
	}

	public void setItemUnit(String itemUnit) {
		this.itemUnit = itemUnit;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getIsPicIdUpdate() {
		return isPicIdUpdate;
	}

	public void setIsPicIdUpdate(String isPicIdUpdate) {
		this.isPicIdUpdate = isPicIdUpdate;
	}

	public String getIsRequiredToWrite() {
		return isRequiredToWrite;
	}

	public void setIsRequiredToWrite(String isRequiredToWrite) {
		this.isRequiredToWrite = isRequiredToWrite;
	}

	public String getLogging() {
		return logging;
	}

	public void setLogging(String logging) {
		this.logging = logging;
	}
}

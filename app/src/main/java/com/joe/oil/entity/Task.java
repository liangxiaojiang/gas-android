package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @description task实体
 * @author baiqiao
 * @data: 2014年7月4日 下午2:32:21
 * @email baiqiao@lanbaoo.com
 */

@Table(name = "task")
public class Task implements Serializable ,Comparable<Task>{
	private static final long serialVersionUID = 1L;
	private int id;
	private String taskId;
	private String name;
	private String deviceNames;
	private String deviceIds;
	private String createTime;
	private String deadTime;
	private String riskTips;
	private String controlTips;
	private String reason;
	private String workNo;
	private String interval;
	private String operateCard;
	private String operateCardUrl;
	private String orderId;
	private String orderNo;
	private String chargerId;
	private String partnerId;
	private String creatorId;
	private String vehicleId;
	private String taskTypeId;
	private String chargerName;
	private String partnerName;
	private String creatorName;
	private String vehicleNumber;
	private String vehicleName;
	private String vehicleDriverName;
	private String vehicleDriverPhone;
	private String taskTypeName;
	private String actId;
	private String actName;
	private String historyId;
	private String managerTime;		//经理审批时间

	private int isFinished; // 任务是否已经做过 0：任务未做； 1：任务已经做过,但未提交到服务器
							// 2：任务已经做过，且已经提交到服务器//3：强行退出未完成任务
	private int isHavePic; // 填写任务完成情况是是否拍照
	private int isAgree; // 任务完成状态，1：已完成 0：未完成
	private String finishedMemo; // 完成任务时的备注
	private String endDate;
	private String startDate;
	private String pics; // 上传图片后返回的id
	private String picUrl; // 图片保存的本地路径
	private	String memo;	//任务备注

	//任务信息填报
	private String completion;//完成情况
	private String workRecord;//工作记录(

	private String preinjectionl;//注入前液位
	private String postinjectionl;//注入后液位
	private String lnjectionVolume;//注入量

	private String tanknumber;//罐车车号
	private String recipientnumber;//接收方数
	private String storagetanknumber;//甲醇罐入前液位
	private String afternumber;//甲醇罐入后液位

	private String licensenumber;//车号
	private String preinstalled;//装前液位
	private String afterloading;//装后液位
	private String crosssection;//装车量

	private String samplingrecord;//取样记录

	private String dictId;
	private String title;
	private String hint;
	private String type;
	private String dictisnull;

	private String qualified;//合格与否
	private String concentration;//浓度
	private String integral;

	private String riskidentification;//风险识别
	private String preventivemeasures;//防范措施
	private String taskRoute;//行车路线
	private String taskSingleNumber;//路单号

	private String workTitle;//模板名称
	private String workType;//模板类型
	private List<WorkDetail> workDetails=new ArrayList<WorkDetail>();
	private List<DictDetail> dictDetails = new ArrayList<DictDetail>();

	private String documentName;//文档名称
	private String documentUrl;//文档Url

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public List<DictDetail> getDictDetails() {
		return dictDetails;
	}

	public void setDictDetails(List<DictDetail> dictDetails) {
		this.dictDetails = dictDetails;
	}

	public List<WorkDetail> getWorkDetails() {
		return workDetails;
	}

	public void setWorkDetails(List<WorkDetail> workDetails) {
		this.workDetails = workDetails;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	private int count;//计数

	public String getCompletion() {
		return completion;
	}

	public void setCompletion(String completion) {
		this.completion = completion;
	}

	public String getWorkRecord() {
		return workRecord;
	}

	public void setWorkRecord(String workRecord) {
		this.workRecord = workRecord;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDeviceNames() {
		return deviceNames;
	}
	public void setDeviceNames(String deviceNames) {
		this.deviceNames = deviceNames;
	}
	public String getDeviceIds() {
		return deviceIds;
	}
	public void setDeviceIds(String deviceIds) {
		this.deviceIds = deviceIds;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getDeadTime() {
		return deadTime;
	}
	public void setDeadTime(String deadTime) {
		this.deadTime = deadTime;
	}
	public String getRiskTips() {
		return riskTips;
	}
	public void setRiskTips(String riskTips) {
		this.riskTips = riskTips;
	}
	public String getControlTips() {
		return controlTips;
	}
	public void setControlTips(String controlTips) {
		this.controlTips = controlTips;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getWorkNo() {
		return workNo;
	}
	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public String getOperateCard() {
		return operateCard;
	}
	public void setOperateCard(String operateCard) {
		this.operateCard = operateCard;
	}
	public String getOperateCardUrl() {
		return operateCardUrl;
	}
	public void setOperateCardUrl(String operateCardUrl) {
		this.operateCardUrl = operateCardUrl;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getChargerId() {
		return chargerId;
	}
	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getTaskTypeId() {
		return taskTypeId;
	}
	public void setTaskTypeId(String taskTypeId) {
		this.taskTypeId = taskTypeId;
	}
	public String getChargerName() {
		return chargerName;
	}
	public void setChargerName(String chargerName) {
		this.chargerName = chargerName;
	}
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public String getVehicleName() {
		return vehicleName;
	}
	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}
	public String getVehicleDriverName() {
		return vehicleDriverName;
	}
	public void setVehicleDriverName(String vehicleDriverName) {
		this.vehicleDriverName = vehicleDriverName;
	}
	public String getVehicleDriverPhone() {
		return vehicleDriverPhone;
	}
	public void setVehicleDriverPhone(String vehicleDriverPhone) {
		this.vehicleDriverPhone = vehicleDriverPhone;
	}
	public String getTaskTypeName() {
		return taskTypeName;
	}
	public void setTaskTypeName(String taskTypeName) {
		this.taskTypeName = taskTypeName;
	}
	public String getActId() {
		return actId;
	}
	public void setActId(String actId) {
		this.actId = actId;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public String getHistoryId() {
		return historyId;
	}
	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}
	public String getManagerTime() {
		return managerTime;
	}
	public void setManagerTime(String managerTime) {
		this.managerTime = managerTime;
	}
	public int getIsFinished() {
		return isFinished;
	}
	public void  setIsFinished(int isFinished) {
		this.isFinished = isFinished;

	}
	public int getIsHavePic() {
		return isHavePic;
	}
	public void setIsHavePic(int isHavePic) {
		this.isHavePic = isHavePic;
	}
	public int getIsAgree() {
		return isAgree;
	}
	public void setIsAgree(int isAgree) {
		this.isAgree = isAgree;
	}
	public String getFinishedMemo() {
		return finishedMemo;
	}
	public void setFinishedMemo(String finishedMemo) {
		this.finishedMemo = finishedMemo;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getPics() {
		return pics;
	}
	public void setPics(String pics) {
		this.pics = pics;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	@Override
	public int compareTo(Task another) {
		return another.getManagerTime().compareTo(this.managerTime);
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPreinjectionl() {
		return preinjectionl;
	}

	public void setPreinjectionl(String preinjectionl) {
		this.preinjectionl = preinjectionl;
	}

	public String getPostinjectionl() {
		return postinjectionl;
	}

	public void setPostinjectionl(String postinjectionl) {
		this.postinjectionl = postinjectionl;
	}

	public String getLnjectionVolume() {
		return lnjectionVolume;
	}

	public void setLnjectionVolume(String lnjectionVolume) {
		this.lnjectionVolume = lnjectionVolume;
	}

	public String getTanknumber() {
		return tanknumber;
	}

	public void setTanknumber(String tanknumber) {
		this.tanknumber = tanknumber;
	}

	public String getRecipientnumber() {
		return recipientnumber;
	}

	public void setRecipientnumber(String recipientnumber) {
		this.recipientnumber = recipientnumber;
	}

	public String getStoragetanknumber() {
		return storagetanknumber;
	}

	public void setStoragetanknumber(String storagetanknumber) {
		this.storagetanknumber = storagetanknumber;
	}

	public String getLicensenumber() {
		return licensenumber;
	}

	public void setLicensenumber(String licensenumber) {
		this.licensenumber = licensenumber;
	}

	public String getPreinstalled() {
		return preinstalled;
	}

	public void setPreinstalled(String preinstalled) {
		this.preinstalled = preinstalled;
	}

	public String getAfterloading() {
		return afterloading;
	}

	public void setAfterloading(String afterloading) {
		this.afterloading = afterloading;
	}

	public String getCrosssection() {
		return crosssection;
	}

	public void setCrosssection(String crosssection) {
		this.crosssection = crosssection;
	}

	public String getSamplingrecord() {
		return samplingrecord;
	}

	public void setSamplingrecord(String samplingrecord) {
		this.samplingrecord = samplingrecord;
	}

	public String getAfternumber() {
		return afternumber;
	}

	public void setAfternumber(String afternumber) {
		this.afternumber = afternumber;
	}

	public String getDictId() {
		return dictId;
	}

	public void setDictId(String dictId) {
		this.dictId = dictId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDictisnull() {
		return dictisnull;
	}

	public void setDictisnull(String dictisnull) {
		this.dictisnull = dictisnull;
	}

	public String getQualified() {
		return qualified;
	}

	public void setQualified(String qualified) {
		this.qualified = qualified;
	}

	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}

	public String getRiskidentification() {
		return riskidentification;
	}

	public void setRiskidentification(String riskidentification) {
		this.riskidentification = riskidentification;
	}

	public String getPreventivemeasures() {
		return preventivemeasures;
	}

	public void setPreventivemeasures(String preventivemeasures) {
		this.preventivemeasures = preventivemeasures;
	}

	public String getTaskRoute() {
		return taskRoute;
	}

	public void setTaskRoute(String taskRoute) {
		this.taskRoute = taskRoute;
	}

	public String getTaskSingleNumber() {
		return taskSingleNumber;
	}

	public void setTaskSingleNumber(String taskSingleNumber) {
		this.taskSingleNumber = taskSingleNumber;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}


}

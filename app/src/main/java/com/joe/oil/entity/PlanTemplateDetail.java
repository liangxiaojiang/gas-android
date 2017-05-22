package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "plan_template")
public class PlanTemplateDetail {
	private int id;
	private String planTemplateDetailId;
	private String officeId;
	private String officeName;
	private String pointId; // 站点
	private String pointName;// 巡检点名称
	private String pointType; // 站点类型1站，2井
	private String planType; // 计划类型1.日计划，2.周计划，3.月计划
	private String executionTime; // 执行时间,用“，”隔开
	private String sort; // 巡检次数
	private String duration; // 持续时间
	private String patrolTime;// 巡检时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlanTemplateDetailId() {
		return planTemplateDetailId;
	}

	public void setPlanTemplateDetailId(String planTemplateDetailId) {
		this.planTemplateDetailId = planTemplateDetailId;
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

	public String getPointId() {
		return pointId;
	}

	public void setPointId(String pointId) {
		this.pointId = pointId;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getPatrolTime() {
		return patrolTime;
	}

	public void setPatrolTime(String patrolTime) {
		this.patrolTime = patrolTime;
	}
}

package com.joe.oil.entity;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "xj_item")
public class CheckItem implements Comparable<CheckItem>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String itemId;
	private String parentIds; // 所有父级编号
	private String pointId; // 巡检点
	private String pointType; // 巡检点类型:1.站点2.井
	private String code; // 巡检编码
	private String name; // 名称
	private String alias; // 别名
	private String type; // 类型:1.选择2.填空
	private String unit; // 单位
	private String downValue; // 下限
	private String upValue; // 上线
	private String updateTime;
	private String tag;		//启停项必填标记
	private String memo;    //提示

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int compareTo(CheckItem c){
		return this.itemId.compareTo(c.getItemId());
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getParentIds() {
		return parentIds;
	}
	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	public String getPointId() {
		return pointId;
	}
	public void setPointId(String pointId) {
		this.pointId = pointId;
	}
	public String getPointType() {
		return pointType;
	}
	public void setPointType(String pointType) {
		this.pointType = pointType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDownValue() {
		return downValue;
	}
	public void setDownValue(String downValue) {
		this.downValue = downValue;
	}
	public String getUpValue() {
		return upValue;
	}
	public void setUpValue(String upValue) {
		this.upValue = upValue;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
}

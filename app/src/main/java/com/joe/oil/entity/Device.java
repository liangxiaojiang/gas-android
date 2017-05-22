package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "devices")
public class Device {
	private int id;
	private Integer deviceId;
	private String code;
	private String name;
	private String lat;
	private String lng;
	private String memo;
	private String officeName;
	private String tchDate;
	private String pch;
	private String djNum;
	private String ysjNum;
	private String flqNum;
	private String tshqNum;
	private String shzhqNum;
	private String fdjNum;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
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

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getTchDate() {
		return tchDate;
	}

	public void setTchDate(String tchDate) {
		this.tchDate = tchDate;
	}

	public String getPch() {
		return pch;
	}

	public void setPch(String pch) {
		this.pch = pch;
	}

	public String getDjNum() {
		return djNum;
	}

	public void setDjNum(String djNum) {
		this.djNum = djNum;
	}

	public String getYsjNum() {
		return ysjNum;
	}

	public void setYsjNum(String ysjNum) {
		this.ysjNum = ysjNum;
	}

	public String getFlqNum() {
		return flqNum;
	}

	public void setFlqNum(String flqNum) {
		this.flqNum = flqNum;
	}

	public String getTshqNum() {
		return tshqNum;
	}

	public void setTshqNum(String tshqNum) {
		this.tshqNum = tshqNum;
	}

	public String getShzhqNum() {
		return shzhqNum;
	}

	public void setShzhqNum(String shzhqNum) {
		this.shzhqNum = shzhqNum;
	}

	public String getFdjNum() {
		return fdjNum;
	}

	public void setFdjNum(String fdjNum) {
		this.fdjNum = fdjNum;
	}
}

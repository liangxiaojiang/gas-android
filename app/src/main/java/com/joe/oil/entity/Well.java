package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "well")
public class Well {
	private int id;
	private String wellId;
	private String code;  //编号
	private String name; // 井名称
	private String tchDate; // 投产时间
	private String schcw; // 生产层位
	private String wzll; // 无阻流量
	private String tchqYy; // 投产前油压
	private String tchqTy; // 投产前套压
	private String lhqhl; // 硫化氢含量
	private String pch; // 陪产
    private String lat;//纬度
    private String lng;//经度
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getWellId() {
		return wellId;
	}
	public void setWellId(String wellId) {
		this.wellId = wellId;
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
	public String getTchDate() {
		return tchDate;
	}
	public void setTchDate(String tchDate) {
		this.tchDate = tchDate;
	}
	public String getSchcw() {
		return schcw;
	}
	public void setSchcw(String schcw) {
		this.schcw = schcw;
	}
	public String getWzll() {
		return wzll;
	}
	public void setWzll(String wzll) {
		this.wzll = wzll;
	}
	public String getTchqYy() {
		return tchqYy;
	}
	public void setTchqYy(String tchqYy) {
		this.tchqYy = tchqYy;
	}
	public String getTchqTy() {
		return tchqTy;
	}
	public void setTchqTy(String tchqTy) {
		this.tchqTy = tchqTy;
	}
	public String getLhqhl() {
		return lhqhl;
	}
	public void setLhqhl(String lhqhl) {
		this.lhqhl = lhqhl;
	}
	public String getPch() {
		return pch;
	}
	public void setPch(String pch) {
		this.pch = pch;
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
}

package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * @Descripton 管线实体
 * @author baiqiao
 *
 */
@Table(name = "line")
public class Line {

	private int id;
	private String lineId;
	private String name;
	private String officeId;
	private String officeName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
}

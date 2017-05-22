package com.joe.oil.imagepicker;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "photoselected")
public class ImageBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	public String parentName;
	public long size;
	public String displayName;
	public String path;
	public boolean isChecked = false;
	private String uploadTaskId;
	private String patrolTime; // 一天中的某个巡检时间

	public ImageBean() {
		super();
	}

	public ImageBean(String parentName, long size, String displayName, String path, boolean isChecked) {
		super();
		this.parentName = parentName;
		this.size = size;
		this.displayName = displayName;
		this.path = path;
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		return "ImageBean [parentName=" + parentName + ", size=" + size + ", displayName=" + displayName + ", path=" + path + ", isChecked=" + isChecked + "]";
	}

	public String getUploadTaskId() {
		return uploadTaskId;
	}

	public void setUploadTaskId(String uploadTaskId) {
		this.uploadTaskId = uploadTaskId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getPatrolTime() {
		return patrolTime;
	}

	public void setPatrolTime(String patrolTime) {
		this.patrolTime = patrolTime;
	}

}

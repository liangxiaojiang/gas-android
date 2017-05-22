package com.joe.oil.sqlite;

public class UpdateStatus {
	public static boolean UPDATE_RESERVE = true;
	public static boolean UPDATE_HOSPITAL = true;
	public static boolean UPDATE_FIREPOWER = true;
	public static boolean UPDATE_COMMUNICATION = true;
	
	public static boolean NeedUpdateDecisionSupport(String type){
		if(type == "reserve")
			return UpdateStatus.UPDATE_RESERVE;
		else
			return UpdateStatus.UPDATE_COMMUNICATION;
	}
	
	public static void UpdateDecisionSupport(String type,boolean status){
		if(type == "reserve")
			UpdateStatus.UPDATE_RESERVE = status;
		else
			UpdateStatus.UPDATE_COMMUNICATION = status;
	}
	
	public static void setAllUpdateStatus(boolean status){
		UpdateStatus.UPDATE_RESERVE = true;
		UpdateStatus.UPDATE_HOSPITAL = true;
		UpdateStatus.UPDATE_FIREPOWER = true;
		UpdateStatus.UPDATE_COMMUNICATION = true;
	}
}

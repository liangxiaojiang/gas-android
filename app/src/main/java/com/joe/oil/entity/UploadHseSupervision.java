package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * Hse督查实体类
 * Created by scar1et on 15-7-23.
 */
@Table(name = "hse_supervision")
public class UploadHseSupervision implements Serializable {

    private static final long serialVersionUID = 6557514971250812917L;

    private int id;
    private int isSuccess;
    private String checkedPoint;

    private String beCheckedOffice;     //受检单位
    private String beCheckedOrgan;      //受检部位
    private String createdDate;           //检查日期
    private String checkOffice;         //检查单位
    private String checkerIds;          //检查人id
    private String checkerNames;        //检查人姓名多个用“，”隔开
    private String no;                  //编号
    private String issue;               //存在问题及不符合项
    private String suggestion;          //整改要求及意见
    private String reason;              //原因分析
    private String handler;             //整改措施
    private String prevent;             //纠正预防措施

    private int status;             //整改进度 1.待作业 2.已整改 3.未完成
    private User charger;               //落实人
    private String finishDate;            //完成时间

    private boolean reChecked;          //复核情况 true：已复核 false:未复核
    private User reChecker;             //复核人
    private String reCheckDate;           //复核日期

    private String level;               //级别 自查自改、检查考核、厂级等
    private String category;            //问题类别  管理、设备、人员
    private String grade;               //严重程度 一般、严重、重大

    public String getCheckedPoint() {
        return checkedPoint;
    }

    public void setCheckedPoint(String checkedPoint) {
        this.checkedPoint = checkedPoint;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getBeCheckedOffice() {
        return beCheckedOffice;
    }

    public void setBeCheckedOffice(String beCheckedOffice) {
        this.beCheckedOffice = beCheckedOffice;
    }

    public String getBeCheckedOrgan() {
        return beCheckedOrgan;
    }

    public void setBeCheckedOrgan(String beCheckedOrgan) {
        this.beCheckedOrgan = beCheckedOrgan;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCheckOffice() {
        return checkOffice;
    }

    public void setCheckOffice(String checkOffice) {
        this.checkOffice = checkOffice;
    }

    public String getCheckerIds() {
        return checkerIds;
    }

    public void setCheckerIds(String checkerIds) {
        this.checkerIds = checkerIds;
    }

    public String getCheckerNames() {
        return checkerNames;
    }

    public void setCheckerNames(String checkerNames) {
        this.checkerNames = checkerNames;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getPrevent() {
        return prevent;
    }

    public void setPrevent(String prevent) {
        this.prevent = prevent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getCharger() {
        return charger;
    }

    public void setCharger(User charger) {
        this.charger = charger;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public boolean isReChecked() {
        return reChecked;
    }

    public void setReChecked(boolean reChecked) {
        this.reChecked = reChecked;
    }

    public User getReChecker() {
        return reChecker;
    }

    public void setReChecker(User reChecker) {
        this.reChecker = reChecker;
    }

    public String getReCheckDate() {
        return reCheckDate;
    }

    public void setReCheckDate(String reCheckDate) {
        this.reCheckDate = reCheckDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}

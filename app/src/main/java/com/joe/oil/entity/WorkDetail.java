package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by liangxiaojiang on 2017/3/31.
 */
@Table(name = "work_detail")
public class WorkDetail {

    private int id;
    private String title;//代表模板名称
    private String hint;//模板说明
    private int type;//0代表数字文本框、1代表文本框、2代表下拉框、3代表日期、4代表单选框
    private String isNull;//是否必填
    private String content;
    private String workId;
    public WorkDetail() {

    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package com.joe.oil.entity;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by liangxiaojiang on 2016/10/21.
 */
@Table(name = "tank")
public class Tank {
    private int id;



    private int tankid;

//    private String taskarea;//作业区
    private String number;//车号
    private String name;
    private String tankarea;//车辆信息

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTankid() {
        return tankid;
    }

    public void setTankid(int tankid) {
        this.tankid = tankid;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTankarea() {
        return tankarea;
    }

    public void setTankarea(String tankarea) {
        this.tankarea = tankarea;
    }
//    public String getTaskarea() {
//        return taskarea;
//    }
//
//    public void setTaskarea(String taskarea) {
//        this.taskarea = taskarea;
//    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

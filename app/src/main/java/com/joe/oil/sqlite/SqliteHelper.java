package com.joe.oil.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.joe.oil.entity.*;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.util.Constants;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

public class SqliteHelper {

    private FinalDb finalDb;
    private boolean isExist = false;
    private SQLiteDatabase itemDb = null;

    public SqliteHelper(Context context) {
        itemDb = dbHandle.getInstance(context);
        finalDb = FinalDb.create(context, "oil.db");
    }

    /**
     * @Description 判断任务时在数据中存在，不存在则保存到数据库中
     * @date 2014年6月27日 上午10:28:27
     */
    public void insert(List<Task> tasks) {
//		finalDb.deleteAll(Task.class);
        for (int i = 0; i < tasks.size(); i++) {
            //8.21,lxj修改，做个判断，当在数据中存在这个任务，就不执行，当不在数据中，就保存在数据库
            if (!isTaskExist(Integer.parseInt(tasks.get(i).getTaskId()))) {
                finalDb.save(tasks.get(i));
            }
//			 else {
//			 finalDb.update(tasks.get(i), "taskId = '" +
//			 tasks.get(i).getTaskId() + "'");
//				 finalDb.update(tasks.get(i).getIsFinished(),"isFinished = '"+tasks.get(i).getIsFinished()+"'");
//			 }
        }
    }

    /**
     * 判断task表中是否存在当前Id的任务
     *
     * @Description
     * @date 2014年6月27日 上午10:27:56
     */
    public boolean isTaskExist(int taskId) {

        List<Task> tasks = finalDb.findAll(Task.class);
        for (int i = 0; i < tasks.size(); i++) {
            if (Integer.parseInt(tasks.get(i).getTaskId()) == taskId) {
                isExist = true;
                break;
            } else {
                isExist = false;
            }
        }
        if (isExist) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将任务模板保存到数据库中
     *
     * @param dictDetails
     */
    public void insertTemplate(List<DictDetail> dictDetails) {
//        finalDb.deleteAll(DictDetail.class);
        for (int i = 0; i < dictDetails.size(); i++) {
            //是否在任务模板中含有这个任务的id，有就不存，没有才存
            if (!isTaskTemplateExist(dictDetails.get(i).getTaskId(), dictDetails.get(i).getTitle())) {
                finalDb.save(dictDetails.get(i));
            } else {
                finalDb.update(dictDetails.get(i));
            }

        }
    }

    public boolean isTaskTemplateExist(String taskId, String title) {

        List<DictDetail> dictDetails = finalDb.findAll(DictDetail.class);
        for (int i = 0; i < dictDetails.size(); i++) {
            if (dictDetails.get(i).getTaskId().equals(taskId)) {
                if (dictDetails.get(i).getTitle().equals(title)) {
                    isExist = true;
                    break;
                }

            } else {
                isExist = false;
            }
        }
        if (isExist) {
            return true;
        } else {
            return false;
        }
    }

    public void insertTemplate2(List<WorkDetail> workDetails) {
//        finalDb.deleteAll(DictDetail.class);
        for (int i = 0; i < workDetails.size(); i++) {
            //是否在任务模板中含有这个任务的id，有就不存，没有才存
            if (!isTaskTemplateExist1(workDetails.get(i).getWorkId(),workDetails.get(i).getTitle())) {
                finalDb.save(workDetails.get(i));
            } else {
                finalDb.update(workDetails.get(i));
            }

        }
    }


    public boolean isTaskTemplateExist1(String workId,String title) {

        List<WorkDetail> workDetails = finalDb.findAll(WorkDetail.class);
        for (int i = 0; i < workDetails.size(); i++) {
            if (workDetails.get(i).getWorkId().equals(workId) ) {
                if (workDetails.get(i).getTitle().equals(title)) {
                    isExist = true;
                    break;
                }
            } else {
                isExist = false;
            }
        }
        if (isExist) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据任务类型、任务ID查找模板信息
     *
     * @param workTypeId
     * @param taskId
     * @return
     */
    public List<DictDetail> getTaskDetail(String workTypeId, String taskId) {
        List<DictDetail> dictTask = finalDb.findAllByWhere(DictDetail.class, "workTypeId = " + "'" + workTypeId + "' and taskId = " + "'" + taskId + "'");
        return dictTask;
    }

    /**
     * 根据任务ID查找模板信息
     *
     * @param
     * @param taskId
     * @return
     */
    public List<WorkDetail> getWorkDetail(String taskId) {
        List<WorkDetail> dictTask = finalDb.findAllByWhere(WorkDetail.class, "workId = " + "'" + taskId + "'");
        return dictTask;
    }

    /**
     * 更新任务模板信息
     *
     * @param dictDetails
     */

    public void insertTemplate1(List<DictDetail> dictDetails) {
//        finalDb.deleteAll(DictDetail.class);
        for (int i = 0; i < dictDetails.size(); i++) {
            finalDb.findAllByWhere(DictDetail.class, "id = " + "'" + dictDetails.get(i).getId() + "'");

//            //是否在任务模板中含有这个任务的id，有就不存，没有才存
//            if (dictDetails.get(i).getTaskId().equals("taskId")&&dictDetails.get(i).getContent().equals("")){
            finalDb.update(dictDetails.get(i));
//            }

        }
    }


    /**
     * 路单
     *
     * @param single
     */
    public void insertSingle(List<Single> single) {
        for (int i = 0; i < single.size(); i++) {
//            if (!isSingleExist(single.get(i).getSingleId()) && (single.get(i).getActName().equals("已审批"))) {
            if (!isSingleExist(single.get(i).getSingleId())) {
                finalDb.save(single.get(i));
            }

        }
    }

    public boolean isSingleExist(String singleId) {

        List<Single> singles = finalDb.findAll(Single.class);
        for (int i = 0; i < singles.size(); i++) {
            if (singles.get(i).getSingleId().equals(singleId)) {
                isExist = true;
                break;
            } else {
                isExist = false;
            }
        }
        if (isExist) {
            return true;
        } else {
            return false;
        }
    }

    public List<Single> getSingleById(String singleId) {
        List<Single> singles = finalDb.findAllByWhere(Single.class, "singleId = " + "'" + singleId + "'");
        return singles;
    }

    public List<Single> getSingleNotFinish(String singleId, String name) {
        List<Single> singles = finalDb.findAllByWhere(Single.class, "singleId = '" + singleId + "'" + " and actName = '已审批'");
//		List<Task> tasks1=finalDb.findAllByWhere(Task.class,"isFinished = 3 and chargerId = '"+ chargerId + "'");
//		tasks.addAll(tasks1);
        return singles;
    }


    /**
     * @Description 获取所有已经完成的任务
     * @date 2016.11.3
     */
    public List<Task> getFinishedTaskIs(String chargerId) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished = 1 And chargerId = '" + chargerId + "'");
        List<Task> task2 = finalDb.findAllByWhere(Task.class, "isFinished = 2 And chargerId = '" + chargerId + "'");
        List<Task> taskWeb = finalDb.findAllByWhere(Task.class, "actName = '完成'  And chargerId = '" + chargerId + "'");
        tasks.addAll(task2);
        taskWeb.addAll(tasks);
        return taskWeb;
    }

    /**
     * @Description 获取所有已经完成的任务
     * @date 2016.11.3
     */
    public List<Task> getFinishedTaskByCode(String chargerId, String deviceIds) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished = 1 And chargerId = '" + chargerId + "' and deviceIds = '" + deviceIds + "'");
        List<Task> task2 = finalDb.findAllByWhere(Task.class, "isFinished = 2 And chargerId = '" + chargerId + "' and deviceIds = '" + deviceIds + "'");
        List<Task> taskWeb = finalDb.findAllByWhere(Task.class, "actName = '完成'  And chargerId = '" + chargerId + "' and deviceIds = '" + deviceIds + "'");
        tasks.addAll(task2);
        taskWeb.addAll(tasks);
        return taskWeb;
    }

    /**
     * @Description 获取数据库中没有完成的任务
     * @date 2014年6月27日 上午10:26:50
     */
    public List<Task> getTaskNotFinish(String chargerId, String deadTime) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished = 0 and chargerId = '" + chargerId + "'" + " and actName = '待作业'");
//		List<Task> tasks1=finalDb.findAllByWhere(Task.class,"isFinished = 3 and chargerId = '"+ chargerId + "'");
//		tasks.addAll(tasks1);
        return tasks;
    }


    public List<Single> getSingleNotFinish(String chargerId) {
        List<Single> single = finalDb.findAllByWhere(Single.class, "actName != '' and chargerId = '" + chargerId + "'");
//
        return single;
    }

    /**
     * 根据用户名判断数据库中已经完成的任务
     * @param chargerId
     * @return
     */
    public List<Task> getTaskNotFinish2(String chargerId) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished = 0 and chargerId = '" + chargerId + "'" + " and actName = '完成'");
//		List<Task> tasks1=finalDb.findAllByWhere(Task.class,"isFinished = 3 and chargerId = '"+ chargerId + "'");
//		tasks.addAll(tasks1);
        return tasks;
    }

    /**
     * @Description 获取数据库中没有完成的任务
     * @date 2014年6月27日 上午10:26:50
     */
    public List<Task> getTaskScanner(String chargerId, String deviceIds) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished = 0 and chargerId = '" + chargerId + "'" + " and actName = '待作业'" + " and deviceIds = '" + deviceIds + "'");
        return tasks;
    }

    /**
     * @Description 获取数据库中没有完成的任务中的标记Item对象集合
     * @serialData 2016.8.30(测试是否可以能让点击listview的Item让显示在第一行）
     */
    public List<Task> getItemTaskNotFinish(String chargerId) {
        List<Task> tasks1 = finalDb.findAllByWhere(Task.class, "isFinished = 3 and chargerId = '" + chargerId + "'");
        return tasks1;
    }

    public List<Task> getnoItemTask(String chargerId, String isfinished) {
        List<Task> tasks1 = finalDb.findAllByWhere(Task.class, "isFinished = 3 and chargerId = '" + chargerId + "'");
        return tasks1;
    }

    /**
     * 判断是否有正在进行中的工作
     *
     * @return
     */
    public List<Task> getNowTask() {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished=3");
        return tasks;
    }

    /**
     * @Description 获取数据库中已完成但没有提交的任务
     * @date 2014年6月27日 上午10:23:19
     */
    public List<Task> getTaskFinishNotUpload(String chargerId) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished = 1 and chargerId = '" + chargerId + "'");
        return tasks;
    }

    /**
     * @Description 获取所有已经完成的任务，包括完成但没有提交任务， 用于历史任务显示
     * @date 2014年6月27日 上午10:20:26
     */
    public List<Task> getTaskIsFinished(String chargerId) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "isFinished = 1 And chargerId = '" + chargerId + "'");
        List<Task> task2 = finalDb.findAllByWhere(Task.class, "isFinished = 2 And chargerId = '" + chargerId + "'");
        tasks.addAll(task2);
        return tasks;
    }

    /**
     * @Description 根据任务Id获取某一条任务
     * @date 2014年6月27日 上午10:19:27
     */
    public List<Task> getTaskById(String taskId) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "taskId = " + "'" + taskId + "'");
        return tasks;
    }

    /**
     * @Description 根据任务Id更新任务的isFinished的字段，
     * @date 2014年6月27日 上午10:16:54
     */
    public boolean updateTaskFinishState(Task task) {
        finalDb.deleteByWhere(Task.class, "taskId = " + "'" + task.getTaskId() + "'");
        finalDb.save(task);
        return true;
    }

    public void updateTasksFinishState(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            finalDb.deleteByWhere(Task.class, "taskId = '" + tasks.get(i).getTaskId() + "'");
            finalDb.save(tasks.get(i));
        }
    }

    /**
     * 根据路单id更新任务
     *
     * @param single
     * @return
     */
    public boolean updateSingleFinishState(Single single) {
        finalDb.deleteByWhere(Single.class, "singleId = " + "'" + single.getSingleId() + "'");
        finalDb.save(single);
        return true;
    }

    /**
     * @Description 获取数据库中所有完成任务且有拍照的任务列表
     * @date 2014年6月27日 上午10:15:47
     */
    public List<Task> getTaskHavePic() {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "picUrl is not null");
        return tasks;
    }

    /**
     * @Description 清除数据库中的所有基础数据
     * @date 2014年7月14日 上午11:44:26
     */
    public void deleteBaseData() {
        finalDb.deleteAll(User.class);
        // finalDb.deleteAll(CheckItem.class);
        finalDb.deleteAll(Device.class);
        finalDb.deleteAll(Well.class);
        finalDb.deleteAll(Line.class);
        // finalDb.deleteAll(PlanTemplateDetail.class);
        finalDb.deleteAll(Dict.class);
        finalDb.deleteAll(DeviceTree.class);
        finalDb.deleteAll(PlanDetail.class);
//        finalDb.deleteAll(Tank.class);
        finalDb.deleteAll(Single.class);
        finalDb.deleteAll(DictDetail.class);

    }

    public void deleteAllSingle() {
        finalDb.deleteAll(Single.class);
    }

    public void deletePlanTemplate() {
        finalDb.deleteAll(PlanTemplateDetail.class);
    }

    public void deleteAllPlanDetail() {
        finalDb.deleteAll(PlanDetail.class);
    }

    public void deleteAllDevice() {
        finalDb.deleteAll(Device.class);
    }

    /**
     * @param users
     * @Description 将用户基础数据保存到数据库中
     * @date 2014年7月14日 上午11:47:02
     */
    public int insertUser(List<User> users) {
        int count = 0;
        for (int i = 0; i < users.size(); i++) {
            finalDb.save(users.get(i));
            count++;
        }
        return count;
    }

    /**
     * @param user
     * @Description 将用户基础数据保存到数据库中
     * @date 2014年7月14日 上午11:47:02
     */
    public void insertUser(User user) {
        finalDb.save(user);
    }

    /**
     * @Description 将巡检项基础数据保存到数据库库中
     * @author baiqiao
     * @date 2014年7月28日 下午4:37:27
     */
    public int insertCheckItem(List<CheckItem> checkItems) {
        int count = 0;
        for (int i = 0; i < checkItems.size(); i++) {
            finalDb.save(checkItems.get(i));
            count++;
        }
        return count;
    }

    /**
     * @Description 将巡检点基础数据保存到数据库中
     * @author baiqiao
     * @date 2014年7月29日 下午5:38:59
     * @deprecated
     */
    public int insertDevice(List<Device> devices) {
        int count = 0;
        for (int i = 0; i < devices.size(); i++) {
            finalDb.save(devices.get(i));
            count++;
        }
        return count;
    }

    /**
     * @Description 更新巡检点基础数据
     * @author yl
     * @date 2014年12月12日
     */
    public void updateDevice(List<Device> devices) {
        for (int i = 0; i < devices.size(); i++) {
            Device data = devices.get(i);
            finalDb.update(data, "deviceId = '" + data.getDeviceId() + "'");
        }
    }

    public int insertWell(List<Well> wells) {
        int count = 0;
        for (int i = 0; i < wells.size(); i++) {
            finalDb.save(wells.get(i));
            count++;
        }
        return count;
    }

    // 向数据库保存管线基础数据
    public int insertLine(List<Line> lines) {
        int count = 0;
        for (int i = 0; i < lines.size(); i++) {
            finalDb.save(lines.get(i));
            count++;
        }
        return count;
    }

    /**
     * @param loginName
     * @Description 通过用户loginName获取数据库中该用户的信息
     * @date 2014年7月14日 下午2:37:38
     */
    public User getUserByLoginName(String loginName) {
        List<User> users = finalDb.findAllByWhere(User.class, "loginName = '" + loginName + "'");
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public void updateUser(User user) {
        finalDb.update(user, "loginName = '" + user.getLoginName() + "'");
    }

    public User getLoginUser() {
        List<User> users = finalDb.findAllByWhere(User.class, "loginStatus = '1'");
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public void initUserLoginStatus() {
        List<User> users = finalDb.findAllByWhere(User.class, "loginStatus = '1'");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            user.setLoginStatus("0");
            updateUser(user);
        }
    }

    /**
     * @param gis 通过定位获取的用户的位置信息
     * @Description 保存经纬度
     */
    public void insertGis(Gis gis) {
        finalDb.save(gis);
    }

    /**
     * @Description 保存巡检计划详情到数据库
     * @author baiqiao
     * @date 2014年7月29日 上午11:18:05
     */
    public int insertPlanDetail(List<PlanDetail> paDetails) {
        int count = 0;
        for (int i = 0; i < paDetails.size(); i++) {
            finalDb.save(paDetails.get(i));
            count++;
        }
        return count;
    }

    public int insertPlanTemplate(List<PlanTemplateDetail> planTemplateDetails) {
        int count = 0;
        for (int i = 0; i < planTemplateDetails.size(); i++) {
            finalDb.save(planTemplateDetails.get(i));
            count++;
        }
        return count;
    }

    /**
     * @param dicts
     * @Description 保存字典集到数据库
     * @author baiqiao
     * @date 2014年9月3日 上午11:08:44
     */
    public int insertDict(List<Dict> dicts) {
        int count = 0;
        for (int i = 0; i < dicts.size(); i++) {
            finalDb.save(dicts.get(i));
            count++;
        }
        return count;
    }

    /**
     * @param deviceTrees
     * @return
     * @Description 保存DeviceTree到数据库
     * @author baiqiao
     * @date 2014年9月4日 上午8:30:14
     */
    public int insertDeviceTree(List<DeviceTree> deviceTrees) {
        int count = 0;
        for (int i = 0; i < deviceTrees.size(); i++) {
            finalDb.save(deviceTrees.get(i));
            count++;
        }
        return count;
    }

    public void insertOffice(List<Office> offices) {
        for (Office office : offices) {
            List<Office> result = finalDb.findAllByWhere(Office.class, "code = '" + office.getCode() + "'");
            if (result.size() > 0) {
                finalDb.update(office, "code = '" + office.getCode() + "'");
            } else {
                finalDb.save(office);
            }
        }
    }

    /**
     * @Description 判断数据是否存在当前计划详情
     * @author baiqiao
     * @date 2014年7月29日 下午3:08:14
     */
    public boolean isPlanDetailExist(String planDetailId, String patrolTime) {

        List<PlanDetail> planDetails = finalDb.findAll(PlanDetail.class);
        for (int i = 0; i < planDetails.size(); i++) {
            if (planDetails.get(i).getPlanDetailId().equals(planDetailId)
                    && planDetails.get(i).getPatrolTime().equals(patrolTime)) {
                isExist = true;
                break;
            } else {
                isExist = false;
            }
        }
        if (isExist) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @Description 更新巡检计划
     * @author baiqiao
     * @date 2014年8月3日 上午10:15:23
     * @deprecated
     */
    public void updatePlanDetails(List<PlanDetail> planDetails) {

        for (int i = 0; i < planDetails.size(); i++) {
            if (isPlanDetailExist(planDetails.get(i).getPlanDetailId(), planDetails.get(i).getPatrolTime())) {
                finalDb.deleteById(PlanDetail.class, "planDetailId = '" + planDetails.get(i).getPlanDetailId() + "'");
                finalDb.save(planDetails.get(i));
            }
        }
    }

    /**
     * @Description 一条gis数据上传成功，根据该条数据的id删除该条数据
     * @author baiqiao
     * @date 2014年7月26日 下午2:43:35
     */
    public void deleteGisByTime(Gis gis) {
        // finalDb.deleteByWhere(Gis.class, "time = " + "'" + gis.getTime() +
        // "' and pics is not null");
        finalDb.deleteByWhere(Gis.class, "time = " + "'" + gis.getTime() + "'");
    }

    public void deleteGis() {
        finalDb.deleteAll(Gis.class);
    }

    /**
     * @param planType 计划类型 1：日计划；2：周计划 3:月计划
     * @param type     巡检类型 1：场站巡护 2：单井巡护
     * @Description 根据条件删除计划
     * @author joe
     * @date 2014年10月31日 上午10:18:18
     */
    public void deletePlanDetail(String planType, String type) {
        finalDb.deleteByWhere(PlanDetail.class, "planType = '" + planType + "' and type = '" + type + "'");
    }

    /**
     * @param planType 计划类型 1：日计划；2：周计划 3:月计划
     * @param type     巡检类型 1：场站巡护 2：单井巡护
     * @param officeId
     * @Description 根据条件删除计划
     * @author yl
     * @date 2014年12月16日
     */
    public void deletePlanDetail(String planType, String type, String officeId) {
        finalDb.deleteByWhere(PlanDetail.class,
                "planType = '" + planType + "' and type = '" + type + "' and officeId = '" + officeId + "'");
    }

    public void deleteUser() {
        finalDb.deleteAll(User.class);
    }

    public void deleteWell() {
        finalDb.deleteAll(Well.class);
    }

    public void deleteCheckItem() {
        finalDb.deleteAll(CheckItem.class);
    }

    public void deleteTank() {
        finalDb.deleteAll(Tank.class);
    }

    /**
     * 删除任务模板表
     */
    public void deleteDictDetail() {
        finalDb.deleteAll(DictDetail.class);
    }

    /**
     * @Description gis数据未上传成功，更新数据库中的status状态
     * @author baiqiao
     * @date 2014年7月27日 下午1:57:54
     */
    public void updateGisByTime(Gis gis) {
        finalDb.update(gis, "time = '" + gis.getTime() + "'");
    }

    public void updateGisPicIds(Gis gis) {
        Gis mGis = new Gis();
        mGis.setPics(gis.getPics());
        finalDb.update(mGis, "time = '" + gis.getTime() + "'");
    }

    /**
     * @Description 更新数据库中的Gis数据，主要是status和canDelete字段
     * @author baiqiao
     * @date 2014年7月27日 下午2:01:24
     */
    public void updateGis(Gis gis) {
        Gis mGis = new Gis();
        mGis.setStatus(gis.getStatus());
        mGis.setGisId(gis.getGisId());
        mGis.setIsPicIdUpload(gis.getIsPicIdUpload());
        finalDb.update(mGis, "time = '" + gis.getTime() + "'");
    }

    /**
     * @Description 获取数据库中没有上传成功的gis数据
     * @author baiqiao
     * @date 2014年7月27日 下午2:01:24
     * @deprecated
     */
    public List<Gis> getGisNotSubmit(String userId) {
        return finalDb.findAllByWhere(Gis.class, "status != '2' and userId = " + "'" + userId + "'");
    }

    /**
     * @Description 获取数据库中没有上传成功的gis数据
     * @author baiqiao
     * @date 2014年7月27日 下午2:01:24
     */
    public List<Gis> getGisNotSubmit() {
        return finalDb.findAllByWhere(Gis.class, "status != '2' limit 300");
    }

    /**
     * @param orderType 排序方式 asc：正序 desc 倒序
     * @return
     */
    public Gis getGisOfStartOrEnd(String orderType, String taskNo) {
        List<Gis> gisStart = finalDb.findAllByWhere(Gis.class,
                "num = '" + taskNo + "' ORDER BY time " + orderType + " LIMIT 1");
        if (gisStart.size() > 0) {
            return gisStart.get(0);
        } else {
            return null;
        }
    }

    public GisFinish getGisFinishByTaskNo(String taskNo) {
        List<GisFinish> gisFinishs = finalDb.findAllByWhere(GisFinish.class, "taskNo = '" + taskNo + "'");
        if (gisFinishs.size() > 0) {
            return gisFinishs.get(0);
        } else {
            return null;
        }
    }

    public List<GisFinish> getAllGisFinishs(String userId) {
        return finalDb.findAllByWhere(GisFinish.class, "userId = '" + userId + "'", "creatTime desc");
    }

    public List<Gis> getAllGisUpload(String userId) {
        return finalDb.findAllByWhere(Gis.class, "userId = '" + userId + "' and exceptionStatus = '2'", "time desc");
    }

    /**
     * @Description 获取数据库中没有同步picId的数据库记录
     * @author yl
     * @date 2014年12月1日
     * @deprecated
     */
    public List<Gis> getGisPicIdNotSubmit(String userId) {
        return finalDb.findAllByWhere(Gis.class,
                "pics != '' and pics != 'null' and exceptionStatus = '2' and userId = " + "'" + userId + "'");
    }

    /**
     * @Description 获取数据库中没有同步picId的数据库记录
     * @author yl
     * @date 2014年12月1日
     */
    public List<Gis> getGisPicIdNotSubmit() {
        return finalDb.findAllByWhere(Gis.class,
                "pics != '' and pics != 'null' and exceptionStatus = '2' and isPicIdUpload = '0' limit 300");
    }

    public List<Gis> getGisByNum(String num) {
        return finalDb.findAllByWhere(Gis.class, "num = '" + num + "' and exceptionStatus != '2'");
    }

    /**
     * @Description 获取该用户当天的巡站计划
     * @author baiqiao
     * @date 2014年7月29日 下午5:17:44
     */
    public List<PlanDetail> getPlanDetailsofStation(String officeId) {
        return finalDb.findAllByWhere(PlanDetail.class, " type = '1' and officeId = '" + officeId + "'", "itemId");
    }

    /**
     * @Description 获取该用户当天的巡井计划
     */
    public List<PlanDetail> getPlanDetailsofWell() {
        return finalDb.findAllByWhere(PlanDetail.class, "type = '2' GROUP BY pointId");
    }

    /**
     * @Description 获取某用户当天某一巡检点的巡检项
     * @author baiqiao
     * @date 2014年7月30日 上午9:42:24
     */
    public List<PlanDetail> getPlanDetailsByDeviceId(String pointId, String patrolTime) {
        // return finalDb.findAllByWhere(PlanDetail.class, "type = '1' and
        // pointId = '" + pointId + "' and patrolTime = '" + patrolTime + "'",
        // "code");

        List<PlanDetail> data = finalDb.findAllByWhere(PlanDetail.class,
                "type = '1' and pointId = '" + pointId + "' and patrolTime = '" + patrolTime + "'", "code");
        if (data.size() > 0) {
            return data;
        } else {
            String newStr = patrolTime.substring(0, 11) + "00:00:00";
            return finalDb.findAllByWhere(PlanDetail.class,
                    "type = '1' and pointId = '" + pointId + "' and patrolTime = '" + newStr + "'", "code");
        }

    }

    public List<PlanDetail> getWellPlanDetailsByDeviceId(String pointId) {
        return finalDb.findAllByWhere(PlanDetail.class,
                "type = '2' and pointId = '" + pointId + "' and code like '" + Constants.CURRENT_WELL_CODE + "%'");
    }

    /**
     * @Description 获取巡站异常巡检项
     * @author baiqiao
     * @date 2014年8月2日 下午2:05:00
     */
    public List<PlanDetail> getExceptionPlanDetailsofStation(String patrolTime, String officeId) {
        return finalDb.findAllByWhere(PlanDetail.class,
                "type = '1' and patrolTime = '" + patrolTime
                        + "' and exceptionStatus = '2' and status != '1' and handleMemoUpload = '0' and officeId = '"
                        + officeId + "' order by planDetailId asc");
    }

    public List<PlanDetail> getExceptionPlanDetailsofWell(String patrolTime, String officeId) {
        return finalDb.findAllByWhere(PlanDetail.class,
                "type = '2' and exceptionStatus = '2' and status != '1' and handleMemoUpload = '0' and officeId = '"
                        + officeId + "' order by planDetailId asc");
    }

    public List<PlanDetail> getAllPlan(String patrolDate) {
        return finalDb.findAllByWhere(PlanDetail.class, "patrolDate = '" + patrolDate + "' and type = '1'");
    }

    public List<PlanDetail> getOnePlanEachStation() {
        return finalDb.findAllByWhere(PlanDetail.class, "type = '1' group by officeName order by code asc");
    }

    public List<PlanDetail> getOnePlanEachSingleWell() {
        return finalDb.findAllByWhere(PlanDetail.class, "type = '2' group by officeName order by code asc");
    }

    /**
     * @Description 获取某用户当天某一巡检点已完成巡检的巡检项
     * @author baiqiao
     * @date 2014年8月1日 上午11:06:08
     */
    public List<PlanDetail> getPlanDetailsByDeviceIdAndFinished(String patrolDate, String pointId) {
        return finalDb.findAllByWhere(PlanDetail.class,
                "pointId = '" + pointId + "' and type = '1' and result is not null");
    }

    public List<PlanDetail> getPlanDetailsByWellIdAndFinished(String patrolDate, String pointId) {
        return finalDb.findAllByWhere(PlanDetail.class,
                "pointId = '" + pointId + "' and type = '2' and (status = '2' or status = '3')");
    }

    /**
     * @Description 获取某一巡检点的所有巡检项
     * @author baiqiao
     * @date 2014年7月30日 上午10:07:06
     * @deprecated
     */
    public List<CheckItem> getCheckItemList(List<PlanDetail> planDetails) {
        List<CheckItem> checkItems = new ArrayList<CheckItem>();
        for (int i = 0; i < planDetails.size(); i++) {
            List<CheckItem> temp = finalDb.findAllByWhere(CheckItem.class,
                    "itemId = '" + planDetails.get(i).getItemId() + "'");
            if (temp.size() > 0) {
                checkItems.add(temp.get(0));
            }
        }
        return checkItems;
    }

    /**
     * @param deviceIds
     * @return
     * @deprecated
     */
    public List<Device> getDevices(List<String> deviceIds) {
        List<Device> devices = new ArrayList<Device>();
        for (int i = 0; i < deviceIds.size(); i++) {
            Device device = new Device();
            device = finalDb.findAllByWhere(Device.class, "deviceId = '" + deviceIds.get(i) + "'").get(0);
            devices.add(device);
        }
        return devices;
    }

    public Device getDeviceById(String deviceId) {
        List<Device> devices = finalDb.findAllByWhere(Device.class, "deviceId = '" + deviceId + "'");
        if (devices.size() > 0) {
            return devices.get(0);
        } else {
            return null;
        }
    }

    public Well getWellById(String wellId) {
        List<Well> wells = finalDb.findAllByWhere(Well.class, "wellId = '" + wellId + "'");
        if (wells.size() > 0) {
            return wells.get(0);
        } else {
            return null;
        }
    }

    public List<Well> getWellList() {
        return finalDb.findAll(Well.class);
    }

    /**
     * @Description 保存输入类型的巡检项的巡检结果
     * @author baiqiao
     * @date 2014年7月30日 下午3:17:23
     */
    public void insertCheckResult(PlanDetail planDetail) {
        // finalDb.deleteById(PlanDetail.class, planDetail.getId());
        // finalDb.save(planDetail);
        finalDb.update(planDetail, "id = '" + planDetail.getId() + "'");
    }

    /**
     * @Description 获取选择类型的巡检项的选择项值
     * @author baiqiao
     * @date 2014年7月30日 下午3:37:54
     */
    public List<CheckItem> getCheckItemChooseValue(String code) {
        return finalDb.findAllByWhere(CheckItem.class, "code like '" + code + "__'");
    }

    /**
     * @Description 根据itemId获取巡检项
     * @author yl
     * @date 2014年12月09日
     */
    public CheckItem getCheckItemsByItemId(String itemId) {
        List<CheckItem> result = finalDb.findAllByWhere(CheckItem.class, "itemId = '" + itemId + "' limit 1");
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * @return
     * @Description 获取数据库中完成巡检但没有上传的数据
     * @author baiqiao0
     * @date 2014年7月31日 上午9:26:39
     */
    public List<PlanDetail> getNotUploadPlanDetails() {
        return finalDb.findAllByWhere(PlanDetail.class, "status = '3' limit 150");
    }

    public List<PlanDetail> getNotUpdataPicPlanDetail() {
        return finalDb.findAllByWhere(PlanDetail.class, "picId != '' and isPicIdUpdate = '0' limit 150");
    }

    // 获取数据库中存在异常但未处理的计划
    public List<PlanDetail> getNotResovledExpectionPlanDetails() {
        return finalDb.findAllByWhere(PlanDetail.class, "exceptionStatus = '2' and handleMemo is null");
    }

    /**
     * @Description 更新巡检数据上传成功后的状态
     * @author baiqiao
     * @date 2014年7月31日 上午10:02:36
     */
    public void updateUploadPlanStatus(List<PlanDetail> planDetails) {
        for (int i = 0; i < planDetails.size(); i++) {
            PlanDetail data = planDetails.get(i);
            finalDb.update(planDetails.get(i),
                    "itemId = '" + data.getItemId() + "' and patrolTime = '" + data.getPatrolTime() + "'");
        }
    }

    /**
     * @param planDetail
     * @deprecated
     */
    public void updateUploadPlanStatus(PlanDetail planDetail) {
        PlanDetail data = new PlanDetail();
        data.setStatus(planDetail.getStatus());
        finalDb.update(data, "id = '" + planDetail.getId() + "'");
    }

    public void updatePlanDetailIsPicIdUpdate(PlanDetail planDetail) {
        PlanDetail data = new PlanDetail();
        data.setIsPicIdUpdate(planDetail.getIsPicIdUpdate());
        finalDb.update(data, "id = '" + planDetail.getId() + "'");
    }

    public void updateDetailPlanFromNetException(UploadException data) {
        PlanDetail plan_local = new PlanDetail();
        plan_local.setResult(data.getResult());
        plan_local.setMemo(data.getDescription());
        plan_local.setExceptionStatus("2");
        plan_local.setStatus("2");
        plan_local.setHandleAdvice(data.getTreatmentAdvice());
        plan_local.setPicId(data.getPicId());
        plan_local.setWorkId(data.getWorkId());
        plan_local.setCreateTime(data.getTime());
        finalDb.update(plan_local,
                "itemId = '" + data.getItemId() + "' and patrolTime = '" + data.getPatrolTime() + "'");
    }

    /**
     * @param planDetail
     * @deprecated
     */
    public void updateDetailPlanofPhotoId2(PlanDetail planDetail) {
        // finalDb.deleteByWhere(PlanDetail.class, "itemId = '" +
        // planDetail.getItemId() + "' and patrolTime = '" +
        // planDetail.getPatrolTime() + "'");
        // finalDb.save(planDetail);

        finalDb.update(planDetail,
                "itemId = '" + planDetail.getItemId() + "' and patrolTime = '" + planDetail.getPatrolTime() + "'");
    }


    public void updateDetailPlanByItemId(PlanDetail planDetail) {
        // finalDb.deleteByWhere(PlanDetail.class, "itemId = '" +
        // planDetail.getItemId() + "' and patrolTime = '" +
        // planDetail.getPatrolTime() + "'");
        // finalDb.save(planDetail);

        finalDb.update(planDetail,
                "itemId = '" + planDetail.getItemId() + "' and patrolTime = '" + planDetail.getPatrolTime() + "'");
    }

    /**
     * 根据itemId获取Plandetail数据
     *
     * @return
     * @deprecated
     */
    public PlanDetail getDetailPlanByItemId(String itemId) {
        List<PlanDetail> result = finalDb.findAllByWhere(PlanDetail.class, "itemId = '" + itemId + "'",
                "createTime desc");
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public PlanDetail getDetailPlanByItemId2(String itemId) {
        List<PlanDetail> result = finalDb.findAllByWhere(PlanDetail.class, "tag like '" + itemId + "'",
                "createTime desc");
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    // 获取数据库中管线基础数据
    public List<Line> getLineData() {
        return finalDb.findAll(Line.class);
    }

    // 清空数据库中全部的巡检计划
    public void cleanAllPlan() {
        finalDb.deleteAll(PlanDetail.class);
    }

    // 获取字典集中集气站级别的数据
    public List<Dict> getDictofStation() {
        return finalDb.findAllByWhere(Dict.class, "grade = '2'");
    }

    public List<Dict> getDictofWork() {
        return finalDb.findAllByWhere(Dict.class, "grade = '3'");
    }

    public List<Dict> getDictofWorkDetail() {
        return finalDb.findAllByWhere(Dict.class, "grade = '4'");
    }

    // 获取deviceTree中作业区级别的数据
    public List<DeviceTree> getDeviceTreeofOfficeLike(String officeCode, String cardType) {
        return finalDb.findAllByWhere(DeviceTree.class,
                "type = '2' and code like '" + officeCode + "__' and  cardType = '" + cardType + "'");
    }

    // 获取deviceTree中作业区级别的数据
    public List<DeviceTree> getDeviceTreeofOfficeEquals(String officeCode, String cardType) {
        return finalDb.findAllByWhere(DeviceTree.class,
                "type = '2' and code = '" + officeCode + "' and  cardType = '" + cardType + "'");
    }

    public List<DeviceTree> getDeviceTreeofStation(String stationCode, String cardType) {
        if (stationCode == null) {
            return finalDb.findAllByWhere(DeviceTree.class, "type = '3' and  cardType = '" + cardType + "'");
        } else {
            return finalDb.findAllByWhere(DeviceTree.class,
                    "type = '3' and code like '" + stationCode + "__'and  cardType = '" + cardType + "'");
        }

    }

    public List<DeviceTree> getDeviceTreeofDevice(String cardType) {
        return finalDb.findAllByWhere(DeviceTree.class, "type = '4' and  cardType = '" + cardType + "'");
    }

    public List<PlanTemplateDetail> getPlanTemplateDetails(String planType, String officeId) {
        if (officeId.equals("")) {
            return finalDb.findAllByWhere(PlanTemplateDetail.class, "planType = '" + planType + "'");
        } else {
            return finalDb.findAllByWhere(PlanTemplateDetail.class,
                    "planType = '" + planType + "' and officeId = '" + officeId + "'");
        }
    }

    public List<CheckItem> getCheckItemsByPoint(String pointId, String pointType) {
        return finalDb.findAllByWhere(CheckItem.class,
                "pointId = '" + pointId + "' and pointType = '" + pointType + "'");
    }

    public List<PlanDetail> getPlanDetailsByWork() {
        return finalDb.findAllByWhere(PlanDetail.class, "workIsSuccess = 0");
    }

    /**
     * @param patrolDate
     * @return
     * @Description 判断日计划是否存在
     * @author joe
     * @date 2014年10月31日 上午10:29:30
     */
    public boolean isDayPlanExist(String patrolDate) {
        List<PlanDetail> result = finalDb.findAllByWhere(PlanDetail.class,
                "patrolDate = '" + patrolDate + "' and planType = '1' limit 1");
        if (result != null && result.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return
     * @Description 判断月计划是否存在
     * @author joe
     * @date 2014年10月31日 上午11:19:16
     */
    public boolean isMonthOrWeekPlanExist(String planType) {
        List<PlanDetail> result = finalDb.findAllByWhere(PlanDetail.class, "planType = '" + planType + "' limit 1");
        if (result != null && result.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param planType 计划类型 1：日计划；2：周计划 3:月计划
     * @param type     巡检类型 1：场站巡护 2：单井巡护
     * @return
     * @Description 获取周计划或月计划波动时间的上限和下限
     * @author joe
     * @date 2014年10月31日 上午11:07:20
     */
    public String getUpAndDownTime(String planType, String type) {
        List<PlanDetail> planDetails = finalDb.findAllByWhere(PlanDetail.class,
                "planType = '" + planType + "' and type = '" + type + "'");
        if (planDetails.size() > 0) {
            return planDetails.get(0).getUpTime() + "," + planDetails.get(0).getDownTime();
        } else {
            return null;
        }
    }

    public void deleteAllTask() {
        finalDb.deleteAll(Task.class);
        finalDb.deleteAll(DictDetail.class);
        finalDb.deleteAll(WorkDetail.class);
        finalDb.deleteAll(FeedBack.class);
    }

    /**
     * @return
     * @Description 插入由添加图片操作选择的图片与事件的绑定信息
     * @author joe
     * @date 2014年11月26日 上午11:22
     */
    public void insertLocalPic(ImageBean bean) {
        finalDb.save(bean);
    }

    /**
     * @return
     * @Description 根据唯一标识获取数据库中由添加图片操作选择的图片
     * @author yl
     * @date 2014年11月26日 上午11:22
     */
    public List<ImageBean> getLocalPic(String uploadTaskId, String createTime) {
        return finalDb.findAllByWhere(ImageBean.class, "uploadTaskId = '" + uploadTaskId + "'and createTime = '" + createTime + "'");
    }


    /**
     * @return
     * @Description 根据唯一标识获取数据库中由添加图片操作选择的图片
     * @author yl
     * @date 2014年11月26日 上午11:22
     */
    public List<ImageBean> getLocalPics(String uploadTaskId) {
        return finalDb.findAllByWhere(ImageBean.class, "uploadTaskId = '" + uploadTaskId + "'");
    }


    /**
     * @return
     * @Description 根据唯一标识获取数据库中由添加图片操作选择的图片
     * @author yl
     * @date 2014年12月10日
     */
    public List<ImageBean> getLocalPics(String uploadTaskId, String patrolTime) {
        return finalDb.findAllByWhere(ImageBean.class,
                "uploadTaskId = '" + uploadTaskId + "' and patrolTime = '" + patrolTime + "'");
    }

    /**
     * @return
     * @Description 根据唯一标识删除数据库中由添加图片操作选择的图片
     * @author joe
     * @date 2014年11月26日 上午11:22
     */
    public void deleteLocalPics(String uploadTaskId) {
        finalDb.deleteByWhere(ImageBean.class, "uploadTaskId = '" + uploadTaskId + "'");
    }

    /**
     * @return
     * @Description 根据唯一标识删除数据库中插入的要上传的图片
     * @author joe
     * @date 2014年11月26日 上午11:22
     */
    public void deletePics(String typeOfId) {
        finalDb.deleteByWhere(Picture.class, "typeOfId = '" + typeOfId + "'");
    }

    /**
     * @return
     * @Description 获取数据库中插入将要上传的图片
     * @author joe
     * @date 2014年11月26日 上午11:22
     */
    public void insertPic(Picture pic) {
        finalDb.save(pic);
    }

    /**
     * @return
     * @Description 更新数据库中上传图片的信息
     * @author joe
     * @date 2014年11月26日 上午11:22
     */
    public void updatePic(Picture pic) {
        Picture picture = new Picture();
        picture.setIsUploadSuccess(pic.getIsUploadSuccess());
        picture.setPicId(pic.getPicId());
        picture.setTypeOfId(pic.getTypeOfId());
        picture.setIsWrokUpdate(pic.getIsWrokUpdate());
        picture.setType(pic.getType());
        finalDb.update(picture, "createTime = '" + pic.getCreateTime() + "'");
    }

    /**
     * @return
     * @Description 根据生成时间获取数据库中上传图片的信息
     * @author Administrator
     * @date 2014年11月26日 上午11:22
     */
    public Picture getPicByCreateTime(String createTime) {
        List<Picture> result = finalDb.findAllByWhere(Picture.class, "createTime = '" + createTime + "'");
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * @return
     * @Description 根据typeOfId获取数据库中上传图片的信息
     * @author Administrator
     * @date 2014年11月26日 上午11:22
     */
    public Picture getPicByTypeOfId(String typeOfId) {
        List<Picture> result = finalDb.findAllByWhere(Picture.class, "typeOfId = '" + typeOfId + "'");
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * @return
     * @Description 获取数据库中没有上传成功的照片
     * @author joe
     * @date 2014年11月11日 上午10:58:03
     */
    public List<Picture> getPictures() {
        return finalDb.findAllByWhere(Picture.class, "isUploadSuccess = '0'");
    }

    /**
     * @return
     * @Description 获取数据库中上传成功但未和数据绑定的图片信息
     * @author joe
     * @date 2014年11月11日 上午10:58:03
     */
    public List<Picture> getPicturesUnBind() {
        List<Picture> data = finalDb.findAllByWhere(Picture.class,
                "isUploadSuccess != 0 and isWrokUpdate = 0 and picId is not null");
        List<Picture> result = new ArrayList<Picture>();
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                Picture picture = data.get(i);
                try {
                    Integer.parseInt(picture.getTypeOfId());
                    result.add(picture);
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public String getOfficeId(String officeName) {
        // XXX 这里有站的时候才执行下面的代码，请改为精确判断是否是苏东xx站
        if (!officeName.contains("站")) {
            return null;
        }
        String officeName2 = officeName.replace("站", "X站");
        List<PlanTemplateDetail> planTemplateDetails = finalDb.findAllByWhere(PlanTemplateDetail.class,
                "officeName = '" + officeName2 + "'");
        if (planTemplateDetails.size() > 0) {
            return planTemplateDetails.get(0).getOfficeId();
        } else {
            return null;
        }
    }

    /**
     * @param type
     * @return
     * @deprecated
     */
    public List<Picture> getPictures(String type) {
        return finalDb.findAllByWhere(Picture.class,
                "isUploadSuccess = 2 and isWrokUpdate = 0 and type = '" + type + "'");
    }

    public PlanDetail getPlanDetailById(String id) {
        return finalDb.findById(id, PlanDetail.class);
    }

    /**
     * @param createTime
     * @return
     * @deprecated
     */
    public PlanDetail getPlanDetailByCreateTime(String createTime) {
        List<PlanDetail> result = finalDb.findAllByWhere(PlanDetail.class, "createTime = '" + createTime + "'");
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * 根据itemId获取Plandetail数据
     *
     * @return
     */
    public PlanDetail getPlanDetailByItemIdAndPatrolTime(String itemId, String patrolTime) {
        List<PlanDetail> result = finalDb.findAllByWhere(PlanDetail.class,
                "itemId = '" + itemId + "' and patrolTime = '" + patrolTime + "'");
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * 根据code获取Plandetail数据
     *
     * @return
     */
    public PlanDetail getPlanDetailByCodeAndPatrolTime(String code, String patrolTime) {
        List<PlanDetail> result = finalDb.findAllByWhere(PlanDetail.class,
                "code = '" + code + "' and patrolTime = '" + patrolTime + "' limit 1");
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public Task getTaskByHistoryId(String historyId) {
        List<Task> tasks = finalDb.findAllByWhere(Task.class, "historyId = '" + historyId + "'");
        if (tasks.size() > 0) {
            return tasks.get(0);
        } else {
            return null;
        }
    }

    public Gis getGisByTime(String time) {
        List<Gis> gises = finalDb.findAllByWhere(Gis.class, "time = '" + time + "'");
        if (gises.size() > 0) {
            return gises.get(0);
        } else {
            return null;
        }
    }


    public void insertException(UploadException exception) {
        // deleteException(exception);
        finalDb.save(exception);
    }

    public void insertHse(UploadHseSupervision uploadHseSupervision) {
        // deleteException(exception);
        finalDb.save(uploadHseSupervision);
    }

    public void deleteException(UploadException exception) {
        finalDb.deleteByWhere(UploadException.class, "time = '" + exception.getTime() + "'");
    }

    public void deleteExceptionByItemId(String itemId) {
        finalDb.deleteByWhere(UploadException.class, "itemId = '" + itemId + "'");
    }

    public UploadException geteExceptionByTime(String time) {
        List<UploadException> exceptions = finalDb.findAllByWhere(UploadException.class, "time = '" + time + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    public UploadHseSupervision getHseByTime(String time) {
        List<UploadHseSupervision> exceptions = finalDb.findAllByWhere(UploadHseSupervision.class,
                "createdDate = '" + time + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    public UploadException geteExceptionByItemId(String itemId) {
        List<UploadException> exceptions = finalDb.findAllByWhere(UploadException.class, "itemId = '" + itemId + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    public UploadException geteExceptionByHistoryId(String historyId) {
        List<UploadException> exceptions = finalDb.findAllByWhere(UploadException.class,
                "historyId = '" + historyId + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    public List<UploadException> geteExceptionByUserId(String userId) {
        return finalDb.findAllByWhere(UploadException.class, "userId = '" + userId + "' and itemId is null",
                "time desc");
    }

    public Task geteExceptionByHistoryId1(String historyId) {
        List<Task> exceptions = finalDb.findAllByWhere(Task.class,
                "historyId = '" + historyId + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    /**
     * 这是意见反馈查询对比
     * @param historyId
     * @return
     */
    public FeedBack geteExceptionByHistoryId2(String historyId) {
        List<FeedBack> exceptions = finalDb.findAllByWhere(FeedBack.class,
                "time = '" + historyId + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    public Task geteExceptionByItemId1(String itemId) {
        List<Task> exceptions = finalDb.findAllByWhere(Task.class, "itemId = '" + itemId + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    public Task geteExceptionByTime1(String time) {
        List<Task> exceptions = finalDb.findAllByWhere(Task.class, "endDate = '" + time + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }
    public FeedBack geteExceptionByTime2(String time) {
        List<FeedBack> exceptions = finalDb.findAllByWhere(FeedBack.class, "time = '" + time + "'");
        if (exceptions != null && exceptions.size() > 0) {
            return exceptions.get(0);
        }
        return null;
    }

    /**
     * @return
     * @description 更新数据库中的上报异常数据
     */
    public void updetaException(UploadException exception) {
        UploadException data = new UploadException();
        data.setWorkId(exception.getWorkId());
        data.setHistoryId(exception.getHistoryId());
        data.setIsUploadSuccess(exception.getIsUploadSuccess());
        data.setPicId(exception.getPicId());
        finalDb.update(data, "time = '" + exception.getTime() + "'");
    }

    /**
     *更新数据库中的任务完成信息
     */
    public void updetaException1(Task task) {
        Task data = new Task();
        data.setPics(task.getPics());
        data.setIsFinished(task.getIsFinished());
        finalDb.update(data, "taskId = '" + task.getTaskId() + "'");
    }
    /**
     * @return
     * @description 更新数据库中的Hse数据
     */
    public void updetaHse(UploadHseSupervision hseSupervision) {
        finalDb.update(hseSupervision, "createdDate = '" + hseSupervision.getCreatedDate() + "'");
    }

    /**
     * @return
     * @description 更新数据库中的上报异常数据
     */
    public void updetaExceptionOfPlanDetail(UploadException exception) {
        finalDb.update(exception,
                "itemId = '" + exception.getItemId() + "' and patrolTime = '" + exception.getPatrolTime() + "'");
    }

    /**
     * @return
     * @description 更新数据库中的上报异常数据
     */
    public void updetaNetExceptionStatus(UploadException exception) {
        UploadException data = new UploadException();
        data.setFromWhere(exception.getFromWhere());
        data.setPatrolTime(exception.getPatrolTime());
        finalDb.update(data, "itemId = '" + exception.getItemId() + "'");
    }

    /**
     * @return
     * @description 获取数据库中没有上传成功的上报异常数据
     */
    public List<UploadException> getAllUnUploadException() {
        return finalDb.findAllByWhere(UploadException.class,
                "isUploadSuccess != '1' and workId = '' and historyId = ''");
    }


    /**
     * 获取数据库中没有上传成功的任务填写信息
     * @return
     */
    public List<Task> getAllUnUploadExceptionTask() {
        return finalDb.findAllByWhere(Task.class,
                "isFinished = '1' ");
    }

    /**
     * 获取数据库中没有上传成功的意见反馈信息
     * @return
     */
    public List<FeedBack> getAllUnUploadExceptionFeedBack() {
        return finalDb.findAllByWhere(FeedBack.class,
                "isUploadSuccess != '1' ");
    }

    /**
     * @return
     * @description 获取数据库中没有上传成功的HSE数据
     */
    public List<UploadHseSupervision> getAllUnUploadHse() {
        return finalDb.findAllByWhere(UploadHseSupervision.class, "isSuccess != '1'");
    }

    /**
     * @return
     * @description 获取数据库中所有的HSE数据
     */
    public List<UploadHseSupervision> getAllHseData() {
        return finalDb.findAll(UploadHseSupervision.class);
    }

    /**
     * @return
     * @description 获取数据库中上传成功但没有生成任务的上报异常数据
     */
    public List<UploadException> getAllUnHandleException() {
        return finalDb.findAllByWhere(UploadException.class,
                "isUploadSuccess != '1' and workId != '' and historyId != ''");
    }

    /**
     * @return
     * @description 获取数据库中已作废的巡检异常项数据
     */
    public List<UploadException> getAllUnUsedException(String patrolTime) {
        return finalDb.findAllByWhere(UploadException.class,
                "fromWhere != '1' and itemId != '' and patrolTime != '" + patrolTime + "'");
    }

    public boolean isCurrentStationPlanExist(String officeName, String type) {
        List<PlanDetail> planDetails = finalDb.findAllByWhere(PlanDetail.class,
                "officeName = '" + officeName + "' and type = '" + type + "' limit 1");
        return planDetails.size() > 0;
    }

    public Device getDeviceByDeviceCode(String deviceCode) {
        List<Device> devices = finalDb.findAllByWhere(Device.class, "code ='" + deviceCode + "'");
        if (devices.size() > 0) {
            return devices.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取当前巡检点id
     *
     * @return
     */
    public String getCurrentPointId(String officeId) {
        List<PlanDetail> cuDetails = finalDb.findAllByWhere(PlanDetail.class, "type = '1' and officeId = '" + officeId
                + "'and result is not null GROUP BY pointId ORDER BY updateTime DESC LIMIT 1");
        if (cuDetails.size() > 0) {
            return cuDetails.get(0).getPointId() + "";
        } else {
            return null;
        }
    }

    /**
     * @Description 保存管线巡护完成后的信息
     */
    public void insertGisFinish(GisFinish gisFinish) {
        finalDb.save(gisFinish);
    }

    /**
     * @Description 保存管线巡护完成后的信息
     */
    public void updateGisFinish(GisFinish gisFinish) {
        finalDb.update(gisFinish, "creatTime = '" + gisFinish.getCreatTime() + "'");
    }

    /**
     * @Description 获取未提交的Gis路线信息
     * @author yl
     * @date 2014年12月2日
     */
    public List<GisFinish> getGisFinishNotSubmit(String userId) {
        return finalDb.findAllByWhere(GisFinish.class,
                "status != '2' and status != '4' and lineName !='' and endTime != '' and userId = '" + userId + "'");
    }

    /**
     * @Description 根据生成时间获取未提交的Gis路线信息
     * @author yl
     * @date 2014年12月2日
     */
    public GisFinish getGisFinishNotSubmitByCreateTime(String createTime) {
        List<GisFinish> data = finalDb.findAllByWhere(GisFinish.class, "creatTime = '" + createTime + "'");
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    /**
     * @Description 获取未结束的巡检任务
     * @author yl
     * @date 2014年12月2日
     */
    public GisFinish getGisFinishNotFinish() {
        List<GisFinish> data = finalDb.findAllByWhere(GisFinish.class, "lineName = '' and endTime = ''");
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    /**
     * @Description 根据巡检项id获取巡检时生成的参数异常派工
     * @author joe
     * @date 2014年12月2日
     */
    public UploadException getExceptionOfPlanDetail(String itemId) {
        List<UploadException> uploadExceptions = finalDb.findAllByWhere(UploadException.class,
                "itemId = '" + itemId + "'");
        if (uploadExceptions.size() > 0) {
            return uploadExceptions.get(0);
        } else {
            return null;
        }
    }

    public List<UploadException> getAllHisNetException() {
        // return finalDb.findAllByWhere(UploadException.class,
        // "fromWhere != '1' and patrolTime != ''");
        return finalDb.findAllByWhere(UploadException.class, "fromWhere != '' and patrolTime != ''");
    }

    public void delettNetException() {
        finalDb.deleteByWhere(UploadException.class, "fromWhere = '2'");
    }

    /**
     * 返回罐车信息
     *
     * @return
     */
    public List<Tank> getTankInfo() {
        List<Tank> tasks = finalDb.findAllByWhere(Tank.class, "number!=''");
        return tasks;
    }

    public List<User> getUserInfo() {
        List<User> users = finalDb.findAllByWhere(User.class, "name!=''");
        return users;
    }

    /**
     * 根据车辆号码查询对应存储罐的截面积
     *
     * @param number
     * @return
     */
    public String getAreaByNum(String number) {
        List<Tank> tank = finalDb.findAllByWhere(Tank.class, "number = '" + number + "'");
        return (tank.size() == 0 ? 0 : tank.size()) == 0 ? "" : tank.get(0).getTankarea();
    }

    public Tank getIdByNum(String number) {
        List<Tank> tank = finalDb.findAllByWhere(Tank.class, "number = '" + number + "'");
        int m = tank.size() == 0 ? 0 : tank.size();
        return m == 0 ? null : tank.get(0);
    }

    /**
     * 判断Tank表中横截面和id是否为空
     *
     * @return
     */
    public int getTankBy() {
        List<Tank> tanks = finalDb.findAllByWhere(Tank.class, "tankarea is not null");
        return tanks.size();
    }

    /**
     * 将意见反馈保存到数据库中
     * @param feedBack
     */
    public void insertFeed(FeedBack feedBack){
        finalDb.save(feedBack);
    }

    /**
     * 更新意见反馈数据
     * @param feedBack
     */
    public void updetaExceptionFeedBack(FeedBack feedBack) {
        FeedBack data = new FeedBack();
        data.setIsUploadSuccess(feedBack.getIsUploadSuccess());
        data.setPicId(feedBack.getPicId());
        finalDb.update(data, "time = '" + feedBack.getTime() + "'");
    }

}

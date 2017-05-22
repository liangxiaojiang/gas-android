package com.joe.oil.http;

import android.annotation.SuppressLint;
import android.util.Log;

import com.joe.oil.entity.CheckItem;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.DeviceTree;
import com.joe.oil.entity.Dict;
import com.joe.oil.entity.DictDetail;
import com.joe.oil.entity.Line;
import com.joe.oil.entity.Office;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.PlanTemplateDetail;
import com.joe.oil.entity.Single;
import com.joe.oil.entity.Tank;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.UploadException;
import com.joe.oil.entity.User;
import com.joe.oil.entity.Well;
import com.joe.oil.entity.WorkDetail;
import com.joe.oil.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author baiqiao
 * @description 所有JSON格式数据解析
 * @data: 2014年7月4日 下午2:56:51
 * @email baiqiao@lanbaoo.com
 */
@SuppressLint("SimpleDateFormat")
public class JsonHelper {
    /**
     * @param json
     * @return List<Task>
     * @Description 派工列表解析
     * @date 2014年7月4日 下午2:57:52
     */
    public List<Task> getTaskList(String json) {
        List<Task> tasks = new ArrayList<Task>();
        List<DictDetail> details = new ArrayList<DictDetail>();
        try {
            JSONArray array = new JSONObject(json).getJSONObject("page").getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {

                Task task = new Task();
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("integral").equals("null")) {
                    task.setIntegral("0");
                } else {
                    task.setIntegral(obj.getString("integral"));
                }
                task.setTaskId(obj.getString("id"));
                task.setName(obj.getString("name"));
                task.setDeviceIds(obj.getString("deviceCode"));
                if (obj.getString("deviceNames").equals("null")) {
                    task.setDeviceNames("");
                } else {
                    task.setDeviceNames(obj.getString("deviceNames"));
                }

                task.setCreateTime(obj.getString("createTime"));
                if (obj.getString("deadTime").length() == task.getCreateTime().length()) {
                    task.setDeadTime(obj.getString("deadTime"));
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateString = format.format(new Date());
                    task.setDeadTime(dateString);
                }

                if (obj.getString("controlTips").equals("null")) {
                    task.setControlTips("");
                } else {
                    task.setControlTips(obj.getString("controlTips"));
                }

                if (obj.getString("riskTips").equals("null")) {
                    task.setRiskTips("");
                } else {
                    task.setRiskTips(obj.getString("riskTips"));
                }

                if (obj.getString("memo").equals("null")) {
                    task.setMemo("");
                } else {
                    task.setMemo(obj.getString("memo"));
                }

                task.setReason(obj.getString("reason"));
                task.setWorkNo(obj.getString("workNo"));
                String interval = obj.getString("interval");
                if (interval.equals("") || interval.equals("null") || interval.equals("0")) {
                    task.setInterval("1");
                } else {
                    task.setInterval(interval);
                }
                task.setOperateCard(obj.getString("operateCard"));
                task.setOperateCardUrl(obj.getString("operateCardUrl"));
                task.setOrderId(obj.getString("orderId"));
                task.setOrderNo(obj.getString("orderNo"));
                task.setChargerId(obj.getString("chargerId"));
                task.setPartnerId(obj.getString("partnerIds"));
                task.setCreatorId(obj.getString("creatorId"));
                task.setVehicleId(obj.getString("vehicleId"));
                task.setTaskTypeId(obj.getString("taskTypeId"));
                task.setChargerName(obj.getString("chargerName"));
                if (obj.getString("partnerNames").equals("null")) {
                    task.setPartnerName("");
                } else {
                    task.setPartnerName(obj.getString("partnerNames"));
                }

                task.setCreatorName(obj.getString("creatorName"));
                if (obj.getString("vehicleNumber").equals("null")) {
                    task.setVehicleNumber("");
                } else {
                    task.setVehicleNumber(obj.getString("vehicleNumber"));
                }

                task.setVehicleName(obj.getString("vehicleName"));
                if (obj.getString("vehicleDriverName").equals("null")) {
                    task.setVehicleDriverName("");
                } else {
                    task.setVehicleDriverName(obj.getString("vehicleDriverName"));
                }
                if (obj.getString("vehicleDriverPhone").equals("null")) {
                    task.setVehicleDriverPhone("");
                } else {
                    task.setVehicleDriverPhone(obj.getString("vehicleDriverPhone"));
                }
                if (obj.getString("vehicleRoute").equals("null")) {
                    task.setTaskRoute("");
                } else {
                    task.setTaskRoute(obj.getString("vehicleRoute"));
                }
                if (obj.getString("vehicleCode").equals("null")) {
                    task.setTaskSingleNumber("");
                } else {
                    task.setTaskSingleNumber(obj.getString("vehicleCode"));
                }

                task.setTaskTypeName(obj.getString("taskTypeName"));
                task.setActId(obj.getString("actId"));
                task.setActName(obj.getString("actName"));
                task.setHistoryId(obj.getString("historyId"));

                if (obj.getString("operateWorkUrl").equals("null")){
                    task.setDocumentName("");
                    task.setDocumentUrl("");
                }else {
                    task.setDocumentName(obj.getString("operateWorkUrl").substring(obj.getString("operateWorkUrl").indexOf(",")+1));
                    task.setDocumentUrl(obj.getString("operateWorkUrl").substring(0,obj.getString("operateWorkUrl").indexOf(",")));
                    Log.d("文档url",task.getDocumentUrl()+"///"+task.getDocumentName());
                }
                JSONObject history = obj.getJSONObject("history");
                task.setManagerTime(history.getString("startTime"));
                JSONArray dictdetails = obj.getJSONArray("dictDetails");
                if (dictdetails.length() == 0) {

                } else {
                    for (int j = 0; j < dictdetails.length(); j++) {

                        DictDetail dictDetail = new DictDetail();
                        JSONObject obj1 = dictdetails.getJSONObject(j);
                        dictDetail.setTitle(obj1.getString("title"));
                        dictDetail.setType(obj1.getInt("type"));
                        dictDetail.setHint(obj1.getString("hint"));
                        dictDetail.setWorkTypeId(obj1.getString("workTypeId"));
                        dictDetail.setTaskId(obj.getString("id"));
                        dictDetail.setTaskName(obj.getString("taskTypeName"));
                        Log.d("中国2", "" + dictDetail.getTitle() + "---" + dictDetail.getType());
                        details.add(dictDetail);
                    }
                    task.setDictDetails(details);
                }

                tasks.add(task);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    public List<Task> getTaskList1(String json) {
        List<Task> tasks = new ArrayList<Task>();
        List<WorkDetail> workdetail = new ArrayList<WorkDetail>();
        try {
            JSONArray array = new JSONObject(json).getJSONObject("page").getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {

                Task task = new Task();
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("integral").equals("null")) {
                    task.setIntegral("0");
                } else {
                    task.setIntegral(obj.getString("integral"));
                }
                task.setTaskId(obj.getString("id"));
                task.setName(obj.getString("name"));
                task.setDeviceIds(obj.getString("deviceCode"));
                if (obj.getString("deviceNames").equals("null")) {
                    task.setDeviceNames("");
                } else {
                    task.setDeviceNames(obj.getString("deviceNames"));
                }

                task.setCreateTime(obj.getString("createTime"));
                if (obj.getString("deadTime").length() == task.getCreateTime().length()) {
                    task.setDeadTime(obj.getString("deadTime"));
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateString = format.format(new Date());
                    task.setDeadTime(dateString);
                }

                if (obj.getString("controlTips").equals("null")) {
                    task.setControlTips("");
                } else {
                    task.setControlTips(obj.getString("controlTips"));
                }

                if (obj.getString("riskTips").equals("null")) {
                    task.setRiskTips("");
                } else {
                    task.setRiskTips(obj.getString("riskTips"));
                }

                if (obj.getString("memo").equals("null")) {
                    task.setMemo("");
                } else {
                    task.setMemo(obj.getString("memo"));
                }

                task.setReason(obj.getString("reason"));
                task.setWorkNo(obj.getString("workNo"));
                String interval = obj.getString("interval");
                if (interval.equals("") || interval.equals("null") || interval.equals("0")) {
                    task.setInterval("1");
                } else {
                    task.setInterval(interval);
                }
                task.setOperateCard(obj.getString("operateCard"));
                task.setOperateCardUrl(obj.getString("operateCardUrl"));
                task.setOrderId(obj.getString("orderId"));
                task.setOrderNo(obj.getString("orderNo"));
                task.setChargerId(obj.getString("chargerId"));
                task.setPartnerId(obj.getString("partnerIds"));
                task.setCreatorId(obj.getString("creatorId"));
                task.setVehicleId(obj.getString("vehicleId"));
                task.setTaskTypeId(obj.getString("taskTypeId"));
                task.setChargerName(obj.getString("chargerName"));
                if (obj.getString("partnerNames").equals("null")) {
                    task.setPartnerName("");
                } else {
                    task.setPartnerName(obj.getString("partnerNames"));
                }

                task.setCreatorName(obj.getString("creatorName"));
                if (obj.getString("vehicleNumber").equals("null")) {
                    task.setVehicleNumber("");
                } else {
                    task.setVehicleNumber(obj.getString("vehicleNumber"));
                }

                task.setVehicleName(obj.getString("vehicleName"));
                if (obj.getString("vehicleDriverName").equals("null")) {
                    task.setVehicleDriverName("");
                } else {
                    task.setVehicleDriverName(obj.getString("vehicleDriverName"));
                }
                if (obj.getString("vehicleDriverPhone").equals("null")) {
                    task.setVehicleDriverPhone("");
                } else {
                    task.setVehicleDriverPhone(obj.getString("vehicleDriverPhone"));
                }
                if (obj.getString("vehicleRoute").equals("null")) {
                    task.setTaskRoute("");
                } else {
                    task.setTaskRoute(obj.getString("vehicleRoute"));
                }
                if (obj.getString("vehicleCode").equals("null")) {
                    task.setTaskSingleNumber("");
                } else {
                    task.setTaskSingleNumber(obj.getString("vehicleCode"));
                }

                task.setTaskTypeName(obj.getString("taskTypeName"));
                task.setActId(obj.getString("actId"));
                task.setActName(obj.getString("actName"));
                task.setHistoryId(obj.getString("historyId"));

                JSONObject history = obj.getJSONObject("history");
                task.setManagerTime(history.getString("startTime"));
                JSONArray workDetails = obj.getJSONArray("workDetails");
                if (workDetails.length() == 0) {

                } else {
                    for (int j = 0; j < workDetails.length(); j++) {

                        WorkDetail workDetail = new WorkDetail();
                        JSONObject obj1 = workDetails.getJSONObject(j);
                        workDetail.setTitle(obj1.getString("title"));
                        workDetail.setType(obj1.getInt("type"));
                        workDetail.setHint(obj1.getString("hint"));
                        workDetail.setId(obj1.getInt("id"));
                        workDetail.setWorkId(obj.getString("id"));
                        workDetail.setContent(obj1.getString("content"));
                        Log.d("中国2", "" + workDetail.getTitle() + "---" + workDetail.getType());
                        workdetail.add(workDetail);
                    }
                    task.setWorkDetails(workdetail);
                }

                tasks.add(task);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Single> getSingle(String json) {
        List<Single> singles = new ArrayList<Single>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                Single single = new Single();
                JSONObject object = array.getJSONObject(i);
                single.setVehicleTask(object.getString("vehicleTask"));
                single.setChargerId(object.getString("chargerId"));
                single.setChargerName(object.getString("chargerName"));
                single.setCreatedDate(object.getString("createdDate"));
                single.setEndTime(object.getString("endTime"));
                if (object.getString("number").equals("null")) {
                    single.setNumber("");
                } else {
                    single.setNumber(object.getString("number"));
                }
                single.setOfficeId(object.getString("officeId"));
                single.setOfficeName(object.getString("officeName"));
                if (object.getString("passengerPhone").equals("null")) {
                    single.setPassengerPhone("");
                } else {
                    single.setPassengerPhone(object.getString("passengerPhone"));
                }
                single.setStartTime(object.getString("startTime"));

                single.setVehicleId(object.getString("vehicleId"));

                single.setVehicleRoute(object.getString("vehicleRoute"));

                single.setWorkId(object.getString("workId"));
                if (object.getString("realStartTime").equals("null")) {
                    single.setRealStartTime("");
                } else {
                    single.setRealStartTime(object.getString("realStartTime"));
                }
                if (object.getString("realEndTime").equals("null")) {
                    single.setRealEndTime("");
                } else {
                    single.setRealEndTime(object.getString("realEndTime"));
                }
                if (object.getString("driverPhone").equals("null")) {
                    single.setRealEndTime("");
                } else {
                    single.setDriverPhone(object.getString("driverPhone"));
                }

                single.setDriverName(object.getString("driverName"));
                single.setSingleId(object.getString("id"));
                single.setVehicleCode(object.getString("vehicleCode"));

                if (object.getString("actName").equals("null")) {
                    single.setRealEndTime("");
                } else {
                    single.setActName(object.getString("actName"));
                }
                if (object.getString("creatorId").equals("null")) {
                    single.setCreatorId("");
                } else {
                    single.setCreatorId(object.getString("creatorId"));
                }
                singles.add(single);

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return singles;
    }

    public List<Task> getTaskInfoList(String json) {
        List<Task> tasks = new ArrayList<Task>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                Task task = new Task();
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("integral").equals("null")) {
                    task.setIntegral("0");
                } else {
                    task.setIntegral(obj.getString("integral"));
                }
                task.setTaskId(obj.getString("id"));
                task.setName(obj.getString("name"));
                task.setDeviceIds(obj.getString("deviceCode"));
                if (obj.getString("deviceNames").equals("null")) {
                    task.setDeviceNames("");
                } else {
                    task.setDeviceNames(obj.getString("deviceNames"));
                }
                task.setCreateTime(obj.getString("createTime"));
                if (obj.getString("controlTips").equals("null")) {
                    task.setControlTips("");
                } else {
                    task.setControlTips(obj.getString("controlTips"));
                }

                if (obj.getString("riskTips").equals("null")) {
                    task.setRiskTips("");
                } else {
                    task.setRiskTips(obj.getString("riskTips"));
                }
                task.setWorkNo(obj.getString("workNo"));
//                String interval = obj.getString("interval");
//                if (interval.equals("") || interval.equals("null") || interval.equals("0")) {
//                    task.setInterval("1");
//                } else {
//                    task.setInterval(interval);
//                }
                task.setOrderId(obj.getString("orderId"));
                task.setOrderNo(obj.getString("orderNo"));
                task.setChargerId(obj.getString("chargerId"));
                if (obj.getString("partnerId").equals("null")) {
                    task.setPartnerId("");
                } else {
                    task.setPartnerId(obj.getString("partnerId"));
                }
                task.setCreatorId(obj.getString("creatorId"));
                if (obj.getString("vehicleId").equals("null")) {
                    task.setVehicleId("");
                } else {
                    task.setVehicleId(obj.getString("vehicleId"));
                }
                task.setChargerName(obj.getString("chargerName"));
                if (obj.getString("partnerName").equals("null")) {
                    task.setPartnerName("");
                } else {
                    task.setPartnerName(obj.getString("partnerName"));
                }

                task.setCreatorName(obj.getString("creatorName"));
                if (obj.getString("vehicleName").equals("null")) {
                    task.setVehicleName("");
                } else {
                    task.setVehicleName(obj.getString("vehicleName"));
                }
                if (obj.getString("vehicleDriverName").equals("null")) {
                    task.setVehicleDriverName("");
                } else {
                    task.setVehicleDriverName(obj.getString("vehicleDriverName"));
                }
                if (obj.getString("driverPhone").equals("null")) {
                    task.setVehicleDriverPhone("");
                } else {
                    task.setVehicleDriverPhone(obj.getString("driverPhone"));
                }
                task.setTaskTypeName(obj.getString("workTypeName"));
                task.setActId(obj.getString("actId"));
                task.setActName(obj.getString("actName"));
                task.setManagerTime(obj.getString("startTime"));
                tasks.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    public List<User> getUserList(String json) {
        List<User> users = new ArrayList<User>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("users");
            for (int i = 0; i < array.length(); i++) {
                User user = new User();
                JSONObject obj = array.getJSONObject(i);
                user.setUserId(obj.getString("id"));
                user.setLoginName(obj.getString("loginName"));
                user.setPassword(obj.getString("password"));
                user.setName(obj.getString("name"));
                user.setPhone(obj.getString("phone"));
                user.setMobile(obj.getString("mobile"));
                user.setHost(obj.getString("host"));
                user.setLoginDate(obj.getString("loginDate"));
                user.setRoleId(obj.getString("roleId"));
                user.setRoleName(obj.getString("roleName"));
                user.setRoleEnname(obj.getString("roleEnname"));
                user.setOfficeId(obj.getString("officeId"));
                user.setOfficeCode(obj.getString("officeCode"));
                user.setOfficeName(obj.getString("officeName"));
                user.setLoginStatus("0");
                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }


    public List<Tank> getTankList(String json) {
        List<Tank> tankInfo = new ArrayList<Tank>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("tankInfo");
            for (int i = 0; i < array.length(); i++) {
                Tank tank = new Tank();
                JSONObject obj = array.getJSONObject(i);
                tank.setTankid(obj.getInt("id"));
                tank.setNumber(obj.getString("number"));
                tank.setTankarea(obj.getString("tankArea"));
                tank.setName(obj.getString("name"));
                tankInfo.add(tank);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tankInfo;
    }

    /**
     * 解析任务模板数据
     *
     * @param json
     * @return
     */
    public List<DictDetail> getTaskTemplateList(String json) {
        List<DictDetail> taskTemplate = new ArrayList<DictDetail>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("dictDetail");
            for (int i = 0; i < array.length(); i++) {
                DictDetail taskTemplate1 = new DictDetail();
                JSONObject obj = array.getJSONObject(i);
                taskTemplate1.setTitle(obj.getString("title"));
                taskTemplate1.setHint(obj.getString("hint"));
                taskTemplate1.setType(obj.getInt("type"));
//                taskTemplate1.setIsNull(obj.getString("isNull"));
                taskTemplate1.setWorkTypeId(obj.getString("workTypeId"));
                taskTemplate.add(taskTemplate1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return taskTemplate;
    }

    /**
     * @param json
     * @return
     * @Description 巡检项解析
     * @author baiqiao
     * @date 2014年7月29日 上午9:14:11
     */
    // public List<CheckItem> getCheckItemList(String json) {
    // List<CheckItem> checkItems = new ArrayList<CheckItem>();
    // try {
    // JSONArray array = new JSONObject(json).getJSONArray("item");
    // for (int i = 0; i < array.length(); i++) {
    // CheckItem checkItem = new CheckItem();
    // JSONObject obj = array.getJSONObject(i);
    // checkItem.setAlias(obj.getString("alias"));
    // checkItem.setCode(obj.getString("code"));
    // checkItem.setItemId(obj.getString("id"));
    // checkItem.setDownValue(obj.getString("downValue"));
    // checkItem.setName(obj.getString("name"));
    // if (!obj.isNull("parent")) {
    // JSONObject parent = obj.getJSONObject("parent");
    // checkItem.setParentIds(parent.getString("id"));
    // }
    // checkItem.setPointId(obj.getString("pointId"));
    // checkItem.setPointType(obj.getString("pointType"));
    // checkItem.setUnit(obj.getString("unit"));
    // checkItem.setUpValue(obj.getString("upValue"));
    // checkItem.setType(obj.getString("type"));
    // checkItems.add(checkItem);
    // }
    // } catch (JSONException e) {
    // e.printStackTrace();
    // }
    // return checkItems;
    // }
    public List<CheckItem> getCheckItemList(String json) {
        List<CheckItem> checkItems = new ArrayList<CheckItem>();
        try {
            JSONObject object = new JSONObject(json).getJSONObject("data");
            Constants.CURRENT_PAGE = object.getInt("pageNo");
            Constants.TOTAL_COUNT = object.getInt("totalCount");
            Constants.NEXT_PAGE = object.getInt("nextPage");
            JSONArray array = object.getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                CheckItem checkItem = new CheckItem();
                JSONObject obj = array.getJSONObject(i);
                checkItem.setAlias(obj.getString("alias"));
                checkItem.setCode(obj.getString("code"));
                checkItem.setItemId(obj.getString("id"));
                checkItem.setDownValue(obj.getString("downValue"));
                checkItem.setName(obj.getString("name"));
                if (!obj.isNull("parent")) {
                    JSONObject parent = obj.getJSONObject("parent");
                    checkItem.setParentIds(parent.getString("id"));
                }
                checkItem.setPointId(obj.getString("pointId"));
                checkItem.setPointType(obj.getString("pointType"));
                checkItem.setUnit(obj.getString("unit"));
                checkItem.setUpValue(obj.getString("upValue"));
                checkItem.setType(obj.getString("type"));
                checkItem.setUpdateTime(obj.getString("updateTime"));
                checkItem.setTag(obj.getString("tag"));
                checkItem.setMemo(obj.getString("memo"));
                checkItems.add(checkItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return checkItems;
    }

    /**
     * @param json
     * @return
     * @Description 巡检点数据解析
     * @author baiqiao
     * @date 2014年7月29日 下午5:33:31
     */
    public List<Device> getDevices(String json) {
        List<Device> devices = new ArrayList<Device>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("device");
            for (int i = 0; i < array.length(); i++) {
                Device device = new Device();
                JSONObject object = array.getJSONObject(i);
                if (object.getString("code").equals("null")) {
                    device.setCode("");
                } else {
                    device.setCode(object.getString("code"));
                }
                device.setDeviceId(Integer.parseInt(object.getString("id")));
                device.setName(object.getString("name"));
                device.setOfficeName(object.getString("officeName"));
                if (object.getString("lat").equals("null")) {
                    device.setLat("");
                } else {
                    device.setLat(object.getString("lat"));
                }

                if (object.getString("lng").equals("null")) {
                    device.setLng("");
                } else {
                    device.setLng(object.getString("lng"));
                }

                if (object.getString("memo").equals("null")) {
                    device.setMemo("");
                } else {
                    device.setMemo(object.getString("memo"));
                }

                if (object.getString("tchDate").equals("null")) {
                    device.setTchDate("");
                } else {
                    device.setTchDate(object.getString("tchDate"));
                }

                if (object.getString("pch").equals("null")) {
                    device.setPch("");
                } else {
                    device.setPch(object.getString("pch"));
                }

                if (object.getString("djNum").equals("null")) {
                    device.setDjNum("");
                } else {
                    device.setDjNum(object.getString("djNum"));
                }

                if (object.getString("ysjNum").equals("null")) {
                    device.setYsjNum("");
                } else {
                    device.setYsjNum(object.getString("ysjNum"));
                }

                if (object.getString("flqNum").equals("null")) {
                    device.setFlqNum("");
                } else {
                    device.setFlqNum(object.getString("flqNum"));
                }

                if (object.getString("tshqNum").equals("null")) {
                    device.setTshqNum("");
                } else {
                    device.setTshqNum(object.getString("tshqNum"));
                }

                if (object.getString("shzhqNum").equals("null")) {
                    device.setShzhqNum("");
                } else {
                    device.setShzhqNum(object.getString("shzhqNum"));
                }

                if (object.getString("fdjNum").equals("null")) {
                    device.setFdjNum("");
                } else {
                    device.setFdjNum(object.getString("fdjNum"));
                }
                devices.add(device);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return devices;
    }

    // 井数据解析
    public List<Well> getWells(String json) {
        List<Well> wells = new ArrayList<Well>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                Well well = new Well();
                JSONObject object = array.getJSONObject(i);
                well.setCode(object.getString("code"));
                if (object.getString("id").equals("")) {
                    well.setWellId("1");
                } else {
                    well.setWellId(object.getString("id"));
                }
                well.setName(object.getString("name"));
                well.setTchDate(object.getString("tchDate"));
                if (object.getString("schcw").equals("null")) {
                    well.setSchcw("");
                } else {
                    well.setSchcw(object.getString("schcw"));
                }

                if (object.getString("wzll").equals("null")) {
                    well.setWzll("");
                } else {
                    well.setWzll(object.getString("wzll"));
                }

                if (object.getString("tchqTy").equals("null")) {
                    well.setTchqTy("");
                } else {
                    well.setTchqTy(object.getString("tchqTy"));
                }

                if (object.getString("tchqYy").equals("null")) {
                    well.setTchqYy("");
                } else {
                    well.setTchqYy(object.getString("tchqYy"));
                }

                if (object.getString("lhqhl").equals("null")) {
                    well.setLhqhl("");
                } else {
                    well.setLhqhl(object.getString("lhqhl"));
                }

                if (object.getString("pch").equals("null")) {
                    well.setPch("");
                } else {
                    well.setPch(object.getString("pch"));
                }
                wells.add(well);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wells;
    }

    /**
     * @param json
     * @return
     * @Description 巡检计划详情解析
     * @author baiqiao
     * @date 2014年7月29日 上午10:01:20
     */
    public List<PlanDetail> getPlanDetailList(String json) {
        List<PlanDetail> planDetails = new ArrayList<PlanDetail>();
        try {
            JSONArray plan = new JSONObject(json).getJSONArray("plan");
            for (int i = 0; i < plan.length(); i++) {

                JSONObject obj = plan.getJSONObject(i);
                PlanDetail planDetail = new PlanDetail();
                planDetail.setItemId(obj.getString("itemId"));
                planDetail.setPlanDetailId(obj.getString("id"));
                planDetail.setPointId(Integer.parseInt(obj.getString("pointId")));
                planDetail.setPointName(obj.getString("pointName"));
                planDetail.setPatrolTime(obj.getString("patrolTime"));
                planDetail.setItemName(obj.getString("itemName"));
                planDetail.setType(obj.getString("pointType"));
                planDetail.setDuration(obj.getString("duration"));
                planDetail.setPlanType(obj.getString("planType"));
                planDetail.setOfficeId(obj.getString("officeId"));
                planDetail.setOfficeName(obj.getString("officeName"));
                planDetail.setItemUnit(obj.getString("itemUnit"));
                if (obj.getString("duration").equals("null") || obj.getString("duration").equals("")) {
                    planDetail.setDuration("1");
                } else {
                    planDetail.setDuration(obj.getString("duration"));
                }
                if (obj.getString("planType").equals("1") && planDetail.getPatrolTime().length() > 13) {// 日计划通过波动时间算出巡检时间上限和下限
                    String hour = planDetail.getPatrolTime().substring(11, 13);
                    int afterAddHour = 0;
                    if (hour.length() > 0) {
                        afterAddHour = Integer.parseInt(hour) + Integer.parseInt(planDetail.getDuration());
                        if (afterAddHour >= 10 && afterAddHour <= 24) {
                            hour = afterAddHour + "";
                        } else if (afterAddHour > 24) {
                            hour = "24";
                        } else {
                            hour = "0" + afterAddHour;
                        }
                    }
                    planDetail.setDownTime(planDetail.getPatrolTime());
                    planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 11) + hour + planDetail.getPatrolTime().substring(13, 19));
                } else if (obj.getString("planType").equals("2") || obj.getString("planType").equals("3") && planDetail.getPatrolTime().length() > 13) {
                    String day = planDetail.getPatrolTime().substring(8, 10);
                    if (Constants.GPS_TIME.length() > 0) {
                        int month = Integer.parseInt(planDetail.getPatrolTime().substring(5, 7));
                        switch (month) {
                            case 1:
                            case 3:
                            case 5:
                            case 7:
                            case 8:
                            case 10:
                            case 12:
                                int afterDay = 0;
                                if (day.length() > 0) {
                                    afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                    if (afterDay > 10 && afterDay <= 31) {
                                        day = afterDay + "";
                                    } else {
                                        day = "0" + afterDay;
                                    }
                                }
                                break;

                            case 4:
                            case 6:
                            case 9:
                            case 11:
                                if (day.length() > 0) {
                                    afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                    if (afterDay > 10 && afterDay <= 30) {
                                        day = afterDay + "";
                                    } else {
                                        day = "0" + afterDay;
                                    }
                                }
                                break;

                            case 2:
                                if (day.length() > 0) {
                                    int year = Integer.parseInt(Constants.GPS_TIME.substring(0, 4));
                                    if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                        if (afterDay > 10 && afterDay <= 29) {
                                            day = afterDay + "";
                                        } else {
                                            day = "0" + afterDay;
                                        }
                                    } else {
                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                        if (afterDay > 10 && afterDay <= 28) {
                                            day = afterDay + "";
                                        } else {
                                            day = "0" + afterDay;
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }

                    }

                    planDetail.setDownTime(planDetail.getPatrolTime());
                    planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 8) + day + planDetail.getPatrolTime().substring(10, 19));
                }
                planDetail.setStatus("1");
                planDetail.setHandleMemoUpload("0");
                planDetail.setUpdateTime(obj.getString("updateTime"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                planDetail.setPatrolDate(format.format(new Date()));
                planDetails.add(planDetail);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return planDetails;
    }

    public List<Line> getLines(String json) {
        List<Line> lines = new ArrayList<Line>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                Line line = new Line();
                JSONObject obj = array.getJSONObject(i);
                line.setLineId(obj.getString("id"));
                line.setName(obj.getString("name"));
                line.setOfficeId(obj.getString("officeId"));
                line.setOfficeName(obj.getString("officeName"));
                lines.add(line);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public List<Office> getOffice(String json) {
        List<Office> offices = new ArrayList<Office>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                Office office = new Office();
                JSONObject obj = array.getJSONObject(i);
                office.setCode(obj.getString("code"));
                office.setGrade(obj.getString("grade"));
                office.setLat(obj.getString("lat"));
                office.setLng(obj.getString("lng"));
                office.setName(obj.getString("name"));
                office.setOfficeId(obj.getString("id"));
                // office.setParentId(obj.getString("parentId"));
                office.setParentIds(obj.getString("parentIds"));
                office.setType(obj.getString("type"));
                offices.add(office);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return offices;
    }

    /**
     * @param json
     * @return
     * @Description 解析字典数据
     * @author baiqiao
     * @date 2014年9月3日 上午11:02:17
     */
    public List<Dict> getDicts(String json) {
        List<Dict> dicts = new ArrayList<Dict>();
        try {
            JSONObject task = new JSONObject(json);
            Dict dict = new Dict();
            dict.setDictId(task.getString("id"));
            dict.setLabel(task.getString("label"));
            dict.setType(task.getString("type"));
            dict.setValue(task.getString("value"));
            dict.setGrade("1");
            dicts.add(dict);
            JSONArray taskChildArray = task.getJSONArray("childList");
            for (int i = 0; i < taskChildArray.length(); i++) {
                Dict taskChildDict = new Dict();
                JSONObject taskchildJsonObject = taskChildArray.getJSONObject(i);
                taskChildDict.setDictId(taskchildJsonObject.getString("id"));
                taskChildDict.setLabel(taskchildJsonObject.getString("label"));
                taskChildDict.setType(taskchildJsonObject.getString("type"));
                taskChildDict.setValue(taskchildJsonObject.getString("value"));
                taskChildDict.setParentIds(taskchildJsonObject.getString("parentIds"));
                taskChildDict.setGrade("2");
                dicts.add(taskChildDict);
                if (!taskchildJsonObject.getString("childList").equals("")) {
                    JSONArray stationChildArray = taskchildJsonObject.getJSONArray("childList");
                    for (int j = 0; j < stationChildArray.length(); j++) {
                        Dict stationChildDict = new Dict();
                        JSONObject stationchildJsonObject = stationChildArray.getJSONObject(j);
                        stationChildDict.setDictId(stationchildJsonObject.getString("id"));
                        stationChildDict.setLabel(stationchildJsonObject.getString("label"));
                        stationChildDict.setType(stationchildJsonObject.getString("type"));
                        stationChildDict.setValue(stationchildJsonObject.getString("value"));
                        stationChildDict.setParentIds(stationchildJsonObject.getString("parentIds"));
                        stationChildDict.setGrade("3");
                        dicts.add(stationChildDict);
                        if (!stationchildJsonObject.getString("childList").equals("")) {
                            JSONArray pointChildArray = stationchildJsonObject.getJSONArray("childList");
                            for (int k = 0; k < pointChildArray.length(); k++) {
                                Dict pointChildDict = new Dict();
                                JSONObject pointchildJsonObject = pointChildArray.getJSONObject(k);
                                pointChildDict.setDictId(pointchildJsonObject.getString("id"));
                                pointChildDict.setLabel(pointchildJsonObject.getString("label"));
                                pointChildDict.setType(pointchildJsonObject.getString("type"));
                                pointChildDict.setValue(pointchildJsonObject.getString("value"));
                                pointChildDict.setParentIds(pointchildJsonObject.getString("parentIds"));
                                pointChildDict.setGrade("4");
                                dicts.add(pointChildDict);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return dicts;
    }

    /**
     * 根据CODE得到作业点位信息JSON解析
     *
     * @param json
     * @return
     */
    public List<Device> getNameList(String json) {
        List<Device> devices = new ArrayList<Device>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                Device device = new Device();
                JSONObject object = array.getJSONObject(i);
                device.setFlqNum(object.getString("flqNum"));
                device.setPch(object.getString("pch"));
                device.setDjNum(object.getString("djNum"));
                device.setMemo(object.getString("memo"));
                device.setYsjNum(object.getString("ysjNum"));
                device.setLng(object.getString("lng"));
                device.setCode(object.getString("code"));
                device.setFdjNum(object.getString("fdjNum"));
                device.setTshqNum(object.getString("tshqNum"));
                device.setShzhqNum(object.getString("shzhqNum"));
                device.setOfficeName(object.getString("officeName"));
                device.setName(object.getString("name"));
                device.setLat(object.getString("lat"));
                device.setTchDate(object.getString("tchDate"));
                devices.add(device);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return devices;
    }


    public List<Map<String, Object>> getTaskInfo(String json) {
        List<Map<String, Object>> infos = new ArrayList<Map<String, Object>>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                JSONObject object = array.getJSONObject(i);
                if (object.getString("num").equals("null")) {
                    map.put("num", "0");
                } else {
                    map.put("num", object.getString("num"));
                }

                if (object.getString("times").equals("null")) {
                    map.put("times", "0");
                } else {
                    map.put("times", object.getString("times"));
                }
                if (object.getString("backNum").equals("null")) {
                    map.put("backNum", "0");
                } else {
                    map.put("backNum", object.getString("backNum"));
                }
                if (object.getString("integral").equals("null")) {
                    map.put("integral", "0");
                } else {
                    map.put("integral", object.getString("integral"));
                }

                infos.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return infos;
    }


    /**
     * @param json
     * @return
     * @Description deviceTree解析
     * @author baiqiao
     * @date 2014年9月3日 下午5:27:46
     */
    public List<DeviceTree> getDeviceTrees(String json, String cardType) {
        List<DeviceTree> deviceTrees = new ArrayList<DeviceTree>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                DeviceTree deviceTree = new DeviceTree();
                JSONObject object = array.getJSONObject(i);
                deviceTree.setCode(object.getString("id"));
                deviceTree.setParentId(object.getString("parent"));
                deviceTree.setText(object.getString("text"));
                try {
                    JSONObject data = object.getJSONObject("data");
                    deviceTree.setTreeId(data.getString("id"));
                    deviceTree.setOfficeId(data.getString("officeId"));
                    deviceTree.setType(data.getString("type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                deviceTree.setCardType(cardType);
                deviceTrees.add(deviceTree);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deviceTrees;
    }

    /**
     * @param json
     * @return
     * @Description 巡检计划模板下载
     * @author baiqiao
     * @date 2014年9月4日 下午5:31:27
     */
    public List<PlanTemplateDetail> getPlanTemplateDetails(String json, String pointType) {
        List<PlanTemplateDetail> planTemplateDetails = new ArrayList<PlanTemplateDetail>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("templates");
            for (int i = 0; i < array.length(); i++) {
                PlanTemplateDetail planTemplateDetail = new PlanTemplateDetail();
                JSONObject object = array.getJSONObject(i);
                planTemplateDetail.setPlanTemplateDetailId(object.getString("id"));
                planTemplateDetail.setPointName(object.getString("pointName"));
                planTemplateDetail.setOfficeId(object.getString("officeId"));
                planTemplateDetail.setOfficeName(object.getString("officeName"));
                planTemplateDetail.setDuration(object.getString("duration"));
                planTemplateDetail.setPlanType(object.getString("planType"));
                planTemplateDetail.setExecutionTime(object.getString("executionTime"));
                planTemplateDetail.setPointType(pointType);
                planTemplateDetail.setPointId(object.getString("pointId"));
                if (!object.isNull("sort")) {
                    planTemplateDetail.setSort(object.getString("sort"));
                }
                planTemplateDetails.add(planTemplateDetail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return planTemplateDetails;
    }

    public List<UploadException> getExceptions(String json) {
        List<UploadException> exceptions = new ArrayList<UploadException>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                UploadException exception = new UploadException();
                JSONObject object = array.getJSONObject(i);
                exception.setResult(object.getString("result"));
                exception.setItemId(object.getString("item_id"));
                exception.setDescription(object.getString("memo"));
                exception.setPatrolTime(object.getString("patrolTime"));
                exception.setTreatmentAdvice(object.getString("handle_advice"));
                exception.setTime(object.getString("updateTime"));
                String picId = object.getString("pic_ids");
                if (picId.equals("null")) {
                    picId = "";
                }
                if (picId.endsWith(",")) {
                    picId = picId.substring(0, picId.length() - 1);
                }

                exception.setPicId(picId);
                exception.setFromWhere("2");
                exception.setIsUploadSuccess("1");
                exception.setWorkId("");
                exception.setPointName("");
                exception.setDeviceName("");
                exception.setDeviceCode("");
                exception.setWorkTypeId("");
                exception.setWorkTypeName("");
                exception.setUserId("");
                exception.setHistoryId("");
                exception.setCategory("4");
                exceptions.add(exception);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exceptions;
    }

}

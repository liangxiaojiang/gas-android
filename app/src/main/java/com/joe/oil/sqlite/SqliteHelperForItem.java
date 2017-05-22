package com.joe.oil.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.joe.oil.entity.CheckItem;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.DeviceTree;
import com.joe.oil.entity.Dict;
import com.joe.oil.entity.DictDetail;
import com.joe.oil.entity.Office;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.PlanTemplateDetail;
import com.joe.oil.entity.Tank;
import com.joe.oil.entity.User;
import com.joe.oil.entity.Well;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SqliteHelperForItem {

	private SQLiteDatabase dbHelper = null;

	public SqliteHelperForItem(Context context) {
		dbHelper = dbHandle.getInstance(context);
	}

	@SuppressLint("SimpleDateFormat")
	public String getUpdateTime() {
		ArrayList<CheckItem> checkItems = new ArrayList<CheckItem>();
		Cursor cursor = dbHelper.rawQuery("SELECT * FROM xj_item ORDER BY updateTime DESC limit 1", null);
		while (cursor.moveToNext()) {
			CheckItem checkItem = new CheckItem();
			checkItem.setId(cursor.getInt(cursor.getColumnIndex("id")));
			checkItem.setItemId(cursor.getString(cursor.getColumnIndex("itemId")));
			checkItem.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
			checkItems.add(checkItem);
		}
		if (checkItems.size() > 0) {
			return checkItems.get(0).getUpdateTime();
		} else {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = simpleDateFormat.format(new Date());
			return dateString;
		}
	}

	public List<Office> getDeviceOfOffice() {
		ArrayList<Office> offices = new ArrayList<Office>();
        Cursor cursor = dbHelper.rawQuery("select * from office where name in (select officeName from plan_template  " +
                "GROUP BY officeName) order by code asc", null);
        while (cursor.moveToNext()) {
			Office office = new Office();
			office.setName(cursor.getString(cursor.getColumnIndex("name")));
			office.setOfficeId(cursor.getString(cursor.getColumnIndex("officeId")));
			offices.add(office);
		}
		return offices;
	}


	public List<PlanDetail> getDetailsBetweenTwoTime() {
		ArrayList<PlanDetail> planDetails = new ArrayList<PlanDetail>();
		Cursor cursor = dbHelper
				.rawQuery(
						"SELECT * FROM plan_detail WHERE (select strftime('%Y-%m-%d %H:%M:%S','now', 'localtime')) < (select strftime('%Y-%m-%d %H:%M:%S', upTime)) and (select strftime('%Y-%m-%d %H:%M:%S','now', 'localtime')) > (select strftime('%Y-%m-%d %H:%M:%S',downTime)) and type = '1'",
						null);
		while (cursor.moveToNext()) {
			PlanDetail planDetail = new PlanDetail();
			planDetail.setChargerId(cursor.getString(cursor.getColumnIndex("chargerId")));
			planDetail.setCode(cursor.getString(cursor.getColumnIndex("code")));
			planDetail.setCreateById(cursor.getString(cursor.getColumnIndex("createById")));
			planDetail.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
			planDetail.setDownTime(cursor.getString(cursor.getColumnIndex("downTime")));
			planDetail.setDownValue(cursor.getString(cursor.getColumnIndex("downValue")));
			planDetail.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
			planDetail.setExceptionStatus(cursor.getString(cursor.getColumnIndex("exceptionStatus")));
			planDetail.setHandleAdvice(cursor.getString(cursor.getColumnIndex("handleAdvice")));
			planDetail.setHandleMemo(cursor.getString(cursor.getColumnIndex("handleMemo")));
			planDetail.setHandleMemoUpload(cursor.getString(cursor.getColumnIndex("handleMemoUpload")));
			planDetail.setItemId(cursor.getString(cursor.getColumnIndex("itemId")));
			planDetail.setItemName(cursor.getString(cursor.getColumnIndex("itemName")));
			planDetail.setItemType(cursor.getString(cursor.getColumnIndex("itemType")));
			planDetail.setItemUnit(cursor.getString(cursor.getColumnIndex("itemUtil")));
			planDetail.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
			planDetail.setOfficeId(cursor.getString(cursor.getColumnIndex("officeId")));
			planDetail.setOfficeName(cursor.getString(cursor.getColumnIndex("officeName")));
			planDetail.setPatrolDate(cursor.getString(cursor.getColumnIndex("patrolDate")));
			planDetail.setPatrolTime(cursor.getString(cursor.getColumnIndex("patrolTime")));
			planDetail.setPicId(cursor.getString(cursor.getColumnIndex("picId")));
			planDetail.setPlanDetailId(cursor.getString(cursor.getColumnIndex("planDetailId")));
			planDetail.setPlanId(cursor.getString(cursor.getColumnIndex("planId")));
			planDetail.setPlanType(cursor.getString(cursor.getColumnIndex("planType")));
			planDetail.setPointId(cursor.getInt(cursor.getColumnIndex("pointId")));
			planDetail.setPointName(cursor.getString(cursor.getColumnIndex("pointName")));
			planDetail.setResult(cursor.getString(cursor.getColumnIndex("result")));
			planDetail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			planDetail.setSort(cursor.getString(cursor.getColumnIndex("sort")));
			planDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
			planDetail.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
			planDetail.setUpTime(cursor.getString(cursor.getColumnIndex("upTime")));
			planDetail.setUpValue(cursor.getString(cursor.getColumnIndex("upValue")));
			planDetail.setVideoId(cursor.getString(cursor.getColumnIndex("videoId")));
			planDetail.setWorkId(cursor.getString(cursor.getColumnIndex("workId")));
			planDetail.setIsPicIdUpdate(cursor.getString(cursor.getColumnIndex("isPicIdUpdate")));
			planDetail.setTips(cursor.getString(cursor.getColumnIndex("tips")));
			planDetails.add(planDetail);
		}
		return planDetails;
	}

	public List<PlanDetail> getDetailsBetweenTwoTimeGroupByPointId(String type, String officeId) {
		ArrayList<PlanDetail> planDetails = new ArrayList<PlanDetail>();
		String sql = null;
		if (officeId.equals("")) {
			sql = "SELECT * FROM plan_detail WHERE (select strftime('%Y-%m-%d %H:%M:%S','now', 'localtime')) < (select strftime('%Y-%m-%d %H:%M:%S', upTime)) and (select strftime('%Y-%m-%d %H:%M:%S','now', 'localtime')) > (select strftime('%Y-%m-%d %H:%M:%S',downTime)) and type = '"
					+ type + "' group by pointName order by sort";
		}else {
			sql = "SELECT * FROM plan_detail WHERE (select strftime('%Y-%m-%d %H:%M:%S','now', 'localtime')) < (select strftime('%Y-%m-%d %H:%M:%S', upTime)) and (select strftime('%Y-%m-%d %H:%M:%S','now', 'localtime')) > (select strftime('%Y-%m-%d %H:%M:%S',downTime)) and type = '"
					+ type + "' and officeId = '" + officeId + "' group by pointName order by sort";
		}
		Cursor cursor = dbHelper
				.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			PlanDetail planDetail = new PlanDetail();
			planDetail.setChargerId(cursor.getString(cursor.getColumnIndex("chargerId")));
			planDetail.setCode(cursor.getString(cursor.getColumnIndex("code")));
			planDetail.setCreateById(cursor.getString(cursor.getColumnIndex("createById")));
			planDetail.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
			planDetail.setDownTime(cursor.getString(cursor.getColumnIndex("downTime")));
			planDetail.setDownValue(cursor.getString(cursor.getColumnIndex("downValue")));
			planDetail.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
			planDetail.setExceptionStatus(cursor.getString(cursor.getColumnIndex("exceptionStatus")));
			planDetail.setHandleAdvice(cursor.getString(cursor.getColumnIndex("handleAdvice")));
			planDetail.setHandleMemo(cursor.getString(cursor.getColumnIndex("handleMemo")));
			planDetail.setHandleMemoUpload(cursor.getString(cursor.getColumnIndex("handleMemoUpload")));
			planDetail.setItemId(cursor.getString(cursor.getColumnIndex("itemId")));
			planDetail.setItemName(cursor.getString(cursor.getColumnIndex("itemName")));
			planDetail.setItemType(cursor.getString(cursor.getColumnIndex("itemType")));
			planDetail.setItemUnit(cursor.getString(cursor.getColumnIndex("itemUnit")));
			planDetail.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
			planDetail.setOfficeId(cursor.getString(cursor.getColumnIndex("officeId")));
			planDetail.setOfficeName(cursor.getString(cursor.getColumnIndex("officeName")));
			planDetail.setPatrolDate(cursor.getString(cursor.getColumnIndex("patrolDate")));
			planDetail.setPatrolTime(cursor.getString(cursor.getColumnIndex("patrolTime")));
			planDetail.setPicId(cursor.getString(cursor.getColumnIndex("picId")));
			planDetail.setPlanDetailId(cursor.getString(cursor.getColumnIndex("planDetailId")));
			planDetail.setPlanId(cursor.getString(cursor.getColumnIndex("planId")));
			planDetail.setPlanType(cursor.getString(cursor.getColumnIndex("planType")));
			planDetail.setPointId(cursor.getInt(cursor.getColumnIndex("pointId")));
			planDetail.setPointName(cursor.getString(cursor.getColumnIndex("pointName")));
			planDetail.setResult(cursor.getString(cursor.getColumnIndex("result")));
			planDetail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			planDetail.setSort(cursor.getString(cursor.getColumnIndex("sort")));
			planDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
			planDetail.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
			planDetail.setUpTime(cursor.getString(cursor.getColumnIndex("upTime")));
			planDetail.setUpValue(cursor.getString(cursor.getColumnIndex("upValue")));
			planDetail.setVideoId(cursor.getString(cursor.getColumnIndex("videoId")));
			planDetail.setWorkId(cursor.getString(cursor.getColumnIndex("workId")));
			planDetail.setIsPicIdUpdate(cursor.getString(cursor.getColumnIndex("isPicIdUpdate")));
			planDetail.setTips(cursor.getString(cursor.getColumnIndex("tips")));
			planDetails.add(planDetail);
		}
		return planDetails;
	}

	public List<PlanDetail> getPlanDetailsOutOfTimeUNUpload() {
		ArrayList<PlanDetail> planDetails = new ArrayList<PlanDetail>();
		Cursor cursor = dbHelper.rawQuery(
				"SELECT * FROM plan_detail WHERE (select strftime('%Y-%m-%d %H:%M:%S', upTime)) < (select strftime('%Y-%m-%d %H:%M:%S','now', 'localtime')) and result != '' and status = '2'", null);
		while (cursor.moveToNext()) {
			PlanDetail planDetail = new PlanDetail();
			planDetail.setChargerId(cursor.getString(cursor.getColumnIndex("chargerId")));
			planDetail.setCode(cursor.getString(cursor.getColumnIndex("code")));
			planDetail.setCreateById(cursor.getString(cursor.getColumnIndex("createById")));
			planDetail.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
			planDetail.setDownTime(cursor.getString(cursor.getColumnIndex("downTime")));
			planDetail.setDownValue(cursor.getString(cursor.getColumnIndex("downValue")));
			planDetail.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
			planDetail.setExceptionStatus(cursor.getString(cursor.getColumnIndex("exceptionStatus")));
			planDetail.setHandleAdvice(cursor.getString(cursor.getColumnIndex("handleAdvice")));
			planDetail.setHandleMemo(cursor.getString(cursor.getColumnIndex("handleMemo")));
			planDetail.setHandleMemoUpload(cursor.getString(cursor.getColumnIndex("handleMemoUpload")));
			planDetail.setItemId(cursor.getString(cursor.getColumnIndex("itemId")));
			planDetail.setItemName(cursor.getString(cursor.getColumnIndex("itemName")));
			planDetail.setItemType(cursor.getString(cursor.getColumnIndex("itemType")));
			planDetail.setItemUnit(cursor.getString(cursor.getColumnIndex("itemUnit")));
			planDetail.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
			planDetail.setOfficeId(cursor.getString(cursor.getColumnIndex("officeId")));
			planDetail.setOfficeName(cursor.getString(cursor.getColumnIndex("officeName")));
			planDetail.setPatrolDate(cursor.getString(cursor.getColumnIndex("patrolDate")));
			planDetail.setPatrolTime(cursor.getString(cursor.getColumnIndex("patrolTime")));
			planDetail.setPicId(cursor.getString(cursor.getColumnIndex("picId")));
			planDetail.setPlanDetailId(cursor.getString(cursor.getColumnIndex("planDetailId")));
			planDetail.setPlanId(cursor.getString(cursor.getColumnIndex("planId")));
			planDetail.setPlanType(cursor.getString(cursor.getColumnIndex("planType")));
			planDetail.setPointId(cursor.getInt(cursor.getColumnIndex("pointId")));
			planDetail.setPointName(cursor.getString(cursor.getColumnIndex("pointName")));
			planDetail.setResult(cursor.getString(cursor.getColumnIndex("result")));
			planDetail.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			planDetail.setSort(cursor.getString(cursor.getColumnIndex("sort")));
			planDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
			planDetail.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
			planDetail.setUpTime(cursor.getString(cursor.getColumnIndex("upTime")));
			planDetail.setUpValue(cursor.getString(cursor.getColumnIndex("upValue")));
			planDetail.setVideoId(cursor.getString(cursor.getColumnIndex("videoId")));
			planDetail.setWorkId(cursor.getString(cursor.getColumnIndex("workId")));
			planDetail.setIsPicIdUpdate(cursor.getString(cursor.getColumnIndex("isPicIdUpdate")));
			planDetail.setTips(cursor.getString(cursor.getColumnIndex("tips")));
			planDetails.add(planDetail);
		}
		return planDetails;
	}

	public void insertPlanDetail(List<PlanDetail> planDetails) {
		// 开启事务
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < planDetails.size(); i++) {
				PlanDetail planDetail = planDetails.get(i);
				ContentValues cv = new ContentValues();
				cv.put("planDetailId", planDetail.getPlanDetailId());
				cv.put("itemId", planDetail.getItemId());
				cv.put("itemName", planDetail.getItemName());
				cv.put("pointId", planDetail.getPointId());
				cv.put("pointName", planDetail.getPointName());
				cv.put("chargerId", planDetail.getChargerId());
				cv.put("officeId", planDetail.getOfficeId());
				cv.put("officeName", planDetail.getOfficeName());
				cv.put("status", planDetail.getStatus());
				cv.put("result", planDetail.getResult());
				cv.put("memo", planDetail.getMemo());
				cv.put("sort", planDetail.getSort());
				cv.put("code", planDetail.getCode());
				cv.put("handleAdvice", planDetail.getHandleAdvice());
				cv.put("updateTime", planDetail.getUpdateTime());
				cv.put("picId", planDetail.getPicId());
				cv.put("upValue", planDetail.getUpValue());
				cv.put("tips", planDetail.getTips());
				cv.put("tag", planDetail.getTag());
				cv.put("downValue", planDetail.getDownValue());
				// cv.put("unit", planDetail.getUnit());
				cv.put("itemType", planDetail.getItemType());
				cv.put("videoId", planDetail.getVideoId());
				cv.put("itemUnit", planDetail.getItemUnit());
				cv.put("duration", planDetail.getDuration());
				cv.put("planType", planDetail.getPlanType());
				cv.put("upTime", planDetail.getUpTime());
				cv.put("downTime", planDetail.getDownTime());
				cv.put("type", planDetail.getType());
				cv.put("createTime", planDetail.getCreateTime());
				cv.put("patrolDate", planDetail.getPatrolDate());
				cv.put("patrolTime", planDetail.getPatrolTime());
				cv.put("createById", planDetail.getCreateById());
				cv.put("exceptionStatus", planDetail.getExceptionStatus());
				cv.put("handleMemo", planDetail.getHandleMemo());
				cv.put("handleMemoUpload", planDetail.getHandleMemoUpload());
				cv.put("workId", planDetail.getWorkId());
				cv.put("isPicIdUpdate", planDetail.getIsPicIdUpdate());
				cv.put("isRequiredToWrite", planDetail.getIsRequiredToWrite());
				dbHelper.insert("plan_detail", null, cv);
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			dbHelper.setTransactionSuccessful();

		}

		catch (Exception e) {
		} finally {
			// 结束事务
			dbHelper.endTransaction();
		}
	}

	public int insertItem(List<CheckItem> checkItems) {
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < checkItems.size(); i++) {
				CheckItem checkItem = checkItems.get(i);
				ContentValues cv = new ContentValues();
				cv.put("itemId", checkItem.getItemId());
				cv.put("parentIds", checkItem.getParentIds());
				cv.put("pointId", checkItem.getPointId());
				cv.put("pointType", checkItem.getPointType());
				cv.put("code", checkItem.getCode());
				cv.put("name", checkItem.getName());
				cv.put("alias", checkItem.getAlias());
				cv.put("type", checkItem.getType());
				cv.put("unit", checkItem.getUnit());
				cv.put("downValue", checkItem.getDownValue());
				cv.put("updateTime", checkItem.getUpdateTime());
				cv.put("upValue", checkItem.getUpValue());
				cv.put("tag", checkItem.getTag());
				cv.put("memo", checkItem.getMemo());

				dbHelper.insert("xj_item", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return checkItems.size();
	}

	public int insertUser(List<User> users) {
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				ContentValues cv = new ContentValues();
				cv.put("userId", user.getUserId());
				cv.put("loginName", user.getLoginName());
				cv.put("password", user.getPassword());
				cv.put("no", user.getNo());
				cv.put("name", user.getName());
				cv.put("mobile", user.getMobile());
				cv.put("phone", user.getPhone());
				cv.put("userType", user.getUserType());
				cv.put("host", user.getHost());
				cv.put("loginDate", user.getLoginDate());
				cv.put("roleId", user.getRoleId());
				cv.put("roleName", user.getRoleName());
				cv.put("roleEnname", user.getRoleEnname());
				cv.put("officeCode", user.getOfficeCode());
				cv.put("officeId", user.getOfficeId());
				cv.put("officeName", user.getOfficeName());
				cv.put("loginStatus", user.getLoginStatus());
				dbHelper.insert("user", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return users.size();
	}

	public int insertDevice(List<Device> devices) {
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < devices.size(); i++) {
				Device device = devices.get(i);
				ContentValues cv = new ContentValues();
				cv.put("deviceId", device.getDeviceId());
				cv.put("code", device.getCode());
				cv.put("name", device.getName());
				cv.put("lat", device.getLat());
				cv.put("lng", device.getLng());
				cv.put("memo", device.getMemo());
				cv.put("officeName", device.getOfficeName());
				cv.put("tchDate", device.getTchDate());
				cv.put("pch", device.getPch());
				cv.put("djNum", device.getDjNum());
				cv.put("ysjNum", device.getYsjNum());
				cv.put("flqNum", device.getFlqNum());
				cv.put("tshqNum", device.getTshqNum());
				cv.put("shzhqNum", device.getShzhqNum());
				cv.put("fdjNum", device.getFdjNum());
				dbHelper.insert("devices", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return devices.size();
	}

	public int insertWell(List<Well> wells) {
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < wells.size(); i++) {
				Well well = wells.get(i);
				ContentValues cv = new ContentValues();
				cv.put("wellId", well.getWellId());
				cv.put("code", well.getCode());
				cv.put("name", well.getName());
				cv.put("tchDate", well.getTchDate());
				cv.put("schcw", well.getSchcw());
				cv.put("wzll", well.getWzll());
				cv.put("tchqYy", well.getTchqYy());
				cv.put("tchqTy", well.getTchqTy());
				cv.put("lhqhl", well.getLhqhl());
				cv.put("pch", well.getPch());
				cv.put("lat", well.getLat());
				cv.put("lng", well.getLng());
				dbHelper.insert("well", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return wells.size();
	}

	public int insertDict(List<Dict> dicts) {
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < dicts.size(); i++) {
				Dict dict = dicts.get(i);
				ContentValues cv = new ContentValues();
				cv.put("dictId", dict.getDictId());
				cv.put("parentIds", dict.getParentIds());
				cv.put("label", dict.getLabel());
				cv.put("value", dict.getValue());
				cv.put("type", dict.getType());
				cv.put("grade", dict.getGrade());
				dbHelper.insert("dict", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return dicts.size();
	}

	public int insertDiviceTree(List<DeviceTree> deviceTrees) {
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < deviceTrees.size(); i++) {
				DeviceTree deviceTree = deviceTrees.get(i);
				ContentValues cv = new ContentValues();
				cv.put("treeId", deviceTree.getTreeId());
				cv.put("code", deviceTree.getCode());
				cv.put("officeId", deviceTree.getOfficeId());
				cv.put("type", deviceTree.getType());
				cv.put("text", deviceTree.getText());
				cv.put("parentId", deviceTree.getParentId());
				cv.put("cardType", deviceTree.getCardType());
				dbHelper.insert("device_tree", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return deviceTrees.size();
	}

	public int insertPlanTemplate(List<PlanTemplateDetail> planTemplateDetails) {
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < planTemplateDetails.size(); i++) {
				PlanTemplateDetail planTemplateDetail = planTemplateDetails.get(i);
				ContentValues cv = new ContentValues();
				cv.put("planTemplateDetailId", planTemplateDetail.getPlanTemplateDetailId());
				cv.put("officeId", planTemplateDetail.getOfficeId());
				cv.put("officeName", planTemplateDetail.getOfficeName());
				cv.put("pointId", planTemplateDetail.getPointId());
				cv.put("pointName", planTemplateDetail.getPointName());
				cv.put("pointType", planTemplateDetail.getPointType());
				cv.put("planType", planTemplateDetail.getPlanType());
				cv.put("executionTime", planTemplateDetail.getExecutionTime());
				cv.put("sort", planTemplateDetail.getSort());
				cv.put("duration", planTemplateDetail.getDuration());
				cv.put("patrolTime", planTemplateDetail.getPatrolTime());
				dbHelper.insert("plan_template", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return planTemplateDetails.size();
	}

	public int insertTank(List<Tank> tanks){
		dbHelper.beginTransaction();
		try {
		for (int i = 0; i < tanks.size(); i++) {
			Tank tank=tanks.get(i);
			ContentValues cv = new ContentValues();
			cv.put("name",tank.getName());
			cv.put("tankid",tank.getTankid());
			cv.put("number",tank.getNumber());
			cv.put("tankarea",tank.getTankarea());
			dbHelper.insert("tank", null, cv);
		}
		dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return tanks.size();
	}

	/**
	 * 保存任务模板到数据库中
	 * @param dictDetails
     * @return
     */
	public int insertTaskTemplate(List<DictDetail> dictDetails){
		dbHelper.beginTransaction();
		try {
			for (int i = 0; i < dictDetails.size(); i++) {
				DictDetail dictTask=dictDetails.get(i);
				ContentValues cv = new ContentValues();
				cv.put("title",dictTask.getTitle());
				cv.put("hint",dictTask.getHint());
				cv.put("isNull",dictTask.getIsNull());
				cv.put("type",dictTask.getType());
				cv.put("workTypeId",dictTask.getWorkTypeId());
				dbHelper.insert("dict_detail", null, cv);
			}
			dbHelper.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			dbHelper.endTransaction();
		}
		return dictDetails.size();
	}

	public boolean getPlanDetailByXjItemIdAndPatrolTime(String xjItemId, String patrolTime) {
		Cursor cursor = dbHelper.rawQuery("SELECT * FROM plan_detail WHERE code = (SELECT substr(code,1,14) FROM xj_item WHERE itemId =  '" + xjItemId
				+ "' LIMIT 1) AND result = (SELECT name FROM xj_item WHERE itemId =  '" + xjItemId + "' LIMIT 1) AND patrolTime = '" + patrolTime + "' LIMIT 1", null);
		Log.d("tag", "cursor.getCount(): " + cursor.getCount());
		if (cursor.getCount() == 0) {
			return false;
		} else {
			return true;
		}
	}
}

package com.joe.oil.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.joe.oil.parolmap.Constantss;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库管理和维护类
**/
public class SdCardDBHelper extends SQLiteOpenHelper{
	
	public static final String TAG = "SdCardDBHelper";
	/**
	 * 数据库名称
	**/
	public static String DATABASE_NAME = "PotralMap.db";
	
	/**
	 * 数据库版本
	**/
	public static int DATABASE_VERSION = 1;
	
	private static SdCardDBHelper _dbHelper = null;
	
	public static SdCardDBHelper getInstance(){
		return _dbHelper;
	}	
		
	/**
	 * 构造函数
	 * 
	 * @param    context 上下文环境
	**/
	public SdCardDBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		_dbHelper = this;
	}
	
	public void clearDatabase() {
		this.getWritableDatabase().execSQL("delete from user");
		Map<String,String> params = new HashMap<String,String>();
		params.put("user", "");
		params.put("password", "");
		Constantss.updateParams(params);
	}
	
	/**
	 * 清空数据库
	 *
	 * @param    db
	**/
	 
	/**
	 * 创建数据库时触发，创建离线存储所需要的数据库表
	 *
	 * @param    db
	**/
	@Override
	public void onCreate(SQLiteDatabase db) {
		 Log.e(TAG, "开始创建数据库表");
		try{
			//创建用户表(user)
			 db.execSQL("create table if not exists user" +
			 		"(_id integer primary key autoincrement,name varchar(20),password varchar(20),role varchar(10),privilege varchar(100),realname varchar(30))");
						
			//创建系统参数表，
			db.execSQL("create table if not exists params" +
				 		"(_id integer primary key autoincrement,name varchar(20),value varchar)");
			
			//服务器IP
			ContentValues serverIP = new ContentValues();
			serverIP.put("name", "serverIP");
			serverIP.put("value", "211.149.209.186");
			db.insert("params", null, serverIP);
			
			//gps设置
			ContentValues gps = new ContentValues();
			gps.put("name", "gps");
			gps.put("value", "10");
			db.insert("params", null, gps);
			
			//在线底图加载控制
			ContentValues onlineMap = new ContentValues();
			onlineMap.put("name", "online_map");
			onlineMap.put("value", "不显示");
			db.insert("params", null, onlineMap);
			
			//单位
			ContentValues unit_m = new ContentValues();
			unit_m.put("name", "unit_m");
			unit_m.put("value", "米");
			db.insert("params", null, unit_m);
			
			//如果是第一次创建数据库表，所有的选项都设置为需要更新。
			UpdateStatus.setAllUpdateStatus(true);
			
		}
		catch(SQLException se){
			se.printStackTrace();
		}		
	}
	
	/** 更新数据库时触发
	 *
	 * @param    db
	 * @param    oldVersion
	 * @param    newVersion
	**/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("ALTER TABLE person ADD COLUMN other STRING"); 
	 }
}
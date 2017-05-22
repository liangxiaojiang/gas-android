package com.joe.oil.parolmap;

import android.content.ContentValues;
import android.database.Cursor;


import com.joe.oil.sqlite.SdCardDBHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Constantss {
	
	public static String SERVER_IP = "211.149.209.186";

	public static String POI_URL=":8080/VehiclePosition/general/poi/queryName?";
	public static String ROUTELIST_URL = ":6080/arcgis/rest/services/GAS/creatline/FeatureServer/0/query";

	public static String ROUTE_URL = ":6080/arcgis/rest/services/GAS/routeServer/NAServer/route";

    public static String ONLINEMAP_URL = "http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetGray/MapServer";

	public static int selectedSegmentID=-1;
	/**
	 * 获取系统参数
	 */
	public static Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		
		Cursor cursor = SdCardDBHelper.getInstance().getReadableDatabase()
				.query("params", null, null, null, null, null, null);
		
		int cursorCount = cursor.getCount();
		if (cursorCount >0 )
		{
		    cursor.moveToFirst();
		    for (int i = 0; i < cursor.getCount(); i++) 
		    {
		    	String name = cursor.getString(1);
				
				String value = cursor.getString(2);
			    params.put(name, value);
				cursor.moveToNext();
		    }
		}

		cursor.close();
		return params;
	}
	
	/**
	 * 更新系统参数
	 */
	public static void updateParams(Map<String, String> params) {
		for (@SuppressWarnings("rawtypes")
		Entry entry : params.entrySet()) {

			Object key = entry.getKey();
			ContentValues cv = new ContentValues();
			cv.put("value", params.get(key));
			String whereClause = "name=?";
			String[] whereArgs = { (String) key };
			SdCardDBHelper.getInstance().getWritableDatabase()
					.update("params", cv, whereClause, whereArgs);
		}
	}


	
}

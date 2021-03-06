package com.joe.oil.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络连接管理类
**/
public class NetworkManager {

	/**
	 * 判断是否有网络连接
	**/
	public static boolean isNetworkConnected(Context context){
		if(context != null){
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
			if (mNetworkInfo != null) {  
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**
	 * 判断WIFI网络是否可用
	**/
	
	public static boolean isWifiConnected(Context context){
		if(context != null){
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
			NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
			if (mNetworkInfo != null) {  
				return mNetworkInfo.isAvailable(); 
			}
		}
		return false;
	}
	
	/**
	 * 判断MOBILE网络是否可用
	**/
	public boolean isMobileConnected(Context context){
		if(context != null){
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
			NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
			if (mNetworkInfo != null) {  
				return mNetworkInfo.isAvailable(); 
			} 	
		}
		return false;
	} 
	
	/**
	 * 获取当前网络连接的类型信息
	**/
	public static int getConnectedType(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {  
	            return mNetworkInfo.getType();  
	        }  
	    }  
	    return -1;
	}
	
}

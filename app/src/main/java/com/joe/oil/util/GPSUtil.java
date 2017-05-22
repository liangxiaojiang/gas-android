package com.joe.oil.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.joe.oil.R;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.eventbus.EventBusManager;
import com.joe.oil.eventbus.EventCode;

public class GPSUtil {
	
	/**
	 * 判断是否存在GPS设备
	 * */
	public static String TAG = "GPSUtil";
	
	//高德定位

	public static AMapLocationClient locationClient = null;
	public static AMapLocationClientOption locationOption = new AMapLocationClientOption();

	public static MapView mapView;
	public static GraphicsLayer gLayer;
	public static Context context;
	
	public static String flag="gps";

	public static Point current_gps_point;



	public static boolean hasGPSDevice()
	{
		boolean has = false;
		try
		{
		    final LocationManager mgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		    if ( mgr == null ) return false;

		    has=mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			has = false;
		}
		return has;
	}

	/**
	 * 初始化定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public static void initLocation(){

		//初始化client
		locationClient = new AMapLocationClient(context);
		//设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);
	}

	/**
	 * 默认的定位参数
	 *
	 */
	private static AMapLocationClientOption getDefaultOption(){
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是ture
		mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
//		mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		return mOption;
	}
	/**
	 * 定位监听
	 */
	private static AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation location) {
			if (null != location) {


				//解析定位结果
				Double geoLat = location.getLatitude();
				Double geoLng = location.getLongitude();


				//Map<String,String> params_gpsAcc = Constantss.getParams();

				//final float gpsAcc=Float.parseFloat(params_gpsAcc.get("gps"));

				SpatialReference sr = SpatialReference.create(4326);

				if(geoLat!=0.0 && geoLng!=0.0) {
					Point ptMap = getPoint(geoLng, geoLat);
					SimpleMarkerSymbol gps_mark = new SimpleMarkerSymbol(Color.BLUE, 12, SimpleMarkerSymbol.STYLE.CIRCLE);

					if (flag.equals("gps")) {
						//Drawable image = context.getResources().getDrawable(R.drawable.route_loc);

						//Symbol symbol = new PictureMarkerSymbol(image);
						Graphic g = new Graphic(ptMap, gps_mark);

						gLayer.removeAll();
						gLayer.addGraphic(g);

						mapView.centerAt(ptMap, false);
						//mapView.setScale(12);
					}
					else if(flag.equals("gps_stop"))
					{
						Graphic g = new Graphic(ptMap, gps_mark);

						gLayer.removeAll();
						gLayer.addGraphic(g);

						mapView.centerAt(ptMap, false);

						current_gps_point=new Point(geoLat,geoLng);

						EventBusManager.dispatchEvent(this, EventCode.GET_GPS_DONE, current_gps_point);

						flag="gps";
						stopLocation();
					}

//			    String cityCode = "";
//				String desc = "";
//				Bundle locBundle = location.getExtras();
//
//				if (locBundle != null) {
//					cityCode = locBundle.getString("citycode");
//					desc = locBundle.getString("desc");
//				}
//				String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
//						+ "\n精    度    :" + location.getAccuracy() + "米"
//						+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
//						+ new Date(location.getTime()).toLocaleString() + "\n城市编码:"
//						+ cityCode + "\n位置描述:" + desc + "\n省:"
//						+ location.getProvince() + "\n市:" + location.getCity()
//						+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
//						.getAdCode());
				}
			} else {
					Toast.makeText(context, "定位失败！", Toast.LENGTH_SHORT).show();
				}
		}
	};

	// 根据控件的选择，重新设置定位参数
	public static void resetOption(String strInterval) {
		// 设置是否需要显示地址信息
		//locationOption.setNeedAddress(true);
		/**
		 * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
		 * 注意：只有在高精度模式下的单次定位有效，其他方式无效
		 */
		//locationOption.setGpsFirst(cbGpsFirst.isChecked());
		// 设置是否开启缓存
		//locationOption.setLocationCacheEnable(cbCacheAble.isChecked());
		//设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
		//locationOption.setOnceLocationLatest(cbOnceLastest.isChecked());


		if (!TextUtils.isEmpty(strInterval)) {
			try{
				// 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
				locationOption.setInterval(Long.valueOf(strInterval)*1000);
			}catch(Throwable e){
				e.printStackTrace();
			}
		}

		/*String strTimeout = "5000";
		if(!TextUtils.isEmpty(strTimeout)){
			try{
				// 设置网络请求超时时间
				locationOption.setHttpTimeOut(Long.valueOf(strTimeout));
			}catch(Throwable e){
				e.printStackTrace();
			}
		}*/

		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}

	/**
	 * 开始定位
	 *
	 */
	public static void startLocation(){
		//根据控件的选择，重新设置定位参数
		//resetOption();
		// 设置定位参数
		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}

	/**
	 * 停止定位
	 *
	 */
	public static void stopLocation(){
		// 停止定位
		locationClient.stopLocation();
		gLayer.removeAll();
	}

	/**
	 * 销毁定位
	 *
	 */
	private void destroyLocation(){
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}


	public static void locator()
	{
		LocationManager mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_FINE);
		cri.setAltitudeRequired(false);   
		cri.setBearingRequired(false);   
		cri.setCostAllowed(true);   
		cri.setPowerRequirement(Criteria.POWER_LOW);   

        //Map<String,String> params_gpsAcc = Constantss.getParams();
        
     	//final float gpsAcc=Float.parseFloat(params_gpsAcc.get("gps"));
		
		try
		{
			Toast.makeText(context, "开始定位！", Toast.LENGTH_SHORT).show();
			
			if(mLocationManager == null)
				mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

			String best = mLocationManager.getBestProvider(cri, true);
			mLocationManager.requestLocationUpdates(best, 2000, 10, new LocationListener(){
				@Override
				public void onLocationChanged(Location loc) {
					
				if(loc.getAccuracy()<300)
				{
					double lat,lon;
					try
					{
						lat = loc.getLatitude();
						lon = loc.getLongitude();

					}
					catch(Exception e)
					{
						lat = 0;
						lon = 0;
						e.printStackTrace();
					}
					
					Point ptMap = getPoint(lon,lat);
					
					//mapView.setResolution(0.597165777664889);
					 
					if(flag.equals("gps"))
					{
						Drawable image = context.getResources().getDrawable(R.drawable.gps_loc);
						
						Symbol symbol = new PictureMarkerSymbol(image);
						Graphic g = new Graphic(ptMap, symbol);
						
						gLayer.removeAll();
						gLayer.addGraphic(g);

						mapView.centerAt(ptMap,false);
						//mapView.setScale(12);
					}
					
				}
				else
				{
					Toast.makeText(context, "当前GPS定位精度较低！"+loc.getAccuracy(), Toast.LENGTH_SHORT).show();
				}
					
				}

				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub
					
				}});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//showMessageBox("Failed to locate");
		}
	}
	
	 /*public static void uploadDataToAGS()
	    {
	       if(NetworkManager.isNetworkConnected(context)){//联网情况下
	  				Thread thread = new Thread(new Runnable(){
	  	 				@Override
	  	 				public void run() {
	  	 					//query(url_query);
	  	 					if(flag.equals("gps_trajectory"))
	  	 					{
	  	 						final String url="http://"+Constantss.SERVER_IP+":8399"+Constantss.SERVER_ADDRESS+"/5/addFeatures";
	  	 						uploadInfos(url);
	  	 					}
	  	 						
	  	 				}});
	  	 			thread.start();
	  	  	}
	  	  	else{	
	  	  	    ProgressDialogUtils.dismissProgressDialog();
	  	  		Toast.makeText(context, "请检查网络设置！", Toast.LENGTH_SHORT).show();
	    	
	  	  	}
	      }
	  public static void uploadInfos(String url){
	    	 //创建一个http客户端
	        HttpClient client=new DefaultHttpClient();
	        //创建一个POST请求  
	        HttpPost request=new HttpPost(url);
	         //设置HTTP POST请求参数必须用NameValuePair  
	        List<NameValuePair> params = new ArrayList<NameValuePair>();  
	            
	        HashMap<String, Object> attr=new HashMap<String, Object>();
	        Map<String,String> params_cardId = Constantss.getParams();
	        
	        String card_id=String.valueOf(params_cardId.get("cardid"));
	        
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
			//SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	    Date date = new Date();	
	 	    //System.currentTimeMillis()+TimeZone.getDefault().getRawOffset()
	        try
	        {
	     	 attr.put("UPDATETIME" , date); 
	 	     attr.put("FACILITY" ,  p[0]);
	 	     attr.put("USERNAME" ,  p[1]);
	 	     attr.put("DEVICEID" ,  card_id);
	 	     attr.put("REMARK" ,  Double.toString(Math.round(polyline.calculateLength2D())).substring(0,Double.toString(Math.round(polyline.calculateLength2D())).length()-2));
	 	   
	 	      SpatialReference sr=SpatialReference.create(4326);
	 	      Graphic gr=new Graphic(GeometryEngine.project(polyline, mapView.getSpatialReference(),sr),null,attr);
	 	         
	 	   	  String attr_JSON=Graphic.toJson(gr);
	 	   	  
	 	   	  String geom_JSON="\"geometry\":"+GeometryEngine.geometryToJson(sr, gr.getGeometry());
	 	   	  
	 	   	  String result_JSON="[{"+attr_JSON.substring(1,attr_JSON.length()-1)+","+geom_JSON+"}]";
	 	   	     
	 	   	  params.add(new BasicNameValuePair("f", "json"));//format设置成json
	 	      params.add(new BasicNameValuePair("Features",result_JSON));
	 	      
	            //设置http Post请求参数 
	            HttpEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
	            request.setEntity(entity); 
	            HttpResponse response=client.execute(request);
	            
	            if(response.getStatusLine().getStatusCode()==200){//如果状态码为200,就是正常返回

	                handler.sendEmptyMessage(0);
	            }
	        } catch (ClientProtocolException e) {
	            e.printStackTrace(); 
	        } catch (IOException e) {
	     	   e.printStackTrace();
	        } catch (Exception e) {
	       	 e.printStackTrace();
	        }
	    }
	  
	  private static Handler handler = new Handler(){
			 public void handleMessage(Message msg) { 
				 ProgressDialogUtils.dismissProgressDialog();
				 
				 if(flag.equals("gps_trajectory"))
				 {
					 Toast.makeText(context, "渠道轨迹上传成功！", Toast.LENGTH_SHORT).show();
					 stopAmap();
				 }
				 
			 }
		};
		*/
	private static Point getPoint(double lon, double lat)
	{
		Point p = new Point(lon,lat);
		SpatialReference sr = SpatialReference.create(4326);
		Point ptMap = (Point) GeometryEngine.project(p, sr,mapView.getSpatialReference());
		return ptMap;
	}

}

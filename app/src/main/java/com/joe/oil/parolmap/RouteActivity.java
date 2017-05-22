package com.joe.oil.parolmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;
import com.joe.oil.R;
import com.joe.oil.activity.BaseActivity;
import com.joe.oil.eventbus.EventBusManager;
import com.joe.oil.eventbus.EventCode;
import com.joe.oil.util.ClearEditText;
import com.joe.oil.util.GPSUtil;
import com.joe.oil.util.NetworkManager;
import com.joe.oil.util.ProgressDialogUtils;
import com.joe.oil.util.RefreshListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteActivity extends BaseActivity
{
	private final static int ROUTE_CODE=6;

	public final static int MAIN_CODE=0;
	private static final int REFRESH_COMPLETE = 0X110;

	private static final int GET_COMPLETE = 0X112;
	private static final int GET_DATA =0X113;
	private static final int REFERSH_DONE =0X114;

	private int index_ref=0;
	private int len_ref=20;
	private int count_ref=0;

	private List<String> mStrRefresh=new ArrayList<String>();


	private ImageView navbar_backView;
	private RefreshListView lst;
	private ListView lst_route;
	private ImageView btn_routeadd;
	private ImageView route_add_two;
	private ImageView route_add_three;
	private ImageView route_add_four;

	private ImageView route_change;
	private ImageView route_change2;
	private ImageView route_change3;
	private ImageView route_change4;

	private RadioGroup routeGroup=null;
	private RadioButton route_get_RadioButton=null;
	private RadioButton route_list_RadioButton=null;

	private RadioGroup routeGroup_mode=null;
	private RadioButton route_dis_RadioButton=null;
	private RadioButton route_time_RadioButton=null;

	private LinearLayout route_id_search_tool;
	private Button route_id_btn;

	private SimpleAdapter hdAdapter;
	private MyAdapter hdAdapter_route;
	List<Map<String, Object>> route_list = new ArrayList<Map<String, Object>>();

	private ListView lst_routeDirs;
	private SimpleAdapter routeDir_Adapter;

	RouteTask mRouteTask = null;
	RouteResult mResults = null;
	ArrayList<String> curDirections = null;

	ArrayList<Point> curStops = new ArrayList<Point>(6);
	Exception mException = null;

	final SpatialReference wm = SpatialReference.create(102100);
	final SpatialReference egs = SpatialReference.create(4326);

	private int flag=0;
	private int flag2=0;
	private int flag_map=0;

	private int flag_route=0;

	private int flag_mode=0;

	private PopupWindow popupOpitionMenu;

	Route curRoute = null;
	String routeSummary = null;
	Point mLocation = null;

	private ClearEditText route_sn;

	private List<Map<String, Object>> hd_list;

	SimpleLineSymbol segmentHider = new SimpleLineSymbol(Color.BLUE, 5);
	// Symbol used to highlight route segments
	SimpleLineSymbol segmentShower = new SimpleLineSymbol(Color.RED, 5);


	public MapView map = null;
	public GraphicsLayer routeLayer,hiddenSegmentsLayer;

	final Handler mHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateRouteUI();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.route_layout);

		map=NavigationActivity.mMapView;
		routeLayer=NavigationActivity.drawLayer;
		//hiddenSegmentsLayer=MainActivity.hiddenSegmentsLayer;
		mRouteTask = NavigationActivity.mRouteTask;

		EventBusManager.addListener(this, EventCode.GET_GPS_DONE, "get_GPS_Complete");

		EventBusManager.addListener(this, EventCode.CLEAR_MAP, "clear_Map");

		View viewPopMenu = getLayoutInflater().inflate(R.layout.popwindow_menu,null);
		popupOpitionMenu = new PopupWindow(viewPopMenu,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupOpitionMenu.setFocusable(true);
		popupOpitionMenu.setBackgroundDrawable(new BitmapDrawable());
		popupOpitionMenu.setAnimationStyle(R.style.popanimation);

		lst_route=(ListView)findViewById(R.id.lst_route);

		Map<String, Object> route_map = new HashMap<String, Object>();

		route_map.put("r_start", String.valueOf("从"));
		route_map.put("r_title", String.valueOf("输入起点"));


		route_list.add(route_map);

		Map<String, Object> route_map2 = new HashMap<String, Object>();
		route_map2.put("r_start", String.valueOf("到"));
		route_map2.put("r_title", String.valueOf("输入终点"));

		route_list.add(route_map2);

		hdAdapter_route = new MyAdapter(this);

		lst_route.setAdapter(hdAdapter_route);

		lst_route.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {

				//HashMap<String, String> loc_map = (HashMap<String, String>) lst_route.getItemAtPosition(arg2);
				//Toast.makeText(RouteActivity.this, String.valueOf(arg2), Toast.LENGTH_SHORT).show();

				Intent intent = new Intent();
				intent.setClass(RouteActivity.this, POISearchActivity.class);
				intent.putExtra("pt_id", String.valueOf(arg2));
				//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent,ROUTE_CODE);

			}

		});


		btn_routeadd=(ImageView)findViewById(R.id.route_add_first);
		route_add_two=(ImageView)findViewById(R.id.route_add_two);
		route_add_three=(ImageView)findViewById(R.id.route_add_three);
		route_add_four=(ImageView)findViewById(R.id.route_add_four);

		lst = (RefreshListView) findViewById(R.id.lst_hd);
		lst.setAdapter(hdAdapter);
		lst.setOnRefreshListener(new RefreshListView.OnRefreshListener() {

			@Override
			public void onLoadingMore() {

				if (index_ref==0)
				{
					requestDataServer(false);
					lst.completeRefresh();
				}
				else
				{
					requestDataServer(true);
				}
			}
		});

		lst.setVisibility(View.GONE);

		lst.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {

				HashMap<String,String> map=(HashMap<String,String>)lst.getItemAtPosition(arg2);


				NavigationActivity mainMapAc=new NavigationActivity();

				mainMapAc.showRoute(map.get("hd_id"));

				Intent intent = new Intent(RouteActivity.this, NavigationActivity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}

		});

		routeGroup=(RadioGroup)findViewById(R.id.route_RG);
		route_get_RadioButton=(RadioButton)findViewById(R.id.route_get);
		route_list_RadioButton=(RadioButton)findViewById(R.id.route_list);

		route_id_search_tool=(LinearLayout)findViewById(R.id.route_id_search);
		route_id_btn=(Button)findViewById(R.id.btn_search_route);
		route_sn=(ClearEditText)findViewById(R.id.route_sn);

		route_id_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String route_id=route_sn.getText().toString();
				if(route_id.equals("") ||  route_id.isEmpty())
				{
					index_ref=0;
					mStrRefresh.clear();

					getDataFromNet("1=1","getIds");

				}
				else {
					String wh="ROUTE_NO like '%"+route_id+"%'";
					index_ref=0;
					lst.completeRefresh();
					getDataFromNet(wh,"query");

				}
			}
		});

		routeGroup_mode=(RadioGroup)findViewById(R.id.route_mode);

		route_dis_RadioButton=(RadioButton)findViewById(R.id.route_distance);
		route_time_RadioButton=(RadioButton)findViewById(R.id.route_time);

		routeGroup_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == route_dis_RadioButton.getId()) {
					flag_mode=1;
				}
				else if (checkedId==route_time_RadioButton.getId())
				{
					flag_mode=0;
				}
			}
		});

		routeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == route_get_RadioButton.getId()) {
					lst.setVisibility(View.GONE);
					btn_routeadd.setVisibility(View.VISIBLE);

					route_list.clear();
					Map<String, Object> route_map1 = new HashMap<String, Object>();
					route_map1.put("r_start", String.valueOf("从"));
					route_map1.put("r_title", String.valueOf("输入起点"));
					route_list.add(route_map1);

					Map<String, Object> route_map2 = new HashMap<String, Object>();
					route_map2.put("r_start", String.valueOf("到"));
					route_map2.put("r_title", String.valueOf("输入终点"));
					route_list.add(route_map2);

					hdAdapter_route.notifyDataSetChanged();

					btn_routeadd.setVisibility(View.VISIBLE);
					route_add_two.setVisibility(View.GONE);
					route_add_three.setVisibility(View.GONE);
					route_add_four.setVisibility(View.GONE);

					route_id_search_tool.setVisibility(View.GONE);

					curStops.add(0,new Point(0,0));
					curStops.add(1,new Point(0,0));
					curStops.add(2,new Point(0,0));
					curStops.add(3,new Point(0,0));
					curStops.add(4,new Point(0,0));


				}
				else if (checkedId==route_list_RadioButton.getId())
				{
					//getDataFromNet();
					route_id_search_tool.setVisibility(View.VISIBLE);

					lst.setVisibility(View.VISIBLE);
					btn_routeadd.setVisibility(View.GONE);
					route_add_two.setVisibility(View.GONE);
					route_add_three.setVisibility(View.GONE);
					route_add_four.setVisibility(View.GONE);

				}
			}
		});

		navbar_backView=(ImageView)findViewById(R.id.navbar_back);
		navbar_backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(RouteActivity.this, NavigationActivity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);

			}
		});

		curStops.add(0,new Point(0,0));
		curStops.add(1,new Point(0,0));
		curStops.add(2,new Point(0,0));
		curStops.add(3,new Point(0,0));
		curStops.add(4,new Point(0,0));

	}
	private void requestDataServer(final boolean load) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				//休息3秒
				SystemClock.sleep(3000);
				if(load){//下拉
					String wh_rf="";

					for(int i=len_ref*index_ref; i<len_ref+len_ref*index_ref && i<mStrRefresh.size(); i++){
						wh_rf+=mStrRefresh.get(i)+",";
						count_ref=i;
					}

					if(len_ref*index_ref>mStrRefresh.size()-1)
					{
						//lst.completeRefresh();

						Message message = Message.obtain();
						message.obj = index_ref;
						message.what = REFERSH_DONE;
						handler.sendMessage(message);
					}
					else
					{
						final String url_query="http://"+Constantss.SERVER_IP+Constantss.ROUTELIST_URL;

						queryRouteInfos(url_query,wh_rf.substring(0,wh_rf.length()-1),"refresh");
					}

					index_ref++;


				}else{//上刷
					//list.add(0, "更新到最新的数据");

				}
				//更新UI
				//Refresh_Handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private Handler Refresh_Handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			lst.setAdapter(hdAdapter);
			hdAdapter.notifyDataSetChanged();

			lst.completeRefresh();

		}
	};

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){

			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if(inputMethodManager.isActive()){
				inputMethodManager.hideSoftInputFromWindow(RouteActivity.this.getCurrentFocus().getWindowToken(), 0);
			}

			String route_id=route_sn.getText().toString();
			if(route_id.equals(""))
			{
				Toast.makeText(RouteActivity.this, "请输入路单号！", Toast.LENGTH_SHORT).show();
			}
			else {
				String wh="ROUTE_NO like '%"+route_id+"%'";
				index_ref=0;

				lst.completeRefresh();
				getDataFromNet(wh,"query");
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){

			Intent intent = new Intent(RouteActivity.this, NavigationActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);

			return true;
		}
		return false;
	}
	public class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter(Context c) {
			this.inflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return route_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/**
		 * 设置数据源与行View关联 设置行中个组件的事件响应 返回设置好的View
		 */
		@Override
		public View getView(final int position, View arg1, final ViewGroup arg2) {
			// 取得要显示的行View
			View myView = inflater.inflate(R.layout.list_item_route, null);

			ImageView button = (ImageView) myView.findViewById(R.id.btn_route_loc);
			TextView txt1 = (TextView) myView.findViewById(R.id.r_start);
			TextView txt2 = (TextView) myView.findViewById(R.id.r_title);

			txt1.setText((String) route_list.get(position).get("r_start"));
			txt2.setText((String) route_list.get(position).get("r_title"));

			// 添加事件响应
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//Toast.makeText(RouteActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
					flag_map=position;
					popupOpitionMenu.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
				}
			});
			return myView;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Bundle bundle=data.getExtras();
		String str=bundle.getString("value");

		if (requestCode==ROUTE_CODE)
		{
			if (resultCode==POISearchActivity.ROUTE_CODE)
			{
				//Toast.makeText(RouteActivity.this, str, Toast.LENGTH_LONG).show();

				String[] stops = str.split(",");

				curStops.set(Integer.valueOf(stops[0]),new Point(Double.valueOf(stops[2]),Double.valueOf(stops[3])));

				Map<String, Object> route_map_new = new HashMap<String, Object>();
				if(Integer.valueOf(stops[0])==0)
				{
					route_map_new.put("r_start", String.valueOf("从"));
				}
				else if(Integer.valueOf(stops[0])==1 && hdAdapter_route.getCount()==2)
				{
					route_map_new.put("r_start", String.valueOf("到"));
				}
				else if(Integer.valueOf(stops[0])==4 && hdAdapter_route.getCount()==5)
				{
					route_map_new.put("r_start", String.valueOf("到"));
				}
				else{
					route_map_new.put("r_start", String.valueOf(""));
				}
				route_map_new.put("r_title", stops[1]);

				route_list.set(Integer.valueOf(stops[0]),route_map_new);

				hdAdapter_route.notifyDataSetChanged();
			}

		}
		else if(requestCode==MAIN_CODE){

			if (resultCode==NavigationActivity.MAIN_CODE)
			{
				String[] stops = str.split(",");

				curStops.set(Integer.valueOf(stops[0]),new Point(Double.valueOf(stops[1]),Double.valueOf(stops[2])));

				Map<String, Object> route_map_new = new HashMap<String, Object>();
				if(Integer.valueOf(stops[0])==0)
				{
					route_map_new.put("r_start", String.valueOf("从"));
				}
				else if(Integer.valueOf(stops[0])==1 && hdAdapter_route.getCount()==2)
				{
					route_map_new.put("r_start", String.valueOf("到"));
				}
				else if(Integer.valueOf(stops[0])==4 && hdAdapter_route.getCount()==5)
				{
					route_map_new.put("r_start", String.valueOf("到"));
				}
				else{
					route_map_new.put("r_start", String.valueOf(""));
				}
				route_map_new.put("r_title", "地图取点");

				route_list.set(Integer.valueOf(stops[0]),route_map_new);

				hdAdapter_route.notifyDataSetChanged();
			}
		}
	}

	private void closePopWindow(MeasureWindow popupWindow) {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_route_loc:
				//upOrDown = "up";

				//popupOpitionMenu.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
				break;
			case R.id.btn_pop_menu_cancle:
				popupOpitionMenu.dismiss();
				break;
			case R.id.btn_pop_menu_choose_on_map:
			{
				popupOpitionMenu.dismiss();

				//EventBusManager.dispatchEvent(this, EventCode.SET_TAP_TYPE, "map_stop");

				Intent intent = new Intent();
				intent.setClass(RouteActivity.this, MapTapActivity.class);
				intent.putExtra("list_id", String.valueOf(flag_map));
				//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent,MAIN_CODE);

				break;
			}
			case R.id.btn_pop_menu_user_current_loc:
			{
				if(GPSUtil.hasGPSDevice())
				{
					String strInterval=Constantss.getParams().get("gps");
					if(Long.valueOf(strInterval)>0)
					{
						GPSUtil.flag="gps_stop";
						GPSUtil.resetOption(strInterval);
						GPSUtil.startLocation();

						//EventBusManager.dispatchEvent(this, EventCode.SET_GPS_ICON, "");
						popupOpitionMenu.dismiss();
						ProgressDialogUtils.showProgressDialog(RouteActivity.this, "正在定位中，请稍后！");
					}
				}
				else{

					Toast.makeText(RouteActivity.this, "请开启GPS！", Toast.LENGTH_LONG).show();
				}

				break;
			}
			case R.id.btn_route:
			{
				flag_route=0;
				getRouteFromNet(curStops);
				break;
			}
			case R.id.route_add_first:
			{
				//route_list.clear();
				Map<String, Object> route_map_end = new HashMap<String, Object>();
				route_map_end.put("r_start", String.valueOf(""));
				route_map_end.put("r_title",String.valueOf(route_list.get(1).get("r_title")));

				Map<String, Object> route_map_new = new HashMap<String, Object>();
				route_map_new.put("r_start", String.valueOf(""));
				route_map_new.put("r_title", "输入途经点");

				route_list.set(1,route_map_new);

				route_list.add(route_map_end);

				curStops.set(2,curStops.get(1));
				curStops.set(1,new Point(0,0));


				hdAdapter_route.notifyDataSetChanged();


				route_add_three.setImageResource(R.drawable.route_add);
				flag=0;

				btn_routeadd.setVisibility(View.GONE);
				route_add_two.setVisibility(View.VISIBLE);
				route_add_three.setVisibility(View.VISIBLE);

				route_add_four.setVisibility(View.GONE);

				//route_updown();
				break;
			}
			case R.id.route_add_two:
			{
				int count=hdAdapter_route.getCount();

				//route_list.clear();

				if(count==3)
				{
					Map<String, Object> route_map_end = new HashMap<String, Object>();
					route_map_end.put("r_start", String.valueOf("到"));
					route_map_end.put("r_title",String.valueOf(route_list.get(2).get("r_title")));

					route_list.remove(2);
					route_list.set(1,route_map_end);

					curStops.set(1,curStops.get(2));

					hdAdapter_route.notifyDataSetChanged();

					btn_routeadd.setVisibility(View.VISIBLE);
					route_add_two.setVisibility(View.GONE);
					route_add_three.setVisibility(View.GONE);

					route_add_four.setVisibility(View.GONE);
				}else if(count==4)
				{

					Map<String, Object> route_map_end = new HashMap<String, Object>();
					route_map_end.put("r_start", String.valueOf(""));
					route_map_end.put("r_title",String.valueOf(route_list.get(3).get("r_title")));


					route_list.remove(1);
					route_list.set(2,route_map_end);
					hdAdapter_route.notifyDataSetChanged();

					curStops.set(1,curStops.get(2));
					curStops.set(2,curStops.get(3));

					route_add_three.setImageResource(R.drawable.route_add);

					btn_routeadd.setVisibility(View.GONE);
					route_add_two.setVisibility(View.VISIBLE);
					route_add_three.setVisibility(View.VISIBLE);
					flag2=0;
					flag=0;

					route_add_four.setVisibility(View.GONE);

				}
				else if(count==5)
				{
					Map<String, Object> route_map_end = new HashMap<String, Object>();
					route_map_end.put("r_start", String.valueOf(""));
					route_map_end.put("r_title",String.valueOf(route_list.get(4).get("r_title")));

					route_list.remove(1);
					route_list.set(3,route_map_end);

					curStops.set(1,curStops.get(2));
					curStops.set(2,curStops.get(3));
					curStops.set(3,curStops.get(4));

					hdAdapter_route.notifyDataSetChanged();

					route_add_three.setImageResource(R.drawable.route_del);
					route_add_four.setImageResource(R.drawable.route_add);
					flag=0;
					flag2=1;
					btn_routeadd.setVisibility(View.GONE);
					route_add_two.setVisibility(View.VISIBLE);
					route_add_three.setVisibility(View.VISIBLE);
					route_add_four.setVisibility(View.VISIBLE);
				}

				//route_updown();

				break;
			}
			case R.id.route_add_three:
			{
				int count=hdAdapter_route.getCount();

				//route_list.clear();

				if(flag2==1)
				{
					if(count==5)
					{
						Map<String, Object> route_map_end = new HashMap<String, Object>();
						route_map_end.put("r_start", String.valueOf(""));
						route_map_end.put("r_title",String.valueOf(route_list.get(4).get("r_title")));

						route_list.remove(2);
						route_list.set(3,route_map_end);

						curStops.set(2,curStops.get(3));
						curStops.set(3,curStops.get(4));

						hdAdapter_route.notifyDataSetChanged();

						route_add_three.setImageResource(R.drawable.route_del);
						route_add_four.setImageResource(R.drawable.route_add);
						flag=0;

						flag2=1;

						btn_routeadd.setVisibility(View.GONE);
						route_add_two.setVisibility(View.VISIBLE);
						route_add_three.setVisibility(View.VISIBLE);
						route_add_four.setVisibility(View.VISIBLE);
					}
					else if(count==4)
					{

						Map<String, Object> route_map_end = new HashMap<String, Object>();
						route_map_end.put("r_start", String.valueOf(""));
						route_map_end.put("r_title",String.valueOf(route_list.get(3).get("r_title")));

						route_list.remove(2);
						route_list.set(2,route_map_end);

						curStops.set(2,curStops.get(3));

						hdAdapter_route.notifyDataSetChanged();

						route_add_three.setImageResource(R.drawable.route_add);
						route_add_four.setImageResource(R.drawable.route_add);
						flag=0;

						flag2=0;

						btn_routeadd.setVisibility(View.GONE);
						route_add_two.setVisibility(View.VISIBLE);
						route_add_three.setVisibility(View.VISIBLE);
						route_add_four.setVisibility(View.GONE);
					}
					else
					{

						Map<String, Object> route_map_end = new HashMap<String, Object>();
						route_map_end.put("r_start", String.valueOf(""));
						route_map_end.put("r_title",String.valueOf(route_list.get(3).get("r_title")));

						route_list.remove(3);
						route_list.set(2,route_map_end);

						curStops.set(1,curStops.get(2));
						curStops.set(2,curStops.get(3));

						hdAdapter_route.notifyDataSetChanged();


						route_add_three.setImageResource(R.drawable.route_add);
						flag2=0;

						btn_routeadd.setVisibility(View.GONE);
						route_add_two.setVisibility(View.VISIBLE);
						route_add_three.setVisibility(View.VISIBLE);

						route_add_four.setVisibility(View.GONE);
					}

				}
				else
				{
					Map<String, Object> route_map_end = new HashMap<String, Object>();
					route_map_end.put("r_start", String.valueOf(""));
					route_map_end.put("r_title",String.valueOf(route_list.get(2).get("r_title")));

					Map<String, Object> route_map_new = new HashMap<String, Object>();
					route_map_new.put("r_start", String.valueOf(""));
					route_map_new.put("r_title", "输入途经点");

					route_list.set(2,route_map_new);
					route_list.add(route_map_end);


					curStops.set(3,curStops.get(2));
					curStops.set(2,new Point(0,0));

					hdAdapter_route.notifyDataSetChanged();

					route_add_three.setImageResource(R.drawable.route_del);
					route_add_four.setImageResource(R.drawable.route_add);
					flag2=1;
					flag=0;
					btn_routeadd.setVisibility(View.GONE);
					route_add_two.setVisibility(View.VISIBLE);
					route_add_three.setVisibility(View.VISIBLE);
					route_add_four.setVisibility(View.VISIBLE);

				}

				//route_updown();
				break;

			}
			case R.id.route_add_four:{
				//route_list.clear();

				if(flag==1)
				{
					//route_list.clear();

					Map<String, Object> route_map_end = new HashMap<String, Object>();
					route_map_end.put("r_start", String.valueOf(""));
					route_map_end.put("r_title",String.valueOf(route_list.get(4).get("r_title")));

					route_list.set(3,route_map_end);
					route_list.remove(4);

					curStops.set(3,curStops.get(4));

					hdAdapter_route.notifyDataSetChanged();

					route_add_three.setImageResource(R.drawable.route_del);
					route_add_four.setImageResource(R.drawable.route_add);
					flag=0;
					btn_routeadd.setVisibility(View.GONE);
					route_add_two.setVisibility(View.VISIBLE);
					route_add_three.setVisibility(View.VISIBLE);
					route_add_four.setVisibility(View.VISIBLE);
				}
				else {

					Map<String, Object> route_map_end = new HashMap<String, Object>();
					route_map_end.put("r_start", String.valueOf("到"));
					route_map_end.put("r_title",String.valueOf(route_list.get(3).get("r_title")));

					Map<String, Object> route_map = new HashMap<String, Object>();
					route_map.put("r_start", String.valueOf(""));
					route_map.put("r_title", String.valueOf("输入途经点"));

					route_list.set(3,route_map);

					route_list.add(route_map_end);

					curStops.set(4,curStops.get(3));
					curStops.set(3,new Point(0,0));

					hdAdapter_route.notifyDataSetChanged();

					route_add_three.setImageResource(R.drawable.route_del);
					route_add_four.setImageResource(R.drawable.route_del);
					flag=1;
					flag2=1;

					btn_routeadd.setVisibility(View.GONE);
					route_add_two.setVisibility(View.VISIBLE);
					route_add_three.setVisibility(View.VISIBLE);
					route_add_four.setVisibility(View.VISIBLE);
				}
				//route_updown();

				break;
			}
		}

		//Toast.makeText(RouteActivity.this, String.valueOf(route_list.size()), Toast.LENGTH_SHORT).show();
	}
	public void queryRouteInfos(String url,String wh,String flag){

		HttpClient client=new DefaultHttpClient();

		HttpPost request=new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();


		params.add(new BasicNameValuePair("f", "json"));
		params.add(new BasicNameValuePair("orderByFields","CREATETIME Desc"));

		if(flag.equals("query"))
		{
			params.add(new BasicNameValuePair("outFields","*"));
			params.add(new BasicNameValuePair("where",wh));
		}
		else if (flag.equals("getIds"))
		{
			params.add(new BasicNameValuePair("returnIdsOnly","true"));
			params.add(new BasicNameValuePair("where","1=1"));

		}
		else
		{
			params.add(new BasicNameValuePair("outFields","*"));
			params.add(new BasicNameValuePair("objectIds",wh));
		}


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		//df.setRoundingMode(BigDecimal.ROUND_HALF_UP);
		SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.RED, 2, SimpleLineSymbol.STYLE.SOLID);

		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			request.setEntity(entity);

			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,2000);//连接时间
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,2000);//数据传输时间

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(),"utf-8");

				if (result != null && result.length() > 0) {

					JSONObject json = new JSONObject(result);

					if (flag.equals("getIds"))
					{
						JSONArray ids_json = (JSONArray) (json.get("objectIds"));
						for(int i=0;i<ids_json.length();i++)
						{
							mStrRefresh.add(String.valueOf(ids_json.getString(i)));
						}

						Message message = Message.obtain();
						message.obj = flag;
						message.what = GET_COMPLETE;
						handler.sendMessage(message);

					}
					else
					{

						JSONArray features = (JSONArray) (json.get("features"));

						for (int i = 0; i < features.length(); i++) {
							JSONObject feature = (JSONObject) features.get(i);
							JSONObject attributes = feature.getJSONObject("attributes");

							Double dt = Double.valueOf(attributes.getString("TIME"))/60;
							BigDecimal bd_dt = new BigDecimal(dt);
							bd_dt=bd_dt.setScale(1,BigDecimal.ROUND_HALF_UP);

							Double ds = Double.valueOf(attributes.getString("ROUTE_LENGTH"));
							BigDecimal bd_ds = new BigDecimal(ds);
							bd_ds=bd_ds.setScale(2,BigDecimal.ROUND_HALF_UP);
							//Date date = new Date(lng);

							Map<String, Object> hd_map = new HashMap<String, Object>();

							hd_map.put("hd_id", String.valueOf(attributes.getString("OBJECTID")));
							hd_map.put("hd_name", String.valueOf(attributes.getString("NAME")));
							hd_map.put("hd_time", String.valueOf(bd_dt+ "小时"));
							//hd_map.put("hd_date", sdf.format(date));
							hd_map.put("hd_date", String.valueOf(attributes.getString("CREATETIME")));
							hd_map.put("hd_dist", String.valueOf(bd_ds + "千米"));
							hd_list.add(hd_map);

						}

						hdAdapter = new SimpleAdapter(this, hd_list, R.layout.list_item_layout,
								new String[]{"hd_date", "hd_name", "hd_dist","hd_time"},
								new int[]{R.id.hd_date, R.id.hd_name, R.id.hd_dist,R.id.hd_time});

						Message message = Message.obtain();
						message.obj = flag;
						message.what = GET_DATA;
						handler.sendMessage(message);
					}

				}
			}

		}catch (ConnectTimeoutException e){

			Toast.makeText(RouteActivity.this, "超时！", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {

			e.printStackTrace();
			//进行处理操作
		} catch (IOException e) {

			e.printStackTrace();
			//进行处理操作
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		/*curStops.set(0,new Point(0,0));
		curStops.set(1,new Point(0,0));
		curStops.set(2,new Point(0,0));
		curStops.set(3,new Point(0,0));
		curStops.set(4,new Point(0,0));*/

		EventBusManager.addListener(this, EventCode.GET_GPS_DONE, "get_GPS_Complete");

		EventBusManager.addListener(this, EventCode.CLEAR_MAP, "clear_Map");
	}

	public void clear_Map(Object parameters){

		hd_list.clear();
		route_sn.setText("");

	}

	public void get_GPS_Complete(Object parameters){
		if(parameters == null){
			Toast.makeText(RouteActivity.this, "定位失败！", Toast.LENGTH_SHORT).show();
		}
		else{
			Point current_pt=(Point) parameters;
			curStops.set(flag_map,current_pt);

			Map<String, Object> route_map_new = new HashMap<String, Object>();
			if(flag_map==0)
			{
				route_map_new.put("r_start", String.valueOf("从"));
			}
			else if(flag_map==1 && hdAdapter_route.getCount()==2)
			{
				route_map_new.put("r_start", String.valueOf("到"));
			}
			else if(flag_map==4 && hdAdapter_route.getCount()==5)
			{
				route_map_new.put("r_start", String.valueOf("到"));
			}
			else{
				route_map_new.put("r_start", String.valueOf(""));
			}
			route_map_new.put("r_title", "输入起点");

			route_list.set(flag_map,route_map_new);

			hdAdapter_route.notifyDataSetChanged();

			ProgressDialogUtils.dismissProgressDialog();
		}
	}
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {

			switch (msg.what)
			{
				case REFRESH_COMPLETE:

					lst.setAdapter(hdAdapter);
					hdAdapter.notifyDataSetChanged();
					break;
				case GET_COMPLETE:
					String wh1="";

					for(int i=0; i<len_ref; i++){
						wh1+=mStrRefresh.get(i)+",";
					}

					index_ref++;
					getDataFromNet(wh1,"refresh");
					break;
				case GET_DATA:

					lst.setAdapter(hdAdapter);
					lst.setSelection(lst.getCount()-5);
					hdAdapter.notifyDataSetChanged();
					lst.completeRefresh();

					if(msg.obj.equals("query"))
					{
						ProgressDialogUtils.dismissProgressDialog();
						Toast.makeText(RouteActivity.this, "获取线路信息成功！", Toast.LENGTH_SHORT).show();

					}

					if(index_ref==1)
					{
						ProgressDialogUtils.dismissProgressDialog();
						Toast.makeText(RouteActivity.this, "获取线路信息成功！", Toast.LENGTH_SHORT).show();

					}

					if(lst.getCount()>0)
					{

					}
					else
					{
						Toast.makeText(RouteActivity.this, "请确认路单号后重试！", Toast.LENGTH_SHORT).show();
					}

					break;
				case REFERSH_DONE:
					Toast.makeText(RouteActivity.this, "没有数据啦！", Toast.LENGTH_SHORT).show();

					lst.completeRefresh();

					break;

			}


			if (msg.obj!=null && msg.obj.toString().equals("e-server"))
			{
				ProgressDialogUtils.dismissProgressDialog();
				Toast.makeText(RouteActivity.this, "请确认服务器地址后重试！", Toast.LENGTH_SHORT).show();
			}

		}
	};

	public void getDataFromNet(final String where, final String flag)
	{
		if(index_ref==0)
		{
			ProgressDialogUtils.showProgressDialog(RouteActivity.this, "获取线路信息，请稍后！");
		}

		if(NetworkManager.isNetworkConnected(this)){//联网情况下
			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {

					final String url_query="http://"+Constantss.SERVER_IP+Constantss.ROUTELIST_URL;

					if(index_ref==0)
					{
						hd_list = new ArrayList<Map<String, Object>>();
					}
					queryRouteInfos(url_query,where,flag);

				}});
			thread.start();
		}
		else{
			ProgressDialogUtils.dismissProgressDialog();
			Toast.makeText(this, "请检查网络或服务设置！", Toast.LENGTH_SHORT).show();

		}
	}

	private void updateRouteUI() {

		ProgressDialogUtils.dismissProgressDialog();

		EventBusManager.dispatchEvent(this, EventCode.HIDE_ROUTE_NAME, "");

		if (mResults == null) {
			//Toast.makeText(this, mException.toString(), Toast.LENGTH_SHORT).show();
			flag_route=flag_route+1;
			if(flag_route<2)
				getRouteFromNet(curStops);
			else
				Toast.makeText(this, "未找到相关路线!", Toast.LENGTH_SHORT).show();
			//return;
		}
		else {
			curRoute = mResults.getRoutes().get(0);

			//hiddenSegmentsLayer.removeAll();

			SimpleLineSymbol routeSymbol = new SimpleLineSymbol(Color.BLUE, 2);

			List<Map<String, Object>> route_list = new ArrayList<Map<String, Object>>();

			/*for (RouteDirection rd : curRoute.getRoutingDirections()) {
				HashMap<String, Object> attribs = new HashMap<String, Object>();
				attribs.put("text", rd.getText());
				attribs.put("time", Double.valueOf(rd.getMinutes()));
				attribs.put("length", Double.valueOf(rd.getLength()));
				curDirections.add(String.format(
						"%s%nTime: %.1f minutes, Length: %.1f miles", rd.getText(),
						rd.getMinutes(), rd.getLength()));
				Graphic routeGraphic = new Graphic(rd.getGeometry(), segmentHider, attribs);
				//hiddenSegmentsLayer.addGraphic(routeGraphic);

				//route_list.add(attribs);
			}*/

			routeDir_Adapter = new SimpleAdapter(this, route_list, R.layout.list_item_routedirs_layout,
					new String[]{"text"}, new int[]{R.id.route_name});


			//lst_routeDirs.setAdapter(routeDir_Adapter);
			///routeDir_Adapter.notifyDataSetChanged();

			// Reset the selected segment
			//selectedSegmentID = -1;
			routeLayer.removeAll();
			// Add the full route graphic and destination graphic to the routeLayer
			Graphic routeGraphic = new Graphic(curRoute.getRouteGraphic()
					.getGeometry(), routeSymbol);

			Graphic endGraphic = new Graphic(
					((Polyline) routeGraphic.getGeometry()).getPoint(((Polyline) routeGraphic
							.getGeometry()).getPointCount() - 1), NavigationActivity.symbol2);

			//Point pt_start = new Point(12119004.2908, 4055420.2348);//12119004.2908, 4055420.2348

			//Point pt_start = new Point(109.39774, 39.16901);

			//Point pt_start_wb=(Point) GeometryEngine.project(pt_start,egs,wm);

			Graphic startGraphic = new Graphic(
					((Polyline) routeGraphic.getGeometry()).getPoint(0), NavigationActivity.symbol1);

			Drawable image_stop= this.getResources().getDrawable(R.drawable.stop1);
			PictureMarkerSymbol route_node = new PictureMarkerSymbol(image_stop);

			route_node.setOffsetX(0);
			route_node.setOffsetY(14);

			//SimpleMarkerSymbol ppp=new SimpleMarkerSymbol(Color.RED,5, SimpleMarkerSymbol.STYLE.CIRCLE);

			//Point pt_node = new Point(12128004.2908, 4058420.2348);//12119004.2908, 4055420.2348


			//Point pt_node = new Point(109.22607,38.96438);
			//Point pt_node_wb=(Point) GeometryEngine.project(pt_node,egs,wm);

			//Graphic nodeGraphic = new Graphic(pt_node_wb,route_node);
			//Graphic nodeGraphic2 = new Graphic(pt_node_wb,ppp);

			routeLayer.addGraphics(new Graphic[] { routeGraphic,startGraphic,endGraphic});

			for (int k=1;k<hdAdapter_route.getCount()-1;k++)
			{
				Point p_node =curStops.get(k);
				Point p_node_wb=(Point) GeometryEngine.project(p_node,egs,wm);
				Graphic nodeGraphic = new Graphic(p_node_wb,route_node);
				routeLayer.addGraphic(nodeGraphic);
			}

			routeSummary = String.format(
					"%s%nTotal time: %.1f minutes, length: %.1f miles",
					curRoute.getRouteName(), curRoute.getTotalMinutes(),
					curRoute.getTotalMiles());
			//directionsLabel.setText(routeSummary);
			//Toast.makeText(this,routeSummary,Toast.LENGTH_LONG).show();
			// Zoom to the extent of the entire route with a padding
			map.setExtent(curRoute.getEnvelope(), 250);

			Intent intent = new Intent(RouteActivity.this, NavigationActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}

	}

	public void getRouteFromNet(final ArrayList stops)
	{
		int flag_stops=0;
		for (int j=0;j<hdAdapter_route.getCount();j++)
		{
			Point pt = (Point) stops.get(j);
			if(pt.getX()==0)
			{
				flag_stops=1;
			}
		}
		if(flag_stops==0) {

			ProgressDialogUtils.showProgressDialog(RouteActivity.this, "正在进行路线规划，请稍后！");

			routeLayer.removeAll();
			//hiddenSegmentsLayer.removeAll();
			curDirections = new ArrayList<String>();


			if (NetworkManager.isNetworkConnected(this)) {//联网情况下
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							// Start building up routing parameters
							RouteParameters rp = mRouteTask.retrieveDefaultRouteTaskParameters();

							rp.setUseHierarchy(true);
							if(flag_mode==0)
							{
								rp.setImpedanceAttributeName("DistanceTime");
							}
							else
							{
								rp.setImpedanceAttributeName("Distance");
							}

							NAFeaturesAsFeature rfaf = new NAFeaturesAsFeature();

							//Point p1 = new Point(12119004.2908, 4055420.2348);
							//Point pt_node = new Point(12128004.2908, 4058420.2348);
							//Point p2 = new Point(12127709.0451, 4063066.9306000024);

							for (int i = 0; i < hdAdapter_route.getCount(); i++) {
								Point pt = (Point) stops.get(i);
								StopGraphic stop_pt = new StopGraphic(pt);
								rfaf.addFeature(stop_pt);
							}
						/*Point p1 = new Point(109.39774, 39.16901);
						Point pt_node = new Point(109.22607,38.96438);
						Point p2 = new Point(109.03931,38.71582);

						//Point pt1 = (Point) GeometryEngine.project(p1,egs,wm);
						//Point pt2 = (Point) GeometryEngine.project(p2,egs,wm);

						StopGraphic point1 = new StopGraphic(p1);
						StopGraphic point3 = new StopGraphic(pt_node);

						StopGraphic point2 = new StopGraphic(p2);
						rfaf.setFeatures(new Graphic[] { point1,point3,point2 });*/
							rfaf.setCompressedRequest(true);
							rp.setStops(rfaf);
							rp.setOutSpatialReference(wm);


							mResults = mRouteTask.solve(rp);
							mHandler.post(mUpdateResults);
						} catch (Exception e) {
							mException = e;
							mHandler.post(mUpdateResults);
						}

					}
				});
				thread.start();
			} else {
				ProgressDialogUtils.dismissProgressDialog();
				Toast.makeText(this, "请检查网络或服务设置！", Toast.LENGTH_SHORT).show();

			}
		}
		else
		{
			Toast.makeText(this, "请检查途经点信息！", Toast.LENGTH_SHORT).show();
		}
	}

}
package com.joe.oil.parolmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.joe.oil.R;
import com.joe.oil.activity.BaseActivity;
import com.joe.oil.eventbus.EventBusManager;
import com.joe.oil.eventbus.EventCode;
import com.joe.oil.util.ClearEditText;
import com.joe.oil.util.NetworkManager;
import com.joe.oil.util.ProgressDialogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POISearchActivity extends BaseActivity
{
	private final static String TAG="POISearchActivity";
	public final static int ROUTE_CODE=6;

	private ImageView navbar_backView;

	private RadioGroup poiGroup=null;
	private RadioButton poi_hospital_RadioButton=null;
	private RadioButton poi_hotel_RadioButton=null;

	private RadioButton poi_wc_RadioButton=null;
	private RadioButton poi_gas_RadioButton=null;
	private RadioButton poi_shop_RadioButton=null;

	private Button btn_search;

	private ListView lst_poi;
	private String ptId_Str;
	private SimpleAdapter poi_Adapter;

	private ClearEditText poi_keywords;
	private MapView map;

	private GraphicsLayer poiLayer;

	private List<Map<String, Object>> poi_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_search);

		map= NavigationActivity.mMapView;

		poiLayer=NavigationActivity.poiLayer;

		poiGroup=(RadioGroup)findViewById(R.id.poi_rg);
		poi_hospital_RadioButton=(RadioButton)findViewById(R.id.rb_hospital);
		poi_hotel_RadioButton=(RadioButton)findViewById(R.id.rb_hotel);

		poi_shop_RadioButton=(RadioButton)findViewById(R.id.rb_shop);
		poi_gas_RadioButton=(RadioButton)findViewById(R.id.rb_gas);
		poi_wc_RadioButton=(RadioButton)findViewById(R.id.rb_wc);


		EventBusManager.addListener(this, EventCode.CLEAR_MAP, "clear_Map");

		ptId_Str = getIntent().getStringExtra("pt_id");

		poiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == poi_hospital_RadioButton.getId()) {

					getDataFromNet("TypeName='综合医院'");
				}
				else if(checkedId==poi_hotel_RadioButton.getId())
				{
					getDataFromNet("TypeName='宾馆酒店'");
				}
				else if(checkedId==poi_wc_RadioButton.getId())
				{
					getDataFromNet("TypeName='公共厕所'");
				}else if(checkedId==poi_shop_RadioButton.getId())
				{
					getDataFromNet("TypeName='超市'");
				}
				else if(checkedId==poi_gas_RadioButton.getId())
				{
					getDataFromNet("TypeName='加油站'");
				}
			}
		});

		poi_keywords=(ClearEditText)findViewById(R.id.keyValue);
		poi_keywords.setText(getIntent().getStringExtra("route"));
//		String wh="苏东";
//		//wh= "Name_CHN like '%"+poi_keywords.getText().toString()+"%'";
//		wh= poi_keywords.getText().toString();
//		getDataFromNet(wh);
		btn_search=(Button)findViewById(R.id.btn_search);
		btn_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (poi_keywords.getText().toString().equals("")) {
					Toast.makeText(POISearchActivity.this, "请输入POI关键字！", Toast.LENGTH_SHORT).show();
				} else {

					String wh="苏东";

					//wh= "Name_CHN like '%"+poi_keywords.getText().toString()+"%'";
					wh= poi_keywords.getText().toString();
					getDataFromNet(wh);
				}

			}
		});

		navbar_backView=(ImageView)findViewById(R.id.navbar_back);
		navbar_backView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//finish();
				if(ptId_Str.equals("00"))
				{
					Intent intent = new Intent(POISearchActivity.this, NavigationActivity.class);
					//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
				else
				{
					Intent intent = new Intent(POISearchActivity.this, RouteActivity.class);
					//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);

				}

			}
		});

		lst_poi = (ListView) findViewById(R.id.lst_poi);
		lst_poi.setAdapter(poi_Adapter);

		lst_poi.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {

				HashMap<String,String> poi_map=(HashMap<String,String>)lst_poi.getItemAtPosition(arg2);

				PictureMarkerSymbol poi_sel_Symbol = new PictureMarkerSymbol(
						map.getContext(), getResources().getDrawable(
						R.drawable.icon_pin_red));
				PictureMarkerSymbol poi_Symbol = new PictureMarkerSymbol(
						map.getContext(), getResources().getDrawable(
						R.drawable.icon_pin_blue));

				if(ptId_Str.equals("00")) {

					TextView tv = new TextView(POISearchActivity.this);
					tv.setTextColor(Color.rgb(255, 0, 0));
					String name_chn = String.valueOf(poi_map.get("poi_name"));
					tv.setText(name_chn);

					Point location = new Point(Double.valueOf(poi_map.get("poi_lon")),Double.valueOf(poi_map.get("poi_lat")));
					SpatialReference sr = SpatialReference.create(4326);
					Point location_web = (Point) GeometryEngine.project(location, sr,map.getSpatialReference());

					Graphic gr_poi=new Graphic(location_web,poi_Symbol);
					poiLayer.removeAll();

					poiLayer.addGraphic(gr_poi);
					map.getCallout().show(location_web,tv);

					map.setExtent(location_web, 50);

					/*for (int index : poiLayer.getGraphicIDs()) {
						Graphic g = poiLayer.getGraphic(index);
						if (poi_map.get("poi_name").contains((String) g.getAttributeValue("poi_name"))) {

							poiLayer.updateGraphic(Constantss.selectedSegmentID, poi_Symbol);
							poiLayer.updateGraphic(index, poi_sel_Symbol);

							Constantss.selectedSegmentID = index;

							TextView tv = new TextView(POISearchActivity.this);
							tv.setTextColor(Color.rgb(255, 0, 0));
							String name_chn = String.valueOf(g.getAttributeValue("poi_name"));
							tv.setText(name_chn);

							Point location = (Point) g.getGeometry();
							map.getCallout().show(location,tv);

							map.setExtent(
									poiLayer.getGraphic(
											index).getGeometry(), 50);
							break;
						}
					}*/

					Intent intent = new Intent(POISearchActivity.this, NavigationActivity.class);
					//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
				else
				{
					Intent intent=new Intent();
					intent.putExtra("value", ptId_Str+","+poi_map.get("poi_name")+","+poi_map.get("poi_lon")+","+poi_map.get("poi_lat"));

					setResult(ROUTE_CODE, intent);

					finish();
				}

			}

		});
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){

			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if(inputMethodManager.isActive()){
				inputMethodManager.hideSoftInputFromWindow(POISearchActivity.this.getCurrentFocus().getWindowToken(), 0);
			}
			if (poi_keywords.getText().toString().equals("")) {
				Toast.makeText(POISearchActivity.this, "请输入POI关键字！", Toast.LENGTH_SHORT).show();
			} else {

				String wh="苏东";

				//wh= "Name_CHN like '%"+poi_keywords.getText().toString()+"%'";
				wh= poi_keywords.getText().toString();

				getDataFromNet(wh);
			}

			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(ptId_Str.equals("00"))
			{
				Intent intent = new Intent(POISearchActivity.this, NavigationActivity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
			else
			{
				Intent intent = new Intent(POISearchActivity.this, RouteActivity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);

			}

			return true;
		}
		return false;
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		ptId_Str = getIntent().getStringExtra("pt_id");

		EventBusManager.addListener(this, EventCode.CLEAR_MAP, "clear_Map");

	}
	public void queryPoiInfos(String url,String where) {

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("name", where));

		try {
				HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
				request.setEntity(entity);
				HttpResponse response = client.execute(request);

				if (response.getStatusLine().getStatusCode() == 200) {

					String result = EntityUtils.toString(response.getEntity());
					if (result != null && result.length() > 0) {

						JSONArray features = new JSONArray(result);

						poi_list = new ArrayList<Map<String, Object>>();

						poi_list.clear();

						for (int i = 0; i < features.length(); i++) {
							JSONObject feature = (JSONObject) features.get(i);

							if (feature.getString("lon").equals("0"))
							{

							}
							else {

								Map<String, Object> poi_map = new HashMap<String, Object>();

								poi_map.put("poi_name", String.valueOf(feature.getString("name")));
								poi_map.put("poi_lon", String.valueOf(feature.getString("lon")));
								poi_map.put("poi_lat", String.valueOf(feature.getString("lat")));

								poi_list.add(poi_map);
							}

						}

						//mMapView.setExtent(getExt(points));

						poi_Adapter = new SimpleAdapter(this, poi_list, R.layout.list_item_poi_layout,
								new String[]{"poi_name","poi_lon","poi_lat"},
								new int[]{R.id.poi_name,R.id.poi_lon,R.id.poi_lat});


						if(features.length()>0)
						{
							Message message=new Message();
							message.what=1;
							message.obj=features;
							handler.sendMessage(message);
						}
						else
						{
							Message message=new Message();
							message.what=0;
							message.obj="未命中";
							handler.sendMessage(message);
						}

					}

				}

			}
			catch(ClientProtocolException e){
				e.printStackTrace();
				//进行处理操作
			}catch(IOException e){
				//进行处理操作
			}catch(JSONException e){
				e.printStackTrace();
			}


	}

	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {

			ProgressDialogUtils.dismissProgressDialog();

			EventBusManager.dispatchEvent(this, EventCode.SET_TAP_TYPE, "poi");
			EventBusManager.dispatchEvent(this, EventCode.HIDE_ROUTE_NAME, "");

			lst_poi.setAdapter(poi_Adapter);
			poi_Adapter.notifyDataSetChanged();

			if(msg.obj!=null && msg.obj.toString().equals("未命中"))
			{
				Toast.makeText(POISearchActivity.this, "没有命中相关信息，请改变条件再试！", Toast.LENGTH_SHORT).show();
			}
			else if(msg.obj!=null) {
				if (ptId_Str.equals("00")) {
					JSONArray features = (JSONArray) (msg.obj);
					poiLayer.removeAll();

					ArrayList<Point> points=new ArrayList<Point>();
					PictureMarkerSymbol poi_Symbol = new PictureMarkerSymbol(
							map.getContext(), getResources().getDrawable(
							R.drawable.icon_pin_blue));

					//SimpleMarkerSymbol poi_default=new SimpleMarkerSymbol(Color.BLUE, 6, SimpleMarkerSymbol.STYLE.CIRCLE);

					try {
						for (int i = 0; i < features.length(); i++) {
							JSONObject feature = (JSONObject) features.get(i);
							Point pt = new Point(Double.valueOf((feature.getString("lon"))), Double.valueOf((feature.getString("lat"))));

							if (feature.getString("lon").equals("0"))
							{

							}
							else {
								SpatialReference sr = SpatialReference.create(4326);
								Point ptMap = (Point) GeometryEngine.project(pt, sr,map.getSpatialReference());
								Map<String, Object> poi_map = new HashMap<String, Object>();

								poi_map.put("poi_name", String.valueOf(feature.getString("name")));
								poi_map.put("poi_lon", String.valueOf(feature.getString("lon")));
								poi_map.put("poi_lat", String.valueOf(feature.getString("lat")));

								points.add(ptMap);
								Graphic g=null;

								g = new Graphic(ptMap, poi_Symbol, poi_map);

								//poiLayer.addGraphic(g);
							}

						}

						double xmax=0,xmin=999999999,ymax=0,ymin=99999999;

						for(int i=0;i<points.size();i++)
						{
							if(points.get(i).getX()<xmin)
								xmin=points.get(i).getX();

							if(points.get(i).getX()>xmax)
								xmax=points.get(i).getX();

							if(points.get(i).getY()<ymin)
								ymin=points.get(i).getY();

							if(points.get(i).getY()>ymax)
								ymax=points.get(i).getY();
						}
						Envelope ext=new Envelope();
						ext.setCoords(xmin,ymin,xmax,ymax);
						//map.setExtent(ext,300);


					}catch(JSONException e){
						e.printStackTrace();
					}

				}
			}
		}
	};

	public void getDataFromNet(final String where)
	{
		ProgressDialogUtils.showProgressDialog(POISearchActivity.this, "正在拼命查询，请稍后！");

		if(NetworkManager.isNetworkConnected(this)){//联网情况下
			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {

					queryPoiInfos("http://"+Constantss.SERVER_IP+Constantss.POI_URL,where);

				}});
			thread.start();
		}
		else{
			ProgressDialogUtils.dismissProgressDialog();
			Toast.makeText(POISearchActivity.this, "请检查网络或服务设置！", Toast.LENGTH_SHORT).show();

		}
	}

	public void clear_Map(Object parameters){

		poi_list.clear();
		poi_keywords.setText("");
	}

}

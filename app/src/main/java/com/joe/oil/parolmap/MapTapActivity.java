package com.joe.oil.parolmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.joe.oil.R;
import com.joe.oil.activity.BaseActivity;
import com.joe.oil.util.NetworkManager;

import java.io.File;
import java.util.Map;

public class MapTapActivity extends BaseActivity
{
	private final static String TAG="MapTapActivity";
	public final static int MAIN_CODE=0;

	private String list_id="";

	public static MapView mMapView;
	public static GraphicsLayer drawLayer =null;

	public static PictureMarkerSymbol symbol_node;

	final SpatialReference wm = SpatialReference.create(102100);
	final SpatialReference egs = SpatialReference.create(4326);

	ImageView btn_MapSwitch_vector;
	ImageView btn_MapSwitch_image;
	TextView  txt_MapTitle;

	ArcGISTiledMapServiceLayer tileLayer_Vector;
	ArcGISLocalTiledLayer local_vector;
	ArcGISLocalTiledLayer local_image;

	private  int flag_map=1;
	private String Tap_type="map_stop";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maptap);

		mMapView = (MapView) findViewById(R.id.map);

		Drawable image= this.getResources().getDrawable(R.drawable.route_loc);
		symbol_node = new PictureMarkerSymbol(image);

		symbol_node.setOffsetX(0);
		symbol_node.setOffsetY(8);

		list_id = getIntent().getStringExtra("list_id");

		Map<String, String> params_Setting = Constantss.getParams();

		Constantss.SERVER_IP = params_Setting.get("serverIP");

		if (String.valueOf(params_Setting.get("online_map")).equals("显示")) {
			if (NetworkManager.isNetworkConnected(this)) {
				{
					tileLayer_Vector = new ArcGISTiledMapServiceLayer(Constantss.ONLINEMAP_URL);
					mMapView.addLayer(tileLayer_Vector);
				}
			} else {
				Toast.makeText(this, "加载在线底图需要网络支持！", Toast.LENGTH_SHORT).show();
			}
		}

		boolean sdExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

		File sdDir = null;

		if (!sdExist) {
			Toast.makeText(MapTapActivity.this, "SD卡不存在，请加载SD卡!", Toast.LENGTH_SHORT).show();
		} else {
			sdDir =Environment.getExternalStorageDirectory();

			//Environment.getExternalStorageDirectory().getAbsolutePath();


			final String URL = sdDir.getPath() + "/ArcGIS/data/arcgiscache/GAS_vec10/Layers/conf.xml";

			File file = new File(URL);

			if (file.exists()) {

				local_vector = new ArcGISLocalTiledLayer("file://" +sdDir.getPath()+ "/ArcGIS/data/arcgiscache/GAS_vec10/Layers");
				mMapView.addLayer(local_vector);

				local_image = new ArcGISLocalTiledLayer("file://" +sdDir.getPath() + "/ArcGIS/data/arcgiscache/GAS_img10/Layers");
				local_image.setVisible(false);
				mMapView.addLayer(local_image);

				//ArcGISLocalTiledLayer local_vector2 = new ArcGISLocalTiledLayer("file:///storage/emulated/0/ArcGIS/data/xian/Layers");
				//mMapView.addLayer(local_vector2);

			} else {

				File fileExt = new File("/storage/extSdCard/ArcGIS");
				if (fileExt.exists()) {
					String vector_URL = "file:///storage/extSdCard/ArcGIS/data/arcgiscache/GAS_vec10/Layers";
					String image_URL = "file:///storage/extSdCard/ArcGIS/data/arcgiscache/GAS_img10/Layers";

					local_vector = new ArcGISLocalTiledLayer(vector_URL);
					mMapView.addLayer(local_vector);

					local_image = new ArcGISLocalTiledLayer(image_URL);
					local_image.setVisible(false);
					mMapView.addLayer(local_image);

				} else {
					Toast.makeText(MapTapActivity.this, "缓存目录不存在，请核查!", Toast.LENGTH_SHORT).show();
				}
			}
		}
		drawLayer = new GraphicsLayer();
		mMapView.addLayer(drawLayer);

		Envelope initExt = new Envelope(11977456.17631376, 4665145.89448697, 12286358.066861996, 4860607.222909627);

		//Envelope initExt=new Envelope (11977456.548124164,4665145.89448697,12286358.207594149,4860607.222909627);
		mMapView.setExtent(initExt);
		//mMapView.setAllowRotationByPinch(true);

		btn_MapSwitch_vector = (ImageView) findViewById(R.id.map_swtich_vector);
		btn_MapSwitch_image = (ImageView) findViewById(R.id.map_swtich_image);

		txt_MapTitle = (TextView) findViewById(R.id.mapTitle);

		btn_MapSwitch_vector.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				flag_map += 1;

				if (flag_map % 2 == 0) {
					local_vector.setVisible(false);
					local_image.setVisible(true);

					btn_MapSwitch_vector.setVisibility(View.GONE);
					btn_MapSwitch_image.setVisibility(View.VISIBLE);

					txt_MapTitle.setText("影像地图");

				} else {

					local_vector.setVisible(true);
					local_image.setVisible(false);

					btn_MapSwitch_vector.setVisibility(View.VISIBLE);
					btn_MapSwitch_image.setVisibility(View.GONE);

					txt_MapTitle.setText("矢量地图");

				}

			}
		});
		btn_MapSwitch_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				flag_map += 1;

				if (flag_map % 2 == 0) {
					local_vector.setVisible(false);
					local_image.setVisible(true);

					btn_MapSwitch_vector.setVisibility(View.GONE);
					btn_MapSwitch_image.setVisibility(View.VISIBLE);

					txt_MapTitle.setText("影像地图");

				} else {

					local_vector.setVisible(true);
					local_image.setVisible(false);

					btn_MapSwitch_vector.setVisibility(View.VISIBLE);
					btn_MapSwitch_image.setVisibility(View.GONE);

					txt_MapTitle.setText("矢量地图");

				}


			}
		});

		class MyTouchListener extends MapOnTouchListener {

			public MyTouchListener(Context context, MapView view) {
				super(context, view);
			}

			public void setType(String geometryType) {
				Tap_type = geometryType;
			}

			public String getType() {
				return Tap_type;
			}

			@Override
			public boolean onSingleTap(MotionEvent point) {
				Point pt = mMapView.toMapPoint(new Point(point.getX(), point.getY()));

				Point pt_wgs = (Point) GeometryEngine.project(pt, wm, egs);

				if (Tap_type.equals("map_stop")) {

					Graphic gr = new Graphic(pt, symbol_node);

					drawLayer.addGraphic(gr);

					Tap_type = "";

					Intent intent = new Intent();
					intent.putExtra("value", list_id + "," + String.valueOf(pt_wgs.getX()) + "," + String.valueOf(pt_wgs.getY()));
					setResult(MAIN_CODE, intent);
					finish();

				}

				return true;
			}

			public boolean onDragPointerMove(MotionEvent from, MotionEvent to) {
				return super.onDragPointerMove(from, to);
			}

			@Override
			public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
				return super.onDragPointerUp(from, to);
			}
		}

		MyTouchListener myListener = new MyTouchListener(this, mMapView);
		mMapView.setOnTouchListener(myListener);
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

}

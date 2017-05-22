package com.joe.oil.parolmap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.ags.LayerServiceInfo;
import com.esri.core.gdb.GdbFeatureTable;
import com.esri.core.gdb.Geodatabase;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.na.RouteTask;
import com.joe.oil.R;
import com.joe.oil.activity.BaseActivity;
import com.joe.oil.activity.MainActivity;
import com.joe.oil.eventbus.EventBusManager;
import com.joe.oil.eventbus.EventCode;
import com.joe.oil.parolmap.MeasureWindow.onSearchBarItemClickListener;
import com.joe.oil.sqlite.DatabaseContext;
import com.joe.oil.sqlite.SdCardDBHelper;
import com.joe.oil.util.AlarmReceiver;
import com.joe.oil.util.AsyncQueryTask;
import com.joe.oil.util.GPSUtil;
import com.joe.oil.util.MTextView;
import com.joe.oil.util.NetworkManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by liangxiaojiang on 2016/11/24.
 */
public class NavigationActivity extends BaseActivity implements onSearchBarItemClickListener{
    private final static String TAG="NavigationActivity";
    public final static int MAIN_CODE=0;

    private String list_id="";

    public static MapView mMapView;
    private static GraphicsLayer gpsLayer = null;
    public static GraphicsLayer poiLayer=null;
    public static GraphicsLayer drawLayer =null;
    private static Callout callout;

    public static PictureMarkerSymbol symbol1;
    public static PictureMarkerSymbol symbol2;

    public static PictureMarkerSymbol symbol_node;

    final SpatialReference wm = SpatialReference.create(102100);
    final SpatialReference egs = SpatialReference.create(4326);

    private MeasureWindow mBarPopupWindow = null;

    ImageView btn_GPS;
    ImageView btn_GPS_open;
    ImageView btn_MapSwitch_vector;
    ImageView btn_MapSwitch_image;
    TextView txt_MapTitle;
    ImageView btn_Measure;
    ImageView btn_Setting;
    ImageView btn_Route;

    TextView btn_POISearch;

    MTextView route_name;
    public static RouteTask mRouteTask = null;

    ArcGISTiledMapServiceLayer tileLayer_Vector;
    ArcGISLocalTiledLayer local_vector;
    ArcGISLocalTiledLayer local_image;

    private  int flag_map=1;

    private String draw_type="";

    private String Tap_type="";
    private ArrayList<Point> points=new ArrayList<Point>();
    private int index_n=0;
    private int index1;
    private int index2;
    private double sum=0;

    private Graphic tempGraphic;
    private Graphic temp_Point;
    private Point pt_center;
    private SimpleLineSymbol lineSymbol;
    private SimpleMarkerSymbol markerSymbol;
    private SimpleFillSymbol fillSymbol;
    private static Polyline polyline = new Polyline();
    private Polygon tempPolygon=null;

    private Geodatabase mLocalGdb = null;
    FeatureLayer routefeatureLayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mMapView = (MapView) findViewById(R.id.map);

        list_id = getIntent().getStringExtra("list_id");

        //初始化SQLite数据库
        DatabaseContext dbContext = new	DatabaseContext(this);
        SdCardDBHelper dbHelper = new SdCardDBHelper(dbContext);
        dbHelper.getWritableDatabase();

        lineSymbol = new SimpleLineSymbol(Color.RED, 2, SimpleLineSymbol.STYLE.SOLID);
        markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 6, SimpleMarkerSymbol.STYLE.CIRCLE);
        fillSymbol = new SimpleFillSymbol(Color.GREEN);
        fillSymbol.setOutline(lineSymbol);
        fillSymbol.setAlpha(75);

        route_name=(MTextView)findViewById(R.id.routetxt);
        route_name.setVisibility(View.GONE);
        Map<String,String> params_Setting = Constantss.getParams();

        Constantss.SERVER_IP=params_Setting.get("serverIP");

        if(String.valueOf(params_Setting.get("online_map")).equals("显示"))
        {
            if(NetworkManager.isNetworkConnected(this)){
                {
                    tileLayer_Vector = new ArcGISTiledMapServiceLayer(Constantss.ONLINEMAP_URL);
                    mMapView.addLayer(tileLayer_Vector);
                }
            }
            else
            {
                Toast.makeText(this, "加载在线底图需要网络支持！", Toast.LENGTH_SHORT).show();
            }
        }
        EventBusManager.addListener(this, EventCode.SET_GPS_ICON, "set_GPS_Icon");
        EventBusManager.addListener(this, EventCode.SET_TAP_TYPE, "set_TAP_Type");
        EventBusManager.addListener(this, EventCode.SHOW_ROUTE_NAME,"show_route_txt");
        EventBusManager.addListener(this, EventCode.HIDE_ROUTE_NAME,"hide_route_txt");

        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());

        if(!sdExist)
        {
            Toast.makeText(NavigationActivity.this, "SD卡不存在，请加载SD卡!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();

            final String URL=sdDir+"/ArcGIS/data/arcgiscache/GAS_vec10/Layers/conf.xml";

            File file=new File(URL);

            if(file.exists())
            {
                local_vector = new ArcGISLocalTiledLayer(Environment.getExternalStorageDirectory()+"/ArcGIS/data/arcgiscache/GAS_vec10/Layers");
                mMapView.addLayer(local_vector);

                local_image = new ArcGISLocalTiledLayer(Environment.getExternalStorageDirectory()+"/ArcGIS/data/arcgiscache/GAS_img10/Layers");
                local_image.setVisible(false);
                mMapView.addLayer(local_image);

                //ArcGISLocalTiledLayer local_vector2 = new ArcGISLocalTiledLayer("file:///storage/emulated/0/ArcGIS/data/xian/Layers");
                //mMapView.addLayer(local_vector2);

                String gdbPath=Environment.getExternalStorageDirectory() + "/ArcGIS/database/routedata.geodatabase";
                mLocalGdb = new Geodatabase(gdbPath);

                if (mLocalGdb != null) {
                    for (GdbFeatureTable gdbFeatureTable : mLocalGdb.getGdbTables()) {
                        LayerServiceInfo layerInfo = gdbFeatureTable.getServiceLayerInfo();

                        if (gdbFeatureTable.hasGeometry()) {

                            routefeatureLayer = new FeatureLayer(gdbFeatureTable);
                            routefeatureLayer.setVisible(false);
                            mMapView.addLayer(routefeatureLayer);
                        }
                    }
                }
            }
            else
            {
                File fileExt=new File("/storage/sdcard1/ArcGIS");
                if(fileExt.exists())
                {
                    String vector_URL="file:///storage/sdcard1/ArcGIS/data/arcgiscache/GAS_vec10/Layers";
                    String image_URL="file:///storage/sdcard1/ArcGIS/data/arcgiscache/GAS_img10/Layers";

                    local_vector = new ArcGISLocalTiledLayer(vector_URL);
                    mMapView.addLayer(local_vector);

                    local_image = new ArcGISLocalTiledLayer(image_URL);
                    local_image.setVisible(false);
                    mMapView.addLayer(local_image);

                }
                else
                {
                    Toast.makeText(NavigationActivity.this, "缓存目录不存在，请核查!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        mMapView.setOnZoomListener(new OnZoomListener() {

            @Override
            public void preAction(float pivotX, float pivotY, double factor) {
                // TODO Auto-generated method stub

            }

            @Override
            public void postAction(float pivotX, float pivotY, double factor) {

                //Toast.makeText(MainActivity.this, String.valueOf(mMapView.getScale()), Toast.LENGTH_SHORT).show();

                if(mMapView.getScale()<200000){
//                    routefeatureLayer.setVisible(true);
                    }
                else {
//                    routefeatureLayer.setVisible(false);
                }
            }
        });

        // Initialize the RouteTask
        try {
            mRouteTask = RouteTask.createOnlineRouteTask("http://"+Constantss.SERVER_IP+Constantss.ROUTE_URL, null);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        //ArcGISFeatureLayer featureLayer=new ArcGISFeatureLayer("http://");
        //featureLayer.setDefinitionExpression("");

        Drawable image1= this.getResources().getDrawable(R.drawable.start1);
        Drawable image2= this.getResources().getDrawable(R.drawable.end1);
        Drawable image3= this.getResources().getDrawable(R.drawable.stop1);
        symbol1 = new PictureMarkerSymbol(image1);
        symbol1.setOffsetX(0);
        symbol1.setOffsetY(18);

        symbol2 = new PictureMarkerSymbol(image2);
        symbol2.setOffsetX(0);
        symbol2.setOffsetY(18);

        symbol_node = new PictureMarkerSymbol(image3);

        symbol_node.setOffsetX(0);
        symbol_node.setOffsetY(12);

        callout = mMapView.getCallout();

        poiLayer=new GraphicsLayer();
        mMapView.addLayer(poiLayer);

        drawLayer=new GraphicsLayer();
        mMapView.addLayer(drawLayer);

        gpsLayer=new GraphicsLayer();
        mMapView.addLayer(gpsLayer);

        Envelope initExt=new Envelope (11977456.17631376,4665145.89448697,12286358.066861996,4860607.222909627);

        //Envelope initExt=new Envelope (11977456.548124164,4665145.89448697,12286358.207594149,4860607.222909627);
        mMapView.setExtent(initExt);
        //mMapView.setAllowRotationByPinch(true);

        mBarPopupWindow = new MeasureWindow(this, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mBarPopupWindow.setOnSearchBarItemClickListener(this);

        GPSUtil.context=this;
        GPSUtil.mapView=mMapView;
        GPSUtil.gLayer=gpsLayer;
        GPSUtil.initLocation();
        //GPSUtil.startLocation();

        btn_GPS = (ImageView) findViewById(R.id.gps);

        btn_GPS_open=(ImageView)findViewById(R.id.gps_open);
        btn_GPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(GPSUtil.hasGPSDevice())
                {
                    //GPSUtil.locator();
                    String strInterval=Constantss.getParams().get("gps");
                    if(Long.valueOf(strInterval)>0)
                    {
                        GPSUtil.flag="gps";
                        GPSUtil.resetOption(strInterval);
                        GPSUtil.startLocation();
                        Toast.makeText(NavigationActivity.this, "开始定位！", Toast.LENGTH_SHORT).show();
                        btn_GPS_open.setVisibility(View.VISIBLE);
                        btn_GPS.setVisibility(View.GONE);
                    }
                }
                else{

                    Toast.makeText(NavigationActivity.this, "请开启GPS！", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_GPS_open.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(GPSUtil.hasGPSDevice())
                {
                    //GPSUtil.locator();
                    GPSUtil.stopLocation();
                    GPSUtil.flag="gps";
                    btn_GPS_open.setVisibility(View.GONE);
                    btn_GPS.setVisibility(View.VISIBLE);
                    Toast.makeText(NavigationActivity.this, "停止定位！", Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(NavigationActivity.this, "请开启GPS！", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_MapSwitch_vector=(ImageView)findViewById(R.id.map_swtich_vector);
        btn_MapSwitch_image = (ImageView) findViewById(R.id.map_swtich_image);

        txt_MapTitle=(TextView)findViewById(R.id.mapTitle);

        btn_MapSwitch_vector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                flag_map+=1;

                if(flag_map%2==0)
                {
                    local_vector.setVisible(false);
                    local_image.setVisible(true);

                    btn_MapSwitch_vector.setVisibility(View.GONE);
                    btn_MapSwitch_image.setVisibility(View.VISIBLE);

                    txt_MapTitle.setText("影像地图");

                }
                else {

                    local_vector.setVisible(true);
                    local_image.setVisible(false);

                    btn_MapSwitch_vector.setVisibility(View.VISIBLE);
                    btn_MapSwitch_image.setVisibility(View.GONE);

                    txt_MapTitle.setText("矢量地图");

                }

            }
        });
        btn_MapSwitch_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                flag_map+=1;

                if(flag_map%2==0)
                {
                    local_vector.setVisible(false);
                    local_image.setVisible(true);

                    btn_MapSwitch_vector.setVisibility(View.GONE);
                    btn_MapSwitch_image.setVisibility(View.VISIBLE);

                    txt_MapTitle.setText("影像地图");

                }
                else {

                    local_vector.setVisible(true);
                    local_image.setVisible(false);

                    btn_MapSwitch_vector.setVisibility(View.VISIBLE);
                    btn_MapSwitch_image.setVisibility(View.GONE);

                    txt_MapTitle.setText("矢量地图");

                }


            }
        });

        btn_Measure = (ImageView) findViewById(R.id.btn_measure);
        btn_Measure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mBarPopupWindow.showAtLocation(mMapView, Gravity.TOP, 0, 176);

                //drawLayer.removeAll();
                poiLayer.removeAll();
                callout.hide();
            }
        });

        btn_Setting = (ImageView) findViewById(R.id.btn_settings);
        btn_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(NavigationActivity.this, SettingssActivity.class);
                //intent.putExtra("pt", String.valueOf(fire_pt.getX())+","+String.valueOf(fire_pt.getY()));
                startActivity(intent);
                callout.hide();
            }
        });

        final String vehicleCode=getIntent().getStringExtra("vehicleCode");
        btn_Route=(ImageView)findViewById(R.id.map_route);
        btn_Route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callout.hide();
                Intent intent = new Intent();
                intent.setClass(NavigationActivity.this, RouteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("vehicleCode",vehicleCode);
                startActivity(intent);
            }
        });


        btn_POISearch=(TextView)findViewById(R.id.btn_poi);
        btn_POISearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(NavigationActivity.this, POISearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("pt_id", "00");
//                intent.putExtra("route",getIntent().getStringExtra("route"));
                startActivity(intent);
                callout.hide();

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
            public boolean onDoubleTap(MotionEvent point) {
                if(draw_type.equals(""))
                {
                    return true;
                }
                else if(draw_type.equals("Polygon"))
                {
                    if(callout.isShowing())
                        callout.hide();

                    if(points.size()>1)
                    {
                        if(tempGraphic!=null)
                        {
                            drawLayer.removeGraphic(index2);
                        }

                        Point lastPoint=mMapView.toMapPoint(new Point(point.getX(),point.getY()));

                        points.add(lastPoint);

                        tempPolygon=new Polygon();

                        tempPolygon.startPath(points.get(0));
                        for(int i=1;i<points.size();i++)
                        {
                            tempPolygon.lineTo(points.get(i));
                        }
                        tempGraphic = new Graphic(tempPolygon, fillSymbol);
                        index2 = drawLayer.addGraphic(tempGraphic);

                        for(int i=0;i<points.size();i++)
                        {
                            Graphic graphic = new Graphic(points.get(i),markerSymbol);
                            drawLayer.addGraphic(graphic);
                        }

                        String sArea = getAreaString(tempPolygon.calculateArea2D(),1);
                        //String sper = getAreaString(tempPolygon.calculateLength2D(),0);

                        PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(
                                createMapBitMap("面积:"+sArea,1));
                        //PictureMarkerSymbol markerSymbol2 = new PictureMarkerSymbol(
                        //           createMapBitMap("周长:"+sper,1));

                        Point pt_center2=new Point(pt_center.getX()-50,pt_center.getY()-50);
                        Graphic area_lable=new Graphic(pt_center2,markerSymbol);
                        //Point pt_center2=new Point(pt_center.getX(),pt_center.getY()-50);
                        //Graphic area_lable2=new Graphic(pt_center2,markerSymbol2);

                        drawLayer.addGraphic(area_lable);
                        //layer.addGraphic(area_lable2);

                    }
                    points.clear();
                    tempPolygon = null;
                    tempGraphic=null;
                    index_n=0;

                    return false;
                }
                else if(draw_type.equals("Polygon"))
                {
                    points.clear();
                    tempPolygon = null;
                    tempGraphic=null;
                    index_n=0;
                    return false;
                }
                else
                {
                    points.clear();
                    tempPolygon = null;
                    tempGraphic=null;
                    index_n=0;

                    return false;
                }
            }

            @Override
            public boolean onSingleTap(MotionEvent point) {
                Point pt= mMapView.toMapPoint(new Point(point.getX(), point.getY()));

                PictureMarkerSymbol poi_sel_Symbol = new PictureMarkerSymbol(
                        mMapView.getContext(), getResources().getDrawable(
                        R.drawable.icon_pin_red));

                PictureMarkerSymbol poi_Symbol = new PictureMarkerSymbol(
                        mMapView.getContext(), getResources().getDrawable(
                        R.drawable.icon_pin_blue));

                Point pt_wgs=(Point) GeometryEngine.project(pt,wm,egs);

                if(Tap_type.equals("meature"))
                {
                    index_n++;

                    points.add(pt);
                    if (index_n == 1) {
                        temp_Point = new Graphic(pt, markerSymbol);
                        index1 = drawLayer.addGraphic(temp_Point);
                    } else {
                        if (draw_type.equals("Polyline")) {

                            if (points.size() > 1) {
                                Point startPoint = points.get(points.size() - 2);
                                Point endPoint = points.get(points.size() - 1);

                                Line line1 = new Line();
                                line1.setStart(startPoint);
                                line1.setEnd(endPoint);

                                polyline.addSegment(line1, false);

                                Polyline temLine = new Polyline();
                                temLine.addSegment(line1, false);

                                tempGraphic = new Graphic(temLine, lineSymbol);
                                drawLayer.addGraphic(tempGraphic);

                                drawLayer.removeGraphic(index1);

                                Graphic graphic1 = new Graphic(points.get(points.size() - 2), markerSymbol);
                                drawLayer.addGraphic(graphic1);

                                Graphic graphic2 = new Graphic(points.get(points.size() - 1), markerSymbol);
                                drawLayer.addGraphic(graphic2);

                            }

                            String sum_Str;
                            sum = Math.round(polyline.calculateLength2D());
                            if (points.size() > 1) {
                                TextView tv = new TextView(NavigationActivity.this);
                                tv.setTextColor(Color.rgb(255, 0, 0));

                                if (sum < 1000) {
                                    sum_Str = "总距离:" + Double.toString(sum).substring(0, Double.toString(sum).length() - 2) + "米";
                                } else {
                                    sum_Str = "总距离:" + Double.toString(sum / 1000).substring(0, Double.toString(sum / 1000).length() - 1) + "千米";
                                }
                                tv.setText(sum_Str);

                                callout.setContent(tv);
                                callout.setOffset(0, -3);//设置偏移量
                                callout.show(points.get(points.size() - 1));
                            }
                        } else if (draw_type.equals("Polygon")) {
                            if (tempGraphic != null) {
                                drawLayer.removeGraphic(index2);
                            }

                            double xmax = 0, xmin = 999999999, ymax = 0, ymin = 99999999;

                            Polygon polygon = new Polygon();
                            polygon.startPath(points.get(0));

                            for (int i = 1; i < points.size(); i++) {
                                polygon.lineTo(points.get(i));
                            }
                            tempGraphic = new Graphic(polygon, fillSymbol);
                            index2 = drawLayer.addGraphic(tempGraphic);

                            for (int i = 0; i < points.size(); i++) {
                                Graphic graphic = new Graphic(points.get(i), markerSymbol);
                                drawLayer.addGraphic(graphic);
                            }

                            for (int i = 0; i < points.size(); i++) {
                                if (points.get(i).getX() < xmin)
                                    xmin = points.get(i).getX();

                                if (points.get(i).getX() > xmax)
                                    xmax = points.get(i).getX();

                                if (points.get(i).getY() < ymin)
                                    ymin = points.get(i).getY();

                                if (points.get(i).getY() > ymax)
                                    ymax = points.get(i).getY();
                            }

                            pt_center = new Point((xmax + xmin) / 2 - 100, (ymin + ymax) / 2);
                        }
                    }

                }
                else if (Tap_type.equals("poi"))
                {
                    if (callout.isShowing()) {
                        callout.hide();
                    }

                    int[] graphicIDs = poiLayer.getGraphicIDs(point.getX(), point.getY(), 25);
                    if (graphicIDs != null && graphicIDs.length > 0) {
                        Graphic gr = poiLayer.getGraphic(graphicIDs[0]);

                        poiLayer.updateGraphic(Constantss.selectedSegmentID, poi_Symbol);
                        poiLayer.updateGraphic(gr.getUid(), poi_sel_Symbol);

                        Constantss.selectedSegmentID = gr.getUid();

                        Point location = (Point) gr.getGeometry();

                        TextView tv = new TextView(NavigationActivity.this);
                        tv.setTextColor(Color.rgb(255, 0, 0));
                        String name_chn = String.valueOf(gr.getAttributeValue("poi_name"));
                        tv.setText(name_chn);
                        callout.show(location, tv);
                    }

                }
                else if(Tap_type.equals("map_stop")) {

                    Graphic gr = new Graphic(pt, symbol_node);

                    drawLayer.addGraphic(gr);

                    Tap_type="";

                    Intent intent=new Intent();
                    intent.putExtra("value", list_id+","+String.valueOf(pt_wgs.getX())+","+String.valueOf(pt_wgs.getY()));
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

        //jackey  2017.3.19,启动一个定时任务当锁屏或者黑屏时启动GPS定时器刷新位置
        //
        AlarmManager am = null;
        am = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent =new Intent(getApplicationContext(), AlarmReceiver.class);
        int requestCode = 0;
        PendingIntent pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //5秒后发送广播，然后每个30秒重复发广播。广播都是直接发到AlarmReceiver的
        long triggerAtTime = SystemClock.elapsedRealtime() + 5 * 1000;
        int interval = 30 * 1000;

        //30s秒后发送定时任务
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,interval, pendIntent);

//        showRouteByNo();
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        EventBusManager.addListener(this, EventCode.SET_GPS_ICON, "set_GPS_Icon");
        EventBusManager.addListener(this, EventCode.SET_TAP_TYPE, "set_TAP_Type");
        EventBusManager.addListener(this, EventCode.SHOW_ROUTE_NAME,"show_route_txt");
    }
    @Override
    public void onBarCodeButtonClick() {
        Tap_type="meature";
        draw_type="Polyline";
        //drawLayer.removeAll();
        callout.hide();

        points.clear();
        tempPolygon = null;
        tempGraphic=null;
        index_n=0;
    }

    @Override
    public void onCameraButtonClick() {
        Tap_type="meature";
        draw_type= "Polygon";
        //drawLayer.removeAll();
        callout.hide();

        points.clear();
        tempPolygon = null;
        tempGraphic=null;
        index_n=0;
    }

    @Override
    public void onColorButtonClick() {
        Tap_type="";
        drawLayer.removeAll();
        poiLayer.removeAll();
        callout.hide();

        points.clear();
        tempPolygon = null;
        tempGraphic=null;
        index_n=0;

        route_name.setVisibility(View.GONE);
        EventBusManager.dispatchEvent(this, EventCode.CLEAR_MAP, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void show_route_txt(Object params){

        String para=(String) params;

        route_name.setText(para);
        route_name.setVisibility(View.VISIBLE);
    }
    public void hide_route_txt(Object params){

        route_name.setVisibility(View.GONE);
    }

    public void set_TAP_Type(Object parameters){
        String paras=(String) parameters;
        if(paras.equals("map_stop"))
        {
            Tap_type="map_stop";
        }
        else if(paras.equals("poi"))
        {
            Tap_type="poi";
        }
    }
    public void set_GPS_Icon(Object parameters){
        btn_GPS_open.setVisibility(View.VISIBLE);
        btn_GPS.setVisibility(View.GONE);
    }
    public void showRoute(String itemId){

        //String[] strs = itemId.split(",");

        AsyncQueryTask ayncQuery = new AsyncQueryTask(mMapView,drawLayer, symbol1,symbol2,symbol_node);
        final String url_route="http://"+Constantss.SERVER_IP+Constantss.ROUTELIST_URL;

        String where = "OBJECTID="+"\'"+itemId+"\'";
        String[] Params = {url_route,where,"*","true"};
        ayncQuery.execute(Params);

        if(callout.isShowing())
            callout.hide();

    }

//    public void showRouteByNo(){
//        AsyncQueryTask ayncQuery = new AsyncQueryTask(mMapView,drawLayer, symbol1,symbol2,symbol_node);
//        final String url_route="http://"+Constantss.SERVER_IP+Constantss.ROUTELIST_URL;
//
//        String where = "ROUTE_NO="+"\'"+getIntent().getStringExtra("vehicleCode")+"\'";
//        String[] Params = {url_route,where,"*","true"};
//        ayncQuery.execute(Params);
//
//        if(callout.isShowing())
//            callout.hide();
//
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
            Intent intent=new Intent(NavigationActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        SdCardDBHelper.getInstance().getWritableDatabase().close();
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String result = data.getExtras().getString("result");

        //Log.i(TAG, result);
    }

    private String getAreaString(double dValue,int index){
        String sArea = "";
        long area = Math.abs(Math.round(dValue));
        DecimalFormat df = new DecimalFormat("#.##");

        //Map<String,String> params= Constantss.getParams();
        //String str_Unit=String.valueOf(params.get("unit_m"));

        if(index==1)
        {
            if(area<10000)
            {
                double dist=area/666.6667;
                sArea=df.format(dist)+"亩";
                //sArea = Double.toString(area/666.67).substring(0,Double.toString(area/666.67).length()-2) + "亩";
            }
            else{
                double dArea = area / 10000.0;
                sArea = df.format(dArea) + "公顷";
            }
        }
        else
        {
            if(area<1000)
            {
                sArea = Double.toString(area).substring(0,Double.toString(area).length()-2) + "米";
            }
            else
            {
                double dArea = area / 1000.0;
                sArea = df.format(dArea) + "千米";
            }

        }

        return sArea;
    }
    /**
     * 文字转换BitMap
     * @param text
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Drawable createMapBitMap(String text,int flag) {

        Paint paint = new Paint();
        if(flag==1)
        {
            paint.setColor(Color.RED);
        }
        else
        {
            paint.setColor(Color.BLACK);
        }

        paint.setTextSize(25);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);

        float textLength = paint.measureText(text);

        int width = (int) textLength + 300;
        int height = 120;

        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas cv = new Canvas(newb);
        cv.drawColor(Color.parseColor("#00000000"));

        cv.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));

        cv.drawText(text, width / 2, 20, paint);

//		 BitmapFactory.Options opts = new BitmapFactory.Options();
//	       opts.inPreferredConfig = Config.RGB_565;
//	       opts.inPurgeable = true;
//	       opts.inInputShareable = true;
//	       InputStream is = context.getResources().openRawResource(resId);
//	       return BitmapFactory.decodeStream(is, null, opts);

//		Bitmap iconbit = BitmapFactory.decodeResource(context.getResources(), R.drawable.basemap) ;



        //Bitmap b = Bitmap.createBitmap(gridchart.getDrawingCache());


        //cv.drawBitmap(iconbit, 10,10, paint);

        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储

        return new BitmapDrawable(newb);

    }
}

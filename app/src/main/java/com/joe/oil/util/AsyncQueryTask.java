package com.joe.oil.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.ags.query.OrderByFields;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;
import com.joe.oil.eventbus.EventBusManager;
import com.joe.oil.eventbus.EventCode;

import java.util.Map;


public class AsyncQueryTask extends AsyncTask<String,Integer,FeatureSet>{////AsyncTask<Params,Progress,Result>

	private MapView mMapView = null;
	private GraphicsLayer gLayer=null;

	private PictureMarkerSymbol symbol1;
	private PictureMarkerSymbol symbol2;

	private PictureMarkerSymbol symbol_node;

	private String r_name="";
	
	public AsyncQueryTask(MapView map, GraphicsLayer layer, PictureMarkerSymbol symbol1, PictureMarkerSymbol symbol2, PictureMarkerSymbol symbol_node){
		super();
		this.mMapView = map;
		this.gLayer=layer;
		this.symbol1=symbol1;
		this.symbol2=symbol2;
		this.symbol_node=symbol_node;
	}
	
	@Override  
	protected void onPreExecute() {  
		//执行前操作 

		super.onPreExecute();
	}  
	/**
	 * 查询结果处理
	 *
	 * @param    queryParams 参数依次为url,whereClause,outFields,returnGeometry,geometry,outSR.	**/
	@Override  
	protected FeatureSet doInBackground(String... queryParams) {
		// TODO Auto-generated method stub
		if(queryParams == null || queryParams.length < 1)
			return null;
		Query query = new Query();//查询对象
		query.setWhere(queryParams[1]);//设置查询条件whereClause		
		query.setOutFields(queryParams[2].split(","));//设置输出字段
		Map<String, OrderByFields> orderByFields=null;
		//orderByFields.put(key, value);
		
		query.setOrderByFields(orderByFields);
		//设置是否返回几何对象
		if(queryParams[3] == "true")
			query.setReturnGeometry(true);
		else
			query.setReturnGeometry(false);
		//指定查询范围
		if(queryParams.length >= 4) 
		//设置返回的空间参考
		if(queryParams.length >= 5){
			SpatialReference sr = SpatialReference.create(102100);
			query.setOutSpatialReference(sr);
		}
		SpatialReference sr = SpatialReference.create(102100);
		query.setOutSpatialReference(sr);
		
		QueryTask qTask = new QueryTask(queryParams[0]);
        FeatureSet fs = null;
        try{
        	fs = qTask.execute(query);//执行查询
        }catch (Exception e){
        	e.printStackTrace();
            return fs;
        }
        return fs;
	}
	
	@Override  
    protected void onProgressUpdate(Integer... progress) {              
        super.onProgressUpdate(progress);
    }
	
	 @Override  
     protected void onPostExecute(FeatureSet result) {
		 if(result != null){
			 Graphic[] graphics = result.getGraphics();
			 SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.SOLID);
			 gLayer.removeAll();	
			 int num = graphics.length;

			 SpatialReference sr = SpatialReference.create(4326);
			 SpatialReference sr_web = SpatialReference.create(102100);
			 for(int i = 0; i < num;i++){
				 Graphic g=new Graphic(graphics[i].getGeometry(),lineSymbol);
				 gLayer.addGraphic(g);
				 
				 mMapView.setExtent(g.getGeometry());
				 	
			     Polyline line= (Polyline)graphics[i].getGeometry();

				 r_name=String.valueOf(graphics[i].getAttributeValue("NAME"));

				 EventBusManager.dispatchEvent(this, EventCode.SHOW_ROUTE_NAME, r_name);

			     String stops=String.valueOf(graphics[i].getAttributeValue("STOP"));

				 if(stops.equals("null"))
				 {

				 }
				 else
				 {
					 String[] stops_points = stops.split(";");

					 for (int j=0;j<stops_points.length;j++)
					 {
						 String[] stops_pts = stops_points[j].split(",");

						 if(stops_pts[0].equals("0"))
						 {

						 }
						 else {
							 Point pt=new Point(Double.valueOf(stops_pts[0]),Double.valueOf(stops_pts[1]));
							 Point ptMap = (Point) GeometryEngine.project(pt, sr,sr_web);
							 Graphic gr_node=new Graphic(ptMap,symbol_node);
							 gLayer.addGraphic(gr_node);
						 }
					 }
				 }

			     //PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(
		          //         createMapBitMap(lab,1));
			     
			     //Point pt_center=line.getPoint(line.getPointCount()/2);
			   
		         Point pt_start=line.getPoint(0);
		         Point pt_end=line.getPoint(line.getPointCount()-1);
		         
			     //Graphic route_label=new Graphic(pt_center,markerSymbol);
			     Graphic gr1=new Graphic(pt_start,symbol1);
			     Graphic gr2=new Graphic(pt_end,symbol2);
			     
			     gLayer.addGraphic(gr1);
			     gLayer.addGraphic(gr2);
			    
			 }
			 
		}
		 super.onPostExecute(result);
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
			paint.setTextAlign(Align.LEFT);

			float textLength = paint.measureText(text);

			int width = (int) textLength + 300;
			int height = 120;

			Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

			Canvas cv = new Canvas(newb);
			cv.drawColor(Color.parseColor("#00000000"));

			cv.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG));

			cv.drawText(text, width / 2, 20, paint);
			
			cv.save(Canvas.ALL_SAVE_FLAG);// 保存
			cv.restore();// 存储

			return new BitmapDrawable(newb);

		}
}
package com.joe.oil.activity;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.joe.oil.R;

import java.io.File;

/**
 * Created by liangxiaojiang on 2017/5/2.
 */
public class PDFviewActivity extends Activity implements OnPageChangeListener
        ,OnLoadCompleteListener, OnDrawListener {
    private PDFView pdfView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pdfview);
        pdfView = (PDFView) findViewById( R.id.pdfView );
//        displayFromFile1("http://192.168.1.9:8080/gas/commons/attachment/download/3630", "00100000224821.pdf");
        //从assets目录读取pdf
//        displayFromAssets("bao.pdf");

        //从文件中读取pdf
        displayFromFile( new File( getIntent().getStringExtra("fileName")));
    }

    /**
     * 获取打开网络的pdf文件
//     * @param fileUrl
//     * @param fileName
     */
//    private void displayFromFile1( String fileUrl ,String fileName) {
//        pdfView.fileFromLocalStorage(this,this,this,fileUrl,fileName);   //设置pdf文件地址
//
//    }

    private void displayFromFile( File file ) {
        pdfView.fromFile(file)   //设置pdf文件地址
                .defaultPage(1)         //设置默认显示第1页
                .onPageChange(this)     //设置翻页监听
                .onLoad(this)           //设置加载监听
                .onDraw(this)            //绘图监听
                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                .enableSwipe(true)  //是否允许翻页，默认是允许翻
                .swipeVertical( false )  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                // .pages( 2 ，5  )  //把2  5 过滤掉
                .load();
    }

    private void displayFromAssets(String assetFileName ) {
        pdfView.fromAsset(assetFileName)   //设置pdf文件地址
                .defaultPage(1)         //设置默认显示第1页
                .onPageChange(this)     //设置翻页监听
                .onLoad(this)           //设置加载监听
                .onDraw(this)            //绘图监听
                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                .enableSwipe(true)   //是否允许翻页，默认是允许翻页
//                 .pages()  //把 5 过滤掉
                .load();
    }

    /**
     * 翻页回调
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
        Toast.makeText( PDFviewActivity.this , "page= " + page +
                " pageCount= " + pageCount , Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载完成回调
     * @param nbPages  总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
        Toast.makeText( PDFviewActivity.this ,  "加载完成" + nbPages  , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
    }
}

package com.joe.oil.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Window;
import android.widget.TextView;

import com.joe.oil.R;

import org.textmining.text.extraction.WordExtractor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by liangxiaojiang on 2017/5/4.
 */
public class TxtReadActivity extends Activity {

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_txtread);

        textView = (TextView) findViewById(R.id.txtread_text);

        String path = getIntent().getStringExtra("fileName");
        if (path.endsWith(".txt")) {
            try {
                File urlFile = new File(path);
                InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "GB2312");
            /*
            如果打开文件是乱码，这个是因为txt格式的默认编码是ANSI，这种的就得用GB2312来打开，要是txt格式的编码是utf-8，这块就得更改为utf-8
             */
                BufferedReader br = new BufferedReader(isr);
                String str = "";
                String mimeTypeLine = null;
                while ((mimeTypeLine = br.readLine()) != null) {
                    //分行读取
                    str += mimeTypeLine + "\n";
//                str = str + mimeTypeLine;
                }
                textView.setText(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.endsWith(".doc")) {
            String str = readDOC(path);
            textView.setText(str.trim().replace("/r", ""));
        }else if (path.endsWith(".docx")){
            String str = readDOCX(path);
            textView.setText(str);
        }else if (path.endsWith(".xls")){
            String str=readXLS(path);
            textView.setText(str);
        }else if (path.endsWith(".xlsx")){
            String str=readXLSX(path);
            textView.setText(str);
        }
    }

    public  String readDOCX(String path) {
        String river = "";
        try {
            ZipFile xlsxFile = new ZipFile(new File(path));
            ZipEntry sharedStringXML = xlsxFile.getEntry("word/document.xml");
            InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParser.getName();
                        System.out.println(tag);
                        if (tag.equalsIgnoreCase("t")) {
                            river += xmlParser.nextText() + "\n";
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                evtType = xmlParser.next();
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        if (river == null) {
            river = "解析文件出现问题,请联系管理员使用word编写文件，不能使用wps打开或者编写文件";
        }

        return river;
    }

    public String readXLS(String path) {
        String str = "";
        try {
            Workbook workbook = null;
            workbook = Workbook.getWorkbook(new File(path));
            Sheet sheet = workbook.getSheet(0);
            Cell cell = null;
            int columnCount = sheet.getColumns();
            int rowCount = sheet.getRows();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    cell = sheet.getCell(j, i);
                    String temp2 = "";
                    if (cell.getType() == CellType.NUMBER) {
                        temp2 = ((NumberCell) cell).getValue() + "";
                    } else if (cell.getType() == CellType.DATE) {
                        temp2 = "" + ((DateCell) cell).getDate();
                    } else {
                        temp2 = "" + cell.getContents();
                    }
                    str = str + "  " + temp2;
                }
                str += "\n";
            }
            workbook.close();
        } catch (Exception e) {
        }
        if (str == null) {
            str = "解析文件出现问题,请联系管理员使用word编写文件，不能使用wps打开或者编写文件";
        }
        return str;
    }

    public String readXLSX(String path) {
        String str = "";
        String v = null;
        boolean flat = false;
        List<String> ls = new ArrayList<String>();
        try {
            ZipFile xlsxFile = new ZipFile(new File(path));
            ZipEntry sharedStringXML = xlsxFile
                    .getEntry("xl/sharedStrings.xml");
            InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParser.getName();
                        if (tag.equalsIgnoreCase("t")) {
                            ls.add(xmlParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                evtType = xmlParser.next();
            }
            ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/sheet1.xml");
            InputStream inputStreamsheet = xlsxFile.getInputStream(sheetXML);
            XmlPullParser xmlParsersheet = Xml.newPullParser();
            xmlParsersheet.setInput(inputStreamsheet, "utf-8");
            int evtTypesheet = xmlParsersheet.getEventType();
            while (evtTypesheet != XmlPullParser.END_DOCUMENT) {
                switch (evtTypesheet) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParsersheet.getName();
                        if (tag.equalsIgnoreCase("row")) {
                        } else if (tag.equalsIgnoreCase("c")) {
                            String t = xmlParsersheet.getAttributeValue(null, "t");
                            if (t != null) {
                                flat = true;
                                System.out.println(flat + "有");
                            } else {
                                System.out.println(flat + "没有");
                                flat = false;
                            }
                        } else if (tag.equalsIgnoreCase("v")) {
                            v = xmlParsersheet.nextText();
                            if (v != null) {
                                if (flat) {
                                    str += ls.get(Integer.parseInt(v)) + "  ";
                                } else {
                                    str += v + "  ";
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlParsersheet.getName().equalsIgnoreCase("row")
                                && v != null) {
                            str += "\n";
                        }
                        break;
                }
                evtTypesheet = xmlParsersheet.next();
            }
            System.out.println(str);
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        if (str == null) {
            str = "解析文件出现问题,请联系管理员使用word编写文件，不能使用wps打开或者编写文件";
        }
        return str;
    }

    public  String readDOC(String path) {
        // 创建输入流读取doc文件
        FileInputStream in;
        String text = null;
        // Environment.getExternalStorageDirectory().getAbsolutePath()+ "/aa.doc")
        try {
            in = new FileInputStream(new File(path));
            int a= in.available();
            WordExtractor extractor = null;
            // 创建WordExtractor
            extractor = new WordExtractor();
            // 对doc文件进行提取
            text = extractor.extractText(in);
            System.out.println("解析得到的东西"+text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (text == null) {
            text = "解析文件出现问题,请联系管理员使用word编写文件，不能使用wps打开或者编写文件";
        }
//        textView.setText(text);
        return text;
    }
}
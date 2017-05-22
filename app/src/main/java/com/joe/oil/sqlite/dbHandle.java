package com.joe.oil.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.joe.oil.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class dbHandle {
	// 得到SD卡路径
	@SuppressLint("SdCardPath")
	private static final String DATABASE_PATH = "/data/data/com.joe.oil/databases";
	private static Context mContet;
	// 数据库名
	private static final String DATABASE_FILENAME = "oil.db";
	private static SQLiteDatabase _db = null;

	public static SQLiteDatabase getInstance(Context context) {
		mContet = context;
		if (_db == null) {
			_db = openDatabase();
		}
		return _db;
	}

	// 得到操作数据库的对象d
	public static SQLiteDatabase openDatabase() {
		try {
			// 得到数据库的完整路径名
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			// 将数据库文件从资源文件放到合适地方（资源文件也就是数据库文件放在项目的res下的raw目录中）
			// 将数据库文件复制到SD卡中
			File dir = new File(DATABASE_PATH);
			if (!dir.exists())
				dir.mkdir();
			// 判断是否存在该文件
			if (!(new File(databaseFilename)).exists()) {
				// 不存在得到数据库输入流对象
				InputStream is = mContet.getResources().openRawResource(R.raw.oil);
				// 创建输出流
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				// 将数据输出
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				// 关闭资源
				fos.close();
				is.close();
			}
			// 得到SQLDatabase对象
			_db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
			return _db;
		} catch (Exception e) {
			_db.close();
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static void Close() {
		// TODO Auto-generated method stub
		
		if(_db!=null || _db.isOpen())
		{
			_db.close();
		}
	}
}
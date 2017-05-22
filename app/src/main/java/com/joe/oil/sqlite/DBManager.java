package com.joe.oil.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager
{
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     * 
     * @param persons
     */
    public void add(List<sqlit> persons)
    {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (sqlit person : persons)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
                        + " VALUES(?, ?, ?)", new Object[] { person.date,
                        person.name, person.isselct});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }

    /**
     * update person's age
     * 
     * @param person
     */
    public void updateAge(sqlit person)
    {
        ContentValues cv = new ContentValues();
        cv.put("isselct", person.isselct);
        db.update(DatabaseHelper.TABLE_NAME, cv, "isselct = ?",
                new String[] { person.isselct });
    }

    /**
     * delete old person
     * 
     * @param person
     */
    public void deleteOldPerson(sqlit person)
    {
        db.delete(DatabaseHelper.TABLE_NAME, "isselct= ?",
                new String[] { String.valueOf(person.isselct) });
    }

    /**
     * query all persons, return list
     * 
     * @return List<Person>
     */
    public List<sqlit> query(String name)//这边是行参，从上页通过实参传过来的值
    {
        ArrayList<sqlit> persons = new ArrayList<sqlit>();
        Cursor c = queryTheCursor(name);
        while (c.moveToNext())
        {
        	sqlit person = new sqlit();
            person.date = c.getString(c.getColumnIndex("date1"));
            person.isselct = c.getString(c.getColumnIndex("isselct"));
            person.name=c.getString(c.getColumnIndex("name"));//用户名
            persons.add(person);
        }
        c.close();
        return persons;
    }



    /**
     * query all persons, return cursor
     * 
     * @return Cursor
     */
    public Cursor queryTheCursor(String name)//这是从上页传过的值，

    {
        //SqlLite语句时，因为有的字段需要加单引号，不加单引号就会报sql语句错误
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME+" WHERE name = '"+name+"'",
                null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB()
    {
        // 释放数据库资源
        db.close();
    }

}

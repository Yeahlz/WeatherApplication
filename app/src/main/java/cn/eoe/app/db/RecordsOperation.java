package cn.eoe.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class RecordsOperation {        // 搜索时进行数据库操作
    RecordSQLiteOpenHelper recordSQLiteOpenHelper;
    SQLiteDatabase recordsdatabase;

    public  RecordsOperation(Context context){        // 建立数据库
        recordSQLiteOpenHelper =new RecordSQLiteOpenHelper(context);
    }

    public void addRecords(String record){    // 添加记录
        if(!isRecord(record)){
            recordsdatabase= recordSQLiteOpenHelper.getReadableDatabase();  //需要通过getReadableDatabase或getWriteableDatabase 才能对数据库进行操作
            ContentValues contentValues = new ContentValues();
            contentValues.put("name",record);
            recordsdatabase.insert("records",null,contentValues);
            recordsdatabase.close();
        }
    }

    public boolean isRecord(String record){  // 判断是否含有该记录
        boolean isrecord = false;
        recordsdatabase= recordSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = recordsdatabase.query("records",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            if(record.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))){
                isrecord = true;
            }
        }
        recordsdatabase.close();
        cursor.close();
        return isrecord;
    }

    public List<String> getRecordsList (){   // 获取全部记录
        List<String> recordsList = new ArrayList<>();
        recordsdatabase =recordSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = recordsdatabase.query("records",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            recordsList.add(name);
        }
        recordsdatabase.close();
        cursor.close();
        return recordsList;
    }

    public void deleteRecordsList(){   // 删除记录
        recordsdatabase =recordSQLiteOpenHelper.getReadableDatabase();
        recordsdatabase.execSQL("delete from records");
        recordsdatabase.close();
    }

    public List<String> getsimlarRecords (String record){   //  模糊查询
        List<String> simlarRecords  = new ArrayList<>();
        recordsdatabase= recordSQLiteOpenHelper.getReadableDatabase();
        String sql = "select * from records where name like '%" + record + "%' order by name ";  //根据输入内容匹配之前搜索记录
        Cursor cursor = recordsdatabase.rawQuery(sql,null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            simlarRecords .add(name);
        }
        recordsdatabase.close();
        cursor.close();
        return  simlarRecords;
    }
}



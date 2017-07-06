package cn.eoe.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/5/13.
 */

public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {    // 数据库帮助类创建数据库
    private static final String Helper = "db";
    private static final int Version = 1;
    public static final String sql="create table records("
            + "id integer primary key autoincrement, " + "name text)";
    public RecordSQLiteOpenHelper(Context context) {
        super(context, Helper, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

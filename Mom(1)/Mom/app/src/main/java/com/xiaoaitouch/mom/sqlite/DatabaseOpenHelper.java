package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiaoaitouch.mom.util.Utils;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private Context context;
    // 数据库名称
    private static final String DATABASE_NAME = "xa_data.db";
    // 数据库版本  
    public static final int DATABASE_VERSION = Utils.getVersionCode();

    //创建所有表语句    数组格式
    private static String[] TABLE_CREATE_ALL = {BeaconTables.CREATE_TABLE, NoteTables.CREATE_TABLE, UserTables.CREATE_TABLE,
            SportTables.CREATE_TABLE, AddToolsTables.CREATE_TABLE, DueTimeTables.CREATE_TABLE, RecordContractionTables.CREATE_TABLE,
            FetalMovementTables.CREATE_TABLE, MeasureHeartTables.CREATE_TABLE};

    //所有表名    数组格式		
    private static String[] TABLE_NAME_ALL = {BeaconTables.TABLE_NAME, NoteTables.TABLE_NAME, UserTables.TABLE_NAME,
            SportTables.TABLE_NAME, AddToolsTables.TABLE_NAME, DueTimeTables.TABLE_NAME, RecordContractionTables.TABLE_NAME,
            FetalMovementTables.TABLE_NAME, MeasureHeartTables.TABLE_NAME};


    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public DatabaseOpenHelper(Context context, int dataVersion) {
        super(context, DATABASE_NAME, null, dataVersion);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < TABLE_CREATE_ALL.length; i++) {
            db.execSQL(TABLE_CREATE_ALL[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.deleteDB(db);
        this.onCreate(db);
    }


    // 删除表
    public void deleteDB(SQLiteDatabase db) {
        for (int i = 0; i < TABLE_NAME_ALL.length; i++) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ALL[i]);
        }
    }


}

package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.xiaoaitouch.mom.module.SportTabModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/24.
 */
public class SportTables {
    public static final String TABLE_NAME = "SportTable";                //表名
    public static final int ROW_COUNTS = 5;                            //表列数，更新数据包，添加表数据用(不包括自增ID)


    public static final String id = "ID";
    public static final String userId = "USERID";
    public static final String sportDate = "SPORTDATE";
    public static final String stepNumber = "STEPNUMBER";
    public static final String km = "KM";
    public static final String calorie = "CALORIE";
    public static final String time = "TIME";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("    //创建该表SQL语句
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + userId + " LONG ,"
            + sportDate + " TEXT ,"
            + stepNumber + " INTEGER ,"
            + km + " Float ,"
            + calorie + " Float ,"
            + time + " Float "
            + ");";

    /**
     * 添加今天的运动数据
     *
     * @param context
     * @param sportTabModule
     */
    public static void addSportTabModule(Context context, SportTabModule sportTabModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, sportDate, stepNumber, km
                , calorie, time};

        String[] arrayValues = {sportTabModule.getUserId() + "", sportTabModule.getSportDate(),
                sportTabModule.getStepNumber() + "", sportTabModule.getKm() + "",
                sportTabModule.getCalorie() + "", sportTabModule.getTime() + ""};
        dbHelper.insert(TABLE_NAME, arrayKey, arrayValues);
        dbHelper.openHelper.close();
    }

    /**
     * 查询今天的运动数据
     *
     * @param context
     * @return
     */
    public static SportTabModule querySportTabModule(Context context, long userIds, String date) {
        DBHelper dbHelper = new DBHelper(context);
        SportTabModule sportTabModule = null;
        Cursor cursor = dbHelper.query("select * from " + TABLE_NAME + " WHERE " + userId + "='" + userIds + "' AND sportDate ='" + date + "';");
        while (cursor.moveToNext()) {
            sportTabModule = new SportTabModule();
            sportTabModule.setCalorie(cursor.getFloat(cursor
                    .getColumnIndex(SportTables.calorie)));
            sportTabModule.setKm(cursor.getFloat(cursor
                    .getColumnIndex(SportTables.km)));
            sportTabModule.setSportDate(cursor.getString(cursor
                    .getColumnIndex(SportTables.sportDate)));
            sportTabModule.setTime(cursor.getFloat(cursor
                    .getColumnIndex(SportTables.time)));
            sportTabModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(SportTables.userId)));
        }
        cursor.close();
        dbHelper.openHelper.close();
        return sportTabModule;
    }

    /**
     * 查询不等于今天的运动数据
     *
     * @param context
     * @return
     */
    public static  List<SportTabModule> queryAllSportTabModule(Context context, long userId, String date) {
        List<SportTabModule> moduleList = new ArrayList<SportTabModule>();
        DBHelper dbHelper = new DBHelper(context);
        SportTabModule sportTabModule = null;
        Cursor cursor = dbHelper.query("select * from " + TABLE_NAME + " WHERE " + id + "='" + userId + "' AND sportDate !='" + date + "';");
        while (cursor.moveToNext()) {
            sportTabModule = new SportTabModule();
            sportTabModule.setCalorie(cursor.getFloat(cursor
                    .getColumnIndex(SportTables.calorie)));
            sportTabModule.setKm(cursor.getFloat(cursor
                    .getColumnIndex(SportTables.km)));
            sportTabModule.setSportDate(cursor.getString(cursor
                    .getColumnIndex(SportTables.sportDate)));
            sportTabModule.setTime(cursor.getFloat(cursor
                    .getColumnIndex(SportTables.time)));
            sportTabModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(SportTables.userId)));
            moduleList.add(sportTabModule);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return moduleList;
    }

    /**
     * 修改今天的运动信息
     *
     * @param context
     * @param sportTabModule
     */
    public static void updateUser(Context context, SportTabModule sportTabModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, sportDate, stepNumber, km
                , calorie, time};

        String[] arrayValues = {sportTabModule.getUserId() + "", sportTabModule.getSportDate(),
                sportTabModule.getStepNumber() + "", sportTabModule.getKm() + "",
                sportTabModule.getCalorie() + "", sportTabModule.getTime() + ""};

        String[] whereArrayKey = {userId, sportDate};
        String[] whereArrayValues = {sportTabModule.getUserId() + "", sportTabModule.getSportDate()};
        dbHelper.update(TABLE_NAME, arrayKey, arrayValues, whereArrayKey, whereArrayValues);
        dbHelper.openHelper.close();
    }

    /**
     * 删除这张表
     *
     * @param context
     */
    public static void deletesportTabModule(Context context, long userId, String date) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.delete("DELETE FROM " + TABLE_NAME + " WHERE " + id + "='" + userId + "' AND sportDate !='" + date + "';");
        dbHelper.openHelper.close();
    }

}

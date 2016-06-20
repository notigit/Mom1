package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.xiaoaitouch.mom.module.FetalMovementModule;
import com.xiaoaitouch.mom.module.MeasureHeartModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: 心率
 * User: huxin
 * Date: 2016/3/4
 * Time: 13:42
 * FIXME
 */
public class MeasureHeartTables {
    public static final String TABLE_NAME = "MeasureHeartTable";                //表名
    public static final int ROW_COUNTS = 5;                            //表列数，更新数据包，添加表数据用(不包括自增ID)


    public static final String id = "ID";
    public static final String userId = "USERID";
    public static final String date = "DATE";
    public static final String number = "NUMBER";
    public static final String type = "TYPE";
    public static final String gweek = "GWEEK";
    public static final String createTime = "CREATETIME";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("    //创建该表SQL语句
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + userId + " LONG ,"
            + number + " INTEGER ,"
            + type + " INTEGER ,"
            + date + " TEXT ,"
            + gweek + " TEXT ,"
            + createTime + " TEXT "
            + ");";

    /**
     * 提交数据
     *
     * @param context
     * @param type
     * @return
     */
    public static List<MeasureHeartModule> submitMeasureHeartModule(Context context, long userId, int type) {
        List<MeasureHeartModule> dueTimeModuleList = new ArrayList<MeasureHeartModule>();
        DBHelper dbHelper = new DBHelper(context);
        String sql = "select * from " + TABLE_NAME + " WHERE userId = '" + userId + "'" + " AND type = '" + type + "'" + ";";
        Cursor cursor = dbHelper.query(sql);
        while (cursor.moveToNext()) {
            MeasureHeartModule fetalMovementModule = new MeasureHeartModule();
            fetalMovementModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(MeasureHeartTables.userId)));
            fetalMovementModule.setNumber(cursor.getInt(cursor
                    .getColumnIndex(MeasureHeartTables.number)));
            fetalMovementModule.setType(cursor.getInt(cursor
                    .getColumnIndex(MeasureHeartTables.type)));
            fetalMovementModule.setDate(cursor.getString(cursor
                    .getColumnIndex(MeasureHeartTables.date)));
            fetalMovementModule.setGweek(cursor.getString(cursor
                    .getColumnIndex(MeasureHeartTables.gweek)));
            fetalMovementModule.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(MeasureHeartTables.createTime)));
            dueTimeModuleList.add(fetalMovementModule);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return dueTimeModuleList;
    }

    public static void updateMeasureHeartModule(Context context, MeasureHeartModule measureHeartModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, number, type, date, gweek, createTime};

        String[] arrayValues = {measureHeartModule.getUserId() + "",
                measureHeartModule.getNumber() + "", measureHeartModule.getType() + "", measureHeartModule.getDate(),
                measureHeartModule.getGweek(), measureHeartModule.getCreateTime()};
        String[] whereArrayKey = {userId, type, createTime};
        String[] whereArrayValues = {measureHeartModule.getUserId() + "", measureHeartModule.getType() + "", measureHeartModule.getCreateTime()};
        dbHelper.update(TABLE_NAME, arrayKey, arrayValues, whereArrayKey, whereArrayValues);
        dbHelper.openHelper.close();
    }

    /**
     * 添加数据
     *
     * @param context
     * @param measureHeartModule
     */
    public static void addMeasureHeartModule(Context context, MeasureHeartModule measureHeartModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, number, type, date, gweek, createTime};

        String[] arrayValues = {measureHeartModule.getUserId() + "",
                measureHeartModule.getNumber() + "", measureHeartModule.getType() + "", measureHeartModule.getDate(),
                measureHeartModule.getGweek(), measureHeartModule.getCreateTime()};
        dbHelper.insert(TABLE_NAME, arrayKey, arrayValues);
        dbHelper.openHelper.close();
    }

    public static List<MeasureHeartModule> queryMeasureHeartModuleList(Context context, long userId) {
        List<MeasureHeartModule> dueTimeModuleList = new ArrayList<MeasureHeartModule>();
        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.orderQuery(TABLE_NAME, "userId", userId + "", "date", "desc");
        while (cursor.moveToNext()) {
            MeasureHeartModule fetalMovementModule = new MeasureHeartModule();
            fetalMovementModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(MeasureHeartTables.userId)));
            fetalMovementModule.setNumber(cursor.getInt(cursor
                    .getColumnIndex(MeasureHeartTables.number)));
            fetalMovementModule.setType(cursor.getInt(cursor
                    .getColumnIndex(MeasureHeartTables.type)));
            fetalMovementModule.setDate(cursor.getString(cursor
                    .getColumnIndex(MeasureHeartTables.date)));
            fetalMovementModule.setGweek(cursor.getString(cursor
                    .getColumnIndex(MeasureHeartTables.gweek)));
            fetalMovementModule.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(MeasureHeartTables.createTime)));
            dueTimeModuleList.add(fetalMovementModule);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return dueTimeModuleList;
    }

    /**
     * 按条件删除
     *
     * @param context
     * @param userId
     */
    public static void deleteMeasureHeartModule(Context context, long userId, String date) {
        DBHelper dbHelper = new DBHelper(context);
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE userId = '" + userId + "'" + " AND date = '" + date + "'" + ";";
        dbHelper.delete(sql);
        dbHelper.openHelper.close();
    }

    /**
     * 删除这张表
     *
     * @param context
     */
    public static void deleteMeasureHeartModule(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.delete("DELETE FROM " + TABLE_NAME);
        dbHelper.openHelper.close();
    }

}

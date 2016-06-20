package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.xiaoaitouch.mom.module.FetalMovementModule;
import com.xiaoaitouch.mom.module.RecordContractionsModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: 记宫缩
 * User: huxin
 * Date: 2016/3/4
 * Time: 13:42
 * FIXME
 */
public class FetalMovementTables {
    public static final String TABLE_NAME = "FetalMovementTable";                //表名
    public static final int ROW_COUNTS = 5;                            //表列数，更新数据包，添加表数据用(不包括自增ID)


    public static final String id = "ID";
    public static final String userId = "USERID";
    public static final String date = "DATE";
    public static final String startTime = "STARTTIME";
    public static final String number = "NUMBER";
    public static final String createTime = "CREATETIME";
    public static final String type = "TYPE";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("    //创建该表SQL语句
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + userId + " LONG ,"
            + number + " INTEGER ,"
            + type + " INTEGER ,"
            + date + " TEXT ,"
            + createTime + " TEXT ,"
            + startTime + " TEXT "
            + ");";

    /**
     * 提交数据
     *
     * @param context
     * @param type
     * @return
     */
    public static List<FetalMovementModule> submitFetalMovementModule(Context context, long userId, int type) {
        List<FetalMovementModule> dueTimeModuleList = new ArrayList<FetalMovementModule>();
        DBHelper dbHelper = new DBHelper(context);
        String sql = "select * from " + TABLE_NAME + " WHERE userId = '" + userId + "'" + " AND type = '" + type + "'" + ";";
        Cursor cursor = dbHelper.query(sql);
        while (cursor.moveToNext()) {
            FetalMovementModule fetalMovementModule = new FetalMovementModule();
            fetalMovementModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(FetalMovementTables.userId)));
            fetalMovementModule.setNumber(cursor.getInt(cursor
                    .getColumnIndex(FetalMovementTables.number)));
            fetalMovementModule.setType(cursor.getInt(cursor
                    .getColumnIndex(FetalMovementTables.type)));
            fetalMovementModule.setDate(cursor.getString(cursor
                    .getColumnIndex(FetalMovementTables.date)));
            fetalMovementModule.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(FetalMovementTables.createTime)));
            fetalMovementModule.setStartTime(cursor.getString(cursor
                    .getColumnIndex(FetalMovementTables.startTime)));
            dueTimeModuleList.add(fetalMovementModule);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return dueTimeModuleList;
    }

    /**
     * 添加数据
     *
     * @param context
     * @param fetalMovementModule
     */
    public static void addFetalMovementModule(Context context, FetalMovementModule fetalMovementModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, number, type, date, createTime, startTime};

        String[] arrayValues = {fetalMovementModule.getUserId() + "",
                fetalMovementModule.getNumber() + "", fetalMovementModule.getType() + "",
                fetalMovementModule.getDate(), fetalMovementModule.getCreateTime(), fetalMovementModule.getStartTime()};
        dbHelper.insert(TABLE_NAME, arrayKey, arrayValues);
        dbHelper.openHelper.close();
    }

    public static void updateFetalMovementModule(Context context, FetalMovementModule fetalMovementModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, number, type, date, createTime, startTime};

        String[] arrayValues = {fetalMovementModule.getUserId() + "",
                fetalMovementModule.getNumber() + "", fetalMovementModule.getType() + "",
                fetalMovementModule.getDate(), fetalMovementModule.getCreateTime(), fetalMovementModule.getStartTime()};
        String[] whereArrayKey = {userId, type, createTime};
        String[] whereArrayValues = {fetalMovementModule.getUserId() + "", fetalMovementModule.getType() + "", fetalMovementModule.getCreateTime()};
        dbHelper.update(TABLE_NAME, arrayKey, arrayValues, whereArrayKey, whereArrayValues);
        dbHelper.openHelper.close();
    }

    public static List<FetalMovementModule> queryFetalMovementModuleList(Context context, long userId) {
        List<FetalMovementModule> dueTimeModuleList = new ArrayList<FetalMovementModule>();
        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.orderQuery(TABLE_NAME, "userId", userId + "", "date", "desc");
        while (cursor.moveToNext()) {
            FetalMovementModule fetalMovementModule = new FetalMovementModule();
            fetalMovementModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(FetalMovementTables.userId)));
            fetalMovementModule.setNumber(cursor.getInt(cursor
                    .getColumnIndex(FetalMovementTables.number)));
            fetalMovementModule.setType(cursor.getInt(cursor
                    .getColumnIndex(FetalMovementTables.type)));
            fetalMovementModule.setDate(cursor.getString(cursor
                    .getColumnIndex(FetalMovementTables.date)));
            fetalMovementModule.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(FetalMovementTables.createTime)));
            fetalMovementModule.setStartTime(cursor.getString(cursor
                    .getColumnIndex(FetalMovementTables.startTime)));
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
     * @param startTime
     */
    public static void deleteFetalMovementModule(Context context, long userId, String startTime) {
        DBHelper dbHelper = new DBHelper(context);
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE userId = '" + userId + "'" + " AND startTime = '" + startTime + "'" + ";";
        dbHelper.delete(sql);
        dbHelper.openHelper.close();
    }

    /**
     * 删除这张表
     *
     * @param context
     */
    public static void deleteFetalMovementModule(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.delete("DELETE FROM " + TABLE_NAME);
        dbHelper.openHelper.close();
    }

}

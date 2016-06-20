package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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
public class RecordContractionTables {
    public static final String TABLE_NAME = "RecordContractionTable";                //表名
    public static final int ROW_COUNTS = 5;                            //表列数，更新数据包，添加表数据用(不包括自增ID)


    public static final String id = "ID";
    public static final String userId = "USERID";
    public static final String date = "DATE";
    public static final String startTime = "STARTTIME";
    public static final String type = "TYPE";
    public static final String cxTime = "CXTIME";//
    public static final String jgTime = "JGTIME";
    public static final String createTime = "CREATETIME";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("    //创建该表SQL语句
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + userId + " LONG ,"
            + type + " INTEGER ,"
            + cxTime + " INTEGER ,"
            + jgTime + " INTEGER ,"
            + createTime + " TEXT ,"
            + date + " TEXT ,"
            + startTime + " TEXT "
            + ");";

    /**
     * 提交数据
     *
     * @param context
     * @param type
     * @return
     */
    public static List<RecordContractionsModule> submitRecordContractionModule(Context context, long userId, int type) {
        List<RecordContractionsModule> dueTimeModuleList = new ArrayList<RecordContractionsModule>();
        DBHelper dbHelper = new DBHelper(context);
        String sql = "select * from " + TABLE_NAME + " WHERE userId = '" + userId + "'" + " AND type = '" + type + "'" + ";";
        Cursor cursor = dbHelper.query(sql);
        while (cursor.moveToNext()) {
            RecordContractionsModule recordContractionsModule = new RecordContractionsModule();
            recordContractionsModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(RecordContractionTables.userId)));
            recordContractionsModule.setType(cursor.getInt(cursor
                    .getColumnIndex(RecordContractionTables.type)));
            recordContractionsModule.setCxTime(cursor.getInt(cursor
                    .getColumnIndex(RecordContractionTables.cxTime)));
            recordContractionsModule.setJgTime(cursor.getInt(cursor
                    .getColumnIndex(RecordContractionTables.jgTime)));
            recordContractionsModule.setDate(cursor.getString(cursor
                    .getColumnIndex(RecordContractionTables.date)));
            recordContractionsModule.setStartTime(cursor.getString(cursor
                    .getColumnIndex(RecordContractionTables.startTime)));
            recordContractionsModule.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(RecordContractionTables.createTime)));
            dueTimeModuleList.add(recordContractionsModule);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return dueTimeModuleList;
    }

    public static void updateRecordContractionModule(Context context, RecordContractionsModule recordContractionsModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, type, cxTime, jgTime, createTime, date, startTime};

        String[] arrayValues = {recordContractionsModule.getUserId() + "", recordContractionsModule.getType() + "",
                recordContractionsModule.getCxTime() + "", recordContractionsModule.getJgTime() + "", recordContractionsModule.getCreateTime(), recordContractionsModule.getDate(),
                recordContractionsModule.getStartTime()};
        String[] whereArrayKey = {userId, type, createTime};
        String[] whereArrayValues = {recordContractionsModule.getUserId() + "", recordContractionsModule.getType() + "", recordContractionsModule.getCreateTime()};
        dbHelper.update(TABLE_NAME, arrayKey, arrayValues, whereArrayKey, whereArrayValues);
        dbHelper.openHelper.close();
    }

    /**
     * 添加数据
     *
     * @param context
     * @param recordContractionsModule
     */
    public static void addRecordContractionModule(Context context, RecordContractionsModule recordContractionsModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, type, cxTime, jgTime, createTime, date, startTime};

        String[] arrayValues = {recordContractionsModule.getUserId() + "", recordContractionsModule.getType() + "",
                recordContractionsModule.getCxTime() + "", recordContractionsModule.getJgTime() + "", recordContractionsModule.getCreateTime(), recordContractionsModule.getDate(),
                recordContractionsModule.getStartTime()};
        dbHelper.insert(TABLE_NAME, arrayKey, arrayValues);
        dbHelper.openHelper.close();
    }

    public static List<RecordContractionsModule> queryRecordContractionModuleList(Context context, long userId) {
        List<RecordContractionsModule> dueTimeModuleList = new ArrayList<RecordContractionsModule>();
        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.orderQuery(TABLE_NAME, "userId", userId + "", "date", "desc");
        while (cursor.moveToNext()) {
            RecordContractionsModule recordContractionsModule = new RecordContractionsModule();
            recordContractionsModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(RecordContractionTables.userId)));
            recordContractionsModule.setType(cursor.getInt(cursor
                    .getColumnIndex(RecordContractionTables.type)));
            recordContractionsModule.setCxTime(cursor.getInt(cursor
                    .getColumnIndex(RecordContractionTables.cxTime)));
            recordContractionsModule.setJgTime(cursor.getInt(cursor
                    .getColumnIndex(RecordContractionTables.jgTime)));
            recordContractionsModule.setDate(cursor.getString(cursor
                    .getColumnIndex(RecordContractionTables.date)));
            recordContractionsModule.setStartTime(cursor.getString(cursor
                    .getColumnIndex(RecordContractionTables.startTime)));
            recordContractionsModule.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(RecordContractionTables.createTime)));
            dueTimeModuleList.add(recordContractionsModule);
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
    public static void deleteRecordContractionModule(Context context, long userId, String startTime) {
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
    public static void deleteRecordContractionModule(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.delete("DELETE FROM " + TABLE_NAME);
        dbHelper.openHelper.close();
    }

}

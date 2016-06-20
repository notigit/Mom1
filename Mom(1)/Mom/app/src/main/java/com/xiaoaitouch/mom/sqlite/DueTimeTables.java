package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.xiaoaitouch.mom.module.AddToolsModule;
import com.xiaoaitouch.mom.module.DueTimeModule;
import com.xiaoaitouch.mom.module.SportTabModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: 产检时间表
 * User: huxin
 * Date: 2016/3/4
 * Time: 13:42
 * FIXME
 */
public class DueTimeTables {
    public static final String TABLE_NAME = "DueTimeTable";                //表名
    public static final int ROW_COUNTS = 5;                            //表列数，更新数据包，添加表数据用(不包括自增ID)


    public static final String id = "ID";
    public static final String userId = "USERID";
    public static final String week = "WEEK";
    public static final String date = "DATE";
    public static final String title = "TITLE";
    public static final String dueDate = "DUEDATE";
    public static final String type = "TYPE";
    public static final String day = "DAY";
    public static final String remindTime = "REMINDTIME";
    public static final String isSelect = "ISSELECT";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("    //创建该表SQL语句
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + userId + " LONG ,"
            + week + " INTEGER ,"
            + type + " INTEGER ,"
            + day + " INTEGER ,"
            + isSelect + " INTEGER ,"
            + date + " TEXT ,"
            + title + " TEXT ,"
            + dueDate + " TEXT ,"
            + remindTime + " TEXT "
            + ");";

    /**
     * 添加数据
     *
     * @param context
     * @param dueTimeModule
     */
    public static void addDueTimeTableModule(Context context, DueTimeModule dueTimeModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, week, type, day,isSelect, title, dueDate, remindTime};

        String[] arrayValues = {dueTimeModule.getUserId() + "", dueTimeModule.getWeek() + "", dueTimeModule.getType() + "", dueTimeModule.getDay() + "",dueTimeModule.getIsSelect() + "",
                dueTimeModule.getTitle(), dueTimeModule.getDueDate(), dueTimeModule.getRemindTime()};
        dbHelper.insert(TABLE_NAME, arrayKey, arrayValues);
        dbHelper.openHelper.close();
    }

    public static List<DueTimeModule> queryDueTimeTableModuleList(Context context, long userId) {
        List<DueTimeModule> dueTimeModuleList = new ArrayList<DueTimeModule>();
        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.orderQuery(TABLE_NAME, "userId", userId + "", "week", "asc");
        while (cursor.moveToNext()) {
            DueTimeModule dueTimeModules = new DueTimeModule();
            dueTimeModules.setUserId(cursor.getLong(cursor
                    .getColumnIndex(DueTimeTables.userId)));
            dueTimeModules.setType(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.type)));
            dueTimeModules.setTitle(cursor.getString(cursor
                    .getColumnIndex(DueTimeTables.title)));
            dueTimeModules.setDueDate(cursor.getString(cursor
                    .getColumnIndex(DueTimeTables.dueDate)));
            dueTimeModules.setRemindTime(cursor.getString(cursor
                    .getColumnIndex(DueTimeTables.remindTime)));
            dueTimeModules.setWeek(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.week)));
            dueTimeModules.setDay(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.day)));
            dueTimeModules.setIsSelect(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.isSelect)));
            dueTimeModuleList.add(dueTimeModules);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return dueTimeModuleList;
    }

    public static DueTimeModule queryDueTimeTableModule(Context context, long userId) {
        DBHelper dbHelper = new DBHelper(context);
        DueTimeModule dueTimeModules = new DueTimeModule();
        String sql = "select * from " + TABLE_NAME + " WHERE userId=" + userId + " and type=2";
        Cursor cursor = dbHelper.query(sql);
        while (cursor.moveToNext()) {
            dueTimeModules.setUserId(cursor.getLong(cursor
                    .getColumnIndex(DueTimeTables.userId)));
            dueTimeModules.setType(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.type)));
            dueTimeModules.setTitle(cursor.getString(cursor
                    .getColumnIndex(DueTimeTables.title)));
            dueTimeModules.setDueDate(cursor.getString(cursor
                    .getColumnIndex(DueTimeTables.dueDate)));
            dueTimeModules.setRemindTime(cursor.getString(cursor
                    .getColumnIndex(DueTimeTables.remindTime)));
            dueTimeModules.setWeek(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.week)));
            dueTimeModules.setDay(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.day)));
            dueTimeModules.setIsSelect(cursor.getInt(cursor
                    .getColumnIndex(DueTimeTables.isSelect)));
        }
        cursor.close();
        dbHelper.openHelper.close();
        return dueTimeModules;
    }

    public static void updatequeryDueTimeTableModule(Context context, DueTimeModule dueTimeModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, week, type, day,isSelect, title, dueDate, remindTime};

        String[] arrayValues = {dueTimeModule.getUserId() + "", dueTimeModule.getWeek() + "", dueTimeModule.getType() + "", dueTimeModule.getDay() + "",dueTimeModule.getIsSelect() + "",
                dueTimeModule.getTitle(), dueTimeModule.getDueDate(), dueTimeModule.getRemindTime()};
        dbHelper.update(TABLE_NAME, arrayKey, arrayValues, "userId", dueTimeModule.getUserId() + "", "week", dueTimeModule.getWeek() + "");
        dbHelper.openHelper.close();
    }

    /**
     * 删除这张表
     *
     * @param context
     */
    public static void deleteDueTimeTableModule(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.delete("DELETE FROM " + TABLE_NAME);
        dbHelper.openHelper.close();
    }

}

package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.xiaoaitouch.mom.module.AddToolsModule;

import java.util.ArrayList;
import java.util.List;

public class AddToolsTables {
    public static final String TABLE_NAME = "AddToolsTable";                //表名
    public static final int ROW_COUNTS = 5;                            //表列数，更新数据包，添加表数据用(不包括自增ID)


    public static final String id = "ID";
    public static final String userId = "USERID";
    public static final String name = "NAME";
    public static final String inform = "INFORM";
    public static final String indexs = "INDEXS";
    public static final String onOff = "ONOFF";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("    //创建该表SQL语句
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + userId + " LONG ,"
            + name + " TEXT ,"
            + inform + " TEXT ,"
            + indexs + " INTEGER ,"
            + onOff + " INTEGER "
            + ");";

    /**
     * 添加工具表
     *
     * @param context
     * @param addToolsModule
     */
    public static void addAddTools(Context context, AddToolsModule addToolsModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, name, inform, indexs, onOff};

        String[] arrayValues = {addToolsModule.getUserId() + "", addToolsModule.getName(),
                addToolsModule.getInform(), addToolsModule.getIndexs() + "", addToolsModule.getOnOff() + ""};
        dbHelper.insert(TABLE_NAME, arrayKey, arrayValues);
        dbHelper.openHelper.close();
    }

    /**
     * 查询工具表
     *
     * @param context
     * @return
     */
    public static List<AddToolsModule> queryAllAddTools(Context context, String userIds) {
        List<AddToolsModule> addToolsModules = new ArrayList<AddToolsModule>();
        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.orderQuery(TABLE_NAME, "userId", userIds, "indexs", "asc");
        while (cursor.moveToNext()) {
            AddToolsModule addToolsModule = new AddToolsModule();
            addToolsModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(AddToolsTables.userId)));
            addToolsModule.setName(cursor.getString(cursor
                    .getColumnIndex(AddToolsTables.name)));
            addToolsModule.setInform(cursor.getString(cursor
                    .getColumnIndex(AddToolsTables.inform)));
            addToolsModule.setIndexs(cursor.getInt(cursor
                    .getColumnIndex(AddToolsTables.indexs)));
            addToolsModule.setOnOff(cursor.getInt(cursor
                    .getColumnIndex(AddToolsTables.onOff)));
            addToolsModules.add(addToolsModule);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return addToolsModules;
    }


    /**
     * 查询添加的工具
     *
     * @param context
     * @return
     */
    public static List<AddToolsModule> queryAddTools(Context context, long userIds) {
        List<AddToolsModule> addToolsModules = new ArrayList<AddToolsModule>();
        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.query("select * from " + TABLE_NAME + " WHERE userId = '" + userIds + "'" + " AND onOff = 1");
        while (cursor.moveToNext()) {
            AddToolsModule addToolsModule = new AddToolsModule();
            addToolsModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(AddToolsTables.userId)));
            addToolsModule.setName(cursor.getString(cursor
                    .getColumnIndex(AddToolsTables.name)));
            addToolsModule.setInform(cursor.getString(cursor
                    .getColumnIndex(AddToolsTables.inform)));
            addToolsModule.setIndexs(cursor.getInt(cursor
                    .getColumnIndex(AddToolsTables.indexs)));
            addToolsModule.setOnOff(cursor.getInt(cursor
                    .getColumnIndex(AddToolsTables.onOff)));
            addToolsModules.add(addToolsModule);
        }
        cursor.close();
        dbHelper.openHelper.close();
        return addToolsModules;
    }

    /**
     * 修改工具
     *
     * @param context
     * @param addToolsModule
     */
    public static void updateAddTools(Context context, AddToolsModule addToolsModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, name, inform, indexs, onOff};

        String[] arrayValues = {addToolsModule.getUserId() + "", addToolsModule.getName(),
                addToolsModule.getInform(), addToolsModule.getIndexs() + "", addToolsModule.getOnOff() + ""};
        dbHelper.update(TABLE_NAME, arrayKey, arrayValues, "userId", addToolsModule.getUserId() + "", "indexs", addToolsModule.getIndexs() + "");
        dbHelper.openHelper.close();
    }

    /**
     * 删除这张表
     *
     * @param context
     */
    public static void deleteAddTools(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.delete(TABLE_NAME, "", "");
        dbHelper.openHelper.close();
    }

}

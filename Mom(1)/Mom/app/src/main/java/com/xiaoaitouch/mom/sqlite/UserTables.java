package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.database.Cursor;

import com.xiaoaitouch.mom.module.UserModule;

public class UserTables {
    public static final String TABLE_NAME = "UserTable";                //表名
    public static final int ROW_COUNTS = 5;                            //表列数，更新数据包，添加表数据用(不包括自增ID)


    public static final String id = "ID";
    public static final String userId = "USERID";
    public static final String age = "AGE";
    public static final String height = "HEIGHT";
    public static final String neckname = "NECKNAME";
    public static final String dueTime = "DUETIME";
    public static final String lastMensesTime = "LASTMENSESTIME";
    public static final String createTime = "CREATETIME";
    public static final String uniqueness = "UNIQUENESS";
    public static final String userName = "USERNAME";
    public static final String headPic = "HEADPIC";
    public static final String weight = "WEIGHT";
    public static final String pwd = "PWD";
    public static final String mensesCircle = "MENSESCIRCLE";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("    //创建该表SQL语句
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + userId + " LONG ,"
            + age + " TEXT ,"
            + height + " TEXT ,"
            + neckname + " TEXT ,"
            + dueTime + " TEXT ,"
            + lastMensesTime + " TEXT ,"
            + createTime + " TEXT ,"
            + uniqueness + " TEXT ,"
            + userName + " TEXT ,"
            + headPic + " TEXT ,"
            + pwd + " TEXT ,"
            + weight + " DOUBLE ,"
            + mensesCircle + " INTEGER "
            + ");";

    /**
     * 添加用户数据
     *
     * @param context
     * @param userModule
     */
    public static void addUser(Context context, UserModule userModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, age, height, neckname
                , dueTime, lastMensesTime, createTime, uniqueness,
                userName, headPic, pwd, weight, mensesCircle};

        String[] arrayValues = {userModule.getUserId() + "", userModule.getAge(),
                userModule.getHeight(), userModule.getNeckname(),
                userModule.getDueTime(), userModule.getLastMensesTime(),
                userModule.getCreateTime(), userModule.getUniqueness(),
                userModule.getUserName(), userModule.getHeadPic(),
                userModule.getPwd(), userModule.getWeight() + "",
                userModule.getMensesCircle() + ""};
        dbHelper.insert(TABLE_NAME, arrayKey, arrayValues);
        dbHelper.openHelper.close();
    }

    /**
     * 查询用户数据
     *
     * @param context
     * @return
     */
    public static UserModule queryUser(Context context) {
        UserModule userModule = null;
        DBHelper dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.query(TABLE_NAME, null, null);
        while (cursor.moveToNext()) {
            userModule = new UserModule();
            userModule.setAge(cursor.getString(cursor
                    .getColumnIndex(UserTables.age)));
            userModule.setCreateTime(cursor.getString(cursor
                    .getColumnIndex(UserTables.createTime)));
            userModule.setDueTime(cursor.getString(cursor
                    .getColumnIndex(UserTables.dueTime)));
            userModule.setHeadPic(cursor.getString(cursor
                    .getColumnIndex(UserTables.headPic)));
            userModule.setHeight(cursor.getString(cursor
                    .getColumnIndex(UserTables.height)));
            userModule.setLastMensesTime(cursor.getString(cursor
                    .getColumnIndex(UserTables.lastMensesTime)));
            userModule.setNeckname(cursor.getString(cursor
                    .getColumnIndex(UserTables.neckname)));
            userModule.setPwd(cursor.getString(cursor
                    .getColumnIndex(UserTables.pwd)));
            userModule.setUserName(cursor.getString(cursor
                    .getColumnIndex(UserTables.userName)));
            userModule.setUserId(cursor.getLong(cursor
                    .getColumnIndex(UserTables.userId)));
            userModule.setWeight(cursor.getDouble(cursor
                    .getColumnIndex(UserTables.weight)));
            userModule.setMensesCircle(cursor.getInt(cursor
                    .getColumnIndex(UserTables.mensesCircle)));

        }
        cursor.close();
        dbHelper.openHelper.close();
        return userModule;
    }

    /**
     * 修改用户信息
     *
     * @param context
     * @param userModule
     */
    public static void updateUser(Context context, UserModule userModule) {
        DBHelper dbHelper = new DBHelper(context);
        String[] arrayKey = {userId, age, height, neckname
                , dueTime, lastMensesTime, createTime, uniqueness,
                userName, headPic, pwd, weight, mensesCircle};

        String[] arrayValues = {userModule.getUserId() + "", userModule.getAge(),
                userModule.getHeight(), userModule.getNeckname(),
                userModule.getDueTime(), userModule.getLastMensesTime(),
                userModule.getCreateTime(), userModule.getUniqueness(),
                userModule.getUserName(), userModule.getHeadPic(),
                userModule.getPwd(), userModule.getWeight() + "",
                userModule.getMensesCircle() + ""};
        dbHelper.update(TABLE_NAME, arrayKey, arrayValues, "userId", userModule.getUserId() + "");
        dbHelper.openHelper.close();
    }

    /**
     * 删除这张表
     *
     * @param context
     */
    public static void deleteUser(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.delete(TABLE_NAME, "", "");
        dbHelper.openHelper.close();
    }

}

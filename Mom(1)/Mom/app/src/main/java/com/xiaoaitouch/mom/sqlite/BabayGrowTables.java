package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xiaoaitouch.mom.module.BabayGrow;
import com.xiaoaitouch.mom.module.BabayWeight;
import com.xiaoaitouch.mom.module.BcModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*  孕期数据库 */
public class BabayGrowTables {

    //本地数据库名字
    private static final String db_name = "baby_grow";
    private static final String db_name_weight = "baby_weight";
    private static final String db_name_bc = "bc";
    private static final String db_name_bc_js = "bc_js";
    private static final String db_file_name = "baby_grow.db";

    //数据库存储路径
    private static final String filePath = "data/data/com.xiaoaitouch.mom/grow.db";
    //数据库存放的文件夹 data/data/com.main.jh 下面
    private static final String pathStr = "data/data/com.xiaoaitouch.mom";

    private SQLiteDatabase database;
    private Context context;
    private ArrayList<BabayGrow> arrayList;


    public BabayGrowTables(Context con) {
        this.context = con;
    }

    public SQLiteDatabase openDatabase(Context context) {
        System.out.println("filePath:" + filePath);
        File jhPath = new File(filePath);
        //查看数据库文件是否存在
        if (jhPath.exists()) {
            //存在则直接返回打开的数据库
            return SQLiteDatabase.openOrCreateDatabase(jhPath, null);
        } else {
            //不存在先创建文件夹
            File path = new File(pathStr);
            if (path.mkdir()) {
                System.out.println("创建成功");
            } else {
                System.out.println("创建失败");
            }

            try {
                //得到资源
                AssetManager am = context.getAssets();
                //得到数据库的输入流
                InputStream is = am.open(db_file_name);
                //用输出流写到SDcard
                FileOutputStream fos = new FileOutputStream(jhPath);
                //创建byte数组  1KB写一次
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return openDatabase(context);
        }
    }

    /**
     * 获取所有数据
     * <>
     *
     * @author
     */
    public ArrayList<BabayGrow> getAllData() {
        arrayList = new ArrayList<BabayGrow>();

        SQLiteDatabase db = openDatabase(context);
        try {
            Cursor cursor = db.rawQuery("select * from " + db_name + " order by week desc", null);
            while (cursor.moveToNext()) {
                BabayGrow info = new BabayGrow();
                info.setWeek(cursor.getInt(cursor.getColumnIndex("week")));
                info.setFw(cursor.getString(cursor.getColumnIndex("fw")));
                info.setGgc(cursor.getString(cursor.getColumnIndex("ggc")));
                info.setSdj(cursor.getString(cursor.getColumnIndex("sdj")));
                info.setFw1(cursor.getString(cursor.getColumnIndex("fw1")));
                info.setGgc1(cursor.getString(cursor.getColumnIndex("ggc1")));
                info.setSdj1(cursor.getString(cursor.getColumnIndex("sdj1")));
                arrayList.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    /**
     * 获取指定周期数据
     *
     * @param week 周期数
     * @author
     */
    public BabayGrow getWeekDate(int week) {
        BabayGrow info = new BabayGrow();

        SQLiteDatabase db = openDatabase(context);
        try {
            Cursor cursor = db.rawQuery("select * from " + db_name + " where week = " + week + ";", null);
            while (cursor.moveToNext()) {
                info.setWeek(cursor.getInt(cursor.getColumnIndex("week")));
                info.setFw(cursor.getString(cursor.getColumnIndex("fw")));
                info.setGgc(cursor.getString(cursor.getColumnIndex("ggc")));
                info.setSdj(cursor.getString(cursor.getColumnIndex("sdj")));
                info.setFw1(cursor.getString(cursor.getColumnIndex("fw1")));
                info.setGgc1(cursor.getString(cursor.getColumnIndex("ggc1")));
                info.setSdj1(cursor.getString(cursor.getColumnIndex("sdj1")));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }


    /**
     * 获取指定周期数据
     *
     * @param week 周期数
     * @author
     */
    public BabayWeight getBabayWeightWeekDate(int week, int sex) {
        BabayWeight info = new BabayWeight();
        SQLiteDatabase db = openDatabase(context);
        try {
            String sql = "select * from " + db_name_weight + " where week = " + week + " and sex " + " = " + sex + ";";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                info.setWeek(cursor.getInt(cursor.getColumnIndex("week")));
                info.setSex(cursor.getInt(cursor.getColumnIndex("sex")));
                info.setFifty(cursor.getInt(cursor.getColumnIndex("fifty")));
                info.setNinety(cursor.getInt(cursor.getColumnIndex("ninety")));
                info.setSeventyFive(cursor.getInt(cursor.getColumnIndex("seventyFive")));
                info.setTwentyFive(cursor.getInt(cursor.getColumnIndex("twentyFive")));
                info.setTen(cursor.getInt(cursor.getColumnIndex("ten")));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    public List<BcModule> getWeekBcData(int week) {
        List<BcModule> moduleList = new ArrayList<BcModule>();
        SQLiteDatabase db = openDatabase(context);
        try {
            String sql = "select a.* , b.name , b.detail from " + db_name_bc + " a left join " + db_name_bc_js + " b on a.scanNo = b.scanNo where week = " + week + ";";
            Log.d("sqlsql", sql);
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                BcModule bcModule=new BcModule();
                bcModule.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                bcModule.setName(cursor.getString(cursor.getColumnIndex("name")));
                bcModule.setValue(cursor.getString(cursor.getColumnIndex("value")));
                moduleList.add(bcModule);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return moduleList;
    }


}
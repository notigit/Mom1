package com.xiaoaitouch.mom.sqlite;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

/*  孕期数据库 */
public class GestationTables {

    //本地数据库名字
    private static final String db_name = "baby_change";
    private static final String db_file_name = "baby_change.db";

    //数据库存储路径
    private static final String filePath = "data/data/com.xiaoaitouch.mom/baby.db";
    //数据库存放的文件夹 data/data/com.main.jh 下面
    private static final String pathStr = "data/data/com.xiaoaitouch.mom";

    private SQLiteDatabase database;
    private Context context;
    private ArrayList<GestationInfo> arrayList;


    public GestationTables(Context con){
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
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

            return openDatabase(context);
        }
    }

    /** 获取所有数据
    * <>
    * @author
    */
    public ArrayList<GestationInfo> getAllData(){
        arrayList = new ArrayList<GestationInfo>();

        SQLiteDatabase db = openDatabase(context);
        try {
            Cursor cursor = db.rawQuery("select * from " + db_name, null);
            while (cursor.moveToNext()){
                GestationInfo info = new GestationInfo();
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setWeek(cursor.getInt(cursor.getColumnIndex("week")));
                info.setHeight(cursor.getString(cursor.getColumnIndex("height")));
                info.setWeight(cursor.getString(cursor.getColumnIndex("weight")));
                info.setTips(cursor.getString(cursor.getColumnIndex("tips")));
                arrayList.add(info);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    /** 获取指定周期数据
    * @param week 周期数
    * @author
    */
    public GestationInfo getWeekDate(int week){
        GestationInfo info = new GestationInfo();

        SQLiteDatabase db = openDatabase(context);
        try {
            Cursor cursor = db.rawQuery("select * from " + db_name + " where week = " + week + ";", null);
            while (cursor.moveToNext()){
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
                info.setWeek(cursor.getInt(cursor.getColumnIndex("week")));
                info.setHeight(cursor.getString(cursor.getColumnIndex("height")));
                info.setWeight(cursor.getString(cursor.getColumnIndex("weight")));
                info.setTips(cursor.getString(cursor.getColumnIndex("tips")));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    public class GestationInfo implements Serializable{
        public String TAG = "GestationInfo";

        private int id ;
        private int week ;
        private String height ;
        private String weight ;
        private String tips ;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
    }

}
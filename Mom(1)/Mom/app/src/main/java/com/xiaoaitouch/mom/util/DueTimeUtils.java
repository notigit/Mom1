package com.xiaoaitouch.mom.util;

import android.content.Context;

import com.xiaoaitouch.mom.module.DueTimeModule;
import com.xiaoaitouch.mom.sqlite.DueTimeTables;

/**
 * Created by Administrator on 2016/3/5.
 */
public class DueTimeUtils {
    public static void deleteDueTime(Context mContext, DueTimeModule mDueTimeModule, int mCurrentWeek) {
        if (mDueTimeModule != null) {
            int week = mDueTimeModule.getWeek();
            switch (week) {
                case 12:
                    if (mCurrentWeek - 12 >= 4) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 16:
                    if (mCurrentWeek - 16 >= 4) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 20:
                    if (mCurrentWeek - 20 >= 4) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 24:
                    if (mCurrentWeek - 24 >= 4) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 28:
                    if (mCurrentWeek - 28 >= 4) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;

                case 30:
                    if (mCurrentWeek - 30 >= 2) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 32:
                    if (mCurrentWeek - 32 >= 2) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 34:
                    if (mCurrentWeek - 34 >= 2) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 36:
                    if (mCurrentWeek - 36 >= 2) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 37:
                    if (mCurrentWeek - 37 >= 1) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 38:
                    if (mCurrentWeek - 38 >= 1) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 39:
                    if (mCurrentWeek - 39 >= 1) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
                case 40:
                    if (mCurrentWeek - 40 >= 1) {
                        DueTimeTables.deleteDueTimeTableModule(mContext);
                    }
                    break;
            }
        }
    }

}

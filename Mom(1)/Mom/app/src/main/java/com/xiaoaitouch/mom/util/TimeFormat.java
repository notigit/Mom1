package com.xiaoaitouch.mom.util;

import java.sql.Date;
import java.text.SimpleDateFormat;


import android.content.Context;

import com.xiaoaitouch.mom.R;


public class TimeFormat {
	private long mTimeStamp;
	private Context mContext;

	public TimeFormat(Context context, long timeStamp) {
		this.mContext = context;
		this.mTimeStamp = timeStamp;
	}

	// 用于显示会话时间
	public String getTime() {
		long currentTime = System.currentTimeMillis();
		Date date1 = new Date(currentTime);
		Date date2 = new Date(mTimeStamp);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(mTimeStamp);
		int hour = Integer.parseInt(date.substring(0, 2));
		// 今天
		if (date1.getDate() - date2.getDate() == 0) {
			if (hour < 6)
				return mContext.getString(R.string.before_dawn) + " " + date;
			else if (hour < 12)
				return mContext.getString(R.string.morning) + " " + date;
			else if (hour < 18)
				return mContext.getString(R.string.afternoon) + " " + date;
			else
				return mContext.getString(R.string.night) + " " + date;
			// 昨天
		} else if (date1.getDate() - date2.getDate() == 1) {
			return mContext.getString(R.string.yesterday);
		} else if (date1.getDay() - date2.getDay() > 0) {
			if (date2.getDay() == 1)
				return mContext.getString(R.string.monday);
			else if (date2.getDay() == 2)
				return mContext.getString(R.string.tuesday);
			else if (date2.getDay() == 3)
				return mContext.getString(R.string.wednesday);
			else if (date2.getDay() == 4)
				return mContext.getString(R.string.thursday);
			else if (date2.getDay() == 5)
				return mContext.getString(R.string.friday);
			else if (date2.getDay() == 6)
				return mContext.getString(R.string.saturday);
			else
				return mContext.getString(R.string.sunday);

		} else if (date1.getYear() == date2.getYear()) {
			return date2.getMonth() + 1 + mContext.getString(R.string.month)
					+ date2.getDate() + mContext.getString(R.string.day);
		} else
			return format1.format(mTimeStamp);
	}

}

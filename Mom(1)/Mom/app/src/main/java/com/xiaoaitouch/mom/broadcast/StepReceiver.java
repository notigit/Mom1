package com.xiaoaitouch.mom.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoaitouch.mom.service.StepUpdateService;
import com.xiaoaitouch.mom.util.SysUtil;

/**
 *
 * @ClassName: StepReceiver
 * @Description: 监听服务是否关闭
 * @author huxin
 * @date 2015-12-2 下午12:29:55
 *
 */
public class StepReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isOnOff = SysUtil.isServiceWork(context,
				"com.xiaoaitouch.mom.service.StepUpdateService");
		boolean stepService = SysUtil.isServiceWork(context,
				"com.xiaoaitouch.mom.pedometer.StepService");
		if (!isOnOff || !stepService) {
			context.startService(new Intent(context, StepUpdateService.class));
		}
	}
}
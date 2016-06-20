package com.xiaoaitouch.mom.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;

import com.xiaoaitouch.mom.R;


/**
 * 从右到左出现
 * 
 * @author huxin
 * 
 */
public class ActionDialog extends Dialog {
	private Context mContext;
	private String mAction;

	public ActionDialog(Context context, String action) {
		this(context, R.style.ActionRightLeftDialog);
		this.mContext = context;
		this.mAction = action;
		init(context);
	}

	public ActionDialog(Context context, int theme) {
		super(context, theme == 0 ? R.style.ActionRightLeftDialog : theme);
		init(context);
	}

	private void init(Context context) {
		final Window window = getWindow();
		window.setGravity(Gravity.CENTER);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		} else {
			return false;
		}
	}

}

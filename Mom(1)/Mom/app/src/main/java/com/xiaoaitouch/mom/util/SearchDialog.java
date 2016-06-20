package com.xiaoaitouch.mom.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;

import com.xiaoaitouch.mom.R;

/**
 * Desc: 弹框搜索
 * User: huxin
 * Date: 2016/2/26
 * Time: 15:18
 * FIXME
 */
public class SearchDialog extends Dialog {

    public SearchDialog(Context context) {
        this(context, R.style.SearchDialog);
    }

    public SearchDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        final Window window = getWindow();
        window.setGravity(Gravity.TOP);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }
}

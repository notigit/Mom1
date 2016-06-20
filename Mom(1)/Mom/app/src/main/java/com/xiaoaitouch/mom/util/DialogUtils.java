package com.xiaoaitouch.mom.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.update.UpdateResponse;
import com.xiaoaitouch.mom.R;

/**
 * Created by Administrator on 2016/1/25.
 */
public class DialogUtils {
    public static final int BUTTON1 = -1;
    public static final int BUTTON2 = -2;

    /**
     * 显示确定提示框
     *
     * @param context 上下文
     * @param content 提示内容
     */
    public static void showOkAlertDialog(Context context, String content) {
        Resources mResources = context.getResources();
        showAlertDialog(context, mResources.getString(R.string.tip), content,
                mResources.getString(R.string.confirm), null);
    }

    /**
     * 显示确定提示框
     *
     * @param context 上下文
     * @param title   标题
     * @param content 提示内容
     */
    public static void showOkAlertDialog(Context context, String title, String content) {
        Resources mResources = context.getResources();
        showAlertDialog(context, title, content, mResources.getString(R.string.confirm), null);
    }


    /**
     * 一个按钮的提示框
     *
     * @param context
     * @param title
     * @param content
     * @param btnText
     * @param listener
     */
    public static void showAlertDialog(Context context, String title,
                                       String content, String btnText, final DialogInterface.OnClickListener listener) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.common_ui_dialog);
        LinearLayout layout = (LinearLayout) window.findViewById(R.id.alert_dialog_choose_layout);
        layout.setVisibility(View.GONE);

        TextView titleTv = (TextView) window.findViewById(R.id.alert_dialog_title_tv);
        TextView contentTv = (TextView) window.findViewById(R.id.alert_dialog_content_tv);
        titleTv.setText(title);
        contentTv.setText(content);

        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        ok.setText(btnText);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(dlg, BUTTON1);
                } else {
                    dlg.dismiss();
                }
            }
        });
    }

    /**
     * 两个按钮的提示框
     *
     * @param context
     * @param title
     * @param content
     * @param leftBtnText
     * @param rightBtnText
     * @param listener
     */
    public static void showAlertDialogChoose(Context context, String title, String content,
                                             String leftBtnText, String rightBtnText, final DialogInterface.OnClickListener listener
    ) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.common_ui_dialog);
        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        ok.setVisibility(View.GONE);
        TextView titleTv = (TextView) window.findViewById(R.id.alert_dialog_title_tv);
        TextView contentTv = (TextView) window.findViewById(R.id.alert_dialog_content_tv);
        Button leftBtn = (Button) window.findViewById(R.id.alert_dialog_left_btn);
        Button rightBtn = (Button) window.findViewById(R.id.alert_dialog_right_btn);

        leftBtn.setText(leftBtnText);
        rightBtn.setText(rightBtnText);
        if (TextUtils.isEmpty(title)) {
            titleTv.setVisibility(View.GONE);
        }
        titleTv.setText(title);
        contentTv.setText(content);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (listener != null) {
                    listener.onClick(dlg, BUTTON1);
                }
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (listener != null) {
                    listener.onClick(dlg, BUTTON2);
                }
            }
        });
    }

    /**
     * 版本更新
     *
     * @param activity
     * @param listener
     */
    public static void appUpdateAlertDialog(Activity activity,
                                            UpdateResponse updateInfo, String action,
                                            final DialogInterface.OnClickListener listener) {
        final ActionDialog mActionDialog = new ActionDialog(activity, action);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.app_update_dialog, null);
        TextView mUpdateContent = (TextView) view
                .findViewById(R.id.umeng_update_content);
        mUpdateContent.setText("最新版本："
                + updateInfo.version
                + "\n"
                + "新版本大小："
                + StringUtils.convertFileSize(Long
                .valueOf(updateInfo.target_size)) + "\n\n" + "更新内容："
                + "\n" + updateInfo.updateLog);

        Button updateBt = (Button) view.findViewById(R.id.umeng_update_id_ok);
        updateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(mActionDialog, BUTTON1);
                }
            }
        });
        mActionDialog.setContentView(view);
        mActionDialog.show();
    }

}

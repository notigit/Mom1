package com.xiaoaitouch.mom.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.easemob.util.VoiceRecorder;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.chat.CommonUtils;
import com.xiaoaitouch.mom.chat.SmileUtils;
import com.xiaoaitouch.mom.chat.VoicePlayClickListener;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.ExpertBean;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;
import com.xiaoaitouch.mom.util.SharedPreferencesUtil;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.util.ViewUtils;

import java.io.File;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 专家咨询
 *
 * @author huxin
 * @data: 2016/1/19 20:06
 * @version: V1.0
 */
public class EasemobActivity extends BaseActivity {
    private String USER_ID = "";
    private String USER_PWD = "";
    private String FRIEND_ID = "";
    @Bind(R.id.easemob_listView)
    ListView listView; // 聊天列表
    @Bind(R.id.easemob_etContent)
    EditText etContent; // 输入框
    @Bind(R.id.easemob_imgInputMode)
    ImageView imgInputMode; // 输入方式(文字、语音)
    @Bind(R.id.recording_container)
    View recordingContainer;
    @Bind(R.id.mic_image)
    ImageView micImage; // 语音图标
    @Bind(R.id.recording_hint)
    TextView recordingHint;
    @Bind(R.id.btn_press_to_speak)
    View buttonPressToSpeak;
    @Bind(R.id.easemob_btnSend)
    TextView sendTv;

    private ChatAdapter chatAdapter;
    private Drawable[] micImages;
    private VoiceRecorder voiceRecorder;
    public String playMsgId;
    private String expertUserInfo;
    private String userHeadPic = "";
    private String tips = "";
    private UserModule userModule;
    private DisplayImageOptions displayImageOptions;

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easemob_activity);
        ButterKnife.bind(this);
        initViewData();
        getExpertConsultation();
    }

    private void getExpertConsultation() {
        displayImageOptions = DisplayImageOptionsUtils
                .getChatDisplayImageOptions();
        expertUserInfo = SharedPreferencesUtil.getString(mContext,
                "expert_user_info", "");
        if (TextUtils.isEmpty(expertUserInfo)) {
            getExpert();
        } else {
            long dayEndTimeMillis = SharedPreferencesUtil.getLong(mContext,
                    "day_last_time", 0);
            if (StringUtils.currentTimeMillis() >= dayEndTimeMillis) {
                getExpert();
            } else {
                String str[] = expertUserInfo.split(",");
                if (str != null && str.length == 2) {
                    FRIEND_ID = str[0];
                    tips = str[1];
                }
                login();
            }
        }
    }

    private void getExpert() {
        GsonTokenRequest<ExpertBean> request = new GsonTokenRequest<ExpertBean>(
                com.android.volley.Request.Method.POST, Configs.SERVER_URL
                + "/expert", new Response.Listener<JsonResponse<ExpertBean>>() {

            @Override
            public void onResponse(JsonResponse<ExpertBean> response) {
                switch (response.state) {
                    case Configs.UN_USE:
                        showToast("版本过低请升级新版本");
                        break;
                    case Configs.FAIL:
                        showToast(response.msg);
                        break;
                    case Configs.SUCCESS:
                        handler.obtainMessage(12, response.data)
                                .sendToTarget();
                        break;
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                showToast("网络数据加载失败");
            }
        }) {

            @Override
            public Type getType() {
                Type type = new TypeToken<JsonResponse<ExpertBean>>() {
                }.getType();

                return type;
            }
        };
        HttpApi.getExpertConsultation("/expert", request);
    }

    @OnClick(R.id.activity_top_back_image)
    public void onBack() {
        ViewUtils.hideSoftInput(mActivity, etContent.getWindowToken());
        onBackBtnClick();
    }

    @Override
    protected void onResume() {
        regiterBroadcast();
        if (chatAdapter != null) {
            chatAdapter.refresh();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        unRegiterBroadcast();
        super.onStop();
    }

    /**
     * 初始化
     */
    public void initViewData() {
        userModule = MyApplication.instance.getUserModule();
        ViewUtils.hideAutoSoftInput(getWindow());
        USER_ID = userModule.getUserName();
        USER_PWD = "Xa" + USER_ID;
        userHeadPic = userModule.getHeadPic();
        voiceRecorder = new VoiceRecorder(micImageHandler);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{
                getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14)};
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                ViewUtils.hideSoftInput(mActivity, etContent.getWindowToken());
                return false;
            }
        });
    }

    @OnClick(R.id.easemob_btnSend)
    public void sendMsg() {
        if (etContent.getText().length() > 0) {
            if (Utils.isNetworkConnected(mContext)) {
                if (FRIEND_ID.equals("")) {
                    showToast("网络数据加载失败");
                } else {
                    sendTextMsg();
                }
            } else {
                showToast("您处于网络断开状态！");
            }
        }
    }

    @OnClick(R.id.easemob_imgInputMode)
    public void changeInput() {
        changeInputMode();
    }

    /**
     * 注册
     */
    public void register() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(USER_ID,
                            USER_PWD);
                    handler.sendEmptyMessage(5);
                } catch (final EaseMobException e) {
                    // 注册失败
                    int errorCode = e.getErrorCode();
                    if (errorCode == EMError.NONETWORK_ERROR) {
                        handler.sendEmptyMessage(0);
                    } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                        handler.sendEmptyMessage(1);
                    } else if (errorCode == EMError.UNAUTHORIZED) {
                        handler.sendEmptyMessage(2);
                    } else {
                        handler.sendEmptyMessage(3);
                    }
                }
            }
        }).start();
    }

    /**
     * 登录
     */
    public void login() {
        EMChatManager.getInstance().login(USER_ID, USER_PWD, new EMCallBack() {// 回调
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance()
                                .loadAllConversations();
                        handler.sendEmptyMessage(4);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                handler.sendEmptyMessage(9);
            }
        });
    }

    /**
     * 切换输入方式
     */
    public void changeInputMode() {
        if (etContent.getVisibility() == View.VISIBLE) {
            sendTv.setVisibility(View.GONE);
            etContent.setVisibility(View.GONE);
            buttonPressToSpeak.setVisibility(View.VISIBLE);
            imgInputMode.setBackgroundResource(R.drawable.chat_input_icon);
            ViewUtils.hideSoftInput(mActivity, etContent.getWindowToken());
        } else {
            sendTv.setVisibility(View.VISIBLE);
            etContent.setVisibility(View.VISIBLE);
            buttonPressToSpeak.setVisibility(View.GONE);
            imgInputMode.setBackgroundResource(R.drawable.yuyin_icon);
        }
    }

    /**
     * 发送文本信息
     */
    public void sendTextMsg() {
        // 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance()
                .getConversation(FRIEND_ID);
        // 创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        // 如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
        // 设置消息body
        TextMessageBody txtBody = new TextMessageBody(etContent.getText()
                .toString());
        message.addBody(txtBody);
        // 设置接收人
        message.setReceipt(FRIEND_ID);
        // 把消息加入到此会话对象中
        conversation.addMessage(message);
        // 发送消息
        EMChatManager.getInstance().sendMessage(message, callBack);
        etContent.setText("");
    }

    /**
     * 发送语音信息
     */
    public void sendAudio(File audioFile, int length) {
        EMConversation conversation = EMChatManager.getInstance()
                .getConversation(FRIEND_ID);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
        // 如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
        VoiceMessageBody body = new VoiceMessageBody(audioFile, length);
        message.addBody(body);
        message.setReceipt(FRIEND_ID);
        conversation.addMessage(message);
        EMChatManager.getInstance().sendMessage(message, callBack);
    }

    /**
     * 消息状态监听
     */
    public EMCallBack callBack = new EMCallBack() {
        @Override
        public void onError(int arg0, String arg1) {
            handler.sendEmptyMessage(11);
        }

        @Override
        public void onProgress(int arg0, String arg1) {

        }

        @Override
        public void onSuccess() {
            handler.sendEmptyMessage(10);
        }
    };

    public void connect() {
        // 注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(
                new MyConnectionListener());
    }

    // 实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            // 已连接到服务器
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆
                    } else {
                        if (NetUtils.hasNetwork(EasemobActivity.this)) {
                            // 连接不到聊天服务器

                        } else {
                            // 当前网络不可用，请检查网络设置
                        }
                    }
                }
            });
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    // Toast.makeText(getApplicationContext(), "用户已存在！",
                    // Toast.LENGTH_SHORT).show();
                    login();
                    break;
                case 2:
                    // Toast.makeText(getApplicationContext(), "注册失败，无权限！",
                    // Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    // Toast.makeText(getApplicationContext(), "注册失败: ",
                    // Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    // Toast.makeText(getApplicationContext(), "登陆聊天服务器成功！",
                    // Toast.LENGTH_SHORT).show();
                    setListView();
                    break;
                case 5:
                    // Toast.makeText(getApplicationContext(), "注册成功",
                    // Toast.LENGTH_SHORT).show();
                    login();
                    break;
                case 9:
                    // Toast.makeText(getApplicationContext(), "登陆聊天服务器失败！",
                    // Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    // Toast.makeText(getApplicationContext(), "消息发送成功",
                    // Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chatAdapter.refreshSelectLast();
                        }
                    });
                    break;
                case 11:
                    Toast.makeText(getApplicationContext(), "消息发送失败",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    ExpertBean expertBean = (ExpertBean) msg.obj;
                    SharedPreferencesUtil.putLong(mContext, "day_last_time",
                            StringUtils.getEndTime());
                    SharedPreferencesUtil.putString(mContext, "expert_user_info",
                            expertBean.getExpert().getExpertId() + ","
                                    + expertBean.getExpert().getTips());
                    FRIEND_ID = expertBean.getExpert().getExpertId();
                    tips = expertBean.getExpert().getTips();
                    register();
                    login();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    NewMessageBroadcastReceiver msgReceiver;

    /**
     * 注册消息广播
     */
    public void regiterBroadcast() {
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager
                .getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
    }

    /**
     * 注销消息广播
     */
    public void unRegiterBroadcast() {
        if (msgReceiver != null) {
            unregisterReceiver(msgReceiver);
        }
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 消息id
            String msgId = intent.getStringExtra("msgid");
            // 发消息的人的username(userid)
            String msgFrom = intent.getStringExtra("from");
            // 消息类型，文本，图片，语音消息等,这里返回的值为msg.type.ordinal()。
            // 所以消息type实际为是enum类型
            int msgType = intent.getIntExtra("type", 0);
            // 更方便的方法是通过msgId直接获取整个message
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            handler.sendEmptyMessage(10);
        }
    }

    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(
                                R.string.send_voice_need_sdcard_support);
                        Toast.makeText(EasemobActivity.this, st4,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener
                                    .stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, USER_ID,
                                getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(EasemobActivity.this,
                                R.string.recoding_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint
                                .setText(getString(R.string.release_to_cancel));
                        recordingHint
                                .setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(
                                R.string.Recording_without_permission);
                        String st2 = getResources().getString(
                                R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(
                                R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                if (Utils.isNetworkConnected(mContext)) {
                                    if (FRIEND_ID.equals("")) {
                                        showToast("网络数据加载失败");
                                    } else {
                                        sendAudio(
                                                new File(voiceRecorder
                                                        .getVoiceFilePath()),
                                                length);
                                    }
                                } else {
                                    showToast("您处于网络断开状态！");
                                }
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), st1,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), st2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(EasemobActivity.this, st3,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    /**
     * 设置聊天页面数据
     */
    public void setListView() {
        chatAdapter = new ChatAdapter(EasemobActivity.this, FRIEND_ID);
        listView.setAdapter(chatAdapter);
        // adapter.refresh();
    }

    /**
     * 聊天适配器
     *
     * @author Administrator
     */
    private class ChatAdapter extends BaseAdapter {
        private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
        private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
        private static final int HANDLER_MESSAGE_SEEK_TO = 2;

        private Context mContext;
        private LayoutInflater mInflater;

        private EMConversation conversation;
        EMMessage[] messages = null;

        Handler chatHandler = new Handler() {
            private void refreshList() {
                // UI线程不能直接使用conversation.getAllMessages()
                // 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
                messages = (EMMessage[]) conversation.getAllMessages().toArray(
                        new EMMessage[conversation.getAllMessages().size()]);
                for (int i = 0; i < messages.length; i++) {
                    conversation.getMessage(i);
                }
                notifyDataSetChanged();
            }

            @Override
            public void handleMessage(android.os.Message message) {
                switch (message.what) {
                    case HANDLER_MESSAGE_REFRESH_LIST:
                        refreshList();
                        break;
                    case HANDLER_MESSAGE_SELECT_LAST:
                        if (messages.length > 0) {
                            listView.setSelection(messages.length - 1);
                        }
                        break;
                    case HANDLER_MESSAGE_SEEK_TO:
                        int position = message.arg1;
                        listView.setSelection(position);
                        break;
                    default:
                        break;
                }
            }
        };

        /**
         * 刷新页面
         */
        public void refresh() {
            if (chatHandler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
                return;
            }
            android.os.Message msg = handler
                    .obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
            chatHandler.sendMessage(msg);
        }

        /**
         * 刷新页面, 选择最后一个
         */
        public void refreshSelectLast() {
            chatHandler.sendMessage(chatHandler
                    .obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
            chatHandler.sendMessage(chatHandler
                    .obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
        }

        /**
         * 刷新页面, 选择Position
         */
        public void refreshSeekTo(int position) {
            chatHandler.sendMessage(chatHandler
                    .obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
            android.os.Message msg = chatHandler
                    .obtainMessage(HANDLER_MESSAGE_SEEK_TO);
            msg.arg1 = position;
            chatHandler.sendMessage(msg);
        }

        public ChatAdapter(Context con, String userId) {
            this.mContext = con;
            this.mInflater = LayoutInflater.from(mContext);
            this.conversation = EMChatManager.getInstance().getConversation(
                    userId);
            this.messages = (EMMessage[]) conversation
                    .getAllMessages()
                    .toArray(
                            new EMMessage[conversation.getAllMessages().size()]);
        }

        @Override
        public int getCount() {
            return messages.length + 1;
        }

        @Override
        public Object getItem(int position) {
            return messages[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EMMessage message = null;
            if (position == 0) {
                message = getEMMessage();
            } else {
                message = messages[position - 1];
            }
            ViewHolder holder = new ViewHolder();
            // if (convertView == null) {
            convertView = createViewByMessage(message);

            if (message.getType() == EMMessage.Type.TXT) {
                holder.imgAvatar = (ImageView) convertView
                        .findViewById(R.id.list_msg_iv_userhead);
                holder.etContent = (TextView) convertView
                        .findViewById(R.id.list_msg_tv_chatcontent);
            } else if (message.getType() == EMMessage.Type.VOICE) {
                holder.iv = ((ImageView) convertView
                        .findViewById(R.id.iv_voice));
                holder.iv_avatar = (ImageView) convertView
                        .findViewById(R.id.iv_userhead);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
                holder.tv_usernick = (TextView) convertView
                        .findViewById(R.id.tv_userid);
                holder.iv_read_status = (ImageView) convertView
                        .findViewById(R.id.iv_unread_voice);
            }
            holder.tvTime = (TextView) convertView
                    .findViewById(R.id.list_msg_tvTimestamp);
            convertView.setTag(holder);
            // } else {
            // holder = (ViewHolder) convertView.getTag();
            // }
            //

            holder.tvTime.setText(message.getMsgTime() + "");
            holder.tvTime.setVisibility(View.INVISIBLE);
            switch (message.getType()) {
                case TXT: // 文本
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    Spannable span = SmileUtils.getSmiledText(mContext,
                            txtBody.getMessage());

                    holder.etContent.setText(span, TextView.BufferType.SPANNABLE);
                    if (message.direct != EMMessage.Direct.RECEIVE) {
                        loadUserHead(holder.imgAvatar);
                    }
                    break;
                case VOICE: // 语音
                    handleVoiceMessage(message, holder, position, convertView);
                    break;
                default:
                    // not supported
                    break;
            }

            return convertView;
        }

        /**
         * 语音消息
         *
         * @param message
         * @param holder
         * @param position
         * @param convertView
         */
        private void handleVoiceMessage(final EMMessage message,
                                        final ViewHolder holder, final int position, View convertView) {
            VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
            int len = voiceBody.getLength();
            if (len > 0) {
                holder.tv.setText(voiceBody.getLength() + "\"");
                holder.tv.setVisibility(View.VISIBLE);
            } else {
                holder.tv.setVisibility(View.INVISIBLE);
            }
            holder.iv.setOnClickListener(new VoicePlayClickListener(message,
                    holder.iv, holder.iv_read_status, this,
                    EasemobActivity.this, FRIEND_ID));
            if (message.direct != EMMessage.Direct.RECEIVE) {
                loadUserHead(holder.iv_avatar);
            }
            if (playMsgId != null && playMsgId.equals(message.getMsgId())
                    && VoicePlayClickListener.isPlaying) {
                AnimationDrawable voiceAnimation;
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    holder.iv.setImageResource(R.anim.voice_from_icon);
                } else {
                    holder.iv.setImageResource(R.anim.voice_to_icon);
                }
                voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
                voiceAnimation.start();
            } else {
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    holder.iv
                            .setImageResource(R.drawable.chatfrom_voice_playing);
                } else {
                    holder.iv.setImageResource(R.drawable.chatto_voice_playing);
                }
            }

            if (message.direct == EMMessage.Direct.RECEIVE) {
                if (message.isListened()) {
                    // 隐藏语音未听标志
                    holder.iv_read_status.setVisibility(View.INVISIBLE);
                } else {
                    holder.iv_read_status.setVisibility(View.VISIBLE);
                }
                return;
            }

        }

        private View createViewByMessage(EMMessage message) {
            switch (message.getType()) {
                case VOICE:
                    return message.direct == EMMessage.Direct.RECEIVE ? mInflater
                            .inflate(R.layout.list_item_received_voice, null)
                            : mInflater
                            .inflate(R.layout.list_item_sent_voice, null);
                default:
                    return message.direct == EMMessage.Direct.RECEIVE ? mInflater
                            .inflate(R.layout.list_item_msg_received, null)
                            : mInflater.inflate(R.layout.list_item_msg_sent, null);
            }
        }

        class ViewHolder {
            ImageView imgAvatar;
            TextView etContent;
            TextView tvTime;

            TextView tv;
            ImageView iv;
            ImageView iv_avatar;
            TextView tv_usernick;
            ImageView iv_read_status;

        }
    }

    private EMMessage getEMMessage() {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        // 如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
        // 设置消息body
        TextMessageBody txtBody = new TextMessageBody(tips);
        message.addBody(txtBody);
        message.direct = EMMessage.Direct.RECEIVE;
        return message;
    }

    private void loadUserHead(ImageView imageView) {
        if (userHeadPic.contains("http")) {
            ImageLoader.getInstance().displayImage(userHeadPic, imageView,
                    displayImageOptions);
        } else {
            ImageLoader.getInstance().displayImage(
                    Configs.IMAGE_URL + userHeadPic, imageView,
                    displayImageOptions);
        }
    }


}

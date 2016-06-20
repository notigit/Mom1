package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.AddToolsAdapter;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.AddToolsModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.sqlite.AddToolsTables;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 添加工具
 * User: huxin
 * Date: 2016/2/24
 * Time: 14:45
 * FIXME
 */
public class AddToolsActivity extends BaseActivity {
    @Bind(R.id.add_toos_lv)
    ListView addToosLv;

    private AddToolsAdapter addToolsAdapter;
    private String[] addToolsName;
    private String[] addToolsInform;
    private UserModule userModule;
    private List<AddToolsModule> addToolsModule = null;
    private long userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tools_activity);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        setHeader("添加工具");
        addToosLv.setDividerHeight(0);

        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            userId = userModule.getUserId();
        }
        addToolsModule = AddToolsTables.queryAllAddTools(mContext, userId + "");
        if (addToolsModule != null && addToolsModule.size() > 1) {
        } else {
            addToolsModule = new ArrayList<AddToolsModule>();
            addToolsName = getResources().getStringArray(R.array.add_tools_name);
            addToolsInform = getResources().getStringArray(R.array.add_tools_inform);
            for (int i = 0; i < addToolsName.length; i++) {
                AddToolsModule addtools = new AddToolsModule();
                addtools.setOnOff(0);
                addtools.setIndexs(i);
                addtools.setInform(addToolsInform[i]);
                addtools.setName(addToolsName[i]);
                addtools.setUserId(userId);
                addToolsModule.add(addtools);
                AddToolsTables.addAddTools(mContext, addtools);
            }
        }
        addToolsAdapter = new AddToolsAdapter(mContext, addToolsModule);
        addToosLv.setAdapter(addToolsAdapter);
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

}

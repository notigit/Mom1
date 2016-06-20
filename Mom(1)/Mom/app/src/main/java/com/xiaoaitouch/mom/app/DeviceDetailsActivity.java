package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.info.BeaconInfo;
import com.xiaoaitouch.mom.sqlite.BeaconTables;


/**
 * Created by Administrator on 2016/1/15.
 */
public class DeviceDetailsActivity extends BaseActivity {

    private TextView tvMac;
    private Button btnDel;

    private String mac = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_details_layout);
        mac = getIntent().getStringExtra(BeaconInfo.TAG);

        getWidget();
    }

    public void getWidget() {
        tvMac = (TextView) this.findViewById(R.id.device_details_tvMac);
        btnDel = (Button) this.findViewById(R.id.device_details_btnDel);


        tvMac.setText("序列号：" + mac);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mac != null) {
                    BeaconTables.delBeacons(DeviceDetailsActivity.this, mac);
                    finish();
                }
            }
        });
    }


}

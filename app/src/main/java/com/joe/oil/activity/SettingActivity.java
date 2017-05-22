
package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.joe.oil.R;
import com.joe.oil.dialog.DeviceIdSettingDialog;
import com.joe.oil.dialog.IpSettingDialog;

public class SettingActivity extends BaseActivity implements OnClickListener {

    private ImageView back;
    private RelativeLayout download;
    private RelativeLayout station;
    private RelativeLayout planDownload;
    private RelativeLayout userDownload;
    private RelativeLayout wellDownload;
    private RelativeLayout deviceId;
    private RelativeLayout itemUpdate;
    private Intent intent;
    private Context context;

    private RelativeLayout tank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        initView();
        initMembers();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.setting_title_back);
        download = (RelativeLayout) this.findViewById(R.id.setting_rl_download);
        station = (RelativeLayout) this.findViewById(R.id.setting_rl_gas_well);
        planDownload = (RelativeLayout) this.findViewById(R.id.setting_rl_plan_download);
        userDownload = (RelativeLayout) this.findViewById(R.id.setting_rl_user_download);
        wellDownload = (RelativeLayout) this.findViewById(R.id.setting_rl_well_download);
        itemUpdate = (RelativeLayout) this.findViewById(R.id.setting_rl_update_xj_item);
        deviceId = (RelativeLayout) this.findViewById(R.id.setting_rl_set_device_id);

        back.setOnClickListener(this);
        download.setOnClickListener(this);
        station.setOnClickListener(this);
        planDownload.setOnClickListener(this);
        userDownload.setOnClickListener(this);
        wellDownload.setOnClickListener(this);
        itemUpdate.setOnClickListener(this);
        deviceId.setOnClickListener(this);

        tank= (RelativeLayout) this.findViewById(R.id.setting_rl_update_tank);
        tank.setOnClickListener(this);
    }

    private void initMembers() {
        context = SettingActivity.this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_title_back:
                this.finish();
                break;

            case R.id.setting_rl_download:
                intent = new Intent(SettingActivity.this, OfficeChooseActivity.class);
                intent.putExtra("what", "baseData");
                startActivity(intent);
                break;

            case R.id.setting_rl_gas_well:
                IpSettingDialog dialog = new IpSettingDialog(context);
                dialog.show();
                break;

            case R.id.setting_rl_plan_download:
                intent = new Intent(SettingActivity.this, OfficeChooseActivity.class);
                intent.putExtra("what", "planData");
                startActivity(intent);
                break;

            case R.id.setting_rl_user_download:
                intent = new Intent(SettingActivity.this, OfficeChooseActivity.class);
                intent.putExtra("what", "userData");
                startActivity(intent);
                break;

            case R.id.setting_rl_well_download:
                intent = new Intent(SettingActivity.this, OfficeChooseActivity.class);
                intent.putExtra("what", "wellData");
                startActivity(intent);
                break;

            case R.id.setting_rl_update_xj_item:
                intent = new Intent(SettingActivity.this, OfficeChooseActivity.class);
                intent.putExtra("what", "itemUpdate");
                startActivity(intent);
                break;

            case R.id.setting_rl_set_device_id:
                DeviceIdSettingDialog deviceIdSettingDialog = new DeviceIdSettingDialog(context);
                deviceIdSettingDialog.show();
                break;

            case R.id.setting_rl_update_tank:
                intent=new Intent(SettingActivity.this,OfficeChooseActivity.class);
                intent.putExtra("what","tank");
                startActivity(intent);

            default:
                break;
        }
    }
}

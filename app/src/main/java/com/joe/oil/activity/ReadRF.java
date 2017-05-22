package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.joe.oil.R;
import com.joe.oil.util.Constants;
import com.joe.oil.util.CustomUtil;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class ReadRF extends BaseActivity {

    private static NfcAdapter mAdapter;
    private static PendingIntent mPendingIntent;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;
    private ImageView read_rf_back;
    private Context context;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_read_rf);

        context = this;
        read_rf_back = (ImageView) this.findViewById(R.id.read_rf_back);
        read_rf_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ReadRF.this.finish();
            }
        });
        read();

    }

    public  void  read(){
        if (Constants.DEVICE_NAME.equals(Constants.DEVICE_MODEL_OF_BEIJIN_STRING)) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                String beijingCode = CustomUtil.getCodeOfBeiJing();
                if (beijingCode == null) {
                    Constants.showToast(this, "请重新读卡");
                } else {
                    beijingCode = beijingCode.substring(2, beijingCode.length());
                    Intent intent1 = new Intent();
                    intent1.putExtra("code1", beijingCode);
                    ReadRF.this.setResult(RESULT_OK, intent1);
                    SoundHandle soundHandle = new SoundHandle();
                    soundHandle.setContext(getApplicationContext());
                    soundHandle.execute();
                }
            } else {
                Constants.showToast(ReadRF.this, "未开启蓝牙连接读卡设备");
            }
//            ReadRF.this.finish();
        } else {
            mAdapter = NfcAdapter.getDefaultAdapter(this);
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            // Setup an intent filter for all MIME based dispatches
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

            try {
                ndef.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            mFilters = new IntentFilter[]{ndef,};

            // Setup a tech list for all NfcF tags
            mTechLists = new String[][]{new String[]{MifareClassic.class.getName()}};
        }
    }
    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null)
            mAdapter.disableForegroundDispatch(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String nfcCode = CustomUtil.getNFCCode(context, intent);
        Intent data = new Intent();
        // 读出来的三个code封装到intent里面
        data.putExtra("code1", nfcCode);
        data.putExtra("code2", nfcCode);
        data.putExtra("code3", nfcCode);
        // 将这个带数据的intent设置到result里面带回上一页面
        ReadRF.this.setResult(RESULT_OK, data);
        // 关闭Activity
        ReadRF.this.finish();
    }
}
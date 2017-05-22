package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.joe.oil.R;
import com.joe.oil.dialog.ExceptionPreviewDialog;
import com.joe.oil.entity.*;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.CustomUtil;
import com.joe.oil.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("HandlerLeak")
public class UploadExceptionActivity extends BaseActivity implements OnClickListener {

    private final String TAG = "UploadExceptionActivity";
    private ImageView back;
    private TextView confirm;//确定
    private TextView preview;
    private TextView takePhoto;
    private TextView title;
    private TextView history;
    private TextView photoNum;
    private RelativeLayout takePhoto_rl;
    private EditText description;
    private Spinner spinnerStation;
    private Spinner spinnerWork;
    private Spinner spinnerWorkDetail;
    private Spinner spinnerOffice;
    private Spinner spinnerStationofTree;
    private Spinner spinnerDevice;
    private SqliteHelper sqliteHelper;
    private HttpRequest http;
    private OilApplication application;
    private User user;
    private String deviceNames;
    private String deviceCode;
    private String workTypeId;
    private String workTypeName;
    private Context context;
    private Handler mHandler = new Handler();
    private String curTotalPicId = "";
    private List<Dict> station;
    private List<Dict> work;
    private List<Dict> workDetail;
    private List<Dict> tempDicts;
    private List<Dict> tempDictsOfWork;
    private List<Dict> afterSetStation;
    private List<Dict> aftersetWork;
    private List<DeviceTree> office;
    private List<DeviceTree> stationofTree;
    private List<DeviceTree> device;
    private List<DeviceTree> tempTrees;
    private List<DeviceTree> temTreesOfStation;
    private List<DeviceTree> afterSetOffice;
    private List<DeviceTree> afterSetStaionofTree;
    private String[] stations;
    private String[] works;
    private String[] workDetails;
    private String stationId;
    private String workId;
    private String[] offices;
    private String[] stationofTrees;
    private String[] devices;
    private String officeId;
    private String staionOfTreeId;
    private String intentFrom;
    private String type = "";
    private String workType = "";
    private boolean isHavePic = false;
    private String time;

    private static NfcAdapter mAdapter;
    private static PendingIntent mPendingIntent;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;

    private String nfcCode;

    private TextView readUpload;
    private MyHandler handler = new MyHandler();
    private TextView readInfoUpload;

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {

            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        showPhotoNum();

        if (mAdapter != null) {

            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_exception_btn_back:
                handleWithFinish();
                break;

            case R.id.upload_exception_confirm:

                preview();
                break;

            case R.id.upload_exception_photo_rl:
                List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
                if (imgData != null && imgData.size() > 0) {
                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
                    Intent intent = new Intent(UploadExceptionActivity.this, PicSelectedEnsureActivity.class);
                    intent.putExtra("intentFrom", 2);
                    intent.putExtra("typeOfId", time);
                    intent.putExtra("imageSelected", imageGroup);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UploadExceptionActivity.this, ImagePickerActivity.class);
                    intent.putExtra("intentFrom", 2);
                    intent.putExtra("typeOfId", time);
                    startActivity(intent);
                }
                break;

            case R.id.upload_exception_history:
                Intent intentHistory = new Intent(UploadExceptionActivity.this, ExceptionHistoryActivity.class);
                startActivity(intentHistory);
                break;

            case R.id.tv_read_upload:
                Intent intentRF = new Intent(context, ReadRF.class);
                startActivityForResult(intentRF, 0);
                break;

            default:
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                handleWithFinish();
                return super.onKeyDown(keyCode, event);

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload_exception);

        initView();
        initMembers();
        initData();
        OnSelectItemListener();

        readNfcAndHandleActivity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcCode = CustomUtil.getNFCCode(context, intent);
        selectionFromNFCCode();
    }

    private void readNfcAndHandleActivity() {
        if (Constants.DEVICE_NAME.equals(Constants.DEVICE_MODEL_OF_BEIJIN_STRING)) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                String beijingCode = CustomUtil.getCodeOfBeiJing();
                if (beijingCode == null) {
                    Constants.showToast(this, "请重新读卡");
                } else {
                    nfcCode = beijingCode.substring(2, beijingCode.length());

                    selectionFromNFCCode();

                    SoundHandle soundHandle = new SoundHandle();
                    soundHandle.setContext(getApplicationContext());
                    soundHandle.execute();
                }
            } else {
                Constants.showToast(context, "未开启蓝牙连接读卡设备");
            }
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

    private void selectionFromNFCCode() {

        for (int i = 0; i < afterSetOffice.size(); i++) {
            DeviceTree deviceTree = afterSetOffice.get(i);
            if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 6).equals(deviceTree.getCode())) {
                spinnerOffice.setSelection(i);
            }
        }
        for (int i = 0; i < afterSetStaionofTree.size(); i++) {
            DeviceTree deviceTree = afterSetStaionofTree.get(i);
            if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 8).equals(deviceTree.getCode())) {

                spinnerStationofTree.setSelection(i);
            }
        }
        for (int i = 0; i < temTreesOfStation.size(); i++) {
            DeviceTree deviceTree = temTreesOfStation.get(i);
            if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 12).equals(deviceTree.getCode())) {

                spinnerDevice.setSelection(i);
            }
        }
    }

    private void showPhotoNum() {
        List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
        if (imgData != null && imgData.size() > 0) {
            takePhoto.setText("查看图片");
            photoNum.setText(imgData.size() + "");
            photoNum.setVisibility(View.VISIBLE);
        } else {
            takePhoto.setText("添图");
            photoNum.setText("");
            photoNum.setVisibility(View.GONE);
        }
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.upload_exception_btn_back);
        confirm = (TextView) this.findViewById(R.id.upload_exception_confirm);//确定
        preview = (TextView) this.findViewById(R.id.upload_exception_priview);
        takePhoto = (TextView) this.findViewById(R.id.upload_exception_photo);
        title = (TextView) this.findViewById(R.id.upload_exception_title_tv);
        history = (TextView) this.findViewById(R.id.upload_exception_history);
        description = (EditText) this.findViewById(R.id.upload_exception_et_description);
        spinnerStation = (Spinner) this.findViewById(R.id.upload_exception_spinner1);
        spinnerWork = (Spinner) this.findViewById(R.id.upload_exception_spinner2);
        spinnerWorkDetail = (Spinner) this.findViewById(R.id.upload_exception_spinner3);
        spinnerOffice = (Spinner) this.findViewById(R.id.upload_exception_spinner4);
        spinnerStationofTree = (Spinner) this.findViewById(R.id.upload_exception_spinner5);
        spinnerDevice = (Spinner) this.findViewById(R.id.upload_exception_spinner6);
        photoNum = (TextView) this.findViewById(R.id.upload_exception_photo_num);
        takePhoto_rl = (RelativeLayout) this.findViewById(R.id.upload_exception_photo_rl);

        readUpload = (TextView) findViewById(R.id.tv_read_upload);
        readUpload.setOnClickListener(this);
        readInfoUpload= (TextView) findViewById(R.id.tv_read_info_upload);


        back.setOnClickListener(this);
        confirm.setOnClickListener(this);
        takePhoto_rl.setOnClickListener(this);
        history.setOnClickListener(this);
        preview.setOnClickListener(this);
        photoNum.setText("");
        photoNum.setVisibility(View.GONE);
    }

    private void initMembers() {
        context = UploadExceptionActivity.this;
        sqliteHelper = new SqliteHelper(context);
        http = HttpRequest.getInstance(context);
        application = (OilApplication) getApplication();
        user = application.getUser();
        tempDicts = new ArrayList<Dict>();
        tempDictsOfWork = new ArrayList<Dict>();
        tempTrees = new ArrayList<DeviceTree>();
        temTreesOfStation = new ArrayList<DeviceTree>();
        afterSetStation = new ArrayList<Dict>();
        aftersetWork = new ArrayList<Dict>();
        afterSetOffice = new ArrayList<DeviceTree>();
        afterSetStaionofTree = new ArrayList<DeviceTree>();
    }

    private void initData() {
        time = DateUtils.getDateTime();
        intentFrom = getIntent().getStringExtra("intentFrom");
        if (intentFrom.equals("MainActivity")) {
            title.setText("上报问题");
        } else {
            title.setText("参数异常派工");
        }

        station = sqliteHelper.getDictofStation();
        work = sqliteHelper.getDictofWork();
        workDetail = sqliteHelper.getDictofWorkDetail();
        String officeCode = user.getOfficeCode();
        if ((Constants.CURRENT_AREA + "").equals("1234")) {
            initCheckPointAll(officeCode);
        } else {
            initCheckPointArea(officeCode);
        }
        device = sqliteHelper.getDeviceTreeofDevice("1");
        stations = new String[station.size()];
        afterSetStation.clear();
        for (int i = 0; i < station.size(); i++) {
            stations[i] = station.get(i).getLabel();
            afterSetStation.add(station.get(i));
        }
        setSpinnerStation();
        offices = new String[office.size()];
        afterSetOffice.clear();
        for (int i = 0; i < office.size(); i++) {
            offices[i] = office.get(i).getText();
            afterSetOffice.add(office.get(i));
        }
        setSpinnerOffice();
        showPhotoNum();
    }

    private void handleCheckPointAll(String officeCode) {

        if (officeCode.length() == 6) {
            office = sqliteHelper.getDeviceTreeofOfficeLike(officeCode.substring(0, 4), type);
            stationofTree = sqliteHelper.getDeviceTreeofStation(null, type);
        } else if (officeCode.length() == 8) {
            office = sqliteHelper.getDeviceTreeofOfficeLike(officeCode.substring(0, 4), type);
            stationofTree = sqliteHelper.getDeviceTreeofStation(officeCode.substring(0, 6), type);
        }
    }

    private void handleCheckPointArea(String officeCode) {

        if (officeCode.length() == 6) {
            office = sqliteHelper.getDeviceTreeofOfficeEquals(officeCode, type);
            stationofTree = sqliteHelper.getDeviceTreeofStation(null, type);
        } else if (officeCode.length() == 8) {
            office = sqliteHelper.getDeviceTreeofOfficeEquals(officeCode.substring(0, 6), type);
            stationofTree = sqliteHelper.getDeviceTreeofStation(officeCode.substring(0, 6), type);
        }
    }

    private void initCheckPointAll(String officeCode) {
        if (officeCode.length() == 6) {
            office = sqliteHelper.getDeviceTreeofOfficeLike(officeCode.substring(0, 4), "1");
            stationofTree = sqliteHelper.getDeviceTreeofStation(null, "1");
        } else if (officeCode.length() == 8) {
            office = sqliteHelper.getDeviceTreeofOfficeLike(officeCode.substring(0, 4), "1");
            stationofTree = sqliteHelper.getDeviceTreeofStation(officeCode.substring(0, 6), "1");
        } else {
            office = new ArrayList<DeviceTree>();
            stationofTree = new ArrayList<DeviceTree>();
        }
    }

    private void initCheckPointArea(String officeCode) {
        if (officeCode.length() == 6) {
            office = sqliteHelper.getDeviceTreeofOfficeEquals(officeCode.substring(0, 6), "1");
            stationofTree = sqliteHelper.getDeviceTreeofStation(null, "1");
        } else if (officeCode.length() == 8) {
            office = sqliteHelper.getDeviceTreeofOfficeEquals(officeCode.substring(0, 6), "1");
            stationofTree = sqliteHelper.getDeviceTreeofStation(officeCode.substring(0, 6), "1");
        } else {
            office = new ArrayList<DeviceTree>();
            stationofTree = new ArrayList<DeviceTree>();
        }
    }

    private void OnSelectItemListener() {
        spinnerStation.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempDicts.clear();
                aftersetWork.clear();
                stationId = afterSetStation.get(position).getDictId();
                for (int i = 0; i < work.size(); i++) {
                    if (work.get(i).getParentIds().contains(stationId)) {
                        tempDicts.add(work.get(i));
                    } else {
                        continue;
                    }
                }
                Dict dict = new Dict();
                dict.setDictId("-1");
                dict.setLabel("未选择");
                tempDicts.add(0, dict);
                works = new String[tempDicts.size()];
                if (tempDicts.size() > 0) {
                    spinnerWork.setVisibility(View.VISIBLE);
                    spinnerDevice.setVisibility(View.VISIBLE);
                    for (int j = 0; j < tempDicts.size(); j++) {
                        works[j] = tempDicts.get(j).getLabel();
                        aftersetWork.add(tempDicts.get(j));
                    }
                    setSpinnerWork();
                } else {
                    spinnerWork.setVisibility(View.GONE);
                    spinnerDevice.setVisibility(View.GONE);
                }
                String officeCode = user.getOfficeCode();
                switch (position) {
                    case 0:
                        type = "1";
                        break;

                    case 1:
                        type = "2";
                        break;

                    case 2:
                        type = "3";
                        break;

                    case 3:
                        type = "4";
                        break;

                    default:
                        break;
                }
                office.clear();
                stationofTree.clear();
                device.clear();
                if ((Constants.CURRENT_AREA + "").equals("1234")) {
                    handleCheckPointAll(officeCode);
                } else {
                    handleCheckPointArea(officeCode);
                }
                device = sqliteHelper.getDeviceTreeofDevice(type);
                offices = new String[office.size()];
                afterSetOffice.clear();
                for (int i = 0; i < office.size(); i++) {
                    offices[i] = office.get(i).getText();
                    afterSetOffice.add(office.get(i));
                }
                setSpinnerOffice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerWork.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempDictsOfWork.clear();
                workId = aftersetWork.get(position).getDictId();
                for (int i = 0; i < workDetail.size(); i++) {
                    if (workDetail.get(i).getParentIds().contains(workId)) {
                        tempDictsOfWork.add(workDetail.get(i));
                    } else {
                        continue;
                    }
                }
                workDetails = new String[tempDictsOfWork.size()];
                if (tempDictsOfWork.size() > 0) {
                    spinnerWorkDetail.setVisibility(View.VISIBLE);
                    spinnerDevice.setVisibility(View.VISIBLE);
                    for (int j = 0; j < tempDictsOfWork.size(); j++) {
                        workDetails[j] = tempDictsOfWork.get(j).getLabel();
                    }
                    setSpinnerWorkDetail();
                } else {
                    spinnerWorkDetail.setVisibility(View.GONE);
                    spinnerDevice.setVisibility(View.GONE);
                }
                switch (position) {
                    case 0:
                        workType = "1";
                        break;

                    case 1:
                        workType = "2";
                        break;

                    case 2:
                        workType = "3";
                        break;

                    case 3:
                        workType = "4";
                        break;

                    case 4:
                        workType = "5";
                        break;

                    default:
                        break;
                }
                setSpinnerOffice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerOffice.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempTrees.clear();
                afterSetStaionofTree.clear();
                officeId = afterSetOffice.get(position).getCode();

                stationofTree = sqliteHelper.getDeviceTreeofStation(officeId.substring(0, 6), type);

                for (int i = 0; i < stationofTree.size(); i++) {
                    if (stationofTree.get(i).getParentId().equals(officeId)) {
                        tempTrees.add(stationofTree.get(i));
                    } else {
                        continue;
                    }
                }
                // Log.d(TAG, ">>>>>>>>joe>>>>>> tempTrees.size()  " +
                // tempTrees.size());
                stationofTrees = new String[tempTrees.size()];
                if (tempTrees.size() > 0) {
                    spinnerStationofTree.setVisibility(View.VISIBLE);
                    for (int j = 0; j < tempTrees.size(); j++) {
                        stationofTrees[j] = tempTrees.get(j).getText();
                        afterSetStaionofTree.add(tempTrees.get(j));
                    }
                    setSpinnerStationTree();
                    for (int i = 0; i < afterSetStaionofTree.size(); i++) {
                        DeviceTree deviceTree = afterSetStaionofTree.get(i);
                        if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 8).equals(deviceTree.getCode())) {

                            spinnerStationofTree.setSelection(i);
                        }
                    }
                } else {
                    spinnerStationofTree.setVisibility(View.GONE);
                    spinnerDevice.setVisibility(View.GONE);
                }
                // if (type.equals("3")) {
                // spinnerStationofTree.setVisibility(View.GONE);
                // spinnerDevice.setVisibility(View.GONE);
                // } else
                if (type.equals("4") && workType.equals("5")) {
                    spinnerStationofTree.setVisibility(View.GONE);
                    spinnerDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerStationofTree.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temTreesOfStation.clear();
                staionOfTreeId = afterSetStaionofTree.get(position).getCode();
                for (int i = 0; i < device.size(); i++) {
                    if (device.get(i).getParentId().equals(staionOfTreeId)) {
                        temTreesOfStation.add(device.get(i));
                    } else {
                        continue;
                    }
                }
                devices = new String[temTreesOfStation.size()];
                if (temTreesOfStation.size() > 0) {
                    spinnerDevice.setVisibility(View.VISIBLE);
                    for (int j = 0; j < temTreesOfStation.size(); j++) {
                        devices[j] = temTreesOfStation.get(j).getText();
                    }
                    setSpinnerDevice();

                    for (int i = 0; i < temTreesOfStation.size(); i++) {
                        DeviceTree deviceTree = temTreesOfStation.get(i);
                        if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 12).equals(deviceTree.getCode())) {

                            spinnerDevice.setSelection(i);
                        }
                    }
                } else {
                    spinnerDevice.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void handleWithFinish() {
        /**
         * 必要操作，手动退出时，表示并未提交异常，此时将用户选择的图片的数据库记录删除
         */
        sqliteHelper.deletePics(time);
        sqliteHelper.deleteLocalPics(time);
        this.finish();
    }

    private void setSpinnerStation() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, stations);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerStation.setAdapter(adapterKind);
        // spinnerStation.setSelection(stations.length-1);
        spinnerStation.setSelected(false);
    }

    private void setSpinnerWork() {
        // String[] works2 = new String[works.length + 1];
        // works2[0] = "未选择";
        // for (int i = 1; i < works2.length; i++) {
        // works2[i] = works[i - 1];
        // }
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, works);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerWork.setAdapter(adapterKind);
        // spinnerWork.setSelection(works.length-1);
        spinnerWork.setSelected(false);
    }

    private void setSpinnerWorkDetail() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, workDetails);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerWorkDetail.setAdapter(adapterKind);
        spinnerWorkDetail.setSelected(false);
    }

    private void setSpinnerOffice() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, offices);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerOffice.setAdapter(adapterKind);
        spinnerOffice.setSelected(false);
    }

    private void setSpinnerStationTree() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, stationofTrees);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerStationofTree.setAdapter(adapterKind);
        spinnerStationofTree.setSelected(false);
    }

    private void setSpinnerDevice() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, devices);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerDevice.setAdapter(adapterKind);
        spinnerDevice.setSelected(false);
    }

    private void preview() {

        String sDescription = description.getText().toString();
//        String workName = spinnerWork.getSelectedItem().toString();
//        if (workName.equals("未选择")) {
//            Constants.showToast(context, "请选择异常类型");
//            return;
//        }
        if (sDescription.length() <= 0) {
            Constants.showToast(context, "请填写问题描述");
            return;
        }
        try {
            if (spinnerStationofTree.getVisibility() == View.GONE) {
                deviceNames = office.get(spinnerOffice.getSelectedItemPosition()).getText();
                deviceCode = office.get(spinnerOffice.getSelectedItemPosition()).getCode();
            } else {
                if (spinnerDevice.getVisibility() == View.VISIBLE) {
                    deviceNames = temTreesOfStation.get(spinnerDevice.getSelectedItemPosition()).getText();
                    deviceCode = temTreesOfStation.get(spinnerDevice.getSelectedItemPosition()).getCode();
                } else {
                    deviceNames = tempTrees.get(spinnerStationofTree.getSelectedItemPosition()).getText();
                    deviceCode = tempTrees.get(spinnerStationofTree.getSelectedItemPosition()).getCode();
                }
            }
        } catch (Exception e) {
            Constants.showToast(context, "上报问题类型选择项数据异常 ×");
            e.printStackTrace();
            return;
        }
        try {
            if (spinnerWorkDetail.getVisibility() == View.VISIBLE) {
                workTypeId = tempDictsOfWork.get(spinnerWorkDetail.getSelectedItemPosition()).getDictId();
                workTypeName = tempDictsOfWork.get(spinnerWorkDetail.getSelectedItemPosition()).getLabel();
            } else {
                workTypeId = tempDicts.get(spinnerWork.getSelectedItemPosition()).getDictId();
                workTypeName = tempDicts.get(spinnerWork.getSelectedItemPosition()).getLabel();
            }
        } catch (Exception e) {
            Constants.showToast(context, "上报问题作业点选择项数据异常 ×");
            e.printStackTrace();
            return;
        }

        UploadException uploadException = new UploadException();
        Device device = null;
        if (deviceCode.length() == 12) {
            device = sqliteHelper.getDeviceByDeviceCode(deviceCode);
        }
        if (device != null) {
            uploadException.setPointName(device.getOfficeName());
        }
        uploadException.setTime(time);
        uploadException.setCategory("4");
        uploadException.setDeviceName(deviceNames);
        uploadException.setPointName(spinnerStationofTree.getSelectedItem().toString());
        uploadException.setDeviceCode(deviceCode);
        uploadException.setWorkTypeId(workTypeId);
        if (workTypeName.equals("未选择")) {
            workTypeName = "";
        }
        uploadException.setWorkTypeName(workTypeName);
        uploadException.setUserId(user.getUserId());
        uploadException.setDescription(sDescription);
        uploadException.setIsUploadSuccess("0");
        uploadException.setPicId(curTotalPicId);
        uploadException.setWorkId("");
        uploadException.setHistoryId("");
        uploadException.setPatrolTime("");
        ExceptionPreviewDialog ePreviewDialog = new ExceptionPreviewDialog(context, uploadException);
        ePreviewDialog.setOnDialogConfirmListener(new ExceptionPreviewDialog.OnDialogConfirmListener() {
            @Override
            public void onDialogConfirm() {
                uploadData();
            }
        });
        ePreviewDialog.show();
    }

    private void uploadData() {
        String sDescription = description.getText().toString();
//        String workName = spinnerWork.getSelectedItem().toString();
//        if (workName.equals("未选择")) {
//            Constants.showToast(context, "请选择问题类型");
//            return;
//        }
        if (sDescription.length() <= 0) {
            Constants.showToast(context, "请填写问题描述");
            return;
        }
        try {
            if (spinnerStationofTree.getVisibility() == View.GONE) {
                deviceNames = office.get(spinnerOffice.getSelectedItemPosition()).getText();
                deviceCode = office.get(spinnerOffice.getSelectedItemPosition()).getCode();
            } else {
                if (spinnerDevice.getVisibility() == View.VISIBLE) {
                    deviceNames = temTreesOfStation.get(spinnerDevice.getSelectedItemPosition()).getText();
                    deviceCode = temTreesOfStation.get(spinnerDevice.getSelectedItemPosition()).getCode();
                } else {
                    deviceNames = tempTrees.get(spinnerStationofTree.getSelectedItemPosition()).getText();
                    deviceCode = tempTrees.get(spinnerStationofTree.getSelectedItemPosition()).getCode();
                }
            }
        } catch (Exception e) {
            Constants.showToast(context, "上报问题类型选择项数据异常 ×");
            e.printStackTrace();
            return;
        }
        try {
            if (spinnerWorkDetail.getVisibility() == View.VISIBLE) {
                workTypeId = tempDictsOfWork.get(spinnerWorkDetail.getSelectedItemPosition()).getDictId();
                workTypeName = tempDictsOfWork.get(spinnerWorkDetail.getSelectedItemPosition()).getLabel() + "_" + tempDicts.get(spinnerWork.getSelectedItemPosition()).getLabel();
            } else {
                workTypeId = tempDicts.get(spinnerWork.getSelectedItemPosition()).getDictId();
                workTypeName = tempDicts.get(spinnerWork.getSelectedItemPosition()).getLabel();
            }
        } catch (Exception e) {
            Constants.showToast(context, "上报问题作业点选择项数据异常 ×");
            e.printStackTrace();
            return;
        }

        curTotalPicId = "";
        List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
        if (imgData != null && imgData.size() > 0) {
            isHavePic = true;
            for (int i = 0; i < imgData.size(); i++) {
                curTotalPicId += ",";
            }
        } else {
            isHavePic = false;
        }
        Log.d("UploadExceptionActivity", curTotalPicId);

        UploadException uploadException = new UploadException();
        Device device = null;
        if (deviceCode.length() == 12) {
            device = sqliteHelper.getDeviceByDeviceCode(deviceCode);
        }
        if (device != null) {
            uploadException.setPointName(device.getOfficeName());
        }
        uploadException.setTime(time);
        uploadException.setDeviceName(deviceNames);
        uploadException.setDeviceCode(deviceCode);
        uploadException.setPointName(spinnerStationofTree.getSelectedItem().toString());
        uploadException.setWorkTypeId(workTypeId);
        if (workTypeName.equals("未选择")) {
            workTypeName = "";
        }
        uploadException.setWorkTypeName(workTypeName);
        uploadException.setUserId(user.getUserId());
        uploadException.setDescription(sDescription);
        uploadException.setIsUploadSuccess("0");
        uploadException.setPicId(curTotalPicId);
        uploadException.setWorkId("");
        uploadException.setHistoryId("");
        uploadException.setFromWhere("1");

        String mCategoryId;
        if (intentFrom.equals("InspectionActivity")) {
            mCategoryId = "4";
        } else {
            mCategoryId = "3";
        }
        uploadException.setCategory(mCategoryId);
        // 向数据库中保存该条异常数据
        sqliteHelper.insertException(uploadException);

        String lat = application.getLat();
        String lng = application.getLng();

        String uploadType = "";
        switch (workTypeId) {
            case "-1":
                uploadType = "-1";
                break;
            case "22":
                uploadType = "22";
                break;
            case "28":
                uploadType = "28";
                break;
            case "39":
                uploadType = "39";
                break;
        }


        // 向服务器提交该条异常数据
        http.requestUploadWork(new UploadWorkHandler(uploadException), deviceNames, deviceCode, workTypeId, user.getUserId(), mCategoryId, curTotalPicId, sDescription, lat, lng, uploadType);
        Constants.showDialog(context);

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doWithUploadFinish();
            }
        }, 2000);
    }

    private void doWithUploadFinish() {
        description.setText("");
        // spinnerDevice.setVisibility(View.GONE);
        // spinnerWorkDetail.setVisibility(View.GONE);
        if (intentFrom.equals("InspectionActivity")) {
            Message msgMessage = InspectionActivity.handleExceptionHandler.obtainMessage();
            msgMessage.sendToTarget();
            UploadExceptionActivity.this.finish();
        }
        Constants.dismissDialog();
        if (Constants.checkNetWork(this)) {
        } else {
            Constants.showToast(this, "网络不给力，数据已转至后台上传");
        }
        // 更新新上传异常数据时间
        time = DateUtils.getDateTime();
        showPhotoNum();
    }

    private class UploadWorkHandler extends Handler {
        private UploadException uploadException;

        public UploadWorkHandler(UploadException uploadException) {
            this.uploadException = uploadException;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_FAILER:
                    Log.d("UploadExceptionActivity", "数据上传失败啦");
                    // HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()),
                    // null);
                    break;

                case HttpRequest.REQUEST_SUCCESS:
                    String result = msg.obj.toString();
                    try {
                        JSONObject object = new JSONObject(result);
                        String workId = object.getString("workId");
                        String historyId = object.getString("historyId");

                        // 下面更新异常的数据库记录 workId、historyId字段，以此来标识此条数据已经上传至服务器
                        uploadException.setWorkId(workId);
                        uploadException.setHistoryId(historyId);
                        sqliteHelper.updetaException(uploadException);
                        Log.d("UploadExceptionActivity", "数据上传成功啦");
                        // 请求服务器处理此条上报异常数据
                        http.requestHandleWork(new HandleWorkHandler(uploadException), workId, user.getUserId());

                        // 更新此条上报异常数据对应的图片信息
                        Picture picture = sqliteHelper.getPicByTypeOfId(uploadException.getTime());
                        if (picture != null) {
                            picture.setTypeOfId(historyId);
                            sqliteHelper.updatePic(picture);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private class HandleWorkHandler extends Handler {
        private UploadException uploadException;

        public HandleWorkHandler(UploadException uploadException) {
            this.uploadException = uploadException;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    Log.d("UploadExceptionActivity", "数据操作成功啦");
                    String status = msg.obj.toString();
                    if (status.equals("success")) {
                        uploadException.setIsUploadSuccess("1");
                        sqliteHelper.updetaException(uploadException);
                    }
                    // doWithUploadFinish();
                    break;

                case HttpRequest.REQUEST_FAILER:
                    Log.d("UploadExceptionActivity", "数据操作失败啦");
                    // HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()),
                    // null);
                    Constants.dismissDialog();
                    break;

                default:
                    break;
            }
        }
    }

    private class  MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    List<Device> devices = (List<Device>)msg.obj;
                    if (devices.size()>0) {
                        readInfoUpload.setText(devices.get(0).getOfficeName()+"_"+devices.get(0).getName());
                    }
                    break;

                case HttpRequest.REQUEST_FAILER:
                    Constants.dismissDialog();
                    break;

                default:
                    break;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                String readCode = data.getStringExtra("code1");//读卡得到点位CODE
                //根据CODE得到点位相关信息接口
                try {
                    http.requestDeviceByCode(handler,readCode,user.getUserId().toString(),"概况");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (readCode != null && readCode.length() > 0) {
                    readInfoUpload.setText(readCode);
                } else {
                    Constants.showToast(context, "非本站点巡检卡！");
                }

        }
    }
}

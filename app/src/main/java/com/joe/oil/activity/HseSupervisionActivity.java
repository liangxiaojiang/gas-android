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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.dialog.HsePreviewDialog;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.DeviceTree;
import com.joe.oil.entity.Dict;
import com.joe.oil.entity.UploadHseSupervision;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.CustomUtil;
import com.joe.oil.util.DateUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("HandlerLeak")
public class HseSupervisionActivity extends BaseActivity implements OnClickListener {

    private final String TAG = "UploadExceptionActivity";
    private ImageView ivLeft;
    private TextView tvOk,mReadRfTv,mReadRfInfoTv;
    private TextView tvHseInfo;
    //    private TextView preview;
    private TextView tvAddPhoto;
    private TextView tvTitle;
    private TextView tvRight;
    private TextView tvPhotoNum;
    private RelativeLayout rlAddPhoto;
    private EditText etMainProblem;
    private EditText etSuggestion;
    private Spinner spnStationType;
    //    private Spinner spnWork;
//    private Spinner spnWorkDetail;
    private Spinner spnOffice;
    private Spinner spnStationOfTree;
    private Spinner spnDevice;
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
    private MyHandler handler = new MyHandler();
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
    private String type = "";
    private String workType = "";
    private boolean isHavePic = false;
    private String time;

    private static NfcAdapter mAdapter;
    private static PendingIntent mPendingIntent;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;

    private String nfcCode;

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
            case R.id.iv_left:
                handleWithFinish();
                break;

            case R.id.tv_ok:

                preview();
                break;

            case R.id.rl_add_photo:
                List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
                if (imgData != null && imgData.size() > 0) {
                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
                    Intent intent = new Intent(context, PicSelectedEnsureActivity.class);
                    intent.putExtra("intentFrom", 2);
                    intent.putExtra("typeOfId", time);
                    intent.putExtra("imageSelected", imageGroup);
                    startActivity(intent);
                } else {
                    Log.d("Image Select Flag", "imgData null");
                    Intent intent = new Intent(context, ImagePickerActivity.class);
                    intent.putExtra("intentFrom", 2);
                    intent.putExtra("typeOfId", time);
                    startActivity(intent);
                }
                break;

            case R.id.tv_right:
                Intent intentHistory = new Intent(context, HseHistoryActivity.class);
                startActivity(intentHistory);
                break;

            case R.id.tv_read_rf:
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
        setContentView(R.layout.activity_hse_supervision);

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
                spnOffice.setSelection(i);
            }
        }
        for (int i = 0; i < afterSetStaionofTree.size(); i++) {
            DeviceTree deviceTree = afterSetStaionofTree.get(i);
            if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 8).equals(deviceTree.getCode())) {

                spnStationOfTree.setSelection(i);
            }
        }
        for (int i = 0; i < temTreesOfStation.size(); i++) {
            DeviceTree deviceTree = temTreesOfStation.get(i);
            if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 12).equals(deviceTree.getCode())) {

                spnDevice.setSelection(i);
            }
        }
    }

    private void showPhotoNum() {
        List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
        if (imgData != null && imgData.size() > 0) {
            tvAddPhoto.setText("查看图片");
            tvPhotoNum.setText(imgData.size() + "");
            tvPhotoNum.setVisibility(View.VISIBLE);
        } else {
            tvAddPhoto.setText("添图");
            tvPhotoNum.setText("");
            tvPhotoNum.setVisibility(View.GONE);
        }
    }

    private void initView() {
        ivLeft = (ImageView) this.findViewById(R.id.iv_left);
        tvOk = (TextView) this.findViewById(R.id.tv_ok);
        mReadRfInfoTv = (TextView) this.findViewById(R.id.tv_read_info);
        mReadRfTv = (TextView) this.findViewById(R.id.tv_read_rf);
        tvHseInfo = (TextView) this.findViewById(R.id.tv_hse_info);
//        preview = (TextView) this.findViewById(R.id.upload_exception_priview);
        tvAddPhoto = (TextView) this.findViewById(R.id.tv_add_photo);
        tvTitle = (TextView) this.findViewById(R.id.tv_title);
        tvRight = (TextView) this.findViewById(R.id.tv_right);
        etMainProblem = (EditText) this.findViewById(R.id.et_main_problem);
        etSuggestion = (EditText) this.findViewById(R.id.et_suggestion);
        spnStationType = (Spinner) this.findViewById(R.id.spn_station_type);
//        spnWork = (Spinner) this.findViewById(R.id.spn_work);
//        spnWorkDetail = (Spinner) this.findViewById(R.id.spn_work_detail);
        spnOffice = (Spinner) this.findViewById(R.id.spn_office);
        spnStationOfTree = (Spinner) this.findViewById(R.id.spn_station_of_tree);
        spnDevice = (Spinner) this.findViewById(R.id.spn_device);
        tvPhotoNum = (TextView) this.findViewById(R.id.tv_photo_num);
        rlAddPhoto = (RelativeLayout) this.findViewById(R.id.rl_add_photo);

        ivLeft.setOnClickListener(this);
        mReadRfTv.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        rlAddPhoto.setOnClickListener(this);
        tvRight.setOnClickListener(this);
//        preview.setOnClickListener(this);
        tvPhotoNum.setText("");
        tvPhotoNum.setVisibility(View.GONE);
    }

    private void initMembers() {
        context = HseSupervisionActivity.this;
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
        tvTitle.setText("HSE督查");

        if (user.getRoleName() != null && user.getRoleName().length() > 0) {

            tvHseInfo.setText("检查单位： " + user.getOfficeName() + "\n检查人员： " + user.getName());
        } else {

            tvHseInfo.setText("检查单位： 采气五厂\n检查人员： " + user.getLoginName());
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
        spnStationType.setOnItemSelectedListener(new OnItemSelectedListener() {

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
//                    spnWork.setVisibility(View.VISIBLE);
                    spnDevice.setVisibility(View.VISIBLE);
                    for (int j = 0; j < tempDicts.size(); j++) {
                        works[j] = tempDicts.get(j).getLabel();
                        aftersetWork.add(tempDicts.get(j));
                    }
//                    setSpinnerWork();
                } else {
//                    spnWork.setVisibility(View.GONE);
                    spnDevice.setVisibility(View.GONE);
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

//        spnWork.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                tempDictsOfWork.clear();
//                workId = aftersetWork.get(position).getDictId();
//                for (int i = 0; i < workDetail.size(); i++) {
//                    if (workDetail.get(i).getParentIds().contains(workId)) {
//                        tempDictsOfWork.add(workDetail.get(i));
//                    } else {
//                        continue;
//                    }
//                }
//                workDetails = new String[tempDictsOfWork.size()];
//                if (tempDictsOfWork.size() > 0) {
//                    spnWorkDetail.setVisibility(View.VISIBLE);
//                    spnDevice.setVisibility(View.VISIBLE);
//                    for (int j = 0; j < tempDictsOfWork.size(); j++) {
//                        workDetails[j] = tempDictsOfWork.get(j).getLabel();
//                    }
//                    setSpinnerWorkDetail();
//                } else {
//                    spnWorkDetail.setVisibility(View.GONE);
//                    spnDevice.setVisibility(View.GONE);
//                }
//                switch (position) {
//                    case 0:
//                        workType = "1";
//                        break;
//
//                    case 1:
//                        workType = "2";
//                        break;
//
//                    case 2:
//                        workType = "3";
//                        break;
//
//                    case 3:
//                        workType = "4";
//                        break;
//
//                    case 4:
//                        workType = "5";
//                        break;
//
//                    default:
//                        break;
//                }
//                setSpinnerOffice();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        spnOffice.setOnItemSelectedListener(new OnItemSelectedListener() {

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
                    spnStationOfTree.setVisibility(View.VISIBLE);
                    for (int j = 0; j < tempTrees.size(); j++) {
                        stationofTrees[j] = tempTrees.get(j).getText();
                        afterSetStaionofTree.add(tempTrees.get(j));
                    }
                    setSpinnerStationTree();
                    for (int i = 0; i < afterSetStaionofTree.size(); i++) {
                        DeviceTree deviceTree = afterSetStaionofTree.get(i);
                        if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 8).equals(deviceTree.getCode())) {

                            spnStationOfTree.setSelection(i);
                        }
                    }
                } else {
                    spnStationOfTree.setVisibility(View.GONE);
                    spnDevice.setVisibility(View.GONE);
                }
                // if (type.equals("3")) {
                // spnStationOfTree.setVisibility(View.GONE);
                // spnDevice.setVisibility(View.GONE);
                // } else
                if (type.equals("4") && workType.equals("5")) {
                    spnStationOfTree.setVisibility(View.GONE);
                    spnDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnStationOfTree.setOnItemSelectedListener(new OnItemSelectedListener() {

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
                    spnDevice.setVisibility(View.VISIBLE);
                    for (int j = 0; j < temTreesOfStation.size(); j++) {
                        devices[j] = temTreesOfStation.get(j).getText();
                    }
                    setSpinnerDevice();

                    for (int i = 0; i < temTreesOfStation.size(); i++) {
                        DeviceTree deviceTree = temTreesOfStation.get(i);
                        if (nfcCode != null && nfcCode.length() > 6 && nfcCode.substring(0, 12).equals(deviceTree.getCode())) {

                            spnDevice.setSelection(i);
                        }
                    }
                } else {
                    spnDevice.setVisibility(View.GONE);
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
        spnStationType.setAdapter(adapterKind);
        spnStationType.setSelected(false);
    }

//    private void setSpinnerWork() {
//        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, works);
//        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
//        spnWork.setAdapter(adapterKind);
//        spnWork.setSelected(false);
//    }
//
//    private void setSpinnerWorkDetail() {
//        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, workDetails);
//        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
//        spnWorkDetail.setAdapter(adapterKind);
//        spnWorkDetail.setSelected(false);
//    }

    private void setSpinnerOffice() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, offices);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spnOffice.setAdapter(adapterKind);
        spnOffice.setSelected(false);
    }

    private void setSpinnerStationTree() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, stationofTrees);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spnStationOfTree.setAdapter(adapterKind);
        spnStationOfTree.setSelected(false);
    }

    private void setSpinnerDevice() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, devices);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spnDevice.setAdapter(adapterKind);
        spnDevice.setSelected(false);
    }

    private void preview() {

        String mainProblem = etMainProblem.getText().toString();
        String suggestion = etSuggestion.getText().toString();

        if (mainProblem.length() <= 0) {
            Constants.showToast(context, "请输入主要存在问题及不符合项");
            return;
        }
        if (suggestion.length() <= 0) {
            Constants.showToast(context, "请输入整改要求及意见");
            return;
        }
        if (spnStationOfTree.getVisibility() == View.GONE) {
            Constants.showToast(context, "请选择受检站点或者刷卡");
            return;
        }

        String officeId = "";
        String organ = "";
        StringBuilder checkedPoint = new StringBuilder();
        try {
            officeId = office.get(spnOffice.getSelectedItemPosition()).getOfficeId();
            if (spnDevice.getVisibility() == View.VISIBLE) {
                checkedPoint.append(tempTrees.get(spnStationOfTree.getSelectedItemPosition()).getText())
                        .append("_")
                        .append(temTreesOfStation.get(spnDevice.getSelectedItemPosition()).getText());
                organ = temTreesOfStation.get(spnDevice.getSelectedItemPosition()).getText();
            } else {
                checkedPoint.append(tempTrees.get(spnStationOfTree.getSelectedItemPosition()).getText());
                organ = tempTrees.get(spnStationOfTree.getSelectedItemPosition()).getText();
            }
        } catch (Exception e) {
            Constants.showToast(context, "上报问题类型选择项数据异常 ×");
            e.printStackTrace();
            return;
        }
//        try {
//            if (spnWorkDetail.getVisibility() == View.VISIBLE) {
//                workTypeId = tempDictsOfWork.get(spnWorkDetail.getSelectedItemPosition()).getDictId();
//                workTypeName = tempDictsOfWork.get(spnWorkDetail.getSelectedItemPosition()).getLabel();
//            } else {
//                workTypeId = tempDicts.get(spnWork.getSelectedItemPosition()).getDictId();
//                workTypeName = tempDicts.get(spnWork.getSelectedItemPosition()).getLabel();
//            }
//        } catch (Exception e) {
//            Constants.showToast(context, "上报问题作业点选择项数据异常 ×");
//            e.printStackTrace();
//            return;
//        }

        final UploadHseSupervision uploadHseSupervision = new UploadHseSupervision();
        uploadHseSupervision.setBeCheckedOffice(officeId);
        uploadHseSupervision.setCheckedPoint(checkedPoint.toString());
        uploadHseSupervision.setBeCheckedOrgan(organ);
        uploadHseSupervision.setCreatedDate(time);
        uploadHseSupervision.setCheckerIds(user.getUserId());
        uploadHseSupervision.setCheckerNames(user.getName());
        uploadHseSupervision.setCheckOffice(user.getOfficeId());
        uploadHseSupervision.setIssue(mainProblem);
        uploadHseSupervision.setSuggestion(suggestion);

        HsePreviewDialog dialog = new HsePreviewDialog(context, uploadHseSupervision);
        dialog.setOnDialogConfirmListener(new HsePreviewDialog.OnDialogConfirmListener() {
            @Override
            public void onDialogConfirm() {
                uploadData(uploadHseSupervision);
            }
        });
        dialog.show();
    }

    private void uploadData(UploadHseSupervision uploadHseSupervision) {

        Constants.showDialog(context);
        // 向数据库中保存该条异常数据
        sqliteHelper.insertHse(uploadHseSupervision);

        // 向服务器提交该条异常数据
        http.requestUploadHse(new HandleHseHandler(uploadHseSupervision), uploadHseSupervision);

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doWithUploadFinish();
            }
        }, 2000);
    }

    private void doWithUploadFinish() {
        etMainProblem.setText("");
        etSuggestion.setText("");
        // spnDevice.setVisibility(View.GONE);
        // spnWorkDetail.setVisibility(View.GONE);
        Constants.dismissDialog();
        if (!Constants.checkNetWork(this)) {
            Constants.showToast(this, "网络不给力，数据已转至后台上传");
        }
        // 更新新上传异常数据时间
        time = DateUtils.getDateTime();
        showPhotoNum();
    }

    private class HandleHseHandler extends Handler {
        private UploadHseSupervision uploadHseSupervision;

        public HandleHseHandler(UploadHseSupervision uploadHseSupervision) {
            this.uploadHseSupervision = uploadHseSupervision;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    String status = msg.obj.toString();
                    if (status.equals("success")) {
                        uploadHseSupervision.setIsSuccess(1);
                        sqliteHelper.updetaHse(uploadHseSupervision);
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
    private class  MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    List<Device> devices = (List<Device>)msg.obj;
                    if (devices.size()>0) {
                        mReadRfInfoTv.setText(devices.get(0).getOfficeName()+"_"+devices.get(0).getName());
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
                    mReadRfInfoTv.setText(readCode);
                } else {
                    Constants.showToast(context, "非本站点巡检卡！");
                }

        }
    }
}

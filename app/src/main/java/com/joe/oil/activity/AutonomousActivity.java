package com.joe.oil.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.DeviceTree;
import com.joe.oil.entity.Dict;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangxiaojiang on 2016/12/14.
 */
public class AutonomousActivity extends Activity implements View.OnClickListener {

    private Context context;
    private User user;
    private OilApplication application;
    private TextView tv_autonomous;//这是作业人员
    private Spinner sworkType;
    private Spinner sworkType2;
    private Spinner sworkType3;
    private Spinner sworkUser;
    private List<String> data_list;
    private List<String> joinCarMax;
    private List<User> userList;
    private ArrayAdapter<String> arr_adapter;
    private String workId;
    private String[] workDetails;

    private List<Dict> station;
    private List<Dict> tempDicts;
    private List<Dict> aftersetWork;
    private List<Dict> work;
    private String stationId;

    private SqliteHelper sqliteHelper;
    private List<Dict> afterSetStation;

    private Spinner spnOffice;
    private Spinner spnStationOfTree;
    private Spinner spnDevice;
    private List<Dict> workDetail;
    private List<Dict> tempDictsOfWork;
    private List<DeviceTree> office;
    private List<DeviceTree> stationofTree;
    private List<DeviceTree> device;
    private List<DeviceTree> tempTrees;
    private List<DeviceTree> temTreesOfStation;
    private List<DeviceTree> afterSetOffice;
    private List<DeviceTree> afterSetStaionofTree;
    private String[] stations;
    private String[] works;
    private String[] offices;
    private String[] stationofTrees;
    private String[] devices;
    private String officeId;
    private String staionOfTreeId;
    private String type = "";
    private String workType = "";
    private String nfcCode;

    private TextView autonomRead,autonReadText;
    private HttpRequest http;
    private MyHandler handler = new MyHandler();
    private ImageView autonomous_btn_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_autonomous);

        initView();
        initMembers();
        initData();
        OnSelectItemListener();
    }

    private void initMembers() {
        context = AutonomousActivity.this;
        application = (OilApplication) getApplication();
        user = application.getUser();
        http = HttpRequest.getInstance(context);
        tempDicts = new ArrayList<Dict>();
        aftersetWork = new ArrayList<Dict>();
        afterSetStation = new ArrayList<Dict>();
        sqliteHelper = new SqliteHelper(context);
        tempDicts = new ArrayList<Dict>();
        tempDictsOfWork = new ArrayList<Dict>();
        tempTrees = new ArrayList<DeviceTree>();
        temTreesOfStation = new ArrayList<DeviceTree>();
        afterSetStation = new ArrayList<Dict>();
        aftersetWork = new ArrayList<Dict>();
        afterSetOffice = new ArrayList<DeviceTree>();
        afterSetStaionofTree = new ArrayList<DeviceTree>();
    }

    private void initView() {
        tv_autonomous= (TextView) findViewById(R.id.tv_autonomous);
        sworkType= (Spinner) findViewById(R.id.spn_work_type);
        spnOffice = (Spinner) this.findViewById(R.id.spn_office);
        spnStationOfTree = (Spinner) this.findViewById(R.id.spn_station_of_tree);
        spnDevice = (Spinner) this.findViewById(R.id.spn_device);
        autonomRead= (TextView) findViewById(R.id.tv_read_operating_point);
        autonReadText= (TextView) findViewById(R.id.tv_read_info);
        autonomous_btn_back= (ImageView) findViewById(R.id.autonomous_btn_back);
        autonomRead.setOnClickListener(this);
        autonomous_btn_back.setOnClickListener(this);

        sworkType2= (Spinner) this.findViewById(R.id.work_type_spinner2);
        sworkType3= (Spinner) this.findViewById(R.id.work_type_spinner3);
        sworkUser= (Spinner) this.findViewById(R.id.tv_matching_people);

//        data_list = new ArrayList<String>();
//        joinCarMax = new ArrayList<String>();
//        sqliteHelper = new SqliteHelper(context);
//        if (sqliteHelper.getUserInfo() != null) {
//            userList = sqliteHelper.getUserInfo();
////            data_list.add(user.getName());
//            for (User user : userList) {
//                data_list.add(user.getName());
//            }
//                data_list.add(tank.getNumber());
//                //甲醇罐规格计算对应车量的罐横截面积
//                if (!tank.getTankarea().equals("null")) {
//                    String tankckg = tank.getTankarea();
//                    String[] tankSize = tankckg.split("\\*");
//                    int ji = Integer.parseInt(tankSize[0]) * Integer.parseInt(tankSize[1]);
//                    joinCarMax.add(ji + "");
//                }
//            }
//            joinCarMax.add(0, "0");
            //适配器
//            arr_adapter = new ArrayAdapter<String>(context, R.layout.item_spinner, data_list);
            //设置样式
//            arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
//            //加载适配器
//            sworkUser.setAdapter(arr_adapter);
//            sworkUser.setSelected(false);
//            sworkUser.setOnItemSelectedListener(
//                    new OnItemSelectedListenerImpl());
//        }
    }



    private void initData() {

        if (user.getRoleName() != null && user.getRoleName().length() > 0) {

            tv_autonomous.setText("自主作业人员： " + user.getName());
        }
//        else {
//
//            tvHseInfo.setText("检查单位： 采气五厂\n检查人员： " + user.getLoginName());
//        }

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
//        showPhotoNum();
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
    private void setSpinnerStation() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, stations);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        sworkType.setAdapter(adapterKind);
        sworkType.setSelected(false);
    }

    private void setSpinnerWork() {
        // String[] works2 = new String[works.length + 1];
        // works2[0] = "未选择";
        // for (int i = 1; i < works2.length; i++) {
        // works2[i] = works[i - 1];
        // }
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, works);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        sworkType2.setAdapter(adapterKind);
        // spinnerWork.setSelection(works.length-1);
        sworkType2.setSelected(false);
    }

    private void setSpinnerWorkDetail() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, workDetails);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        sworkType3.setAdapter(adapterKind);
        sworkType3.setSelected(false);
    }

    private void OnSelectItemListener() {
        sworkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                    sworkType2.setVisibility(View.VISIBLE);
                    sworkType3.setVisibility(View.VISIBLE);
//                    spnWork.setVisibility(View.VISIBLE);
                    spnDevice.setVisibility(View.VISIBLE);
                    for (int j = 0; j < tempDicts.size(); j++) {
                        works[j] = tempDicts.get(j).getLabel();
                        aftersetWork.add(tempDicts.get(j));
                    }
                    setSpinnerWork();
                } else {
//                    spnWork.setVisibility(View.GONE);
                    sworkType2.setVisibility(View.GONE);
                    sworkType3.setVisibility(View.GONE);
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

        sworkType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                    sworkType3.setVisibility(View.VISIBLE);
//                    spinnerDevice.setVisibility(View.VISIBLE);
                    for (int j = 0; j < tempDictsOfWork.size(); j++) {
                        workDetails[j] = tempDictsOfWork.get(j).getLabel();
                    }
                    setSpinnerWorkDetail();
                } else {
                    sworkType3.setVisibility(View.GONE);
//                    spinnerDevice.setVisibility(View.GONE);
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

        spnOffice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                if (type.equals("4") && workType.equals("5")) {
                    spnStationOfTree.setVisibility(View.GONE);
                    spnDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnStationOfTree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

    private void setSpinnerOffice() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, offices);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spnOffice.setAdapter(adapterKind);
        spnOffice.setSelected(false);
    }
    private void setSpinnerDevice() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, devices);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spnDevice.setAdapter(adapterKind);
        spnDevice.setSelected(false);
    }
    private void setSpinnerStationTree() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, stationofTrees);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spnStationOfTree.setAdapter(adapterKind);
        spnStationOfTree.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_read_operating_point:
                Intent intentRF = new Intent(context, ReadRF.class);
                startActivityForResult(intentRF, 0);
                break;
            case R.id.autonomous_btn_back:
                this.finish();
                break;
            default:
                break;
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
                        autonReadText.setText(devices.get(0).getOfficeName()+"_"+devices.get(0).getName());
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
                    http.requestDeviceByCode(handler,readCode,user.getUserId().toString(),"设备概况");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (readCode != null && readCode.length() > 0) {
                    autonReadText.setText(readCode);
                } else {
                    Constants.showToast(context, "非本站点巡检卡！");
                }

        }
    }
}
package com.joe.oil.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.PicSelectedEnsureAdapter;
import com.joe.oil.entity.Picture;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.User;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.imagepicker.QiAlbumViewFragment;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PicSelectedEnsureActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private TextView modify;
	private TextView confirm;
	private GridView wv_pic_selected;
	private ImageGroup imageGroup;
	private List<ImageBean> imageSets;
	private PicSelectedEnsureAdapter picSelectedEnsureAdapter;

	private String typeOfId;
	private int intentFrom;
	private PlanDetail planDetail;
	private User user;
	private OilApplication application;
	private SqliteHelper sqliteHelper;

	private final int REQUEST_CODE = 0x123;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			ImageGroup newImageGroup = (ImageGroup) data.getSerializableExtra("imageSelected");
			if (newImageGroup != null && newImageGroup.getImageSets().size() > 0) {
				imageSets.clear();
				imageGroup = newImageGroup;
				List<ImageBean> newData = imageGroup.getImageSets();
				for (int i = 0; i < newData.size(); i++) {
					imageSets.add(newData.get(i));
				}
				picSelectedEnsureAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pic_select_ensure);

		findViews();
		initDataSet();
	}

	private void findViews() {
		wv_pic_selected = (GridView) findViewById(R.id.wv_pic_selected);
		wv_pic_selected.setOverScrollMode(View.OVER_SCROLL_NEVER);
		wv_pic_selected.setOnItemClickListener(this);
		modify = (TextView) this.findViewById(R.id.tv_title_modify);
		confirm = (TextView) this.findViewById(R.id.tv_title_ensure);
		
		modify.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	private void initDataSet() {
		sqliteHelper = new SqliteHelper(this);
		application = (OilApplication) getApplication();
		user = application.getUser();

		intentFrom = getIntent().getIntExtra("intentFrom", 1);
		typeOfId = getIntent().getStringExtra("typeOfId");
		if (intentFrom == 0 || intentFrom == 1) {
			planDetail = (PlanDetail) getIntent().getSerializableExtra("planDetail");
		}
		if (intentFrom == 5) {
			modify.setText("返回");
			confirm.setVisibility(View.INVISIBLE);
		}
		else {
			modify.setText("修改");
			confirm.setVisibility(View.VISIBLE);
		}
		imageGroup = (ImageGroup) getIntent().getSerializableExtra("imageSelected");
		if (imageGroup != null) {
			imageSets = imageGroup.getImageSets();
		} else {
			imageSets = new ArrayList<ImageBean>();
		}
		picSelectedEnsureAdapter = new PicSelectedEnsureAdapter(this, imageSets);
		wv_pic_selected.setAdapter(picSelectedEnsureAdapter);
	}

	private void jumpToPicSelect() {
		Intent intent = new Intent(PicSelectedEnsureActivity.this, ImagePickerActivity.class);
		intent.putExtra("isModifyPicture", true);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	@Override
	protected void onDestroy() {
		ImageLoader.getInstance().clearMemoryCache();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_modify:
			if(intentFrom == 5){
				this.finish();
			}
			else {
				jumpToPicSelect();
			}
			break;

		case R.id.tv_title_ensure:
			handleWithYourEnsure();
			this.finish();
			break;

		default:
			break;
		}
	}

	private void handleWithYourEnsure() {
		// 首先删除旧的选择
		sqliteHelper.deletePics(typeOfId);
		sqliteHelper.deleteLocalPics(typeOfId);

		/**
		 * @description 1. 将要上传服务器的图片信息组合并与计划、任务或异常等绑定存储数据库
		 * @description 2. 将所选择的图片的本地信息与计划、任务或异常等绑定存储数据库
		 */
		String names = "";
		String urls = "";
		for (int i = 0; i < imageSets.size(); i++) {
			ImageBean bean = imageSets.get(i);
			bean.setUploadTaskId(typeOfId);
			names += bean.getDisplayName() + ";";
			urls += bean.getPath() + ";";
			if (intentFrom == 0 || intentFrom == 1) {
				bean.setPatrolTime(planDetail.getPatrolTime());
			} else {
				bean.setPatrolTime("");
			}
			sqliteHelper.insertLocalPic(bean);
		}
		if (imageSets.size() > 0) {
			Picture pic = new Picture();
			pic.setChargerId(user.getUserId());
			pic.setCreateTime(DateUtils.getDateTime());
			pic.setName(names);
			pic.setType(intentFrom);
			pic.setUrl(urls);
			pic.setIsWrokUpdate(0);
			pic.setTypeOfId(typeOfId);
			pic.setIsUploadSuccess(0);
			if (intentFrom == 0 || intentFrom == 1) {
				pic.setPatrolTime(planDetail.getPatrolTime());
			} else {
				pic.setPatrolTime("");
			}
			sqliteHelper.insertPic(pic);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (picSelectedEnsureAdapter.getItemViewType(position) == picSelectedEnsureAdapter.getTYPE_PIC()) {
			Intent intent = new Intent();
			intent.putExtra("showSelect", false);
			intent.putExtra("mCurrentGroup", imageGroup);
			intent.putExtra("mSelectCount", imageGroup.getImageCount());
			intent.putExtra("position", position);
			intent.setClass(PicSelectedEnsureActivity.this, QiAlbumViewFragment.class);
			startActivity(intent);
		} else {
			jumpToPicSelect();
		}
	}
}

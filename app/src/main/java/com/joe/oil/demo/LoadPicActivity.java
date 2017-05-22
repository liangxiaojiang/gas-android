package com.joe.oil.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import com.joe.oil.R;
import com.joe.oil.imagepicker.Config;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.imagepicker.QiAlbumViewFragment;
/**
 * @description 图片选择测试界面
 * @deprecated
 * @author Administrator
 */
public class LoadPicActivity extends Activity {
	private long exitTime;
	private GridView gridView;
	private ImageSelectAdapter mPicSelectAdapter;
	private ImageGroup mCurrentGroup;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.demo);
		Button btn = (Button) this.findViewById(R.id.btn);

		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			btn.setText("点我测试" + "(" + info.versionName + ")");
		} catch (Exception e) {
			btn.setText("点我测试");
		}

		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoadPicActivity.this, ImagePickerActivity.class);
				// 动态设置图片的数量
				Config.setLimit(3);
				// (特殊情况下)设置本机照片存储父目录
				// Config.setPhotoPath ("/sdcard-ext/DCIM");
				startActivityForResult(intent, 0x123);
			}
		});
		gridView = (GridView) this.findViewById(R.id.child_grid);
		mPicSelectAdapter = new ImageSelectAdapter(this);
		gridView.setAdapter(mPicSelectAdapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("showSelect", false);
				intent.putExtra("mCurrentGroup", mCurrentGroup);
				intent.putExtra("mSelectCount", mCurrentGroup.getImageCount());
				intent.putExtra("position", position);
				intent.setClass(LoadPicActivity.this, QiAlbumViewFragment.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x123 && resultCode == RESULT_OK) {
			Intent intent = data;
			mCurrentGroup = (ImageGroup) intent.getSerializableExtra("imageSelected");
			/**
			 * mCurrentGroup.getImageSets () 取得图片集
			 */

			if (mCurrentGroup != null && mCurrentGroup.getImageSets().size() > 0) {
				mPicSelectAdapter.taggle(mCurrentGroup);
				mPicSelectAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// 退出代码
				new Thread(new Runnable() {
					@Override
					public void run() {
					}
				}).start();
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

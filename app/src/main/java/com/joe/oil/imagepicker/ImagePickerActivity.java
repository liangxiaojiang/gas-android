package com.joe.oil.imagepicker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.joe.oil.R;
import com.joe.oil.activity.BaseActivity;
import com.joe.oil.activity.PicSelectedEnsureActivity;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagePickerActivity extends BaseActivity implements OnItemClickListener {

	private static final int PHOTO_REQUEST_CAREMA = 99;// 拍照
	private static final int PHOTO_REQUEST_PREVIEW = 98;// 预览
	private static final int PHOTO_REQUEST_DISPLAY = 97;// 浏览
	static boolean isOpened = false;
	// private File photoFile;
	private static String version;
	private GridView gridView;
	private PicSelectAdapter mPicSelectAdapter;
	private TextView imageGroupBtn;
	private TextView complete;
	private TextView preView;
	private TextView back;
	private PopupWindow popWindow;
	private int selected = 0;
	private int height = 0;
	private List<ImageGroup> mImageGroups;
	private String photoPath;
	private OnImageSelectedCountListener onImageSelectedCountListener = new OnImageSelectedCountListener() {

		@Override
		public int getImageSelectedCount() {
			return selected;
		}
	};
	private OnImageSelectedListener onImageSelectedListener = new OnImageSelectedListener() {

		@Override
		public void notifyChecked() {
			selected = getSelectedCount();
			// 改变完成统计
			complete.setText("完成(" + selected + "/" + Config.limit + ")");
			// 改变预览统计
			if (selected == 0) {
				preView.setText("");
			} else {
//				preView.setText("预览(" + selected + "/" + Config.limit + ")");
			}
		}
	};
	private int lastPosition = 0;
	private Uri photoUri;
	private Cursor mCursor;
	private ImageGroup mCurrentGroup;
	/**
	 * 图片扫描任务
	 */
	private ImageLoadTask mLoadTask = null;
	private ListView selectPopupListView;
	private ImageGroupAdapter mImageGroupAdapter;

	public static Uri getImageContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
			return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
		} else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}

	/**
	 * 收集崩溃信息
	 *
	 * @param context
	 * @param crashReport
	 */
	public static void sendAppCrashReport(Context context, final String crashReport) {

		RequestQueue mRequestQueue = Volley.newRequestQueue(context);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://himfc.sinaapp.com/Mfc/report", new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				StringBuffer exceptionStr = new StringBuffer();
				exceptionStr.append(Environment.getExternalStorageDirectory().getPath() + "\n");
				exceptionStr.append(Environment.getExternalStorageDirectory().getAbsolutePath() + "\n");
				exceptionStr.append(Environment.getExternalStorageDirectory().getName() + "\n");
				exceptionStr.append(Environment.getExternalStorageDirectory().getParent() + "\n");
				exceptionStr.append(Environment.getExternalStorageDirectory().getTotalSpace() + "\n");
				exceptionStr.append(Environment.getExternalStorageDirectory().getFreeSpace() + "\n");

				Map<String, String> map = new HashMap<String, String>();
				map.put("crash", crashReport + exceptionStr.toString());
				map.put("version", version);
				return map;
			}
		};
		mRequestQueue.add(stringRequest);
		mRequestQueue.start();
	}

	private String typeOfId;
	private int intentFrom;
	private PlanDetail planDetail;
	private boolean isModifyPicture = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			version = "1.0";
		}

		intentFrom = getIntent().getIntExtra("intentFrom", 1);
		typeOfId = getIntent().getStringExtra("typeOfId");
		if (intentFrom == 0 || intentFrom == 1) {
			planDetail = (PlanDetail) getIntent().getSerializableExtra("planDetail");
		}
		try {
			isModifyPicture = getIntent().getBooleanExtra("isModifyPicture", false);
		} catch (Exception e) {
			isModifyPicture = false;
		}

		// Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler(this));
		String pathForDCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
		if (DebugConfig.debug)
			Log.v("QiLog", "ImagePickerActivity.onCreate pathForDCIM" + " ~~~ " + pathForDCIM);
		if (DebugConfig.debug)
			Log.v("QiLog", "ImagePickerActivity.onCreate pathForDCIM" + " ~~~ " + Arrays.toString(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).list()));

		RelativeLayout bodyLayout = new RelativeLayout(this);
		LanbaooTop mLanbaooTop = new LanbaooTop(this, "返回", "", "完成");
		mLanbaooTop.setId(110);
		mLanbaooTop.onLeftClicked(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		complete = mLanbaooTop.getmRightBtn();
		RelativeLayout.LayoutParams mLanbaooTopRLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mLanbaooTopRLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		bodyLayout.addView(mLanbaooTop, mLanbaooTopRLP);

		final LanbaooBottom mLanbaooBottom = new LanbaooBottom(this, "", "", "");
		mLanbaooBottom.setId(111);
		RelativeLayout.LayoutParams mLanbaooBottomRLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mLanbaooBottomRLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bodyLayout.addView(mLanbaooBottom, mLanbaooBottomRLP);

		gridView = new GridView(this);
		gridView.setNumColumns(3);
		gridView.setGravity(Gravity.CENTER);
		gridView.setCacheColorHint(Color.TRANSPARENT);
		gridView.setHorizontalSpacing(getResources().getDimensionPixelSize(R.dimen.checkbox_padding));
		gridView.setVerticalSpacing(getResources().getDimensionPixelSize(R.dimen.checkbox_padding));
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setVerticalScrollBarEnabled(false);

		RelativeLayout.LayoutParams mGridViewRLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mGridViewRLP.addRule(RelativeLayout.BELOW, mLanbaooTop.getId());
		mGridViewRLP.addRule(RelativeLayout.ABOVE, mLanbaooBottom.getId());
		mGridViewRLP.bottomMargin = 2;
		bodyLayout.addView(gridView, mGridViewRLP);

		setContentView(bodyLayout);

		back = mLanbaooTop.getmLeftBtn();
		complete = mLanbaooTop.getmRightBtn();
		imageGroupBtn = mLanbaooBottom.getmLeftBtn();
		preView = mLanbaooBottom.getmRightBtn();

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//选择图片的时候下面有个预览
//		preView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//
//				ImageGroup imageGroup = new ImageGroup("预览", getSelectedItem());
//				intent.putExtra("mCurrentGroup", imageGroup);
//				intent.putExtra("mSelectCount", getSelectedCount());
//				intent.putExtra("position", 0);
//				intent.setClass(ImagePickerActivity.this, QiAlbumViewFragment.class);
//				startActivityForResult(intent, PHOTO_REQUEST_PREVIEW);
//			}
//		});

		complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageGroup imageGroup = new ImageGroup("ALL", getSelectedItem());
				if (isModifyPicture) {
					Intent intent = new Intent();
					intent.putExtra("imageSelected", imageGroup);
					intent.putExtra("intentFrom", intentFrom);
					intent.putExtra("typeOfId", typeOfId);
					setResult(RESULT_OK, intent);
				} else {
					Intent intent = new Intent(ImagePickerActivity.this, PicSelectedEnsureActivity.class);
					intent.putExtra("imageSelected", imageGroup);
					intent.putExtra("intentFrom", intentFrom);
					intent.putExtra("typeOfId", typeOfId);
					if (intentFrom == 1) {
						intent.putExtra("planDetail", planDetail);
					}
					startActivity(intent);
				}
				ImagePickerActivity.this.finish();
			}
		});

		imageGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isOpened && popWindow != null) {
					height = getWindow().getDecorView().getHeight();
					WindowManager.LayoutParams ll = getWindow().getAttributes();
					ll.alpha = 0.3f;
					getWindow().setAttributes(ll);
					View parent = getWindow().getDecorView();
					popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
					// popWindow.showAsDropDown(mLanbaooBottom);
				} else {
					if (popWindow != null) {
						popWindow.dismiss();
					}
				}
			}
		});

		mPicSelectAdapter = new PicSelectAdapter(ImagePickerActivity.this, gridView, onImageSelectedCountListener);
		gridView.setAdapter(mPicSelectAdapter);
		mPicSelectAdapter.setOnImageSelectedListener(onImageSelectedListener);
		loadImages();
		gridView.setOnItemClickListener(this);

		photoPath = Config.PHOTO_PATH_OLD_DEVICE;
		File dirDCIM = new File(photoPath);
		// if (!dirDCIM.isDirectory()) {
		// filePath = Config.PHOTO_PATH_SH_DEVICE;
		// dirDCIM = new File(filePath);
		// }
		// if (!dirDCIM.isDirectory()) {
		// filePath = Config.PHOTO_PATH_BJ_DEVICE;
		// dirDCIM = new File(filePath);
		// }
		if (!dirDCIM.isDirectory()) {
			if (Constants.DEVICE_NAME.equals(Constants.DEVICE_MODEL_OF_BEIJIN_STRING)) {
				photoPath = Config.PHOTO_PATH_BJ_DEVICE;
				// photoPath =
				// Environment.getExternalStorageDirectory().toString();
			} else {
				photoPath = Config.PHOTO_PATH_SH_DEVICE;
			}
		}
		Log.d("tag", photoPath);

	}

	@Override
	protected void onDestroy() {
		ImageLoader.getInstance().clearMemoryCache();
		super.onDestroy();
	}

	/**
	 * 调用系统相机拍照
	 */
	private void takePhoto() {
		ContentValues values = new ContentValues();
		// photoUri = getContentResolver ().insert (
		// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String photoName = formatter.format(curDate);

		if (file.isDirectory()) {
			if (DebugConfig.debug)
				Log.v("QiLog", "ImagePickerActivity.takePhoto" + " ~~~ " + "DIRECTORY_DCIM 存在");
		} else {
			if (DebugConfig.debug)
				Log.v("QiLog", "ImagePickerActivity.takePhoto" + " ~~~ " + "DIRECTORY_DCIM 找不到");
		}

		// photoUri = getImageContentUri (this, photoFile);

		// File dirDCIM = new File(Config.getPhotoPath());
		File dirDCIM = new File(photoPath);
		// ContextWrapper cw = new ContextWrapper(this);
		// File dir = cw.getDir(Environment.DIRECTORY_DCIM,
		// Context.MODE_PRIVATE);
		// boolean a = dir.setWritable(true, false);
		//
		// Log.d("tag", a + "");
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || dirDCIM.isDirectory()) {
			// if (!(new File(Config.getPhotoPath() + "/Camera").isDirectory()))
			// {
			// (new File(Config.getPhotoPath() + "/Camera")).mkdirs();
			// }
			// File photoFile = new File(Config.getPhotoPath() + "/Camera/" +
			// photoName + ".jpg");
			if (!(new File(photoPath + "/Camera").isDirectory())) {
				(new File(photoPath + "/Camera")).mkdirs();
			}
			File photoFile = new File(photoPath + "/Camera/" + photoName + ".jpg");
			values.put(MediaStore.Images.Media.DATA, photoFile.getPath());
		}

		photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		if (DebugConfig.debug)
			Log.v("QiLog", "ImagePickerActivity.takePhoto" + " ~~~ " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// getContentResolver().insert(photoUri, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Intent intentCamera = getPackageManager().getLaunchIntentForPackage("com.android.camera");
		if (intentCamera != null) {
			intent.setPackage("com.android.camera");
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, PHOTO_REQUEST_CAREMA);

		// if
		// (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		// {
		// fileName = getFileName ();
		// System.out.println (Environment.getExternalStorageDirectory ()
		// .toString ());
		// System.out.println (Environment.getExternalStorageDirectory ()
		// .getAbsolutePath ());
		// dirPath = Environment.getExternalStorageDirectory ().getPath ()
		// + Config.getSavePath ();
		// File tempFile = new File (dirPath);
		// if (! tempFile.exists ()) {
		// tempFile.mkdirs ();
		// }
		// File saveFile = new File (tempFile, fileName + ".jpg");
		// Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
		// intent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile
		// (saveFile));
		// startActivityForResult (intent, PHOTO_REQUEST_CAREMA);
		//
		// } else {
		// Toast.makeText(ImagePickerActivity.this, "未检测到内存卡，拍照功能可能不可使用!",
		// Toast.LENGTH_SHORT).show();
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PHOTO_REQUEST_DISPLAY) {
			ImageGroup mPreviewGroup = (ImageGroup) data.getExtras().getSerializable("mCurrentGroup");
			if (mCurrentGroup.getImageCount() == mPreviewGroup.getImageCount()) {
				int count = mCurrentGroup.getImageCount();
				for (int i = 0; i < count; i++) {
					mCurrentGroup.getImageSets().get(i).isChecked = mPreviewGroup.getImageSets().get(i).isChecked;
				}
			}
			mPicSelectAdapter.notifyDataSetChanged();
			selected = getSelectedCount();
//			if (selected == 0) {
//				preView.setText("");
//			} else {
//				preView.setText("预览(" + selected + "/" + Config.limit + ")");
//			}
			complete.setText("完成(" + selected + "/" + Config.limit + ")");
		}

		if (requestCode == PHOTO_REQUEST_PREVIEW) {
			ImageGroup mPreviewGroup = (ImageGroup) data.getExtras().getSerializable("mCurrentGroup");
			// if(mCurrentGroup.getImageCount ()==mPreviewGroup.getImageCount
			// ()){
			// int count = mCurrentGroup.getImageCount ();
			// for(int i=0;i<count;i++){
			// mCurrentGroup.getImageSets ().get
			// (i).isChecked=mPreviewGroup.getImageSets ().get (i).isChecked;
			// }
			// } else {
			// int count = mCurrentGroup.getImageCount ();
			// // ArrayList<ImageBean> imageBeans= new ArrayList<ImageBean>
			// (mPreviewGroup.getImageSets ());
			// if(mPreviewGroup.getImageSets ().size
			// ()>0&&mPreviewGroup.getImageSets ().get (0).path==null){
			// mPreviewGroup.getImageSets ().remove (0);
			// }
			// for(int i=0;i<count;i++){
			// ImageBean imageBeanToRemove=new ImageBean ();
			// for(int j=0;j<mPreviewGroup.getImageSets ().size ();j++){
			// String path = mCurrentGroup.getImageSets ().get (i).path;
			// String cpath=mPreviewGroup.getImageSets ().get (j).path;
			// if(path!=null&&cpath!=null&&path.contentEquals (cpath)){
			// mCurrentGroup.getImageSets ().get
			// (i).isChecked=mPreviewGroup.getImageSets ().get (j).isChecked;
			// // imageBeansToRemove.add (mPreviewGroup.getImageSets ().get
			// (j));
			// imageBeanToRemove = mPreviewGroup.getImageSets ().get (j);
			// }
			// }
			// mPreviewGroup.getImageSets ().remove (imageBeanToRemove);
			// if(mPreviewGroup.getImageSets ().size ()==0){
			// break;
			// }
			//
			// }
			// }

			for (ImageBean imagePreview : mPreviewGroup.getImageSets()) {
				if (!imagePreview.isChecked && imagePreview.path != null) {
					List<ImageGroup> subImageGroup = mImageGroups.subList(0, mImageGroups.size());
					ArrayList<ImageBean> beans = new ArrayList<ImageBean>();
					for (ImageGroup imageGroup : subImageGroup) {
						for (ImageBean imageBean : imageGroup.imageSets) {
							if (imageBean.path != null && imageBean.path.contentEquals(imagePreview.path)) {
								imageBean.isChecked = false;
							}
						}
					}
				}
			}

			mPicSelectAdapter.notifyDataSetChanged();
			selected = getSelectedCount();
//			if (selected == 0) {
//				preView.setText("");
//			} else {
//				preView.setText("预览(" + selected + "/" + Config.limit + ")");
//			}
			complete.setText("完成(" + selected + "/" + Config.limit + ")");
		}
		if (requestCode == PHOTO_REQUEST_CAREMA) {
			if (photoUri == null) {
				return;
			}
			if (data == null) {
				Log.d("tag", "data null");
			}
			if (resultCode == RESULT_CANCELED) {
				Log.d("tag", "delete");
				getContentResolver().delete(photoUri, null, null);
				return;
			}
			// 按刚刚指定的那个文件名，查询数据库，获得更多的照片信息，比如图片的物理绝对路径
			// photoUri = getImageContentUri (this,photoFile);
			try {
				mCursor = getContentResolver().query(photoUri, null, null, null, null);
				if (mCursor != null) {
					String path = null;
					Long photoDateAdd = 0L;
					if (mCursor.moveToNext()) {
						path = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
						photoDateAdd = mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
						long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
						String display_name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
						String parentName = new File(path).getParentFile().getName();
						List<ImageGroup> subImageGroup = mImageGroups.subList(1, mImageGroups.size());
						if (subImageGroup.size() > 0) {
							lastPosition = 1;
						} else {
							lastPosition = 0;
						}
						for (ImageGroup imageGroup : subImageGroup) {

							String imageGroupFolderName = imageGroup.getFolderName();
							if (imageGroupFolderName != null && imageGroupFolderName.contentEquals(parentName)) {
								if (selected == Config.getLimit()) {
									imageGroup.getImageSets().add(1, new ImageBean(parentName, size, display_name, path, false));
								} else {
									imageGroup.getImageSets().add(1, new ImageBean(parentName, size, display_name, path, true));
									selected++;
//									if (selected == 0) {
//										preView.setText("");
//									} else {
//										preView.setText("预览(" + selected + "/" + Config.limit + ")");
//									}
									complete.setText("完成(" + selected + "/" + Config.limit + ")");
								}
								mCurrentGroup.isSelected = false;
								mCurrentGroup = imageGroup;
								mCurrentGroup.isSelected = true;
								break;
							}
							lastPosition++;

							// for (ImageBean imageBean : imageGroup.imageSets)
							// {
							// }
						}

						imageGroupBtn.setText(mCurrentGroup.getFolderName().trim());
						mPicSelectAdapter.taggle(mCurrentGroup);
						mPicSelectAdapter.notifyDataSetChanged();

						selectPopupListView.smoothScrollToPosition(lastPosition);
						mImageGroupAdapter.notifyDataSetChanged();

						ImageGroup GroupAll = new ImageGroup();
						for (ImageGroup group : subImageGroup) {
							GroupAll.getImageSets().addAll(group.getImageSets());
						}
						GroupAll.setFolderName("所有图片");
						mImageGroups.set(0, GroupAll);
					}

					if (DebugConfig.debug)
						Log.v("QiLog", "com.lanbaoo.publish.Fragment.LanbaooPublish.onActivityResult" + " ~~~ " + photoDateAdd);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} finally {
				if (mCursor != null) {
					mCursor.close();
				}
			}
		}
	}

	/**
	 * 加载图片
	 */
	private void loadImages() {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// Toast.makeText(ImagePickerActivity.this, "未检测到内存卡!",
			// Toast.LENGTH_SHORT).show();
			// return;
		}

		// 线程正在执行
		if (mLoadTask != null && mLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		}

		mLoadTask = new ImageLoadTask(this, new OnTaskResultListener() {
			@Override
			public void onResult(boolean success, String error, Object result) {
				// 如果加载成功
				if (success && result != null && result instanceof ArrayList) {

					int fileCount = 0;
					String folderName = "";
					// File dirDCIM = new File(Config.getPhotoPath());
					File dirDCIM = new File(photoPath);

					File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
					if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || dirDCIM.isDirectory()) {
						file = dirDCIM;
					}
					if (file.isDirectory()) {
						String[] subDir = file.list();
						if (subDir != null) {
							for (File subFile : file.listFiles()) {
								if (subFile.isDirectory() && !subFile.isHidden()) {
									if (subFile.listFiles().length > fileCount) {
										fileCount = subFile.listFiles().length;
										folderName = subFile.getName();
									}
								}
							}
						}
					}
					mImageGroups = (ArrayList<ImageGroup>) result;
					if (mImageGroups.size() != 0) {
						if (folderName.length() > 0) {
							for (ImageGroup mGroup : mImageGroups) {
								if (folderName.contentEquals(mGroup.getFolderName())) {
									mCurrentGroup = mGroup;
									break;
								}
								lastPosition++;
							}
							if (mCurrentGroup == null) {
								mCurrentGroup = mImageGroups.get(0);
							}
						} else {
							mCurrentGroup = mImageGroups.get(0);
						}
						mCurrentGroup.isSelected = true;
						mPicSelectAdapter.taggle(mCurrentGroup);
						imageGroupBtn.setText(mCurrentGroup.getFolderName().trim());
					}
					popWindow = showPopWindow();
				} else {
					// 加载失败，显示错误提示
					/**
					 * 当设备没有任何图片的时候的处理
					 */
					mImageGroups = new ArrayList<ImageGroup>();

					mImageGroups.add(new ImageGroup());
					mCurrentGroup = mImageGroups.get(0);
					mCurrentGroup.setFolderName("Camera");
					mCurrentGroup.getImageSets().add(new ImageBean());

					mCurrentGroup.isSelected = true;
					mPicSelectAdapter.taggle(mCurrentGroup);
					imageGroupBtn.setText(mCurrentGroup.getFolderName().trim());

					ImageGroup GroupAll = new ImageGroup();
					for (ImageGroup group : mImageGroups) {
						GroupAll.getImageSets().addAll(group.getImageSets());
					}
					GroupAll.setFolderName("所有图片");
					mImageGroups.add(0, GroupAll);

					popWindow = showPopWindow();
					takePhoto();
				}
			}
		});
		TaskUtil.execute(mLoadTask);
	}

	/**
	 * 获取选中的数值
	 *
	 * @return
	 */
	private int getSelectedCount() {
		int count = 0;
		List<ImageGroup> subImageGroup = mImageGroups.subList(1, mImageGroups.size());
		for (ImageGroup imageGroup : subImageGroup) {
			for (ImageBean imageBean : imageGroup.imageSets) {
				if (imageBean.isChecked) {
					count++;
				}
			}
		}
		return count;
	}

	private ArrayList<ImageBean> getSelectedItem() {
		int count = 0;
		if (mImageGroups != null && mImageGroups.size() > 1) {
			List<ImageGroup> subImageGroup = mImageGroups.subList(1, mImageGroups.size());
			ArrayList<ImageBean> beans = new ArrayList<ImageBean>();
			OK: for (ImageGroup imageGroup : subImageGroup) {
				for (ImageBean imageBean : imageGroup.imageSets) {
					if (imageBean.isChecked) {
						beans.add(imageBean);
						count++;
					}
					if (count == Config.limit) {
						break OK;
					}
				}
			}
			return beans;
		} else {
			return new ArrayList<ImageBean>();
		}
	}

	private PopupWindow showPopWindow() {
		ImageSelectPopupView selectPopupView = new ImageSelectPopupView(this);
		final PopupWindow mPopupWindow = new PopupWindow(selectPopupView, ViewGroup.LayoutParams.MATCH_PARENT, 4 * getResources().getDisplayMetrics().heightPixels / 5, true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setAnimationStyle(R.style.DataSheetAnimation);

		selectPopupListView = selectPopupView.getmListView();
		mImageGroupAdapter = new ImageGroupAdapter(this);
		selectPopupListView.setAdapter(mImageGroupAdapter);
		mImageGroupAdapter.setData(mImageGroups);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams ll = getWindow().getAttributes();
				ll.alpha = 1f;
				getWindow().setAttributes(ll);
			}
		});
		// listView.setSelection (lastPosition);
		selectPopupListView.smoothScrollToPosition(lastPosition);
		selectPopupListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (lastPosition != position) {
					ImageGroup mGroup = (ImageGroup) parent.getItemAtPosition(position);
					mCurrentGroup.isSelected = false;
					mCurrentGroup = mGroup;
					mCurrentGroup.isSelected = true;
					mImageGroupAdapter.notifyDataSetChanged();
					mPicSelectAdapter.taggle(mGroup);
					// 更改选中的文字
					imageGroupBtn.setText(mGroup.getFolderName().trim());
					lastPosition = position;
				}
				mPopupWindow.dismiss();
			}
		});
		return mPopupWindow;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			takePhoto();
		} else {
			Intent intent = new Intent();
			intent.putExtra("mCurrentGroup", mCurrentGroup);
			intent.putExtra("mSelectCount", getSelectedCount());
			intent.putExtra("position", position - 1);
			intent.setClass(this, QiAlbumViewFragment.class);
			startActivityForResult(intent, PHOTO_REQUEST_DISPLAY);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			ImageGroup imageGroup = new ImageGroup("ALL", getSelectedItem());
			Intent intent = new Intent();
			intent.putExtra("imageSelected", imageGroup);
			setResult(RESULT_OK, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	public interface OnImageSelectedListener {
		void notifyChecked();
	}

	public interface OnImageSelectedCountListener {
		int getImageSelectedCount();
	}
}

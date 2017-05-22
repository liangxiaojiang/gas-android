/**
 *LanbaooAlbumViewFragment
 *1/21/14 10:20 PM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.joe.oil.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * User: demo
 * wuchangqi@meet-future.com
 * Date: 1/21/14
 * Time: 10:20 PM
 */
public class QiAlbumViewFragment extends FragmentActivity {
	private LanbaooTop mLanbaooTop;
	private LanbaooBottom mLanbaooBottom;
	private ViewPager viewPager;
	//	private int mCurrentPhoto;
	private TextView mSelectedBtn;
	private ImageGroup mCurrentGroup;
	private ImageLoader imageLoader = ImageLoader.getInstance ();
	private LanbaooPagerAdapter mLanbaooPagerAdapter;
	private int lastPage;
	private int nextPage;
	private boolean isDecrease = false;
	private boolean isIncrease = false;
	private int mSelectCount;
	private TextView complete;

	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		requestWindowFeature (Window.FEATURE_NO_TITLE);
		RelativeLayout bodyLayout = new RelativeLayout (this);
		bodyLayout.setBackgroundColor (Color.parseColor ("#000000"));
		bodyLayout.setLayoutParams (new RelativeLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setContentView (bodyLayout);
		getWindow ().setLayout (WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		Intent intent = getIntent ();
		final int position = intent.getExtras ().getInt ("position");
		lastPage = position;

		mCurrentGroup = (ImageGroup) intent.getExtras ().getSerializable ("mCurrentGroup");
		mSelectCount = intent.getExtras ().getInt ("mSelectCount", 0);
		String path = mCurrentGroup.getImageSets ().get (0).path;
		boolean showSelect = true;
		if (path == null || path.length () == 0) {
			mCurrentGroup.getImageSets ().remove (0);
		}
		showSelect = intent.getBooleanExtra ("showSelect", true);
		mLanbaooTop = new LanbaooTop (this, "返回", (position + 1) + "/" + mCurrentGroup.getImageCount (), "完成");
		mLanbaooTop.setId (99);
		RelativeLayout.LayoutParams mLanbaooTopRLP = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLanbaooTopRLP.addRule (RelativeLayout.CENTER_HORIZONTAL);
		mLanbaooTopRLP.addRule (RelativeLayout.ALIGN_PARENT_TOP);
		bodyLayout.addView (mLanbaooTop, mLanbaooTopRLP);

		mLanbaooTop.onLeftClicked (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				Intent intent = new Intent ();
				mCurrentGroup.getImageSets ().add (0, new ImageBean ());
				intent.putExtra ("mCurrentGroup", mCurrentGroup);
				setResult (RESULT_OK, intent);
				finish ();
			}
		});

		complete = mLanbaooTop.getmRightBtn ();

		if (mSelectCount == 0) {
			complete.setText ("");
		} else {
			complete.setText ("完成(" + mSelectCount + "/" + Config.limit + ")");
		}

		complete.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				Intent intent = new Intent ();
				mCurrentGroup.getImageSets ().add (0, new ImageBean ());
				intent.putExtra ("mCurrentGroup", mCurrentGroup);
				setResult (RESULT_OK, intent);
				finish ();
			}
		});

		if (showSelect) {
			mLanbaooBottom = new LanbaooBottom (this, null, null, "选择");
		} else {
			mLanbaooBottom = new LanbaooBottom (this, null, null, null);
		}
		mLanbaooBottom.setId (98);
		RelativeLayout.LayoutParams mLanbaooBottomRLP = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLanbaooBottomRLP.addRule (RelativeLayout.CENTER_HORIZONTAL);
		mLanbaooBottomRLP.addRule (RelativeLayout.ALIGN_PARENT_BOTTOM);
		bodyLayout.addView (mLanbaooBottom, mLanbaooBottomRLP);

		mSelectedBtn = mLanbaooBottom.getmRightBtn ();
		if (showSelect) {
			mSelectedBtn.setCompoundDrawablesWithIntrinsicBounds (getResources ().getDrawable (R.drawable.pictures_select_icon), null, null, null);
		}

		mLanbaooPagerAdapter = new LanbaooPagerAdapter (this, mCurrentGroup);

		viewPager = new ViewPager (this);

		RelativeLayout.LayoutParams mViewPagerRLP = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mViewPagerRLP.addRule (RelativeLayout.CENTER_HORIZONTAL);
		mViewPagerRLP.addRule (RelativeLayout.BELOW, mLanbaooTop.getId ());
		mViewPagerRLP.addRule (RelativeLayout.ABOVE, mLanbaooBottom.getId ());
		bodyLayout.addView (viewPager, mViewPagerRLP);

		viewPager.setAdapter (mLanbaooPagerAdapter);
		viewPager.setCurrentItem (position);

		viewPager.setOnPageChangeListener (new ViewPager.OnPageChangeListener () {
			@Override
			public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
				if (DebugConfig.debug)
					Log.v ("QiLog", "onPageScrolled" + " ~~~ " + position + " xxx " + positionOffset);
				if (position == lastPage && positionOffset >= 0.5) {
					if (mCurrentGroup.getImageSets ().get (lastPage + 1).isChecked) {
						mSelectedBtn.setSelected (true);
					} else {
						mSelectedBtn.setSelected (false);
					}
					mLanbaooTop.setText ((lastPage + 1 + 1) + "/" + mCurrentGroup.getImageCount ());
					isIncrease = true;
				} else {
					if (isIncrease) {
						if (mCurrentGroup.getImageSets ().get (lastPage + 1).isChecked) {
							mSelectedBtn.setSelected (true);
						} else {
							mSelectedBtn.setSelected (false);
						}
						mLanbaooTop.setText ((lastPage + 1 + 1) + "/" + mCurrentGroup.getImageCount ());
						isIncrease = false;
					}
				}
				if (position < lastPage && positionOffset <= 0.5) {
					if (mCurrentGroup.getImageSets ().get (position).isChecked) {
						mSelectedBtn.setSelected (true);
					} else {
						mSelectedBtn.setSelected (false);
					}
					mLanbaooTop.setText ((position + 1) + "/" + mCurrentGroup.getImageCount ());
					isDecrease = true;
				} else {
					if (isDecrease) {
						if (mCurrentGroup.getImageSets ().get (lastPage).isChecked) {
							mSelectedBtn.setSelected (true);
						} else {
							mSelectedBtn.setSelected (false);
						}
						mLanbaooTop.setText ((lastPage + 1) + "/" + mCurrentGroup.getImageCount ());
						isDecrease = false;
					}
				}
				if (positionOffset == 0.0) {
					lastPage = position;
					if (mCurrentGroup.getImageSets ().get (lastPage).isChecked) {
						mSelectedBtn.setSelected (true);
					} else {
						mSelectedBtn.setSelected (false);
					}
					mLanbaooTop.setText ((lastPage + 1) + "/" + mCurrentGroup.getImageCount ());
				}
			}

			@Override
			public void onPageSelected (int position) {
				if (DebugConfig.debug) Log.v ("QiLog", "onPageSelected position" + " ~~~ " + position);
//				nextPage = position;
//				if (lastPage > position) {//Move to left
//				} else {//Move to right
//				};
//				lastPage = position;
			}

			@Override
			public void onPageScrollStateChanged (int state) {
				if (DebugConfig.debug)
					Log.v ("QiLog", "onPageScrollStateChanged state " + " ~~~ " + state);
				if (state == 2) {
//					lastPage = nextPage;
					if (mCurrentGroup.getImageSets ().get (lastPage).isChecked) {
						mSelectedBtn.setSelected (true);
					} else {
						mSelectedBtn.setSelected (false);
					}
					mLanbaooTop.setText ((lastPage + 1) + "/" + mCurrentGroup.getImageCount ());
				}
			}
		});

		mLanbaooBottom.onRightClicked (new View.OnClickListener () {
			@Override
			public void onClick (View v) {

				ImageBean imageBean = mCurrentGroup.getImageSets ().get (lastPage);
				if (imageBean.isChecked) {
					imageBean.isChecked = false;
					mSelectedBtn.setSelected (false);
					mSelectCount--;
				} else {
					if (mSelectCount == Config.getLimit ()) {
						Toast.makeText (QiAlbumViewFragment.this,
								"最多只能选择" + Config.getLimit () + "张图片",
								Toast.LENGTH_SHORT).show ();
					} else {
						imageBean.isChecked = true;
						mSelectedBtn.setSelected (true);
						mSelectCount++;
					}
				}

				if (mSelectCount == 0) {
					complete.setText ("");
				} else {
					complete.setText ("完成(" + mSelectCount + "/" + Config.limit + ")");
				}

				mLanbaooPagerAdapter.notifyDataSetChanged ();

			}
		});
	}


	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction () == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent ();
			mCurrentGroup.getImageSets ().add (0, new ImageBean ());
			intent.putExtra ("mCurrentGroup", mCurrentGroup);
			setResult (RESULT_OK, intent);
			finish ();
			return true;
		}
		return super.onKeyDown (keyCode, event);
	}

	@Override
	protected void onStop () {
		super.onStop ();
	}

	@Override
	protected void onPause () {
		super.onPause ();
	}

	@Override
	protected void onDestroy () {
		super.onDestroy ();
		imageLoader.clearMemoryCache ();
	}
}

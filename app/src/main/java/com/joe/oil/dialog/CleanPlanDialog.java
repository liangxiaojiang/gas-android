package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

public class CleanPlanDialog extends Dialog implements OnClickListener{
	private TextView cancel;
	private TextView confirm;
	private Context context;
	private SqliteHelper sqliteHelper;
	private SharedPreferences sPreferences;
	private Editor editor;

	public CleanPlanDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_clean_plan);
		
		this.context = context;
		
		initView();
		initMembers();
	}
	
	private void initView(){
		confirm = (TextView) this.findViewById(R.id.dialog_clean_plan_confirm);
		cancel = (TextView) this.findViewById(R.id.dialog_clean_plan_cancel);
		
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	
	private void initMembers(){
		sqliteHelper = new SqliteHelper(context);
		sPreferences = context.getSharedPreferences("oil", 0);
		editor = sPreferences.edit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_clean_plan_cancel:
			dismiss();
			break;
			
		case R.id.dialog_clean_plan_confirm:
			sqliteHelper.cleanAllPlan();
			editor.remove(Constants.IS_DOWNLOAD_PLAN);
			editor.remove(Constants.STATION_COUNT);
			editor.remove(Constants.WELL_COUNT);
			editor.remove(Constants.CURRENT_STATION);
			editor.remove(Constants.CURRENT_WELL);
			editor.commit();
			Constants.showToast(context, "清除成功！");
			dismiss();
			break;

		default:
			break;
		}
	}

}

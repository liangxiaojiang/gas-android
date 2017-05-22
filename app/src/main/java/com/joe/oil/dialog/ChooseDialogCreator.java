package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.TextView;
import com.joe.oil.R;

/**
 * 自定义选择框
 * Created by scar1et on 15-7-30.
 */
public class ChooseDialogCreator implements View.OnClickListener {

    private Context context;
    private Dialog dialog;

    private View view;

    private TextView tvOne;
    private TextView tvTow;
    private TextView tvThree;
    private TextView tvFour;
    private TextView tvFive;
    private TextView tvCancel;

    private OnChooseListener onChooseListener;
    private OnChooseThreeListener onChooseThreeListener;
    private OnChooseFourListener onChooseFourListener;
    private OnChooseFiveListener onChooseFiveListener;

    public Dialog createDialog(Context context, String one, String tow, OnChooseListener onChooseListener) {

        this.context = context;
        this.onChooseListener = onChooseListener;

        initView();
        initDataSet(one, tow);
        initEvent();

        return dialog;
    }

    public void setOnChooseThreeListener(OnChooseThreeListener onChooseThreeListener, String three) {
        tvThree.setText(three);
        tvThree.setVisibility(View.VISIBLE);
        this.onChooseThreeListener = onChooseThreeListener;
    }

    public void setOnChooseFourListener(OnChooseFourListener onChooseFourListener, String four) {
        tvFour.setText(four);
        tvFour.setVisibility(View.VISIBLE);
        this.onChooseFourListener = onChooseFourListener;
    }

    public void setOnChooseFiveListener(OnChooseFiveListener onChooseFiveListener, String five) {
        tvFive.setText(five);
        tvFive.setVisibility(View.VISIBLE);
        this.onChooseFiveListener = onChooseFiveListener;
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();

        switch (v.getId()) {
            case R.id.tv_one:
                onChooseListener.onOneClick();
                break;
            case R.id.tv_tow:
                onChooseListener.onTowClick();
                break;
            case R.id.tv_three:
                onChooseThreeListener.onThreeClick();
                break;
            case R.id.tv_four:
                onChooseFourListener.onFourClick();
                break;
            case R.id.tv_five:
                onChooseFiveListener.onFiveClick();
                break;
            case R.id.tv_cancel:
                onChooseListener.onCancelClick();
                break;
            default:
                break;
        }
    }

    private void initEvent() {

        tvCancel.setOnClickListener(this);
        tvOne.setOnClickListener(this);
        tvTow.setOnClickListener(this);
        tvThree.setOnClickListener(this);
        tvFour.setOnClickListener(this);
        tvFive.setOnClickListener(this);
    }

    private void initDataSet(String one, String tow) {

        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.2f;
        lp.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);

        tvOne.setText(one);
        tvTow.setText(tow);
    }

    private void initView() {

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dialog_choose, null);

        tvOne = (TextView) view.findViewById(R.id.tv_one);
        tvTow = (TextView) view.findViewById(R.id.tv_tow);
        tvThree = (TextView) view.findViewById(R.id.tv_three);
        tvFour = (TextView) view.findViewById(R.id.tv_four);
        tvFive = (TextView) view.findViewById(R.id.tv_five);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

        dialog = new Dialog(context, R.style.Theme_DataSheet);
    }

    public interface OnChooseListener {

        void onOneClick();

        void onTowClick();

        void onCancelClick();
    }

    public interface OnChooseThreeListener {

        void onThreeClick();
    }

    public interface OnChooseFourListener {

        void onFourClick();
    }

    public interface OnChooseFiveListener {

        void onFiveClick();
    }
}

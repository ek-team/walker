package com.pharosmed.walker.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.utils.SPHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/6/3
 * Describe:
 */
public class DoctorInfoActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.img_wx)
    ImageView imgWx;
    @BindView(R.id.ll_wx)
    LinearLayout llWx;
    @BindView(R.id.tv_doctor_name)
    TextView tvDoctorName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_work_years)
    TextView tvWorkYears;
    @BindView(R.id.tv_hospital_name)
    TextView tvHospitalName;
    @BindView(R.id.tv_hospital_tel)
    TextView tvHospitalTel;
    private UserBean userBean;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        userBean = SPHelper.getUser();
    }

    private void initView() {
        if (!TextUtils.isEmpty(userBean.getDoctor())){
            tvDoctorName.setText(userBean.getDoctor());
            tvHospitalName.setText(userBean.getHospitalName());
        }else {
            llWx.setVisibility(View.VISIBLE);
            imgWx.setImageResource(R.mipmap.ic_wx_app);
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_info;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}

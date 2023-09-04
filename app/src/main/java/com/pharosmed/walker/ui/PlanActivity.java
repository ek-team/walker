package com.pharosmed.walker.ui;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.customview.NoScrollViewPager;
import com.pharosmed.walker.fragment.InfoFragment;
import com.pharosmed.walker.fragment.TrainPlanFragment;
import com.pharosmed.walker.fragment.TrainRecordFragment;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.SqlToExcleUtil;
import com.pharosmed.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class PlanActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.pager)
    NoScrollViewPager pager;
    private TrainPlanFragment trainPlanFragment;
    private TrainRecordFragment recordFragment;
    private InfoFragment infoFragment;
    private List<Fragment> mFragment = new ArrayList<>(2);

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();

    }

    private void initView() {
        String[] mTitles = {getResources().getString(R.string.info),
                getResources().getString(R.string.plan),
                getResources().getString(R.string.record)};
        tab.setTabData(mTitles);
        infoFragment = new InfoFragment();
        trainPlanFragment = new TrainPlanFragment();
        recordFragment = new TrainRecordFragment();
        mFragment.add(infoFragment);
        mFragment.add(trainPlanFragment);
        mFragment.add(recordFragment);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOffscreenPageLimit(2);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                pager.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void initData() {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                setPoint(false);
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(),battery.getBatteryStatus());
                break;
            default:
                break;
        }
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_plan;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}

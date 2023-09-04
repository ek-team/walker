package com.pharosmed.walker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.Battery;
import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.constants.MessageEvent;
import com.pharosmed.walker.database.UserManager;
import com.pharosmed.walker.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/6/2
 * Describe:
 */
public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_edit_name)
    ImageView ivEditName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.iv_edit_age)
    ImageView ivEditAge;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.iv_edit_weight)
    ImageView ivEditWeight;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.layout_personal_info)
    LinearLayout layoutPersonalInfo;
    @BindView(R.id.layout_about_us)
    LinearLayout layoutAboutUs;
    @BindView(R.id.list_system_message)
    RecyclerView listSystemMessage;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        String[] mTitles = {getResources().getString(R.string.personal_info),
                getResources().getString(R.string.about_us),
                getResources().getString(R.string.system_message)};
        tab.setTabData(mTitles);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        layoutPersonalInfo.setVisibility(View.VISIBLE);
                        layoutAboutUs.setVisibility(View.GONE);
                        listSystemMessage.setVisibility(View.GONE);
                        break;
                    case 1:
                        layoutPersonalInfo.setVisibility(View.GONE);
                        layoutAboutUs.setVisibility(View.VISIBLE);
                        listSystemMessage.setVisibility(View.GONE);
                        break;
                    case 2:
                        layoutPersonalInfo.setVisibility(View.GONE);
                        layoutAboutUs.setVisibility(View.GONE);
                        listSystemMessage.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        tvName.setText(SPHelper.getUser().getAccount());
        tvWeight.setText(SPHelper.getUser().getTelephone());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_info;
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
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery  = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            default:
                break;
        }
    }
    @OnClick({R.id.iv_back, R.id.tv_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_exit:
                Global.USER_MODE = false;
                UserBean userBean = UserManager.getInstance().loadGuest();
                SPHelper.saveUser(userBean);
                startTargetActivity(MainActivity.class,true);

                break;
        }
    }
}

package com.pharosmed.walker.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.tv_device_id)
    TextView tvDeviceId;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.et_verification_code)
    EditText etVerificationCode;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.btn_account_login)
    TextView btnAccountLogin;
    @BindView(R.id.iv_weixin)
    ImageView ivWeixin;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_see)
    ImageView ivSee;
    @BindView(R.id.tv_forget)
    TextView tvForget;
    @BindView(R.id.btn_acc_login)
    TextView btnAccLogin;
    @BindView(R.id.btn_code_login)
    TextView btnCodeLogin;
    @BindView(R.id.iv_weixin_2)
    ImageView ivWeixin2;
    @BindView(R.id.layout_account)
    LinearLayout layoutAccount;
    @BindView(R.id.tv_membership_agreement)
    TextView tvMembershipAgreement;
    @BindView(R.id.tv_privacy_policy)
    TextView tvPrivacyPolicy;
    private UserBean userBean;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userBean = bundle.getParcelable("userInfo");
        }
    }

    private void initView() {
        btnCodeLogin.setVisibility(View.GONE);
        ivWeixin2.setVisibility(View.GONE);
        if(userBean != null){
            etAccount.setText(userBean.getAccount());
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }


    @OnClick({R.id.tv_get_code, R.id.btn_login, R.id.btn_account_login, R.id.iv_weixin, R.id.iv_see, R.id.tv_forget, R.id.btn_acc_login, R.id.btn_code_login, R.id.tv_membership_agreement, R.id.tv_privacy_policy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_get_code:
                break;
            case R.id.btn_login:
                break;
            case R.id.btn_account_login:
                break;
            case R.id.iv_weixin:
                break;
            case R.id.iv_see:
                break;
            case R.id.tv_forget:
                break;
            case R.id.btn_acc_login:
                if (!TextUtils.isEmpty(userBean.getPassword())){
                    if (!etAccount.getText().toString().equals(userBean.getAccount())){
                        ToastUtils.showShort("用户名不对");
                        return;
                    }
                    if(!etPassword.getText().toString().equals(userBean.getPassword())){
                        ToastUtils.showShort("密码不对");
                        return;
                    }
                }
                Global.USER_MODE = true;
                SPHelper.saveUser(userBean);
                startTargetActivity(MainActivity.class,false);
                break;
            case R.id.btn_code_login:

                break;
            case R.id.tv_membership_agreement:
                Bundle bundle = new Bundle();
                bundle.putInt("select_value",0);
                startTargetActivity(bundle,PrivacyPolicyActivity.class,false);
                break;
            case R.id.tv_privacy_policy:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("select_value",1);
                startTargetActivity(bundle1,PrivacyPolicyActivity.class,false);
                break;
        }
    }
}

package com.pharosmed.walker.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.constants.AppKeyManager;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharosmed.walker.database.UserManager;
import com.pharosmed.walker.utils.ToastUtils;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_step_1)
    TextView tvStep1;
    @BindView(R.id.tv_step_2)
    TextView tvStep2;
    @BindView(R.id.tv_step_3)
    TextView tvStep3;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_pwd_see)
    ImageView ivPwdSee;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.iv_confirm_pwd_see)
    ImageView ivConfirmPwdSee;
    @BindView(R.id.tv_step_1_ok)
    TextView tvStep1Ok;
    @BindView(R.id.layout_sub_step_1)
    LinearLayout layoutSubStep1;
    @BindView(R.id.layout_step_1)
    RelativeLayout layoutStep1;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_age)
    EditText etAge;
    @BindView(R.id.et_weight)
    EditText etWeight;
    @BindView(R.id.rb_male)
    RadioButton rbMale;
    @BindView(R.id.rb_female)
    RadioButton rbFemale;
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_step_2_previous)
    TextView tvStep2Previous;
    @BindView(R.id.tv_step_2_ok)
    TextView tvStep2Ok;
    @BindView(R.id.layout_step_2)
    LinearLayout layoutStep2;
    @BindView(R.id.tv_hospital_address)
    TextView tvHospitalAddress;
    @BindView(R.id.tv_hospital_name)
    TextView tvHospitalName;
    @BindView(R.id.tv_doctor)
    TextView tvDoctor;
    @BindView(R.id.tv_step_3_previous)
    TextView tvStep3Previous;
    @BindView(R.id.tv_step_3_finish)
    TextView tvStep3Finish;
    @BindView(R.id.layout_step_3)
    LinearLayout layoutStep3;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.et_CaseHistoryNum)
    EditText etCaseHistoryNum;
    @BindView(R.id.sp_select_diagnostic_result)
    Spinner spinner;
    private String provinceName;
    private String cityName;
    private String countyName;
    private int sex = 0; //性别  男-1  女-0
    private String account;
    private String password;
    private String name;
    private String age;
    private String weight;
    private String city;
    private String doctor;
    private String mobile;
    private String openid;
    private List<Province> provincesList;
    private AlertDialog provinceDialog;
    private Context context;
    private boolean isHidenPwd = true;
    private boolean isHidenConfirmPwd = true;
    private DatePickerDialog mDatePickerDialog;
    private UserBean userBean;
    private String hospitalAddress;
    private String hospitalName;
    private int selectPosition;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }



    private void initView() {
        rgSex.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_male:
                    sex = 1;
                    break;
                case R.id.rb_female:
                    sex = 0;
                    break;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white_88));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initDataTime();
    }
    private void initData() {
        if (userBean == null){
            userBean = new UserBean();
        }

    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_register;
    }

    @OnClick({R.id.iv_back, R.id.iv_pwd_see, R.id.tv_step_1_ok, R.id.tv_location, R.id.tv_hospital_address, R.id.tv_hospital_name,
            R.id.tv_step_2_previous, R.id.tv_step_3_previous, R.id.tv_step_3_finish,R.id.tv_step_2_ok, R.id.iv_confirm_pwd_see, R.id.tv_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_pwd_see:
                if (isHidenPwd){
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    ivPwdSee.setImageResource(R.drawable.ic_pwd_enable);
                }else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    ivPwdSee.setImageResource(R.drawable.ic_pwd_unable);
                }
                isHidenPwd = !isHidenPwd;
                break;
            case R.id.tv_step_1_ok:
                firstCommit();
                break;
            case R.id.tv_location:
                addressSelect();
                break;
            case R.id.tv_hospital_address:
                break;
            case R.id.tv_hospital_name:
                break;
            case R.id.tv_step_3_previous:
                setDisplayView(layoutStep2,tvStep2);
                break;
            case R.id.tv_step_3_finish:
                finishCommit();
                break;
            case R.id.tv_step_2_previous:
                setDisplayView(layoutStep1,tvStep1);
                break;
            case R.id.tv_step_2_ok:
                secondCommit();
                break;
            case R.id.iv_confirm_pwd_see:
                if (isHidenConfirmPwd){
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    ivConfirmPwdSee.setImageResource(R.drawable.ic_pwd_enable);
                }else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    ivConfirmPwdSee.setImageResource(R.drawable.ic_pwd_unable);
                }
                isHidenConfirmPwd = !isHidenConfirmPwd;
                break;
            case R.id.tv_date:
                if (mDatePickerDialog != null) {
                    mDatePickerDialog.show();
                }
                break;
        }
    }

    private void firstCommit(){
        account = etAccount.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showShort("请输入用户名");
            return;
        }
        if (account.length() < 2 || account.length() > 25) {
            ToastUtils.showShort("用户名为2-25位字符");
            return;
        }

        password = etPassword.getText().toString().trim();
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.showShort("请输入密码");
//            return;
//        }
        if (!TextUtils.isEmpty(password) && password.length() < 4 || password.length() > 20) {
            ToastUtils.showShort("密码为4-20位字符");
            return;
        }

        String confirm_password = etConfirmPassword.getText().toString().trim();
//        if (TextUtils.isEmpty(confirm_password)) {
//            ToastUtils.showShort("请确认密码");
//            return;
//        }
        if (!password.equals(confirm_password)) {
            ToastUtils.showShort("两次密码输入不一致");
            return;
        }
        setDisplayView(layoutStep2,tvStep2);
    }
    private void secondCommit(){
        name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("请输入姓名");
            return;
        }
        age = etAge.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            ToastUtils.showShort("请输入年龄");
            return;
        }
        if (Integer.parseInt(age) < 1 || Integer.parseInt(age) > 150) {
            ToastUtils.showShort("年龄不符合范围");
            return;
        }
        weight = etWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weight)) {
            ToastUtils.showShort("请输入体重");
            return;
        }
        if (Integer.parseInt(weight) <35 || Integer.parseInt(weight) > 100) {
            ToastUtils.showShort("体重必须在35kg到100kg之间");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            ToastUtils.showShort("请选择个人所在地");
            return;
        }
        setDisplayView(layoutStep3,tvStep3);
    }
    private void finishCommit(){
        if (TextUtils.isEmpty(etCaseHistoryNum.getText().toString())) {
            ToastUtils.showShort("请输入病历号");
            return;
        }
        userBean.setCaseHistoryNo(etCaseHistoryNum.getText().toString());
        userBean.setWeight(weight);
        userBean.setAccount(account);
        userBean.setPassword(password);
        userBean.setAddress(city);
        userBean.setAge(Integer.parseInt(age));
        userBean.setHospitalAddress(hospitalAddress);
        userBean.setHospitalName(hospitalName);
        userBean.setDoctor(doctor);
        userBean.setName(name);
        Resources res = getResources();
        String[] array = res.getStringArray(R.array.diagnostic_result_list);
        userBean.setDiagnosis(array[selectPosition]);
        userBean.setTelephone(mobile);
        UserManager.getInstance().insert(userBean,0);
        goEvaluateDialog();
    }
    private void goEvaluateDialog(){
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否开始评估");
        dialog.setCancel("取消");
        dialog.setSure("开始评估");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
            startTargetActivity(bundle,ConnectDeviceActivity.class,true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class,true);
        });
        dialog.show();
    }
    private void setDisplayView(LinearLayout layout,TextView textView){
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.GONE);
        layoutStep3.setVisibility(View.GONE);
        tvStep1.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep2.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep3.setBackgroundResource(R.drawable.round_empty_bg);
        layout.setVisibility(View.VISIBLE);
        textView.setBackgroundResource(R.drawable.round_orange_bg);

    }
    private void setDisplayView(RelativeLayout layout,TextView textView){
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.GONE);
        layoutStep3.setVisibility(View.GONE);
        tvStep1.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep2.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep3.setBackgroundResource(R.drawable.round_empty_bg);
        layout.setVisibility(View.VISIBLE);
        textView.setBackgroundResource(R.drawable.round_orange_bg);

    }
    private void addressSelect(){
        provincesList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            String json = ConvertUtils.toString(getAssets().open("city.json"));
            provincesList.addAll(new Gson().fromJson(json,new TypeToken<List<Province>>(){}.getType()));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        if (provincesList.size() != 0) {
            builder.setTitle("选择省份");
            List<String> provinces = new ArrayList<>();
            for (Province province : provincesList) {
                provinces.add(province.getAreaName());
            }
            builder.setItems(provinces.toArray(new String[provinces.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Province selectProvince = provincesList.get(which);
                    provinceName = selectProvince.getAreaName();

                    List<City> citiesList = selectProvince.getCities();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("选择城市");
                    ArrayList<String> cities = new ArrayList<>();
                    for (City city : citiesList) {
                        cities.add(city.getAreaName());
                    }
                    builder.setItems(cities.toArray(new String[cities.size()]), (dialog12, which12) -> {
                        City selectCity = citiesList.get(which12);
                        cityName = citiesList.get(which12).getAreaName();

                        List<County> countiesList = selectCity.getThirds();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("选择区县");
                        ArrayList<String> counties = new ArrayList<>();
                        for (County county : countiesList) {
                            counties.add(county.getAreaName());
                        }
                        builder1.setItems(counties.toArray(new String[counties.size()]), (dialog1, which1) -> {
                            countyName = countiesList.get(which1).getAreaName();
                            city = provinceName + "/" + cityName + "/" + countyName;
                            tvLocation.setText(city);
                        });
                        builder1.show();
                    });
                    builder.show();
                }
            });
            if (provinceDialog == null) {
                provinceDialog = builder.show();
            } else {
                if (!provinceDialog.isShowing()) {
                    provinceDialog = builder.show();
                }
            }
        }
    }
    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(context,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
                    DateTime curDate = DateTime.now();
                    if (a.isAfter(curDate)) {
                        year = curDate.getYear();
                        month = curDate.getMonthOfYear();
                        dayOfMonth = curDate.getDayOfMonth();
                    }
                    userBean.setDate(new DateTime(year, month, dayOfMonth, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour()).toString("yyyy-MM-dd HH:mm:ss"));
                    tvDate.setText(year + "-" + month + "-" + dayOfMonth);
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }
}

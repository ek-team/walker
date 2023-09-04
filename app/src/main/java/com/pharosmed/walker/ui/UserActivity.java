package com.pharosmed.walker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.MainActivity;
import com.pharosmed.walker.R;
import com.pharosmed.walker.adapter.UsersAdapter;
import com.pharosmed.walker.constants.Global;
import com.pharosmed.walker.customview.GridSpacingItemDecoration;
import com.pharosmed.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharosmed.walker.database.UserManager;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/25
 * Describe:
 */
public class UserActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.add)
    Button add;
    private UsersAdapter mAdapter;
    private UserBean userBean;
    private UserManager mUserManager;
    private int page = 0;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        registerForContextMenu(mRecyclerView);
        mAdapter = new UsersAdapter(this);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 20, true));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            UserBean userBean = mAdapter.getData().get(position);
            if (userBean.getId() == 0 && userBean.getCaseHistoryNo().equals("123456")){
                Global.USER_MODE = false;
                SPHelper.saveUser(userBean);
                startTargetActivity(MainActivity.class,true);
            }else {
                Global.USER_MODE = true;
                Bundle bundle = new Bundle();
                bundle.putParcelable("userInfo",userBean);
                startTargetActivity(bundle,LoginActivity.class,true);
            }
        });
    }

    private void initData() {
        mUserManager = UserManager.getInstance();
        List<UserBean> userBeanList = mUserManager.loadAll();
        if (userBeanList != null && userBeanList.size() <= 0){
            startTargetActivity(UserEditActivity.class,true);
        }
        if (userBeanList != null && userBeanList.size() > 0){
            mAdapter.setData(userBeanList);
        }

    }

    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        userBean = mAdapter.getData().get(mAdapter.getPosition());
        switch (item.getItemId()) {
            case R.id.item_delete:
                if (userBean.getId() == 0 && userBean.getCaseHistoryNo().equals("123456")){
                    ToastUtils.showShort("系统用户不能删除！");
                }else {
                    deleteDialog();
                }
                break;
            case R.id.item_update:
                SPHelper.saveUser(userBean);
                Intent intent = new Intent(this,UserEditActivity.class);
                intent.putExtra("Mode",1);
                startActivity(intent);
                break;
            case R.id.item_query:
                SPHelper.saveUser(userBean);
                Intent intent1 = new Intent(this,UserEditActivity.class);
                intent1.putExtra("Mode",2);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    protected void deleteDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否删除用户？");
        dialog.setSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserManager.deleteById(userBean.getUserId());
                mAdapter.setData(mUserManager.loadAll());
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user;
    }
    @Override
    protected void onResume() {
        initData();
//        mAdapter.setData(mUserManager.loadAll());
        super.onResume();
    }

    @OnClick({R.id.iv_back, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.add:
                startTargetActivity(RegisterActivity.class,true);
                break;
        }
    }
}

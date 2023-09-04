package com.pharosmed.walker.database;

import com.pharosmed.walker.beans.UserBean;
import com.pharosmed.walker.constants.PlanTemplate;
import com.pharosmed.walker.greendao.UserBeanDao;
import com.pharosmed.walker.utils.DateFormatUtil;
import com.pharosmed.walker.utils.GreenDaoHelper;
import com.pharosmed.walker.utils.SPHelper;
import com.pharosmed.walker.utils.SnowflakeIdUtil;

import java.util.List;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class UserManager {
    private final static int limit = 20;
    private static volatile UserManager instance = null;

    private UserBeanDao mUserDao;

    private UserManager() {
        mUserDao = GreenDaoHelper.getDaoSession().getUserBeanDao();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }
    /**
     * 查询所有用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadAll() {
        return mUserDao.loadAll();
    }
    /**
     * 查询所有用户信息
     *
     * @param
     * @return
     */
    public List<UserBean> loadAllExceptGuest() {
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.CaseHistoryNo.notEq("123456")).list();
    }
    /**
     * 查询访客信息
     *
     * @param
     * @return
     */
    public UserBean loadGuest() {
        return mUserDao.queryBuilder().where(UserBeanDao.Properties.Id.eq(0),UserBeanDao.Properties.CaseHistoryNo.eq("123456")).unique();
    }

    /**
     * 添加新用户
     *
     * @param userBean
     * @return
     */
    public void insert(UserBean userBean, int mode) {
//        userBean.setPingYin(MyFunc.toPinyin(userBean.getName()));
        if (mode == 0){
            userBean.setCreateDate(DateFormatUtil.getNowDate());
            userBean.setUserId(SnowflakeIdUtil.getUniqueId());
            mUserDao.insert(userBean);
            SPHelper.saveUser(userBean);
//            TrainPlanManager.getInstance().insertList(SnowflakeIdUtil.getUniqueId());
        }else {
            userBean.setUpdateDate(DateFormatUtil.getNowDate());
            mUserDao.update(userBean);
        }
    }
    /**
     * 添加新用户
     *
     * @param id
     * @return
     */
    public UserBean initUser(Long id) {
        if (mUserDao.queryBuilder().where(UserBeanDao.Properties.Id.eq(id)).list().size() > 0){
            return mUserDao.queryBuilder().where(UserBeanDao.Properties.Id.eq(id)).unique();
        }
        UserBean userBean = new UserBean();
        userBean.setId(id);
        userBean.setCaseHistoryNo("123456");
        userBean.setCreateDate(DateFormatUtil.getNowDate());
        userBean.setSex(0);
        userBean.setName("访客");
        userBean.setAge(50);
        userBean.setDiagnosis("通用");
        userBean.setWeight("60");
        userBean.setUserId(SnowflakeIdUtil.getUniqueId());
        userBean.setDate(DateFormatUtil.getNowDate());
        mUserDao.insert(userBean);
        return userBean;
    }
    /**
     * 修改用户信息
     *
     * @param
     * @return
     */
    public void update(UserBean userBean) {
//        userBean.setPingYin(MyFunc.toPinyin(userBean.getName()));
        userBean.setUpdateDate(DateFormatUtil.getNowDate());
        mUserDao.update(userBean);
    }
    /**
     * 根据ID删除用户
     *
     * @param id
     */
    public void deleteById(long id) {
        UserBean user = mUserDao.load(id);

        mUserDao.delete(user);
    }
    /**
     * 分页加载数据
     *
     * @param page
     * @return
     */
    public List<UserBean> load(int page) {
        return mUserDao.queryBuilder().offset(page * limit).limit(limit).list();
    }
    public boolean isUniqueValue(String value, int mode){
        List<UserBean> userBeans = mUserDao.queryBuilder().where(UserBeanDao.Properties.CaseHistoryNo.eq(value)).list();
        if (mode == 0 && userBeans.size() <= 0){
            return true;
        }
        return mode != 0 && userBeans.size() <= 1;
    }
}

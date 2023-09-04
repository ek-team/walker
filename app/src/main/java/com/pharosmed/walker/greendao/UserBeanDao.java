package com.pharosmed.walker.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.pharosmed.walker.beans.UserBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER".
*/
public class UserBeanDao extends AbstractDao<UserBean, Long> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity UserBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property CaseHistoryNo = new Property(3, String.class, "caseHistoryNo", false, "CASE_HISTORY_NO");
        public final static Property Age = new Property(4, int.class, "age", false, "AGE");
        public final static Property Date = new Property(5, String.class, "date", false, "DATE");
        public final static Property Sex = new Property(6, int.class, "sex", false, "SEX");
        public final static Property Diagnosis = new Property(7, String.class, "diagnosis", false, "DIAGNOSIS");
        public final static Property Photo = new Property(8, String.class, "photo", false, "PHOTO");
        public final static Property Doctor = new Property(9, String.class, "doctor", false, "DOCTOR");
        public final static Property HospitalName = new Property(10, String.class, "hospitalName", false, "HOSPITAL_NAME");
        public final static Property HospitalAddress = new Property(11, String.class, "hospitalAddress", false, "HOSPITAL_ADDRESS");
        public final static Property Address = new Property(12, String.class, "address", false, "ADDRESS");
        public final static Property Telephone = new Property(13, String.class, "telephone", false, "TELEPHONE");
        public final static Property Linkman = new Property(14, String.class, "linkman", false, "LINKMAN");
        public final static Property PingYin = new Property(15, String.class, "pingYin", false, "PING_YIN");
        public final static Property CreateDate = new Property(16, String.class, "createDate", false, "CREATE_DATE");
        public final static Property UpdateDate = new Property(17, String.class, "updateDate", false, "UPDATE_DATE");
        public final static Property Remark = new Property(18, String.class, "remark", false, "REMARK");
        public final static Property Weight = new Property(19, String.class, "weight", false, "WEIGHT");
        public final static Property EvaluateWeight = new Property(20, float.class, "evaluateWeight", false, "EVALUATE_WEIGHT");
        public final static Property Account = new Property(21, String.class, "account", false, "ACCOUNT");
        public final static Property Password = new Property(22, String.class, "password", false, "PASSWORD");
        public final static Property Str = new Property(23, String.class, "str", false, "STR");
    }


    public UserBeanDao(DaoConfig config) {
        super(config);
    }
    
    public UserBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"NAME\" TEXT NOT NULL ," + // 2: name
                "\"CASE_HISTORY_NO\" TEXT NOT NULL UNIQUE ," + // 3: caseHistoryNo
                "\"AGE\" INTEGER NOT NULL ," + // 4: age
                "\"DATE\" TEXT," + // 5: date
                "\"SEX\" INTEGER NOT NULL ," + // 6: sex
                "\"DIAGNOSIS\" TEXT NOT NULL ," + // 7: diagnosis
                "\"PHOTO\" TEXT," + // 8: photo
                "\"DOCTOR\" TEXT," + // 9: doctor
                "\"HOSPITAL_NAME\" TEXT," + // 10: hospitalName
                "\"HOSPITAL_ADDRESS\" TEXT," + // 11: hospitalAddress
                "\"ADDRESS\" TEXT," + // 12: address
                "\"TELEPHONE\" TEXT," + // 13: telephone
                "\"LINKMAN\" TEXT," + // 14: linkman
                "\"PING_YIN\" TEXT," + // 15: pingYin
                "\"CREATE_DATE\" TEXT," + // 16: createDate
                "\"UPDATE_DATE\" TEXT," + // 17: updateDate
                "\"REMARK\" TEXT," + // 18: remark
                "\"WEIGHT\" TEXT NOT NULL ," + // 19: weight
                "\"EVALUATE_WEIGHT\" REAL NOT NULL ," + // 20: evaluateWeight
                "\"ACCOUNT\" TEXT," + // 21: account
                "\"PASSWORD\" TEXT," + // 22: password
                "\"STR\" TEXT);"); // 23: str
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindString(3, entity.getName());
        stmt.bindString(4, entity.getCaseHistoryNo());
        stmt.bindLong(5, entity.getAge());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(6, date);
        }
        stmt.bindLong(7, entity.getSex());
        stmt.bindString(8, entity.getDiagnosis());
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(9, photo);
        }
 
        String doctor = entity.getDoctor();
        if (doctor != null) {
            stmt.bindString(10, doctor);
        }
 
        String hospitalName = entity.getHospitalName();
        if (hospitalName != null) {
            stmt.bindString(11, hospitalName);
        }
 
        String hospitalAddress = entity.getHospitalAddress();
        if (hospitalAddress != null) {
            stmt.bindString(12, hospitalAddress);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(13, address);
        }
 
        String telephone = entity.getTelephone();
        if (telephone != null) {
            stmt.bindString(14, telephone);
        }
 
        String linkman = entity.getLinkman();
        if (linkman != null) {
            stmt.bindString(15, linkman);
        }
 
        String pingYin = entity.getPingYin();
        if (pingYin != null) {
            stmt.bindString(16, pingYin);
        }
 
        String createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindString(17, createDate);
        }
 
        String updateDate = entity.getUpdateDate();
        if (updateDate != null) {
            stmt.bindString(18, updateDate);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(19, remark);
        }
        stmt.bindString(20, entity.getWeight());
        stmt.bindDouble(21, entity.getEvaluateWeight());
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(22, account);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(23, password);
        }
 
        String str = entity.getStr();
        if (str != null) {
            stmt.bindString(24, str);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindString(3, entity.getName());
        stmt.bindString(4, entity.getCaseHistoryNo());
        stmt.bindLong(5, entity.getAge());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(6, date);
        }
        stmt.bindLong(7, entity.getSex());
        stmt.bindString(8, entity.getDiagnosis());
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(9, photo);
        }
 
        String doctor = entity.getDoctor();
        if (doctor != null) {
            stmt.bindString(10, doctor);
        }
 
        String hospitalName = entity.getHospitalName();
        if (hospitalName != null) {
            stmt.bindString(11, hospitalName);
        }
 
        String hospitalAddress = entity.getHospitalAddress();
        if (hospitalAddress != null) {
            stmt.bindString(12, hospitalAddress);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(13, address);
        }
 
        String telephone = entity.getTelephone();
        if (telephone != null) {
            stmt.bindString(14, telephone);
        }
 
        String linkman = entity.getLinkman();
        if (linkman != null) {
            stmt.bindString(15, linkman);
        }
 
        String pingYin = entity.getPingYin();
        if (pingYin != null) {
            stmt.bindString(16, pingYin);
        }
 
        String createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindString(17, createDate);
        }
 
        String updateDate = entity.getUpdateDate();
        if (updateDate != null) {
            stmt.bindString(18, updateDate);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(19, remark);
        }
        stmt.bindString(20, entity.getWeight());
        stmt.bindDouble(21, entity.getEvaluateWeight());
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(22, account);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(23, password);
        }
 
        String str = entity.getStr();
        if (str != null) {
            stmt.bindString(24, str);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserBean readEntity(Cursor cursor, int offset) {
        UserBean entity = new UserBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.getString(offset + 2), // name
            cursor.getString(offset + 3), // caseHistoryNo
            cursor.getInt(offset + 4), // age
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // date
            cursor.getInt(offset + 6), // sex
            cursor.getString(offset + 7), // diagnosis
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // photo
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // doctor
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // hospitalName
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // hospitalAddress
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // address
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // telephone
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // linkman
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // pingYin
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // createDate
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // updateDate
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // remark
            cursor.getString(offset + 19), // weight
            cursor.getFloat(offset + 20), // evaluateWeight
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // account
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // password
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23) // str
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        entity.setCaseHistoryNo(cursor.getString(offset + 3));
        entity.setAge(cursor.getInt(offset + 4));
        entity.setDate(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSex(cursor.getInt(offset + 6));
        entity.setDiagnosis(cursor.getString(offset + 7));
        entity.setPhoto(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDoctor(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setHospitalName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setHospitalAddress(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setAddress(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setTelephone(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setLinkman(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setPingYin(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setCreateDate(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setUpdateDate(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setRemark(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setWeight(cursor.getString(offset + 19));
        entity.setEvaluateWeight(cursor.getFloat(offset + 20));
        entity.setAccount(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setPassword(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setStr(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
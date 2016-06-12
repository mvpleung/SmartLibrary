package org.smart.library.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;

import org.smart.library.control.AppConfig;
import org.smart.library.control.AppConfig.AppSharedName;
import org.smart.library.control.Version;
import org.smart.library.control.L;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Liangzc
 *         created on 2013-1-10
 */
public class RecordPreferences {

    private static RecordPreferences instance;
    private Context context;

    private SharedPreferences shareClientPreferences, shareClientHistoryPreference; //
    private final static String AUTO_LOGIN = "AUTO_LOGIN";
    private final static String VERSION_NAME = "VERSION_NAME";
    private final static String LAST_LOGIN = "0";
    private final static String HISTORY_USER_PREFERENCE = "sharedprefrence_history_user";

    private RecordPreferences() {
    }

    private RecordPreferences(Context context) {
        this.context = context;
        shareClientPreferences = context.getSharedPreferences(AppConfig.SHARED_USER_INFO, Context.MODE_PRIVATE);
    }

    public static RecordPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new RecordPreferences(context);
        }
        return instance;
    }

    /**
     * 创建 Or 获取 SharedPreferences
     *
     * @param name
     */
    public SharedPreferences getSharedPreferences(String name) {
        return getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 创建 Or 获取 SharedPreferences
     *
     * @param name
     * @param mode
     */
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (TextUtils.isEmpty(name))
            return null;
        return context.getSharedPreferences(name, mode);
    }

    /**
     * 添加键值对
     *
     * @param key
     * @param value
     */
    public void putSharedValue(String key, Object value) {
        this.putSharedValue(shareClientPreferences, new String[]{key}, new Object[]{value});
    }

    /**
     * 添加键值对
     */
    public void putSharedValue(String[] keys, Object[] values) {
        this.putSharedValue(shareClientPreferences, keys, values);
    }

    /**
     * 添加键值对
     *
     * @param key
     * @param value
     */
    public void putSharedValue(String sharedName, String key, Object value) {
        this.putSharedValue(getSharedPreferences(sharedName, Context.MODE_PRIVATE), new String[]{key}, new Object[]{value});
    }

    /**
     * 添加键值对
     *
     * @param key
     * @param value
     */
    public void putSharedValue(SharedPreferences sharedPre, String key, Object value) {
        this.putSharedValue(sharedPre, new String[]{key}, new Object[]{value});
    }

    /**
     * 添加键值对
     *
     * @param key
     * @param value
     */
    public void putSharedValue(SharedPreferences sharedPre, String[] key, Object[] value) {
        if (sharedPre == null || key == null || value == null) {
            return;
        }
        Editor editor = sharedPre.edit();
        for (int i = 0; i < key.length; i++) {
            if (key[i] == null || value[i] == null) {
                return;
            }
            if (value[i].getClass() == String.class) {
                editor.putString(key[i], value[i].toString());
            } else if (value[i].getClass() == Integer.class) {
                editor.putInt(key[i], (Integer) value[i]);
            } else if (value[i].getClass() == Long.class) {
                editor.putLong(key[i], (Long) value[i]);
            } else if (value[i].getClass() == Boolean.class) {
                editor.putBoolean(key[i], (Boolean) value[i]);
            } else if (value[i].getClass() == Float.class) {
                editor.putFloat(key[i], (Float) value[i]);
            }
        }
        editor.commit();
    }

    /**
     * 通过key获取值
     *
     * @param <T>
     * @param <T>
     * @param key
     * @param cls value的类型[String,Integer,Long,Boolean,Float]
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getSharedValue(String sharedName, String key, Class<T> cls, Comparable<?>... defaultValues) {
        Map<String, Comparable<?>> map = getSharedValue(getSharedPreferences(sharedName, Context.MODE_PRIVATE), new String[]{key}, new Class[]{cls}, defaultValues);
        return (T) map.get(key);
    }

    /**
     * 通过key获取值
     *
     * @param <T>
     * @param key
     * @param cls value的类型[String,Integer,Long,Boolean,Float]
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getSharedValue(String key, Class<T> cls, Comparable<?>... defaultValues) {
        Map<String, Comparable<?>> map = getSharedValue(shareClientPreferences, new String[]{key}, new Class[]{cls}, defaultValues);
        return (T) map.get(key);
    }

    /**
     * 存储
     *
     * @param key
     * @param value
     * @return
     */
    public boolean putSharedValue(String key, boolean value) {
        Editor mEdit = shareClientPreferences.edit();
        mEdit.putBoolean(key, value);
        return mEdit.commit();
    }

    /**
     * 存储
     *
     * @param key
     * @param value
     * @return
     */
    public boolean putSharedValue(String key, String value) {
        Editor mEdit = shareClientPreferences.edit();
        mEdit.putString(key, value);
        return mEdit.commit();
    }

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    public String getSharedValue(String key, String defaultValue) {
        return shareClientPreferences.getString(key, defaultValue);
    }

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    public boolean getSharedValue(String key, boolean defaultValue) {
        return shareClientPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 通过key获取值
     *
     * @param clss value的类型[String,Integer,Long,Boolean,Float]
     * @return
     */
    public Map<String, Comparable<?>> getSharedValue(String[] keys, Class<?>[] clss, Comparable<?>... defaultValues) {
        return getSharedValue(shareClientPreferences, keys, clss, defaultValues);
    }

    /**
     * 通过key获取值
     *
     * @param clss value的类型[String,Integer,Long,Boolean,Float]
     * @return
     */
    private Map<String, Comparable<?>> getSharedValue(SharedPreferences sharedPre, String[] keys, Class<?>[] clss, Comparable<?>... defaultValues) {
        if (keys == null || sharedPre == null || clss == null) {
            return null;
        }
        Map<String, Comparable<?>> map = new HashMap<String, Comparable<?>>();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null || clss[i] == null) {
                return null;
            }
            if (clss[i] == String.class) {
                map.put(keys[i], sharedPre.getString(keys[i], defaultValues != null && defaultValues.length > 0 ? (String) defaultValues[i] : null));
            } else if (clss[i] == Integer.class) {
                map.put(keys[i], sharedPre.getInt(keys[i], defaultValues != null && defaultValues.length > 0 ? (Integer) defaultValues[i] : 0));
            } else if (clss[i] == Long.class) {
                map.put(keys[i], sharedPre.getLong(keys[i], defaultValues != null && defaultValues.length > 0 ? (Long) defaultValues[i] : 0));
            } else if (clss[i] == Boolean.class) {
                map.put(keys[i], sharedPre.getBoolean(keys[i], defaultValues != null && defaultValues.length > 0 ? (Boolean) defaultValues[i] : false));
            } else if (clss[i] == Float.class) {
                map.put(keys[i], sharedPre.getFloat(keys[i], defaultValues != null && defaultValues.length > 0 ? (Float) defaultValues[i] : 0));
            }
        }
        return map;
    }

    /**
     * 清楚指定shared
     *
     * @param context
     * @param sharedKey
     */
    public void clearSharedValue(Context context, String sharedName, String... sharedKey) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        Set<String> keys = sharedPreferences.getAll().keySet();
        if (keys != null && keys.size() > 0 && sharedKey != null && sharedKey.length != 0) {
            int length = sharedKey.length;
            Editor editor = sharedPreferences.edit();
            for (int i = 0; i < length; i++) {
                for (String string : keys) {
                    if (string.equals(sharedKey[i]))
                        editor.remove(string);
                }
            }
            editor.commit();

        }
    }

    /**
     * 批量清楚shared
     *
     * @param context
     * @param sharedKey
     * @param match     匹配关键词：｛"contains","startsWith","endsWith"｝
     */
    public void clearSharedValue(Context context, String sharedName, String match, String[] sharedKey) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        Set<String> keys = sharedPreferences.getAll().keySet();
        if (keys != null && keys.size() > 0 && sharedKey != null && sharedKey.length != 0) {
            int length = sharedKey.length;
            Editor editor = sharedPreferences.edit();
            for (int i = 0; i < length; i++) {
                for (String string : keys) {
                    boolean isMatch = false;
                    if ("contains".equals(match)) {
                        isMatch = string.contains(sharedKey[i]);
                    } else if ("startsWith".equals(match)) {
                        isMatch = string.startsWith(sharedKey[i]);
                    } else if ("endsWith".equals(match)) {
                        isMatch = string.endsWith(sharedKey[i]);
                    }
                    if (isMatch)
                        editor.remove(string);
                }
            }
            editor.commit();
        }
    }

    /**
     * 保存一个历史用户
     *
     * @param userName 用户名
     * @param password 密码 当
     */
    public void recordHistoryUser(String userName, String password) {
        Editor edit = shareClientPreferences.edit();
        edit.putString(userName, password);
        edit.putString(LAST_LOGIN, userName);
        edit.commit();
    }

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    public String getSharedValue(String key) {
        return shareClientPreferences.getString(key, null);
    }

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    public int getSharedValue(String key, int defaultValue) {
        return shareClientPreferences.getInt(key, defaultValue);
    }

    /**
     * 获取最后一次登录的用户
     */
    public String getLastLoginUser() {
        return shareClientPreferences.getString(LAST_LOGIN, null);
    }

    /**
     * 获取历史用户,并从密码是否为空来判断是否有记录密码
     *
     * @return
     */
    public Map<String, String> quallyAllHistoryUser() {
        Map<String, String> historyList = new HashMap<String, String>();
        Map<String, ?> allData = shareClientPreferences.getAll();
        Set<String> keySet = allData.keySet();
        for (String tempUserName : keySet) {
            if (!"".equals(tempUserName) && !"0".equals(tempUserName)) {
                String password = shareClientPreferences.getString(tempUserName, null);
                historyList.put(tempUserName, password);
            }
        }
        return historyList;
    }

    /**
     * 清楚所有历史用户
     */
    public void clearAllHistoryUser() {
        Editor edit = shareClientPreferences.edit();
        edit.clear();
        edit.commit();
    }

    /**
     * 判断用户是否第一次登陆
     *
     * @param user
     * @return
     */
    public boolean checkUserFirstLogin(String user) {
        boolean result = false;
        if (user != null && "".equals(user)) {
            Map<String, ?> allData = shareClientPreferences.getAll();
            Set<String> keySet = allData.keySet();
            for (String string : keySet) {
                if (user.equals(string)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 自动登录（以用户名为key，存储是否登录）
     *
     * @param userName 用户名
     */
    public void autoLoginUser(String userName, boolean isGraris) {
        Editor edit = shareClientPreferences.edit();
        edit.putBoolean(userName, isGraris);
        edit.putString(AUTO_LOGIN, userName);
        edit.commit();
    }

    /**
     * 获取勾选自动登录的用户名
     *
     * @return
     */
    public String getAutoUser() {
        return shareClientPreferences.getString(AUTO_LOGIN, null);
    }

    /**
     * 添加注销记录
     */
    public void addLogout(boolean flag) {
        Editor edit = shareClientPreferences.edit();
        edit.putBoolean("Logout", flag);
        edit.commit();
    }

    /**
     * 获取注销记录
     */
    public boolean getLogout() {
        return shareClientPreferences.getBoolean("Logout", false);
    }

    /**
     * 添加版本到本地
     */
    public boolean addVersionName() {
        Editor edit = shareClientPreferences.edit();
        edit.putString(VERSION_NAME, Version.getAppVersionName(context));
        return edit.commit();
    }

    /**
     * 获取应用是否首次启动
     */
    public boolean isNewVersion() {
        String versionName = this.getVersionName();
        return !versionName.equalsIgnoreCase(Version.getAppVersionName(context));
    }

    /**
     * 获取记录中保存的版本号
     */
    public String getVersionName() {
        return shareClientPreferences.getString(VERSION_NAME, "");
    }

    /**
     * 保存当前登陆用户名
     */
    public boolean saveLoginUser(String mLoginUser) {
        try {
            String encode = new String(Base64.encode(AESUtils.des3EncodeECB(mLoginUser.getBytes("UTF-8")), Base64.DEFAULT), "UTF-8");
            Editor edit = shareClientPreferences.edit();
            edit.putString(AppSharedName.USER_LOGIN, encode);
            return edit.commit();
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    /**
     * 获取保存的用户名
     *
     * @return
     */
    public String getSavedLoginUser() {
        String userEncode = getSharedValue(AppSharedName.USER_LOGIN, "");
        if (TextUtils.isEmpty(userEncode))
            return userEncode;
        try {
            return new String(AESUtils.ees3DecodeECB(Base64.decode(userEncode.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
            return "";
        }
    }

    /**
     * 清楚用户名
     *
     * @return
     */
    public boolean clearLoginUser() {
        Editor edit = shareClientPreferences.edit();
        edit.putString(AppSharedName.USER_LOGIN, "");
        return edit.commit();
    }

    /**
     * 保存认证码
     */
    public boolean savePassport(String passport) {
        try {
            String encode = TextUtils.isEmpty(passport) ? passport : new String(Base64.encode(AESUtils.des3EncodeECB(passport.getBytes("UTF-8")), Base64.DEFAULT), "UTF-8");
            Editor edit = shareClientPreferences.edit();
            edit.putString(AppSharedName.PASSPORT, encode);
            return edit.commit();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取保存的认证码
     *
     * @return
     */
    public String getSavedPassport() {
        String encode = getSharedValue(AppSharedName.PASSPORT, "");
        if (TextUtils.isEmpty(encode))
            return encode;
        try {
            return new String(AESUtils.ees3DecodeECB(Base64.decode(encode.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 清楚passport
     *
     * @return
     */
    public boolean clearPassport() {
        Editor edit = shareClientPreferences.edit();
        edit.putString(AppSharedName.PASSPORT, "");
        return edit.commit();
    }

    /**
     * 保存加密数据
     */
    public boolean saveEncodeData(String shareName, String value) {
        try {
            Editor edit = shareClientPreferences.edit();
            edit.putString(shareName, !TextUtils.isEmpty(value) ? AESUtils.encodeData(value) : value);
            return edit.commit();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取保存的加密数据
     *
     * @return
     */
    public String getDecodeData(String shareName, String defaultValue) {
        String encode = getSharedValue(shareName, defaultValue);
        if (TextUtils.isEmpty(encode))
            return encode;
        try {
            return AESUtils.decodeData(encode);
        } catch (Exception e) {
            return !TextUtils.isEmpty(defaultValue) ? defaultValue : encode;
        }
    }

    /**
     * 保存历史用户
     *
     * @param loginUser
     * @return
     */
    public boolean saveHistoryUser(String loginUser) {
        try {
            if (shareClientHistoryPreference == null)
                shareClientHistoryPreference = getSharedPreferences(HISTORY_USER_PREFERENCE);
            Editor edit = shareClientHistoryPreference.edit();
            edit.putString(AESUtils.encodeData(loginUser), String.valueOf(System.currentTimeMillis()));
            return edit.commit();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取历史用户列表
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getHistoryUserList() {
        try {
            if (shareClientHistoryPreference == null)
                shareClientHistoryPreference = getSharedPreferences(HISTORY_USER_PREFERENCE);
            Map<String, ?> allData = shareClientHistoryPreference.getAll();
            if (JudgmentLegal.isMapFull(allData)) {
                List<String> values = new ArrayList<String>((Collection<String>) allData.values());
                Collections.sort(values);
                Collections.reverse(values);
                Set<String> mSet = allData.keySet();
                LinkedHashSet<String> mSetList = null;
                try {
                    for (String value : values) {
                        if (!TextUtils.isEmpty(value)) {
                            Iterator<String> mIterator = mSet.iterator();
                            while (mIterator.hasNext()) {
                                String mString = mIterator.next();
                                String mValue = (String) allData.get(mString);
                                if (!TextUtils.isEmpty(mValue) && TextUtils.equals(mValue, value)) {
                                    String mDecode = AESUtils.decodeData(mString);
                                    if (!TextUtils.isEmpty(mDecode)) {
                                        if (mSetList == null)
                                            mSetList = new LinkedHashSet<String>();
                                        mSetList.add(mDecode);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    L.e(e.getMessage(), e);
                }
                return JudgmentLegal.isSetFull(mSetList) ? new ArrayList<String>(mSetList) : null;
            }
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        return new ArrayList<String>();
    }
}

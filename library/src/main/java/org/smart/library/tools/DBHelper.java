package org.smart.library.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.google.gson.reflect.TypeToken;

import org.smart.library.control.AppConfig;
import org.smart.library.control.AppConfig.CacheName;
import org.smart.library.control.AppConfig.Config;
import org.smart.library.control.Version;
import org.smart.library.model.Configuration;
import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

/**
 * DB 操作类
 *
 * @author LiangZiChao
 *         created on 2014-8-11下午2:40:18
 *         In the com.xiaobai.xbtrip.utils
 */
@SuppressLint("UseSparseArrays")
public class DBHelper {

    /*
     * 数据库名称
     */
    private SparseArray<String> sparseArray;

    public String mPackageName;
    public String mPackagePath, mDbPath;
    private String mDbFullPath;
    private File dbFile = null;

    private Context context;

    private static DBHelper dbHelper;

    private DbManager globalDbMg;

    private DBHelper() {
    }

    public static DbManager getInstance(Context context, int dbResource) {
        return getInstance(context, dbResource, null);
    }

    public static DbManager getInstance(Context context, int dbResource, String dbPath) {
        dbHelper = new DBHelper();
        dbHelper.initConfig(context, dbPath, dbResource);
        return dbHelper.globalDbMg;
    }

    public static DbManager getInstance(Context context) {
        dbHelper = new DBHelper();
        dbHelper.context = context;
        try {
            if (dbHelper.globalDbMg == null)
                dbHelper.globalDbMg = dbHelper.createDbManager(AppConfig.APP_NAME);
        } catch (DbException e) {
            LogUtil.e(e.getMessage(), e);
        }
        return dbHelper.globalDbMg;
    }

    /**
     * 初始化配置
     *
     * @param context
     */
    public void initConfig(Context context, String dbPath, int dbResource) {
        try {
            if (sparseArray == null)
                sparseArray = Toolkit.getResourcesByReflect("raw", null);
            this.context = context;
            if (TextUtils.isEmpty(mPackageName)) {
                mPackageName = context.getPackageName();
                mPackagePath = "/data" + Environment.getDataDirectory().getAbsolutePath() + File.separator + mPackageName + "/databases/";
            }
            String dbName = sparseArray.get(dbResource) + ".db";
            mDbPath = TextUtils.isEmpty(dbPath) ? mPackagePath : dbPath;
            mDbFullPath = (mDbPath.endsWith("/") ? mDbPath : (mDbPath + File.separator)) + dbName;
            LogUtil.i("packagename:" + mPackageName);
            LogUtil.i("mDbPath:" + mDbPath);

            // 拷贝数据库
            if (!isExists()) {
                write2Sdcard(dbResource, dbName);
                globalDbMg = createDbManager(dbName);
                saveCurrentVersionCode();
            } else {
                globalDbMg = createDbManager(dbName);
                if (needUpdate())
                    updateDataBase(dbResource, dbName);
                else
                    LogUtil.i("database is New");
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
        }
    }

    /**
     * 创建DbManager
     *
     * @param dbName
     */
    private DbManager createDbManager(String dbName) throws DbException {
        DbManager db = x.getDb(new DbManager.DaoConfig().setDbDir(!TextUtils.isEmpty(mDbPath) ? new File(mDbPath) : null).setDbName(dbName).setDbVersion(Version.getAppVersionCode(context))
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        LogUtil.i("DataBase Upgrade VersionCode:" + oldVersion + "--->" + newVersion);
                    }
                }).setAllowTransaction(true));
        return db;
    }

    /**
     * db is exists
     *
     * @return
     */
    public boolean isExists() {
        dbFile = new File(mDbPath);
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
        dbFile = new File(mDbFullPath);
        return dbFile.exists();
    }

    /**
     * 数据库是否需要拷贝（随包DB）
     *
     * @return true:需要拷贝 false：不需要
     */
    public boolean needUpdate() throws DbException {
        Configuration configuration = globalDbMg.selector(Configuration.class).where("option", "=", Config.VERSION_CODE).findFirst();
        return (configuration == null || TextUtils.isEmpty(configuration.optionValue)) ? true : Integer.parseInt(configuration.optionValue) < Version.getAppVersionCode(context);
    }

    /**
     * 是否需要合并本地数据
     *
     * @return
     */
    public boolean isMegraData(DbManager dbUtils) throws DbException {
        Configuration configuration = dbUtils.selector(Configuration.class).where("option", "=", Config.IS_MERGE_DATA).findFirst();
        return (configuration == null || TextUtils.isEmpty(configuration.optionValue)) ? true : "Y".equals(configuration.optionValue);
    }

    /**
     * 升级DB
     */
    private void updateDataBase(int dbResource, String dbName) {
        LogUtil.i("updating database...");
        String tempName = "temp_" + dbName;
        boolean isMegraData = false, isDelete = false;
        try {
            boolean result = write2Sdcard(dbResource, tempName); // 拷贝数据库到本地临时文件
            if (!result)
                return;
            DbManager tempDbManager = createDbManager(tempName);
            isMegraData = isMegraData(tempDbManager);
            if (isMegraData) {
                DbManager db = createDbManager(dbName);
                ArrayList<String> arrayList = getMergaClass();
                if (arrayList != null && arrayList.size() > 0)
                    for (String cls : arrayList) {
                        if (cls != null)
                            tempDbManager.save(db.findAll(Class.forName(cls)));
                    }
            }
            isDelete = dbFile.delete();
            if (isDelete) {
                File file = new File(mDbPath + tempDbManager.getDaoConfig().getDbName());
                file.renameTo(new File(mDbFullPath));
                FileTools.deleteFile(mDbPath + tempName + "-journal");
            }
            globalDbMg.close();
            globalDbMg = createDbManager(dbName);
            saveCurrentVersionCode();
        } catch (Exception e) {
            // TODO: handle exception
            if (isMegraData && !isDelete) {
                FileTools.deleteFile(mDbPath + tempName);
                FileTools.deleteFile(mDbPath + tempName + "-journal");
            }
            LogUtil.e(e.getMessage(), e);
        }
    }

    /**
     * 获取需要合并数据的类
     */
    @SuppressWarnings({"unchecked"})
    private ArrayList<String> getMergaClass() {
        boolean isExists = FileTools.FileExistsToFilesDir(CacheName.MERGE_CLASS_CACHE);
        if (isExists) {
            ArrayList<String> lists = (ArrayList<String>) FileTools.ReadFromJsonCacheName(CacheName.MERGE_CLASS_CACHE, new TypeToken<ArrayList<String>>() {
            }.getType());
            return lists;
        }
        return null;
    }

    /**
     * 写入数据库当前CODE（升级完成后）
     */
    private void saveCurrentVersionCode() throws DbException {
        SqlInfo sqlInfo = new SqlInfo("UPDATE config SET optionValue=" + Version.getAppVersionCode(context) + " WHERE option = 'VERSION_CODE'");
        globalDbMg.execNonQuery(sqlInfo);
    }

    /**
     * 写入SDCARD
     */
    private boolean write2Sdcard(int dbResource, String dbName) {
        return FileTools.write2Sdcard(context.getResources().openRawResource(dbResource), mDbPath + dbName);
    }

    /**
     * 销毁
     */
    public static void destory() {
        dbHelper.globalDbMg = null;
        dbHelper = null;
    }
}

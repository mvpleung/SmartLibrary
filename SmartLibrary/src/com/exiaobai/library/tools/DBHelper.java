package com.exiaobai.library.tools;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.exiaobai.library.control.AppConfig;
import com.exiaobai.library.control.AppConfig.CacheName;
import com.exiaobai.library.control.AppConfig.Config;
import com.exiaobai.library.control.Version;
import com.exiaobai.library.model.Configuration;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

/**
 * DB 操作类
 * 
 * @author LiangZiChao
 * @Date 2014-8-11下午2:40:18
 * @Package com.xiaobai.xbtrip.utils
 */
@SuppressLint("UseSparseArrays")
public class DBHelper {

	/*
	 * 数据库名称
	 */
	private SparseArray<String> sparseArray;

	public String PACKAGE_NAME;
	public String PACKAGE_PATH, DB_PATH;
	private String DB_FILE;
	private File dbFile = null;

	private Context context;

	private static DBHelper dbHelper;

	private DbUtils globalDbUtils;

	private DBHelper() {
	}

	public static DbUtils getInstance(Context context, int dbResource) {
		return getInstance(context, dbResource, null);
	}

	public static DbUtils getInstance(Context context, int dbResource, String dbPath) {
		dbHelper = new DBHelper();
		dbHelper.initConfig(context, dbPath, dbResource);
		return dbHelper.globalDbUtils;
	}

	public static DbUtils getInstance(Context context) {
		dbHelper = new DBHelper();
		dbHelper.context = context;
		try {
			if (dbHelper.globalDbUtils == null)
				dbHelper.globalDbUtils = dbHelper.createDbUtils(AppConfig.APP_NAME);
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
		return dbHelper.globalDbUtils;
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
			if (TextUtils.isEmpty(PACKAGE_NAME)) {
				PACKAGE_NAME = context.getPackageName();
				PACKAGE_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + File.separator + PACKAGE_NAME + "/databases/";
			}
			String dbName = sparseArray.get(dbResource) + ".db";
			DB_PATH = TextUtils.isEmpty(dbPath) ? PACKAGE_PATH : dbPath;
			DB_FILE = (DB_PATH.endsWith("/") ? DB_PATH : (DB_PATH + File.separator)) + dbName;
			LogUtils.i("packagename:" + PACKAGE_NAME);
			LogUtils.i("DB_PATH:" + DB_PATH);

			// 拷贝数据库
			if (!isExists()) {
				write2Sdcard(dbResource, dbName);
				globalDbUtils = createDbUtils(dbName);
				saveCurrentVersionCode();
			} else {
				globalDbUtils = createDbUtils(dbName);
				if (needUpdate())
					updateDataBase(dbResource, dbName);
				else
					LogUtils.i("database is New");
			}
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
		}
	}

	/**
	 * 创建DbUtils
	 * 
	 * @param dbName
	 * @throws DbException
	 */
	private DbUtils createDbUtils(String dbName) throws DbException {
		DbUtils dbUtils = DbUtils.create(context, DB_PATH, dbName, Version.getAppVersionCode(context), new DbUpgradeListener() {

			@Override
			public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
				LogUtils.i("DataBase Upgrade VersionCode:" + oldVersion + "--->" + newVersion);
			}
		});
		dbUtils.configAllowTransaction(true);
		dbUtils.configDebug(AppConfig.DEBUG_MODEL);
		return dbUtils;
	}

	/**
	 * db is exists
	 * 
	 * @return
	 */
	public boolean isExists() {
		dbFile = new File(DB_PATH);
		if (!dbFile.exists()) {
			dbFile.mkdirs();
		}
		dbFile = new File(DB_FILE);
		return dbFile.exists();
	}

	/**
	 * 数据库是否需要拷贝（随包DB）
	 * 
	 * @return true:需要拷贝 false：不需要
	 * @throws DbException
	 */
	public boolean needUpdate() throws DbException {
		Configuration configuration = globalDbUtils.findFirst(Selector.from(Configuration.class).where("option", "=", Config.VERSION_CODE));
		return (configuration == null || TextUtils.isEmpty(configuration.optionValue)) ? true : Integer.parseInt(configuration.optionValue) < Version.getAppVersionCode(context);
	}

	/**
	 * 是否需要合并本地数据
	 * 
	 * @return
	 * @throws DbException
	 */
	public boolean isMegraData(DbUtils dbUtils) throws DbException {
		Configuration configuration = dbUtils.findFirst(Selector.from(Configuration.class).where("option", "=", Config.IS_MERGE_DATA));
		return (configuration == null || TextUtils.isEmpty(configuration.optionValue)) ? true : "Y".equals(configuration.optionValue);
	}

	/**
	 * 升级DB
	 * 
	 * @throws DbException
	 */
	private void updateDataBase(int dbResource, String dbName) {
		LogUtils.i("updating database...");
		String tempName = "temp_" + dbName;
		boolean isMegraData = false, isDelete = false;
		try {
			boolean result = write2Sdcard(dbResource, tempName); // 拷贝数据库到本地临时文件
			if (!result)
				return;
			DbUtils tempDbUtils = createDbUtils(tempName);
			isMegraData = isMegraData(tempDbUtils);
			if (isMegraData) {
				DbUtils db = createDbUtils(dbName);
				ArrayList<String> arrayList = getMergaClass();
				if (arrayList != null && arrayList.size() > 0)
					for (String cls : arrayList) {
						if (cls != null)
							tempDbUtils.saveAll(db.findAll(Class.forName(cls)));
					}
			}
			isDelete = dbFile.delete();
			if (isDelete) {
				File file = new File(DB_PATH + tempDbUtils.getDaoConfig().getDbName());
				file.renameTo(new File(DB_FILE));
				FileTools.deleteFile(DB_PATH + tempName + "-journal");
			}
			globalDbUtils.close();
			globalDbUtils = createDbUtils(dbName);
			saveCurrentVersionCode();
		} catch (Exception e) {
			// TODO: handle exception
			if (isMegraData && !isDelete) {
				FileTools.deleteFile(DB_PATH + tempName);
				FileTools.deleteFile(DB_PATH + tempName + "-journal");
			}
			LogUtils.e(e.getMessage(), e);
		}
	}

	/**
	 * 获取需要合并数据的类
	 */
	@SuppressWarnings({ "unchecked" })
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
	 * 
	 * @throws DbException
	 */
	private void saveCurrentVersionCode() throws DbException {
		SqlInfo sqlInfo = new SqlInfo("UPDATE config SET optionValue=? WHERE option = 'VERSION_CODE'", String.valueOf(Version.getAppVersionCode(context)));
		globalDbUtils.execNonQuery(sqlInfo);
	}

	/**
	 * 写入SDCARD
	 */
	private boolean write2Sdcard(int dbResource, String dbName) {
		return FileTools.write2Sdcard(context.getResources().openRawResource(dbResource), DB_PATH + dbName);
	}

	/**
	 * 销毁
	 */
	public static void destory() {
		dbHelper.globalDbUtils = null;
		dbHelper = null;
	}
}

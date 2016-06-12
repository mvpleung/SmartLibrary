package org.smart.library.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.smart.library.control.AppConfig;
import org.smart.library.control.AppManager;
import org.smart.library.control.L;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.Locale;

/**
 * 这是一个文件管理的类，实现了判断文件是否存在，读取文件内容，保存文件内容， 将文件转换成 json， 清除缓存 等相关的静态函数。
 */
@SuppressWarnings("unchecked")
public class FileTools {

    private final static int BUFFER_SIZE = 1024;

    public static String APP_FILES;

    public static String SD_PATH;

    public static String SD_FILES;

    /**
     * 初始化应用程序的数据存储区路径，静态函数被调用。
     *
     * @param context
     */
    public static void init(Context context) {
        APP_FILES = context.getFilesDir().getAbsolutePath();
        if (TextUtils.isEmpty(SD_PATH))
            getExtPath();
        SD_FILES = SD_PATH + File.separator + AppConfig.APP_NAME;
        if (TextUtils.isEmpty(AppConfig.Cache_Dir))
            AppConfig.Cache_Dir = hasSDCard() ? (SD_FILES + File.separator) : context.getCacheDir().getPath();
    }

    /**
     * 判断是否有sdcard
     *
     * @return
     */
    public static boolean hasSDCard() {
        boolean b = false;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            b = true;
        }
        return b;
    }

    /**
     * 得到sdcard路径
     *
     * @return
     */
    public static String getExtPath() {
        if (TextUtils.isEmpty(SD_PATH) && hasSDCard())
            SD_PATH = Environment.getExternalStorageDirectory().getPath();
        return SD_PATH;
    }

    /**
     * 得到/data/data/文件目录
     *
     * @param mActivity
     * @return
     */
    public static String getPackagePath(Context mActivity) {
        return mActivity.getFilesDir().toString();
    }

    /**
     * 清理缓存
     *
     * @param endWith
     */
    public static boolean ClearCache(String endWith) {
        String[] Paths = new String[]{APP_FILES, SD_FILES};
        return ClearFiles(Paths, endWith);
    }

    /**
     * 清理临时文件
     */
    public static boolean ClearTempFiles() {
        String[] Paths = new String[]{APP_FILES, SD_FILES};
        return ClearFiles(Paths, "");
    }

    /**
     * 清理文件
     */
    public static boolean ClearFiles(File[] dirs, String filter) {
        String[] Paths = new String[dirs.length];
        int length = dirs.length;
        for (int i = 0; i < length; i++) {
            Paths[i] = dirs[i].getPath();
        }
        return ClearFiles(Paths, filter);
    }

    /**
     * 清理文件
     */
    public static boolean ClearFiles(String path, String filter) {
        return ClearFiles(StringIsNullOrEmpty(path) ? new String[]{APP_FILES, SD_FILES} : new String[]{path}, filter);
    }

    /**
     * 清理文件
     */
    public static boolean ClearFiles(String[] Paths, String filter) {
        boolean result = false;
        try {
            for (String path : Paths) {
                if (!DirectoryExists(path))
                    continue;

                String files[] = StringIsNullOrEmpty(filter) ? DirectoryGetFiles(path) : DirectoryGetFiles(path, filter);

                if (files == null)
                    continue;

                for (String fnx : files) {
                    String filename = PathCombine(path, fnx);
                    L.i("deleting... " + filename);
                    File file = new File(filename);
                    if (file.exists())
                        result = file.delete();
                    else
                        L.i("%s doesn't exists." + filename);
                }
            }
        } catch (Exception ex) {
            L.e(ex.getMessage(), ex);
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (StringIsNullOrEmpty(path))
            return false;
        return deleteFiles(new String[]{path});
    }

    /**
     * 批量删除文件
     *
     * @param paths
     * @return
     */
    public static boolean deleteFiles(String[] paths) {
        if (paths == null || paths.length == 0)
            return false;
        boolean result = false;
        for (String path : paths) {
            if (!DirectoryExists(path))
                continue;
            File file = new File(path);
            if (file.exists())
                result = file.delete();
            else
                L.i("%s doesn't exists." + path);
        }
        return result;
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/" + Environment.getDataDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/" + Environment.getDataDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 按名字清除本应用数据库 * * @param context * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * 清除本应用所有的数据 * * @param context * @param filepath
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null)
                for (File item : listFiles) {
                    item.delete();
                }
        }
    }

    /**
     * 清除app缓存
     */
    public static void clearAppCache(Context context) {
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webview.db-shm");
        context.deleteDatabase("webview.db-wal");
        context.deleteDatabase("webviewCache.db");
        context.deleteDatabase("webviewCache.db-shm");
        context.deleteDatabase("webviewCache.db-wal");
        // 清除数据缓存
        clearCacheFolder(context.getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(context.getCacheDir(), System.currentTimeMillis());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        clearCacheFolder(context.getExternalCacheDir(), System.currentTimeMillis());
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     * @return
     */
    private static int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                File[] listFiles = dir.listFiles();
                if (listFiles != null)
                    for (File child : listFiles) {
                        if (child.isDirectory()) {
                            deletedFiles += clearCacheFolder(child, curTime);
                        }
                        if (child.lastModified() < curTime) {
                            if (child.delete()) {
                                deletedFiles++;
                            }
                        }
                    }
            } catch (Exception e) {

            }
        }
        return deletedFiles;
    }

    /**
     * 通过路径获取文件名
     *
     * @param mPath
     * @return
     */
    public static String getFileName(String mPath) {
        if (TextUtils.isEmpty(mPath))
            return mPath;
        int separatorIndex = mPath.lastIndexOf(File.separator);
        return (separatorIndex < 0) ? mPath : mPath.substring(separatorIndex + 1);
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirectorySize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getDirectorySize(file); // 递归调用继续统计
                }
            }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String FormatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取列表
     *
     * @param path
     */
    private static String[] DirectoryGetFiles(String path) {
        File folder = new File(path);
        return folder.list();
    }

    /**
     * 获取列表
     *
     * @param path
     */
    private static String[] DirectoryGetFiles(String path, final String filter) {
        File folder = new File(path);
        return folder.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(filter))
                    return true;
                return false;
            }
        });
    }

    /**
     * 获取文件路径
     *
     * @param fn
     */
    public static String FindFilePathInner(String fn) {
        String f = PathCombine(APP_FILES, fn);
        if (FileExists(f))
            return f;

        try {
            f = PathCombine(SD_FILES, fn);
            if (FileExists(f))
                return f;
        } catch (Exception ex) {
            L.e(ex.getMessage(), ex);
        }

        return "";
    }

    /**
     * @param fn
     * @return
     */
    public static String GenFilePathInner(String fn) {
        return PathCombine(APP_FILES, fn);
    }

    private static String PathCombine(String path, String name) {
        return path + File.separator + name;
    }

    /**
     * 通过缓存名称读取JSON
     *
     * @param <T>
     * @param type
     * @param cacheName
     */
    public static <T> T ReadFromJsonCacheName(String cacheName, Type type) {
        return (T) ReadFromJsonFile(GenFilePathInner(cacheName), type);
    }

    /**
     * 通过缓存路径读取JSON
     *
     * @param <T>
     * @param type
     * @param path
     */
    public static <T> T ReadFromJsonFile(String path, Type type) {
        L.i("cachePath:" + path);
        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(path));
        } catch (FileNotFoundException e1) {
            return null;
        }

        T t;

        try {
            t = (T) new Gson().fromJson(reader, type);
        } catch (Exception e) {
            L.e(e.getMessage(), e);
            return null;
        }

        return t;
    }

    protected static byte[] ReadFileBytes(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");

        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");

            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            if (f != null)
                f.close();
        }
    }

    public static byte[] ReadFileBytes(String file) throws IOException {
        // Utils.Log("ReadFile: %s", file);
        return ReadFileBytes(new File(file));
    }

    public static InputStream ReadFileInputStream(String file) throws IOException {
        // Utils.Log("ReadFile: %s", file);
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        return inStream;
    }

    /**
     * 通过缓存路径保存JSON
     *
     * @param obj
     * @param path
     */
    public static void SaveJson(Object obj, String path) {
        SaveByte(new Gson().toJson(obj).getBytes(), path);
    }

    /**
     * 通过缓存名称保存JSON
     */
    public static void SaveJsonByCacheName(Object obj, String cacheName) {
        SaveByte(new Gson().toJson(obj).getBytes(), GenFilePathInner(cacheName));
    }

    /**
     * 通过缓存路径保存JSON
     *
     * @param buffer
     * @param path
     */
    public static void SaveByte(byte[] buffer, String path) {
        L.i("cachePath:" + path);
        if (buffer != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
                fos.write(buffer);
                fos.flush();
                fos.close();
                // Log("Write " + fn);
            } catch (Exception e) {
                L.e(e.getMessage(), e);
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    L.e(e.getMessage(), e);
                }
            }
        }
    }

    public static boolean StringIsNullOrEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;

        return false;
    }

    private static boolean DirectoryCreateDirectory(String path) {
        File folder = new File(path);
        return folder.mkdirs();
    }

    private static boolean DirectoryExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean FileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * files目录缓存文件是否存在
     *
     * @param cacheName 缓存名称
     * @return
     */
    public static boolean FileExistsToFilesDir(String cacheName) {
        File file = new File(GenFilePathInner(cacheName));
        return file.exists();
    }

    public static String GenFilePathOuter(String fn) {
        String f = GenFilePathOuterTry(SD_PATH, fn);
        if (!TextUtils.isEmpty(f))
            return f;
        return PathCombine(APP_FILES, fn);
    }

    private static String GenFilePathOuterTry(String path, String fn) {
        if (!DirectoryExists(path))
            return "";

        path = PathCombine(path, AppConfig.APP_NAME);

        if (DirectoryExists(path))
            return PathCombine(path, fn);

        if (DirectoryCreateDirectory(PathCombine(path, ".nomedia"))) {
            L.i("Create path" + path);
            return PathCombine(path, fn);
        } else {
            return "";
        }
    }

    /**
     * 将一个 byte[] 的数据保存到文件中。
     *
     * @param buffer 保存的字节数组
     * @param fn     保存的文件名绝对路径
     */
    public static void SaveToFile(byte[] buffer, String fn) {
        String fnx = fn + ".temp";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fnx);
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                L.e(e.getMessage(), e);
            }
        }

        if (FileExists(fnx)) {
            File file = new File(fnx);
            file.renameTo(new File(fn));
        }
    }

    /**
     * @param context
     * @param obj       实现了序列化
     * @param cacheName
     */
    public static void SaveObjectToFile(Context context, Object obj, String cacheName) {
        // 保存在本地
        try {
            // 通过openFileOutput方法得到一个输出流，方法参数为创建的文件名（不能有斜杠），操作模式
            FileOutputStream fos = context.openFileOutput(cacheName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);// 写入
            fos.close(); // 关闭输出流
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }

        // 保存在sd卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录
            File sdFile = new File(sdCardDir, cacheName);
            if (!FileExists(cacheName))
                sdFile.mkdir();
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream(sdFile);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(obj);// 写入
                fos.close(); // 关闭输出流
            } catch (Exception e) {
                // TODO Auto-generated catch block
                L.e(e.getMessage(), e);
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                    if (oos != null)
                        oos.close();
                } catch (IOException e) {
                    L.e(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 读取对象
     *
     * @param context
     * @return
     */
    public static Object ReadObjectFromFile(Context context, String cacheName) {
        Object obj = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(cacheName); // 获得输入流
            ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            L.e(e.getMessage(), e);
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                L.e(e.getMessage(), e);
            }
        }
        return obj;
    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            L.e(e.getMessage(), e);
        } finally {
            try {
                if (fi != null)
                    fi.close();
                if (in != null)
                    in.close();
                if (fo != null)
                    fo.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                L.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 打开文件
     *
     * @param context
     * @param filePath
     */
    public static void openFile(Context context, String filePath) {
        File f = new File(filePath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        context.startActivity(intent);
    }

    /**
     * 获取文件类型
     *
     * @param f
     * @return
     */
    private static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase(Locale.getDefault());
        if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }

    /**
     * 写入SDCARD
     *
     * @param is
     * @param savePath
     */
    public static boolean write2Sdcard(InputStream is, String savePath) {
        boolean result = false;
        String fileName = null;
        try {
            fileName = savePath.substring(savePath.lastIndexOf("/") + 1, savePath.length());
            FileOutputStream fos = new FileOutputStream(savePath);
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
                fos.flush();
            }
            fos.close();
            is.close();
            L.i(fileName + " Write To SdCard Success");
            result = true;
        } catch (Exception e) {
            deleteFile(savePath);
            L.i(fileName + " Write To SdCard Fail");
            L.e(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 保存图片到Sdcard
     *
     * @param data
     * @param path
     */
    public static void saveSourcePhoto(Bitmap data, String path) {
        boolean isSD = hasSDCard();
        if (!isSD) {// 是否安装sd卡
            UITools.showToastShortDuration(AppManager.getAppManager().getCurrentActivity(), "请插入SD卡");
            return;
        }

        // 把文件写入sd卡中
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            data.compress(Bitmap.CompressFormat.JPEG, 80, bos);// 把数据写入文件
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        } finally {
            try {
                if (data != null)
                    data.recycle();
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                L.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 保存图片到Sdcard
     *
     * @param data
     * @param path
     */
    public static void saveSmallPhoto(Bitmap data, String path) {
        boolean isSD = hasSDCard();
        if (!isSD) {// 是否安装sd卡
            UITools.showToastShortDuration(AppManager.getAppManager().getCurrentActivity(), "请插入SD卡");
            return;
        }

        // 把文件写入sd卡中
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            // 如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800
            Bitmap newBM = Bitmap.createScaledBitmap(data, data.getWidth() > 1280 ? 1280 : data.getWidth(), data.getHeight() > 960 ? 960 : data.getHeight(), false);

            newBM.compress(Bitmap.CompressFormat.JPEG, 60, bos);// 把数据写入文件
        } catch (FileNotFoundException e) {
            L.e(e.getMessage(), e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                L.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取应用缓存目录
     *
     * @return
     */
    public static String getCacheDir() {
        if (TextUtils.isEmpty(AppConfig.Cache_Dir))
            throw new IllegalArgumentException("The AppConfig.Cache_Dir is empty. Execute FileTools.init ? ");
        return !AppConfig.Cache_Dir.endsWith(File.separator) ? AppConfig.Cache_Dir += File.separator : AppConfig.Cache_Dir;
    }

    /**
     * 获取缓存文件路径（以系统时间为准）
     *
     * @param fileNamePath 需要缓存的文件名称
     * @return
     */
    public static String getCacheFilePath(String fileNamePath) {
        if (TextUtils.isEmpty(fileNamePath))
            return fileNamePath;
        int index = fileNamePath.lastIndexOf(".");
        String mTempName = index != -1 ? fileNamePath.substring(0, index) : fileNamePath;
        String type = index != -1 ? fileNamePath.substring(index) : "";
        int indexDir = fileNamePath.indexOf("/");
        if (indexDir != -1) {
            fileNamePath = getCacheFileDir(fileNamePath.substring(0, indexDir));
        }
        return fileNamePath + "_" + mTempName + "_" + System.currentTimeMillis() + type;
    }

    /**
     * 获取缓存文件路径（以传入的文件路径为准）
     *
     * @param filePath
     * @return
     */
    public static String getCacheFileDir(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return filePath;
        String mCacheDir = getCacheDir() + filePath;
        File mFile = new File(mCacheDir);
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        return mCacheDir;
    }
}

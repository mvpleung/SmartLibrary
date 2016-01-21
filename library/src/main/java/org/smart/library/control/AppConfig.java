package org.smart.library.control;

/**
 * 配置相关
 *
 * @author LiangZiChao
 *         created on 2015年6月11日
 */
public class AppConfig {

    public static boolean DEBUG_MODEL;

    /**
     * BOOT
     */
    public static String Boot_File;

    /**
     * APP_NAME
     */
    public static String APP_NAME = "smartLibrary";

    /**
     * 用户
     */
    public static String SHARED_USER_INFO = "sharedprefrence_userinfo";

    /**
     * 缓存目录
     */
    public static String Cache_Dir;

    /**
     * 请求码
     *
     * @author LiangZiChao
     *         created on 2015年7月9日
     */
    public class AppRequestCode {

        /**
         * System Share
         */
        public static final int SYSTEM_SHARE = 0x9002;

        /**
         * camera
         */
        public static final int REQUEST_CAMERA = 0x9003;

        /**
         * 图片预览
         */
        public static final int PHOTO_PREVIEW = 0x9004;

        /**
         * 选择图片
         */
        public static final int REQUEST_PHOTO_SELECT = 0x9005;
    }

    /**
     * SharedName
     *
     * @author LiangZiChao
     *         created on 2015年7月9日
     *         In the net.gemeite.smartcommunity.constant
     */
    public class AppSharedName {

        /**
         * UserName
         */
        public static final String USER_LOGIN = "user_login";

        /**
         * Passprot
         */
        public static final String PASSPORT = "passport";

        /**
         * 消息声音提醒
         */
        public static final String RECEIVE_AUDIO_REMIND = "receiveAudioRemind";

        /**
         * 消息震动提醒
         */
        public static final String RECEIVE_VIBRATION_REMIND = "receiveVibrationRemind";
    }

    /**
     * 配置文件
     *
     * @author LiangZiChao
     *         created on 2014-8-20下午11:54:28
     *         In the com.xiaobai.xbtrip.constant
     */
    public class Config {

        /**
         * 是否合并本地DB和随包DB
         */
        public static final String IS_MERGE_DATA = "IS_MERGE_DATA";

        /**
         * 程序CODE，用来控制是否升级数据库
         */
        public static final String VERSION_CODE = "VERSION_CODE";

        /**
         * 需要合并数据的类
         */
        public static final String MERGE_CLASS = "MERGE_CLASS";
    }

    /**
     * 缓存名称
     *
     * @author LiangZiChao
     *         created on 2014-8-22下午4:32:41
     *         In the com.xiaobai.xbtrip.constant
     */
    public class CacheName {

        /**
         * 需要合并数据的类
         */
        public static final String MERGE_CLASS_CACHE = "merge_class.xml";
    }
}

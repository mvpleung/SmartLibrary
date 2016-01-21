package org.smart.library.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.util.SparseArray;

import org.xutils.common.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 操作工具类
 *
 * @author LiangZiChao
 *         created on 2014-7-31下午7:05:06
 */
public class Toolkit {

    /**
     * Clone Object
     *
     * @param <T>
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneObject(Object obj) {
        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream out = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream in = null;
        try {
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            in = new ObjectInputStream(byteIn);
            return (T) in.readObject();
        } catch (Exception e) {
            return (T) obj;
        } finally {
            try {
                if (byteOut != null)
                    byteOut.close();
                if (byteIn != null)
                    byteIn.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LogUtil.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取联系人电话
     *
     * @param context
     * @param contactData
     * @return
     */
    public static String getContactPhone(Context context, Uri contactData) {
        String[] contacts = getContactPhones(context, contactData);
        if (contacts != null && contacts.length > 1) {
            return contacts[1];
        }
        return null;
    }

    /**
     * 获取联系人姓名和电话
     *
     * @param context
     * @return [name, phone]
     */
    public static String[] getContactPhones(Context context, Uri contactData) {
        try {
            Cursor cursor = context.getContentResolver().query(contactData, null, null, null, null);
            if (cursor == null || cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int phoneNum = cursor.getInt(phoneColumn);
            String[] results = new String[2];
            if (phoneNum > 0) {
                // 获得联系人的ID号
                int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                String contactId = cursor.getString(idColumn);
                // 取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                results[0] = cursor.getString(nameFieldColumnIndex);
                // 获得联系人电话的cursor
                Cursor phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                if (phone.moveToFirst()) {
                    for (; !phone.isAfterLast(); phone.moveToNext()) {
                        int index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String phoneNumber = phone.getString(index);
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            results[1] = phoneNumber;
                            break;
                        }
                        continue;
                    }
                    if (!phone.isClosed()) {
                        phone.close();
                    }
                }
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return results;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * double型 格式化两位小数的地方(保留小数点后面两位的地方)
     *
     * @param price
     * @return
     */
    public static String formatTwoDecimalPlaces(Double price) {
        double priceValue = getDoubleValue(price);
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);
        return ddf1.format(priceValue);
    }

    /**
     * double型 格式化1位小数的地方(保留小数点后面1位的地方)
     *
     * @param price
     * @return
     */
    public static String formatOneDecimalPlaces(Double price) {
        double priceValue = getDoubleValue(price);
        try {
            DecimalFormat df = new DecimalFormat("0.0");
            // TODO parse double value string here
            return df.format(priceValue);
        } catch (NumberFormatException e) {
            // 字符串不能转换为合法的double类型值时,异常处理
            return null;
        }

    }

    /**
     * float型 格式化两位小数的地方(保留小数点后面两位的地方)
     *
     * @param price
     * @return
     */
    public static String formatTwoDecimalPlaces(float price) {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);
        return ddf1.format(price);
    }

    /**
     * float型 格式化两位小数的地方(保留小数点后面两位的地方)
     *
     * @param price
     * @return
     */
    public static float formatTwoDecimalPlace(float price) {
        try {
            NumberFormat ddf1 = NumberFormat.getNumberInstance();
            ddf1.setMaximumFractionDigits(2);
            return Float.parseFloat(ddf1.format(price));
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * float型 格式化N位小数的地方
     *
     * @param price
     * @return
     */
    public static float formatTwoDecimalPlace(float price, int digits) {
        try {
            NumberFormat ddf1 = NumberFormat.getNumberInstance();
            ddf1.setMaximumFractionDigits(digits);
            return Float.parseFloat(ddf1.format(price));
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 转化元
     *
     * @param oyuan
     * @return
     */
    public static String convertToYuan(String oyuan) {
        float rs = Float.parseFloat(oyuan) / 100;
        DecimalFormat formator = new DecimalFormat("##,###,##0.00");
        return "￥" + formator.format(rs).toString();
    }

    /**
     * 转浮点
     *
     * @param price
     * @return
     */
    public static float priceToFloat(String price) {
        price.replace("￥", "");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(',');
        dfs.setMonetaryDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("###,###.##", dfs);
        Number num = null;
        try {
            num = df.parse(price);
            return num.floatValue();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            LogUtil.e(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 通过Double对象获取double值
     *
     * @param data
     * @return
     */
    public static double getDoubleValue(Double data) {
        if (data == null)
            return 0;
        return data.doubleValue();
    }

    /**
     * 通过Integer获取int值
     *
     * @param data
     * @return
     */
    public static int getIntValue(Integer data) {
        if (data == null)
            return 0;
        return data.intValue();
    }

    /**
     * 通过Long获取int值
     *
     * @param data
     * @return
     */
    public static long getLongValue(Long data) {
        if (data == null)
            return 0;
        return data.longValue();
    }

    /**
     * 转换Null为""
     *
     * @param str
     * @return
     */
    public static String getStringValue(String str) {
        if (!TextUtils.isEmpty(str))
            return str;
        return "";
    }

    /**
     * 去除重复Bean，无序（仅限于相同内存地址）
     *
     * @param <T>
     * @param arlList
     */
    public static <T> void removeDuplicate(ArrayList<T> arlList) {
        HashSet<T> h = new HashSet<T>(arlList);
        arlList.clear();
        arlList.addAll(h);
    }

    /**
     * 去除重复Bean，维持原有顺序（仅限于相同内存地址）
     *
     * @param <T>
     * @param arlList
     */
    public static <T> void removeDuplicateWithOrder(List<T> arlList) {
        Set<T> set = new HashSet<T>();
        List<T> newList = new ArrayList<T>();
        for (Iterator<T> iter = arlList.iterator(); iter.hasNext(); ) {
            T element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        arlList.clear();
        arlList.addAll(newList);
    }

    /**
     * 通过key获取Value
     *
     * @param <T>
     * @param <T>
     * @param key
     * @param isLike 是否是模糊搜索
     * @return
     */
    public static <T> T getMapValue(String key, HashMap<String, T> hashMap, boolean isLike) {
        T t = null;
        if (TextUtils.isEmpty(key) || hashMap == null)
            return t;
        // 是否为模糊搜索
        if (isLike) {
            List<String> keyList = new ArrayList<String>();
            Set<String> treeSet = hashMap.keySet();
            for (String string : treeSet) {
                // 通过排序后,key是有序的.
                if (string.indexOf(key) != -1 || key.indexOf(string) != -1) {
                    keyList.add(string);
                    t = hashMap.get(string);
                    break;
                } else if (string.indexOf(key) == -1 && keyList.size() == 0) {
                    // 当不包含这个key时而且key.size()等于0时,说明还没找到对应的key的开始
                    continue;
                } else {
                    // 当不包含这个key时而且key.size()大于0时,说明对应的key到当前这个key已经结束.不必要在往下找
                    break;
                }
            }
            keyList.clear();
            keyList = null;
        } else {
            t = hashMap.get(key);
        }
        return t;
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    private static long lastClickTime;

    /**
     * 是否是连续点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取资源ID
     *
     * @param resourceType drawable,raw等
     * @param withKey      关键字
     * @param pattern      匹配规则 start,end,match
     * @return
     */
    public static int getResourceReflect(String resourceType, String withKey, String pattern) {
        List<Integer> resourceids = getResourcesReflect(resourceType, withKey, pattern);
        return resourceids != null && resourceids.size() > 0 ? resourceids.get(0) : 0;
    }

    /**
     * 获取资源ID数量
     *
     * @param resourceType drawable,raw等
     * @param withKey      关键字
     * @param pattern      匹配规则 start,end,match
     * @return
     */
    public static int getResourceReflectCount(String resourceType, String withKey, String pattern) {
        List<Integer> resourceids = getResourcesReflect(resourceType, withKey, pattern);
        return resourceids != null ? resourceids.size() : 0;
    }

    /**
     * 通过反射获取资源
     *
     * @param resourceType drawable,raw等
     * @param withKey
     * @param pattern      匹配规则 start,end,match
     * @return
     */
    public static Integer[] getResourcesReflectArray(String resourceType, String withKey, String pattern) {
        List<Integer> integers = getResourcesReflect(resourceType, withKey, pattern);
        return integers == null ? new Integer[]{} : integers.toArray(new Integer[]{});
    }

    /**
     * 通过反射获取资源
     *
     * @param resourceType drawable,raw等
     * @param withKey
     * @param pattern      匹配规则 start,end,match
     * @return
     */
    public static List<Integer> getResourcesReflect(String resourceType, String withKey, String pattern) {
        try {
            Field[] fields = Class.forName("org.smart.library.R$" + resourceType).getFields();
            ArrayList<Integer> integers = null;
            for (Field field : fields) {
                if ("match".equals(pattern) && field.getName().equalsIgnoreCase(withKey) || "start".equals(pattern) && field.getName().startsWith(withKey) || "end".equals(pattern) && field.getName().endsWith(withKey)) {
                    if (integers == null)
                        integers = new ArrayList<Integer>();
                    integers.add(field.getInt(field.getName()));
                }
            }
            return integers;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 通过反射获取资源
     *
     * @param resourceType drawable,raw等
     * @param startsWith
     * @return
     */
    public static SparseArray<String> getResourcesByReflect(String resourceType, String startsWith) {
        try {
            Field[] fields = Class.forName("org.smart.library.R$" + resourceType).getFields();
            SparseArray<String> sparseArray = new SparseArray<String>(fields.length);
            for (Field field : fields) {
                if (!TextUtils.isEmpty(startsWith) && !field.getName().startsWith(startsWith)) {
                    continue;
                }
                sparseArray.put(field.getInt(field.getName()), field.getName());
            }
            return sparseArray;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 通过Identifier获取资源
     *
     * @param context
     * @return
     */
    public static int[] getResourcesByIdentifier(Context context, String resourceType, String startsWith) {
        try {
            Field[] fields = Class.forName("org.smart.library.R$" + resourceType).getFields();
            int i = 0;
            for (Field field : fields) {
                if (field.getName().startsWith(startsWith)) {
                    i++;
                }
            }
            int[] images = new int[i];
            for (int j = 0; i < images.length; i++) {
                images[j] = context.getResources().getIdentifier(startsWith + j, resourceType, context.getPackageName());
            }
            return images;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 把中文数字解析为阿拉伯数字(Integer)
     *
     * @param chineseNumber 中文数字
     * @return 阿拉伯数字(Integer), 如果是无法识别的中文数字则返回-1
     */
    public static int parseChineseNumber(String chineseNumber) {
        chineseNumber = chineseNumber.replace("仟", "千");
        chineseNumber = chineseNumber.replace("佰", "百");
        chineseNumber = chineseNumber.replace("拾", "十");
        chineseNumber = chineseNumber.replace("玖", "九");
        chineseNumber = chineseNumber.replace("捌", "八");
        chineseNumber = chineseNumber.replace("柒", "七");
        chineseNumber = chineseNumber.replace("陆", "六");
        chineseNumber = chineseNumber.replace("伍", "五");
        chineseNumber = chineseNumber.replace("肆", "四");
        chineseNumber = chineseNumber.replace("叁", "三");
        chineseNumber = chineseNumber.replace("贰", "二");
        chineseNumber = chineseNumber.replace("壹", "一");
        return parseChineseNumber(chineseNumber, 1);
    }

    /**
     * 把中文数字解析为阿拉伯数字(Integer)
     *
     * @param preNumber     第二大的进位
     * @param chineseNumber 中文数字
     * @return 阿拉伯数字(Integer), 如果是无法识别的中文数字则返回-1
     */
    private static int parseChineseNumber(String chineseNumber, int preNumber) {
        int ret = 0;
        if (chineseNumber.indexOf("零") == 0) {
            int index = 0;
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1);
        } else if (chineseNumber.indexOf("亿") != -1) {
            int index = chineseNumber.indexOf("亿");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index - 1);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 100000000 + parseChineseNumber(postfix, 10000000);
        } else if (chineseNumber.indexOf("万") != -1) {
            int index = chineseNumber.indexOf("万");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);

            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 10000 + parseChineseNumber(postfix, 1000);
        } else if (chineseNumber.indexOf("千") != -1) {
            int index = chineseNumber.indexOf("千");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 1000 + parseChineseNumber(postfix, 100);
        } else if (chineseNumber.indexOf("百") != -1) {
            int index = chineseNumber.indexOf("百");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 100 + parseChineseNumber(postfix, 10);
        } else if (chineseNumber.indexOf("十") != -1) {
            int index = chineseNumber.indexOf("十");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 10 + parseChineseNumber(postfix, 1);
        } else if (chineseNumber.equals("一")) {
            ret = 1 * preNumber;
        } else if (chineseNumber.equals("二")) {
            ret = 2 * preNumber;
        } else if (chineseNumber.equals("三")) {
            ret = 3 * preNumber;
        } else if (chineseNumber.equals("四")) {
            ret = 4 * preNumber;
        } else if (chineseNumber.equals("五")) {
            ret = 5 * preNumber;
        } else if (chineseNumber.equals("六")) {
            ret = 6 * preNumber;
        } else if (chineseNumber.equals("七")) {
            ret = 7 * preNumber;
        } else if (chineseNumber.equals("八")) {
            ret = 8 * preNumber;
        } else if (chineseNumber.equals("九")) {
            ret = 9 * preNumber;
        } else if (chineseNumber.equals("")) {
            ret = 0;
        } else {
            ret = -1;
        }
        return ret;
    }

    /**
     * 通过Uri获取图片路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String queryImageUrl(Context context, Uri uri) {
        try {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ImageColumns.DATA}, null, null, null);
            cursor.moveToNext();
            String path = cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return path;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 十六进制转二进制
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 二进制转十六进制
     *
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }
}

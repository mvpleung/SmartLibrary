package org.smart.library.tools;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import org.xutils.common.util.LogUtil;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 控件合法性的判断
 *
 * @author LiangZiChao
 *         created on 2013-1-16
 */
@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class JudgmentLegal {
    /**
     * 判断是否纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null || "".equals(str))
            return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 帐号名验证 5-15 不能是纯数字 不能包含特殊符号除"_"
     */
    public static boolean isMemberNo(String str) {
        Pattern parrert = Pattern.compile("\\w{5,15}");
        Matcher matcher = parrert.matcher(str);
        return !matcher.matches();
    }

    /**
     * 判断字符串是否包含中文字符
     *
     * @param StrName 路径
     * @return
     */
    public static boolean isChinseName(String StrName) {
        if (TextUtils.isEmpty(StrName))
            return false;
        String regex = "([\u4e00-\u9fa5]+)";
        Matcher matcher = Pattern.compile(regex).matcher(StrName);
        return matcher.matches();
    }

    /**
     * 转码字符串中的中文（UTF-8）
     *
     * @param mString
     * @return
     */
    public static String encodeChinese(String mString) {
        if (TextUtils.isEmpty(mString))
            return mString;
        try {
            String regex = "([\u4e00-\u9fa5]+)";
            Matcher matcher = Pattern.compile(regex).matcher(mString);
            while (matcher.find()) {
                String mMatcher = matcher.group(0);
                mString = mString.replace(mMatcher, URLEncoder.encode(mMatcher, "utf-8"));
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
        }
        return mString;
    }

    /**
     * 判断是否钱币字符合法
     *
     * @param str
     * @return
     */
    public static boolean isMoneyLegal(String str) {
        Pattern pattern1 = Pattern.compile("\\d{1,3}(,{1}\\d{3})*(\\.\\d+)?"); // 匹配11,222.33
        Pattern pattern2 = Pattern.compile("\\d*(\\.\\d+)?"); // 匹配11222.33
        Pattern pattern3 = Pattern.compile("[0]+\\d+(\\.\\d+)?");
        if (pattern3.matcher(str).matches()) {
            return false;
        }
        return pattern1.matcher(str).matches() || pattern2.matcher(str).matches();
    }

    /**
     * 返回是的字符串的格式是每四个一组中间加个空格
     *
     * @param str
     * @return
     */
    public static String toFourEachRow(String str) {
        if (TextUtils.isEmpty(str))
            return str;
        String strPattern = "00000000000000000000";
        StringBuffer s = new StringBuffer(str);
        if (str.length() < 20) {
            s.append(strPattern, 0, 20 - str.length());
        }
        int temp = 20;
        while (temp > 4) {
            temp -= 4;
            s.insert(temp, " ");
        }
        return s.toString().substring(0, str.length() + (20 / 4 - 1));
    }

    /**
     * 返回是的字符串的格式是每四个一组中间加个空格
     *
     * @param str
     * @return
     */
    public static String formatBankNumber(String str) {
        if (TextUtils.isEmpty(str))
            return str;
        StringBuffer buffer = new StringBuffer();
        buffer.append(str);
        for (int i = 0; i < buffer.length(); i++) {
            if (i % 5 == 0) {
                buffer.insert(i, " ");
            }
        }
        if (buffer.length() < 2) {
            return buffer.substring(0);
        } else {
            return buffer.substring(1);
        }
    }

    /**
     * 返回是的字符串的格式是格式化的账号，每四个一组，中间是星号
     *
     * @param str
     * @return
     */
    public static String toNumberStar(String str) {
        if (TextUtils.isEmpty(str))
            return str;
        String strPattern = " **** **** **** ";
        String s = toFourEachRow(str);
        String res;
        res = s.substring(0, 4) + strPattern + s.substring(s.length() - 4, s.length());
        return res;
    }

    /**
     * 格式化订单号中间使用****
     */
    public static String formartOrderIdStar(String str) {
        if (TextUtils.isEmpty(str))
            return str;
        String strPattern = "**";
        String res;
        if (str.length() > 16) {
            int index = str.length() / 2;
            res = str.substring(0, index - 2) + strPattern + str.substring(index + 2, str.length());
        } else {
            res = str;
        }
        return res;

    }

    /**
     * 返回是的字符串的格式是格式化的手机号码，前后四个一组，中间是星号
     *
     * @param str
     * @return
     */
    public static String formatPhoneNumberStar(String str) {
        if (TextUtils.isEmpty(str) || str.length() < 11)
            return str;
        String res = str.substring(0, 3) + "****" + str.substring(7, 11);
        return res;
    }

    /**
     * 格式化证件号码（前后4位，中间以*代替，不足四位，前后两位）
     *
     * @param mData
     * @return
     */
    public static String formatStringStar(String mData) {
        if (TextUtils.isEmpty(mData))
            return mData;
        StringBuffer res = new StringBuffer();
        int length = mData.length();
        int indexInsert = length / 4;
        if (length >= 5) {
            indexInsert = indexInsert > 4 ? 4 : indexInsert < 2 ? 2 : indexInsert;
        } else {
            indexInsert = indexInsert == 0 ? 1 : indexInsert;
        }
        res.append(mData.substring(0, indexInsert));
        res.append(mData.substring(length - (length >= 5 ? indexInsert - 1 : indexInsert), length));
        int temp = length <= 2 ? (length++) : res.length();
        while (temp < length) {
            temp++;
            res.insert(indexInsert, "*");
        }
        return res.toString();
    }

    /**
     * 返回是是否只有一个小数点
     *
     * @param str
     * @return
     */
    public static boolean isOnlyOneDot(String str) {
        boolean res;
        int temp = str.indexOf(".");
        if (temp != -1 && str.lastIndexOf(".") != temp) {
            res = false;
        } else {
            res = true;
        }
        return res;
    }

    /**
     * 判定QQ号是否有效
     *
     * @param str
     * @return
     */
    public static boolean isQQNumberLegal(String str) {
        boolean res;

        // if (!isNumeric(str)) {
        // res = false;
        // } else {
        res = true;
        if (str.length() < 5) {
            res = false;
        }
        // }
        return res;
    }

    /**
     * 判断信用卡是否有效
     *
     * @param cardNumber
     * @return
     */
    public static boolean isCreditCardLegal(String cardNumber) {
        String digitsOnly = getDigitsOnly(cardNumber);
        int sum = 0;
        int digit = 0;
        int addend = 0;
        boolean timesTwo = false;

        for (int i = digitsOnly.length() - 1; i >= 0; i--) {
            digit = Integer.parseInt(digitsOnly.substring(i, i + 1));
            if (timesTwo) {
                addend = digit * 2;
                if (addend > 9) {
                    addend -= 9;
                }
            } else {
                addend = digit;
            }
            sum += addend;
            timesTwo = !timesTwo;
        }

        int modulus = sum % 10;
        return modulus == 0;
    }

    private static String getDigitsOnly(String s) {
        StringBuffer digitsOnly = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (Character.isDigit(c)) {
                digitsOnly.append(c);
            }
        }
        return digitsOnly.toString();
    }

    /**
     * 判定银行卡号是否有效
     *
     * @param cardId
     * @return
     */
    public static boolean isBankCardNumberLegal(String cardId) {
        if (cardId.length() < 16 || cardId.length() > 19) {
            return false;
        }
        if (!isNumeric(cardId)) {
            return false;
        }
        // if (cardId.length() == 16 || cardId.length() == 19) {
        // LogHelper.i("cardId:" + cardId);
        // char bit = getBankCardCheckCode(cardId.substring(0,
        // cardId.length() - 1));
        // LogHelper.i("bit:" + bit);
        // if (bit == 'N') {
        // return false;
        // }
        // return cardId.charAt(cardId.length() - 1) == bit;
        // }
        return true;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0 || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    // public static boolean isBankCardNumberLegal(String str) {
    // boolean res;
    // if (str.length() > 25 || str.length() < 1 || !isNumeric(str)) {
    // res = false;
    // } else {
    // res = true;
    // }
    // Pattern pattern = Pattern.compile("(\\d{4}\\ ){4}\\d{4}");
    // if (pattern.matcher(str).matches()) {
    // res = true;
    // }
    // return res;
    // }

    /**
     * 判定手机号码是否有效
     *
     * @param str
     * @return
     */
    public static boolean isPhoneNumberLegal(String str) {
        return Pattern.compile("^1\\d{10}$").matcher(str).matches();
    }

    /**
     * 是否是空值
     *
     * @param str
     * @return true是空值，false是非空值
     */
    public static boolean isNull(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 返回字符串是否为全0
     *
     * @param str
     * @return
     */
    public static boolean isAllZero(String str) {
        boolean res;
        int i;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0') {
                break;
            }
        }
        if (i < str.length()) {
            res = false;
        } else {
            res = true;
        }
        return res;
    }

    /**
     * 判断金额是否为空
     *
     * @param str
     * @return
     */
    public static boolean isMoneyNull(String str) {
        return isAllZero(str) || isNull(str);
    }

    /**
     * 格式化金额（返回元）
     *
     * @param formatStr 格式化参数（0.00）
     * @param money
     * @param weight    比重（分：100 (除以100)）
     * @return
     */
    public static String formatMoney(String formatStr, String money, double weight) {
        if (!JudgmentLegal.isNumeric(money))
            return "0.00";
        DecimalFormat format = new DecimalFormat(formatStr);
        return format.format(Double.parseDouble(money) / weight);
    }

    /**
     * 会员账号长度限制6-15位，不能为纯数字
     *
     * @param str
     * @return
     */
    public static boolean isMemberAccount(String str) {
        return str != null && !isNumeric(str) && Pattern.compile("\\w{6,15}").matcher(str).matches();
    }

    /**
     * 3. 密码长度限制6-20位
     *
     * @param str
     * @return true是密码合法，false是密码不合法
     */
    public static boolean isPasswordLimit(String str) {
        return str.length() >= 6 && str.length() <= 20;
    }

    /**
     * 5. 验证码长度限制6位，纯数字
     *
     * @param captcha
     * @return
     */
    public static boolean isCaptcha(String captcha) {
        return captcha != null && captcha.length() == 6 && isNumeric(captcha);
    }

    /**
     * 6. 银行卡长度限制最大20未，所有银行卡输入需格式化（6225 3223 2221 0000 0000）
     *
     * @param str
     * @return null是长度超过20，最后返回格式化输入的银行卡号
     */
    public static String showBankCardnumberInput(String str) {
        String res;

        Pattern pattern = Pattern.compile("(\\d{4}\\ ){4}\\d{4}");
        if (pattern.matcher(str).matches()) {
            return str;
        }
        if (str != null && str.length() > 20) {
            res = null;
        } else {
            res = toFourEachRow(str);
        }
        return res;
    }

    /**
     * 7.1 输出格式化（6225 **** **** ***** 0000）
     *
     * @param str
     * @return null是长度超过20，最后返回格式化输出的卡号
     */
    public static String showCardNumberOutput(String str) {
        String res = null;

        if (!TextUtils.isEmpty(str)) {
            if (str.length() > 20)
                res = null;
            else
                res = toNumberStar(str);
        }
        return res;
    }

    /**
     * 我的订单 订单号输出格式 （62256225**62250000）
     *
     * @param str
     * @return null是长度超过18
     */

    public static String showOrderOrdidNumberOutput(String str) {
        String res;

        if (str != null && str.length() > 18) {
            res = null;
        } else {
            res = toNumberStar(str);
        }
        return res;
    }

    /**
     * 金额输出格式化，以元为单位，纯整数，三位加逗号。
     *
     * @param str
     * @return
     */
    public static String showMoneyOutput(String str) {
        int i;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0') {
                break;
            }
        }
        if (i < str.length()) {
            str = str.substring(i);
        }
        int temp = str.length();
        StringBuffer s = new StringBuffer(str);
        while (temp > 3) {
            temp -= 3;
            s.insert(temp, ",");
        }
        return s.toString();
    }

    /**
     * 身份证号码长度的判断
     *
     * @param idCard
     * @return
     */
    public static boolean iDCardNumberLegthVerify(String idCard) {
        if (null != idCard) {
            if (idCard.length() == 15 || idCard.length() == 18) {
                return true;
            }
        }
        return false;
    }

    /***
     * 电话号码验证（包括手机号码和固话及小灵通）
     * 手机号码 移动：134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
     * 联通：130,131,132,152,155,156,185,186 电信：133,1349,153,180,189
     * 大陆地区固话及小灵通 区号：010,020,021,022,023,024,025,027,028,029 号码：七位或八位
     */

    public static boolean checkCellPhone(String cellPhone) {
        /**
         * 中国移动：China Mobile
         **/
        String MOBILE = "^1(3[0-9]|5[0-35-9]|8[0253-9])\\d{8}$";
        /**
         * 中国联通：China Unicom
         **/
        String CU = "^1(34[0-8]|(3[5-9]|5[017-9]|8[23678])\\d)\\d{7}$";
        /**
         * 中国电信：China Telecom
         **/
        String CT = "^1(3[0-2]|5[256]|8[56])\\d{8}$";
        /**
         * 大陆地区固话及小灵通 区号：010,020,021,022,023,024,025,027,028,029 号码：七位或八位
         */
        String PHS = "^0(10|2[0-5789]|\\d{3})\\d{7,8}$";

        Pattern patternMOBILE = Pattern.compile(MOBILE);
        Matcher matcherMOBILE = patternMOBILE.matcher(cellPhone);

        Pattern patternCU = Pattern.compile(CU);
        Matcher matcherCU = patternCU.matcher(cellPhone);

        Pattern patternCT = Pattern.compile(CT);
        Matcher matcherCT = patternCT.matcher(cellPhone);

        Pattern patternPHS = Pattern.compile(CT);
        Matcher matcherPHS = patternPHS.matcher(cellPhone);

        return matcherMOBILE.matches() || matcherCU.matches() || matcherCT.matches() || matcherPHS.matches();
    }

    /**
     * 验证邮箱
     *
     * @param emails
     * @return
     */
    public static boolean isEmailNO(String emails) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(check);
        Matcher m = p.matcher(emails);
        return m.matches();
    }

    /**
     * URL检查
     *
     * @param pInput 要检查的字符串
     * @return boolean 返回检查结果
     */
    public static boolean isUrl(String pInput) {
        if (isNull(pInput)) {
            return false;
        }
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 提取字符串中的电话号码
     *
     * @param num
     * @return
     */
    public static String getPhoneNum(String num) {
        String number = null;
        Pattern pattern = Pattern.compile("((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9])|(170))\\d{8}|0\\d{2,3}-?\\d{7,8}");
        Matcher matcher = pattern.matcher(num);
        while (matcher.find()) {
            number = matcher.group();
            return number;
        }
        return null;
    }

    /**
     * 判断list是否有内容
     *
     * @param list
     * @return
     */
    public static <T> boolean isListFull(List<T> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * 判断map是否有内容
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean isMapFull(Map<K, V> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 判断set是否有内容
     *
     * @param set
     * @return
     */
    public static <T> boolean isSetFull(Set<T> set) {
        return set != null && !set.isEmpty();
    }

    /**
     * 重置List
     *
     * @param list
     */
    public static <T> void resetList(List<T> list) {
        if (isListFull(list))
            list.clear();
    }

    /**
     * 重置Set
     *
     * @param set
     */
    public static <T> void resetSet(Set<T> set) {
        if (isSetFull(set))
            set.clear();
    }

    /**
     * 数组是否有内容
     *
     * @param array
     * @return
     */
    public static <T> boolean isArrayFull(T[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 过滤电话号码的编码
     *
     * @param tel
     * @return
     */
    public static String filterTel(String tel) {
        if (!TextUtils.isEmpty(tel)) {
            tel = tel.indexOf("-") != -1 ? tel.replace("-", "") : tel;
            tel = tel.indexOf(" ") != -1 ? tel.replace(" ", "") : tel;
            tel = tel.indexOf("+86") != -1 ? tel.replace("+86", "") : tel;
        }
        return tel;

    }
}

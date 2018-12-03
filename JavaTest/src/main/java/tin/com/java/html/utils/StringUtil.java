package tin.com.java.html.utils;


import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @package : com.cliff.libs.util
 * @description : 字符串工具类
 * Created by chenhx on 2018/4/2 17:07.
 */

public class StringUtil {


    public static int toInt(String str, int defaultValue) {
        if (isEmpty(str)) {
            return defaultValue;
        }
        return Integer.valueOf(str);
    }

    public static boolean isEmpty(String str) {
        if(str==null){
            return true;
        }else if(str.length()==0){
            return true;
        }
        return false;
    }


    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    //是否纯数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 验证手机格式  移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）/^0?1[3|4|5|7|8][0-9]\d{8}$/
     * 总结起来就是第一位必定为1，第二位必定为3或5或8或7（电信运营商），其他位置的可以为0-9
     */
    public static boolean isMobile(String mobiles) {
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (isEmpty(mobiles)){
            return false;
        }
        else{
            return mobiles.matches(telRegex);
        }
    }


    public static int length(String str){
        int charCount=0;
        try {
            charCount = str.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return charCount;
    }



}

package allen.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean isMobileNO(String mobile) {
        if (mobile == null) {
            return false;
        }
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 传入内容和表达式，返回匹配的字符串列表
     * @param content
     * @param regex
     * @return
     */
    public static List<String> pick(String content ,String regex){
        Pattern tagSrc = Pattern.compile(regex);
        Matcher matcher = tagSrc
                .matcher(content);
        StringBuffer tagSb = new StringBuffer();
        List<String> retList = new ArrayList<String>();
        while (matcher.find()) {
            retList.add(matcher.group());
        }
        return retList;
    }

    public static void main(String[] args) {
        List<String> pick = pick("\"http://meituan.com/呵呵\"", "[\\u4e00-\\u9fbb]+");
        for (String s : pick) {
            System.out.println(s);
        }
    }

}
package allen.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * RequestUtils
 * 
 * @version 1.0
 * @created 2013-1-24
 */
public class RequestUtils {

    /**
     * 从cookie中获取某个元素的值
     * 
     * @param cookies
     * @param key
     * @return
     */
    public static String getCookieData(Cookie[] cookies, String key) {
        String value = null;
        if (cookies != null && key != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), key)) {
                    value = cookie.getValue();
                }
            }
        }
        return value;
    }

    public static String getCookieAsString(Cookie[] cookies) {
        StringBuilder sb = new StringBuilder();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                sb.append(cookie.getName()).append("=")
                        .append(cookie.getValue()).append(",")
                        .append(cookie.getDomain()).append("|");
            }
        }
        return sb.toString();
    }

    /**
     * 程序的任何一个地方，获取request对象。当前服务必须要是web形式启动的
     * 
     * @return
     */
    public static HttpServletRequest getRequest() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            return request;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 进行urlencode，如果输入为null，则返回空串""
     * 
     * @param param
     * @return
     */
    public static String urlEncode(String param) {
        if (param != null) {
            try {
                return URLEncoder.encode(param, "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
        }
        return "";
    }

}

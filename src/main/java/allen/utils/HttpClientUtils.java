package allen.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient的util类
 *
 * @version 1.0
 */
public class HttpClientUtils {

    private static HttpClient httpclient = ServerHolder.getInstance()
            .getHttpClient();

    public static String get(String url) {
        return get(url, 0);
    }

    /**
     * @param url
     * @return
     */
    public static String getWithLongTimeOut(String url) {
        return get(url, 30 * 1000);
    }


    /**
     * 无论http response code是多少都返回body内容,对于post请求也可以有类似的写法
     * @param url
     * @param timeout
     * @return
     */
    public static String getResponseIgnoredError(String url, int timeout) {
        PrintUtils.soutIfTest("[Get URL] " + url);
        HttpGet httpGet = new HttpGet(url);
        if (timeout > 0) {
            httpGet.getParams().setIntParameter(
                    CoreConnectionPNames.SO_TIMEOUT, timeout);
        }
        try {
            HttpResponse response = httpclient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String get(String url, int timeout) {
        PrintUtils.soutIfTest("[Get URL] " + url);
        HttpGet httpGet = new HttpGet(url);
        if (timeout > 0) {
            httpGet.getParams().setIntParameter(
                    CoreConnectionPNames.SO_TIMEOUT, timeout);
        }
        try {
            String content = httpclient.execute(httpGet,
                    new BasicResponseHandler());
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String url, Map<String, String> postData) {
        PrintUtils.soutIfTest("[Post URL] " + url + "\t" + postData);
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (postData != null) {
            for (String key : postData.keySet()) {
                params.add(new BasicNameValuePair(key, postData.get(key)));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            String content = httpclient.execute(httpPost,
                    new BasicResponseHandler());
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpsGet(String url) {
        PrintUtils.soutIfTest("[HttpsGet URL] " + url);
        HttpClient httpsClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            // 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[]{xtm}, null);
            // 创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            // 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpsClient.getConnectionManager().getSchemeRegistry()
                    .register(new Scheme("https", 443, socketFactory));
            HttpResponse response = httpsClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpsPost(String url, Map<String, String> postData) {
        PrintUtils.soutIfTest("[HttpsPost URL] " + url + "\t" + postData);
        HttpClient httpsClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (postData != null) {
            for (String key : postData.keySet()) {
                params.add(new BasicNameValuePair(key, postData.get(key)));
            }
        }
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            // 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[]{xtm}, null);
            // 创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            // 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpsClient.getConnectionManager().getSchemeRegistry()
                    .register(new Scheme("https", 443, socketFactory));

            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpsClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpsPost(String url, List<NameValuePair> params) {
        PrintUtils.soutIfTest("[HttpsPost URL] " + url + "\t" + params);
        HttpClient httpsClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            // 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[]{xtm}, null);
            // 创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            // 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpsClient.getConnectionManager().getSchemeRegistry()
                    .register(new Scheme("https", 443, socketFactory));

            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            PrintUtils.serrInTest("Http Post Entity: " + EntityUtils.toString(httpPost.getEntity(), "utf-8"));
            HttpResponse response = httpsClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

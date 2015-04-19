package allen.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HttpClient的pool util类,可以根据名称区分
 *
 * @author zhengxu
 * @version 1.0
 */
public class HttpClientPoolUtils {

    private static Map<String, Pool> poolMap = new HashMap<String, Pool>();
    private static final String DEFAULT = "default";

    public static void register(String poolName, Integer socketTimeout) {
        poolMap.put(poolName, new Pool(socketTimeout));
    }

    public static Pool getPool(String poolName) {
        return poolMap.get(poolName);
    }

    public static Pool getDefaultPool() {
        Pool defaultPool = poolMap.get(DEFAULT);
        if (defaultPool == null) {
            synchronized (HttpClientPoolUtils.class) {
                defaultPool = poolMap.get(DEFAULT);
                if (defaultPool == null) {
                    defaultPool = new Pool(null);
                    poolMap.put(DEFAULT, defaultPool);
                }
            }
        }
        return defaultPool;
    }

    public static void main(String[] args) {
        String s = HttpClientPoolUtils.getDefaultPool().get("http://cnbeta.com");
        System.out.println(s);
    }

    private static class InnerServerHolder {

        private static InnerServerHolder ins = new InnerServerHolder();

        /**
         * 线程池 暂且使用cpu个数做线程个数
         */
        private final ExecutorService executorService = Executors
                .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        private final ConcurrentMap<String, String> statusMap = new ConcurrentHashMap<String, String>();

        private DefaultHttpClient client;

        private InnerServerHolder() {
        }

        public static InnerServerHolder getInstance() {
            return ins;
        }

        public ExecutorService getExecutorService() {
            return executorService;
        }

        /**
         * 创建带连接池的httpclient，线程安全<br>
         * 默认每host最大100连接，timeout时间为5秒
         */
        public DefaultHttpClient createClientWithPool(Integer socketTimeoutMillisecond) {
            ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
            connectionManager.setDefaultMaxPerRoute(300);
            connectionManager.setMaxTotal(2048);
            client = new MyHttpClient(connectionManager);
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                    "allen-httpclient");

            client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                    socketTimeoutMillisecond != null ? socketTimeoutMillisecond : 10 * 1000);
            client.getParams().setIntParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 2 * 1000);
            return client;
        }

        public DefaultHttpClient getHttpClient(Integer socketTimeout) {
            if (client == null) {
                synchronized (this) {
                    this.createClientWithPool(socketTimeout);
                }
            }
            return client;
        }

        public void shutdown() {
            executorService.shutdownNow();
        }

        public void markStatus(String key, String value) {
            statusMap.put(key, value);
        }

        public String getStatus() {
            return statusMap.toString();
        }

        private static class MyHttpClient extends DefaultHttpClient {
            MyHttpClient(final ClientConnectionManager conman) {
                super(conman);
            }

            @Override
            public HttpContext createHttpContext() {
                HttpContext context = super.createHttpContext();
                context.setAttribute(ClientContext.COOKIE_STORE,
                        new BasicCookieStore());
                return context;
            }
        }

    }

    public static class Pool {

        Integer socketTimoutMillisecond;

        public Pool(Integer socketTimoutMillisecond) {
            this.socketTimoutMillisecond = socketTimoutMillisecond;
        }

        private HttpClient httpclient = InnerServerHolder.getInstance()
                .getHttpClient(socketTimoutMillisecond);

        public String get(String url) {
            return get(url, 0);
        }

        /**
         * 同步deal list等缓存时，设置长一点的timeout
         *
         * @param url
         * @return
         */
        public String getWithLongTimeOut(String url) {
            return get(url, 30 * 1000);
        }

        private String get(String url, int timeout) {
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

        public String post(String url, Map<String, String> postData) {
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

        public String httpsgGet(String url) {
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

        public String httpsPost(String url, Map<String, String> postData) {
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

        public String httpsPost(String url, List<NameValuePair> params) {
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

}

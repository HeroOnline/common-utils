package allen.utils;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */
public class ServerHolder {

    private static ServerHolder ins = new ServerHolder();

    /** 线程池 暂且使用cpu个数做线程个数 */
    private final ExecutorService executorService = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final ConcurrentMap<String, String> statusMap = new ConcurrentHashMap<String, String>();

    private DefaultHttpClient client;

    private ServerHolder() {
    }

    public static ServerHolder getInstance() {
        return ins;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * 创建带连接池的httpclient，线程安全<br>
     * 默认每host最大100连接，timeout时间为5秒
     * */
    private DefaultHttpClient createClientWithPool() {
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
        connectionManager.setDefaultMaxPerRoute(1024);
        connectionManager.setMaxTotal(2048);
        client = new DefaultHttpClient(connectionManager);
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "my UA");
        client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                10 * 1000);
        client.getParams().setIntParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 2 * 1000);
        return client;
    }

    public DefaultHttpClient getHttpClient() {
        if (client == null) {
            synchronized (this) {
                this.createClientWithPool();
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

}

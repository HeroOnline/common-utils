package allen.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 线程池
 * 
 * @created 2012-5-8
 * 
 * @version 1.0
 */
public class ThreadPool {

    // 定时任务线程池
    private final ExecutorService scheduledPool;
    // 固定数量的线程池
    private final ExecutorService exec;
    // 任务队列允许的最大数量
    private final int MAX_TASK_QUEUE_SIZE = 10000;
    // 任务队列
    private BlockingQueue<Runnable> queue;

    private static ThreadPool instance = new ThreadPool();

    public static ThreadPool getInstance() {
        return instance;
    }

    private ThreadPool() {
        queue = new LinkedBlockingQueue<Runnable>();
        exec = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * 2);
        scheduledPool = Executors.newScheduledThreadPool(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }).start();
    }

    public void schedule(Runnable runnable, long initialDelay, long period,
            TimeUnit timeUnit) {
        ((ScheduledExecutorService) scheduledPool).scheduleAtFixedRate(
                runnable, initialDelay, period, timeUnit);
    }

    /**
     * 往线程池里添加一个task
     * 
     * 返回true表示添加任务成功，false表示添加任务失败
     * 
     * @param task
     * @return
     */
    public boolean offer(Runnable task) {
        if (queue.size() < MAX_TASK_QUEUE_SIZE) {
            return queue.offer(task);
        }
        return false;
    }

    private void start() {
        while (true) {
            try {
                Runnable task = queue.poll(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    exec.execute(task);
                }
            } catch (Exception e) {
            }
        }
    }

    // public static void main(String[] args) {
    // ThreadPool.getInstance().schedule(new Runnable() {
    //
    // @Override
    // public void run() {
    // System.out.println("3 seconds delay --- "
    // + System.currentTimeMillis());
    // }
    //
    // }, 3, 3, TimeUnit.SECONDS);
    //
    // ThreadPool.getInstance().schedule(new Runnable() {
    //
    // @Override
    // public void run() {
    // System.out.println("5 seconds delay --- "
    // + System.currentTimeMillis());
    // }
    //
    // }, 5, 5, TimeUnit.SECONDS);
    // }
}

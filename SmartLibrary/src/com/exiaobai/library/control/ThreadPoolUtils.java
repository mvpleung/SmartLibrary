package com.exiaobai.library.control;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.lidroid.xutils.util.LogUtils;

/**
 * 线程池
 * 
 * @author LiangZiChao
 * @Data 2014-7-24下午2:00:09
 * @Package com.xiaobai.xbtrip.network
 */
public class ThreadPoolUtils {

	private ThreadPoolUtils() {

	}

	// 线程池核心线程数
	private static int CORE_POOL_SIZE = 5;

	// 线程池最大线程数
	private static int MAX_POOL_SIZE = 100;

	// 额外线程空状态生存时间
	private static int KEEP_ALIVE_TIME = 10000;

	// 阻塞队列。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程。
	private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);

	// 线程工厂
	private static ThreadFactory threadFactory = new ThreadFactory() {
		private final AtomicInteger integer = new AtomicInteger();

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
		}
	};

	// 线程池
	private static AbstractExecutorService threadPool;

	static {
		threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	/**
	 * 从线程池中抽取线程，执行指定的Runnable对象
	 * 
	 * @param runnable
	 */
	public static void execute(Runnable runnable) {
		threadPool.execute(runnable);
	}

	/**
	 * 中断线程池
	 */
	public static void shutDown() {
		List<Runnable> threadList = threadPool.shutdownNow();
		for (Runnable r : threadList) {
			LogUtils.i("ShutDown_Runnable:" + r.getClass());
		}
	}
}
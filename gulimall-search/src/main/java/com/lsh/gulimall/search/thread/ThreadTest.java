package com.lsh.gulimall.search.thread;

import java.util.Arrays;
import java.util.concurrent.*;

public class ThreadTest {

	public static ExecutorService executorService = Executors.newFixedThreadPool(16);


	public static void main(String[] args) throws ExecutionException, InterruptedException {

		System.out.println("main....");

		CompletableFuture.runAsync(() -> {
			System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running...");
		}, executorService);

		CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running...");
			return 10 / 2;
		}, executorService).whenCompleteAsync((t, u) -> {
			/*结果*/
			System.out.println("res: " + t);
			/*异常*/
			System.out.println("exc: " + u);
			/*出现异常的情况*/
		}).exceptionally(t -> {
			System.out.println("exc:" + t);
			return 10;
			/*完成后处理*/
		}).handle((t, thr) -> {
			System.out.println("ExceptionL: " + thr);
			return t / 5;
		});


		System.out.println(integerCompletableFuture.get());


		/**
		 * 串行化
		 */
		CompletableFuture<Integer> exceptionally = CompletableFuture.supplyAsync(() -> {
			System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running...");
			return 10 / 2;
			/*不能感知执行结果*/
		}, executorService).
//				thenRunAsync(()->{
//			System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running...");
//		}, executorService);
				/*可以感知结果 无返回值*/
//						thenAccept((res) -> {
//					System.out.println(res);
//				});
				/*可以接受返回值 有返回值*/
						thenApplyAsync((res) -> {
					System.out.println(res);
					return res + 1;
				}).exceptionally((r) -> {
					System.out.println(r);
					return 0;
				});

		/*异步编排回调*/
		System.out.println("res: " + exceptionally.get());



		/*两个异步任务 都完成*/
		CompletableFuture<Integer> integerCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("任务1");
			return 10 / 2;
		}, executorService);

		CompletableFuture<Integer> integerCompletableFuture2 = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
			System.out.println("任务2");
			return 10 / 5;
		}, executorService);

//      无法接受返回值
//		integerCompletableFuture1.runAfterBoth(integerCompletableFuture2, () -> {
//			System.out.println("任务3");
//		});

		/*可以接受返回值("字符串") 无返回值*/
		integerCompletableFuture1.thenAcceptBoth(integerCompletableFuture2, (f1, f2) -> {
			System.out.println("f1+f2 = " + f1 + f2);
			System.out.println("任务3");
		});

		CompletableFuture<String> combineAsync = integerCompletableFuture1.thenCombineAsync(integerCompletableFuture2, (f1, f2) -> {
			System.out.println("任务3");
			return "f1+f2 = " + f1 + f2;

		}, executorService);
		System.out.println("combineAsync.get() = " + combineAsync.get());

		System.out.println("其他任务");

		/*两个任务有一个完成执行第三个任务 不感知结果 无返回值*/
		integerCompletableFuture1.runAfterEither(integerCompletableFuture2, () -> {
			System.out.println("--------------------------");
		});

		/*感知结果 无返回值*/
		integerCompletableFuture1.acceptEitherAsync(integerCompletableFuture2, (res) -> {
			System.out.println("res:" + res);
		}, executorService);

		/*感知结果 有返回值*/
		CompletableFuture<Integer> integerCompletableFuture3 = integerCompletableFuture1.applyToEitherAsync(integerCompletableFuture2, (res) -> {
			System.out.println("res:" + res);
			return res + 1;
		}, executorService);

		System.out.println("integerCompletableFuture3.get() = " + integerCompletableFuture3.get());


		executorService.shutdown();
		System.out.println("end.....");

	}


	public static void test(String[] args) throws ExecutionException, InterruptedException {
		/*1*/
//		CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//			System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running...");
//		}, executorService);

		/*2*/
//		CompletableFuture.supplyAsync(new Supplier<Integer>() {
//
//			@Override
//			public Integer get() {
//				return 10 / 2;
//			}
//		}, executorService);

		/*3*/
		CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> 10 / 2, executorService);

		System.out.println("integerCompletableFuture.get() = " + integerCompletableFuture.get());

		executorService.shutdown();
		new ThreadTest().testThread(null);
	}


	public void testThread(String[] args) {

		/*
		 * 1. 继承Thread
		 * 2. 实现Runnable
		 * 3. 实现callable接口+futureTask
		 * 4. 线程池
		 * */
		System.out.println("main start........" + Arrays.toString(args));
//		1
		Thread01 thread01 = new Thread01();
		thread01.start();

//		2
		Thread thread02 = new Thread(() -> System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running..."));

		thread02.start();

//		2
		Thread thread03 = new Thread(new Runnable01());
		thread03.start();


//		3 FutureTask -> Runnable  阻塞等待线程执行完成 可以拿到返回结果
		FutureTask<Integer> integerFutureTask = new FutureTask<>(new Callable01());
		new Thread(integerFutureTask).start();
		try {
			Integer integer = integerFutureTask.get();
			System.out.println("FutureTask: " + integer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FutureTask<Boolean> integerFutureTask1 = new FutureTask<Boolean>(new Runnable01(), true);
		new Thread(integerFutureTask1).start();
		try {
			System.out.println(integerFutureTask1.get());
		} catch (Exception e) {
			e.printStackTrace();
		}


//		4 ThreadPool 推荐 控制资源 submit可以获取到返回值  execute 无返回值 1,2无法获取返回值 1,2,3都无法控制资源
		/*1*/
		executorService.execute(new Runnable01());

		/*2*/
		executorService.submit(new Runnable01());
		executorService.shutdown();
		/**
		 7大参数
		 *  corePoolSize 核心线程池 创建好以后就准备就绪的线程数量 一直存在(不允许超时情况)
		 *  maximumPoolSize 最大线程数量 控制资源并发
		 *  keepAliveTime 当前线程数量大于核心数量,空闲线程等待最大存活时间后释放 maximumPoolSize-corePoolSize
		 *  unit 时间单位
		 *  BlockingQueue<Runnable> workQueue 指定数量 阻塞队列 如果任务过多 就会将目前将多的任务放在阻塞队列 有空闲线程就会从阻塞队列中取出新的任务执行 默认最大值 -> Integer 最大值
		 *  ThreadFactory threadFactory 线程的创建工厂
		 *  RejectedExecutionHandler handler 拒绝策略 如果队列满了,按照指定的拒绝执行策略拒绝执行任务 默认 ThreadPoolExecutor.AbortPolicy 丢弃策略
		 *  不抛弃继续执行 CallerRunsPolicy
		 * */
		/*初始8 超过8 ->进入阻塞队列 阻塞队列满了  -> 16 超过16 -> 200 超过200+16 拒绝策略*/
		/*3*/
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 5000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(200), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
		threadPoolExecutor.execute(new Runnable01());
		threadPoolExecutor.allowCoreThreadTimeOut(true);
		threadPoolExecutor.shutdown();

		/*初始大小0,最大 integer 所有都可以回收*/
		ExecutorService executorService1 = Executors.newCachedThreadPool();
		/*固定大小*/
		ExecutorService executorService2 = Executors.newFixedThreadPool(20);
		executorService1.execute(new Runnable01());
		executorService2.execute(new Runnable01());
		executorService1.shutdown();
		executorService2.shutdown();
		/*可以定时执行*/
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);
		/*10s后执行*/
		scheduledExecutorService.schedule(new Runnable01(), 10000, TimeUnit.MILLISECONDS);
		scheduledExecutorService.schedule(new Runnable01(), 10000, TimeUnit.MILLISECONDS);
		scheduledExecutorService.shutdown();
		/*单线程 后代队列获取任务单个执行*/
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.shutdown();
		System.out.println("main end..........");
	}


	public static class Thread01 extends Thread {

		@Override
		public void run() {
			System.out.println(currentThread().getId() + ":" + currentThread().getName() + " running...");
		}
	}


	public static class Runnable01 implements Runnable {

		@Override
		public void run() {
			System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running...");
		}
	}

	public static class Callable01 implements Callable<Integer> {


		@Override
		public Integer call() {
			System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + " running...");
			return 10 / 2;
		}
	}


}

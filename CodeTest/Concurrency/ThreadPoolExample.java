import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * 线程池示例 - ExecutorService使用
 * =============================================================================
 *
 * 本示例演示了Java线程池的创建和使用，包括：
 * 1. FixedThreadPool - 固定大小的线程池
 * 2. CachedThreadPool - 缓存线程池
 * 3. ScheduledThreadPool - 定时任务线程池
 * 4. SingleThreadExecutor - 单线程执行器
 * 5. 自定义ThreadPoolExecutor
 *
 * 【为什么需要线程池？】
 *
 * 如果我们为每个任务都创建一个新线程，会有以下问题：
 * 1. 线程创建和销毁的开销：创建线程需要分配栈内存、初始化线程局部存储等
 * 2. 线程生命周期的管理：需要跟踪线程状态、处理异常等
 * 3. 资源耗尽风险：如果请求过多，会创建大量线程，导致内存耗尽（OOM）
 *
 * 线程池通过复用线程来解决这些问题：
 * - 线程池维护一定数量的工作线程
 * - 任务被提交到队列，由工作线程从队列中取出并执行
 * - 任务完成后，线程不会销毁，而是继续等待下一个任务
 *
 * 【线程池的核心组成部分】
 * 1. 工作线程集合（Worker Threads）
 * 2. 任务队列（Task Queue）
 * 3. 线程工厂（Thread Factory）
 * 4. 拒绝策略（Rejected Execution Handler）
 *
 * 【线程池的工作流程】
 * 1. 提交任务到线程池
 * 2. 如果工作线程数 < 核心线程数，创建新工作线程
 * 3. 如果工作线程数 >= 核心线程数，将任务加入队列
 * 4. 如果队列已满且工作线程数 < 最大线程数，创建新工作线程
 * 5. 如果队列已满且工作线程数 >= 最大线程数，执行拒绝策略
 *
 * =============================================================================
 */
public class ThreadPoolExample {

    // =============================================================================
    // 任务计数器
    // =============================================================================
    // AtomicInteger是原子整数类，基于CAS操作实现线程安全
    // 用于统计任务执行次数等需要原子操作的场景
    // =============================================================================
    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    /**
     * =============================================================================
     * main方法 - 程序入口
     * =============================================================================
     *
     * 本方法演示了Java中常用的几种线程池类型，以及如何创建自定义线程池。
     *
     * 【线程池的创建方式】
     * 1. 使用Executors工具类的静态工厂方法（简单快捷）
     * 2. 直接创建ThreadPoolExecutor（更灵活，可自定义参数）
     *
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时抛出
     * @throws ExecutionException 当任务执行抛出异常时抛出
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=== 线程池示例 ===");
        System.out.println("本示例演示Java中常用的线程池类型和使用场景");
        System.out.println();

        // 测试固定大小的线程池
        testFixedThreadPool();

        // 测试缓存线程池
        testCachedThreadPool();

        // 测试定时线程池
        testScheduledThreadPool();

        // 测试单线程执行器
        testSingleThreadExecutor();

        // 测试自定义线程池
        testCustomThreadPool();

        System.out.println("\n所有线程池测试完成！");
    }

    /**
     * =============================================================================
     * 测试 FixedThreadPool（固定大小线程池）
     * =============================================================================
     *
     * 【创建方式】
     * ExecutorService executor = Executors.newFixedThreadPool(n);
     *
     * 【特点】
     * - 核心线程数 = 最大线程数 = n
     * - 没有临时线程（keepAliveTime = 0）
     * - 任务队列是无限队列（LinkedBlockingQueue）
     *
     * 【适用场景】
     * - CPU密集型任务（需要限制线程数，避免过度竞争CPU）
     * - 需要控制并发数量的场景
     * - 负载相对稳定的场景
     *
     * 【注意事项】
     * - 如果所有线程都在忙碌，新任务会在队列中等待
     * - 队列不会满，因此不会触发拒绝策略
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testFixedThreadPool() throws InterruptedException {
        System.out.println("\n--- 测试 FixedThreadPool ---");
        System.out.println("特点：固定3个工作线程，任务交替执行");

        // =================================================================
        // 创建固定大小线程池
        // =================================================================
        // 参数3表示线程池中有3个工作线程
        // 这3个线程会被复用，处理所有提交的任务
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // =================================================================
        // 提交10个任务
        // =================================================================
        // 由于只有3个工作线程，任务会分批执行
        // 例如：任务1、2、3同时开始，任务4、5、6等待...
        for (int i = 0; i < 10; i++) {
            final int taskId = i + 1;
            executor.execute(() -> {
                // Thread.currentThread().getName() 获取执行当前任务的线程名
                System.out.println("固定线程池 - 任务" + taskId
                    + " 由线程 " + Thread.currentThread().getName() + " 执行");

                try {
                    // 模拟任务执行时间
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // =================================================================
        // 关闭线程池
        // =================================================================
        // shutdown()：不再接受新任务，但会执行已提交的任务
        // 区别于shutdownNow()：尝试取消正在执行的任务
        executor.shutdown();

        // =================================================================
        // 等待线程池终止
        // =================================================================
        // awaitTermination()：等待线程池完成所有任务或超时
        // 如果所有任务在10秒内完成，返回true；否则返回false
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("固定线程池任务完成");
    }

    /**
     * =============================================================================
     * 测试 CachedThreadPool（缓存线程池）
     * =============================================================================
     *
     * 【创建方式】
     * ExecutorService executor = Executors.newCachedThreadPool();
     *
     * 【特点】
     * - 核心线程数 = 0
     * - 最大线程数 = Integer.MAX_VALUE（几乎无限制）
     * - 临时线程的空闲存活时间 = 60秒
     * - 使用SynchronousQueue作为任务队列
     *
     * 【适用场景】
     * - 短命异步任务（任务执行很快）
     * - 需要处理大量短期任务的场景
     * - 负载不确定或波动较大的场景
     *
     * 【SynchronousQueue的特性】
     * - 队列不存储元素，每个插入操作必须等待一个删除操作
     * - 每个任务直接提交给工作线程，不排队
     * - 如果没有空闲线程，会创建新线程
     *
     * 【注意事项】
     * - 如果任务执行时间很长，会创建大量线程
     * - 可能导致线程数过多，占用过多系统资源
     * - 需要谨慎使用，避免资源耗尽
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testCachedThreadPool() throws InterruptedException {
        System.out.println("\n--- 测试 CachedThreadPool ---");
        System.out.println("特点：线程数动态增长，60秒后回收空闲线程");

        // 创建缓存线程池（不指定大小）
        ExecutorService executor = Executors.newCachedThreadPool();

        // =================================================================
        // 提交20个任务
        // =================================================================
        // CachedThreadPool会为每个任务尝试创建新线程（如果需要）
        // 由于任务执行时间较短（200ms），线程会在任务完成后被复用
        for (int i = 0; i < 20; i++) {
            final int taskId = i + 1;
            executor.execute(() -> {
                System.out.println("缓存线程池 - 任务" + taskId
                    + " 由线程 " + Thread.currentThread().getName() + " 执行");

                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("缓存线程池任务完成");
    }

    /**
     * =============================================================================
     * 测试 ScheduledThreadPool（定时任务线程池）
     * =============================================================================
     *
     * 【创建方式】
     * ScheduledExecutorService executor = Executors.newScheduledThreadPool(n);
     *
     * 【特点】
     * - 核心线程数 = n
     * - 最大线程数 = Integer.MAX_VALUE
     * - 临时线程的空闲存活时间 = 10秒
     * - 支持定时任务和周期任务
     *
     * 【适用场景】
     * - 定时任务（如每天凌晨执行数据备份）
     * - 周期任务（如每分钟检查系统状态）
     * - 延迟任务（如用户登录后30分钟发送提醒）
     *
     * 【ScheduledExecutorService的方法】
     * - schedule(Runnable, delay, TimeUnit)：延迟执行一次
     * - scheduleAtFixedRate(Runnable, initialDelay, period, TimeUnit)：
     *   固定周期执行（如果任务执行时间超过周期，会等待上一个任务完成）
     * - scheduleWithFixedDelay(Runnable, initialDelay, delay, TimeUnit)：
     *   固定延迟执行（上一个任务完成后，延迟delay再执行下一个）
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testScheduledThreadPool() throws InterruptedException {
        System.out.println("\n--- 测试 ScheduledThreadPool ---");
        System.out.println("特点：支持定时和周期任务");

        // =================================================================
        // 创建定时线程池
        // =================================================================
        // 参数2表示有2个工作线程
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        // =================================================================
        // 延迟执行任务
        // =================================================================
        // schedule()：延迟5秒后执行一次
        // 注意：这个方法返回ScheduledFuture，可以用来取消任务或检查状态
        executor.schedule(() -> {
            System.out.println("定时任务 - 延迟5秒执行");
        }, 5, TimeUnit.SECONDS);

        // =================================================================
        // 周期执行任务
        // =================================================================
        // scheduleAtFixedRate()：按固定周期执行
        // 参数：初始延迟1秒，周期2秒
        // 注意：如果任务执行时间超过周期，下一次任务会等待（不会并发）
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            System.out.println("定时任务 - 周期性执行，时间: " + System.currentTimeMillis());
        }, 1, 2, TimeUnit.SECONDS);

        // =================================================================
        // 取消周期任务
        // =================================================================
        // schedule()：延迟10秒后执行，用于取消周期性任务
        executor.schedule(() -> {
            // future.cancel()：取消任务
            // 参数false：不会中断正在执行的任务
            future.cancel(false);
            System.out.println("周期性任务已取消");
        }, 10, TimeUnit.SECONDS);

        // 运行15秒（包含所有定时任务的执行）
        TimeUnit.SECONDS.sleep(15);

        executor.shutdown();
        System.out.println("定时线程池任务完成");
    }

    /**
     * =============================================================================
     * 测试 SingleThreadExecutor（单线程执行器）
     * =============================================================================
     *
     * 【创建方式】
     * ExecutorService executor = Executors.newSingleThreadExecutor();
     *
     * 【特点】
     * - 核心线程数 = 最大线程数 = 1
     * - 只有一个工作线程
     * - 任务按顺序执行
     *
     * 【适用场景】
     * - 需要保证任务顺序执行的场景
     * - 需要线程安全的单例场景
     * - 后台任务串行执行（如日志写入）
     *
     * 【额外保障】
     * - SingleThreadExecutor内部使用了更强的内存屏障
     * - 确保任务执行的可见性和有序性
     * - 如果工作线程意外终止，会创建新线程继续执行
     *
     * 【与FixedThreadPool(1)的区别】
     * - SingleThreadExecutor额外保证了任务的顺序性
     * - SingleThreadExecutor有更强的线程安全保证
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testSingleThreadExecutor() throws InterruptedException {
        System.out.println("\n--- 测试 SingleThreadExecutor ---");
        System.out.println("特点：单一工作线程，任务按顺序执行");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // =================================================================
        // 提交5个任务
        // =================================================================
        // 由于只有一个工作线程，任务会严格按顺序执行
        // 例如：任务1完成后才执行任务2，任务2完成后才执行任务3...
        for (int i = 0; i < 5; i++) {
            final int taskId = i + 1;
            executor.execute(() -> {
                System.out.println("单线程池 - 任务" + taskId
                    + " 由线程 " + Thread.currentThread().getName() + " 执行");

                try {
                    TimeUnit.SECONDS.sleep(1);  // 每个任务执行1秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("单线程池任务完成");
    }

    /**
     * =============================================================================
     * 测试自定义 ThreadPoolExecutor
     * =============================================================================
     *
     * 【ThreadPoolExecutor的构造参数】
     *
     * 1. corePoolSize：核心线程数
     *    - 线程池维护的最小线程数
     *    - 即使空闲也不会被回收（除非设置allowCoreThreadTimeOut）
     *
     * 2. maximumPoolSize：最大线程数
     *    - 线程池允许的最大线程数
     *    - 当队列满时，会创建临时线程（直到达到最大线程数）
     *
     * 3. keepAliveTime：临时线程的空闲存活时间
     *    - 超过核心线程数的线程，在空闲超过此时间后会被回收
     *    - 0表示临时线程立即回收
     *
     * 4. unit：keepAliveTime的单位
     *
     * 5. workQueue：任务队列
     *    - ArrayBlockingQueue：有界队列，需要指定容量
     *    - LinkedBlockingQueue：无界队列（默认Integer.MAX_VALUE）
     *    - SynchronousQueue：同步队列，不存储元素
     *
     * 6. threadFactory：线程工厂
     *    - 用于创建新线程
     *    - 可以自定义线程名称、优先级、是否为守护线程等
     *
     * 7. handler：拒绝策略
     *    - 当线程池和队列都满时，如何处理新任务
     *
     * 【线程池的工作流程（更详细）】
     * 1. 提交任务
     * 2. 如果运行线程数 < corePoolSize，创建新核心线程执行任务
     * 3. 如果运行线程数 >= corePoolSize，将任务加入队列
     * 4. 如果队列已满且运行线程数 < maximumPoolSize，创建临时线程执行任务
     * 5. 如果队列已满且运行线程数 >= maximumPoolSize，执行拒绝策略
     *
     * 【拒绝策略】
     * - AbortPolicy（默认）：抛出RejectedExecutionException
     * - CallerRunsPolicy：由提交任务的线程执行
     * - DiscardPolicy：直接丢弃任务
     * - DiscardOldestPolicy：丢弃队列中最老的任务，然后重试
     *
     * @throws InterruptedException 当线程被中断时抛出
     * @throws ExecutionException 当任务执行抛出异常时抛出
     */
    private static void testCustomThreadPool() throws InterruptedException, ExecutionException {
        System.out.println("\n--- 测试自定义 ThreadPoolExecutor ---");
        System.out.println("特点：可自定义所有参数");

        // =================================================================
        // 配置线程池参数
        // =================================================================
        int corePoolSize = 2;          // 核心线程数：2
        int maximumPoolSize = 4;       // 最大线程数：4
        long keepAliveTime = 60L;      // 临时线程存活时间：60秒
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(10);  // 队列容量：10
        ThreadFactory threadFactory = new CustomThreadFactory();  // 自定义线程工厂
        RejectedExecutionHandler handler = new CustomRejectionHandler();  // 自定义拒绝策略

        // =================================================================
        // 创建自定义线程池
        // =================================================================
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            corePoolSize,              // 核心线程数
            maximumPoolSize,           // 最大线程数
            keepAliveTime,            // 空闲时间
            TimeUnit.SECONDS,         // 时间单位
            workQueue,                // 任务队列
            threadFactory,            // 线程工厂
            handler                   // 拒绝策略
        );

        System.out.println("线程池配置：核心线程=2, 最大线程=4, 队列容量=10");

        // =================================================================
        // 提交15个任务
        // =================================================================
        // 分析：
        // - 前2个任务：创建核心线程1和2执行
        // - 任务3-12（10个）：加入队列（队列容量10）
        // - 任务13-15（3个）：队列已满，创建临时线程3和4执行
        // - 第15个任务：队列满、线程数已达最大，执行拒绝策略
        for (int i = 0; i < 15; i++) {
            final int taskId = i + 1;
            try {
                executor.execute(() -> {
                    System.out.println("自定义线程池 - 任务" + taskId
                        + " 由线程 " + Thread.currentThread().getName() + " 执行");

                    try {
                        TimeUnit.MILLISECONDS.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (RejectedExecutionException e) {
                System.out.println("任务" + taskId + " 被拒绝执行");
            }
        }

        // =================================================================
        // 使用Future获取任务结果
        // =================================================================
        // submit()方法返回一个Future对象
        // Future代表异步计算的结果
        Future<Integer> future = executor.submit(() -> {
            System.out.println("Callable任务执行 - 这是一个有返回值的任务");
            TimeUnit.SECONDS.sleep(1);
            return 42;  // 返回值
        });

        try {
            // get()：阻塞等待任务完成并获取结果
            // 也可以设置超时：get(timeout, TimeUnit)
            Integer result = future.get(5, TimeUnit.SECONDS);
            System.out.println("Callable任务结果: " + result);
        } catch (TimeoutException e) {
            System.out.println("任务超时");
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("自定义线程池任务完成");
    }

    // =============================================================================
    // 自定义线程工厂
    // =============================================================================
    /**
     * CustomThreadFactory - 自定义线程工厂
     *
     * 【ThreadFactory的作用】
     * - 用于创建新线程
     * - 可以自定义线程的名称、优先级、守护状态等
     * - 方便排查问题和监控系统
     *
     * 【为什么要自定义线程工厂？】
     * - 设置有意义的线程名称（便于问题排查）
     * - 设置线程为守护线程或用户线程
     * - 设置线程优先级
     * - 添加线程创建前后的钩子（如记录日志）
     *
     * 【默认线程工厂（Executors.defaultThreadFactory()）】
     * - 创建的线程名称格式：pool-N-thread-M
     * - 优先级为普通优先级
     * - 非守护线程
     */
    static class CustomThreadFactory implements ThreadFactory {
        // 原子整数，用于生成唯一的线程编号
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        /**
         * 创建新线程
         *
         * @param r 要由新线程执行的Runnable任务
         * @return 新创建的Thread
         */
        @Override
        public Thread newThread(Runnable r) {
            // 创建新线程，设置有意义的名称
            Thread thread = new Thread(r, "CustomThread-" + threadNumber.getAndIncrement());

            // 设置为非守护线程（用户线程）
            // 守护线程在所有用户线程结束时会被JVM强制终止
            thread.setDaemon(false);

            // 设置线程优先级为普通优先级
            // 注意：不建议随意提高优先级，可能导致其他线程饥饿
            thread.setPriority(Thread.NORM_PRIORITY);

            return thread;
        }
    }

    // =============================================================================
    // 自定义拒绝策略
    // =============================================================================
    /**
     * CustomRejectionHandler - 自定义拒绝策略
     *
     * 【RejectedExecutionHandler接口】
     * 当线程池无法接受新任务时，会调用此接口的实现
     *
     * 【为什么需要自定义拒绝策略？】
     * - 默认策略（AbortPolicy）会抛出异常，可能导致任务丢失
     * - 可以记录日志、发送告警、执行降级处理等
     * - 可以让提交任务的线程自己执行（CallerRunsPolicy）
     *
     * 【常见的拒绝策略】
     * 1. AbortPolicy：抛出RejectedExecutionException（默认）
     * 2. CallerRunsPolicy：由提交任务的线程执行
     * 3. DiscardPolicy：静默丢弃
     * 4. DiscardOldestPolicy：丢弃最老的任务
     */
    static class CustomRejectionHandler implements RejectedExecutionHandler {

        /**
         * 处理被拒绝的任务
         *
         * @param r 被拒绝的任务
         * @param executor 执行任务的线程池
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 打印线程池的当前状态，帮助诊断问题
            System.out.println("任务被拒绝，线程池状态: "
                + "活动线程数: " + executor.getActiveCount()
                + ", 队列大小: " + executor.getQueue().size()
                + ", 已完成任务: " + executor.getCompletedTaskCount());

            // 抛出异常（也可以选择其他处理方式）
            throw new RejectedExecutionException("任务被拒绝执行");
        }
    }

    // =============================================================================
    // 扩展知识点
    // =============================================================================
    /**
     * 【线程池的关闭】
     *
     * 1. shutdown()
     *    - 不再接受新任务
     *    - 已提交的任务会继续执行
     *    - 队列中的任务会执行完成
     *
     * 2. shutdownNow()
     *    - 尝试停止所有正在执行的任务
     *    - 停止处理队列中的任务
     *    - 返回未执行的任务列表
     *    - 注意：不保证能停止所有任务
     *
     * 3. awaitTermination()
     *    - 阻塞等待线程池终止
     *    - 通常在shutdown()后调用
     *    - 用于确保所有任务完成后再继续
     *
     * =================================================================
     *
     * 【合理配置线程池参数】
     *
     * 1. CPU密集型任务
     *    - 核心线程数 = CPU核心数 + 1
     *    - 原因：CPU密集型任务需要大量CPU时间，没有太多等待
     *
     * 2. IO密集型任务
     *    - 核心线程数 = CPU核心数 * 2（或更多）
     *    - 原因：IO密集型任务大部分时间在等待，CPU空闲
     *
     * 3. 混合型任务
     *    - 使用工具类根据实际情况调整
     *    - Runtime.getRuntime().availableProcessors() 获取CPU核心数
     *
     * 4. 队列选择
     *    - 有界队列：控制内存使用，防止OOM
     *    - 无界队列：任务多时占用大量内存
     *    - 同步队列：任务直接提交给线程，不排队
     *
     * =================================================================
     *
     * 【监控线程池】
     *
     * ThreadPoolExecutor提供的监控方法：
     * - getPoolSize()：当前线程数
     * - getActiveCount()：正在执行任务的线程数
     * - getCompletedTaskCount()：已完成的任务数
     * - getTaskCount()：总任务数
     * - getQueue().size()：队列中的任务数
     *
     * 推荐使用监控工具：
     * - Micrometer + Prometheus
     * - Spring Boot Actuator
     * - 自定义监控指标
     *
     * =================================================================
     *
     * 【ForkJoinPool】
     *
     * Java 7引入的另一种线程池，专门用于分治任务：
     * - Work-Stealing算法：每个线程有自己的任务队列
     * - 当一个线程空闲时，从其他线程的队列"偷"任务
     * - 适合递归任务（如归并排序、快速排序）
     *
     * 示例：
     * ForkJoinPool pool = ForkJoinPool.commonPool();
     * ForkJoinTask<T> task = new MyRecursiveTask(...);
     * T result = pool.invoke(task);
     *
     * =================================================================
     *
     * 【并行流与线程池】
     *
     * Java 8的parallelStream()使用ForkJoinPool：
     * - 默认使用公共线程池（ForkJoinPool.commonPool()）
     * - 线程数默认为CPU核心数 - 1
     * - 可以通过系统属性java.util.concurrent.ForkJoinPool.common.parallelism设置
     *
     * 注意：
     * - 长时间运行的流操作会占用公共线程池
     * - 可能影响其他使用公共线程池的代码
     * - 建议使用自定义线程池处理长时间任务
     *
     * 示例：
     * ExecutorService executor = Executors.newFixedThreadPool(4);
     * List<R> results = data.parallelStream()
     *     .map(e -> {
     *         // 自定义线程池执行
     *     })
     *     .collect(Collectors.toList());
     *
     * =================================================================
     *
     * 【思考题】
     *
     * 1. FixedThreadPool和CachedThreadPool哪个更好？
     *    - 没有绝对的答案，取决于任务特性
     *    - FixedThreadPool：适合负载稳定的场景
     *    - CachedThreadPool：适合短命任务、负载波动的场景
     *    - 需要根据实际情况选择和调优
     *
     * 2. 为什么单线程执行器能保证线程安全？
     *    - 只有一个工作线程，所有任务串行执行
     *    - 不存在并发访问共享资源的问题
     *    - 相当于一个天然的同步机制
     *
     * 3. 如何选择任务队列？
     *    - 有界队列（ArrayBlockingQueue）：控制内存，防止OOM
     *    - 无界队列（LinkedBlockingQueue）：任务不能丢失，但可能占用大量内存
     *    - 同步队列（SynchronousQueue）：不存储任务，直接提交给线程
     *
     * 4. 什么时候需要自定义线程池？
     *    - 默认线程池参数不满足需求
     *    - 需要自定义线程名称（方便排查问题）
     *    - 需要特殊的拒绝策略
     *    - 需要监控和统计数据
     */
}
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * Future和Callable示例
 * =============================================================================
 *
 * 本示例演示了Java中Future和Callable的用法，用于异步任务处理。
 *
 * 【什么是Callable？】
 *
 * Callable是一个函数式接口，类似于Runnable，但有两个关键区别：
 *
 * 1. 返回值：
 *    - Callable有返回值（泛型类型V）
 *    - Runnable没有返回值
 *
 * 2. 异常处理：
 *    - Callable允许抛出受检异常
 *    - Runnable不允许抛出受检异常
 *
 * public interface Callable<V> {
 *     V call() throws Exception;
 * }
 *
 * 【什么是Future？】
 *
 * Future表示一个异步计算的结果。它提供了检查计算是否完成、
 * 等待计算完成、以及获取计算结果的方法。
 *
 * 核心方法：
 * - get()：获取结果（阻塞直到完成）
 * - get(timeout)：获取结果（阻塞直到超时）
 * - cancel()：取消任务
 * - isDone()：检查是否完成
 * - isCancelled()：检查是否已取消
 *
 * 【Future的局限性】
 *
 * 1. 不能手动完成：
 *    - Future代表的是一个"未来"的结果
 *    - 但你不能直接完成一个Future
 *    - 必须通过ExecutorService提交任务
 *
 * 2. 不能组合：
 *    - 不能方便地组合多个Future
 *    - 等待所有Future完成需要自己实现循环
 *
 * 3. 没有回调：
 *    - 任务完成后只能通过get()阻塞获取结果
 *    - 不能注册回调函数
 *
 * 【Future vs CompletableFuture】
 *
 * CompletableFuture是Future的增强版，解决了上述局限：
 * - 可以手动完成
 * - 支持组合和链式调用
 * - 支持回调机制
 *
 * 本示例展示了Future的基础用法，为学习CompletableFuture打下基础。
 *
 * =============================================================================
 */
public class FutureCallableExample {

    /**
     * =============================================================================
     * main方法 - 程序入口
     * =============================================================================
     *
     * 演示Future和Callable的多种使用场景：
     * 1. 基本用法：提交任务，获取结果
     * 2. 超时处理：防止无限等待
     * 3. 多个任务：并行处理和等待
     * 4. FutureTask：可管理的任务包装器
     * 5. 异常处理：捕获任务执行中的异常
     *
     * @param args 命令行参数
     * @throws Exception 可能抛出的异常
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== Future和Callable示例 ===");
        System.out.println("本示例演示异步任务处理的基础用法");
        System.out.println();

        // 测试基本Future使用
        testBasicFuture();

        // 测试Future超时处理
        testFutureWithTimeout();

        // 测试多个Future并行处理
        testMultipleFutures();

        // 测试FutureTask的使用
        testFutureTask();

        // 测试异常处理
        testExceptionHandling();

        System.out.println("\n所有Future和Callable测试完成！");
    }

    /**
     * =============================================================================
     * 测试基本Future使用
     * =============================================================================
     *
     * 【核心概念】
     *
     * 1. 提交Callable任务：
     *    ExecutorService.submit(Callable<T>) 返回 Future<T>
     *
     * 2. 主线程继续执行：
     *    提交任务后，主线程可以继续做其他事情
     *    不需要等待任务完成
     *
     * 3. 获取结果：
     *    future.get() - 阻塞直到结果可用
     *
     * 【执行流程】
     *
     * 主线程                          Executor线程
     *   |                                |
     *   |-- submit(Callable) -->        |
     *   |                                |-- 执行计算
     *   |                                |   (耗时2秒)
     *   |-- 继续执行其他任务              |
     *   |   (耗时0.6秒)                  |
     *   |                                |
     *   |-- get() --阻塞--               |-- 返回结果
     *   |                                |
     *   |-- 获得结果，继续执行            |
     *
     * 【为什么使用线程池？】
     *
     * 直接创建线程的缺点：
     * - 每次创建线程都有开销
     * - 无限制创建线程会导致OOM
     *
     * 线程池的优点：
     * - 复用线程，减少开销
     * - 控制线程数量，避免资源耗尽
     * - 提供任务管理功能
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testBasicFuture() throws Exception {
        System.out.println("\n--- 测试基本Future使用 ---");
        System.out.println("特点：主线程提交任务后可以继续执行，最后获取结果");

        // 创建固定大小线程池（2个工作线程）
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // =================================================================
        // 提交Callable任务
        // =================================================================
        // submit方法接受Callable<T>，返回Future<T>
        // 线程池会分配一个线程来执行这个任务
        Future<Integer> future = executor.submit(new CalculationTask(10));

        System.out.println("主线程继续执行其他任务...");
        System.out.println("（任务正在后台执行）");

        // =================================================================
        // 主线程做其他工作
        // =================================================================
        // 模拟主线程执行其他任务（总共0.6秒）
        for (int i = 0; i < 3; i++) {
            System.out.println("主线程执行任务 " + (i+1));
            TimeUnit.MILLISECONDS.sleep(200);  // 每次0.2秒
        }

        // =================================================================
        // 获取异步任务结果
        // =================================================================
        // get()方法是阻塞的
        // 如果任务还没完成，会一直等待
        // 如果任务完成，立即返回结果
        if (!future.isDone()) {
            System.out.println("异步任务尚未完成，等待结果...");
        }

        // 阻塞获取结果
        Integer result = future.get();
        System.out.println("异步任务结果: " + result);

        // =================================================================
        // 关闭线程池
        // =================================================================
        // shutdown()：不再接受新任务，等待已提交任务完成
        executor.shutdown();

        // 等待线程池终止（最多3秒）
        executor.awaitTermination(3, TimeUnit.SECONDS);
    }

    /**
     * =============================================================================
     * 测试Future超时处理
     * =============================================================================
     *
     * 【为什么需要超时？】
     *
     * future.get() 会一直阻塞直到任务完成。
     * 如果任务卡住，主线程也会一直等待。
     *
     * 超时机制：
     * - 防止无限等待
     * - 允许放弃长时间运行的任务
     * - 提高系统的响应性
     *
     * 【超时后的处理】
     *
     * 1. 捕获TimeoutException
     * 2. 调用future.cancel(true)尝试取消任务
     *    - true：尝试中断正在执行任务的线程
     *    - false：不中断，只是标记任务为取消状态
     * 3. 检查任务是否成功取消
     *
     * 【cancel()的注意事项】
     *
     * 1. 如果任务还没开始：会被标记为已取消，不会执行
     * 2. 如果任务正在执行：尝试中断线程（调用interrupt()）
     * 3. 如果任务已完成：cancel()返回false
     *
     * 4. 中断不一定成功：
     *    - 如果任务不响应中断（没有阻塞在可中断的方法上）
     *    - 任务可能继续执行
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testFutureWithTimeout() throws Exception {
        System.out.println("\n--- 测试Future超时处理 ---");
        System.out.println("特点：设置超时时间，避免无限等待");

        // 创建单线程执行器
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // =================================================================
        // 提交长时间运行的任务
        // =================================================================
        Future<String> future = executor.submit(() -> {
            System.out.println("长时间任务开始执行...");
            System.out.println("（模拟需要5秒才能完成）");

            // 模拟长时间运行的任务
            // 如果这个任务不响应中断，会一直运行
            TimeUnit.SECONDS.sleep(5);

            return "长时间任务完成";
        });

        try {
            // =================================================================
            // 设置超时时间为2秒
            // =================================================================
            // 如果2秒内任务没完成，会抛出TimeoutException
            System.out.println("等待任务完成，超时时间2秒...");
            String result = future.get(2, TimeUnit.SECONDS);
            System.out.println("任务结果: " + result);

        } catch (TimeoutException e) {
            // =================================================================
            // 处理超时
            // =================================================================
            System.out.println("任务超时（超过2秒），取消任务...");

            // 尝试取消任务
            // true：尝试中断正在执行的线程
            boolean cancelled = future.cancel(true);

            // 检查取消结果
            if (future.isCancelled()) {
                System.out.println("任务已成功取消");
            } else {
                System.out.println("任务取消失败，可能已经完成");
            }
        }

        // 关闭线程池
        executor.shutdown();
    }

    /**
     * =============================================================================
     * 测试多个Future并行处理
     * =============================================================================
     *
     * 【并行处理的优势】
     *
     * 假设每个任务需要1秒：
     * - 串行执行：4个任务 = 4秒
     * - 并行执行：4个任务 = 1秒（理论上）
     *
     * 实际上由于线程调度开销，可能略多于1秒
     *
     * 【等待多个Future完成的方式】
     *
     * 方式1：轮询isDone()（本例使用）
     * while (!allDone) {
     *     if (future1.isDone() && future2.isDone() && ...) {
     *         allDone = true;
     *     }
     * }
     *
     * 方式2：使用CountDownLatch
     * CountDownLatch latch = new CountDownLatch(4);
     * // 每个任务完成后 latch.countDown()
     * latch.await();
     *
     * 方式3：使用CompletableFuture.allOf()
     * CompletableFuture.allOf(f1, f2, f3, f4).join();
     *
     * 【获取多个结果】
     *
     * 每个Future需要单独调用get()获取结果
     * 注意：此时所有任务都已完成，get()会立即返回
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testMultipleFutures() throws Exception {
        System.out.println("\n--- 测试多个Future并行处理 ---");
        System.out.println("特点：提交多个任务并行执行，提高效率");

        // 创建4个工作线程的线程池
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // =================================================================
        // 提交多个异步任务
        // =================================================================
        // 4个任务并行执行，理论上只需要约1秒（最慢的任务）
        // 而不是串行的4秒
        Future<Integer> task1 = executor.submit(new CalculationTask(5));
        Future<Integer> task2 = executor.submit(new CalculationTask(8));
        Future<Integer> task3 = executor.submit(new CalculationTask(12));
        Future<Integer> task4 = executor.submit(new CalculationTask(15));

        System.out.println("所有任务已提交，等待结果...");
        System.out.println("（任务并行执行，总时间约等于最慢任务的时间）");

        // =================================================================
        // 等待所有任务完成
        // =================================================================
        // 使用轮询方式检查所有任务是否完成
        // 每次检查间隔0.5秒
        while (!(task1.isDone() && task2.isDone() && task3.isDone() && task4.isDone())) {
            System.out.println("等待任务完成...");
            TimeUnit.MILLISECONDS.sleep(500);
        }

        System.out.println("所有任务已完成！");

        // =================================================================
        // 获取所有结果
        // =================================================================
        // 此时所有get()都会立即返回，因为任务已完成
        int result1 = task1.get();
        int result2 = task2.get();
        int result3 = task3.get();
        int result4 = task4.get();

        int total = result1 + result2 + result3 + result4;

        System.out.println("任务1结果: " + result1);
        System.out.println("任务2结果: " + result2);
        System.out.println("任务3结果: " + result3);
        System.out.println("任务4结果: " + result4);
        System.out.println("总和: " + total);

        executor.shutdown();
    }

    /**
     * =============================================================================
     * 测试FutureTask的使用
     * =============================================================================
     *
     * 【什么是FutureTask？】
     *
     * FutureTask是Future的实现类，同时实现了Runnable接口。
     * 它包装了一个Callable或Runnable，可以：
     * - 提交给线程池执行
     * - 作为普通Runnable由Thread执行
     * - 直接调用run()执行（同步）
     *
     * public class FutureTask<V> implements RunnableFuture<V>
     * public interface RunnableFuture<V> extends Runnable, Future<V>
     *
     * 【使用场景】
     *
     * 1. 延迟执行：
     *    FutureTask futureTask = new FutureTask<>(() -> {
     *        // 耗时计算
     *        return result;
     *    });
     *    new Thread(futureTask).start();  // 稍后启动
     *    // 或者现在不启动，稍后再启动
     *
     * 2. 一次性执行：
     *    FutureTask futureTask = new FutureTask<>(callable);
     *    // 可以多次start()吗？不行！
     *    // FutureTask只能运行一次
     *
     * 3. 在任务执行前做其他事情：
     *    FutureTask futureTask = new FutureTask<>(...);
     *    // 先做其他准备
     *    prepare();
     *    // 再启动任务
     *    futureTask.run();
     *
     * 【FutureTask vs 直接submit】
     *
     * FutureTask：
     * - 可以控制任务何时开始
     * - 可以创建后不立即执行
     * - 只能执行一次
     *
     * executor.submit()：
     * - 提交后立即由线程池执行
     * - 可以提交多次相同的Callable
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testFutureTask() throws Exception {
        System.out.println("\n--- 测试FutureTask ---");
        System.out.println("特点：可管理的任务包装器，控制任务执行时机");

        // =================================================================
        // 创建FutureTask
        // =================================================================
        // FutureTask包装了一个Callable
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            System.out.println("FutureTask开始执行...");
            System.out.println("（模拟耗时2秒的计算）");

            // 模拟耗时操作
            TimeUnit.SECONDS.sleep(2);

            return "FutureTask执行完成";
        });

        // =================================================================
        // 手动启动FutureTask
        // =================================================================
        // 与executor.submit()不同，这里手动创建线程
        // 这样可以控制任务何时开始
        Thread thread = new Thread(futureTask, "FutureTask-Thread");
        thread.start();

        // =================================================================
        // 主线程可以做其他事情
        // =================================================================
        System.out.println("主线程继续执行...");
        TimeUnit.MILLISECONDS.sleep(500);

        // =================================================================
        // 检查任务状态
        // =================================================================
        // isDone()：任务是否完成（正常完成、异常、取消）
        if (futureTask.isDone()) {
            System.out.println("任务已完成");
        } else {
            System.out.println("任务仍在进行中...");
        }

        // =================================================================
        // 获取结果
        // =================================================================
        // 阻塞直到结果可用
        String result = futureTask.get();
        System.out.println("FutureTask结果: " + result);
    }

    /**
     * =============================================================================
     * 测试异常处理
     * =============================================================================
     *
     * 【任务抛出异常的情况】
     *
     * 如果Callable的call()方法抛出异常：
     * 1. 异常会被捕获
     * 2. 存储在Future中
     * 3. 调用get()时会抛出ExecutionException
     *
     * 【ExecutionException】
     *
     * ExecutionException包装了原始异常：
     * - getCause()：获取原始异常
     * - getMessage()：获取异常消息
     *
     * 【异常处理建议】
     *
     * try {
     *     future.get();
     * } catch (ExecutionException e) {
     *     Throwable cause = e.getCause();
     *     if (cause instanceof MyException) {
     *         // 处理特定异常
     *     } else {
     *         // 处理其他异常
     *     }
     * } catch (InterruptedException e) {
     *     // 处理中断
     *     Thread.currentThread().interrupt();
     * }
     *
     * 【本例模拟的异常】
     *
     * 根据当前时间戳的奇偶性决定是否抛出异常：
     * - 偶数：抛出RuntimeException
     * - 奇数：正常返回
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testExceptionHandling() throws Exception {
        System.out.println("\n--- 测试Future异常处理 ---");
        System.out.println("特点：捕获并处理任务执行中的异常");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // =================================================================
        // 提交可能抛出异常的任务
        // =================================================================
        Future<String> future = executor.submit(() -> {
            System.out.println("任务开始执行...");
            TimeUnit.SECONDS.sleep(1);

            // 模拟随机异常
            // 根据当前时间戳决定是否抛出异常
            if (System.currentTimeMillis() % 2 == 0) {
                throw new RuntimeException("模拟任务执行异常！");
            }

            return "任务正常完成";
        });

        try {
            // =================================================================
            // 获取结果
            // =================================================================
            // 如果任务正常完成，返回结果
            // 如果任务抛出异常，get()会抛出ExecutionException
            String result = future.get();
            System.out.println("任务结果: " + result);

        } catch (ExecutionException e) {
            // =================================================================
            // 处理任务执行中的异常
            // =================================================================
            // ExecutionException包装了原始异常
            System.out.println("捕获到执行异常: " + e.getCause().getMessage());
            System.out.println("异常类型: " + e.getCause().getClass().getSimpleName());
        }

        executor.shutdown();
    }

    // =============================================================================
    // 计算任务 - 模拟耗时计算
    // =============================================================================
    /**
     * CalculationTask - 计算斐波那契数列的Callable任务
     *
     * 【斐波那契数列】
     *
     * F(0) = 0
     * F(1) = 1
     * F(n) = F(n-1) + F(n-2) (n >= 2)
     *
     * 例如：0, 1, 1, 2, 3, 5, 8, 13, 21, 34, ...
     *
     * 【为什么用递归？】
     *
     * 递归版本更直观，但效率较低（指数级时间复杂度）。
     * 实际生产中应该使用迭代版本。
     * 这里使用递归是为了模拟"耗时计算"。
     *
     * 【Callable vs Runnable】
     *
     * Runnable:
     * - void run()
     * - 不能返回结果
     * - 不能抛出受检异常
     *
     * Callable:
     * - V call() throws Exception
     * - 可以返回结果
     * - 可以抛出异常
     *
     * 【泛型类型】
     *
     * Callable<Integer>表示call()方法返回Integer类型。
     * 这与Future<Integer>配合使用。
     */
    static class CalculationTask implements Callable<Integer> {
        private final int number;

        public CalculationTask(int number) {
            this.number = number;
        }

        /**
         * 执行计算
         *
         * @return 斐波那契数列第number项的值
         * @throws Exception 如果计算过程中出错
         */
        @Override
        public Integer call() throws Exception {
            System.out.println(Thread.currentThread().getName()
                + " 开始计算斐波那契数列第" + number + "项");

            // 模拟耗时计算（递归方式）
            int result = fibonacci(number);

            System.out.println(Thread.currentThread().getName()
                + " 计算完成，结果: " + result);
            return result;
        }

        /**
         * 递归计算斐波那契数列
         *
         * 【时间复杂度】
         *
         * 这是指数级时间复杂度！
         * F(n)需要计算F(n-1)和F(n-2)
         * F(n-1)又需要计算F(n-2)和F(n-3)
         * ...以此类推
         *
         * 实际上会重复计算很多子问题。
         *
         * 【优化方案】
         *
         * 1. 迭代版本（推荐）：
         *    int fibonacci(int n) {
         *        if (n <= 1) return n;
         *        int a = 0, b = 1;
         *        for (int i = 2; i <= n; i++) {
         *            int temp = a + b;
         *            a = b;
         *            b = temp;
         *        }
         *        return b;
         *    }
         *
         * 2. 记忆化递归：
         *    使用数组缓存已计算的结果
         *
         * 3. 矩阵快速幂：
         *    用于非常大的n
         *
         * @param n 斐波那契数列的索引
         * @return 斐波那契数列第n项的值
         * @throws InterruptedException 如果线程被中断
         */
        private int fibonacci(int n) throws InterruptedException {
            // 基础情况
            if (n <= 1) {
                return n;
            }

            // 模拟计算时间（增加可观察性）
            TimeUnit.MILLISECONDS.sleep(300);

            // 递归计算
            return fibonacci(n-1) + fibonacci(n-2);
        }
    }

    // =============================================================================
    // 网络请求任务 - 模拟网络IO
    // =============================================================================
    /**
     * NetworkTask - 模拟网络请求的Callable任务
     *
     * 【实际应用】
     *
     * 在真实场景中，这个任务可能是：
     * - 调用REST API
     * - 查询数据库
     * - 访问远程服务
     *
     * 【为什么要用Future？】
     *
     * 网络请求的特点：
     * - 等待时间长（可能是几秒）
     * - 主线程不应该阻塞
     * - 可以并行发起多个请求
     *
     * Future的优势：
     * - 发起请求后立即返回
     * - 稍后获取结果
     * - 提高系统响应性
     *
     * 【模拟延迟】
     *
     * 使用TimeUnit.MILLISECONDS.sleep()模拟网络延迟。
     * 实际开发中，这个延迟是真实的网络请求时间。
     */
    static class NetworkTask implements Callable<String> {
        private final String url;
        private final int timeout;

        public NetworkTask(String url, int timeout) {
            this.url = url;
            this.timeout = timeout;
        }

        @Override
        public String call() throws Exception {
            System.out.println(Thread.currentThread().getName()
                + " 开始请求: " + url);

            // 模拟网络请求时间
            TimeUnit.MILLISECONDS.sleep(timeout);

            // 模拟响应数据
            return "{\"status\": 200, \"data\": \"响应数据 from " + url + "\"}";
        }
    }

    // =============================================================================
    // 文件处理任务 - 模拟文件IO
    // =============================================================================
    /**
     * FileProcessTask - 模拟文件处理的Callable任务
     *
     * 【文件IO的特点】
     *
     * - 速度慢（相对于内存）
     * - 可能阻塞
     * - 需要关闭资源
     *
     * 【与Future的配合】
     *
     * 1. 提交任务后立即返回
     * 2. 文件处理在后台进行
     * 3. 完成后通过Future获取结果
     *
     * 【实际应用】
     *
     * - 读取大文件
     * - 解析日志
     * - 导出数据
     * - 处理CSV/Excel
     *
     * 【返回值设计】
     *
     * 返回处理的行数是常见做法：
     * - 告诉调用者处理了多少数据
     * - 可以用于进度监控
     * - 可以用于统计
     */
    static class FileProcessTask implements Callable<Integer> {
        private final String fileName;

        public FileProcessTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println(Thread.currentThread().getName()
                + " 开始处理文件: " + fileName);

            // 模拟文件处理时间
            TimeUnit.MILLISECONDS.sleep(800);

            // 返回处理的行数（模拟）
            int lineCount = (int) (Math.random() * 100) + 50;
            return lineCount;
        }
    }

    // =============================================================================
    // 扩展知识点
    // =============================================================================
    /**
     * 【Future使用场景总结】
     *
     * 适合使用Future的场景：
     * 1. 耗时计算（复杂数学计算、加密解密）
     * 2. 网络请求（REST API、数据库查询）
     * 3. 文件IO（读取大文件、处理日志）
     * 4. 批量处理（可以并行处理多个任务）
     *
     * 不适合使用Future的场景：
     * 1. 任务之间有依赖（后续任务需要前一个任务的结果）
     *    - 这种情况应该使用CompletableFuture
     * 2. 需要回调通知（任务完成后自动触发后续操作）
     *    - Future只能通过get()阻塞获取结果
     * 3. 任务需要组合（多个任务的结果合并）
     *    - Future需要手动等待所有任务完成
     *
     * =================================================================
     *
     * 【ExecutorService的submit方法】
     *
     * ExecutorService提供了多个submit重载：
     *
     * submit(Callable<T>)
     * - 返回Future<T>
     * - 可以返回结果或抛出异常
     *
     * submit(Runnable)
     * - 返回Future<?>
     * - Runnable没有返回值
     * - 但Future仍然可以用于检查状态
     *
     * submit(Runnable, result)
     * - 返回Future<T>
     * - 当Runnable完成时，Future返回指定的result
     *
     * =================================================================
     *
     * 【Future的局限性】
     *
     * Future虽然强大，但有一些局限性：
     *
     * 1. 不能组合：
     *    // 无法方便地实现这个：
     *    // 先执行A和B，然后C需要A和B的结果
     *
     * 2. 不能手动完成：
     *    // 无法实现这个场景：
     *    // 任务进行中，但数据已经准备好，提前通知Future
     *
     * 3. 没有回调：
     *    // 只能通过get()阻塞获取结果
     *    // 无法注册任务完成后的回调函数
     *
     * 解决方案：使用CompletableFuture
     *
     * =================================================================
     *
     * 【线程池选择建议】
     *
     * 1. CPU密集型任务：
     *    - 使用固定大小线程池
     *    - 线程数 = CPU核心数
     *    - 避免创建过多线程（浪费资源）
     *
     * 2. IO密集型任务：
     *    - 可以使用更大的线程数
     *    - 线程数 = CPU核心数 * 2（经验值）
     *    - 因为线程大部分时间在等待IO
     *
     * 3. 混合型任务：
     *    - 根据任务特性调整
     *    - 可能需要使用有界队列和拒绝策略
     *
     * 4. 单线程需求：
     *    - 使用SingleThreadExecutor
     *    - 保证任务按顺序执行
     *
     * =================================================================
     *
     * 【最佳实践】
     *
     * 1. 总是设置超时：
     *    future.get(timeout, TimeUnit.SECONDS);
     *    避免无限等待
     *
     * 2. 正确处理异常：
     *    try {
     *        future.get();
     *    } catch (ExecutionException e) {
     *        // 处理任务异常
     *    } catch (InterruptedException e) {
     *        // 处理中断
     *        Thread.currentThread().interrupt();
     *    }
     *
     * 3. 记得关闭线程池：
     *    executor.shutdown();
     *    避免资源泄漏
     *
     * 4. 避免在任务内部创建线程池：
     *    会增加复杂度，可能导致资源问题
     *
     * 5. 使用有界队列：
     *    防止无限积压任务
     *    new ThreadPoolExecutor(..., new LinkedBlockingQueue<>(100));
     *
     * =================================================================
     *
     * 【思考题答案】
     *
     * 1. 为什么Future.get()会抛出InterruptedException？
     *    - 表示当前线程被中断
     *    - 应该立即返回，并重置中断状态
     *    - Thread.currentThread().interrupt();
     *
     * 2. 如何取消一个正在执行的任务？
     *    - future.cancel(true)
     *    - 注意：interrupt()不一定会成功
     *    - 任务必须响应中断才能被取消
     *
     * 3. Callable和Runnable可以相互转换吗？
     *    - Runnable转Callable：需要包装
     *      Callable<T> callable = () -> {
     *          runnable.run();
     *          return null;  // 或其他默认值
     *      };
     *
     *    - Callable转Runnable：使用FutureTask
     *      FutureTask task = new FutureTask<>(callable);
     *      // FutureTask实现了Runnable接口
     *
     * 4. 多个Future如何等待全部完成？
     *    - 轮询所有Future的isDone()
     *    - 使用CountDownLatch
     *    - 使用CompletableFuture.allOf()
     */
}
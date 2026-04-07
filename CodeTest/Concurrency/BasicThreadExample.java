import java.util.concurrent.TimeUnit;

/**
 * =================================================================
 * 基础线程示例 - 展示并发基本概念
 * =================================================================
 *
 * 本示例演示了Java并发编程中最基础的概念：如何创建和启动线程。
 *
 * 【并发 vs 并行】
 * - 并发（Concurrency）：多个任务交替执行，在某个时间点只有一个任务在运行，
 *   但多个任务可以在短时间内快速切换，给人同时执行的感觉
 * - 并行（Parallelism）：多个任务真正同时执行，需要多核CPU支持
 *
 * 【线程的基本概念】
 * - 线程是程序执行的最小单位，一个进程可以包含多个线程
 * - 线程共享进程的堆内存，但有独立的栈内存
 * - 主线程是程序的入口，当主线程结束时，整个进程可能还在运行（如果有其他线程）
 *
 * 【创建线程的三种方式】
 * 1. 继承Thread类：简单直接，但无法继承其他类
 * 2. 实现Runnable接口：更灵活，可以继承其他类（推荐方式）
 * 3. 使用Lambda表达式：Java 8+的简洁写法，适合简单的线程任务
 *
 * =================================================================
 */
public class BasicThreadExample {

    /**
     * =================================================================
     * main方法 - 程序入口
     * =================================================================
     *
     * 主线程是Java程序的默认线程，JVM会自动创建并启动它。
     * 所有子线程都将在这里被创建和启动。
     *
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static void main(String[] args) throws InterruptedException {
        // 打印主线程信息，帮助理解线程的基本概念
        System.out.println("=== 基础线程示例 ===");
        System.out.println("主线程开始执行，线程ID: " + Thread.currentThread().getId());
        System.out.println("主线程名称: " + Thread.currentThread().getName());
        System.out.println("主线程优先级: " + Thread.currentThread().getPriority());
        System.out.println();

        // ===================================================
        // 方式1：继承Thread类创建线程
        // ===================================================
        // 步骤：
        // 1. 创建一个类继承Thread类
        // 2. 重写run()方法，将要执行的代码放在run()中
        // 3. 创建Thread子类实例，调用start()方法启动线程
        //
        // 优点：简单直接
        // 缺点：已经继承了Thread类，无法继承其他类
        // ===================================================
        Thread thread1 = new MyThread("线程A");

        // ===================================================
        // 方式2：实现Runnable接口创建线程
        // ===================================================
        // 步骤：
        // 1. 创建一个类实现Runnable接口
        // 2. 实现run()方法
        // 3. 创建Runnable实现类实例，作为Thread构造函数的参数
        // 4. 调用Thread的start()方法启动线程
        //
        // 优点：可以继承其他类，更灵活（推荐使用）
        // 缺点：代码相对复杂一点
        // ===================================================
        Thread thread2 = new Thread(new MyRunnable(), "线程B");

        // ===================================================
        // 方式3：使用Lambda表达式创建线程（Java 8+）
        // ===================================================
        // 这是创建线程最简洁的方式，特别适合简单的线程任务。
        // Lambda表达式本质上就是Runnable接口的简洁实现。
        //
        // 语法解析：
        // () -> { ... }  就是Runnable接口的run()方法的实现
        // () 表示参数列表为空（run方法没有参数）
        // -> 表示箭头，分隔参数和方法体
        // { ... } 包含要执行的代码
        // ===================================================
        Thread thread3 = new Thread(() -> {
            // 循环执行5次任务
            for (int i = 0; i < 5; i++) {
                // Thread.currentThread().getName() 获取当前执行线程的名称
                System.out.println(Thread.currentThread().getName()
                    + " - Lambda线程执行第" + (i+1) + "次");

                // ===========================================
                // TimeUnit.MILLISECONDS.sleep() - 线程休眠
                // ===========================================
                // 作用：让当前线程暂停执行指定的时间
                // 原理：线程会进入TIMED_WAITING状态，时间到了后变为RUNNABLE状态
                // 注意事项：
                // - 会释放CPU资源，但不会释放锁（如有）
                // - 可以被interrupt()方法中断，会抛出InterruptedException
                // - 这里捕获异常后调用Thread.currentThread().interrupt()
                //   是为了恢复中断状态，让调用者知道线程被中断了
                // ===========================================
                try {
                    TimeUnit.MILLISECONDS.sleep(300);  // 休眠300毫秒
                } catch (InterruptedException e) {
                    // 当线程在sleep期间被中断时恢复中断状态
                    Thread.currentThread().interrupt();
                }
            }
        }, "线程C");

        // ===================================================
        // 启动线程 - start() vs run()
        // ===================================================
        // start()：启动新线程，线程会变为RUNNABLE状态，等待CPU调度执行
        //          这是正确启动线程的方式
        // run()：直接调用run()方法，不会有新线程创建，只会在当前线程执行
        //
        // 【重要】一个线程只能start()一次，多次调用会抛出IllegalThreadStateException
        // ===================================================
        System.out.println("准备启动三个子线程...");
        thread1.start();  // 启动线程1
        thread2.start();  // 启动线程2
        thread3.start();  // 启动线程3

        System.out.println("主线程已启动三个子线程，继续执行自己的任务...");
        System.out.println();

        // ===================================================
        // 主线程执行其他任务
        // ===================================================
        // 重点：主线程和子线程是并行执行的！
        // 这里主线程循环3次，每次休眠200毫秒
        // 与此同时，子线程也在执行（虽然它们的sleep时间不同）
        // ===================================================
        for (int i = 0; i < 3; i++) {
            System.out.println("主线程执行其他任务第" + (i+1) + "次");
            TimeUnit.MILLISECONDS.sleep(200);  // 主线程休眠200毫秒
        }
        System.out.println();

        // ===================================================
        // join() - 等待线程结束
        // ===================================================
        // 作用：调用join()的线程会等待被调用join()的线程执行完毕
        // 原理：
        // - 调用线程（主线程）会进入WAITING或TIMED_WAITING状态
        // - 直到被等待的线程执行完毕或超时
        //
        // 使用场景：
        // - 当需要确保某个线程完成后再继续执行时使用
        // - 例如：等待所有子线程完成后再汇总结果
        //
        // 注意：join()可以被interrupt()中断
        // ===================================================
        System.out.println("主线程等待所有子线程执行完毕...");
        thread1.join();   // 主线程等待线程1完成
        thread2.join();   // 主线程等待线程2完成
        thread3.join();   // 主线程等待线程3完成

        System.out.println();
        System.out.println("所有线程执行完毕，程序正常结束");
    }

    // =================================================================
    // 方式1：继承Thread类
    // =================================================================
    /**
     * MyThread - 通过继承Thread创建线程的示例
     *
     * 【Thread类的生命周期】
     * 1. NEW（新建）：创建了Thread对象，但还没调用start()
     * 2. RUNNABLE（就绪）：调用了start()，等待CPU调度
     * 3. RUNNING（运行）：获得了CPU时间片，正在执行run()
     * 4. BLOCKED/WAITING/TIMED_WAITING（阻塞）：
     *    - BLOCKED：等待获取锁
     *    - WAITING：无限期等待（wait(), join()等）
     *    - TIMED_WAITING：有限期等待（sleep(), wait(timeout)等）
     * 5. TERMINATED（终止）：run()方法执行完毕
     *
     * 【线程状态转换图】
     * NEW → RUNNABLE → RUNNING → (各种阻塞状态) → TERMINATED
     *                  ↓
     *            WAITING/BLOCKED (等待锁或资源)
     *                  ↓
     *              RUNNABLE
     */
    static class MyThread extends Thread {

        /**
         * 构造函数 - 为线程指定名称
         * @param name 线程名称，建议使用有意义的名称，便于调试
         */
        public MyThread(String name) {
            super(name);  // 调用Thread类的构造函数，设置线程名称
        }

        /**
         * run()方法 - 线程执行入口
         *
         * 【重要】不要直接调用run()方法！
         * - 直接调用run()会在当前线程执行，不会创建新线程
         * - 应该调用start()方法，它会创建新线程并调用run()
         *
         * run()方法的内容就是新线程要执行的任务。
         * 当run()方法执行完毕，线程就进入TERMINATED状态。
         */
        @Override
        public void run() {
            // 循环执行5次任务
            for (int i = 0; i < 5; i++) {
                // getName()获取当前线程的名称
                System.out.println(getName() + " - 继承Thread方式执行第" + (i+1) + "次");

                try {
                    // 休眠400毫秒，模拟任务执行时间
                    TimeUnit.MILLISECONDS.sleep(400);
                } catch (InterruptedException e) {
                    // 恢复中断状态
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // =================================================================
    // 方式2：实现Runnable接口
    // =================================================================
    /**
     * MyRunnable - 通过实现Runnable接口创建线程的示例
     *
     * 【为什么推荐使用Runnable？】
     * 1. Java不支持多重继承，实现接口更灵活
     * 2. Runnable可以被多个线程共享（同一个Runnable实例创建多个线程）
     * 3. 更容易与线程池、Future等配合使用
     * 4. 符合"组合优于继承"的设计原则
     *
     * 【Runnable vs Thread】
     * - Thread类本身实现了Runnable接口
     * - Thread更像是"线程对象"的包装
     * - Runnable更像是"任务"的抽象
     */
    static class MyRunnable implements Runnable {

        /**
         * run()方法 - 任务执行入口
         *
         * 与Thread的方式不同，这里的run()方法签名是接口规定的。
         * 当Thread执行start()时，会在新线程中调用这个run()方法。
         */
        @Override
        public void run() {
            // 循环执行5次任务
            for (int i = 0; i < 5; i++) {
                // Thread.currentThread() - 获取当前正在执行的线程对象
                System.out.println(Thread.currentThread().getName()
                    + " - 实现Runnable方式执行第" + (i+1) + "次");

                try {
                    // 休眠500毫秒，不同线程的休眠时间不同
                    // 这展示了线程并发执行时的时间交错
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // =================================================================
    // 扩展知识点
    // =================================================================
    /**
     * 【线程的常用方法】
     *
     * 1. start() - 启动线程
     *    - 创建新的执行线程
     *    - 调用run()方法
     *    - 只能调用一次
     *
     * 2. run() - 线程执行体
     *    - 包含线程要执行的代码
     *    - 可以直接调用，但不是启动线程的方式
     *
     * 3. sleep(long millis) - 线程休眠
     *    - 让当前线程暂停执行
     *    - 不释放锁（如果持有锁的话）
     *    - 静态方法，作用于当前正在执行的线程
     *
     * 4. join() - 等待线程结束
     *    - 等待被调用线程执行完毕
     *    - 可以设置超时时间
     *
     * 5. interrupt() - 中断线程
     *    - 设置中断标志
     *    - 可以中断sleep/wait/join等阻塞操作
     *
     * 6. yield() - 线程让步
     *    - 提示调度器当前线程愿意放弃CPU
     *    - 线程会变为RUNNABLE状态，可能马上又获得CPU
     *
     * 7. setDaemon(boolean) - 设置守护线程
     *    - 守护线程在所有用户线程结束时会被JVM强制终止
     *    - 默认false（用户线程）
     *    - 例：GC线程就是守护线程
     *
     * 8. setPriority(int) - 设置线程优先级
     *    - 优先级范围：1-10（Thread.MIN_PRIORITY到Thread.MAX_PRIORITY）
     *    - 默认值：5（Thread.NORM_PRIORITY）
     *    - 只是一个提示，JVM不保证严格按优先级调度
     *
     * =================================================================
     *
     * 【思考题】
     * 1. 如果不调用join()，主线程结束后程序会怎样？
     *    - 如果主线程结束，子线程可能还在运行
     *    - 但只要还有非守护线程在运行，JVM就不会退出
     *
     * 2. 为什么使用TimeUnit而不是Thread.sleep()？
     *    - TimeUnit是Java 5引入的工具类
     *    - API更清晰：TimeUnit.MILLISECONDS.sleep(100) 比 Thread.sleep(100) 更易读
     *    - 支持更多时间单位：DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS
     *
     * 3. 为什么捕获InterruptedException后要调用interrupt()？
     *    - 因为sleep/wait/join等方法在抛出InterruptedException时会清除中断状态
     *    - 恢复中断状态是为了让调用者知道线程被中断了
     *    - 这是处理中断的标准做法
     */
}
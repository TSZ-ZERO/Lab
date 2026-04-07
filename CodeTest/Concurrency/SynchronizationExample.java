import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * =============================================================================
 * 同步机制示例 - 锁和同步块
 * =============================================================================
 *
 * 本示例演示了如何解决线程安全问题，展示了多种同步机制：
 * 1. synchronized方法
 * 2. synchronized块
 * 3. ReentrantLock（可重入锁）
 * 4. CAS（Compare-And-Swap）原子操作
 *
 * 【为什么需要同步？】
 * 在ThreadSafetyProblem中，我们看到多个线程同时访问共享资源会导致数据不一致。
 * 同步机制通过对共享资源加锁，确保同一时刻只有一个线程能访问该资源，
 * 从而保证线程安全。
 *
 * 【同步的核心概念】
 * 1. 原子性（Atomicity）：一个操作要么完全执行，要么完全不执行
 * 2. 可见性（Visibility）：一个线程对共享变量的修改，对其他线程可见
 * 3. 有序性（Ordering）：程序执行的顺序按代码顺序进行
 *
 * 【synchronized vs Lock】
 * - synchronized：Java内置的同步机制，自动获取和释放锁
 * - Lock：显式的锁机制，需要手动获取和释放，更灵活
 *
 * =============================================================================
 */
public class SynchronizationExample {

    // =============================================================================
    // 共享资源 - 银行账户余额
    // =============================================================================
    // 与ThreadSafetyProblem一样，这个变量被多个线程共享
    // 不同的是，这次我们会用各种同步机制保护它
    // =============================================================================
    private static int accountBalance = 1000;

    // =============================================================================
    // 方式1：synchronized使用的锁对象
    // =============================================================================
    // synchronized块需要指定一个锁对象
    // 任何Object都可以作为锁对象
    // 重要：多个线程必须使用同一个锁对象才能实现同步
    // =============================================================================
    private static final Object lock = new Object();

    // =============================================================================
    // 方式2：ReentrantLock（可重入锁）
    // =============================================================================
    // ReentrantLock是java.util.concurrent.locks包提供的显式锁
    // 相比synchronized，它提供更多功能：
    // - 可以尝试获取锁（tryLock）
    // - 可以设置超时（tryLock(timeout)）
    // - 可以中断（lockInterruptibly）
    // - 可以公平锁（Fair Sync）
    //
    // 【可重入（Reentrant）是什么意思？】
    // 如果一个线程已经持有某个锁，它可以多次获取该锁而不会死锁
    // 例如：synchronized方法是可重入的
    // class A {
    //     synchronized void method1() {
    //         method2();  // 可以直接调用，因为锁是可重入的
    //     }
    //     synchronized void method2() {
    //         // ...
    //     }
    // }
    // =============================================================================
    private static final Lock reentrantLock = new ReentrantLock();

    /**
     * =============================================================================
     * main方法 - 程序入口
     * =============================================================================
     *
     * 本方法演示四种不同的同步机制，每种机制都能解决线程安全问题。
     * 运行后，你会看到四种方式都能得到正确的最终余额：1500元。
     *
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 同步机制示例 ===");
        System.out.println("本示例演示四种不同的同步机制来解决线程安全问题");
        System.out.println("预期结果：无论使用哪种机制，最终余额都应该是 1500 元");
        System.out.println();

        // 测试synchronized方法
        testSynchronizedMethod();

        // 测试synchronized块
        testSynchronizedBlock();

        // 测试ReentrantLock
        testReentrantLock();

        // 测试原子操作（CAS思想）
        testAtomicOperation();

        System.out.println();
        System.out.println("所有同步机制测试完成！");
        System.out.println("四种方式都能得到正确的余额，证明同步机制有效解决了线程安全问题。");
    }

    /**
     * =============================================================================
     * 测试 synchronized 方法
     * =============================================================================
     *
     * 【synchronized方法的原理】
     * 当一个线程调用synchronized方法时，该方法会自动获取对象的内置锁（monitor lock）。
     * 当方法返回时，会自动释放这个锁。
     *
     * 【静态synchronized方法】
     * 静态方法的锁是类级别的，锁对象是Class对象。
     * 所有该类的实例共享同一个类锁。
     *
     * 【synchronized方法的特性】
     * - 自动获取和释放锁
     * - 可重入（同一线程可多次获取）
     * - 悲观锁（假设每次都会冲突）
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testSynchronizedMethod() throws InterruptedException {
        System.out.println("\n--- 测试 synchronized 方法 ---");
        accountBalance = 1000;
        System.out.println("初始余额: " + accountBalance);
        System.out.println("预期最终余额: 1500 元 (1000 + 10*100 - 10*50)");
        System.out.println();

        // 创建10个线程，每个线程执行一次存款和一次取款
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                // 存款100元（使用synchronized方法）
                depositWithSynchronizedMethod(100);
                // 取款50元（使用synchronized方法）
                withdrawWithSynchronizedMethod(50);
            }, "线程-" + (i+1));
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("最终余额: " + accountBalance + " (正确)");
    }

    /**
     * =============================================================================
     * 测试 synchronized 块
     * =============================================================================
     *
     * 【synchronized块 vs synchronized方法】
     * - synchronized块可以更精确地控制同步范围
     * - 可以只同步必要的代码，减少锁的持有时间
     * - 性能通常比synchronized方法更好
     *
     * 【synchronized块的语法】
     * synchronized (lockObject) {
     *     // 需要同步的代码
     * }
     *
     * 【选择锁对象的原则】
     * 1. 多个线程必须使用同一个锁对象
     * 2. 锁对象应该是不可变的
     * 3. 尽量使用私有对象作为锁（避免外部代码也使用它）
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testSynchronizedBlock() throws InterruptedException {
        System.out.println("\n--- 测试 synchronized 块 ---");
        accountBalance = 1000;
        System.out.println("初始余额: " + accountBalance);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                depositWithSynchronizedBlock(100);
                withdrawWithSynchronizedBlock(50);
            }, "线程-" + (i+1));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("最终余额: " + accountBalance + " (正确)");
    }

    /**
     * =============================================================================
     * 测试 ReentrantLock
     * =============================================================================
     *
     * 【ReentrantLock的特性】
     * 1. 需要手动获取和释放锁（必须在finally中释放）
     * 2. 支持tryLock()：尝试获取锁，不会阻塞
     * 3. 支持lockInterruptibly()：可中断的获取锁
     * 4. 支持公平锁：按等待时间顺序获取锁
     *
     * 【ReentrantLock vs synchronized】
     * 优点：
     * - 更灵活，可以tryLock
     * - 可以设置超时
     * - 可以中断
     * - 可以有多个条件变量（Condition）
     *
     * 缺点：
     * - 需要手动释放锁（忘记释放会导致死锁）
     * - 代码更复杂
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testReentrantLock() throws InterruptedException {
        System.out.println("\n--- 测试 ReentrantLock ---");
        accountBalance = 1000;
        System.out.println("初始余额: " + accountBalance);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                depositWithReentrantLock(100);
                withdrawWithReentrantLock(50);
            }, "线程-" + (i+1));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("最终余额: " + accountBalance + " (正确)");
    }

    /**
     * =============================================================================
     * 测试原子操作（CAS思想）
     * =============================================================================
     *
     * 【CAS（Compare-And-Swap）】
     * CAS是一种无锁算法，它包含三个操作数：
     * - 内存位置（V）
     * - 期望原值（A）
     * - 新值（B）
     *
     * CAS执行过程：
     * 当V的值等于A时，才将V的值更新为B；否则什么都不做。
     * 这个过程是原子的。
     *
     * 【CAS的优点】
     * - 不需要加锁，没有死锁风险
     * - 没有线程阻塞和唤醒的开销
     * - 性能通常更好
     *
     * 【CAS的缺点】
     * - 如果冲突频繁，会导致大量重试
     * - 只能保证单个变量的原子性
     * - 有ABA问题（需要用版本号解决）
     *
     * 【Java中的CAS】
     * java.util.concurrent.atomic包提供了基于CAS的原子类：
     * - AtomicInteger
     * - AtomicLong
     * - AtomicReference
     * - 等等...
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testAtomicOperation() throws InterruptedException {
        System.out.println("\n--- 测试原子操作思想 ---");
        accountBalance = 1000;
        System.out.println("初始余额: " + accountBalance);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                atomicDeposit(100);
                atomicWithdraw(50);
            }, "线程-" + (i+1));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("最终余额: " + accountBalance + " (正确)");
    }

    // =============================================================================
    // synchronized方法实现
    // =============================================================================
    /**
     * 使用synchronized方法的存款操作
     *
     * 【原理】
     * 方法的synchronized修饰符会让线程在调用此方法时自动获取this对象的内置锁。
     * 对于静态方法，锁对象是类本身（Class对象）。
     *
     * 【为什么能解决线程安全问题？】
     * - 当线程A进入depositWithSynchronizedMethod时，获取了锁
     * - 线程B尝试调用同一个方法，发现锁被占用，只能等待
     * - 线程A执行完方法后释放锁
     * - 线程B才能获取锁并进入方法
     * - 这样就保证了同一时刻只有一个线程在执行存款操作
     *
     * 【注意事项】
     * synchronized方法会锁定整个方法，如果方法中有耗时的不需要同步的操作，
     * 会降低并发性能。这也是synchronized块更灵活的原因。
     *
     * @param amount 存款金额
     */
    private static synchronized void depositWithSynchronizedMethod(int amount) {
        // =================================================================
        // 读取余额
        // =================================================================
        // 虽然这里有sleep，但由于方法被synchronized修饰，
        // 其他线程无法同时进入这个方法
        int currentBalance = accountBalance;

        try {
            TimeUnit.MILLISECONDS.sleep(10);  // 模拟业务处理
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // =================================================================
        // 更新余额
        // =================================================================
        // 在这个线程释放锁之前，其他线程无法进入任何synchronized方法
        // 因此不存在竞态条件
        accountBalance = currentBalance + amount;
    }

    /**
     * 使用synchronized方法的取款操作
     *
     * 【与存款方法共用同一把锁】
     * 两个方法都是synchronized且是静态方法，所以共用类锁（Class对象）。
     * 这意味着：当线程A在执行存款时，线程B不能执行取款（反之亦然）。
     * 这样保证了操作的串行化，从而保证线程安全。
     *
     * @param amount 取款金额
     */
    private static synchronized void withdrawWithSynchronizedMethod(int amount) {
        int currentBalance = accountBalance;

        try {
            TimeUnit.MILLISECONDS.sleep(10);  // 模拟业务处理
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 检查余额是否足够
        if (currentBalance >= amount) {
            accountBalance = currentBalance - amount;
        }
    }

    // =============================================================================
    // synchronized块实现
    // =============================================================================
    /**
     * 使用synchronized块的存款操作
     *
     * 【synchronized块的优点】
     * 1. 可以只同步必要的代码，减少锁的持有时间
     * 2. 可以选择不同的锁对象，实现更细粒度的控制
     * 3. 可以让不需要同步的代码并行执行，提高性能
     *
     * 【lock对象的选择】
     * 这里使用一个专门的lock对象，而不是this或类本身。
     * 好处是外部代码无法访问这个锁，可以避免死锁。
     *
     * @param amount 存款金额
     */
    private static void depositWithSynchronizedBlock(int amount) {
        // =================================================================
        // 进入同步块
        // =================================================================
        // synchronized块在进入时会获取lock对象的锁
        // 只有获取到锁的线程才能执行块内的代码
        synchronized (lock) {
            int currentBalance = accountBalance;

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // =================================================================
            // 在同步块内执行
            // =================================================================
            // 在线程A执行这段代码期间，线程B会被阻塞在synchronized (lock)处
            // 无法进入这个同步块
            accountBalance = currentBalance + amount;
        }  // 退出同步块，自动释放锁
    }

    /**
     * 使用synchronized块的取款操作
     *
     * 【与存款方法共用同一把锁】
     * 两个方法都使用synchronized(lock)，所以共享同一个锁。
     * 这确保了存款和取款操作不会同时执行。
     *
     * @param amount 取款金额
     */
    private static void withdrawWithSynchronizedBlock(int amount) {
        synchronized (lock) {
            int currentBalance = accountBalance;

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (currentBalance >= amount) {
                accountBalance = currentBalance - amount;
            }
        }
    }

    // =============================================================================
    // ReentrantLock实现
    // =============================================================================
    /**
     * 使用ReentrantLock的存款操作
     *
     * 【ReentrantLock的使用模式】
     * lock.lock();       // 获取锁（阻塞等待）
     * try {
     *     // 临界区代码
     * } finally {
     *     lock.unlock();  // 释放锁（必须在finally中）
     * }
     *
     * 【为什么必须在finally中释放锁？】
     * 如果临界区代码抛出异常，finally确保锁一定会被释放。
     * 否则，其他等待该锁的线程会永远等待下去（死锁）。
     *
     * @param amount 存款金额
     */
    private static void depositWithReentrantLock(int amount) {
        // =================================================================
        // 获取锁
        // =================================================================
        // lock()方法会阻塞，直到获取到锁
        // 与synchronized不同的是，这是显式的获取锁
        reentrantLock.lock();

        try {
            // =================================================================
            // 临界区
            // =================================================================
            // 在这个区域内，同一时刻只有一个线程能执行
            int currentBalance = accountBalance;

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            accountBalance = currentBalance + amount;
        } finally {
            // =================================================================
            // 释放锁
            // =================================================================
            // 无论临界区是否正常完成，都必须释放锁
            // 这是ReentrantLock使用中最重要的规则
            reentrantLock.unlock();
        }
    }

    /**
     * 使用ReentrantLock的取款操作
     *
     * 【可重入示例】
     * 如果一个线程已经获取了reentrantLock锁，它可以再次获取该锁而不会死锁。
     * 这是"可重入"的含义：锁的持有计数会+1。
     * 每次unlock()调用会使计数-1，只有计数归零时，锁才会真正释放给其他线程。
     *
     * @param amount 取款金额
     */
    private static void withdrawWithReentrantLock(int amount) {
        reentrantLock.lock();
        try {
            int currentBalance = accountBalance;

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (currentBalance >= amount) {
                accountBalance = currentBalance - amount;
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    // =============================================================================
    // CAS（Compare-And-Swap）实现
    // =============================================================================
    /**
     * 使用CAS思想的存款操作
     *
     * 【CAS的工作原理】
     * 1. 读取当前值：currentBalance = accountBalance
     * 2. 尝试更新：如果 accountBalance == currentBalance，则更新
     * 3. 如果更新失败（因为其他线程已经修改了accountBalance），重试
     *
     * 【关键点：compareAndSet方法】
     * 这是一个模拟方法。真实的CAS是由CPU提供的原子指令。
     * 在Java中，可以使用sun.misc.Unsafe或java.util.concurrent.atomic包。
     *
     * 【ABA问题】
     * CAS的一个著名问题是ABA问题：
     * - 线程A读取值V
     * - 线程B修改V为W，然后又改回V
     * - 线程A的CAS操作成功，因为它看到的还是V
     *
     * 解决方案：使用版本号（AtomicStampedReference）
     *
     * @param amount 存款金额
     */
    private static void atomicDeposit(int amount) {
        // =================================================================
        // CAS循环
        // =================================================================
        // 使用无限循环配合CAS操作，直到成功为止
        // 这是典型的乐观锁模式：假设冲突不常发生
        while (true) {
            // 读取当前余额
            int currentBalance = accountBalance;

            try {
                TimeUnit.MILLISECONDS.sleep(10);  // 模拟业务处理
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // =================================================================
            // 尝试原子更新
            // =================================================================
            // compareAndSet是原子操作：
            // - 检查accountBalance是否等于currentBalance
            // - 如果等于，说明没有被其他线程修改，执行更新
            // - 如果不等，说明被其他线程修改了，放弃更新，重试
            if (compareAndSet(currentBalance, currentBalance + amount)) {
                break;  // 更新成功，退出循环
            }
            // 更新失败，说明有竞争，重试
        }
    }

    /**
     * 使用CAS思想的取款操作
     *
     * 【与存款相同的CAS模式】
     * 取款操作也是通过CAS循环实现：
     * 1. 读取当前余额
     * 2. 检查是否足够
     * 3. CAS尝试更新
     * 4. 如果失败，重试
     *
     * @param amount 取款金额
     */
    private static void atomicWithdraw(int amount) {
        while (true) {
            int currentBalance = accountBalance;

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (currentBalance >= amount) {
                // 尝试CAS更新
                if (compareAndSet(currentBalance, currentBalance - amount)) {
                    break;
                }
                // 更新失败，重试
            } else {
                // 余额不足，直接退出
                break;
            }
        }
    }

    /**
     * =============================================================================
     * compareAndSet方法 - CAS的核心
     * =============================================================================
     *
     * 这是一个简化的CAS实现，用于演示CAS的工作原理。
     * 注意：这个方法本身不是原子的，但在我们的模拟中，
     * 我们用synchronized来保证它的原子性。
     *
     * 【真实的CAS】
     * 在真实的Java代码中，应该使用：
     * - java.util.concurrent.atomic.AtomicInteger.compareAndSet()
     * - java.util.concurrent.atomic.AtomicReference.compareAndSet()
     *
     * 这些类内部使用Unsafe类的CAS操作，直接调用CPU的原子指令。
     *
     * 【为什么需要synchronized？】
     * 因为accountBalance是普通变量，不是原子变量。
     * 为了模拟CAS的检查-更新过程，我们需要保证这个过程的原子性。
     * 在真实场景中，accountBalance应该是AtomicInteger类型。
     *
     * @param expect 期望的当前值
     * @param update 要更新的新值
     * @return true如果更新成功，false如果当前值不等于expect
     */
    private static synchronized boolean compareAndSet(int expect, int update) {
        // =================================================================
        // 检查当前值是否等于期望值
        // =================================================================
        // 这是CAS的核心：只有在值没有被改变的情况下才更新
        if (accountBalance == expect) {
            // 值没有被改变，执行更新
            accountBalance = update;
            return true;
        } else {
            // 值已经被其他线程改变，放弃更新
            return false;
        }
    }

    // =============================================================================
    // 扩展知识点
    // =============================================================================
    /**
     * 【synchronized的底层原理】
     *
     * synchronized之所以能实现同步，是因为它依赖于JVM的对象头中的Mark Word。
     * Mark Word记录了对象的锁信息：
     * - 无锁状态：对象的哈希码、年龄等信息
     * - 偏向锁：记录持有锁的线程ID
     * - 轻量级锁：栈帧中的Lock Record指针
     * - 重量级锁：Monitor对象指针
     *
     * 【锁的升级过程】
     * JDK 6引入了锁升级的概念（也叫锁膨胀）：
     * 无锁 → 偏向锁 → 轻量级锁 → 重量级锁
     *
     * 偏向锁：第一次被某个线程获取时，记录线程ID，之后该线程进入不需要任何同步。
     * 轻量级锁：在有多个线程竞争时，将锁标记移到栈帧中，使用CAS操作。
     * 重量级锁：竞争激烈时，升级为重量级锁，使用操作系统的Mutex。
     *
     * =================================================================
     *
     * 【Lock接口的主要方法】
     *
     * void lock() - 获取锁，如果锁不可用则等待
     * void lockInterruptibly() - 可中断的获取锁
     * boolean tryLock() - 尝试获取锁，立即返回
     * boolean tryLock(long time, TimeUnit unit) - 尝试获取锁，带超时
     * void unlock() - 释放锁
     * Condition newCondition() - 创建新的条件变量
     *
     * =================================================================
     *
     * 【ReentrantLock的公平锁 vs 非公平锁】
     *
     * 公平锁：按等待时间顺序获取锁（FIFO）
     * - new ReentrantLock(true) 创建公平锁
     * - 优点：不会饥饿
     * - 缺点：性能稍差
     *
     * 非公平锁：不保证顺序
     * - new ReentrantLock(false) 或 new ReentrantLock() 创建非公平锁
     * - 优点：性能更好
     * - 缺点：可能导致线程饥饿
     *
     * 注意：synchronized是非公平锁
     *
     * =================================================================
     *
     * 【Condition接口】
     *
     * Condition类似于Object的wait/notify/notifyAll，
     * 但功能更强大：
     * - 可以创建多个条件变量
     * - 可以精确控制等待的线程
     *
     * 使用示例：
     * Lock lock = new ReentrantLock();
     * Condition condition = lock.newCondition();
     *
     * // 等待
     * lock.lock();
     * try {
     *     while (!condition) {
     *         condition.await();
     *     }
     * } finally {
     *     lock.unlock();
     * }
     *
     * // 通知
     * lock.lock();
     * try {
     *     condition.signal();  // 通知一个等待的线程
     *     condition.signalAll();  // 通知所有等待的线程
     * } finally {
     *     lock.unlock();
     * }
     *
     * =================================================================
     *
     * 【原子变量类（java.util.concurrent.atomic）】
     *
     * 常用原子类：
     * - AtomicInteger：原子整数
     * - AtomicLong：原子长整数
     * - AtomicBoolean：原子布尔值
     * - AtomicReference<V>：原子引用
     * - AtomicIntegerArray：原子整数数组
     * - AtomicStampedReference：带版本号的原子引用（解决ABA问题）
     *
     * 常用方法：
     * - get()：获取当前值
     * - set(newValue)：设置新值
     * - getAndSet(newValue)：原子地设置新值并返回旧值
     * - compareAndSet(expect, update)：CAS操作
     * - incrementAndGet()：原子+1并返回
     * - decrementAndGet()：原子-1并返回
     *
     * =================================================================
     *
     * 【思考题】
     *
     * 1. synchronized和ReentrantLock哪个性能更好？
     *    - 在JDK 6之前，synchronized性能较差
     *    - JDK 6进行了大量优化，包括锁升级机制
     *    - 现在两者性能差异不大，大部分场景可以互换
     *    - 通常建议优先使用synchronized，因为更简单
     *
     * 2. 什么是死锁？如何避免？
     *    - 死锁：多个线程相互等待对方持有的锁
     *    - 避免方法：
     *      a. 按固定顺序获取锁
     *      b. 设置锁超时（tryLock）
     *      c. 使用重入锁（ReentrantLock）
     *      d. 减少锁的使用范围
     *
     * 3. 为什么CAS比锁更高效？
     *    - 锁：线程获取锁失败后会被阻塞，涉及上下文切换
     *    - CAS：失败后重试，不涉及线程阻塞和唤醒
     *    - 只有在竞争不激烈时，CAS才比锁高效
     *    - 竞争激烈时，CAS会导致大量重试，反而性能下降
     *
     * 4. 什么是ABA问题？如何解决？
     *    - ABA问题：线程A读取V，线程B修改V为W又改回V，线程A的CAS成功
     *    - 解决方法：使用版本号（AtomicStampedReference）
     *    - 每次修改不仅修改值，还修改版本号
     *    - CAS检查时不仅比较值，还比较版本号
     */
}
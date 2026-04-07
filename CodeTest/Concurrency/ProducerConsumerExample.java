import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * 生产者消费者模式示例
 * =============================================================================
 *
 * 本示例演示了经典的生产者消费者模式的多种实现方式：
 * 1. wait/notify：最基础的实现方式
 * 2. BlockingQueue：Java并发包提供的高性能实现
 * 3. Semaphore：信号量实现的缓冲区
 *
 * 【什么是生产者消费者模式？】
 *
 * 生产者消费者模式是一种常见的并发设计模式，它的核心思想是：
 * - 生产者（Producer）：负责生产数据，将数据放入共享缓冲区
 * - 消费者（Consumer）：负责取出数据，进行处理
 * - 缓冲区（Buffer）：存放数据的队列，负责协调生产和消费的速度
 *
 * 【为什么需要这个模式？】
 *
 * 1. 解耦：生产者和消费者不需要知道彼此的存在
 *    - 生产者只管生产，不知道谁在消费
 *    - 消费者只管消费，不知道谁在生产
 *    - 两者通过缓冲区通信
 *
 * 2. 速率匹配：平衡生产者和消费者的处理速度
 *    - 生产速度快时，数据在缓冲区积累
 *    - 消费速度快时，缓冲区提供数据
 *    - 避免相互等待
 *
 * 3. 峰值处理：应对突发流量
 *    - 即使生产者短时间内生产大量数据
 *    - 消费者可以慢慢处理，不会丢失数据
 *
 * 【本示例的核心场景】
 *
 * 我们模拟一个消息队列系统：
 * - 生产者不断生产消息
 * - 消费者不断消费消息
 * - 队列容量有限（5个位置）
 * - 当队列满时，生产者等待
 * - 当队列空时，消费者等待
 *
 * =============================================================================
 */
public class ProducerConsumerExample {

    /**
     * =============================================================================
     * main方法 - 程序入口
     * =============================================================================
     *
     * 本方法演示了三种不同的生产者消费者实现方式。
     * 观察输出可以看到：
     * - 生产和消费的交替进行
     * - 队列状态的动态变化
     * - 不同实现方式的差异
     *
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 生产者消费者模式示例 ===");
        System.out.println("本示例演示三种不同的实现方式");
        System.out.println("观察生产和消费的交替进行");
        System.out.println();

        // 测试wait/notify实现
        testWaitNotify();

        // 测试BlockingQueue实现
        testBlockingQueue();

        // 测试Semaphore实现
        testSemaphore();

        System.out.println("\n所有生产者消费者模式测试完成！");
    }

    /**
     * =============================================================================
     * 测试 wait/notify 实现
     * =============================================================================
     *
     * 【wait/notify的原理】
     *
     * wait()：
     * - 让当前线程进入等待状态
     * - 释放持有的对象锁
     * - 线程会进入该对象的等待队列
     *
     * notify()：
     * - 从对象的等待队列中随机唤醒一个线程
     * - 线程从wait()返回，但需要重新获取锁
     *
     * notifyAll()：
     * - 唤醒对象的等待队列中的所有线程
     * - 所有线程竞争同一把锁
     *
     * 【为什么用while而不是if？】
     *
     * 使用while循环而不是if判断的原因：
     * 1. 多个消费者可能被唤醒
     * 2. 唤醒后需要重新检查条件（可能已经被其他线程消费）
     * 3. 防止"虚假唤醒"（虽然JVM规范说几乎不会发生）
     *
     * 【本实现的特点】
     * - 使用循环队列作为缓冲区
     * - putIndex和takeIndex实现队列的循环利用
     * - count记录当前元素数量
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testWaitNotify() throws InterruptedException {
        System.out.println("\n--- 使用 wait/notify 实现 ---");
        System.out.println("特点：最基础的实现方式，需要手动管理等待和唤醒");

        // 创建容量为5的消息队列
        MessageQueue queue = new MessageQueue(5);

        // 创建2个生产者
        Thread producer1 = new Thread(new WaitNotifyProducer(queue, "生产者1"), "Producer1");
        Thread producer2 = new Thread(new WaitNotifyProducer(queue, "生产者2"), "Producer2");

        // 创建3个消费者
        Thread consumer1 = new Thread(new WaitNotifyConsumer(queue, "消费者1"), "Consumer1");
        Thread consumer2 = new Thread(new WaitNotifyConsumer(queue, "消费者2"), "Consumer2");
        Thread consumer3 = new Thread(new WaitNotifyConsumer(queue, "消费者3"), "Consumer3");

        // 启动所有线程
        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
        consumer3.start();

        // 运行10秒后停止
        TimeUnit.SECONDS.sleep(10);

        // 停止所有线程
        // 通过中断标志通知线程停止
        producer1.interrupt();
        producer2.interrupt();
        consumer1.interrupt();
        consumer2.interrupt();
        consumer3.interrupt();

        // 等待所有线程结束
        producer1.join();
        producer2.join();
        consumer1.join();
        consumer2.join();
        consumer3.join();

        System.out.println("wait/notify模式测试完成");
    }

    /**
     * =============================================================================
     * 测试 BlockingQueue 实现
     * =============================================================================
     *
     * 【BlockingQueue的特性】
     *
     * BlockingQueue是Java并发包提供的接口，实现了多种阻塞操作：
     *
     * 插入操作：
     * - put(E e)：阻塞直到队列有空位
     * - offer(E e, timeout, unit)：阻塞直到超时
     * - offer(E e)：非阻塞，队列满返回false
     *
     * 移除操作：
     * - take()：阻塞直到队列有元素
     * - poll(timeout, unit)：阻塞直到超时
     * - poll()：非阻塞，队列空返回null
     *
     * 【ArrayBlockingQueue vs LinkedBlockingQueue】
     *
     * ArrayBlockingQueue：
     * - 基于数组实现
     * - 有界队列，容量固定
     * - 吞吐量较高
     *
     * LinkedBlockingQueue：
     * - 基于链表实现
     * - 可选有界或无界（默认Integer.MAX_VALUE）
     * - 吞吐量较低（但插入和删除需要节点分配）
     *
     * 【本实现的特点】
     * - 使用固定容量5的有界队列
     * - 生产者和消费者使用不同的线程池
     * - 展示了线程池与BlockingQueue的配合
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testBlockingQueue() throws InterruptedException {
        System.out.println("\n--- 使用 BlockingQueue 实现 ---");
        System.out.println("特点：Java并发包提供，使用简单，性能优秀");

        // 创建容量为5的阻塞队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        AtomicInteger messageId = new AtomicInteger(0);

        // 创建生产者线程池（3个线程）
        ExecutorService producerPool = Executors.newFixedThreadPool(3);
        // 创建消费者线程池（2个线程）
        ExecutorService consumerPool = Executors.newFixedThreadPool(2);

        // 启动3个生产者
        for (int i = 0; i < 3; i++) {
            final String producerName = "BlockingQueue生产者" + (i+1);
            producerPool.execute(() -> {
                // 使用isInterrupted()检查中断状态，而不是捕获InterruptedException
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 生成消息并放入队列
                        String message = producerName + " - 消息" + messageId.incrementAndGet();
                        queue.put(message);  // 阻塞直到队列有空位
                        System.out.println("生产: " + message + " | 队列大小: " + queue.size());

                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        // 收到中断请求，重置中断状态并退出
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // 启动2个消费者
        for (int i = 0; i < 2; i++) {
            final String consumerName = "BlockingQueue消费者" + (i+1);
            consumerPool.execute(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 从队列取出消息
                        String message = queue.take();  // 阻塞直到队列有元素
                        System.out.println("消费: " + message + " | 队列大小: " + queue.size()
                            + " - " + consumerName);

                        TimeUnit.MILLISECONDS.sleep(800);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // 运行8秒
        TimeUnit.SECONDS.sleep(8);

        // 关闭线程池
        // shutdownNow()会尝试中断正在执行的任务
        producerPool.shutdownNow();
        consumerPool.shutdownNow();

        // 等待线程池终止
        producerPool.awaitTermination(3, TimeUnit.SECONDS);
        consumerPool.awaitTermination(3, TimeUnit.SECONDS);

        System.out.println("BlockingQueue模式测试完成");
    }

    /**
     * =============================================================================
     * 测试 Semaphore 实现
     * =============================================================================
     *
     * 【Semaphore（信号量）的概念】
     *
     * Semaphore是一种计数信号量，用于控制访问资源的线程数量。
     *
     * 核心概念：
     * - permits（许可）：信号量维护一定数量的许可
     * - acquire()：获取许可，如果无可用许可则阻塞
     * - release()：释放许可，归还给信号量
     *
     * 【Semaphore的分类】
     *
     * 公平信号量：
     * - 按等待时间顺序获取许可（FIFO）
     * - new Semaphore(n, true)
     *
     * 非公平信号量：
     * - 不保证顺序，可能插队
     * - new Semaphore(n) 或 new Semaphore(n, false)
     *
     * 【本实现的原理】
     *
     * 使用三个信号量实现生产者消费者：
     * 1. emptySlots：空槽信号量，初始为容量（5）
     *    - 生产者获取（减少空槽），释放（增加满槽）
     *
     * 2. fullSlots：满槽信号量，初始为0
     *    - 消费者获取（减少满槽），释放（增加空槽）
     *
     * 3. mutex：互斥信号量，初始为1
     *    - 保证对缓冲区的互斥访问
     *    - 同一时刻只有一个线程能操作缓冲区
     *
     * 【工作流程】
     *
     * 生产者：
     * 1. 获取emptySlots许可（等待空槽）
     * 2. 获取mutex许可（进入临界区）
     * 3. 放入数据
     * 4. 释放mutex许可（离开临界区）
     * 5. 释放fullSlots许可（增加满槽）
     *
     * 消费者：
     * 1. 获取fullSlots许可（等待满槽）
     * 2. 获取mutex许可（进入临界区）
     * 3. 取出数据
     * 4. 释放mutex许可（离开临界区）
     * 5. 释放emptySlots许可（增加空槽）
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void testSemaphore() throws InterruptedException {
        System.out.println("\n--- 使用 Semaphore 实现 ---");
        System.out.println("特点：更底层的同步原语，可灵活控制资源访问");

        // 创建容量为5的缓冲区
        SemaphoreBuffer buffer = new SemaphoreBuffer(5);

        // 创建生产者线程
        Thread semaphoreProducer = new Thread(() -> {
            int messageId = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = "Semaphore消息" + (++messageId);
                    buffer.put(message);
                    System.out.println("Semaphore生产: " + message);
                    TimeUnit.MILLISECONDS.sleep(600);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "SemaphoreProducer");

        // 创建消费者线程
        Thread semaphoreConsumer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = buffer.take();
                    System.out.println("Semaphore消费: " + message);
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "SemaphoreConsumer");

        // 启动线程
        semaphoreProducer.start();
        semaphoreConsumer.start();

        // 运行6秒
        TimeUnit.SECONDS.sleep(6);

        // 停止线程
        semaphoreProducer.interrupt();
        semaphoreConsumer.interrupt();
        semaphoreProducer.join();
        semaphoreConsumer.join();

        System.out.println("Semaphore模式测试完成");
    }

    // =============================================================================
    // 使用wait/notify实现的消息队列
    // =============================================================================
    /**
     * MessageQueue - 基于wait/notify的线程安全消息队列
     *
     * 【核心设计】
     *
     * 1. 循环队列：
     *    - 使用数组存储消息
     *    - putIndex：下一个写入位置
     *    - takeIndex：下一个读取位置
     *    - 到达末尾时环绕到开头
     *
     * 2. 线程安全：
     *    - 所有公共方法使用synchronized修饰
     *    - 保证同一时刻只有一个线程操作队列
     *
     * 3. 阻塞操作：
     *    - put()：队列满时等待，直到有空位
     *    - take()：队列空时等待，直到有元素
     *
     * 【为什么用while循环判断？】
     *
     * 在wait()外面用while循环判断条件是标准做法：
     *
     * synchronized (this) {
     *     while (count == items.length) {  // 用while而不是if！
     *         wait();
     *     }
     *     // 生产数据
     *     notifyAll();
     * }
     *
     * 原因：
     * 1. 防止虚假唤醒：虽然JVM规范几乎不会出现，但规范允许
     * 2. 多消费者场景：一个notify可能唤醒不该唤醒的线程
     * 3. 重新检查条件：被唤醒后，条件可能已经改变
     */
    static class MessageQueue {
        // 存储消息的数组
        private final String[] items;
        // 当前元素数量
        private int count;
        // 下一个写入位置
        private int putIndex;
        // 下一个读取位置
        private int takeIndex;

        /**
         * 构造函数
         * @param capacity 队列容量
         */
        public MessageQueue(int capacity) {
            this.items = new String[capacity];
        }

        /**
         * 放入消息
         *
         * 【流程】
         * 1. 如果队列满，等待
         * 2. 放入消息到putIndex位置
         * 3. putIndex环绕（如果到达末尾则回到开头）
         * 4. 元素数量+1
         * 5. 通知所有等待的消费者
         *
         * @param item 要放入的消息
         * @throws InterruptedException 当线程被中断时抛出
         */
        public synchronized void put(String item) throws InterruptedException {
            // =================================================================
            // 等待直到队列有空位
            // =================================================================
            // 使用while循环防止虚假唤醒
            while (count == items.length) {
                System.out.println(Thread.currentThread().getName() + "：队列满，等待空位...");
                wait();  // 释放锁，进入等待队列
            }

            // =================================================================
            // 放入消息
            // =================================================================
            items[putIndex] = item;
            System.out.println(Thread.currentThread().getName() + "：放入消息到位置" + putIndex);

            // putIndex环绕
            if (++putIndex == items.length) {
                putIndex = 0;
            }

            // 元素数量+1
            count++;

            // =================================================================
            // 通知消费者
            // =================================================================
            // 使用notifyAll()唤醒所有等待的消费者
            // 也可以使用notify()，但要确保唤醒正确的线程
            notifyAll();
        }

        /**
         * 取出消息
         *
         * 【流程】
         * 1. 如果队列空，等待
         * 2. 从takeIndex位置取出消息
         * 3. takeIndex环绕（如果到达末尾则回到开头）
         * 4. 元素数量-1
         * 5. 通知所有等待的生产者
         *
         * @return 取出的消息
         * @throws InterruptedException 当线程被中断时抛出
         */
        public synchronized String take() throws InterruptedException {
            // =================================================================
            // 等待直到队列有元素
            // =================================================================
            while (count == 0) {
                System.out.println(Thread.currentThread().getName() + "：队列空，等待消息...");
                wait();
            }

            // =================================================================
            // 取出消息
            // =================================================================
            String item = items[takeIndex];
            System.out.println(Thread.currentThread().getName() + "：从位置" + takeIndex + "取出消息");

            // 清空位置（帮助GC）
            items[takeIndex] = null;

            // takeIndex环绕
            if (++takeIndex == items.length) {
                takeIndex = 0;
            }

            // 元素数量-1
            count--;

            // =================================================================
            // 通知生产者
            // =================================================================
            notifyAll();

            return item;
        }
    }

    // =============================================================================
    // wait/notify生产者
    // =============================================================================
    /**
     * WaitNotifyProducer - 使用wait/notify的生产者
     *
     * 【生产者的工作】
     * 1. 不断生产消息
     * 2. 调用queue.put()将消息放入队列
     * 3. 如果队列满，put()会阻塞
     * 4. 每次生产后休眠一段时间（模拟生产时间）
     */
    static class WaitNotifyProducer implements Runnable {
        private final MessageQueue queue;
        private final String name;
        private int messageId = 0;

        public WaitNotifyProducer(MessageQueue queue, String name) {
            this.queue = queue;
            this.name = name;
        }

        @Override
        public void run() {
            // 检查中断标志来决定是否继续
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 生成消息
                    String message = name + " - 消息" + (++messageId);
                    queue.put(message);
                    System.out.println("生产: " + message);

                    // 模拟生产时间
                    TimeUnit.MILLISECONDS.sleep(400);
                } catch (InterruptedException e) {
                    // 收到中断请求，重置中断状态并退出循环
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // =============================================================================
    // wait/notify消费者
    // =============================================================================
    /**
     * WaitNotifyConsumer - 使用wait/notify的消费者
     *
     * 【消费者的工作】
     * 1. 不断从队列取出消息
     * 2. 调用queue.take()获取消息
     * 3. 如果队列空，take()会阻塞
     * 4. 每次消费后休眠一段时间（模拟处理时间）
     */
    static class WaitNotifyConsumer implements Runnable {
        private final MessageQueue queue;
        private final String name;

        public WaitNotifyConsumer(MessageQueue queue, String name) {
            this.queue = queue;
            this.name = name;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 从队列取出消息
                    String message = queue.take();
                    System.out.println("消费: " + message + " - " + name);

                    // 模拟处理时间
                    TimeUnit.MILLISECONDS.sleep(600);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // =============================================================================
    // 使用Semaphore实现的缓冲区
    // =============================================================================
    /**
     * SemaphoreBuffer - 使用Semaphore实现的线程安全缓冲区
     *
     * 【设计原理】
     *
     * 使用三个信号量来协调生产者和消费者：
     *
     * 1. emptySlots（空槽信号量）
     *    - 初始值 = capacity（队列容量）
     *    - 代表可用空间数量
     *    - 生产者需要先获取此信号量
     *
     * 2. fullSlots（满槽信号量）
     *    - 初始值 = 0
     *    - 代表已有数据的数量
     *    - 消费者需要先获取此信号量
     *
     * 3. mutex（互斥信号量）
     *    - 初始值 = 1（二值信号量）
     *    - 保证对缓冲区的互斥访问
     *    - 任何时刻只有一个线程能操作缓冲区
     *
     * 【生产流程】
     *
     * 1. emptySlots.acquire()：获取空槽许可
     *    - 如果没有空槽，此操作会阻塞
     *
     * 2. mutex.acquire()：获取互斥许可
     *    - 保证操作缓冲区时的互斥
     *
     * 3. 放入数据到缓冲区
     *
     * 4. mutex.release()：释放互斥许可
     *
     * 5. fullSlots.release()：增加满槽计数
     *    - 通知消费者有数据可用
     *
     * 【消费流程】
     *
     * 1. fullSlots.acquire()：获取满槽许可
     *    - 如果没有数据，此操作会阻塞
     *
     * 2. mutex.acquire()：获取互斥许可
     *
     * 3. 从缓冲区取出数据
     *
     * 4. mutex.release()：释放互斥许可
     *
     * 5. emptySlots.release()：增加空槽计数
     *    - 通知生产者有空间可用
     */
    static class SemaphoreBuffer {
        private final String[] buffer;
        private int putIndex;
        private int takeIndex;
        private int count;

        // 空槽信号量（初始为容量）
        private final Semaphore emptySlots;
        // 满槽信号量（初始为0）
        private final Semaphore fullSlots;
        // 互斥信号量（初始为1）
        private final Semaphore mutex;

        /**
         * 构造函数
         * @param capacity 缓冲区容量
         */
        public SemaphoreBuffer(int capacity) {
            this.buffer = new String[capacity];
            this.emptySlots = new Semaphore(capacity);  // 初始空槽数量=容量
            this.fullSlots = new Semaphore(0);         // 初始满槽数量=0
            this.mutex = new Semaphore(1);             // 互斥信号量
        }

        /**
         * 放入数据
         *
         * @param item 要放入的数据
         * @throws InterruptedException 当线程被中断时抛出
         */
        public void put(String item) throws InterruptedException {
            // =================================================================
            // 获取空槽许可
            // =================================================================
            // 如果没有空槽（permit=0），此操作会阻塞
            // 这是生产者速度过快时的限流机制
            emptySlots.acquire();

            // =================================================================
            // 进入临界区
            // =================================================================
            // 获取互斥锁，保证对缓冲区的互斥访问
            mutex.acquire();

            try {
                // 放入数据
                buffer[putIndex] = item;
                System.out.println(Thread.currentThread().getName()
                    + "：Semaphore放入数据到位置" + putIndex);

                // 索引环绕
                if (++putIndex == buffer.length) {
                    putIndex = 0;
                }

                count++;
            } finally {
                // =================================================================
                // 离开临界区
                // =================================================================
                // 必须释放互斥锁
                mutex.release();

                // =================================================================
                // 增加满槽计数
                // =================================================================
                // 通知消费者有数据可用
                // 会唤醒一个等待fullSlots许可的消费者线程
                fullSlots.release();
            }
        }

        /**
         * 取出数据
         *
         * @return 取出的数据
         * @throws InterruptedException 当线程被中断时抛出
         */
        public String take() throws InterruptedException {
            // =================================================================
            // 获取满槽许可
            // =================================================================
            // 如果没有数据（permit=0），此操作会阻塞
            // 这是消费者速度过快时的限流机制
            fullSlots.acquire();

            // =================================================================
            // 进入临界区
            // =================================================================
            mutex.acquire();

            try {
                // 取出数据
                String item = buffer[takeIndex];
                System.out.println(Thread.currentThread().getName()
                    + "：Semaphore从位置" + takeIndex + "取出数据");

                // 清空位置
                buffer[takeIndex] = null;

                // 索引环绕
                if (++takeIndex == buffer.length) {
                    takeIndex = 0;
                }

                count--;

                return item;
            } finally {
                // =================================================================
                // 离开临界区
                // =================================================================
                mutex.release();

                // =================================================================
                // 增加空槽计数
                // =================================================================
                // 通知生产者有空间可用
                emptySlots.release();
            }
        }
    }

    // =============================================================================
    // 扩展知识点
    // =============================================================================
    /**
     * 【wait/notify vs BlockingQueue】
     *
     * wait/notify优点：
     * - 更底层，可以实现更灵活的控制逻辑
     * - 适合学习底层原理
     *
     * wait/notify缺点：
     * - 使用复杂，容易出错
     * - 容易死锁（忘记notifyAll、锁顺序问题等）
     * - 代码不够直观
     *
     * BlockingQueue优点：
     * - 使用简单，API清晰
     * - 线程安全，性能优秀
     * - 由JDK实现，经过充分测试
     *
     * BlockingQueue缺点：
     * - 功能固定，不够灵活
     * - 需要自己处理中断
     *
     * 建议：实际开发中优先使用BlockingQueue
     *
     * =================================================================
     *
     * 【信号量（Semaphore）的应用场景】
     *
     * 1. 限流：
     *    - 控制对资源的访问数量
     *    - 例如：限制数据库连接数、限制HTTP请求数
     *
     * 2. 资源池：
     *    - 管理有限资源
     *    - 例如：线程池、对象池
     *
     * 3. 同步：
     *    - 作为简单的同步机制
     *    - 例如：多个线程等待某个条件
     *
     * =================================================================
     *
     * 【生产者消费者模式的变体】
     *
     * 1. 多生产者多消费者：
     *    - 多个生产者线程
     *    - 多个消费者线程
     *    - 需要处理更复杂的同步问题
     *
     * 2. 优先级队列：
     *    - 消费者可以设置优先级
     *    - 高优先级消费者先获取数据
     *
     * 3. 批量生产/消费：
     *    - 生产者批量生产
     *    - 消费者批量消费
     *    - 提高吞吐量
     *
     * 4. 消息持久化：
     *    - 队列中的消息持久化到磁盘
     *    - 系统重启后可以恢复
     *    - 例如：RabbitMQ、Kafka
     *
     * =================================================================
     *
     * 【JDK中的生产者消费者实现】
     *
     * 1. BlockingQueue：
     *    - ArrayBlockingQueue
     *    - LinkedBlockingQueue
     *    - PriorityBlockingQueue
     *    - DelayQueue
     *
     * 2. Exchanger：
     *    - 两个线程交换数据
     *    - 线程A给线程B数据，同时获取线程B的数据
     *
     * 3. Phaser：
     *    - 多个线程分阶段执行
     *    - 适合复杂的工作流
     *
     * =================================================================
     *
     * 【思考题】
     *
     * 1. 为什么wait()必须在synchronized块内调用？
     *    - wait()会释放对象锁
     *    - 如果不在synchronized内，线程可能在wait()前就获取了锁
     *    - 导致其他线程无法进入同步块来调用notify()
     *
     * 2. notify()和notifyAll()的区别？
     *    - notify()随机唤醒一个等待线程
     *    - notifyAll()唤醒所有等待线程
     *    - 建议使用notifyAll()，避免遗漏
     *
     * 3. Semaphore的acquire()可以被中断吗？
     *    - 可以，acquire()会抛出InterruptedException
     *    - 使用acquireUninterruptibly()可以不响应中断
     *
     * 4. 如何选择消息队列的实现？
     *    - In-JVM：使用BlockingQueue
     *    - 跨JVM/跨机器：使用Redis、RabbitMQ、Kafka等
     */
}
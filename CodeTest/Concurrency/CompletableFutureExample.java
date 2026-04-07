import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * =============================================================================
 * CompletableFuture示例 - 异步编程
 * =============================================================================
 *
 * 本示例演示了Java 8+的CompletableFuture进行异步编程。
 *
 * 【什么是CompletableFuture？】
 *
 * CompletableFuture是Java 8引入的Future增强版，提供了更强大的异步编程能力。
 * 它实现了Future和CompletionStage接口，支持函数式编程风格。
 *
 * 【CompletableFuture vs Future】
 *
 * Future的局限性：
 * - 只能通过get()阻塞获取结果
 * - 不能手动完成
 * - 不能组合多个异步操作
 *
 * CompletableFuture的优势：
 * - 支持回调函数（thenApply、thenAccept等）
 * - 支持链式调用（ chaining）
 * - 支持组合多个异步操作
 * - 支持手动完成
 * - 支持异常处理
 *
 * 【核心概念】
 *
 * 1. 异步执行：
 *    - supplyAsync()：有返回值的异步任务
 *    - runAsync()：无返回值的异步任务
 *
 * 2. 链式调用：
 *    - thenApply()：转换结果
 *    - thenAccept()：消费结果
 *    - thenRun()：不关心结果，只执行操作
 *
 * 3. 组合操作：
 *    - thenCompose()：扁平化嵌套的CompletableFuture
 *    - thenCombine()：组合两个独立的CompletableFuture
 *    - allOf()：等待所有CompletableFuture完成
 *    - anyOf()：任意一个CompletableFuture完成即可
 *
 * 4. 异常处理：
 *    - exceptionally()：处理异常，返回默认值
 *    - handle()：无论成功失败都处理
 *    - whenComplete()：完成时的回调
 *
 * =============================================================================
 */
public class CompletableFutureExample {

    /**
     * =============================================================================
     * main方法 - 程序入口
     * =============================================================================
     *
     * 演示CompletableFuture的多种使用场景：
     * 1. 基本用法：创建异步任务
     * 2. 链式调用：thenApply、thenCompose
     * 3. 组合操作：allOf、anyOf、thenCombine
     * 4. 异常处理：exceptionally、handle
     * 5. 异步方法：thenApplyAsync
     * 6. 真实场景：电商订单处理流程
     *
     * @param args 命令行参数
     * @throws Exception 可能抛出的异常
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== CompletableFuture示例 ===");
        System.out.println("本示例演示CompletableFuture的强大功能");
        System.out.println();

        // 测试基本用法
        testBasicUsage();

        // 测试链式调用
        testChaining();

        // 测试组合多个Future
        testCombining();

        // 测试异常处理
        testExceptionHandling();

        // 测试异步方法
        testAsyncMethods();

        // 测试真实场景应用
        testRealWorldScenario();

        System.out.println("\n所有CompletableFuture测试完成！");
    }

    /**
     * =============================================================================
     * 测试基本用法
     * =============================================================================
     *
     * 【supplyAsync vs runAsync】
     *
     * supplyAsync(Supplier<T>):
     * - 用于有返回值的异步任务
     * - 返回CompletableFuture<T>
     * - 类似 Callable<T>
     *
     * runAsync(Runnable):
     * - 用于无返回值的异步任务
     * - 返回CompletableFuture<Void>
     * - 类似 Runnable
     *
     * 【两个方法的共同点】
     *
     * 1. 都使用ForkJoinPool作为默认线程池
     * 2. 都支持自定义线程池
     * 3. 都是异步执行
     *
     * 【使用默认线程池 vs 自定义线程池】
     *
     * 默认线程池（ForkJoinPool.commonPool()）：
     * - 优点：无需手动管理线程池
     * - 缺点：所有任务共享一个线程池，可能相互影响
     *
     * 自定义线程池：
     * - 优点：可以控制线程数量和行为
     * - 缺点：需要手动关闭线程池
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testBasicUsage() throws Exception {
        System.out.println("\n--- 测试基本用法 ---");
        System.out.println("特点：创建异步任务的两种方式");

        // =================================================================
        // 方式1：supplyAsync - 有返回值的异步任务
        // =================================================================
        // supplyAsync接受一个Supplier<T>
        // Supplier的get()方法会被异步执行
        // 返回CompletableFuture<String>
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务1开始执行，线程: " + Thread.currentThread().getName());
            sleep(1000);
            return "任务1结果";
        });

        // =================================================================
        // 方式2：runAsync - 无返回值的异步任务
        // =================================================================
        // runAsync接受一个Runnable
        // Runnable的run()方法会被异步执行
        // 返回CompletableFuture<Void>
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("异步任务2开始执行，线程: " + Thread.currentThread().getName());
            sleep(800);
            System.out.println("任务2执行完成");
        });

        // =================================================================
        // 获取结果
        // =================================================================
        // get()方法会阻塞直到结果可用
        String result1 = future1.get();
        System.out.println("任务1结果: " + result1);

        // 对于Void的Future，只需要调用get()等待完成
        future2.get();
    }

    /**
     * =============================================================================
     * 测试链式调用
     * =============================================================================
     *
     * 【什么是链式调用？】
     *
     * 链式调用允许你将多个异步操作串联起来，形成一个处理管道。
     * 每个步骤可以：
     * - 转换上一步的结果（thenApply）
     * - 消费上一步的结果（thenAccept）
     * - 执行其他操作（thenRun）
     *
     * 【执行流程】
     *
     * 原始数据
     *   |
     *   v
     * 第一阶段（获取数据）
     *   |
     *   v (thenApply)
     * 第二阶段（数据处理）--> 新数据
     *   |
     *   v (thenApply)
     * 第三阶段（数据格式化）--> 格式化结果
     *   |
     *   v (thenCompose)
     * 第四阶段（组合异步任务）--> 最终结果
     *
     * 【thenApply vs thenCompose】
     *
     * thenApply(Function<T, U>)：
     * - T是输入类型，U是输出类型
     * - 用于同步转换
     * - 如果返回的是CompletableFuture，会被嵌套
     *
     * thenCompose(Function<T, CompletableFuture<U>>)：
     * - 用于扁平化嵌套的CompletableFuture
     * - 当异步操作返回CompletableFuture时使用
     * - 避免 CompletableFuture<CompletableFuture<U>>
     *
     * 【线程执行】
     *
     * 默认情况下，链式调用的每个阶段都在同一个线程执行。
     * 但如果使用thenApplyAsync，则会在不同线程执行。
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testChaining() throws Exception {
        System.out.println("\n--- 测试链式调用 ---");
        System.out.println("特点：多个异步操作串联执行");

        // =================================================================
        // 创建链式调用
        // =================================================================
        // 每个thenApply都会等待前一个阶段完成
        // 然后处理结果并传递给下一个阶段
        CompletableFuture<String> future = CompletableFuture
            // 第一阶段：获取数据
            .supplyAsync(() -> {
                System.out.println("第一阶段：数据获取");
                sleep(500);
                return "原始数据";
            })
            // 第二阶段：数据处理（在大写）
            .thenApply(data -> {
                System.out.println("第二阶段：数据处理，线程: " + Thread.currentThread().getName());
                sleep(300);
                return data.toUpperCase();  // "原始数据" -> "原始数据"（已经是处理）
            })
            // 第三阶段：数据格式化
            .thenApply(data -> {
                System.out.println("第三阶段：数据格式化，线程: " + Thread.currentThread().getName());
                sleep(200);
                return "处理结果: " + data;
            })
            // 第四阶段：组合另一个异步任务
            // 注意：这里返回的是CompletableFuture，所以使用thenCompose
            .thenCompose(result -> {
                System.out.println("第四阶段：组合另一个异步任务");
                // 返回CompletableFuture，thenCompose会自动扁平化
                return CompletableFuture.supplyAsync(() -> {
                    sleep(400);
                    return result + " -> 最终结果";
                });
            });

        String finalResult = future.get();
        System.out.println("链式调用最终结果: " + finalResult);
    }

    /**
     * =============================================================================
     * 测试组合多个Future
     * =============================================================================
     *
     * 【allOf - 等待所有Future完成】
     *
     * CompletableFuture.allOf(CompletableFuture<?>... cfs)
     * - 接受多个CompletableFuture
     * - 返回一个新的CompletableFuture
     * - 当所有输入的CompletableFuture都完成时，新的CompletableFuture才完成
     * - 注意：返回的CompletableFuture<Void>，不包含结果
     *
     * 使用场景：
     * - 需要等待多个独立任务全部完成
     * - 例如：批量处理完成后发送通知
     *
     * 【anyOf - 任意一个Future完成】
     *
     * CompletableFuture.anyOf(CompletableFuture<?>... cfs)
     * - 接受多个CompletableFuture
     * - 返回一个新的CompletableFuture
     * - 当任意一个输入的CompletableFuture完成时，新的CompletableFuture就完成
     * - 注意：返回CompletableFuture<Object>，是第一个完成任务的结果
     *
     * 使用场景：
     * - 竞态场景：多个服务返回，取最快的结果
     * - 例如：多个CDN节点，取响应最快的
     *
     * 【thenCombine - 组合两个Future的结果】
     *
     * cf1.thenCombine(cf2, (result1, result2) -> { ... })
     * - 等待两个CompletableFuture都完成
     * - 然后用它们的结果进行组合
     * - 返回组合后的结果
     *
     * 使用场景：
     * - 两个任务有依赖关系但可以并行执行
     * - 例如：获取用户信息和订单信息后合并
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testCombining() throws Exception {
        System.out.println("\n--- 测试组合多个Future ---");
        System.out.println("特点：并行执行多个任务，然后合并结果");

        // =================================================================
        // 模拟三个独立的异步任务
        // =================================================================
        // 这三个任务互不依赖，可以并行执行
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1：用户信息查询");
            sleep(800);
            return "用户数据";
        });

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2：订单信息查询");
            sleep(600);
            return "订单数据";
        });

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务3：商品信息查询");
            sleep(400);
            return "商品数据";
        });

        // =================================================================
        // 方式1：allOf - 等待所有任务完成
        // =================================================================
        // allOf返回一个CompletableFuture<Void>
        // 当所有任务都完成时，它才完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2, task3);

        // allOf完成后，执行汇总操作
        allOf.thenRun(() -> {
            System.out.println("所有任务完成，开始汇总结果");
            try {
                // 现在可以安全地获取所有结果
                String result1 = task1.get();
                String result2 = task2.get();
                String result3 = task3.get();
                System.out.println("汇总结果: " + result1 + " + " + result2 + " + " + result3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).get();

        // =================================================================
        // 方式2：anyOf - 任意一个任务完成
        // =================================================================
        // anyOf返回一个CompletableFuture<Object>
        // 当任意一个任务完成时，它就完成
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(task1, task2, task3);

        // 获取第一个完成的任务的结果
        Object firstResult = anyOf.get();
        System.out.println("第一个完成的任务结果: " + firstResult);

        // =================================================================
        // 方式3：thenCombine - 组合两个任务的结果
        // =================================================================
        // thenCombine等待两个任务都完成
        // 然后用BiFunction合并结果
        CompletableFuture<String> combined = task1.thenCombine(task2, (result1, result2) -> {
            System.out.println("组合任务1和任务2的结果");
            return result1 + " + " + result2;
        });

        System.out.println("组合结果: " + combined.get());
    }

    /**
     * =============================================================================
     * 测试异常处理
     * =============================================================================
     *
     * 【异常处理的三种方式】
     *
     * 1. exceptionally() - 类似于catch
     *    - 当任务抛出异常时执行
     *    - 返回一个默认值
     *    - 如果任务正常完成，不执行
     *
     * 2. handle() - 类似于try-catch-finally
     *    - 无论任务成功还是失败都会执行
     *    - 可以根据结果或异常返回不同的值
     *    - 总是返回非null的值
     *
     * 3. whenComplete() - 类似于finally
     *    - 无论任务成功还是失败都会执行
     *    - 不改变结果，只用于监控/日志
     *    - 不能返回结果
     *
     * 【执行顺序】
     *
     * exceptionally()：
     * - 如果前一个阶段抛出异常，执行exceptionally的回调
     * - 如果前一个阶段正常完成，exceptionally不执行
     *
     * handle()：
     * - 无论前一个阶段成功还是失败，handle都会执行
     * - 可以根据throwable是否为null判断成功或失败
     *
     * whenComplete()：
     * - 与handle类似，但返回CompletableFuture<Void>
     * - 不能改变结果值
     *
     * 【异常传播】
     *
     * 在链式调用中，如果某个阶段抛出异常：
     * - 后续阶段不会执行
     * - 异常会传播到最终调用get()的地方
     * - 除非使用exceptionally()或handle()处理异常
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testExceptionHandling() throws Exception {
        System.out.println("\n--- 测试异常处理 ---");
        System.out.println("特点：三种异常处理方式的对比");

        // =================================================================
        // 模拟可能抛出异常的任务
        // =================================================================
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始执行可能失败的任务");
            sleep(300);

            // 根据时间戳的奇偶性决定是否抛出异常
            if (System.currentTimeMillis() % 2 == 0) {
                throw new RuntimeException("模拟任务执行失败");
            }

            return "任务成功完成";
        });

        // =================================================================
        // 异常处理方式1：exceptionally
        // =================================================================
        // exceptionally类似于try-catch
        // 只在任务失败时执行
        // 返回一个默认值
        CompletableFuture<String> handled1 = future.exceptionally(throwable -> {
            System.out.println("捕获到异常: " + throwable.getMessage());
            return "默认返回值";
        });

        // =================================================================
        // 异常处理方式2：handle
        // =================================================================
        // handle类似于try-catch-finally
        // 无论成功还是失败都会执行
        // 可以根据throwable判断是否有异常
        CompletableFuture<String> handled2 = future.handle((result, throwable) -> {
            if (throwable != null) {
                // 任务失败
                System.out.println("任务失败，异常: " + throwable.getMessage());
                return "失败时的默认值";
            } else {
                // 任务成功
                System.out.println("任务成功，结果: " + result);
                return result + " -> 处理后的结果";
            }
        });

        System.out.println("异常处理结果1: " + handled1.get());
        System.out.println("异常处理结果2: " + handled2.get());

        // =================================================================
        // 测试whenComplete
        // =================================================================
        // whenComplete类似于finally
        // 用于执行清理操作或记录日志
        // 不能改变结果
        CompletableFuture<String> futureWithComplete = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("测试whenComplete的任务");
                sleep(200);
                return "任务结果";
            })
            .whenComplete((result, throwable) -> {
                // result可能是null（如果任务失败）
                // throwable可能是null（如果任务成功）
                System.out.println("任务完成回调，结果: " + result + ", 异常: " + throwable);
            });

        futureWithComplete.get();
    }

    /**
     * =============================================================================
     * 测试异步方法
     * =============================================================================
     *
     * 【同步方法 vs 异步方法】
     *
     * thenApply()：
     * - 同步执行
     * - 在前一个阶段完成后立即执行
     * - 使用前一个阶段的线程
     *
     * thenApplyAsync()：
     * - 异步执行
     * - 在前一个阶段完成后，不阻塞当前线程
     * - 使用线程池中的线程
     *
     * 【为什么要用异步方法？】
     *
     * 1. 避免阻塞：
     *    - 同步方法会阻塞当前线程
     *    - 异步方法让线程可以处理其他任务
     *
     * 2. 提高吞吐量：
     *    - 线程可以并行处理多个阶段
     *    - 适合IO密集型操作
     *
     * 3. 利用多核：
     *    - 异步方法可以在不同CPU核心执行
     *    - 提高CPU利用率
     *
     * 【自定义线程池】
     *
     * 默认情况下，async方法使用ForkJoinPool.commonPool()
     * 但可以指定自定义线程池：
     *
     * thenApplyAsync(Function, Executor)
     *
     * 为什么要自定义线程池？
     * 1. 隔离性：避免与其他任务竞争线程
     * 2. 控制：可以控制线程数量和类型
     * 3. 特性：可以使用特殊类型的线程池（如ScheduledThreadPool）
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testAsyncMethods() throws Exception {
        System.out.println("\n--- 测试异步方法 ---");
        System.out.println("特点：使用自定义线程池和异步执行");

        // =================================================================
        // 创建自定义线程池
        // =================================================================
        ExecutorService customExecutor = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r, "CustomThread-" + System.nanoTime());
            t.setDaemon(true);  // 设置为守护线程
            return t;
        });

        // =================================================================
        // 使用自定义线程池执行异步任务
        // =================================================================
        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("第一阶段，线程: " + Thread.currentThread().getName());
                sleep(500);
                return "初始数据";
            }, customExecutor);

        // =================================================================
        // 使用异步方法串联
        // =================================================================
        // thenApplyAsync会使用线程池中的线程执行
        // 不同阶段可能使用不同的线程
        CompletableFuture<String> result = future
            .thenApplyAsync(data -> {
                System.out.println("第二阶段，线程: " + Thread.currentThread().getName());
                sleep(300);
                return data + " -> 处理1";
            }, customExecutor)
            .thenApplyAsync(data -> {
                System.out.println("第三阶段，线程: " + Thread.currentThread().getName());
                sleep(200);
                return data + " -> 处理2";
            }, customExecutor);

        System.out.println("异步方法结果: " + result.get());

        // 关闭线程池
        customExecutor.shutdown();
    }

    /**
     * =============================================================================
     * 测试真实场景应用
     * =============================================================================
     *
     * 【电商订单处理流程】
     *
     * 传统方式（同步）：
     * 1. 处理订单（500ms）
     * 2. 检查库存（400ms）- 必须等待订单处理完成
     * 3. 验证用户（300ms）- 必须等待订单处理完成
     * 4. 扣减库存（200ms）- 必须等待库存检查完成
     * 5. 生成支付（300ms）- 必须等待库存扣减完成
     *
     * 总时间：500 + 400 + 300 + 200 + 300 = 1700ms
     *
     * CompletableFuture方式（异步）：
     * 1. 处理订单（500ms）
     * 2. 并行：检查库存（400ms）和验证用户（300ms）
     * 3. 扣减库存（200ms）- 必须等待库存检查完成
     * 4. 生成支付（300ms）- 必须等待库存扣减完成
     *
     * 总时间：500 + max(400, 300) + 200 + 300 = 1300ms
     *
     * 节省时间：400ms
     *
     * 【thenCompose的使用】
     *
     * thenCompose用于处理返回CompletableFuture的函数。
     * 它的作用是"扁平化"嵌套的CompletableFuture。
     *
     * 没有thenCompose：
     * CompletableFuture<CompletableFuture<String>>
     *
     * 有thenCompose：
     * CompletableFuture<String>
     *
     * 【超时处理】
     *
     * 使用get(timeout, TimeUnit)设置超时。
     * 如果超时，抛出TimeoutException。
     * 然后可以调用cancel(true)取消任务。
     *
     * @throws Exception 可能抛出的异常
     */
    private static void testRealWorldScenario() throws Exception {
        System.out.println("\n--- 测试真实场景应用 ---");
        System.out.println("特点：电商订单处理流程的异步实现");

        // =================================================================
        // 模拟电商订单处理流程
        // =================================================================
        CompletableFuture<String> orderProcessing = CompletableFuture
            // 第一阶段：处理订单
            .supplyAsync(() -> {
                System.out.println("1. 开始处理订单");
                sleep(500);
                return "订单信息";
            })
            // 第二阶段：验证（并行检查库存和用户）
            .thenCompose(orderInfo -> {
                // thenCompose用于处理返回CompletableFuture的情况
                // 这里需要并行执行库存检查和用户验证

                // 库存检查（异步）
                CompletableFuture<Boolean> inventoryCheck = CompletableFuture.supplyAsync(() -> {
                    System.out.println("2.1 检查库存");
                    sleep(400);
                    return true;  // 模拟库存充足
                });

                // 用户验证（异步）
                CompletableFuture<Boolean> userValidation = CompletableFuture.supplyAsync(() -> {
                    System.out.println("2.2 验证用户信息");
                    sleep(300);
                    return true;  // 模拟验证通过
                });

                // 等待两个检查都完成
                // 使用thenCombine组合两个异步操作
                return inventoryCheck.thenCombine(userValidation, (inventoryOk, userOk) -> {
                    if (inventoryOk && userOk) {
                        System.out.println("3. 库存和用户验证通过");
                        return orderInfo + " -> 验证通过";
                    } else {
                        throw new RuntimeException("订单验证失败");
                    }
                });
            })
            // 第三阶段：扣减库存
            .thenApply(validatedOrder -> {
                System.out.println("4. 开始扣减库存");
                sleep(200);
                return validatedOrder + " -> 库存已扣减";
            })
            // 第四阶段：生成支付信息
            .thenApply(orderWithInventory -> {
                System.out.println("5. 生成支付信息");
                sleep(300);
                return orderWithInventory + " -> 等待支付";
            })
            // 异常处理：捕获任何阶段的异常
            .exceptionally(throwable -> {
                System.out.println("订单处理失败: " + throwable.getMessage());
                return "订单失败: " + throwable.getMessage();
            });

        String finalResult = orderProcessing.get();
        System.out.println("订单处理最终结果: " + finalResult);

        // =================================================================
        // 测试超时控制
        // =================================================================
        System.out.println("\n--- 测试超时控制 ---");

        CompletableFuture<String> timeoutExample = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始执行可能超时的任务");
            sleep(3000);  // 模拟耗时3秒的操作
            return "任务完成";
        });

        try {
            // 设置2秒超时
            String result = timeoutExample.get(2, TimeUnit.SECONDS);
            System.out.println("任务结果: " + result);
        } catch (TimeoutException e) {
            System.out.println("任务超时（超过2秒），取消任务");
            timeoutExample.cancel(true);
        }
    }

    /**
     * =============================================================================
     * 工具方法：睡眠指定毫秒数
     * =============================================================================
     *
     * @param millis 睡眠时间（毫秒）
     */
    private static void sleep(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    // =============================================================================
    // 模拟网络服务
    // =============================================================================
    /**
     * NetworkService - 模拟网络请求服务
     *
     * 【为什么要模拟？】
     *
     * 实际开发中，网络请求是典型的异步操作：
     * - HTTP请求
     * - RPC调用
     * - 消息队列发送
     *
     * 【CompletableFuture的优势】
     *
     * 使用CompletableFuture处理网络请求：
     * 1. 发起请求后立即返回，不阻塞
     * 2. 可以并行发起多个请求
     * 3. 容易组合和转换结果
     *
     * 【实际应用】
     *
     * CompletableFuture常用于：
     * - 微服务调用
     * - 第三方API集成
     * - 数据库查询（配合响应式框架）
     */
    static class NetworkService {
        /**
         * 获取用户数据
         *
         * @param userId 用户ID
         * @return 包含用户数据的CompletableFuture
         */
        public static CompletableFuture<String> fetchUserData(String userId) {
            return CompletableFuture.supplyAsync(() -> {
                sleep(200);
                return "用户数据: " + userId;
            });
        }

        /**
         * 获取订单数据
         *
         * @param orderId 订单ID
         * @return 包含订单数据的CompletableFuture
         */
        public static CompletableFuture<String> fetchOrderData(String orderId) {
            return CompletableFuture.supplyAsync(() -> {
                sleep(300);
                return "订单数据: " + orderId;
            });
        }

        /**
         * 获取商品数据
         *
         * @param productId 商品ID
         * @return 包含商品数据的CompletableFuture
         */
        public static CompletableFuture<String> fetchProductData(String productId) {
            return CompletableFuture.supplyAsync(() -> {
                sleep(250);
                return "商品数据: " + productId;
            });
        }
    }

    // =============================================================================
    // 模拟数据库服务
    // =============================================================================
    /**
     * DatabaseService - 模拟数据库操作服务
     *
     * 【数据库操作的特点】
     *
     * - IO密集型操作
     * - 需要处理连接和资源
     * - 可能需要事务管理
     *
     * 【与CompletableFuture的结合】
     *
     * 1. 异步执行数据库操作
     * 2. 组合多个数据库操作
     * 3. 处理数据库异常
     *
     * 【注意事项】
     *
     * 实际使用中，数据库操作通常是同步的。
     * CompletableFuture主要用于包装异步操作。
     * 如果数据库驱动支持异步（如Reactive SQL），效果更好。
     */
    static class DatabaseService {
        /**
         * 保存订单
         *
         * @param orderData 订单数据
         * @return 表示保存是否成功的CompletableFuture
         */
        public static CompletableFuture<Boolean> saveOrder(String orderData) {
            return CompletableFuture.supplyAsync(() -> {
                sleep(150);
                System.out.println("订单保存成功: " + orderData);
                return true;
            });
        }

        /**
         * 更新库存
         *
         * @param productId 商品ID
         * @param quantity  扣减数量
         * @return 表示更新是否成功的CompletableFuture
         */
        public static CompletableFuture<Boolean> updateInventory(String productId, int quantity) {
            return CompletableFuture.supplyAsync(() -> {
                sleep(100);
                System.out.println("库存更新: " + productId + ", 数量: " + quantity);
                return true;
            });
        }
    }

    // =============================================================================
    // 扩展知识点
    // =============================================================================
    /**
     * 【CompletableFuture方法分类】
     *
     * 1. 创建异步任务：
     *    - supplyAsync(Supplier<T>)
     *    - runAsync(Runnable)
     *
     * 2. 转换结果（同步）：
     *    - thenApply(Function<T, U>)
     *    - thenAccept(Consumer<T>)
     *    - thenRun(Runnable)
     *
     * 3. 转换结果（异步）：
     *    - thenApplyAsync(Function<T, U>)
     *    - thenAcceptAsync(Consumer<T>)
     *    - thenRunAsync(Runnable)
     *
     * 4. 组合操作：
     *    - thenCompose(Function<T, CompletionStage<U>>)
     *    - thenCombine(CompletionStage<U>, BiFunction<T, U, V>)
     *    - allOf(CompletionStage<?>...)
     *    - anyOf(CompletionStage<?>...)
     *
     * 5. 异常处理：
     *    - exceptionally(Function<Throwable, T>)
     *    - handle(BiFunction<T, Throwable, U>)
     *    - whenComplete(BiConsumer<T, Throwable>)
     *
     * 6. 手动完成：
     *    - complete(T value)
     *    - completeExceptionally(Throwable)
     *    - obtrudeValue(T value)
     *    - obtrudeException(Throwable)
     *
     * =================================================================
     *
     * 【thenApply vs thenCompose 的区别】
     *
     * thenApply - 用于同步转换：
     *
     * CompletableFuture<String> cf = ...;
     * cf.thenApply(s -> s.toUpperCase())  // String -> String
     *
     * thenCompose - 用于异步扁平化：
     *
     * CompletableFuture<CompletableFuture<String>> nested = ...;
     * nested.thenCompose(inner -> inner)  // CompletableFuture<String>
     *
     * 【示例】
     *
     * // thenApply示例
     * CompletableFuture.supplyAsync(() -> "hello")
     *     .thenApply(s -> s + " world")  // "hello world"
     *
     * // thenCompose示例
     * CompletableFuture.supplyAsync(() -> "hello")
     *     .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " world"))
     *     // 直接返回CompletableFuture<String>，不用嵌套
     *
     * =================================================================
     *
     * 【CompletableFuture的线程池】
     *
     * 默认线程池：ForkJoinPool.commonPool()
     * - 所有CompletableFuture默认共享
     * - 线程数 = CPU核心数 - 1
     * - 是守护线程池
     *
     * 自定义线程池：
     * - supplyAsync(Supplier, Executor)
     * - runAsync(Runnable, Executor)
     * - thenApplyAsync(Function, Executor)
     * - ...
     *
     * 【为什么使用守护线程池？】
     *
     * ForkJoinPool被设计为守护线程池：
     * - 如果所有用户线程都结束，守护线程会被JVM自动终止
     * - 这样JVM可以正常退出
     * - 避免了手动关闭线程池
     *
     * =================================================================
     *
     * 【最佳实践】
     *
     * 1. 总是使用超时：
     *    future.get(5, TimeUnit.SECONDS);
     *    避免无限等待
     *
     * 2. 处理异常：
     *    - 使用exceptionally或handle处理异常
     *    - 避免异常丢失
     *
     * 3. 使用thenCompose代替嵌套：
     *    // 不好：嵌套CompletableFuture
     *    future.thenApply(x -> anotherFuture);
     *
     *    // 好：扁平化
     *    future.thenCompose(x -> anotherFuture);
     *
     * 4. 使用自定义线程池：
     *    - IO密集型任务使用更大的线程池
     *    - 避免阻塞默认线程池
     *
     * 5. 记得关闭线程池：
     *    customExecutor.shutdown();
     *
     * =================================================================
     *
     * 【与反应式编程的关系】
     *
     * CompletableFuture是Java 8引入的，用于异步编程。
     * 它是命令式的（imperative）。
     *
     * Java 9引入了Flow API，实现了反应式流（Reactive Streams）规范。
     * 反应式流是声明式的（declarative）。
     *
     * 选择建议：
     * - 简单场景：CompletableFuture足够
     * - 复杂场景：考虑反应式流（Project Reactor、RxJava）
     *
     * =================================================================
     *
     * 【常见面试题】
     *
     * 1. CompletableFuture和Future的区别？
     *    - CompletableFuture支持回调，Future不支持
     *    - CompletableFuture可以组合，Future不能
     *    - CompletableFuture可以手动完成，Future不能
     *
     * 2. thenApply和thenCompose的区别？
     *    - thenApply用于同步转换
     *    - thenCompose用于异步扁平化
     *
     * 3. allOf和anyOf的区别？
     *    - allOf等待所有Future完成
     *    - anyOf等待任意一个Future完成
     *
     * 4. 如何处理异常？
     *    - exceptionally()：类似catch
     *    - handle()：类似try-catch-finally
     *    - whenComplete()：类似finally
     *
     * 5. 为什么使用自定义线程池？
     *    - 隔离任务
     *    - 控制线程数量
     *    - 避免阻塞默认线程池
     */
}
package com.tsz.myblog.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * API 控制器
 * 负责处理前端发送的 HTTP 请求并返回响应
 */
@RestController  // 标识为 REST 控制器，返回 JSON 数据
@RequestMapping("/api")  // 所有接口的公共路径前缀
public class ApiController {

    /**
     * 问候接口
     * GET /api/hello
     *
     * @param name 可选的查询参数，表示称呼的名字
     * @return 包含问候消息和服务器时间戳的 Map
     */
    @GetMapping("/hello")  // 处理 GET 请求，路径为 /api/hello
    public Map<String, String> sayHello(@RequestParam(required = false) String name) {
        Map<String, String> res = new HashMap<>();
        // 如果 name 为空或空白，则使用默认名字 "Guest"
        String userName = (name == null || name.isBlank()) ? "Guest" : name;
        // 构建响应消息
        res.put("message", "Hello, " + userName + "!");
        // 添加服务器时间戳
        res.put("serverTime", String.valueOf(System.currentTimeMillis()));
        return res;
    }

    /**
     * 查找数组中的重复元素
     * POST /api/findDuplicates
     *
     * @param nums 前端传入的整型数组
     * @return 包含原数组、重复元素、重复数量和服务器时间戳的 Map
     */
    @PostMapping("/findDuplicates")  // 处理 POST 请求，路径为 /api/findDuplicates
    public Map<String, Object> findDuplicates(@RequestBody int[] nums) {
        Map<String, Object> res = new HashMap<>();
        // 调用私有方法查找重复元素
        List<Integer> duplicates = find(nums);
        // 将结果存入响应 Map
        res.put("originalArray", nums);           // 原数组
        res.put("duplicates", duplicates);       // 重复元素列表
        res.put("count", duplicates.size());      // 重复数量
        res.put("serverTime", System.currentTimeMillis());  // 服务器时间戳
        return res;
    }

    /**
     * 查找数组中的重复元素（私有方法）
     * 使用双指针+排序的方式实现
     *
     * @param nums 输入的整型数组
     * @return 重复元素的列表
     */
    private static List<Integer> find(int[] nums) {
        List<Integer> result = new ArrayList<>();
        // 先对数组进行排序，使相同的元素相邻
        Arrays.sort(nums);
        // 使用双指针遍历数组
        int left = 0;
        while (left < nums.length - 1) {
            // 如果当前元素与下一个元素相同，则为重复元素
            if (nums[left] == nums[left + 1]) {
                // 将重复元素添加到结果列表
                result.add(nums[left]);
                // 跳过所有相同的重复元素
                while (left < nums.length - 1 && nums[left] == nums[left + 1]) {
                    left++;
                }
            }
            // 移动指针到下一个元素
            left++;
        }
        return result;
    }
}
